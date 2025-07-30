package com.rnzapp.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rnzapp.R
import com.rnzapp.api.WordPressApi
import com.rnzapp.databinding.ActivityNdaFormBinding
import com.rnzapp.model.Visitor
import com.rnzapp.utils.RetrofitInstance
import com.rnzapp.utils.TokenManager
import com.rnzapp.utils.Validators
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class NDAFormActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNdaFormBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var api: WordPressApi
    
    private var selectedFileUri: Uri? = null
    private var selectedFileName: String? = null
    
    private val countries = arrayOf(
        "Select Country",
        "United Arab Emirates",
        "Saudi Arabia",
        "Qatar",
        "Kuwait",
        "Bahrain",
        "Oman",
        "Other"
    )
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = getFileName(it)
            binding.tvFileName.text = selectedFileName ?: "File selected"
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNdaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeComponents()
        setupToolbar()
        setupCountrySpinner()
        setupDatePickers()
        setupClickListeners()
        handleIntentData()
    }
    
    private fun initializeComponents() {
        tokenManager = TokenManager(this)
        RetrofitInstance.initialize(tokenManager)
        api = RetrofitInstance.retrofit.create(WordPressApi::class.java)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupCountrySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries)
        binding.spinnerCountry.setAdapter(adapter)
        
        binding.spinnerCountry.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = countries[position]
            updateIdFields(selectedCountry)
        }
    }
    
    private fun updateIdFields(country: String) {
        if (country == "United Arab Emirates") {
            binding.tilEmiratesId.visibility = View.VISIBLE
            binding.tilPassport.visibility = View.GONE
        } else {
            binding.tilEmiratesId.visibility = View.GONE
            binding.tilPassport.visibility = View.VISIBLE
        }
    }
    
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        binding.etVisitDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etVisitDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        binding.etSignedDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etSignedDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnChooseFile.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }
        
        binding.btnSubmit.setOnClickListener {
            submitForm()
        }
    }
    
    private fun handleIntentData() {
        // Handle QR code prefill data if available
        intent.extras?.let { extras ->
            extras.getString("name")?.let { binding.etName.setText(it) }
            extras.getString("email")?.let { binding.etEmail.setText(it) }
            extras.getString("phone")?.let { binding.etPhone.setText(it) }
            extras.getString("country")?.let { 
                binding.spinnerCountry.setText(it, false)
                updateIdFields(it)
            }
        }
    }
    
    private fun submitForm() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val country = binding.spinnerCountry.text.toString().trim()
        val visitDate = binding.etVisitDate.text.toString().trim()
        val signedDate = binding.etSignedDate.text.toString().trim()
        val agreedToTerms = binding.cbAgreeTerms.isChecked
        
        val (idType, idNumber) = if (binding.tilEmiratesId.visibility == View.VISIBLE) {
            "emirates_id" to binding.etEmiratesId.text.toString().trim()
        } else {
            "passport" to binding.etPassport.text.toString().trim()
        }
        
        val validationResult = Validators.validateVisitorForm(
            name, email, phone, address, country, idType, idNumber, visitDate, signedDate, agreedToTerms
        )
        
        if (!validationResult.isValid) {
            showError(validationResult.errorMessage ?: "Please check your input")
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader == null) {
                    showError("Authentication required. Please login again.")
                    return@launch
                }
                
                // Prepare multipart request
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
                val countryBody = country.toRequestBody("text/plain".toMediaTypeOrNull())
                val idTypeBody = idType.toRequestBody("text/plain".toMediaTypeOrNull())
                val idNumberBody = idNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val visitDateBody = visitDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val signedDateBody = signedDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val agreedBody = agreedToTerms.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val filePart = selectedFileUri?.let { uri ->
                    val file = createTempFileFromUri(uri)
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", selectedFileName ?: "document", requestFile)
                }
                
                val response = api.submitNDA(
                    nameBody, emailBody, phoneBody, addressBody, countryBody,
                    idTypeBody, idNumberBody, visitDateBody, signedDateBody,
                    agreedBody, filePart, authHeader
                )
                
                showLoading(false)
                
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.success) {
                        showSuccess(getString(R.string.form_submit_success))
                        finish()
                    } else {
                        showError(result.message)
                    }
                } else {
                    showError(getString(R.string.form_submit_error))
                }
                
            } catch (e: Exception) {
                showLoading(false)
                showError(getString(R.string.network_error))
            }
        }
    }
    
    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".tmp", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }
    
    private fun getFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.success))
            .show()
    }
}
