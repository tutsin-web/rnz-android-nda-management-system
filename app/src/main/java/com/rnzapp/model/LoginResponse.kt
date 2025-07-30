package com.rnzapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("user_email")
    val userEmail: String,
    
    @SerializedName("user_nicename")
    val userNicename: String,
    
    @SerializedName("user_display_name")
    val userDisplayName: String,
    
    @SerializedName("user_roles")
    val userRoles: List<String> = emptyList(),
    
    @SerializedName("user_capabilities")
    val userCapabilities: Map<String, Boolean> = emptyMap(),
    
    @SerializedName("user_id")
    val userId: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

// User roles enum for better type safety
enum class UserRole(val roleName: String, val displayName: String) {
    ADMINISTRATOR("administrator", "Administrator"),
    EDITOR("editor", "Editor"),
    AUTHOR("author", "Author"),
    CONTRIBUTOR("contributor", "Contributor"),
    SUBSCRIBER("subscriber", "Subscriber"),
    RECEPTIONIST("receptionist", "Receptionist"),
    NDA_MANAGER("nda_manager", "NDA Manager"),
    VISITOR_COORDINATOR("visitor_coordinator", "Visitor Coordinator");
    
    companion object {
        fun fromString(role: String): UserRole? {
            return values().find { it.roleName.equals(role, ignoreCase = true) }
        }
    }
}

// Permissions for different features
enum class Permission(val capability: String) {
    VIEW_ANALYTICS("view_analytics"),
    MANAGE_NDAS("manage_ndas"),
    SUBMIT_NDAS("submit_ndas"),
    VIEW_PDFS("view_pdfs"),
    GENERATE_QR("generate_qr"),
    SCAN_QR("scan_qr"),
    EXPORT_DATA("export_data"),
    MANAGE_USERS("manage_users");
}
