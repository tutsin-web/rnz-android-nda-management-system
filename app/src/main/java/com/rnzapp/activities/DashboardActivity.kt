package com.rnzapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.rnzapp.R
import com.rnzapp.databinding.ActivityDashboardBinding
import com.rnzapp.utils.TokenManager

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var tokenManager: TokenManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeComponents()
        setupToolbar()
        setupUserInfo()
        setupRoleBasedAccess()
        setupClickListeners()
    }
    
    private fun initializeComponents() {
        tokenManager = TokenManager(this)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    private fun setupUserInfo() {
        val displayName = tokenManager.getUserDisplayName() ?: "User"
        val primaryRole = tokenManager.getPrimaryRole()
        
        binding.tvUserName.text = "$displayName (${primaryRole?.displayName ?: "User"})"
    }
    
    private fun setupRoleBasedAccess() {
        // Show/hide features based on user permissions
        
        // NDA Form - Available to all users who can submit NDAs
        binding.cardNDAForm.visibility = if (tokenManager.canSubmitNDAs()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // QR Scanner - Available to users who can scan QR codes
        binding.cardQRScan.visibility = if (tokenManager.canScanQR()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // PDF Viewer - Available to users who can view PDFs
        binding.cardPDFViewer.visibility = if (tokenManager.canViewPDFs()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // Analytics - Available to users who can view analytics
        binding.cardAnalytics.visibility = if (tokenManager.canViewAnalytics()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // Show role-specific welcome message
        showRoleBasedWelcomeMessage()
    }
    
    private fun showRoleBasedWelcomeMessage() {
        val welcomeMessage = when {
            tokenManager.isAdmin() -> "Welcome, Administrator! You have full access to all features."
            tokenManager.isReceptionist() -> "Welcome, Receptionist! You can manage visitor NDAs and scan QR codes."
            tokenManager.hasRole(com.rnzapp.model.UserRole.NDA_MANAGER) -> "Welcome, NDA Manager! You can manage all NDA-related activities."
            tokenManager.hasRole(com.rnzapp.model.UserRole.VISITOR_COORDINATOR) -> "Welcome, Visitor Coordinator! You can coordinate visitor activities."
            else -> "Welcome! Access features based on your permissions."
        }
        
        Snackbar.make(binding.root, welcomeMessage, Snackbar.LENGTH_LONG).show()
    }
    
    private fun setupClickListeners() {
        binding.cardNDAForm.setOnClickListener {
            if (tokenManager.canSubmitNDAs()) {
                startActivity(Intent(this, NDAFormActivity::class.java))
            } else {
                showAccessDeniedMessage("NDA Form")
            }
        }
        
        binding.cardQRScan.setOnClickListener {
            if (tokenManager.canScanQR()) {
                startActivity(Intent(this, QRScanActivity::class.java))
            } else {
                showAccessDeniedMessage("QR Scanner")
            }
        }
        
        binding.cardPDFViewer.setOnClickListener {
            if (tokenManager.canViewPDFs()) {
                startActivity(Intent(this, PDFViewerActivity::class.java))
            } else {
                showAccessDeniedMessage("PDF Viewer")
            }
        }
        
        binding.cardAnalytics.setOnClickListener {
            if (tokenManager.canViewAnalytics()) {
                startActivity(Intent(this, AnalyticsActivity::class.java))
            } else {
                showAccessDeniedMessage("Analytics")
            }
        }
    }
    
    private fun showAccessDeniedMessage(feature: String) {
        Snackbar.make(
            binding.root, 
            "Access denied: You don't have permission to access $feature", 
            Snackbar.LENGTH_LONG
        ).show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        tokenManager.clearAll()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
