package com.example.ui.screens

import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactRecord
import com.example.data.model.UserProfile
import com.example.ui.components.Dialpad
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
fun DialerScreen(
  dialerInput: String,
  userProfile: UserProfile,
  quickContacts: List<ContactRecord>,
  onDigitPress: (Char) -> Unit,
  onDigitLongPress: (Char) -> Unit,
  onBackspace: () -> Unit,
  onTextInput: (String) -> Unit,
  onSelectPeer: (String) -> Unit,
  onInitiateCall: () -> Unit,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  val view = LocalView.current
  var isTextMode by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Profile & Call ID Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkSurface)
        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .testTag("my_call_id_card")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "MY VOXFREE CALL ID",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = EmeraldPrimary,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = userProfile.callId,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "@${userProfile.username}",
              fontSize = 13.sp,
              color = TextSecondary
            )
          }
        }

        IconButton(
          onClick = {
            clipboardManager.setText(AnnotatedString(userProfile.callId))
            Toast.makeText(context, "Call ID ${userProfile.callId} copied!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("copy_my_id_btn")
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy ID",
            tint = CyanAccent,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Dial Peer Chips
    if (quickContacts.isNotEmpty()) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Quick Call Peers",
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(quickContacts) { contact ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                .clickable { onSelectPeer(contact.callId) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(contact.avatarColorHex))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = contact.displayName.split(" ").firstOrNull() ?: contact.username,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "#${contact.callId}",
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextMuted
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Call Target Input Display
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(DarkSurface)
        .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isTextMode) "Enter Username (@handle)" else "Enter 5-Digit Call ID",
          fontSize = 11.sp,
          color = TextSecondary,
          fontFamily = FontFamily.Monospace
        )

        IconButton(
          onClick = { isTextMode = !isTextMode },
          modifier = Modifier.size(24.dp).testTag("toggle_input_mode_btn")
        ) {
          Icon(
            imageVector = if (isTextMode) Icons.Default.Tag else Icons.Default.Keyboard,
            contentDescription = "Switch Input Mode",
            tint = CyanAccent,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      if (isTextMode) {
        OutlinedTextField(
          value = dialerInput,
          onValueChange = onTextInput,
          placeholder = { Text("e.g. sarah_k", color = TextMuted) },
          leadingIcon = { Text("@", color = CyanAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
          trailingIcon = {
            if (dialerInput.isNotEmpty()) {
              IconButton(onClick = onBackspace) {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Clear", tint = TextMuted)
              }
            }
          },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("username_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldPrimary,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )
      } else {
        Text(
          text = if (dialerInput.isEmpty()) "• • • • •" else dialerInput,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 4.sp,
          color = if (dialerInput.isEmpty()) TextMuted else TextPrimary,
          textAlign = TextAlign.Center,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              val clip = clipboardManager.getText()?.text ?: ""
              if (clip.isNotEmpty()) {
                onTextInput(clip.filter { it.isDigit() || it.isLetter() || it == '_' })
              }
            },
            modifier = Modifier.testTag("paste_id_btn")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = TextMuted, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Paste", fontSize = 11.sp, color = TextMuted)
            }
          }

          if (dialerInput.isNotEmpty()) {
            IconButton(
              onClick = onBackspace,
              modifier = Modifier.testTag("backspace_id_btn")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Interactive Keypad (when in numeric mode)
    if (!isTextMode) {
      Dialpad(
        onKeyPress = onDigitPress,
        onKeyLongPress = onDigitLongPress
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Big Green Call Button
    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(CircleShape)
        .background(EmeraldPrimary)
        .clickable {
          view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
          onInitiateCall()
        }
        .testTag("p2p_call_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Call,
        contentDescription = "Start P2P Call",
        tint = Color.Black,
        modifier = Modifier.size(32.dp)
      )
    }
  }
}
