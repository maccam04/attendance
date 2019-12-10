package com.macsanityapps.virtualattendance.common

import java.util.regex.Pattern

object ValidationRule {

    private val MOBILE_NUMBER_REGEX = "[7-9][0-9]{9}$"
    private val TEXT_WITH_MOBILE_NUMBER_REGEX = ".*[7-9][0-9]{9}.*"
    private val TEXT_WITH_EMAIL_ADDRESS_REGEX = ".*[a-zA-Z0-9\\+\\" + ".\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9]{1,64}\\.[a-zA-Z0-9]{1,25}.*"

    private val USERNAME_REGEX = "^[a-zA-Z][a-zA-Z._0-9]{2,19}$"
    private val TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX = ".*[0-9]{5,}.*"

    fun isValidMobileNumber(number: String): Boolean {
        val mPattern = Pattern.compile(MOBILE_NUMBER_REGEX)
        val matcher = mPattern.matcher(number)
        return matcher.find()
    }

    fun isValidPrice(price: String): ValidationResult<String> {

        if (price == "0") {
            return ValidationResult.failure("Invalid input price", price)
        }

        return ValidationResult.success(price)
    }

    fun isValidQty(qty: String): ValidationResult<String> {

        if (qty.toInt() == 0) {
            return ValidationResult.failure("Invalid Qty", qty)
        }

        return ValidationResult.success(qty)
    }

    fun isNotEmpty(text: String): ValidationResult<String> {
        if (text.isEmpty()) {
            return ValidationResult.failure("This field is required.", text)
        }
        return ValidationResult.success(text)
    }

    fun containsFourConsecutiveNumbers(text: String): Boolean {
        val mPattern = Pattern.compile(TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX)
        val matcher = mPattern.matcher(text)
        return matcher.find()
    }

    fun containsMobileNumber(text: String): Boolean {
        val mPattern = Pattern.compile(TEXT_WITH_MOBILE_NUMBER_REGEX)
        val matcher = mPattern.matcher(text)
        return matcher.find()
    }

    fun isValidEmailAddress(text: String): ValidationResult<String> {
        if (text.isEmpty()) {
            return ValidationResult.failure(null, text)
        }

        val mPattern = Pattern.compile(TEXT_WITH_EMAIL_ADDRESS_REGEX)
        val matcher = mPattern.matcher(text)
        val isValid = matcher.find()

        return if (isValid) {
            ValidationResult.success(text)
        } else ValidationResult.failure("Please enter correct email address", text)

    }

}
