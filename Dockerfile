# Simplified Runtime Image
FROM docker.io/library/eclipse-temurin:17-jre-focal
WORKDIR /app

# IMPORTANT: Run the following on your host machine before building this image:
# ./gradlew :server:installDist :composeApp:wasmJsBrowserDistribution

# Copy pre-built Server (using relative paths from project root)
COPY ./server/build/install/server /app/server
# Copy pre-built UI files (using relative paths from project root)
COPY ./composeApp/build/dist/wasmJs/productionExecutable /app/www

EXPOSE 8080

# Run the server
ENTRYPOINT ["/app/server/bin/server"]
