package com.rnzapp.utils

import android.util.Patterns
import java.util.regex.Pattern

object Validators {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPhone(phone: String): Boolean {
        // Basic phone validation - at least 7 digits, can contain +, spaces, dashes
        val phonePattern = "^[+]?[0-9\\s\\-()]{7,15}$"
        return phone.isNotEmpty() && Pattern.matches(phonePattern, phone)
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.trim().length >= 2
    }
    
    fun isValidAddress(address: String): Boolean {
        return address.isNotEmpty() && address.trim().length >= 5
    }
    
    fun isValidIdNumber(idNumber: String, idType: String): Boolean {
        return when (idType) {
            "emirates_id" -> isValidEmiratesId(idNumber)
            "passport" -> isValidPassport(idNumber)
            else -> idNumber.isNotEmpty()
        }
    }
    
    private fun isValidEmiratesId(emiratesId: String): Boolean {
        // Emirates ID format: 784-YYYY-XXXXXXX-X (15 digits total)
        val cleanId = emiratesId.replace("-", "").replace(" ", "")
        return cleanId.length == 15 && cleanId.all { it.isDigit() }
    }
    
    private fun isValidPassport(passport: String): Boolean {
        // Basic passport validation - alphanumeric, 6-12 characters
        val passportPattern = "^[A-Za-z0-9]{6,12}$"
        return passport.isNotEmpty() && Pattern.matches(passportPattern, passport)
    }
    
    fun isValidDate(date: String): Boolean {
        // Basic date validation - assumes format YYYY-MM-DD or DD/MM/YYYY
        return date.isNotEmpty() && (
            Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", date) ||
            Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", date)
        )
    }
    
    fun isValidCountry(country: String): Boolean {
        return country.isNotEmpty() && country != "Select Country"
    }
    
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    fun validateVisitorForm(
        name: String,
        email: String,
        phone: String,
        address: String,
        country: String,
        idType: String,
        idNumber: String,
        visitDate: String,
        signedDate: String,
        agreedToTerms: Boolean
    ): ValidationResult {
        
        when {
            !isValidName(name) -> return ValidationResult(false, "Please enter a valid name (at least 2 characters)")
            !isValidEmail(email) -> return ValidationResult(false, "Please enter a valid email address")
            !isValidPhone(phone) -> return ValidationResult(false, "Please enter a valid phone number")
            !isValidAddress(address) -> return ValidationResult(false, "Please enter a valid address (at least 5 characters)")
            !isValidCountry(country) -> return ValidationResult(false, "Please select a country")
            !isValidIdNumber(idNumber, idType) -> {
                val idTypeName = if (idType == "emirates_id") "Emirates ID" else "Passport"
                return ValidationResult(false, "Please enter a valid $idTypeName")
            }
            !isValidDate(visitDate) -> return ValidationResult(false, "Please select a valid visit date")
            !isValidDate(signedDate) -> return ValidationResult(false, "Please select a valid signed date")
            !agreedToTerms -> return ValidationResult(false, "Please agree to the terms and conditions")
            else -> return ValidationResult(true)
        }
    }
}
