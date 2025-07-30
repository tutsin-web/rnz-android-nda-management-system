package com.rnzapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.rnzapp.model.Permission
import com.rnzapp.model.UserRole

class TokenManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "rnz_app_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_DISPLAY_NAME = "user_display_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLES = "user_roles"
        private const val KEY_USER_CAPABILITIES = "user_capabilities"
    }
    
    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    
    fun saveUserInfo(email: String, name: String, displayName: String, userId: String? = null) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_DISPLAY_NAME, displayName)
            .putString(KEY_USER_ID, userId)
            .apply()
    }
    
    fun saveUserRoles(roles: List<String>) {
        val rolesString = roles.joinToString(",")
        sharedPreferences.edit()
            .putString(KEY_USER_ROLES, rolesString)
            .apply()
    }
    
    fun saveUserCapabilities(capabilities: Map<String, Boolean>) {
        val capabilitiesString = capabilities.entries.joinToString(",") { "${it.key}:${it.value}" }
        sharedPreferences.edit()
            .putString(KEY_USER_CAPABILITIES, capabilitiesString)
            .apply()
    }
    
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
    
    fun getUserDisplayName(): String? {
        return sharedPreferences.getString(KEY_USER_DISPLAY_NAME, null)
    }
    
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    fun getUserRoles(): List<UserRole> {
        val rolesString = sharedPreferences.getString(KEY_USER_ROLES, "") ?: ""
        return if (rolesString.isNotEmpty()) {
            rolesString.split(",").mapNotNull { UserRole.fromString(it.trim()) }
        } else {
            emptyList()
        }
    }
    
    fun getUserCapabilities(): Map<String, Boolean> {
        val capabilitiesString = sharedPreferences.getString(KEY_USER_CAPABILITIES, "") ?: ""
        return if (capabilitiesString.isNotEmpty()) {
            capabilitiesString.split(",").associate { capability ->
                val parts = capability.split(":")
                if (parts.size == 2) {
                    parts[0].trim() to parts[1].trim().toBoolean()
                } else {
                    parts[0].trim() to false
                }
            }
        } else {
            emptyMap()
        }
    }
    
    fun hasRole(role: UserRole): Boolean {
        return getUserRoles().contains(role)
    }
    
    fun hasPermission(permission: Permission): Boolean {
        val capabilities = getUserCapabilities()
        return capabilities[permission.capability] == true || isAdmin()
    }
    
    fun isAdmin(): Boolean {
        return hasRole(UserRole.ADMINISTRATOR)
    }
    
    fun isReceptionist(): Boolean {
        return hasRole(UserRole.RECEPTIONIST)
    }
    
    fun canViewAnalytics(): Boolean {
        return hasPermission(Permission.VIEW_ANALYTICS) || isAdmin()
    }
    
    fun canManageNDAs(): Boolean {
        return hasPermission(Permission.MANAGE_NDAS) || isAdmin()
    }
    
    fun canSubmitNDAs(): Boolean {
        return hasPermission(Permission.SUBMIT_NDAS) || isAdmin() || isReceptionist()
    }
    
    fun canViewPDFs(): Boolean {
        return hasPermission(Permission.VIEW_PDFS) || isAdmin()
    }
    
    fun canGenerateQR(): Boolean {
        return hasPermission(Permission.GENERATE_QR) || isAdmin()
    }
    
    fun canScanQR(): Boolean {
        return hasPermission(Permission.SCAN_QR) || isAdmin() || isReceptionist()
    }
    
    fun canExportData(): Boolean {
        return hasPermission(Permission.EXPORT_DATA) || isAdmin()
    }
    
    fun getPrimaryRole(): UserRole? {
        val roles = getUserRoles()
        return when {
            roles.contains(UserRole.ADMINISTRATOR) -> UserRole.ADMINISTRATOR
            roles.contains(UserRole.NDA_MANAGER) -> UserRole.NDA_MANAGER
            roles.contains(UserRole.RECEPTIONIST) -> UserRole.RECEPTIONIST
            roles.contains(UserRole.VISITOR_COORDINATOR) -> UserRole.VISITOR_COORDINATOR
            roles.contains(UserRole.EDITOR) -> UserRole.EDITOR
            roles.contains(UserRole.AUTHOR) -> UserRole.AUTHOR
            roles.contains(UserRole.CONTRIBUTOR) -> UserRole.CONTRIBUTOR
            roles.contains(UserRole.SUBSCRIBER) -> UserRole.SUBSCRIBER
            else -> roles.firstOrNull()
        }
    }
    
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    fun getAuthHeader(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }
}
