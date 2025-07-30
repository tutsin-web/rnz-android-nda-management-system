# RNZ App - Setup Instructions

Follow these step-by-step instructions to set up and run the RNZ Android app on your development environment.

## 📋 Prerequisites

### Required Software
1. **Android Studio** (Arctic Fox 2020.3.1 or later)
   - Download from: https://developer.android.com/studio
   - Install with default settings

2. **Java Development Kit (JDK) 11 or later**
   - Usually bundled with Android Studio
   - Verify: `java -version` in terminal

3. **Android SDK** (API Level 24 or higher)
   - Will be installed through Android Studio

### Hardware Requirements
- **For Development**: 8GB RAM minimum, 16GB recommended
- **For Testing**: Android device (API 24+) or emulator

## 🚀 Step-by-Step Setup

### Step 1: Download the Project
1. Download all project files from the current workspace
2. Create a new folder called `RNZApp` on your computer
3. Copy all files maintaining the folder structure:
```
RNZApp/
├── app/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── README.md
└── SETUP_INSTRUCTIONS.md
```

### Step 2: Open in Android Studio
1. Launch **Android Studio**
2. Click **"Open an Existing Project"**
3. Navigate to your `RNZApp` folder and select it
4. Click **"OK"**

### Step 3: Sync Project
1. Android Studio will automatically start syncing
2. If prompted, click **"Sync Now"** in the notification bar
3. Wait for Gradle sync to complete (may take 5-10 minutes first time)
4. Resolve any dependency issues if prompted

### Step 4: Configure Android SDK
1. Go to **File → Project Structure**
2. Under **Project Settings → Project**, set:
   - **Project SDK**: Android API 34
   - **Project language level**: 8 or higher
3. Under **Modules → app**, verify:
   - **Compile SDK Version**: 34
   - **Build Tools Version**: Latest available

### Step 5: Set Up Device/Emulator

#### Option A: Physical Device (Recommended)
1. Enable **Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging**:
   - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Allow USB debugging when prompted

#### Option B: Android Emulator
1. **First, ensure project is synced**: File → Sync Project with Gradle Files
2. **Build the project**: Build → Clean Project, then Build → Rebuild Project
3. In Android Studio: **Tools → AVD Manager**
4. Click **"Create Virtual Device"**
5. Choose **Pixel 4** or similar modern device
6. Select **API Level 30** or higher
7. Click **"Next"** → **"Finish"**
8. Start the emulator

**⚠️ If you see "No Module in Dropdown" error, see the EMULATOR_SETUP_GUIDE.md file for detailed troubleshooting steps.**

### Step 6: Build the Project
1. In Android Studio, click **Build → Make Project**
2. Wait for build to complete
3. Check **Build** tab for any errors
4. If errors occur, check the troubleshooting section below

### Step 7: Run the App
1. Select your device/emulator from the device dropdown
2. Click the **green "Run" button** (▶️) or press **Shift+F10**
3. Wait for app installation and launch
4. The login screen should appear

## 🔐 Testing the App

### Login Credentials
- **Username**: `admin`
- **Password**: `4pAgL#MgH&wKt`

### Test Flow
1. **Login**: Enter credentials and tap "Login"
2. **Dashboard**: Verify all 4 cards are visible and clickable
3. **NDA Form**: 
   - Fill out the form
   - Test country selection (UAE vs Others)
   - Try file upload
   - Submit form
4. **QR Scanner**: 
   - Grant camera permission
   - Test QR code scanning
5. **PDF Viewer**: 
   - Test PDF loading
   - Try download/share functions
6. **Analytics**: 
   - View charts and data
   - Test date filtering

## 🛠️ Troubleshooting

### Common Issues & Solutions

#### 1. Gradle Sync Failed
```bash
# Solution: Clean and rebuild
./gradlew clean
./gradlew build
```

#### 2. SDK Not Found
- Go to **File → Project Structure → SDK Location**
- Set Android SDK location (usually: `/Users/[username]/Library/Android/sdk`)

#### 3. Build Tools Missing
- Open **SDK Manager** (Tools → SDK Manager)
- Install latest **Build Tools** and **Platform Tools**

#### 4. Dependency Resolution Issues
- Check internet connection
- Try **File → Invalidate Caches and Restart**

#### 5. App Crashes on Launch
- Check **Logcat** for error messages
- Verify minimum SDK version (API 24)
- Ensure all permissions are granted

#### 6. Network Issues
- Verify internet connection
- Check if WordPress backend is accessible
- Test API endpoints manually if needed

### Logcat Debugging
1. Open **View → Tool Windows → Logcat**
2. Filter by package name: `com.rnzapp`
3. Look for error messages in red
4. Check network requests and responses

## 📱 Device Requirements

### Minimum Requirements
- **Android Version**: 7.0 (API Level 24)
- **RAM**: 2GB
- **Storage**: 100MB free space
- **Permissions**: Camera, Storage, Internet

### Recommended Requirements
- **Android Version**: 10.0+ (API Level 29+)
- **RAM**: 4GB+
- **Storage**: 500MB free space

## 🔧 Development Tips

### Code Structure
- **Activities**: Main app screens
- **API**: WordPress REST API integration
- **Models**: Data classes for API responses
- **Utils**: Helper classes and managers

### Key Files to Understand
1. `LoginActivity.kt` - Authentication flow
2. `WordPressApi.kt` - API endpoints
3. `TokenManager.kt` - JWT token handling
4. `RetrofitInstance.kt` - Network configuration

### Making Changes
1. Modify code as needed
2. **Build → Clean Project**
3. **Build → Rebuild Project**
4. Test changes on device/emulator

## 📞 Support

### If You Need Help
1. Check the **README.md** for detailed documentation
2. Review error messages in **Logcat**
3. Verify all setup steps were completed
4. Check WordPress backend connectivity

### Common Commands
```bash
# Clean project
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## ✅ Success Checklist

- [ ] Android Studio installed and updated
- [ ] Project opens without errors
- [ ] Gradle sync completes successfully
- [ ] App builds without errors
- [ ] App installs on device/emulator
- [ ] Login screen appears
- [ ] Can login with provided credentials
- [ ] All dashboard features accessible
- [ ] Network requests work (check Logcat)

---

**🎉 Congratulations! Your RNZ App is now ready for development and testing.**

For detailed feature documentation, refer to the main **README.md** file.
