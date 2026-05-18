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
Before generating implementation code, you **MUST** write a failing test in the appropriate test directory that defines the expected behavior. Use mocking libraries (like `mockk`) to isolate units of code.

## 🛡️ Constraint Protocol
When a new feature is requested, follow these steps strictly:

1. **Analyze the Boundary**: Identify which layer this feature belongs to (Domain, Data, or Presentation).
2. **Define the Interface**: Define the contract (Interface/Protocol) first in the Domain layer.
3. **Draft the Test**: Write the unit test for the Use Case or implementation based on the Interface.
4. **Implement**: Write the minimum code required to make the test pass.
5. **Critique**: If the implementation violates the Dependency Rule or other principles, point it out and suggest a refactor before finalizing.

## 📁 Directory Structure & Context
- **`data/`**: Source `.xlsx` or `.csv` files. Path configured via `CSV_FILE_PATH` in `.env`.
- **`.design/`**: Design documents and UI/UX specifications.
- **`.openproject/`**: `work_packages_api.md` (API details & Ktor examples) and `.env.example`.
- **`.jules/`**: `api_reference.md` (Jules API docs) and `.env.example`.

## 📊 Dashboard Data Consistency
When implementing sync logic from CSV/Excel, you **MUST** ensure the OpenProject work packages are populated with fields that drive the BoD Dashboard. Refer to `OPENPROJECT_DATA_MAPPING.md` for full details.

Key fields to map from source files:
- **Net Progress**: Map to `percentageDone` (0-100).
- **Effort Distribution**: 
    - Map "Hours" to `estimatedTime` using ISO 8601 format (e.g., `PT8H`).
    - Use keywords "Strategic" or "Growth" in the `subject` to categorize as Strategic vs BAU.
- **Critical Path**: Ensure `dueDate` is populated for items intended as Milestones. Use "Milestone" in the subject or ensure the Work Package Type is set correctly.
- **Risks**: Use "Risk" in the subject for automatic detection in the Risk Heatmap.
- **The Red List**: Populating `dueDate` and `percentageDone` is critical for identifying overdue items.

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
