# PMO BOD Dashboard - Testing Guide

This guide explains how to test the newly implemented PMO Board of Directors Web Dashboard locally.

## Prerequisites
- Docker & Docker Compose
- Java 17+ (If building locally without Docker)
- An active OpenProject API key and host URL.

## 1. Local Testing with Mock Data
To quickly test the UI generation and layout without needing an actual OpenProject connection, follow these steps:

1. **Build the application on your Mac**:
   ```bash
   ./gradlew :server:installDist :composeApp:wasmJsBrowserDistribution
   ```

2. **Start the Docker/Podman container**:
   ```bash
   docker-compose up --build
   ```

3. **Verify**:
   - Dashboard: `http://localhost:8080`
   - Health Check: `http://localhost:8080/health`
   - Config Check: `http://localhost:8080/config.json`

## 2. Production Testing with Real OpenProject Data
To test the real integration with your OpenProject instance:

1. Update your `.env` file with your actual OpenProject credentials and set the mock flag to false:
   ```bash
   # .env
   USE_MOCK_DATA=false
   OPENPROJECT_URL=https://your-openproject-instance.com
   OPENPROJECT_API_KEY=your_actual_api_key
   ```

2. Restart the container:
   ```bash
   docker-compose up --build
   ```

## Troubleshooting
- **CORS Issues:** If the data fails to load, check your browser's Developer Console. You may need to configure OpenProject's CORS settings.
- **Health Check:** You can verify if the server is running by visiting `http://localhost:8080/health`.
- **Config Check:** You can see the dynamic configuration by visiting `http://localhost:8080/config.json`.

## 3. Local Testing without Docker
If you prefer not to use Docker, you can run the web application directly through Gradle's development server.

1. Ensure your Java 17+ environment is set up.
2. In the root of the project, run:
   \`\`\`bash
   ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
   \`\`\`
   This will start a local webpack dev server, typically on port `8080`.

3. Because there is no Nginx container injecting the `.env` variables into a `config.json` file, the development server will look for a static `config.json` file at the root of the served resources.

   To provide one, manually create a `config.json` file in `composeApp/src/wasmJsMain/resources/` with the required keys before running the command:
   \`\`\`json
   {
     "OPENPROJECT_URL": "http://localhost",
     "OPENPROJECT_API_KEY": "mock_key",
     "USE_MOCK_DATA": true
   }
   \`\`\`
4. Once the server starts, navigate to `http://localhost:8080` to view the dashboard.
