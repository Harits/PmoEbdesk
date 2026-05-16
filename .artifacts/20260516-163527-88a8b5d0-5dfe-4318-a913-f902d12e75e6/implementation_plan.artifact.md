# Implementation Plan - Refactor to Screaming Architecture and Implement Sync Feature

This plan outlines the refactoring of the existing `dashboard` code to follow Screaming Architecture and the implementation of a new `sync` feature to synchronize CSV data with OpenProject.

## User Review Required

> [!IMPORTANT]
> - I will be moving existing classes to new packages. This will require updating imports in the `composeApp` module.
> - The `sync` feature will initially focus on reading the provided CSV files and creating/updating work packages in OpenProject.

## Proposed Changes

### Dashboard Feature Refactor

Reorganize existing dashboard-related code into a structured feature package.

#### [NEW] [DashboardModels.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/dashboard/domain/model/DashboardModels.kt)
- Move `RAGStatus`, `DashboardMetrics`, `Milestone`, `Risk`, `ProjectException`, and `BoardIntervention` here.

#### [NEW] [OpenProjectRepository.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/dashboard/domain/repository/OpenProjectRepository.kt)
- Move `OpenProjectRepository` interface here.

#### [NEW] [GetBodDashboardDataUseCase.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/dashboard/domain/usecase/GetBodDashboardDataUseCase.kt)
- Move `GetBodDashboardDataUseCase` here.

#### [NEW] [OpenProjectRepositoryImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/dashboard/data/repository/OpenProjectRepositoryImpl.kt)
- Move `MockOpenProjectRepositoryImpl` and `ProductionOpenProjectRepositoryImpl` here.

#### [NEW] [OpenProjectDtos.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/dashboard/data/remote/model/OpenProjectDtos.kt)
- Move `WorkPackagesResponse`, `EmbeddedWorkPackages`, `WorkPackageElement`, and `Description` here.

#### [DELETE] [Domain.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/Domain.kt)
#### [DELETE] [UseCase.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/UseCase.kt)
#### [DELETE] [Repository.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/Repository.kt)
#### [DELETE] [RepositoryImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/RepositoryImpl.kt)

---

### Sync Feature Implementation

Implement the sync pipeline from local CSV to OpenProject.

#### [NEW] [WorkPackage.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/domain/model/WorkPackage.kt)
- Domain entity representing an OpenProject work package.

#### [NEW] [SyncRepository.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/domain/repository/SyncRepository.kt)
- Interface for syncing work packages.

#### [NEW] [SyncWorkPackagesUseCase.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/domain/usecase/SyncWorkPackagesUseCase.kt)
- Use case to orchestrate the sync process.

#### [NEW] [SyncRepositoryImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/data/repository/SyncRepositoryImpl.kt)
- Implementation of `SyncRepository`.

#### [NEW] [CsvDataSource.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/data/local/CsvDataSource.kt)
- Responsible for parsing CSV files.

#### [NEW] [OpenProjectDataSource.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/sync/data/remote/OpenProjectDataSource.kt)
- Responsible for API calls to OpenProject for the sync feature.

## Verification Plan

### Automated Tests
- `SyncWorkPackagesUseCaseTest`: Verify the orchestration of reading CSV and calling OpenProject API.
- `CsvDataSourceTest`: Verify CSV parsing with sample data.
- `OpenProjectDataSourceTest`: Verify API call construction (using MockKtor or similar).

### Manual Verification
- Run the sync process with one of the provided CSV files and verify work packages are created/updated in a mock or real OpenProject instance.
