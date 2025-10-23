
# run.md

## Requisitos
- Java 17+
- gradle 8+
- spring boot 3.5+

Ejecutar desde IntelliJ IDEA
- Abrir el proyecto en IntelliJ.
- Importar como proyecto Gradle si se solicita.
- Ejecutar la clase con `public static void main` en el módulo `application` (o usar la tarea Gradle `bootRun` desde la vista Gradle).
- Para debug: crear una Run/Debug Configuration tipo `Application` o usar la tarea `bootRun` en modo debug.
- Para poder consumir los endpoints que se encuentran en ProductsController, es necesario primero hacer una peticion al endpoint /get-login-token para obtener un token JWT valido y la seguridad permita el consumo. 
