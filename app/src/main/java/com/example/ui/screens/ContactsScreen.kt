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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactRecord
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
fun ContactsScreen(
  contacts: List<ContactRecord>,
  onCallContact: (ContactRecord) -> Unit,
  onAddContact: (String, String, String) -> Unit,
  onDeleteContact: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  val filteredContacts = remember(searchQuery, contacts) {
    if (searchQuery.isBlank()) {
      contacts
    } else {
      contacts.filter {
        it.displayName.contains(searchQuery, ignoreCase = true) ||
          it.username.contains(searchQuery, ignoreCase = true) ||
          it.callId.contains(searchQuery)
      }
    }
  }

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
          text = "Peer Contacts",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "${contacts.size} connected P2P callers",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }

      Button(
        onClick = { showAddDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("add_contact_modal_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Peer", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("contacts_search_input"),
      placeholder = { Text("Search by name, @username, or 5-digit ID…", color = TextMuted) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = DarkBorder,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredContacts, key = { it.id }) { contact ->
        ContactItem(
          contact = contact,
          onCall = { onCallContact(contact) },
          onDelete = { onDeleteContact(contact.id) }
        )
      }
    }
  }

  if (showAddDialog) {
    AddContactDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, username, callId ->
        onAddContact(name, username, callId)
        showAddDialog = false
      }
    )
  }
}

@Composable
fun ContactItem(
  contact: ContactRecord,
  onCall: () -> Unit,
  onDelete: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurface)
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .clickable { onCall() }
      .padding(14.dp)
      .testTag("contact_item_${contact.callId}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(contact.avatarColorHex)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = contact.displayName.take(1).uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = contact.displayName,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
            if (contact.isOnline) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(EmeraldLight)
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "@${contact.username}",
              fontSize = 12.sp,
              color = CyanAccent
            )
            Text(
              text = " • Call ID: ${contact.callId}",
              fontSize = 11.sp,
              color = TextMuted,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onCall,
          modifier = Modifier.testTag("call_contact_${contact.callId}")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            tint = EmeraldPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier.testTag("delete_contact_${contact.callId}")
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

@Composable
fun AddContactDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, username: String, callId: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }
  var callId by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Peer Contact", color = TextPrimary) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Display Name") },
          placeholder = { Text("e.g. Jordan Lee") },
          singleLine = true,
          modifier = Modifier.testTag("add_contact_name_input")
        )
        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Username Handle") },
          placeholder = { Text("e.g. jordan_l") },
          singleLine = true,
          modifier = Modifier.testTag("add_contact_username_input")
        )
        OutlinedTextField(
          value = callId,
          onValueChange = { if (it.length <= 5) callId = it },
          label = { Text("5-Digit Call ID") },
          placeholder = { Text("e.g. 54321") },
          singleLine = true,
          modifier = Modifier.testTag("add_contact_id_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && callId.isNotBlank()) {
            onConfirm(name.trim(), username.trim().ifEmpty { name.lowercase().replace(" ", "_") }, callId.trim())
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        modifier = Modifier.testTag("confirm_add_contact_btn")
      ) {
        Text("Save Contact", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    },
    containerColor = DarkSurface
  )
}
