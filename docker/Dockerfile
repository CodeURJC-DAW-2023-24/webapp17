# Stage frontend (angular)
FROM node:22 AS frontend
WORKDIR /project
COPY /frontend .
RUN npm install
RUN npm run build -- --configuration production --base-href="/new/"

# Stage backend (java)
FROM maven:3.9.6-eclipse-temurin-21 AS backend
WORKDIR /project
COPY /webapp17/pom.xml /project/
COPY /webapp17/src /project/src
COPY --from=frontend /project/dist/frontend/browser/ /project/src/main/resources/static/new
RUN mvn clean install -e -DskipTests=true
RUN mvn package -o -DskipTests=true

# Stage jdk
FROM eclipse-temurin:21-jdk
WORKDIR /usr/src/app
COPY --from=backend /project/target/*.jar webapp17.jar
EXPOSE 8443
ENTRYPOINT ["sh", "-c", "sleep 10 && java -jar webapp17.jar"]
