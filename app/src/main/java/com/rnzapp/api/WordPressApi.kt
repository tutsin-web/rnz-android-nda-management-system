package com.rnzapp.api

import com.rnzapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WordPressApi {
    
    @POST("jwt-auth/v1/token")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>
    
    @POST("jwt-auth/v1/token/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<ResponseBody>
    
    @Multipart
    @POST("nda/v1/submit")
    suspend fun submitNDA(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part("country") country: RequestBody,
        @Part("id_type") idType: RequestBody,
        @Part("id_number") idNumber: RequestBody,
        @Part("visit_date") visitDate: RequestBody,
        @Part("signed_date") signedDate: RequestBody,
        @Part("agreed_to_terms") agreedToTerms: RequestBody,
        @Part file: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Response<NDASubmissionResponse>
    
    @GET("nda/v1/analytics")
    suspend fun getAnalytics(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Header("Authorization") token: String
    ): Response<AnalyticsData>
    
    @GET("nda/v1/submissions")
    suspend fun getSubmissions(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Header("Authorization") token: String
    ): Response<List<Visitor>>
    
    @GET("nda/v1/pdf/{submission_id}")
    suspend fun getPDFUrl(
        @Path("submission_id") submissionId: String,
        @Header("Authorization") token: String
    ): Response<PDFResponse>
    
    @POST("nda/v1/qr-generate")
    suspend fun generateQRCode(
        @Body request: QRGenerateRequest,
        @Header("Authorization") token: String
    ): Response<QRResponse>
}

data class PDFResponse(
    val pdf_url: String,
    val filename: String
)

data class QRGenerateRequest(
    val form_data: Map<String, String>? = null,
    val expiry_hours: Int = 24
)

data class QRResponse(
    val qr_code_url: String,
    val form_url: String,
    val expires_at: String
)
