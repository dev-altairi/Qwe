package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.NetworkSettingsRepository
import com.example.data.repository.VoxFreeRepository
import com.example.telephony.webrtc.IceCandidatePayload
import com.example.telephony.webrtc.SessionDescriptionPayload
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VoxFree", appName)
  }

  @Test
  fun `verify user profile generated with 5-digit call id`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = VoxFreeRepository(context)
    val profile = repository.userProfile.value
    assertNotNull(profile)
    assertEquals(5, profile.callId.length)
  }

  @Test
  fun `verify signaling json payloads serialization`() {
    val candidate = IceCandidatePayload(
      sdpMid = "audio",
      sdpMLineIndex = 0,
      candidate = "candidate:1 1 UDP 2122260223 192.168.1.105 54228 typ host"
    )
    val json = candidate.toJson()
    assertEquals("candidate", json.getString("type"))
    assertEquals("audio", json.getString("sdpMid"))

    val parsed = IceCandidatePayload.fromJson(json)
    assertEquals(candidate.candidate, parsed.candidate)

    val sdp = SessionDescriptionPayload("offer", "v=0\r\no=...")
    val sdpJson = sdp.toJson()
    assertEquals("offer", sdpJson.getString("type"))
  }

  @Test
  fun `verify network settings repository persists custom websocket url on cold start`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo1 = NetworkSettingsRepository(context)
    val customUrl = "ws://192.168.1.150:8080/ws"
    repo1.saveSignalingUrl(customUrl)
    repo1.saveAudioRoutingMode("In-Call Mode")

    // Simulate app cold restart with a new repository instance
    val repo2 = NetworkSettingsRepository(context)
    assertEquals(customUrl, repo2.getSignalingUrlSync())
    assertEquals(customUrl, repo2.signalingUrl.value)
    assertEquals("In-Call Mode", repo2.audioRoutingMode.value)
  }
}
