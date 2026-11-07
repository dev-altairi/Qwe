package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GatewayConfig
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
  currentConfig: GatewayConfig,
  onSaveConfig: (GatewayConfig) -> Unit,
  modifier: Modifier = Modifier
) {
  var accountSid by remember { mutableStateOf(currentConfig.accountSid) }
  var authToken by remember { mutableStateOf(currentConfig.authToken) }
  var callerId by remember { mutableStateOf(currentConfig.callerId) }
  var twimlAppSid by remember { mutableStateOf(currentConfig.twimlAppSid) }
  var showSavedNotification by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Column {
      Text(
        text = "Gateway & Protocol Setup",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = "Twilio CPaaS, SIP Signaling, and WebRTC Media Architecture",
        fontSize = 12.sp,
        color = TextSecondary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Architecture Overview Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkSurface)
        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Hub, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "3-LAYER TELECOM ARCHITECTURE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent,
            fontFamily = FontFamily.Monospace
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "1. Media Layer: UDP + RTP/SRTP real-time encrypted voice packets\n" +
            "2. Signaling Layer: SIP / WebSockets via Twilio Voice gateway\n" +
            "3. Telecom Layer: CPaaS PSTN trunking to landlines & mobile carriers",
          fontSize = 12.sp,
          color = TextSecondary,
          lineHeight = 18.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Twilio Credentials Form
    Text(
      text = "Twilio CPaaS Configuration",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = accountSid,
      onValueChange = { accountSid = it },
      label = { Text("Account SID") },
      placeholder = { Text("ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", color = TextMuted) },
      modifier = Modifier.fillMaxWidth().testTag("account_sid_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = authToken,
      onValueChange = { authToken = it },
      label = { Text("Auth Token") },
      placeholder = { Text("Optional (for live REST gateway bridge)", color = TextMuted) },
      modifier = Modifier.fillMaxWidth().testTag("auth_token_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = callerId,
      onValueChange = { callerId = it },
      label = { Text("Verified Caller ID Number") },
      placeholder = { Text("+12025550143", color = TextMuted) },
      modifier = Modifier.fillMaxWidth().testTag("caller_id_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = twimlAppSid,
      onValueChange = { twimlAppSid = it },
      label = { Text("TwiML App SID") },
      placeholder = { Text("APxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", color = TextMuted) },
      modifier = Modifier.fillMaxWidth().testTag("twiml_app_sid_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Protocol Specs
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(DarkSurfaceVariant)
        .padding(14.dp)
    ) {
      Column {
        Text("Transport Protocol: ${currentConfig.transportProtocol}", fontSize = 12.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Audio Codec: ${currentConfig.preferredCodec}", fontSize = 12.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("STUN / TURN Server: ${currentConfig.stunTurnServer}", fontSize = 12.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
      onClick = {
        val updated = currentConfig.copy(
          accountSid = accountSid.trim(),
          authToken = authToken.trim(),
          callerId = callerId.trim(),
          twimlAppSid = twimlAppSid.trim()
        )
        onSaveConfig(updated)
        showSavedNotification = true
      },
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("save_gateway_config_btn")
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Gateway Settings", fontWeight = FontWeight.Bold, color = Color.Black)
      }
    }

    if (showSavedNotification) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Settings saved successfully to persistent storage.",
        fontSize = 12.sp,
        color = EmeraldPrimary,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}
