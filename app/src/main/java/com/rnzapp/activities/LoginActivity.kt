package com.rnzapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rnzapp.R
import com.rnzapp.api.WordPressApi
import com.rnzapp.databinding.ActivityLoginBinding
import com.rnzapp.model.LoginRequest
import com.rnzapp.utils.RetrofitInstance
import com.rnzapp.utils.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var api: WordPressApi
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeComponents()
        checkIfAlreadyLoggedIn()
        setupClickListeners()
    }
    
    private fun initializeComponents() {
        tokenManager = TokenManager(this)
        RetrofitInstance.initialize(tokenManager)
        api = RetrofitInstance.retrofit.create(WordPressApi::class.java)
    }
    
    private fun checkIfAlreadyLoggedIn() {
        if (tokenManager.isLoggedIn()) {
            navigateToDashboard()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }
    
    private fun performLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (validateInput(username, password)) {
            showLoading(true)
            
            lifecycleScope.launch {
                try {
                    val loginRequest = LoginRequest(username, password)
                    val response = api.login(loginRequest)
                    
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        
                        // Save token and user info
                        tokenManager.saveToken(loginResponse.token)
                        tokenManager.saveUserInfo(
                            loginResponse.userEmail,
                            loginResponse.userNicename,
                            loginResponse.userDisplayName,
                            loginResponse.userId
                        )
                        
                        // Save user roles and capabilities
                        tokenManager.saveUserRoles(loginResponse.userRoles)
                        tokenManager.saveUserCapabilities(loginResponse.userCapabilities)
                        
                        showLoading(false)
                        navigateToDashboard()
                        
                    } else {
                        showLoading(false)
                        showError(getString(R.string.login_error))
                    }
                    
                } catch (e: Exception) {
                    showLoading(false)
                    showError(getString(R.string.network_error))
                }
            }
        }
    }
    
    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true
        
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        return isValid
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.etUsername.isEnabled = !show
        binding.etPassword.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
