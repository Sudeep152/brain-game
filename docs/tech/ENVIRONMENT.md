# Android Development Environment

Checked on 2026-10-14 for T-004. No software or SDK packages were installed, upgraded, or removed.

## Host

```text
$ sw_vers
ProductName:		macOS
ProductVersion:		27.0.1
BuildVersion:		26A434

$ uname -m
arm64

$ uname -r
27.0.0
```

## JDK

The shell resolves Java through `/usr/bin/java`. `JAVA_HOME` is not exported.

```text
$ java -version
openjdk version "25.0.4.1" 2026-08-18
OpenJDK Runtime Environment JBR-25.0.4.1+1-623.69-nomod (build 25.0.4.1+1-b623.69)
OpenJDK 64-Bit Server VM JBR-25.0.4.1+1-623.69-nomod (build 25.0.4.1+1-b623.69, mixed mode, sharing)

$ javac -version
javac 25.0.4.1

$ /usr/libexec/java_home -V
Matching Java Virtual Machines (3):
    25.0.4.1 (arm64) "JetBrains s.r.o." - "JBR-25.0.4.1+1-623.69-nomod 25.0.4.1" /Users/shashank/Library/Java/JavaVirtualMachines/jbr-25.0.4.1/Contents/Home
/Users/shashank/Library/Java/JavaVirtualMachines/jbr-25.0.4.1/Contents/Home
    21.0.11 (arm64) "JetBrains s.r.o." - "JBR-21.0.11+10-1163.116-nomod 21.0.11" /Users/shashank/Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home
    17.0.19 (arm64) "Eclipse Adoptium" - "OpenJDK 17.0.19" /Users/shashank/Library/Java/JavaVirtualMachines/temurin-17.0.19/Contents/Home
```

Android Studio's bundled runtime is a separate JDK:

```text
$ /Applications/Android\ Studio.app/Contents/jbr/Contents/Home/bin/java -version
openjdk version "25.0.3" 2026-04-21
OpenJDK Runtime Environment (build 25.0.3+-15898627-b508.16)
OpenJDK 64-Bit Server VM (build 25.0.3+-15898627-b508.16, mixed mode)
```

## Android Studio

Installed at `/Applications/Android Studio.app`.

```text
$ /usr/libexec/PlistBuddy -c 'Print :CFBundleShortVersionString' '/Applications/Android Studio.app/Contents/Info.plist'
2026.2

$ /usr/libexec/PlistBuddy -c 'Print :CFBundleVersion' '/Applications/Android Studio.app/Contents/Info.plist'
AI-262.9437.185.2621.16467767
```

## Android SDK and platforms

`ANDROID_HOME` and `ANDROID_SDK_ROOT` are not exported, but the SDK is present at the standard path `/Users/shashank/Library/Android/sdk`. Android SDK command-line tools are not on `PATH`, and no `cmdline-tools`/`sdkmanager` installation was found under that SDK.

```text
$ for var_name in ANDROID_HOME ANDROID_SDK_ROOT JAVA_HOME; do ...; done
ANDROID_HOME=<unset>
ANDROID_SDK_ROOT=<unset>
JAVA_HOME=<unset>

$ ls -1 /Users/shashank/Library/Android/sdk/platforms
android-34
android-35
android-36
android-36.1
android-37.0
android-37.1

$ for platform_path in /Users/shashank/Library/Android/sdk/platforms/*; do ...; done
android-34: 3
android-35: 2
android-36: 2
android-36.1: 1
android-37.0: 2
android-37.1: 1

$ ls -1 /Users/shashank/Library/Android/sdk/build-tools
34.0.0
35.0.0
36.0.0
36.1.0
37.0.0
```

## Gradle

There is no global `gradle` command on `PATH`, and this folder has no Gradle wrapper (`gradlew`). Four wrapper distributions are cached: 8.11.1, 8.14.5, 9.4.1, and 9.6.1. The newest cached executable runs successfully outside the file sandbox:

```text
$ gradle --version
zsh:1: command not found: gradle

$ /Users/shashank/.gradle/wrapper/dists/gradle-9.6.1-bin/4ticwg1pgcbps2hj28r8so764/gradle-9.6.1/bin/gradle --version

------------------------------------------------------------
Gradle 9.6.1
------------------------------------------------------------

Build time:    2026-06-26 14:25:50 UTC
Revision:      309d128bd9fe8c0b71311878fc660b9cbaa07c51

Kotlin:        2.3.21
Groovy:        4.0.32
Ant:           Apache Ant(TM) version 1.10.17 compiled on April 6 2026
Launcher JVM:  25.0.4.1 (JetBrains s.r.o. 25.0.4.1+1-b623.69)
Daemon JVM:    /Users/shashank/Library/Java/JavaVirtualMachines/jbr-25.0.4.1/Contents/Home (no Daemon JVM specified, using current Java home)
OS:            Mac OS X 27.0.1 aarch64
```

## adb and connected devices

`adb` is installed in the SDK but is not on `PATH`.

```text
$ /Users/shashank/Library/Android/sdk/platform-tools/adb version
Android Debug Bridge version 1.0.41
Version 37.0.1-15733141
Installed as /Users/shashank/Library/Android/sdk/platform-tools/adb
Running on Darwin 27.0.0 (arm64)

$ /Users/shashank/Library/Android/sdk/platform-tools/adb devices -l
* daemon not running; starting now at tcp:5037
* daemon started successfully
List of devices attached

```

No physical device or running emulator was attached. The adb server was stopped after this check to restore its prior state.

## Emulator and AVDs

The Android Emulator package is installed at version 37.2.12. Its executable is arm64, but asking it for its version returned the error below. Listing AVDs still worked.

```text
$ file /Users/shashank/Library/Android/sdk/emulator/emulator
/Users/shashank/Library/Android/sdk/emulator/emulator: Mach-O 64-bit executable arm64

$ sed -n '1,120p' /Users/shashank/Library/Android/sdk/emulator/source.properties
Pkg.UserSrc=false
Pkg.Revision=37.2.12
Pkg.Path=emulator
Pkg.Desc=Android Emulator
Pkg.BuildId=16428233

$ /Users/shashank/Library/Android/sdk/emulator/emulator -version
Incompatible processor. This Qt build requires the following features:
    neon

$ /Users/shashank/Library/Android/sdk/emulator/emulator -list-avds
Pixel_10a
Wear_OS_Small_Round
```

Installed system images:

```text
$ find /Users/shashank/Library/Android/sdk/system-images -mindepth 3 -maxdepth 3 -type d -print
/Users/shashank/Library/Android/sdk/system-images/android-37.0/android-wear-signed/arm64-v8a
/Users/shashank/Library/Android/sdk/system-images/android-37.2/google_apis_playstore_ps16k/arm64-v8a
```

## Notable limitations

- SDK, `adb`, and emulator executables are installed but not on the current shell's `PATH`.
- No global Gradle command or project Gradle wrapper exists yet; only cached wrapper distributions are available.
- The emulator executable reports a missing `neon` processor feature on this host, so emulator launch viability remains unverified.
- No connected Android device or running emulator was detected.
