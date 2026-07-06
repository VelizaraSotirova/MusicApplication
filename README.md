MusicApp: Collaborative Catalog Manager
MusicApp is a full-stack music catalog management system. It allows users to manage their songs, filter/search libraries, and collaborate with friends by merging external song catalogs. The project has evolved from basic command-line requirements into a robust client-server architecture using an Android mobile interface and a Spring Boot backend.

Features
Catalog Management: Add, delete, and list songs with rating support (1-5 stars).
Search & Filter: Search by title or artist (case-insensitive) and sort the catalog dynamically.
Merge Functionality: Import and merge external catalogs from JSON files, automatically handling duplicates.
Undo History: Undo recent actions to revert catalog changes.
Full-Stack Architecture: Secured user authentication and persistent storage via a relational database.

Tech Stack
Backend: Java, Spring Boot, JPA/Hibernate.
Frontend: Android (Kotlin), Jetpack Compose, MVVM pattern.
Networking: Retrofit for REST API communication.

How to Run
Backend:
  Navigate to the backend directory.
  Ensure a database is connected via application.properties.
  Execute: ./mvnw spring-boot:run.
Frontend:
  Open the Android project in Android Studio.
  Update BASE_URL in RetrofitClient.kt to your server's IP address.
  Build and deploy to an Android device/emulator.

Assumptions & Implementation Notes
Architecture: The project was built as a full-stack mobile application rather than a CLI tool, as this provides a more scalable and user-friendly platform for managing music catalogs.
Persistence: Songs are persisted in a relational database rather than a local file to support multi-user authentication.
Undo/Redo: Undo functionality is implemented for catalog modifications; Redo is currently out of scope for this version.

AI Usage Statement
During the development of this project, I utilized AI tools (Gemini) primarily as a coding assistant. I employed AI to generate boilerplate code for Retrofit services and to debug complex data-flow issues between the ViewModel and the Repository. However, I overrode the AI's initial architectural advice regarding the Merge function and undo logic; while the AI suggested creating a separate MergeController for clean separation of concerns, I decided to consolidate the logic within the CatalogController to reduce system complexity and maintain a more unified entry point for all catalog-related operations given the project's current scale.
