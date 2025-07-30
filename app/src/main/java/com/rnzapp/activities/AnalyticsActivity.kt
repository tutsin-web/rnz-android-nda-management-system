package com.rnzapp.activities

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import com.rnzapp.R
import com.rnzapp.api.WordPressApi
import com.rnzapp.databinding.ActivityAnalyticsBinding
import com.rnzapp.model.AnalyticsData
import com.rnzapp.utils.RetrofitInstance
import com.rnzapp.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var api: WordPressApi
    
    private var currentAnalyticsData: AnalyticsData? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeComponents()
        setupToolbar()
        setupDatePickers()
        setupClickListeners()
        setupCharts()
        loadAnalyticsData()
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
    
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        
        // Set default dates (last 30 days)
        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val startDate = calendar.time
        
        binding.etStartDate.setText(dateFormat.format(startDate))
        binding.etEndDate.setText(dateFormat.format(endDate))
        
        binding.etStartDate.setOnClickListener {
            showDatePicker { date ->
                binding.etStartDate.setText(dateFormat.format(date))
            }
        }
        
        binding.etEndDate.setOnClickListener {
            showDatePicker { date ->
                binding.etEndDate.setText(dateFormat.format(date))
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnApplyFilter.setOnClickListener {
            loadAnalyticsData()
        }
        
        binding.btnRetry.setOnClickListener {
            loadAnalyticsData()
        }
    }
    
    private fun setupCharts() {
        setupLineChart()
        setupPieChart()
    }
    
    private fun setupLineChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }
    
    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawHoleEnabled(true)
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Countries"
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            
            legend.apply {
                isEnabled = true
                verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.LEFT
                orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 0f
                yOffset = 0f
            }
        }
    }
    
    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun loadAnalyticsData() {
        val startDate = binding.etStartDate.text.toString()
        val endDate = binding.etEndDate.text.toString()
        
        showLoading()
        
        lifecycleScope.launch {
            try {
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader == null) {
                    showError("Authentication required. Please login again.")
                    return@launch
                }
                
                val response = api.getAnalytics(startDate, endDate, authHeader)
                
                if (response.isSuccessful && response.body() != null) {
                    currentAnalyticsData = response.body()!!
                    hideLoading()
                    updateUI(currentAnalyticsData!!)
                } else {
                    showError("Failed to load analytics data")
                }
                
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            }
        }
    }
    
    private fun updateUI(data: AnalyticsData) {
        updateSummaryStats(data)
        updateLineChart(data)
        updatePieChart(data)
    }
    
    private fun updateSummaryStats(data: AnalyticsData) {
        binding.tvTotalSubmissions.text = data.totalSubmissions.toString()
        
        val topCountry = data.topCountries.maxByOrNull { it.count }
        binding.tvTopCountry.text = topCountry?.country ?: "N/A"
    }
    
    private fun updateLineChart(data: AnalyticsData) {
        val entries = data.submissionsOverTime.mapIndexed { index, timeData ->
            Entry(index.toFloat(), timeData.count.toFloat())
        }
        
        val dataSet = LineDataSet(entries, "Submissions").apply {
            color = getColor(R.color.rnz_primary)
            setCircleColor(getColor(R.color.rnz_primary))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 9f
            setDrawFilled(true)
            fillColor = getColor(R.color.rnz_primary_light)
        }
        
        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        
        // Set X-axis labels
        val labels = data.submissionsOverTime.map { it.date }
        binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        
        binding.lineChart.invalidate()
    }
    
    private fun updatePieChart(data: AnalyticsData) {
        val entries = data.topCountries.map { countryData ->
            PieEntry(countryData.percentage, countryData.country)
        }
        
        val dataSet = PieDataSet(entries, "Countries").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = com.github.mikephil.charting.utils.MPPointF(0f, 40f)
            selectionShift = 5f
            
            // Use custom colors
            colors = listOf(
                getColor(R.color.chart_color_1),
                getColor(R.color.chart_color_2),
                getColor(R.color.chart_color_3),
                getColor(R.color.chart_color_4),
                getColor(R.color.chart_color_5)
            )
        }
        
        val pieData = PieData(dataSet).apply {
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }
        
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }
    
    private fun showLoading() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }
    
    private fun hideLoading() {
        binding.layoutLoading.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = message
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.analytics_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadAnalyticsData()
                true
            }
            R.id.action_export -> {
                exportData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun exportData() {
        currentAnalyticsData?.let { data ->
            // For demo purposes, show a message
            Snackbar.make(binding.root, "Export functionality would be implemented here", Snackbar.LENGTH_LONG).show()
            
            // In a real implementation, you would:
            // 1. Generate CSV/Excel file
            // 2. Save to external storage
            // 3. Share via Intent
        }
    }
}
