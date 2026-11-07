package com.example.data.repository

import com.example.data.model.CountryRate

object RatesRepository {
  val allRates: List<CountryRate> = listOf(
    CountryRate("US", "United States", "+1", "🇺🇸", creditsPerMinute = 1, qualityGrade = "A+"),
    CountryRate("CA", "Canada", "+1", "🇨🇦", creditsPerMinute = 1, qualityGrade = "A+"),
    CountryRate("GB", "United Kingdom", "+44", "🇬🇧", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("IN", "India", "+91", "🇮🇳", creditsPerMinute = 2, qualityGrade = "A"),
    CountryRate("DE", "Germany", "+49", "🇩🇪", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("FR", "France", "+33", "🇫🇷", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("JP", "Japan", "+81", "🇯🇵", creditsPerMinute = 3, qualityGrade = "A+"),
    CountryRate("AU", "Australia", "+61", "🇦🇺", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("BR", "Brazil", "+55", "🇧🇷", creditsPerMinute = 3, qualityGrade = "A"),
    CountryRate("MX", "Mexico", "+52", "🇲🇽", creditsPerMinute = 2, qualityGrade = "A"),
    CountryRate("NG", "Nigeria", "+234", "🇳🇬", creditsPerMinute = 4, qualityGrade = "A-"),
    CountryRate("PH", "Philippines", "+63", "🇵🇭", creditsPerMinute = 4, qualityGrade = "A-"),
    CountryRate("PK", "Pakistan", "+92", "🇵🇰", creditsPerMinute = 3, qualityGrade = "A"),
    CountryRate("AE", "United Arab Emirates", "+971", "🇦🇪", creditsPerMinute = 5, qualityGrade = "A"),
    CountryRate("SA", "Saudi Arabia", "+966", "🇸🇦", creditsPerMinute = 4, qualityGrade = "A"),
    CountryRate("IT", "Italy", "+39", "🇮🇹", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("ES", "Spain", "+34", "🇪🇸", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("SG", "Singapore", "+65", "🇸🇬", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("KR", "South Korea", "+82", "🇰🇷", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("ZA", "South Africa", "+27", "🇿🇦", creditsPerMinute = 3, qualityGrade = "A"),
    CountryRate("NL", "Netherlands", "+31", "🇳🇱", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("SE", "Sweden", "+46", "🇸🇪", creditsPerMinute = 2, qualityGrade = "A+"),
    CountryRate("CN", "China", "+86", "🇨🇳", creditsPerMinute = 2, qualityGrade = "A"),
    CountryRate("EG", "Egypt", "+20", "🇪🇬", creditsPerMinute = 4, qualityGrade = "A-"),
    CountryRate("TR", "Turkey", "+90", "🇹🇷", creditsPerMinute = 3, qualityGrade = "A")
  )

  fun detectCountry(rawNumber: String): CountryRate {
    val clean = rawNumber.trim().replace(" ", "").replace("-", "")
    if (clean.isEmpty()) {
      return allRates[0] // Default US
    }

    // Try longest matching dialCode first
    val sortedByLength = allRates.sortedByDescending { it.dialCode.length }
    for (rate in sortedByLength) {
      if (clean.startsWith(rate.dialCode)) {
        return rate
      }
      val prefixWithoutPlus = rate.dialCode.removePrefix("+")
      if (clean.startsWith(prefixWithoutPlus) && clean.length > prefixWithoutPlus.length) {
        return rate
      }
    }

    return CountryRate("GL", "International PSTN", "+", "🌐", creditsPerMinute = 3, qualityGrade = "A")
  }
}
