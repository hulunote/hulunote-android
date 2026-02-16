#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "=== Hulunote Android APK Builder ==="

# 1. Check ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    # Try common macOS paths
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "/usr/local/share/android-sdk" ]; then
        export ANDROID_HOME="/usr/local/share/android-sdk"
    else
        echo "ERROR: ANDROID_HOME not set. Please set it to your Android SDK path."
        exit 1
    fi
fi
echo "ANDROID_HOME: $ANDROID_HOME"

# 2. Check JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    # Try macOS java_home
    if /usr/libexec/java_home -v 17 >/dev/null 2>&1; then
        export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    elif /usr/libexec/java_home >/dev/null 2>&1; then
        export JAVA_HOME=$(/usr/libexec/java_home)
    fi
fi
echo "JAVA_HOME: $JAVA_HOME"

# 3. Generate gradle wrapper if missing
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo ">>> Generating Gradle wrapper..."
    # Use Android Studio's bundled Gradle if available
    STUDIO_GRADLE=$(find /Applications/Android\ Studio*.app -name "gradle" -type f -path "*/bin/gradle" 2>/dev/null | head -1)
    if [ -n "$STUDIO_GRADLE" ]; then
        echo "Using Android Studio Gradle: $STUDIO_GRADLE"
        "$STUDIO_GRADLE" wrapper --gradle-version 8.11.1
    elif command -v gradle >/dev/null 2>&1; then
        gradle wrapper --gradle-version 8.11.1
    else
        echo "ERROR: No gradle found. Please run 'gradle wrapper' manually or open project in Android Studio first."
        exit 1
    fi
fi

# 4. Build debug APK
echo ">>> Building debug APK..."
./gradlew assembleDebug

# 5. Output location
APK_PATH="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "=== BUILD SUCCESS ==="
    echo "APK: $APK_PATH"
    echo "Size: $SIZE"
    echo ""
    echo "Install to device:  adb install -r $APK_PATH"
else
    echo "ERROR: APK not found at expected path."
    exit 1
fi
