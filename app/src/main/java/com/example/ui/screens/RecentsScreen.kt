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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhoneForwarded
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.CallRecord
import com.example.data.model.CallStatus
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentsScreen(
  calls: List<CallRecord>,
  onRedial: (String) -> Unit,
  onDeleteCall: (Long) -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showClearConfirm by remember { mutableStateOf(false) }
  val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Call History",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "${calls.size} direct P2P voice sessions",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }

      if (calls.isNotEmpty()) {
        IconButton(
          onClick = { showClearConfirm = true },
          modifier = Modifier.testTag("clear_history_btn")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Clear All",
            tint = TextMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (calls.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PhoneForwarded,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(36.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No P2P Calls Yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Dial any 5-digit Call ID or select a peer from Contacts to experience zero-latency WebRTC calling.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(calls, key = { it.id }) { call ->
          CallRecordItem(
            call = call,
            formattedTime = dateFormat.format(Date(call.timestamp)),
            onRedial = { onRedial(call.peerCallId) },
            onDelete = { onDeleteCall(call.id) }
          )
        }
      }
    }
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Clear All Call Logs?", color = TextPrimary) },
      text = { Text("This will permanently remove all P2P call history records from this device.", color = TextSecondary) },
      confirmButton = {
        Button(
          onClick = {
            onClearAll()
            showClearConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirm = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = DarkSurface
    )
  }
}

@Composable
fun CallRecordItem(
  call: CallRecord,
  formattedTime: String,
  onRedial: () -> Unit,
  onDelete: () -> Unit
) {
  val durationStr = if (call.durationSeconds > 0) {
    val m = call.durationSeconds / 60
    val s = call.durationSeconds % 60
    "%02d:%02d".format(m, s)
  } else {
    call.status.name.lowercase().replaceFirstChar { it.uppercase() }
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurface)
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .clickable { onRedial() }
      .padding(14.dp)
      .testTag("call_record_${call.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = if (call.status == CallStatus.COMPLETED) EmeraldPrimary else CoralRed,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = if (call.peerUsername.isNotEmpty()) "@${call.peerUsername}" else "ID: ${call.peerCallId}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "ID: ${call.peerCallId}",
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace,
              color = CyanAccent
            )
            Text(
              text = " • $formattedTime",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = durationStr,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (call.status == CallStatus.COMPLETED) EmeraldPrimary else CoralRed
            )
            Text(
              text = " • MOS ${"%.2f".format(call.avgMosScore)}",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = EmeraldPrimary
            )
            Text(
              text = " • ${call.avgLatencyMs}ms",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = TextMuted
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onRedial,
          modifier = Modifier.testTag("redial_btn_${call.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Redial",
            tint = EmeraldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier.testTag("delete_btn_${call.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
