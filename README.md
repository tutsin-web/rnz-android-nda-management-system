# RNZ App - Android NDA Management System

A complete Android application built in Kotlin that connects to the RNZ WordPress backend for managing visitor NDA submissions and admin controls.

## Features

### 🔐 Authentication
- JWT-based login system
- Secure token storage
- Auto-login functionality
- Session management

### 📝 NDA Form Management
- Dynamic visitor form with conditional fields
- Emirates ID field for UAE visitors
- Passport field for other countries
- File upload support
- Form validation
- Date pickers for visit and signed dates

### 📱 QR Code Integration
- QR code scanning using ML Kit
- Form prefill from QR data
- URL handling for web links
- Camera permission management

### 📄 PDF Viewing
- Remote PDF loading via WebView
- Google Docs viewer integration
- Download and share functionality
- External app integration

### 📊 Analytics Dashboard
- Real-time analytics from WordPress backend
- Interactive charts (Line and Pie charts)
- Date filtering
- Country-wise statistics
- Export functionality

## Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM pattern
- **Networking**: Retrofit + OkHttp
- **Authentication**: JWT tokens
- **UI**: Material Design 3
- **Charts**: MPAndroidChart
- **QR Scanning**: Google ML Kit
- **Camera**: CameraX
- **File Handling**: Storage Access Framework

## Project Structure

```
app/src/main/
├── java/com/rnzapp/
│   ├── activities/          # All activity classes
│   │   ├── LoginActivity.kt
│   │   ├── DashboardActivity.kt
│   │   ├── NDAFormActivity.kt
│   │   ├── QRScanActivity.kt
│   │   ├── PDFViewerActivity.kt
│   │   └── AnalyticsActivity.kt
│   ├── api/                 # API interfaces
│   │   └── WordPressApi.kt
│   ├── model/               # Data models
│   │   ├── LoginResponse.kt
│   │   ├── Visitor.kt
│   │   └── AnalyticsData.kt
│   └── utils/               # Utility classes
│       ├── RetrofitInstance.kt
│       ├── TokenManager.kt
│       └── Validators.kt
└── res/
    ├── layout/              # XML layouts
    ├── values/              # Colors, strings, themes
    ├── drawable/            # Vector drawables and shapes
    └── menu/                # Menu resources
```

## API Integration

### WordPress Backend
- **Base URL**: `https://rnz-group.com/wp-json/`
- **Authentication**: JWT tokens via `jwt-auth/v1/token`
- **NDA Submission**: `nda/v1/submit`
- **Analytics**: `nda/v1/analytics`
- **PDF URLs**: `nda/v1/pdf/{submission_id}`

### Authentication Flow
1. User enters credentials
2. App sends POST request to JWT endpoint
3. Server returns JWT token and user info
4. Token stored securely in SharedPreferences
5. Token attached to all subsequent API calls

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Update API credentials if needed
5. Build and run on device/emulator

### Configuration
The app is pre-configured to connect to the RNZ WordPress backend. No additional setup required for basic functionality.

## Key Features Implementation

### Dynamic Form Fields
```kotlin
private fun updateIdFields(country: String) {
    if (country == "United Arab Emirates") {
        binding.tilEmiratesId.visibility = View.VISIBLE
        binding.tilPassport.visibility = View.GONE
    } else {
        binding.tilEmiratesId.visibility = View.GONE
        binding.tilPassport.visibility = View.VISIBLE
    }
}
```

### JWT Token Management
```kotlin
class TokenManager(context: Context) {
    fun saveToken(token: String)
    fun getToken(): String?
    fun getAuthHeader(): String?
    fun clearAll()
    fun isLoggedIn(): Boolean
}
```

### QR Code Processing
```kotlin
private fun handleQRCodeResult(result: String) {
    if (result.startsWith("http")) {
        // Handle URL with form prefill
        val uri = Uri.parse(result)
        val intent = Intent(this, NDAFormActivity::class.java)
        // Extract and pass parameters
    }
}
```

## Security Features

- Secure JWT token storage
- Input validation on all forms
- Network security with HTTPS
- Camera permission handling
- File access permissions

## UI/UX Design

- **Theme**: RNZ agriculture/green branding
- **Design System**: Material Design 3
- **Colors**: Green primary (#4CAF50) with complementary accents
- **Typography**: Clean, readable fonts
- **Layout**: Mobile-first responsive design
- **Navigation**: Intuitive card-based dashboard

## Testing

### Manual Testing Checklist
- [ ] Login with valid credentials
- [ ] Form submission with all field types
- [ ] QR code scanning functionality
- [ ] PDF viewing and download
- [ ] Analytics charts display
- [ ] Network error handling
- [ ] Permission requests

### Test Credentials
- Username: `admin`
- Password: `4pAgL#MgH&wKt`

## Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Future Enhancements

- [ ] Offline data caching
- [ ] Push notifications
- [ ] Biometric authentication
- [ ] Multi-language support
- [ ] Dark theme support
- [ ] Advanced analytics filters
- [ ] Bulk operations

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## License

© 2025 RNZ Group. All rights reserved.

## Support

For technical support or questions:
- Email: developer@rnz-group.com
- Documentation: [Internal Wiki]
- Issue Tracker: [GitHub Issues]

---

**Built with ❤️ for RNZ Group's digital transformation initiative.**
