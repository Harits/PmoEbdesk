# Android Studio Agent Instructions

Welcome! This repository acts as a bridge to sync data from local Excel/CSV files into OpenProject, leveraging the Jules API where necessary. 

As the Android Studio AI Agent, your goal is to assist the user in writing the Kotlin Multiplatform (KMP) code to perform this synchronization, adhering to **Clean Architecture**, **Screaming Architecture**, and **TDD** principles.

## 🏗️ Architectural Principles

### 1. Screaming Architecture
The folder structure must reflect the **Business Domain**, not the frameworks used. Avoid generic names like `models` or `fragments` at the top level. Instead, organize by features/entities (e.g., `sync`, `workpackages`, `parsing`).

### 2. Clean Architecture & Dependency Rule
- **Domain Layer (Entities & Use Cases)**: Business logic. No dependencies on outer layers (Ktor, Excel libraries, etc.).
- **Data Layer (Repositories & Data Sources)**: Implementations of Domain interfaces. Handles API calls and file reading.
- **Presentation Layer**: UI logic and ViewModels.
- **Dependency Rule**: Dependencies must only point inwards. Outer layers depend on inner layers.

### 3. Coding Standards
- **Single Responsibility (SRP)**: Every class and function must have only one reason to change.
- **Small Functions**: No function should exceed 20 lines unless absolutely necessary for complex algorithms.

## 🧪 TDD First Protocol
Before generating implementation code, you **MUST** write a failing test in the appropriate test directory that defines the expected behavior. Use `kotlin-test` and `ktor-client-mock` to isolate units of code and mock API responses.

## 🛡️ Constraint Protocol
When a new feature is requested, follow these steps strictly:

1. **Analyze the Boundary**: Identify which layer this feature belongs to (Domain, Data, or Presentation).
2. **Define the Interface**: Define the contract (Interface/Protocol) first in the Domain layer.
3. **Draft the Test**: Write the unit test for the Use Case or implementation based on the Interface.
4. **Implement**: Write the minimum code required to make the test pass.
5. **Critique**: If the implementation violates the Dependency Rule or other principles, point it out and suggest a refactor before finalizing.

## 📁 Directory Structure & Context
- **`shared/src/commonMain/kotlin/.../`**: 
    - `sync/`: Logic for CSV parsing and OpenProject API interaction.
    - `dashboard/`: Logic for fetching and aggregating metrics for the UI.
    - `projects/`: UI and domain logic for project listing and search.
- **`data/`**: Source `.xlsx` or `.csv` files. Path configured via `CSV_FILE_PATH` in `.env`.
- **`.design/`**: Design documents and UI/UX specifications.
- **`.openproject/`**: `work_packages_api.md` (API details & Ktor examples) and `.env.example`.
- **`.jules/`**: `api_reference.md` (Jules API docs) and `.env.example`.
- **Notebooks**: `OpenProjectSync.ipynb` and `Dashboard.ipynb` are used for prototyping sync logic and data analysis.

## 📊 Dashboard Data Consistency
When implementing sync logic from CSV/Excel, you **MUST** ensure the OpenProject work packages are populated with fields that drive the BoD Dashboard. Refer to `OPENPROJECT_DATA_MAPPING.md` for full details.

Key fields to map from source files:
- **Net Progress**: Map to `percentageDone` (0-100).
- **Keywords**: Look for "Milestone" or "Risk" in columns like `Task_1`, `Task_2`, or `Full_Project_Name`.
- **Effort Distribution**: 
    - Map "Hours" to `estimatedTime` using ISO 8601 format (e.g., `PT8H`).
    - Use keywords "Strategic" or "Growth" in the `subject` to categorize as Strategic vs BAU.
- **Critical Path**: Ensure `dueDate` is populated for items intended as Milestones. Use "Milestone" in the subject or ensure the Work Package Type is set correctly.
- **Risks**: Use "Risk" in the subject for automatic detection in the Risk Heatmap.
- **The Red List**: Populating `dueDate` and `percentageDone` is critical for identifying overdue items.

