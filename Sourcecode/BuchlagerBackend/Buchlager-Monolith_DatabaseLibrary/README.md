# Buchlager-Backend mit monolithischer Architektur

## Verwendung

### Voraussetzungen
1. Java 11 JDK muss installiert sein (getestet mit Amazon Corretto JDK 11).
2. Beim ersten Start muss eine Internetverbindung bestehen, damit die eventuell fehlenden Bibliotheken mit Maven heruntergeladen werden können.

### Start des Backends
* mvn spring-boot:run -DskipTests=true
* Mit dem Browser die URL http://localhost:8888/ aufrufen. Es erfolgt ein Redirect zur Swagger-UI.

### Todo (Start ohne Compilieren mit Jar)
* Jar-Package aus den Releases herunterladen.
* java -jar BuchlagerBackendMonolith-X.X.X-RELEASE.jar

## Technologien
* Spring-Boot 2.2.6.RELEASE
* Spring-Data JPA mit Hibernate (Version durch Spring gemanaged)
* Springfox Swagger 2.9.2
* Springfox Swagger-UI 2.9.2
* Modelmapper 2.3.7
* H2 Database (Version durch Spring gemanaged)

## Verfügbare Profile
* Aktuell: In-Memory-Database mit automatischer Datengenerierung bei jedem Start...
* tbd

### Verwenden anderer Profile
* tbd

## Dokumentation
* TODO