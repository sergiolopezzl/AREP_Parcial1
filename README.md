# AREP_parcial1
### Instrucciones de Ejecución
* Clone el repositorio desde GitHub:

```
git clone https://github.com/sergiolopezzl/AREP_Parcial1.git
```

* Navegue al directorio del proyecto: 

```
cd AREP_Parcial1
```

* Compile el proyecto y descargue las dependencias con Maven: 

```
mvn clean package
```

* Ejecute el servidor utilizando el siguiente comando: 

```
java -cp target/classes org.example.ReflectiveChatGTP
```
* Ejecute el servidor fachada utilizando el siguiente comando:
```
java -cp target/classes org.example.ServiceFacade
```
* Debe tener los dos ejecutandose al tiempo

Una vez que el servidor esté en funcionamiento, acceda a 
http://localhost:35000/cliente desde su navegador para comenzar a escribir comandos.

### Pruebas
* Se realizó la petición a http://localhost:35000/cliente
![prueba1.png](src/main/resources/img/prueba1.PNG)
* Si esta recibiendo el servidor pero esta fallando un poco de la logica de los metodos de la calculadora
  ![prueba2.png](src/main/resources/img/prueba2.PNG)
* Se corrigeron cosas en la clase de ReflectiveChatGTP en el metodo de handleRequest y ya funciona correctamente
  ![prueba3.png](src/main/resources/img/prueba3.PNG)