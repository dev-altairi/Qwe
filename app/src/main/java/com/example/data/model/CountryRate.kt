package com.example.data.model

data class CountryRate(
  val iso: String,
  val name: String,
  val dialCode: String,
  val flagEmoji: String,
  val creditsPerMinute: Int,
  val qualityGrade: String,
  val routeType: String = "PSTN Direct Route"
)
