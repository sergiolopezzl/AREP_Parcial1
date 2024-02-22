package org.example;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectiveChatGTP {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(45000)) {
            System.out.println("ReflectiveChatGTP is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine = in.readLine();
            if (inputLine != null) {
                URI uri = new URI(inputLine.split(" ")[1]);
                String path = uri.getPath();
                String query = uri.getQuery();
                if ("/compreflex".equals(path)) {
                    System.out.println("Query: " + query);
                    String[] queryParams = query.split("=");
                    if (queryParams.length >= 2) {
                        String commandAndParams = queryParams[1];
                        Matcher matcher = Pattern.compile("([^,()]+)").matcher(commandAndParams);
                        List<String> params = new ArrayList<>();
                        while (matcher.find()) {
                            params.add(matcher.group());
                        }

                        String command = params.remove(0);

                        Object result = null;

                        if (command.startsWith("binaryInvoke") && params.size() == 6) {
                            result = binaryInvoke(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5));
                        } else if (command.startsWith("invoke") && params.size() == 2) {
                            result = invoke(params.get(0), params.get(1));
                        } else if (command.startsWith("unaryInvoke") && params.size() == 4) {
                            result = unaryInvoke(params.get(0), params.get(1), params.get(2), params.get(3));
                        }

                        out.println("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + (result != null ? result.toString() : ""));
                    } else {
                        out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nInvalid query parameters");
                    }
                } else {
                    out.println("HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\n404 Not Found");
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static Object invoke(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            return method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object binaryInvoke(String className, String methodName, String paramType1, String paramValue1, String paramType2, String paramValue2) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, getParamType(paramType1), getParamType(paramType2));
            Object arg1 = parseParam(paramType1, paramValue1);
            Object arg2 = parseParam(paramType2, paramValue2);
            return method.invoke(null, arg1, arg2);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object unaryInvoke(String className, String methodName, String paramType, String paramValue) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, getParamType(paramType));
            Object arg = parseParam(paramType, paramValue);
            return method.invoke(null, arg);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getParamType(String paramType) {
        switch (paramType) {
            case "int":
                return int.class;
            case "double":
                return double.class;
            case "String":
                return String.class;
            default:
                return null;
        }
    }

    private static Object parseParam(String paramType, String paramValue) {
        switch (paramType) {
            case "int":
                return Integer.parseInt(paramValue);
            case "double":
                return Double.parseDouble(paramValue);
            case "String":
                return paramValue;
            default:
                return null;
        }
    }
}
