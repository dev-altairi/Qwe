package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun IncomingCallOverlay(
  callerName: String,
  callerId: String,
  onAccept: () -> Unit,
  onDecline: () -> Unit,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "pulse_avatar")
  val pulseScale by transition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(800),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("incoming_call_overlay"),
    color = DarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top header
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "INCOMING P2P WEBRTC CALL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = CyanAccent,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Pulsing Avatar
        Box(
          modifier = Modifier
            .size(110.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(DarkSurfaceVariant)
            .border(2.dp, EmeraldPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(60.dp)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = callerName,
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (callerId.startsWith("@")) callerId else "Call ID: #$callerId",
          fontSize = 15.sp,
          color = TextSecondary,
          fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Direct UDP / SRTP (Opus 48kHz HD)",
          fontSize = 12.sp,
          color = EmeraldPrimary,
          fontFamily = FontFamily.Monospace
        )
      }

      // Bottom Accept / Decline Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Decline Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(CoralRed)
              .border(2.dp, Color(0xFFB91C1C), CircleShape)
              .testTag("decline_call_btn"),
            contentAlignment = Alignment.Center
          ) {
            IconButton(
              onClick = onDecline,
              modifier = Modifier.fillMaxSize()
            ) {
              Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "Decline",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Decline", fontSize = 13.sp, color = TextSecondary)
        }

        // Accept Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(EmeraldPrimary)
              .border(2.dp, Color(0xFF059669), CircleShape)
              .testTag("accept_call_btn"),
            contentAlignment = Alignment.Center
          ) {
            IconButton(
              onClick = onAccept,
              modifier = Modifier.fillMaxSize()
            ) {
              Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Accept",
                tint = Color.Black,
                modifier = Modifier.size(34.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Accept", fontSize = 13.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
