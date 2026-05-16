# Implementation Plan - Fix Search, Hide Budget, and Fix Filter

Fix the project search feature, hide the budget field, and implement the missing filtering functionality.

## User Review Required

> [!IMPORTANT]
> - I am hiding the budget field from the `ProjectCard`.
> - I will implement a "Status" filter in the `FilterBar`.
> - For the search feature, I will enhance it to search both Name and Identifier in the mock repository, and verify the production API call format.

## Proposed Changes

### Domain Layer

#### [ProjectRepository.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/domain/repository/ProjectRepository.kt)
- Add `status: ProjectStatus? = null` to `searchProjects` method.

#### [SearchProjectsUseCase.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/domain/usecase/SearchProjectsUseCase.kt)
- Add `status: ProjectStatus? = null` to `invoke` method.

### Data Layer

#### [ProjectRepositoryImpl.kt](file:///Users/macbook/StudioProjects/pmoebdesk/shared/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/data/repository/ProjectRepositoryImpl.kt)
- Update `MockProjectRepositoryImpl` to filter by `ProjectStatus`.
- Update `ProductionProjectRepositoryImpl` to add status filter to OpenProject API call.
- Enhance search logic in `MockProjectRepositoryImpl` to search in `identifier` as well.

### Presentation Layer

#### [ProjectCard.kt](file:///Users/macbook/StudioProjects/pmoebdesk/composeApp/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/ui/components/ProjectCard.kt)
- Remove the Budget display section.

#### [FilterBar.kt](file:///Users/macbook/StudioProjects/pmoebdesk/composeApp/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/ui/components/FilterBar.kt)
- Add interactivity: Click to open a status selection menu.
- Add `selectedStatus: ProjectStatus?` and `onStatusSelected: (ProjectStatus?) -> Unit` parameters.

#### [SearchScreen.kt](file:///Users/macbook/StudioProjects/pmoebdesk/composeApp/src/commonMain/kotlin/com/sekota/pmoebdesk/projects/ui/SearchScreen.kt)
- Add `selectedStatus` and `onStatusChange` parameters and pass them to `FilterBar`.

#### [main.kt (wasmJsMain)](file:///Users/macbook/StudioProjects/pmoebdesk/composeApp/src/wasmJsMain/kotlin/com/sekota/pmoebdesk/main.kt)
- Add `selectedStatus` state to `AppContainer`.
- Update `handleSearch` (or add `handleFilter`) to include status in the repository call.

## Verification Plan

### Automated Tests
- `gradle_build(":shared:allTests")`
- Add new tests in `shared/src/commonTest/kotlin/com/sekota/pmoebdesk/projects/data/repository/ProjectRepositoryTest.kt` to verify search and filter logic in `MockProjectRepositoryImpl`.

### Manual Verification
1. Open the search screen.
2. Type a keyword that matches a project name or identifier.
3. Click on the Filter bar and select a status.
4. Verify the list updates correctly.
5. Verify the Budget field is gone from project cards.
