Event Management App

A native Android Event Management application built using Kotlin, XML, Firebase and MVVM architecture. The application allows users to securely authenticate, create and manage events, view event analytics, search and filter events, and receive Firebase Cloud Messaging notifications.

Features
Authentication
Email/password registration
Email/password login
Persistent Firebase authentication session
Password reset through email
User-friendly authentication error messages
Logout
Event Management
Create events
Edit existing events
Delete events
Event title validation
Event date and time validation
Prevents creating events in the past
Event description
Event location
Events stored separately for each authenticated user
Real-time Firestore updates
Dashboard
Total events count
Upcoming events count
Past events count
Displays the next 4 upcoming events
Events displayed in chronological order
Monthly event statistics using MPAndroidChart
Search & Filters
Search events by:
Title
Description
Location
Filter by:
All
Upcoming
Past
Theme
System Default
Light Mode
Dark Mode
Notifications
Firebase Cloud Messaging integration
FCM token stored for authenticated users
Notification channel support
Push notification handling
Notification permission support on Android 13+
Technology Stack
Language: Kotlin
UI: XML
Architecture: MVVM + Repository Pattern
State Management: ViewModel + LiveData
Authentication: Firebase Authentication
Database: Cloud Firestore
Push Notifications: Firebase Cloud Messaging
Charts: MPAndroidChart
Asynchronous Operations: Kotlin Coroutines
Firebase Tasks: Kotlin Coroutines Play Services
UI Components: Material Components
Minimum SDK: 24
Target SDK: 37
Compile SDK: 37
Java: 11
Architecture

The application follows MVVM with Repository Pattern.

UI Layer
│
├── Activities
│   └── Dashboard
│   └── EventActivity
│   └── ViewAllEvents
│   └── Authentication
│
├── Fragments
│   ├── LoginFragment
│   ├── SignUpFragment
│   └── ForgetPasswordFragment
│
▼
ViewModel Layer
│
├── AuthViewModel
└── EventViewModel
│
▼
Repository Layer
│
├── AuthRepository
└── EventRepository
│
▼
Firebase
│
├── Firebase Authentication
├── Cloud Firestore
└── Firebase Cloud Messaging
Architecture Flow
User Interaction
       ↓
Activity / Fragment
       ↓
ViewModel
       ↓
Repository
       ↓
Firebase
       ↓
Result / LiveData
       ↓
UI Update

The Repository layer keeps Firebase-related operations separate from the UI and ViewModel layers.

Project Structure
app/src/main/java/com/example/eventmanagement/

├── auth/
│   ├── data/
│   │   └── repository/
│   │       └── AuthRepository.kt
│   │
│   ├── ui/
│   │   ├── activity/
│   │   │   └── Authentication.kt
│   │   └── fragments/
│   │       ├── LoginFragment.kt
│   │       ├── SignUpFragment.kt
│   │       └── ForgetPasswordFragment.kt
│   │
│   └── viewmodel/
│       └── AuthViewModel.kt
│
├── events/
│   ├── data/
│   │   ├── models/
│   │   │   └── Events.kt
│   │   └── repository/
│   │       └── EventRepository.kt
│   │
│   ├── ui/
│   │   ├── Dashboard.kt
│   │   ├── EventActivity.kt
│   │   ├── ViewAllEvents.kt
│   │   └── adapters/
│   │       └── EventListAdapter.kt
│   │
│   └── viewmodel/
│       └── EventViewModel.kt
│
├── notifications/
│   └── MyFirebaseMessagingService.kt
│
└── utils/
    ├── EventManagementApplication.kt
    ├── NotificationHelper.kt
    └── ThemeManager.kt
Firebase Configuration

The application uses:

Firebase Authentication
Cloud Firestore
Firebase Cloud Messaging
1. Create a Firebase Project

Create a project using the Firebase Console.

2. Add Android Application

Register an Android application using:

com.example.eventmanagement

The package name must match the application's applicationId.

3. Add google-services.json

Download the Firebase configuration file and place it inside:

app/google-services.json

The file is intentionally excluded from this GitHub repository using .gitignore.

4. Enable Authentication

In Firebase Console:

Authentication
→ Sign-in method
→ Email/Password
→ Enable
5. Create Firestore Database

In Firebase Console:

Firestore Database
→ Create Database

The application stores events using the following structure:

users
 └── {userId}
      └── events
           └── {eventId}
                ├── id
                ├── title
                ├── description
                ├── dateTime
                ├── location
                └── userId
6. Firebase Cloud Messaging

FCM is used for push notification support.

The FCM token is stored under:

users/{userId}/fcmToken
How to Run
Requirements
Android Studio
JDK 11
Android SDK
Firebase account/project
Android device or emulator
Internet connection for Firebase operations
Steps
Clone this repository.
git clone https://github.com/abprince99/Event-Management.git
Open the project in Android Studio.
Add your Firebase configuration file:
app/google-services.json
Sync the project with Gradle.
Make sure Firebase Authentication and Firestore are configured.
Run the application on an Android device or emulator.
Authentication Flow
Splash Screen
      ↓
Check FirebaseAuth.currentUser
      ↓
 ┌───────────────┐
 │               │
Logged In     Not Logged In
 │               │
 ↓               ↓
Dashboard     Authentication

Firebase Authentication automatically maintains the authenticated session.

Event Flow
Create / Edit Event
        ↓
EventActivity
        ↓
EventViewModel
        ↓
EventRepository
        ↓
Cloud Firestore
        ↓
Real-time Listener
        ↓
Dashboard / Event List
Event Validation

The application validates:

Title cannot be empty
Email cannot be empty
Password cannot be empty
Password must meet Firebase's minimum requirement
Event date must be selected
Event time must be selected
Event date/time cannot be in the past

Firebase exceptions are converted into user-friendly messages where applicable.

Offline Support

Cloud Firestore's Android SDK provides offline persistence support.

Previously retrieved Firestore data can remain available locally when the device temporarily loses network connectivity, and Firestore synchronizes changes when connectivity is restored.

Dashboard Analytics

The dashboard displays:

Total events
Upcoming events
Past events
Upcoming event list
Monthly event statistics

The monthly statistics are displayed using MPAndroidChart.

Search and Filtering

The All Events screen provides:

Search

Searches across:

Title
Description
Location
Filters
All
Upcoming
Past

Search and filtering are performed on the events received from Firestore.

Dark Mode

The application supports:

System Default
Light Mode
Dark Mode

The selected theme is stored locally using SharedPreferences and applied when the application starts.

Push Notifications

Firebase Cloud Messaging is integrated for push notifications.

The application:

Obtains the FCM device token.
Stores the token for the authenticated user.
Handles incoming Firebase messages.
Displays notifications using a notification channel.
Requests notification permission on Android 13+.

The current implementation provides the FCM client-side infrastructure for receiving notifications.

Security & Configuration

The Firebase configuration file:

google-services.json

is excluded from version control using .gitignore.

Do not commit private API keys, service-account credentials, private signing keys, or other secrets to the repository.

For production deployment, Firebase Authentication and Firestore Security Rules should be configured according to the application's production security requirements.

Repository

GitHub:

https://github.com/abprince99/Event-Management

Package Name
com.example.eventmanagement
License

This project was created as an Android development assignment/demo project.
