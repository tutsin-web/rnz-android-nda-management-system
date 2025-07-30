package com.rnzapp.model

import com.google.gson.annotations.SerializedName

data class AnalyticsData(
    @SerializedName("total_submissions")
    val totalSubmissions: Int,
    
    @SerializedName("top_countries")
    val topCountries: List<CountryData>,
    
    @SerializedName("submissions_over_time")
    val submissionsOverTime: List<TimeData>,
    
    @SerializedName("date_range")
    val dateRange: DateRange
)

data class CountryData(
    @SerializedName("country")
    val country: String,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("percentage")
    val percentage: Float
)

data class TimeData(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("count")
    val count: Int
)

data class DateRange(
    @SerializedName("start_date")
    val startDate: String,
    
    @SerializedName("end_date")
    val endDate: String
)

data class AnalyticsRequest(
    @SerializedName("start_date")
    val startDate: String? = null,
    
    @SerializedName("end_date")
    val endDate: String? = null
)
