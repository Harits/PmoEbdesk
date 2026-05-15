# PMO BOD Dashboard - Testing Guide

This guide explains how to test the newly implemented PMO Board of Directors Web Dashboard locally.

## Prerequisites
- Docker & Docker Compose
- Java 17+ (If building locally without Docker)
- An active OpenProject API key and host URL.

## 1. Local Testing with Mock Data
To quickly test the UI generation and layout without needing an actual OpenProject connection, you can run the app using the Mock Data variant.

1. Create a local `.env` file from the provided variables (if you don't have one):
   \`\`\`bash
   # .env
   USE_MOCK_DATA=true
   OPENPROJECT_URL=http://localhost
   OPENPROJECT_API_KEY=mock_key
   \`\`\`
   *(Note: URL and API key are ignored when `USE_MOCK_DATA=true`, but must be present to satisfy the configuration loading).*

2. Start the Docker container:
   \`\`\`bash
   docker-compose up --build
   \`\`\`

3. Open your browser and navigate to `http://localhost:8080`.
4. You should see the fully rendered "Inverse Pyramid" dashboard populated with mock data (e.g., 68% progress, Amber status, predefined milestones).

## 2. Production Testing with Real OpenProject Data
To test the real integration with your OpenProject instance via Ktor:

1. Update your `.env` file with your actual OpenProject credentials and set the mock flag to false:
   \`\`\`bash
   # .env
   USE_MOCK_DATA=false
   OPENPROJECT_URL=https://your-openproject-instance.com
   OPENPROJECT_API_KEY=your_actual_api_key
   \`\`\`

2. Restart the Docker container to pick up the new environment variables:
   \`\`\`bash
   docker-compose down
   docker-compose up --build
   \`\`\`

3. Refresh your browser at `http://localhost:8080`.
4. The dashboard will now execute a real REST call to your OpenProject `/api/v3/work_packages` endpoint, aggregate the live data, and display the real-time metrics.

## Troubleshooting
- **CORS Issues:** If the data fails to load in Production mode, check your browser's Developer Console. You may need to explicitly configure your OpenProject instance's CORS settings to allow requests from `http://localhost:8080`.
- **Blank Screen:** Ensure that `config.json` is properly being generated in the `/usr/share/nginx/html` directory inside the Docker container. You can check the container logs (`docker-compose logs web`) for any entrypoint errors.
