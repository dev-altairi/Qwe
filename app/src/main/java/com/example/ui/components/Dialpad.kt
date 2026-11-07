package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

data class DialKey(
  val digit: Char,
  val subtext: String = ""
)

@Composable
fun Dialpad(
  modifier: Modifier = Modifier,
  onKeyPress: (Char) -> Unit,
  onKeyLongPress: (Char) -> Unit = {}
) {
  val rows = listOf(
    listOf(DialKey('1', ""), DialKey('2', "A B C"), DialKey('3', "D E F")),
    listOf(DialKey('4', "G H I"), DialKey('5', "J K L"), DialKey('6', "M N O")),
    listOf(DialKey('7', "P Q R S"), DialKey('8', "T U V"), DialKey('9', "W X Y Z")),
    listOf(DialKey('*', ""), DialKey('0', "+"), DialKey('#', ""))
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    for (row in rows) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (key in row) {
          DialpadButton(
            key = key,
            onClick = { onKeyPress(key.digit) },
            onLongClick = { onKeyLongPress(key.digit) }
          )
        }
      }
    }
  }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DialpadButton(
  key: DialKey,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val view = LocalView.current

  Box(
    modifier = modifier
      .size(72.dp)
      .clip(CircleShape)
      .background(DarkSurfaceVariant)
      .border(1.dp, DarkBorder, CircleShape)
      .combinedClickable(
        onClick = {
          view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
          onClick()
        },
        onLongClick = {
          view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
          onLongClick()
        }
      )
      .testTag("dialpad_key_${key.digit}"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = key.digit.toString(),
        fontSize = 26.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )
      if (key.subtext.isNotEmpty()) {
        Spacer(modifier = Modifier.height(1.dp))
        Text(
          text = key.subtext,
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 1.sp,
          color = TextMuted
        )
      }
    }
  }
}
