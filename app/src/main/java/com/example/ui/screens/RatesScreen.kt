package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryRate
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RatesScreen(
  rates: List<CountryRate>,
  onSelectCountry: (CountryRate) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredRates = remember(searchQuery, rates) {
    if (searchQuery.isBlank()) {
      rates
    } else {
      rates.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
          it.dialCode.contains(searchQuery) ||
          it.iso.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Column {
      Text(
        text = "PSTN Rates & Coverage",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = "Transparent per-minute credit rates for mobile & landlines",
        fontSize = 12.sp,
        color = TextSecondary
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("rates_search_input"),
      placeholder = { Text("Search country or dial code (e.g. +44, India)…", color = TextMuted) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredRates, key = { it.iso + it.dialCode }) { rate ->
        CountryRateItem(
          rate = rate,
          onClick = { onSelectCountry(rate) }
        )
      }
    }
  }
}

@Composable
fun CountryRateItem(
  rate: CountryRate,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurface)
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(14.dp)
      .testTag("rate_item_${rate.iso}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Text(text = rate.flagEmoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = rate.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = rate.dialCode,
              fontSize = 12.sp,
              color = CyanAccent,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = " • Grade ${rate.qualityGrade}",
              fontSize = 12.sp,
              color = TextMuted
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${rate.creditsPerMinute} cr/min",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
          )
          Text(
            text = "Direct PSTN",
            fontSize = 10.sp,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Select",
            tint = EmeraldLight,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
