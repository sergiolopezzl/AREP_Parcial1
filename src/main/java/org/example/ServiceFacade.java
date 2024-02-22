package org.example;

import java.io.*;
import java.net.*;

public class ServiceFacade {

    private static final String CLIENT_HTML = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n" +
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Interactuar con ReflectiveChatGTP</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>Interactuar con ReflectiveChatGTP</h1>\n" +
            "    <div>\n" +
            "        <label for=\"commandInput\">Ingrese su comando:</label><br>\n" +
            "        <input type=\"text\" id=\"commandInput\"><br><br>\n" +
            "        <button onclick=\"sendCommand()\">Enviar Comando</button>\n" +
            "    </div>\n" +
            "    <div id=\"response\"></div>\n" +
            "\n" +
            "    <script>\n" +
            "        function sendCommand() {\n" +
            "            const commandInput = document.getElementById('commandInput').value;\n" +
            "            const url = `http://localhost:45000/compreflex?comando=${encodeURIComponent(commandInput)}`;\n" +
            "\n" +
            "            fetch(url)\n" +
            "                .then(response => {\n" +
            "                    if (!response.ok) {\n" +
            "                        throw new Error('Hubo un problema con la solicitud.');\n" +
            "                    }\n" +
            "                    return response.text();\n" +
            "                })\n" +
            "                .then(data => {\n" +
            "                    document.getElementById('response').innerText = data;\n" +
            "                })\n" +
            "                .catch(error => {\n" +
            "                    console.error('Error:', error);\n" +
            "                    document.getElementById('response').innerText = 'Error al enviar el comando.';\n" +
            "                });\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>ChatGTP</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>Enviar Comando al Servidor</h1>\n" +
            "    <form id=\"commandForm\">\n" +
            "        <label for=\"commandInput\">Ingrese su comando:</label><br>\n" +
            "        <input type=\"text\" id=\"commandInput\" name=\"commandInput\"><br><br>\n" +
            "        <input type=\"button\" value=\"Enviar Comando\" onclick=\"sendCommand()\">\n" +
            "    </form>\n" +
            "    <div id=\"response\"></div>\n" +
            "\n" +
            "    <script>\n" +
            "        function sendCommand() {\n" +
            "            const commandInput = document.getElementById('commandInput').value;\n" +
            "            const url = `http://localhost:45000/compreflex?comando=${encodeURIComponent(commandInput)}`;\n" +
            "\n" +
            "            fetch(url)\n" +
            "                .then(response => response.json())\n" +
            "                .then(data => {\n" +
            "                    displayResponse(data);\n" +
            "                })\n" +
            "                .catch(error => {\n" +
            "                    console.error('Error al enviar el comando:', error);\n" +
            "                    displayResponse({ error: 'Error al enviar el comando' });\n" +
            "                });\n" +
            "        }\n" +
            "\n" +
            "        function displayResponse(responseData) {\n" +
            "            const responseDiv = document.getElementById('response');\n" +
            "            responseDiv.innerHTML = ''; // Limpiar contenido anterior\n" +
            "\n" +
            "            if (responseData.error) {\n" +
            "                responseDiv.innerText = 'Error: ' + responseData.error;\n" +
            "            } else {\n" +
            "                // Mostrar los datos de la respuesta\n" +
            "                // visualización de los datos devueltos por el servidor\n" +
            "                responseDiv.innerText = JSON.stringify(responseData);\n" +
            "            }\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n";
    private static final String GET_URL = "http://localhost:45000/compreflex?comando=";

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            System.out.println("ServiceFacade is running...");
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
                String response;
                if (inputLine.startsWith("GET /cliente")) {
                    response = CLIENT_HTML;
                } else if (inputLine.startsWith("GET /consulta")) {
                    response = handleConsulta(inputLine);
                } else {
                    response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\n404 Not Found";
                }
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String invokeRestService(String query) {
        String outputLine = "";
        try {
            URL obj = new URL(GET_URL + query);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inC = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLineC;
                StringBuffer response = new StringBuffer();
                while ((inputLineC = inC.readLine()) != null) {
                    response.append(inputLineC);
                }
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n" + response;
                inC.close();
            } else {
                System.out.println("GET request not worked");
            }
            System.out.println("GET DONE");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputLine;
    }

    private static String handleConsulta(String inputLine) {
        try {
            String query = inputLine.split("\\?")[1].split(" ")[0].replace("comando=", "");
            String response = invokeRestService(query);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/plain\r\n\r\n500 Internal Server Error";
        }
    }
}