## 🚀 Deployment & Containerization
The project is optimized for a single-container deployment using Ktor to serve both the backend API and the Compose WasmJS frontend.

### 1. Local Build Strategy
To avoid memory-intensive compilation inside containers, always build artifacts on the host machine before packaging:
```bash
./gradlew :server:installDist :composeApp:wasmJsBrowserDistribution
```

### 2. Podman/Docker Execution
Use the provided `docker-compose.yml` to start the unified container:
```bash
podman compose up --build
```
- **Unified Entrypoint**: The container runs on port `8080` (mapped from `SERVER_PORT`).
- **Static Files**: The WasmJS UI is served from the `/app/www` directory.
- **Built-in API Proxy**: The server transparently proxies `/api/*` requests to the `OPENPROJECT_URL` to bypass CORS issues.
- **Dynamic Config**: The `/config.json` endpoint is generated at runtime from environment variables.

### 3. Environment Variables
Ensure the following are provided via `.env` or container environment:
- `OPENPROJECT_URL` (or `OPENPROJECT_HOST`): The base URL of the OpenProject instance.
- `OPENPROJECT_API_KEY`: API key for authentication.
- `USE_MOCK_DATA`: Set to `true` for local UI testing without an active API.
- `ALLOWED_PROJECT_IDS`: Comma-separated string of IDs to filter the dashboard.

## 🧱 OpenProject Business Rules & Validation
When syncing or creating work packages, you MUST adhere to these API constraints:
1. **Milestones**: For any Work Package of type "Milestone", `startDate` and `dueDate` **must be identical**. 
2. **Progress vs. Effort**: OpenProject does not allow `% Complete` (`percentageDone`) to be set if `Estimated Time` (`estimatedTime`) is 0 or null. 
   - *Rule*: If `percentageDone > 0`, ensure `estimatedTime` is at least `PT1H`.
3. **Date Consistency**: `dueDate` cannot be earlier than `startDate`.
4. **Date Formatting**: CSV start/finish dates must be formatted as `d-MMM-yyyy` (using English month abbreviations: `Jan`, `Feb`, `Mar`, `Apr`, `May`, `Jun`, `Jul`, `Aug`, `Sep`, `Oct`, `Nov`, `Dec`) to prevent parsing failures.
5. **Monthly Task Expansion**: Standardize task names containing `"bulanan"` or `"bulanan selama"` to use the keyword `"Monthly"` so the sync engine's task expansion logic executes successfully.
6. **Header Layout**: Use the operational CSV header structure exactly:
   `,Nama Projek,Nama Projek,CUSTOMER,,IMA,ISA,IAS,BDAAS,Report,AI,HARDWARE,SALES,Semester,Waktu kerja sama,Start,Finish,Ket.,ID PMO,Tgl Dibuat,Tgl Last Update,,,,,,,,Task 1,Task 2,capture Doc Permintaan,Doc Pengujian`

## 🛠️ Typical Sync Pipeline Tasks
1. **Environment Setup**: Read variables from `.env` (credentials, `CSV_FILE_PATH`).
2. **Read Data (Data Layer)**: Use `kotlin-csv` or Apache POI to parse files.
3. **Map Data (Domain Layer)**: 
   - Use Cases to map source columns to Domain Entities.
   - **Crucial**: Align mapping with `OPENPROJECT_DATA_MAPPING.md` requirements.
4. **Sync via OpenProject (Data Layer)**: 
   - Use Ktor Client to interact with OpenProject REST API.
   - Refer strictly to `.openproject/work_packages_api.md`.
   - Handle optimistic locking (`lockVersion`) and HTTP errors gracefully.
5. **Jules Integration (Optional)**: Trigger Jules tasks as part of the domain logic if required.

## ⚠️ Important Notes
- Always handle HTTP errors gracefully with informative logs.
- Network requests must be in Coroutines (`suspend` functions).
- Do not commit real `.env` files.
