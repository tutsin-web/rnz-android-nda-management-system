package com.rnzapp.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rnzapp.R
import com.rnzapp.api.WordPressApi
import com.rnzapp.databinding.ActivityPdfViewerBinding
import com.rnzapp.utils.RetrofitInstance
import com.rnzapp.utils.TokenManager
import kotlinx.coroutines.launch

class PDFViewerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPdfViewerBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var api: WordPressApi
    
    private var currentPdfUrl: String? = null
    private var currentFileName: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeComponents()
        setupToolbar()
        setupWebView()
        setupClickListeners()
        handleIntent()
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
    
    private fun setupWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }
        
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoading()
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoading()
                showActions()
            }
            
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showError("Failed to load PDF: $description")
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnReload.setOnClickListener {
            currentPdfUrl?.let { loadPdf(it) }
        }
        
        binding.btnSelectPdf.setOnClickListener {
            // For demo purposes, show a sample PDF
            loadSamplePdf()
        }
        
        binding.btnDownload.setOnClickListener {
            currentPdfUrl?.let { downloadPdf(it) }
        }
        
        binding.btnShare.setOnClickListener {
            currentPdfUrl?.let { sharePdf(it) }
        }
    }
    
    private fun handleIntent() {
        val submissionId = intent.getStringExtra("submission_id")
        val pdfUrl = intent.getStringExtra("pdf_url")
        
        when {
            submissionId != null -> loadPdfBySubmissionId(submissionId)
            pdfUrl != null -> loadPdf(pdfUrl)
            else -> showNoPdfSelected()
        }
    }
    
    private fun loadPdfBySubmissionId(submissionId: String) {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader == null) {
                    showError("Authentication required. Please login again.")
                    return@launch
                }
                
                val response = api.getPDFUrl(submissionId, authHeader)
                
                if (response.isSuccessful && response.body() != null) {
                    val pdfResponse = response.body()!!
                    currentPdfUrl = pdfResponse.pdf_url
                    currentFileName = pdfResponse.filename
                    loadPdf(pdfResponse.pdf_url)
                } else {
                    showError("Failed to get PDF URL")
                }
                
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            }
        }
    }
    
    private fun loadPdf(url: String) {
        currentPdfUrl = url
        hideAllOverlays()
        showLoading()
        
        // Use Google Docs Viewer to display PDF
        val googleDocsUrl = "https://docs.google.com/gviewer?embedded=true&url=$url"
        binding.webView.loadUrl(googleDocsUrl)
    }
    
    private fun loadSamplePdf() {
        // For demo purposes, load a sample PDF
        val samplePdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        loadPdf(samplePdfUrl)
    }
    
    private fun downloadPdf(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(currentFileName ?: "NDA_Document.pdf")
            request.setDescription("Downloading NDA PDF")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                currentFileName ?: "NDA_Document.pdf"
            )
            
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            
            Snackbar.make(binding.root, "Download started", Snackbar.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Download failed: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }
    
    private fun sharePdf(url: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "NDA Document: $url")
                putExtra(Intent.EXTRA_SUBJECT, "NDA Document")
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share PDF"))
            
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Share failed: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }
    
    private fun showLoading() {
        hideAllOverlays()
        binding.layoutLoading.visibility = View.VISIBLE
    }
    
    private fun hideLoading() {
        binding.layoutLoading.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        hideAllOverlays()
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = message
    }
    
    private fun showNoPdfSelected() {
        hideAllOverlays()
        binding.layoutNoPdf.visibility = View.VISIBLE
    }
    
    private fun showActions() {
        binding.layoutActions.visibility = View.VISIBLE
    }
    
    private fun hideAllOverlays() {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutNoPdf.visibility = View.GONE
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pdf_viewer_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                currentPdfUrl?.let { loadPdf(it) }
                true
            }
            R.id.action_open_external -> {
                currentPdfUrl?.let { openInExternalApp(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun openInExternalApp(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "No PDF viewer app found", Snackbar.LENGTH_LONG).show()
        }
    }
    
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
