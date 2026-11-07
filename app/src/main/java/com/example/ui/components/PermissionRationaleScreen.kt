package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun PermissionRationaleScreen(
  onRequestPermissions: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("permission_rationale_screen"),
    color = DarkBackground
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceVariant)
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "HARDWARE PERMISSIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = EmeraldPrimary,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Center Visual & Rationale
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(DarkSurface)
            .border(2.dp, EmeraldPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Microphone",
            tint = EmeraldPrimary,
            modifier = Modifier.size(56.dp)
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
          text = "Microphone Access Required",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "VoxFree transmits encrypted, studio-quality voice audio directly between devices using peer-to-peer WebRTC over UDP. To speak with your peers, Android requires microphone recording access.",
          fontSize = 14.sp,
          color = TextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Permission Details Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Microphone (AudioRecord)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Audio input for Opus 48 kHz voice calls", fontSize = 11.sp, color = TextMuted)
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Notifications, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Call Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Alerts for incoming P2P call requests", fontSize = 11.sp, color = TextMuted)
              }
            }
          }
        }
      }

      // Bottom Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onRequestPermissions,
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("grant_permissions_btn")
        ) {
          Text("Grant Microphone Access", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        OutlinedButton(
          onClick = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
              data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
          },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("open_app_settings_btn")
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextSecondary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Open Device Settings", color = TextSecondary, fontSize = 13.sp)
        }
      }
    }
  }
}
