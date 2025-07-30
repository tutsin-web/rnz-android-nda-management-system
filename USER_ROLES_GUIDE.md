# RNZ App - User Roles & Authentication Guide

## Overview

The RNZ App supports **multiple user roles** with role-based access control (RBAC). The app works with any valid WordPress user credentials, not just admin login. Different user roles have different permissions and access levels.

## ✅ **Answer to Your Question**

**The app works with BOTH admin and receptionist logins (and other roles too)!**

- ✅ **Admin users**: Full access to all features
- ✅ **Receptionist users**: Can submit NDAs, scan QR codes, limited access
- ✅ **Other roles**: Access based on their specific permissions

## 🔐 Supported User Roles

### 1. **Administrator**
- **Full access** to all app features
- Can view analytics, manage NDAs, export data
- Access to all dashboard cards

### 2. **Receptionist** 
- **Primary role for front desk staff**
- Can submit visitor NDA forms
- Can scan QR codes for quick form access
- Cannot view analytics or export data
- Perfect for reception desk usage

### 3. **NDA Manager**
- Specialized role for NDA management
- Can manage all NDA-related activities
- Can view PDFs and generate QR codes
- Limited analytics access

### 4. **Visitor Coordinator**
- Can coordinate visitor activities
- Can submit NDAs and scan QR codes
- Limited administrative access

### 5. **Editor/Author/Contributor**
- Standard WordPress roles
- Basic access based on capabilities
- Can submit NDAs if granted permission

### 6. **Subscriber**
- Basic user role
- Limited access to features
- Typically read-only access

## 🎯 Permission System

### Feature Permissions

| Feature | Admin | Receptionist | NDA Manager | Visitor Coord. | Others |
|---------|-------|--------------|-------------|----------------|--------|
| **NDA Form** | ✅ | ✅ | ✅ | ✅ | Based on caps |
| **QR Scanner** | ✅ | ✅ | ✅ | ✅ | Based on caps |
| **PDF Viewer** | ✅ | ❌ | ✅ | ❌ | Based on caps |
| **Analytics** | ✅ | ❌ | Limited | ❌ | Based on caps |
| **Export Data** | ✅ | ❌ | ❌ | ❌ | Based on caps |

### Capability-Based Access

The app checks for specific WordPress capabilities:

- `submit_ndas` - Can submit NDA forms
- `view_analytics` - Can view analytics dashboard
- `manage_ndas` - Can manage NDA submissions
- `view_pdfs` - Can view PDF documents
- `generate_qr` - Can generate QR codes
- `scan_qr` - Can scan QR codes
- `export_data` - Can export analytics data

## 🔑 How Authentication Works

### 1. **Login Process**
```kotlin
// User enters any valid WordPress credentials
username: "receptionist_user"
password: "their_password"

// App sends to WordPress JWT endpoint
POST https://rnz-group.com/wp-json/jwt-auth/v1/token

// WordPress returns user info with roles
{
  "token": "jwt_token_here",
  "user_email": "receptionist@rnz-group.com",
  "user_display_name": "Reception Staff",
  "user_roles": ["receptionist"],
  "user_capabilities": {
    "submit_ndas": true,
    "scan_qr": true,
    "view_analytics": false
  }
}
```

### 2. **Role-Based Dashboard**
- Dashboard cards show/hide based on permissions
- Welcome message adapts to user role
- Access denied messages for restricted features

### 3. **Dynamic UI Updates**
```kotlin
// Example: Hide analytics card for receptionists
binding.cardAnalytics.visibility = if (tokenManager.canViewAnalytics()) {
    View.VISIBLE
} else {
    View.GONE
}
```

## 👥 Setting Up User Roles in WordPress

### For Administrators:

1. **Create Receptionist User**:
   ```
   Username: receptionist
   Password: [secure_password]
   Role: Receptionist (or custom role)
   ```

2. **Assign Capabilities**:
   - `submit_ndas`: true
   - `scan_qr`: true
   - `view_pdfs`: false
   - `view_analytics`: false

3. **Test Login**:
   - Use receptionist credentials in the app
   - Verify appropriate features are available

### Custom Role Creation (WordPress):
```php
// Add to functions.php or plugin
add_role('receptionist', 'Receptionist', array(
    'read' => true,
    'submit_ndas' => true,
    'scan_qr' => true,
    'view_pdfs' => false,
    'view_analytics' => false
));
```

## 📱 User Experience by Role

### **Admin Experience**
- Sees all 4 dashboard cards
- Welcome: "Welcome, Administrator! You have full access to all features."
- Can access every feature without restrictions

### **Receptionist Experience**
- Sees NDA Form and QR Scanner cards only
- Welcome: "Welcome, Receptionist! You can manage visitor NDAs and scan QR codes."
- Perfect for front desk operations

### **Limited User Experience**
- Sees only permitted features
- Welcome: "Welcome! Access features based on your permissions."
- Clean, focused interface

## 🔧 Testing Different Roles

### Test Credentials

1. **Admin Test**:
   ```
   Username: admin
   Password: 4pAgL#MgH&wKt
   Expected: Full access to all features
   ```

2. **Receptionist Test** (if created):
   ```
   Username: receptionist
   Password: [your_receptionist_password]
   Expected: NDA Form + QR Scanner only
   ```

3. **Custom Role Test**:
   ```
   Username: [custom_user]
   Password: [their_password]
   Expected: Features based on their capabilities
   ```

## 🛡️ Security Features

### 1. **Token-Based Authentication**
- JWT tokens for secure API communication
- Automatic token validation
- Secure token storage in encrypted SharedPreferences

### 2. **Permission Validation**
- Server-side permission checks
- Client-side UI adaptation
- Graceful access denial handling

### 3. **Role Hierarchy**
- Admins always have full access
- Role-specific permission inheritance
- Fallback to capability-based access

## 🚀 Implementation Benefits

### For Organizations:
- **Scalable**: Support unlimited user roles
- **Secure**: Proper permission management
- **Flexible**: Easy to add new roles/permissions
- **User-Friendly**: Clean, role-appropriate interfaces

### For Receptionists:
- **Simple Interface**: Only see relevant features
- **Quick Access**: Fast NDA submission and QR scanning
- **No Confusion**: No access to admin-only features
- **Mobile Optimized**: Perfect for front desk tablets/phones

## 📞 Support & Troubleshooting

### Common Issues:

1. **"Access Denied" Messages**:
   - Check user role in WordPress admin
   - Verify capabilities are assigned correctly
   - Ensure JWT plugin is working

2. **Missing Dashboard Cards**:
   - Normal behavior for limited roles
   - Check user permissions in WordPress
   - Verify role assignment

3. **Login Issues**:
   - Any valid WordPress user can login
   - Check username/password in WordPress
   - Verify JWT plugin configuration

### Getting Help:
- Check WordPress user roles and capabilities
- Review JWT plugin settings
- Test with admin credentials first
- Contact system administrator for role assignments

---

## 🎉 Summary

**The RNZ App is designed to work with ALL user types:**

✅ **Administrators** - Full access  
✅ **Receptionists** - NDA management focus  
✅ **Custom roles** - Based on permissions  
✅ **Standard WordPress users** - Capability-based access  

The app automatically adapts its interface and functionality based on the logged-in user's role and permissions, providing a secure, user-appropriate experience for everyone from front desk staff to system administrators.
