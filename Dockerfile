FROM docker.io/library/node:18-alpine AS build
WORKDIR /app
FROM eclipse-temurin:17-jdk-focal AS jvm-build
WORKDIR /app
COPY . .
RUN ./gradlew :composeApp:wasmJsBrowserDistribution

FROM nginx:alpine
COPY --from=jvm-build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY env.sh /docker-entrypoint.d/40-env.sh
RUN chmod +x /docker-entrypoint.d/40-env.sh
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
