# Stage 1: Build the frontend
FROM node:20.11.1 AS frontend-build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Build the backend
FROM maven:3.8-openjdk-17 AS backend-build
WORKDIR /app
COPY --from=frontend-build /app/target/classes/static/ /app/src/main/resources/static/
COPY src ./src
COPY . .
RUN mvn -DskipTests --batch-mode -Pprod clean verify

# Stage 3: Create the final image
FROM openjdk:17-jdk-slim
WORKDIR /app
RUN mkdir /app/uploads
RUN mkdir /config
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
