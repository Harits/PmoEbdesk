# PMO EB Desk

PMO EB Desk is a Kotlin Multiplatform (KMP) application designed to bridge the gap between local Excel/CSV files and OpenProject. It synchronizes project data into OpenProject work packages, which in turn drive a Board of Directors (BoD) Dashboard.

## 🏗️ Architectural Principles

This project adheres to strict architectural standards to ensure maintainability, scalability, and testability:

*   **Screaming Architecture**: The folder structure reflects the business domain (e.g., `sync`, `dashboard`, `projects`) rather than framework details.
*   **Clean Architecture**: Logic is organized into layers (Domain, Data, Presentation) with a strict inward dependency rule.
*   **TDD (Test-Driven Development)**: Features are developed by first writing failing tests.

## 📁 Project Structure

*   **[`/shared`](./shared/src/commonMain/kotlin)**: The core of the application, shared between all targets. It contains the business logic, domain models, and data repository implementations organized by feature:
    *   `sync/`: Logic for CSV parsing and OpenProject API interaction.
    *   `dashboard/`: Metrics aggregation for the UI.
    *   `projects/`: Project listing and search logic.
*   **[`/server`](./server/src/main/kotlin)**: A Ktor server application that serves the backend API and the Compose WasmJS frontend. It includes a built-in API proxy to handle CORS for OpenProject requests.
*   **[`/composeApp`](./composeApp/src)**: The UI layer built with Compose Multiplatform.
    *   `commonMain/`: Shared UI components.
    *   `jvmMain/`: Desktop-specific implementation.

## 🚀 Getting Started

### 1. Configuration

Create a `.env` file in the root directory based on `.env.example`. Required variables include:

*   `OPENPROJECT_URL`: Base URL of your OpenProject instance.
*   `OPENPROJECT_API_KEY`: Your API key for authentication.
*   `CSV_FILE_PATH`: Path to the source `.xlsx` or `.csv` files.
*   `ALLOWED_PROJECT_IDS`: Comma-separated IDs to filter the dashboard.

### 2. Build & Run

#### Desktop (JVM) Application
```shell
./gradlew :composeApp:run
```

#### Ktor Server
```shell
./gradlew :server:run
```

#### Containerized Deployment (Podman/Docker)
The project is optimized for single-container deployment.
1. Build artifacts:
   ```shell
   ./gradlew :server:installDist :composeApp:wasmJsBrowserDistribution
   ```
2. Start the container:
   ```shell
   podman compose up --build
   ```
The server runs on port `8080` (or `SERVER_PORT`) and serves the UI from `/app/www`.

## 📊 Business Rules & Data Mapping

Data synchronization follows specific rules to ensure consistency with the BoD Dashboard. Refer to [**`OPENPROJECT_DATA_MAPPING.md`**](./OPENPROJECT_DATA_MAPPING.md) for full details.

### Key Constraints:
*   **Milestones**: `startDate` and `dueDate` must be identical.
*   **Progress**: `% Complete` cannot be set if `Estimated Time` is null or zero.
*   **Consistency**: `dueDate` cannot be earlier than `startDate`.

## 🧪 Testing

Run all unit tests using:
```shell
./gradlew test
```
See [**`TESTING.md`**](./TESTING.md) for more details on our TDD protocol.
