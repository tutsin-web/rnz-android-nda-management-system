# Android Emulator Setup - Troubleshooting Guide

## 🚨 Issue: "No Module in Dropdown" when creating Android Emulator

This is a common Android Studio issue. Here are step-by-step solutions:

## 🔧 **Solution 1: Project Sync & Build**

### Step 1: Sync Project
1. Open Android Studio
2. Click **File → Sync Project with Gradle Files**
3. Wait for sync to complete (may take 5-10 minutes)
4. Look for "Gradle sync finished" in the status bar

### Step 2: Build Project
1. Click **Build → Clean Project**
2. Wait for clean to complete
3. Click **Build → Rebuild Project**
4. Wait for build to complete successfully

### Step 3: Check Module Recognition
1. Go to **File → Project Structure**
2. Under **Modules**, you should see "app" module
3. If not visible, click **+ → Import Gradle Project** and select your app folder

## 🔧 **Solution 2: Fix Gradle Configuration**

### Check build.gradle files:

1. **Root build.gradle** (already created as build.gradle.kts)
2. **app/build.gradle** (should exist)
3. **settings.gradle** (should include ':app')

### Verify settings.gradle content:
```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "RNZApp"
include ':app'
```

## 🔧 **Solution 3: Android SDK Setup**

### Step 1: Check SDK Installation
1. Go to **File → Settings** (or **Android Studio → Preferences** on Mac)
2. Navigate to **Appearance & Behavior → System Settings → Android SDK**
3. Ensure these are installed:
   - **Android SDK Platform** (API 34 or latest)
   - **Android SDK Build-Tools** (latest version)
   - **Android Emulator**
   - **Android SDK Platform-Tools**

### Step 2: Install Missing Components
1. In SDK Manager, check boxes for missing components
2. Click **Apply** and **OK**
3. Wait for installation to complete

## 🔧 **Solution 4: AVD Manager Setup**

### Step 1: Access AVD Manager
1. Click **Tools → AVD Manager**
2. Or click the AVD Manager icon in the toolbar
3. If greyed out, the project needs to sync first

### Step 2: Create Virtual Device
1. Click **Create Virtual Device**
2. Choose **Phone** category
3. Select **Pixel 4** or **Pixel 6**
4. Click **Next**

### Step 3: Select System Image
1. Choose **API Level 30** or higher
2. If not downloaded, click **Download** next to the API level
3. Wait for download to complete
4. Click **Next**

### Step 4: Configure AVD
1. Give it a name: "RNZ_Test_Device"
2. Click **Finish**

## 🔧 **Solution 5: Reset Android Studio**

### If above solutions don't work:

1. **Invalidate Caches**:
   - **File → Invalidate Caches and Restart**
   - Click **Invalidate and Restart**

2. **Reimport Project**:
   - Close Android Studio
   - Delete `.idea` folder in project root
   - Reopen project in Android Studio

3. **Reset AVD Manager**:
   - Close Android Studio
   - Delete `~/.android/avd` folder (backup first)
   - Restart Android Studio

## 🔧 **Solution 6: Alternative - Use Physical Device**

### If emulator issues persist:

1. **Enable Developer Options** on Android phone:
   - Settings → About Phone
   - Tap "Build Number" 7 times

2. **Enable USB Debugging**:
   - Settings → Developer Options
   - Turn on "USB Debugging"

3. **Connect Device**:
   - Connect via USB cable
   - Allow debugging when prompted
   - Device should appear in Android Studio

## 📱 **Quick Emulator Creation Steps**

### Once project is synced:

1. **Tools → AVD Manager**
2. **Create Virtual Device**
3. **Phone → Pixel 4 → Next**
4. **API 30+ → Download (if needed) → Next**
5. **Name: RNZ_Emulator → Finish**
6. **Click Play button** to start emulator

## 🚀 **Running the RNZ App**

### After emulator is created:

1. **Select emulator** from device dropdown
2. **Click Run button** (green play icon)
3. **Wait for app installation**
4. **App should launch** showing login screen

### Test Login:
```
Username: admin
Password: 4pAgL#MgH&wKt
```

## 🛠️ **Common Error Solutions**

### Error: "SDK location not found"
**Solution:**
1. File → Project Structure
2. SDK Location tab
3. Set Android SDK location (usually `/Users/[username]/Library/Android/sdk`)

### Error: "Gradle sync failed"
**Solution:**
1. Check internet connection
2. File → Invalidate Caches and Restart
3. Try again after restart

### Error: "No connected devices"
**Solution:**
1. Start emulator first
2. Wait for full boot (2-3 minutes)
3. Refresh device list in Android Studio

### Error: "App installation failed"
**Solution:**
1. Wipe emulator data: AVD Manager → Actions → Wipe Data
2. Restart emulator
3. Try installing again

## 📞 **Still Having Issues?**

### Check These:

1. **Android Studio Version**: Use Arctic Fox 2020.3.1 or later
2. **Java Version**: JDK 11 or later
3. **System Requirements**: 8GB RAM minimum
4. **Disk Space**: At least 4GB free for emulator

### Alternative Testing:
- Use Android Studio's **Layout Inspector** to preview UI
- Test on physical device instead of emulator
- Use **Firebase Test Lab** for cloud testing

## ✅ **Success Checklist**

- [ ] Project synced successfully
- [ ] Build completed without errors
- [ ] Module "app" visible in Project Structure
- [ ] Android SDK components installed
- [ ] AVD created successfully
- [ ] Emulator starts and boots completely
- [ ] App installs and runs on emulator
- [ ] Login screen appears
- [ ] Can login with test credentials

---

**💡 Pro Tip**: Always ensure your project builds successfully before trying to create an emulator. The "no module" issue is usually related to Gradle sync problems, not emulator configuration.
