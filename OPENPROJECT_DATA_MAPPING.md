# OpenProject to Dashboard Data Mapping

This document explains how data is fetched from OpenProject and mapped to the Dashboard UI components.

## 1. Data Retrieval Flow

The process starts in `main.kt` and flows through the repository to the API.

1.  **`main.kt` (`AppContainer`)**:
    *   Triggers `getDashboardMetrics` whenever `selectedProject` or the `projects` list changes.
    *   Determines which Project IDs to fetch based on `config.json` (`ALLOWED_PROJECT_IDS`) or the current project selection.

2.  **`ProductionOpenProjectRepositoryImpl`**:
    *   Constructs a request to `/api/v3/work_packages`.
    *   **Filters**: Applies a JSON filter to only fetch work packages belonging to the specified project(s).
    *   **Page Size**: Fetches up to 100 work packages per request.

## 2. Metric Calculations

Once the work packages are fetched, they are processed into `DashboardMetrics`:

| Dashboard Metric | Calculation / Mapping Logic |
| :--- | :--- |
| **Portfolio Health** | Based on `netProgressPercentage`: >80% is Green, >50% is Amber, else Red. |
| **Net Progress** | The average of `percentageDone` across all fetched work packages. |
| **Trend %** | Currently a derived value: +2.5% if progress > 50%, -1.2% otherwise. |
| **Effort Distribution** | **Strategic**: Sum of `estimatedTime` for work packages with "Strategic" or "Growth" in the subject.<br>**BAU**: Sum of all other work package `estimatedTime`. |
| **Milestones** | Work packages where the `type` link title contains "Milestone" or the subject contains "Milestone". Sorted by `dueDate`. |
| **Risks** | Work packages where the `type` link title contains "Risk" or the subject contains "Risk". |
| **The Red List** | Filtered list of work packages that are **not 100% done**, have a **dueDate**, and that date is **in the past**. |
| **Board Interventions** | Suggested actions based on the number of overdue work packages found in "The Red List". |

## 3. Configuration Details

*   **API Base**: Defined in `config.json` as `OPENPROJECT_URL`.
*   **Authentication**: Basic Auth using `apikey:<OPENPROJECT_API_KEY>`.
*   **Time Tracking**: For "Effort Distribution" to be accurate, users must fill in the "Estimated Time" field in OpenProject work packages (e.g., `8h`).

## 4. UI Components

The data is displayed in the following components in `DashboardScreen.kt`:
*   `PortfolioHealthBanner`
*   `NetProgressCard`
*   `EffortDistributionCard`
*   `CriticalPathRoadmapCard`
*   `RiskHeatmapCard`
*   `RedListTableCard`
*   `BoardInterventionsSidebar`
