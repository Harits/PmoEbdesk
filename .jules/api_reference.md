# Jules API Reference

Jules is Google's AI-powered autonomous coding agent. The API allows you to programmatically trigger coding sessions for tasks like bug fixing, dependency upgrades, and refactoring.

> [!WARNING]
> The Jules API is currently in **v1alpha**. Specifications are subject to change.

## 🚀 Getting Started

### Base URL
`https://jules.googleapis.com/v1alpha`

### Authentication
The API uses API keys for authentication.
- **Header**: `X-Goog-Api-Key`
- **Value**: Your Jules API Key (generated in Jules web app settings).

## 📂 Core Resources

### Sources (`sources/*`)
Represents the codebase the agent will work on (e.g., a GitHub repository).
- **Prerequisite**: The Jules GitHub app must be installed on the target repository.

### Sessions (`sessions/*`)
A unit of work or "project" context. You start a session by providing a prompt and a source.

### Activities (`sessions/*/activities/*`)
Individual events within a session, such as plan generation, messages, or progress reports.

## 🛠️ Key Methods

### Create Session
Starts a new coding session.
- **Endpoint**: `POST /sessions`
- **Payload Example**:
```json
{
  "name": "sessions/my-upgrade-task",
  "source": "sources/my-github-repo",
  "prompt": "Upgrade the project to use Kotlin 2.0 and fix any breaking changes."
}
```

### Send Message
Interact with the agent to refine requirements or provide feedback.
- **Endpoint**: `POST /sessions/{session_id}:sendMessage`
- **Payload Example**:
```json
{
  "message": "Focus on the shared module first."
}
```

### Approve Plan
Approve the agent's proposed plan before it modifies code.
- **Endpoint**: `POST /sessions/{session_id}/activities/{activity_id}:approve`

### Get Progress
Monitor the status of a session or activity.
- **Endpoint**: `GET /sessions/{session_id}`

## 💡 Common Use Cases
- **Bug Fixing**: Automatically diagnose and fix reported issues.
- **Dependency Management**: Automate library upgrades and migration tasks.
- **Code Refactoring**: Apply large-scale changes across the codebase.
- **Test Generation**: Identify and fill gaps in test coverage.

## 🔗 References
- [Official Jules Website](https://jules.google)
- [Jules API Reference (Internal)](https://jules.google/docs/api/reference/)
