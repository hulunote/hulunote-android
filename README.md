# Hulunote Android

An outline-based note editor for Android, built with Jetpack Compose. Connects to the [Hulunote](https://www.hulunote.top) backend for syncing databases, notes, and hierarchical outline blocks.

## Screenshots

- Login with email/password
- Browse databases and notes
- Edit outlines with indentation, collapse/expand, and inline editing

## Features

- **Outline Editor** - Tree-structured block editing with indent/outdent, collapse/expand, and reorder
- **Real-time Sync** - Debounced auto-save to the Hulunote backend
- **Database & Note Management** - Browse databases, create/delete notes
- **Purple Theme** - Custom Material 3 theme matching the Hulunote design system

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository |
| Networking | Retrofit + OkHttp + Moshi |
| DI | Hilt |
| Async | Coroutines + StateFlow |
| Navigation | Navigation Compose |
| Min SDK | 26 (Android 8.0) |

## Project Structure

```
com.hulunote.android/
├── data/
│   ├── api/          # Retrofit API interface
│   ├── model/        # Data classes (Auth, Database, Note, Nav)
│   └── repository/   # Repository layer
├── di/               # Hilt dependency injection modules
├── ui/
│   ├── theme/        # Colors, typography, Material 3 theme
│   ├── login/        # Login screen
│   ├── database/     # Database list screen
│   ├── note/         # Note list screen
│   └── editor/       # Outline editor (core feature)
└── util/             # Token manager, auth interceptor
```

## Build

### Prerequisites

- Android Studio (Ladybug or later)
- JDK 17
- Android SDK with API 35

### Build APK

```bash
./build_apk.sh
```

Or manually:

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Install to Device

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Backend

This app connects to `https://www.hulunote.top`. The backend is built with Rust (Axum + PostgreSQL). See [hulunote-rust](../hulunote-rust) for the backend source.

## License

MIT
