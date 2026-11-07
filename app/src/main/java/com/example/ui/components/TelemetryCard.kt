package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telephony.VoipTelemetry
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TelemetryCard(
  telemetry: VoipTelemetry,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(DarkSurface)
      .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(EmeraldPrimary)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DIRECT P2P WEBRTC MEDIA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = CyanAccent,
            fontFamily = FontFamily.Monospace
          )
        }

        Text(
          text = "MOS ${"%.2f".format(telemetry.mosScore)} • ${telemetry.qualityGrade}",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = EmeraldPrimary,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        TelemetryStatItem(
          label = "RTT Latency",
          value = "${telemetry.latencyMs} ms",
          color = EmeraldPrimary
        )
        TelemetryStatItem(
          label = "Jitter",
          value = "${telemetry.jitterMs} ms",
          color = TextPrimary
        )
        TelemetryStatItem(
          label = "Packet Loss",
          value = "${"%.1f".format(telemetry.packetLossPercent)}%",
          color = EmeraldPrimary
        )
        TelemetryStatItem(
          label = "Audio Codec",
          value = "Opus 48kHz",
          color = CyanAccent
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Route: ${telemetry.connectionMode}",
          fontSize = 10.sp,
          color = TextMuted,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "STUN: Google Public",
          fontSize = 10.sp,
          color = TextMuted,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
fun TelemetryStatItem(
  label: String,
  value: String,
  color: Color
) {
  Column {
    Text(
      text = label,
      fontSize = 10.sp,
      color = TextSecondary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = color
    )
  }
}
