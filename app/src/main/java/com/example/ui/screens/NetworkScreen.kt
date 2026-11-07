package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.NetworkSettingsRepository
import com.example.telephony.webrtc.SignalingState
import com.example.telephony.webrtc.WebRtcIceState
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
fun NetworkScreen(
  userProfile: UserProfile,
  currentSignalingUrl: String,
  audioRoutingMode: String,
  signalingState: SignalingState,
  iceState: WebRtcIceState,
  onUpdateSignalingUrl: (String) -> Unit,
  onUpdateAudioMode: (String) -> Unit,
  onUpdateProfile: (username: String, displayName: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var signalingUrlInput by remember(currentSignalingUrl) { mutableStateOf(currentSignalingUrl) }
  var usernameInput by remember { mutableStateOf(userProfile.username) }
  var displayNameInput by remember { mutableStateOf(userProfile.displayName) }
  var pingResultMs by remember { mutableIntStateOf(18) }

  val audioModes = listOf("Auto (Adaptive)", "Communication Mode", "In-Call Mode")

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Column {
      Text(
        text = "WebRTC & P2P Diagnostics",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = "Persistent WebSocket Hub, Google STUN & Hardware Adaptation",
        fontSize = 12.sp,
        color = TextSecondary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Real-Time WebRTC Architecture Card
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
          Icon(Icons.Default.Hub, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "HARDENED WEBRTC AUDIO PIPELINE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary,
            fontFamily = FontFamily.Monospace
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "• Routing: $audioRoutingMode (with Xiaomi/OEM fallback)\n" +
            "• Factory: AudioSource (AEC, AGC, NS) -> AudioTrack ARDAMSa0\n" +
            "• STUN Server: stun:stun.l.google.com:19302 (MaxBundle)\n" +
            "• Candidate Propagation: Inbound Candidate Queueing Active",
          fontSize = 11.sp,
          color = CyanAccent,
          lineHeight = 17.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Live Status Card
    Text(
      text = "Live Connection State",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(DarkSurfaceVariant)
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NetworkStatusRow(
          label = "Signaling Server Status",
          detail = currentSignalingUrl,
          status = signalingState.name,
          isGood = signalingState == SignalingState.REGISTERED || signalingState == SignalingState.CONNECTED
        )
        NetworkStatusRow(
          label = "WebRTC ICE State",
          detail = "Google STUN (stun:stun.l.google.com:19302)",
          status = iceState.name,
          isGood = iceState != WebRtcIceState.FAILED && iceState != WebRtcIceState.DISCONNECTED
        )
        NetworkStatusRow(
          label = "Hardware Voice Routing",
          detail = audioRoutingMode,
          status = "Active",
          isGood = true
        )
        NetworkStatusRow(
          label = "Audio Codec & Track",
          detail = "Opus HD 48kHz (AEC, AGC & Noise Supp.)",
          status = "ARDAMSa0 (48kbps)",
          isGood = true
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Hardware Audio Mode Selector (OEM Compatibility)
    Text(
      text = "Hardware Audio Routing Mode",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      audioModes.forEach { mode ->
        val isSelected = audioRoutingMode == mode
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
            .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
            .clickable {
              onUpdateAudioMode(mode)
              Toast.makeText(context, "Audio mode set to $mode", Toast.LENGTH_SHORT).show()
            }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mode.split(" ").first(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else TextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Signaling Server URL Configuration (Persisted)
    Text(
      text = "WebSocket Signaling Endpoint (Persistent)",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = signalingUrlInput,
      onValueChange = { signalingUrlInput = it },
      label = { Text("Signaling Server WebSocket URL") },
      placeholder = { Text("ws://192.168.1.100:8080/ws", color = TextMuted) },
      modifier = Modifier.fillMaxWidth().testTag("signaling_url_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        onClick = {
          signalingUrlInput = NetworkSettingsRepository.LAN_PRESET_URL
          onUpdateSignalingUrl(signalingUrlInput)
          Toast.makeText(context, "Saved & Reconnected to Local LAN Preset", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.weight(1f).testTag("lan_preset_btn")
      ) {
        Icon(Icons.Default.Lan, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("LAN Preset", fontSize = 11.sp)
      }

      OutlinedButton(
        onClick = {
          signalingUrlInput = NetworkSettingsRepository.DEFAULT_SIGNALING_URL
          onUpdateSignalingUrl(signalingUrlInput)
          Toast.makeText(context, "Saved & Reconnected to Cloud Server", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.weight(1f).testTag("cloud_preset_btn")
      ) {
        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Cloud Server", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = {
        onUpdateSignalingUrl(signalingUrlInput.trim())
        Toast.makeText(context, "Saved & Reconnected to $signalingUrlInput", Toast.LENGTH_SHORT).show()
      },
      colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth().testTag("apply_signaling_url_btn")
    ) {
      Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
      Spacer(modifier = Modifier.width(6.dp))
      Text("Save URL & Reconnect", color = Color.Black, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Profile Identity
    Text(
      text = "My Profile & Identity",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = displayNameInput,
      onValueChange = { displayNameInput = it },
      label = { Text("Display Name") },
      modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = usernameInput,
      onValueChange = { usernameInput = it.lowercase().replace(" ", "_") },
      label = { Text("Username Handle (@)") },
      leadingIcon = { Text("@", color = CyanAccent, fontWeight = FontWeight.Bold) },
      modifier = Modifier.fillMaxWidth().testTag("edit_username_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = {
        onUpdateProfile(usernameInput.trim(), displayNameInput.trim())
        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
      },
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
    ) {
      Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
      Spacer(modifier = Modifier.width(6.dp))
      Text("Save Profile", color = Color.Black, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(30.dp))
  }
}

@Composable
fun NetworkStatusRow(
  label: String,
  detail: String,
  status: String,
  isGood: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
      Text(text = detail, fontSize = 11.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(if (isGood) EmeraldLight else Color(0xFFEF4444))
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = status,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isGood) EmeraldLight else Color(0xFFEF4444)
      )
    }
  }
}
