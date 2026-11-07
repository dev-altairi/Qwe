package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.telephony.ActiveCallState
import com.example.telephony.CallPhase
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ActiveCallOverlay(
  callState: ActiveCallState,
  onEndCall: () -> Unit,
  onToggleMute: () -> Unit,
  onToggleSpeaker: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showSignalingLogs by remember { mutableStateOf(false) }

  val formattedDuration = remember(callState.durationSeconds) {
    val mins = callState.durationSeconds / 60
    val secs = callState.durationSeconds % 60
    "%02d:%02d".format(mins, secs)
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("active_call_overlay"),
    color = DarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top status
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(DarkSurfaceVariant)
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "P2P WebRTC Direct UDP",
              fontSize = 11.sp,
              color = EmeraldPrimary,
              fontFamily = FontFamily.Monospace
            )
          }

          IconButton(
            onClick = { showSignalingLogs = !showSignalingLogs },
            modifier = Modifier.testTag("toggle_signaling_log_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Terminal,
              contentDescription = "Signaling Logs",
              tint = if (showSignalingLogs) CyanAccent else TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Peer Avatar
        Box(
          modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant)
            .border(
              2.dp,
              if (callState.phase == CallPhase.CONNECTED_P2P) EmeraldPrimary else DarkBorder,
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = if (callState.phase == CallPhase.CONNECTED_P2P) EmeraldPrimary else CyanAccent,
            modifier = Modifier.size(52.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (callState.peerName.isNotEmpty()) callState.peerName else callState.peerIdentifier,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (callState.peerIdentifier.startsWith("@")) callState.peerIdentifier else "ID: ${callState.peerIdentifier}",
          fontSize = 14.sp,
          color = TextSecondary,
          fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phase Status
        val statusText = when (callState.phase) {
          CallPhase.SIGNALING_OFFER -> "Sending SDP Offer over WebSocket…"
          CallPhase.STUN_CANDIDATES -> "Gathering Google STUN candidates…"
          CallPhase.RINGING_PEER -> "Ringing peer device…"
          CallPhase.CONNECTED_P2P -> "Connected P2P ($formattedDuration)"
          CallPhase.ENDED -> "Call Ended (${callState.endReason})"
          else -> "Calling…"
        }

        Text(
          text = statusText,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (callState.phase == CallPhase.CONNECTED_P2P) EmeraldPrimary else CyanAccent
        )
      }

      // Middle: Waveform & Telemetry
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        AnimatedVisibility(visible = showSignalingLogs) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(DarkSurface)
              .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "WEBSOCKET & WEBRTC SIGNALING LOG",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(4.dp))
              for (log in callState.signalingLog.takeLast(5)) {
                Text(
                  text = log,
                  fontSize = 10.sp,
                  color = TextMuted,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        WaveformVisualizer(
          isActive = callState.phase == CallPhase.CONNECTED_P2P,
          isMuted = callState.isMuted,
          liveAmplitudes = callState.waveAmplitudes.ifEmpty { null }
        )

        Spacer(modifier = Modifier.height(18.dp))

        TelemetryCard(telemetry = callState.telemetry)
      }

      // Bottom Call Controls
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CallControlButton(
            icon = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
            label = if (callState.isMuted) "Unmute" else "Mute",
            isActive = callState.isMuted,
            onClick = onToggleMute,
            tag = "call_mute_btn"
          )

          CallControlButton(
            icon = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
            label = "Speaker",
            isActive = callState.isSpeakerOn,
            onClick = onToggleSpeaker,
            tag = "call_speaker_btn"
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // End Call Button
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(CoralRed)
            .border(2.dp, Color(0xFFB91C1C), CircleShape)
            .testTag("end_call_btn"),
          contentAlignment = Alignment.Center
        ) {
          IconButton(
            onClick = onEndCall,
            modifier = Modifier.fillMaxSize()
          ) {
            Icon(
              imageVector = Icons.Default.CallEnd,
              contentDescription = "End Call",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun CallControlButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.testTag(tag)
  ) {
    Box(
      modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(if (isActive) EmeraldPrimary else DarkSurfaceVariant)
        .border(1.dp, if (isActive) EmeraldPrimary else DarkBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      IconButton(
        onClick = onClick,
        modifier = Modifier.fillMaxSize()
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = if (isActive) Color.Black else TextPrimary,
          modifier = Modifier.size(26.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      fontSize = 12.sp,
      color = if (isActive) EmeraldPrimary else TextSecondary
    )
  }
}
