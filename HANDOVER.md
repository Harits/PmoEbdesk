# PMO Dashboard Handover Guide (TS Team)

This project contains a unified dashboard (frontend + backend) in a single container.

## 🚀 Quick Start

### 1. Build the Artifacts
This project uses a "Local Build Strategy" to avoid container memory issues. Build the artifacts on your machine first:
```bash
./gradlew :server:installDist :composeApp:wasmJsBrowserDistribution
```

### 2. Configure Environment
Create a `.env` file in the root directory:
```env
OPENPROJECT_HOST=https://your-openproject-instance.com
OPENPROJECT_API_KEY=your_actual_api_key
# Optional: IDs of projects to show on the dashboard
ALLOWED_PROJECT_IDS=657,658,659 
```

### 3. Start with Podman/Docker
```bash
podman compose up --build -d
```

### 4. Access the Dashboard
Open your browser to: `http://localhost:8080`

## 📂 Project Structure
- `server/`: Ktor backend (serves UI and proxies API).
- `composeApp/`: Compose WasmJS frontend.
- `shared/`: Logic shared between server and UI.
- `Dockerfile`: Production runtime configuration.
