# Android Studio Agent Instructions

Welcome! This repository acts as a bridge to sync data from local Excel/CSV files into OpenProject, leveraging the Jules API where necessary. 

As the Android Studio AI Agent, your goal is to assist the user in writing the Kotlin Multiplatform (KMP) code to perform this synchronization.

## 📁 Directory Structure & Context
You have several resources available to understand the environment and the target APIs:

- **`data/`**: This directory is where the user will place the source `.xlsx` or `.csv` files containing the data to be synced.
- **`.openproject/`**: 
  - Contains `.env.example` showing the required configuration variables.
  - Contains `work_packages_api.md` detailing how to authenticate, create, and update Work Packages using the OpenProject REST API (with Ktor examples).
- **`.jules/`**:
  - Contains `.env.example` showing required configuration variables.
  - Contains `api_reference.md` which has the documentation for the Jules API, in case Jules needs to be invoked during the sync process.

## 🛠️ Your Tasks
When the user asks you to implement the sync logic, you should follow these steps:

1. **Environment Setup**: Ensure the user has copied the `.env.example` files to actual `.env` files and populated them with their credentials. (Do not commit real `.env` files). Read these credentials into the KMP application.
2. **Read the Data**: Write Kotlin code to parse the Excel/CSV file located in the `data/` directory. You can use common Kotlin CSV libraries (like `kotlin-csv`) or Apache POI (if running purely on JVM/Desktop target).
3. **Map the Data**: Map the columns from the source file to the attributes required by OpenProject Work Packages (e.g., Subject, Description, Assignee, etc.).
4. **Sync via OpenProject API**: 
   - Use Ktor Client to interact with the OpenProject instance.
   - Refer strictly to `.openproject/work_packages_api.md` for payload structures.
   - First, fetch the appropriate Project ID if not already hardcoded.
   - Iterate through the parsed data and send `POST` requests to create new Work Packages or `PATCH` requests to update existing ones.
5. **Jules Integration (Optional)**: If the user requires you to trigger Jules tasks as part of the pipeline, refer to `.jules/api_reference.md` to format the REST requests properly.

## ⚠️ Important Notes
- Always handle HTTP errors gracefully and print informative logs (e.g., response status and body) if an OpenProject API call fails.
- OpenProject uses optimistic locking (`lockVersion`) for updates. If updating, fetch the work package first to get the current lock version.
- Make sure network requests are run inside Kotlin Coroutines (`suspend` functions) and don't block the main thread.
