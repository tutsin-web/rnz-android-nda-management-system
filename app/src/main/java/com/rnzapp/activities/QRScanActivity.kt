package com.rnzapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.rnzapp.databinding.ActivityQrScanBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScanActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityQrScanBinding
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    
    private val barcodeScanner = BarcodeScanning.getClient()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showPermissionDenied()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupClickListeners()
        
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        checkCameraPermission()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnRequestPermission.setOnClickListener {
            requestCameraPermission()
        }
        
        binding.btnRetry.setOnClickListener {
            checkCameraPermission()
        }
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                showPermissionRequest()
            }
        }
    }
    
    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    
    private fun startCamera() {
        hideAllOverlays()
        binding.tvStatus.text = "Scanning..."
        
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (exc: Exception) {
                Log.e("QRScanActivity", "Use case binding failed", exc)
                showError("Failed to start camera")
            }
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }
        
        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { barcodes ->
                    processBarcodes(barcodes)
                })
            }
        
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        
        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e("QRScanActivity", "Use case binding failed", exc)
            showError("Failed to bind camera")
        }
    }
    
    private fun processBarcodes(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    val url = barcode.url?.url
                    if (url != null) {
                        handleQRCodeResult(url)
                        return
                    }
                }
                Barcode.TYPE_TEXT -> {
                    val text = barcode.displayValue
                    if (text != null) {
                        handleQRCodeResult(text)
                        return
                    }
                }
            }
        }
    }
    
    private fun handleQRCodeResult(result: String) {
        runOnUiThread {
            binding.tvStatus.text = "QR Code detected!"
            
            // Stop camera
            cameraProvider?.unbindAll()
            
            // Process the result
            if (result.startsWith("http") || result.startsWith("https")) {
                // It's a URL - check if it's an NDA form link
                if (result.contains("nda") || result.contains("form")) {
                    // Parse URL parameters for prefill data
                    val uri = Uri.parse(result)
                    val intent = Intent(this, NDAFormActivity::class.java)
                    
                    // Extract query parameters for prefilling
                    uri.getQueryParameter("name")?.let { intent.putExtra("name", it) }
                    uri.getQueryParameter("email")?.let { intent.putExtra("email", it) }
                    uri.getQueryParameter("phone")?.let { intent.putExtra("phone", it) }
                    uri.getQueryParameter("country")?.let { intent.putExtra("country", it) }
                    
                    startActivity(intent)
                    finish()
                } else {
                    // Open in browser or WebView
                    openUrlInBrowser(result)
                }
            } else {
                // It's plain text - try to parse as form data
                parseTextData(result)
            }
        }
    }
    
    private fun openUrlInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            showError("Unable to open URL")
        }
    }
    
    private fun parseTextData(text: String) {
        // Try to parse text as key-value pairs (e.g., "name=John,email=john@example.com")
        val intent = Intent(this, NDAFormActivity::class.java)
        
        try {
            val pairs = text.split(",")
            for (pair in pairs) {
                val keyValue = pair.split("=")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim()
                    val value = keyValue[1].trim()
                    intent.putExtra(key, value)
                }
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // If parsing fails, just open NDA form without prefill
            startActivity(intent)
            finish()
        }
    }
    
    private fun showPermissionRequest() {
        hideAllOverlays()
        binding.layoutPermission.visibility = View.VISIBLE
    }
    
    private fun showPermissionDenied() {
        hideAllOverlays()
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = "Camera permission is required to scan QR codes"
    }
    
    private fun showError(message: String) {
        hideAllOverlays()
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = message
    }
    
    private fun hideAllOverlays() {
        binding.layoutPermission.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        barcodeScanner.close()
    }
    
    private inner class QRCodeAnalyzer(
        private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit
    ) : ImageAnalysis.Analyzer {
        
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            onBarcodeDetected(barcodes)
                        }
                    }
                    .addOnFailureListener {
                        Log.e("QRScanActivity", "Barcode scanning failed", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }
}
