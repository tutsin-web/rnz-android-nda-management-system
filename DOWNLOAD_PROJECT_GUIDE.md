# How to Download RNZ Android Project from Sandbox

## 📦 **Method 1: Download via VSCode Interface (Recommended)**

### Step 1: Access File Explorer
1. In the VSCode interface, look for the **Explorer panel** on the left side
2. You should see all project files listed in the file tree

### Step 2: Download Individual Files
1. **Right-click** on any file in the Explorer
2. Select **"Download"** from the context menu
3. The file will download to your computer's Downloads folder

### Step 3: Download Folders
1. **Right-click** on the `app` folder
2. Select **"Download"** - this will download the entire app folder as a ZIP
3. Repeat for any other folders you need

## 📦 **Method 2: Download All Files Systematically**

### Essential Files to Download:

#### **Root Level Files:**
- `build.gradle` - App-level build configuration
- `build.gradle.kts` - Project-level build configuration  
- `settings.gradle` - Gradle settings
- `gradle.properties` - Gradle properties
- `README.md` - Main documentation
- `SETUP_INSTRUCTIONS.md` - Setup guide
- `USER_ROLES_GUIDE.md` - Role authentication guide
- `EMULATOR_SETUP_GUIDE.md` - Emulator troubleshooting
- `GITHUB_REPUBLISH_GUIDE.md` - GitHub republish guide
- `.gitignore` - Git ignore file

#### **App Folder:**
- Right-click on `app` folder → Download
- This contains all Android source code, layouts, and resources

## 📦 **Method 3: Create Project Structure Locally**

### Step 1: Create Root Folder
1. Create a new folder on your computer: `RNZApp`
2. Download all root-level files into this folder

### Step 2: Recreate Folder Structure
Create these folders in your `RNZApp` directory:
```
RNZApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── rnzapp/
│           │           ├── activities/
│           │           ├── api/
│           │           ├── model/
│           │           └── utils/
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── menu/
│           │   ├── values/
│           │   └── xml/
│           └── AndroidManifest.xml
```

### Step 3: Download Files to Correct Locations
Download each file and place it in the corresponding folder structure.

## 📦 **Method 4: Terminal/Command Line Download**

### If Terminal is Available:
1. Open terminal in VSCode (Terminal → New Terminal)
2. Create a ZIP file:
```bash
# Create ZIP of entire project
zip -r RNZApp.zip . -x "*.git*" "node_modules/*" "build/*"

# Or create tar.gz
tar -czf RNZApp.tar.gz --exclude='.git' --exclude='node_modules' --exclude='build' .
```
3. Download the created ZIP file

## 📦 **Method 5: Copy-Paste Method**

### For Text Files:
1. **Open each file** in VSCode
2. **Select All** (Ctrl+A / Cmd+A)
3. **Copy** (Ctrl+C / Cmd+C)
4. **Create new file** on your local computer
5. **Paste content** and save with correct filename

### File List to Copy:
```
Root Files:
- build.gradle
- build.gradle.kts
- settings.gradle
- gradle.properties
- README.md
- SETUP_INSTRUCTIONS.md
- USER_ROLES_GUIDE.md
- EMULATOR_SETUP_GUIDE.md
- GITHUB_REPUBLISH_GUIDE.md
- .gitignore

Activities:
- app/src/main/java/com/rnzapp/activities/LoginActivity.kt
- app/src/main/java/com/rnzapp/activities/DashboardActivity.kt
- app/src/main/java/com/rnzapp/activities/NDAFormActivity.kt
- app/src/main/java/com/rnzapp/activities/QRScanActivity.kt
- app/src/main/java/com/rnzapp/activities/PDFViewerActivity.kt
- app/src/main/java/com/rnzapp/activities/AnalyticsActivity.kt

Models:
- app/src/main/java/com/rnzapp/model/LoginResponse.kt
- app/src/main/java/com/rnzapp/model/Visitor.kt
- app/src/main/java/com/rnzapp/model/AnalyticsData.kt

Utils:
- app/src/main/java/com/rnzapp/utils/TokenManager.kt
- app/src/main/java/com/rnzapp/utils/Validators.kt
- app/src/main/java/com/rnzapp/utils/RetrofitInstance.kt

API:
- app/src/main/java/com/rnzapp/api/WordPressApi.kt

Layouts:
- app/src/main/res/layout/activity_login.xml
- app/src/main/res/layout/activity_dashboard.xml
- app/src/main/res/layout/activity_nda_form.xml
- app/src/main/res/layout/activity_qr_scan.xml
- app/src/main/res/layout/activity_pdf_viewer.xml
- app/src/main/res/layout/activity_analytics.xml

Resources:
- app/src/main/res/values/strings.xml
- app/src/main/res/values/colors.xml
- app/src/main/res/values/themes.xml
- app/src/main/AndroidManifest.xml

And all other drawable, menu, and XML files...
```

## 🎯 **Quick Download Checklist**

### Essential Files (Minimum Required):
- [ ] `build.gradle` (app-level)
- [ ] `build.gradle.kts` (project-level)
- [ ] `settings.gradle`
- [ ] `gradle.properties`
- [ ] Entire `app/` folder
- [ ] `README.md`
- [ ] `.gitignore`

### Documentation Files:
- [ ] `SETUP_INSTRUCTIONS.md`
- [ ] `USER_ROLES_GUIDE.md`
- [ ] `EMULATOR_SETUP_GUIDE.md`
- [ ] `GITHUB_REPUBLISH_GUIDE.md`

## 🚀 **After Download**

### Verify Your Download:
1. **Check folder structure** matches Android project layout
2. **Ensure all .kt files** are present
3. **Verify all .xml files** are downloaded
4. **Check documentation files** are included

### Next Steps:
1. **Open in Android Studio**: File → Open → Select your RNZApp folder
2. **Sync Project**: File → Sync Project with Gradle Files
3. **Build Project**: Build → Clean Project, then Build → Rebuild Project
4. **Run App**: Connect device/emulator and click Run

## 💡 **Pro Tips**

### For Faster Download:
- Download the `app` folder first (contains most important code)
- Download documentation files separately
- Use right-click → Download on folders when possible

### File Organization:
- Keep the exact folder structure as shown
- Don't rename files or folders
- Maintain the `com.rnzapp` package structure

### Troubleshooting:
- If download fails, try downloading smaller chunks
- Ensure stable internet connection
- Try different browser if issues persist

---

**🎉 Once downloaded, you'll have the complete RNZ Android project ready to open in Android Studio!**
