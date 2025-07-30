package com.rnzapp.model

import com.google.gson.annotations.SerializedName

data class Visitor(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("id_type")
    val idType: String, // "emirates_id" or "passport"
    
    @SerializedName("id_number")
    val idNumber: String,
    
    @SerializedName("visit_date")
    val visitDate: String,
    
    @SerializedName("signed_date")
    val signedDate: String,
    
    @SerializedName("file_path")
    val filePath: String? = null,
    
    @SerializedName("agreed_to_terms")
    val agreedToTerms: Boolean
)

data class NDASubmissionResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("submission_id")
    val submissionId: String? = null
)
