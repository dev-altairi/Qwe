package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
  currentBalance: Int,
  dailyClaimed: Boolean,
  onDismiss: () -> Unit,
  onClaimDaily: () -> Unit,
  onClaimBoost: () -> Unit
) {
  var isWatchingAd by remember { mutableStateOf(false) }
  var adCountdown by remember { mutableIntStateOf(5) }
  var adFinished by remember { mutableStateOf(false) }

  LaunchedEffect(isWatchingAd) {
    if (isWatchingAd) {
      adCountdown = 5
      while (adCountdown > 0) {
        delay(1000)
        adCountdown--
      }
      adFinished = true
      isWatchingAd = false
      onClaimBoost()
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarkSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
      modifier = Modifier.fillMaxWidth().testTag("rewarded_boost_dialog")
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Free Call Credits",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_boost_dialog_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Balance banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Current Calling Balance",
              fontSize = 12.sp,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$currentBalance Credits",
              fontSize = 28.sp,
              fontWeight = FontWeight.Black,
              color = EmeraldPrimary
            )
            Text(
              text = "≈ ${currentBalance / 2} to $currentBalance minutes of PSTN calling",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isWatchingAd) {
          // Sponsor Viewing Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.verticalGradient(
                  listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
              )
              .border(1.dp, CyanAccent, RoundedCornerShape(16.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(
                progress = { (5 - adCountdown) / 5f },
                color = CyanAccent,
                trackColor = DarkBorder,
                modifier = Modifier.size(54.dp),
                strokeWidth = 4.dp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Viewing Telecom Sponsor ($adCountdown s)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Subsidizing your international phone call…",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
              )
            }
          }
        } else if (adFinished) {
          // Success Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(EmeraldContainer)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OnEmeraldContainer)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "+30 Credits Added to Balance!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = OnEmeraldContainer
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { adFinished = false },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            modifier = Modifier.fillMaxWidth().testTag("boost_again_btn")
          ) {
            Text("Boost Another 30 Credits", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        } else {
          // Reward Options
          // 1. Daily Bonus
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(DarkSurfaceVariant)
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CardGiftcard,
                  contentDescription = null,
                  tint = if (dailyClaimed) TextMuted else EmeraldLight,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Daily Free Grant",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                  )
                  Text(
                    text = if (dailyClaimed) "Already claimed today" else "+100 daily calling credits",
                    fontSize = 11.sp,
                    color = if (dailyClaimed) TextMuted else EmeraldPrimary
                  )
                }
              }

              Button(
                onClick = onClaimDaily,
                enabled = !dailyClaimed,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("claim_daily_btn")
              ) {
                Text(
                  text = if (dailyClaimed) "Claimed" else "Claim",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (dailyClaimed) TextMuted else Color.Black
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 2. Video Booster
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(DarkSurfaceVariant)
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.PlayCircle,
                  contentDescription = null,
                  tint = CyanAccent,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Rewarded Ad Booster",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                  )
                  Text(
                    text = "+30 credits (5-sec sponsor spot)",
                    fontSize = 11.sp,
                    color = CyanAccent
                  )
                }
              }

              Button(
                onClick = { isWatchingAd = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.testTag("watch_booster_btn")
              ) {
                Text(
                  text = "Watch",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Zero cost to you. Calls are subsidized through sponsor partnerships & developer pools.",
          fontSize = 11.sp,
          color = TextMuted,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
