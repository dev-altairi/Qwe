package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.telephony.CallPhase
import com.example.ui.AppTab
import com.example.ui.VoxFreeViewModel
import com.example.ui.components.ActiveCallOverlay
import com.example.ui.components.IncomingCallOverlay
import com.example.ui.components.PermissionRationaleScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.DialerScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.RecentsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VoxFreeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      VoxFreeTheme {
        VoxFreeApp()
      }
    }
  }
}

@Composable
fun VoxFreeApp(viewModel: VoxFreeViewModel = viewModel()) {
  val context = LocalContext.current
  val selectedTab by viewModel.selectedTab.collectAsState()
  val dialerInput by viewModel.dialerInput.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val contacts by viewModel.contacts.collectAsState()
  val calls by viewModel.calls.collectAsState()
  val activeCallState by viewModel.activeCallState.collectAsState()
  val incomingCall by viewModel.incomingCall.collectAsState()

  // Comprehensive runtime permission state
  var hasMicPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionsLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { _ ->
    hasMicPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun launchPermissionsRequest() {
    val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    }
    permissionsLauncher.launch(permissionsToRequest.toTypedArray())
  }

  fun requestCall() {
    if (hasMicPermission) {
      viewModel.initiateCall()
    } else {
      launchPermissionsRequest()
    }
  }

  // Back handling
  BackHandler(enabled = incomingCall != null || activeCallState.phase != CallPhase.IDLE || selectedTab != AppTab.DIALER) {
    if (incomingCall != null) {
      viewModel.declineIncomingCall()
    } else if (activeCallState.phase != CallPhase.IDLE) {
      viewModel.endCall("User Disconnect")
    } else if (selectedTab != AppTab.DIALER) {
      viewModel.selectTab(AppTab.DIALER)
    }
  }

  // If microphone permission is not granted, block and show the permission explanation screen
  if (!hasMicPermission) {
    PermissionRationaleScreen(
      onRequestPermissions = { launchPermissionsRequest() }
    )
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkBackground,
    contentWindowInsets = WindowInsets.safeDrawing,
    bottomBar = {
      if (activeCallState.phase == CallPhase.IDLE) {
        NavigationBar(
          containerColor = DarkSurface,
          windowInsets = WindowInsets.navigationBars,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          NavigationBarItem(
            selected = selectedTab == AppTab.DIALER,
            onClick = { viewModel.selectTab(AppTab.DIALER) },
            icon = { Icon(Icons.Default.Dialpad, contentDescription = "Dialer") },
            label = { Text("Dialer", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = EmeraldPrimary,
              indicatorColor = EmeraldPrimary,
              unselectedIconColor = TextMuted,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_dialer")
          )

          NavigationBarItem(
            selected = selectedTab == AppTab.CONTACTS,
            onClick = { viewModel.selectTab(AppTab.CONTACTS) },
            icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
            label = { Text("Contacts", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = EmeraldPrimary,
              indicatorColor = EmeraldPrimary,
              unselectedIconColor = TextMuted,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_contacts")
          )

          NavigationBarItem(
            selected = selectedTab == AppTab.RECENTS,
            onClick = { viewModel.selectTab(AppTab.RECENTS) },
            icon = { Icon(Icons.Default.History, contentDescription = "Recents") },
            label = { Text("Recents", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = EmeraldPrimary,
              indicatorColor = EmeraldPrimary,
              unselectedIconColor = TextMuted,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_recents")
          )

          NavigationBarItem(
            selected = selectedTab == AppTab.NETWORK,
            onClick = { viewModel.selectTab(AppTab.NETWORK) },
            icon = { Icon(Icons.Default.Hub, contentDescription = "Network") },
            label = { Text("Network", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              selectedTextColor = EmeraldPrimary,
              indicatorColor = EmeraldPrimary,
              unselectedIconColor = TextMuted,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_network")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        AppTab.DIALER -> {
          DialerScreen(
            dialerInput = dialerInput,
            userProfile = userProfile,
            quickContacts = contacts.take(4),
            onDigitPress = { viewModel.onDigitPress(it) },
            onDigitLongPress = { viewModel.onDigitLongPress(it) },
            onBackspace = { viewModel.onBackspace() },
            onTextInput = { viewModel.onTextInput(it) },
            onSelectPeer = { viewModel.selectPeerToCall(it) },
            onInitiateCall = { requestCall() }
          )
        }

        AppTab.CONTACTS -> {
          ContactsScreen(
            contacts = contacts,
            onCallContact = {
              viewModel.selectPeerToCall(it.callId)
              requestCall()
            },
            onAddContact = { name, username, callId ->
              viewModel.addContact(name, username, callId)
            },
            onDeleteContact = { viewModel.deleteContact(it) }
          )
        }

        AppTab.RECENTS -> {
          RecentsScreen(
            calls = calls,
            onRedial = {
              viewModel.selectPeerToCall(it)
              requestCall()
            },
            onDeleteCall = { viewModel.deleteCallRecord(it) },
            onClearAll = { viewModel.clearAllCalls() }
          )
        }

        AppTab.NETWORK -> {
          val signalingState by viewModel.signalingState.collectAsState()
          val iceState by viewModel.iceState.collectAsState()
          val savedUrl by viewModel.savedSignalingUrl.collectAsState()
          val audioMode by viewModel.audioRoutingMode.collectAsState()
          NetworkScreen(
            userProfile = userProfile,
            currentSignalingUrl = savedUrl,
            audioRoutingMode = audioMode,
            signalingState = signalingState,
            iceState = iceState,
            onUpdateSignalingUrl = { viewModel.saveSignalingServerUrl(it) },
            onUpdateAudioMode = { viewModel.saveAudioRoutingMode(it) },
            onUpdateProfile = { username, displayName ->
              viewModel.updateUserProfile(username, displayName)
            }
          )
        }
      }

      // Active Call Overlay Transition
      AnimatedVisibility(
        visible = activeCallState.phase != CallPhase.IDLE,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        ActiveCallOverlay(
          callState = activeCallState,
          onEndCall = { viewModel.endCall("User Ended Call") },
          onToggleMute = { viewModel.toggleMute() },
          onToggleSpeaker = { viewModel.toggleSpeaker() }
        )
      }

      // Incoming Call Overlay Transition
      AnimatedVisibility(
        visible = incomingCall != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
      ) {
        incomingCall?.let { call ->
          IncomingCallOverlay(
            callerName = call.callerName,
            callerId = call.senderId,
            onAccept = {
              requestCall()
              viewModel.acceptIncomingCall()
            },
            onDecline = { viewModel.declineIncomingCall() }
          )
        }
      }
    }
  }
}
