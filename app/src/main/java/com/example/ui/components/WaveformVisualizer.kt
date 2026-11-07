package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun WaveformVisualizer(
  modifier: Modifier = Modifier,
  isActive: Boolean = true,
  isMuted: Boolean = false,
  liveAmplitudes: List<Float>? = null,
  barCount: Int = 24
) {
  val transition = rememberInfiniteTransition(label = "wave_anim")
  val pulse by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_float"
  )

  val waveBrush = Brush.verticalGradient(
    colors = if (isMuted) {
      listOf(Color(0xFF64748B), Color(0xFF334155))
    } else {
      listOf(CyanAccent, EmeraldPrimary, EmeraldLight)
    }
  )

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
  ) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val totalBarWidth = canvasWidth / barCount
    val barWidth = totalBarWidth * 0.55f

    for (i in 0 until barCount) {
      val dynamicFactor = if (isActive && !isMuted) {
        if (liveAmplitudes != null && i < liveAmplitudes.size) {
          liveAmplitudes[i]
        } else {
          val centerDist = kotlin.math.abs(i - barCount / 2f) / (barCount / 2f)
          val baseHeightFactor = (1.0f - centerDist * 0.6f).coerceIn(0.15f, 1.0f)
          val phase = kotlin.math.sin((i * 0.45f) + (pulse * 3.1415f))
          baseHeightFactor * (0.35f + 0.65f * kotlin.math.abs(phase))
        }
      } else {
        0.1f
      }

      val barHeight = (canvasHeight * dynamicFactor).coerceAtLeast(4.dp.toPx())
      val top = (canvasHeight - barHeight) / 2f
      val left = i * totalBarWidth + (totalBarWidth - barWidth) / 2f

      drawRoundRect(
        brush = waveBrush,
        topLeft = Offset(left, top),
        size = Size(barWidth, barHeight),
        cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
      )
    }
  }
}
