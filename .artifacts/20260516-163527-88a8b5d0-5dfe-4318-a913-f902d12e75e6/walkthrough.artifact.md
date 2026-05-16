# Walkthrough - Refactor to Screaming Architecture & Sync Feature Implementation

I have successfully refactored the project to follow **Screaming Architecture** and implemented the **Sync Feature** using **Clean Architecture** principles.

## Dashboard Feature Refactor

The previously monolithic `com.sekota.pmoebdesk` package has been reorganized into a feature-based structure.

- **Domain Layer**: Contains entities (`DashboardModels.kt`), the repository interface (`OpenProjectRepository.kt`), and the use case (`GetBodDashboardDataUseCase.kt`).
- **Data Layer**: Contains the production and mock repository implementations (`OpenProjectRepositoryImpl.kt`) and the API DTOs (`OpenProjectDtos.kt`).

This ensures that the business logic is decoupled from implementation details and the folder structure clearly shows the "Dashboard" feature.

## Sync Feature Implementation

The new `sync` feature provides a pipeline to synchronize local CSV data with OpenProject work packages.

### Key Components

- **[SyncWorkPackagesUseCase.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/domain/usecase/SyncWorkPackagesUseCase.kt)**: Orchestrates the sync process. It includes logic to "expand" tasks marked as "Monthly" into multiple monthly work packages.
- **[SyncRepositoryImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/data/repository/SyncRepositoryImpl.kt)**: Maps data between CSV sources and OpenProject.
- **[JvmCsvDataSource.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/jvmMain/kotlin/com/sekota/pmoebdesk/sync/data/local/JvmCsvDataSource.kt)**: A JVM-specific implementation for reading CSV files using `kotlin-csv`.
- **[OpenProjectDataSourceImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/data/remote/OpenProjectDataSourceImpl.kt)**: Handles REST API interactions with OpenProject.

### Monthly Task Expansion
The sync engine automatically detects if a task is "Monthly" (e.g., "Pembuatan Laporan Monthly") and creates individual work packages for each month between the project's start and finish dates.

## Verification Summary

### Automated Tests
- **[SyncWorkPackagesUseCaseTest.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonTest/kotlin/com/sekota/pmoebdesk/sync/domain/usecase/SyncWorkPackagesUseCaseTest.kt)**: Verified that a monthly task spanning 3 months is correctly expanded into 3 separate work packages with appropriate dates.
- **[RepositoryTest.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonTest/kotlin/com/sekota/pmoebdesk/RepositoryTest.kt)**: Verified that the dashboard mock data still works after the refactor.

### Compilation
- Verified that the `:shared` module passes all JVM tests.
- Verified that the `:server` module (which contains the `OpenProjectSync` script) compiles successfully with the new shared logic.

## How to Run Sync
The sync process can be triggered via the Gradle task in the server module:
```bash
./gradlew :server:runSync
```
Ensure your `.openproject/.env` is configured with the correct `OPENPROJECT_HOST`, `OPENPROJECT_API_KEY`, and `CSV_FILE_PATH`.
