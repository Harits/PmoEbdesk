# Use Case Specifications

## 1. View PMO Dashboard
**Actor**: Board of Director
**Description**: The Board of Director accesses the main dashboard to view the overall health, progress, and critical issues of the PMO.
**Pre-conditions**: User is authenticated and authorized as a Board of Director.
**Post-conditions**: The user sees the fully loaded dashboard with all current metrics, statuses, and exceptions.

## 2. View Strategic RAG Status
**Actor**: Board of Director
**Description**: The user views the current Red/Amber/Green status to quickly gauge the strategic health of the PMO portfolio.
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: RAG status is visible.

## 3. View Progress Updates
**Actor**: Board of Director
**Description**: The user views detailed progress metrics, such as Net Progress % and effort hours (Strategic Growth vs Business As Usual).
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: Progress metrics are visible.

## 4. View Critical Path Milestones
**Actor**: Board of Director
**Description**: The user views the timeline and status of upcoming critical milestones.
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: Timeline and milestone statuses are visible.

## 5. View Exception Management (The Red List)
**Actor**: Board of Director
**Description**: The user views projects that require immediate attention (The Red List) and their mitigation summaries.
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: A list of critical exceptions and proposed mitigations is visible.

## 6. View Risk Heatmap
**Actor**: Board of Director
**Description**: The user views a heatmap showing the distribution and severity of project risks.
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: The Risk Heatmap matrix is visible.

## 7. Manage Board Interventions
**Actor**: Board of Director, Admin
**Description**: Users view actions that require board-level intervention, such as budget approvals or strategic shifts.
**Pre-conditions**: Dashboard is loaded.
**Post-conditions**: A list of required interventions is visible.

## 8. Sign Decisions
**Actor**: Board of Director
**Description**: The Board of Director approves or signs off on the requested board interventions.
**Pre-conditions**: User is viewing the required interventions.
**Post-conditions**: The intervention status is updated to signed/approved.
