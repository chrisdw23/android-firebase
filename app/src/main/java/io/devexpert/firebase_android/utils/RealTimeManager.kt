package io.devexpert.firebase_android.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.devexpert.firebase_android.model.Contact
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RealTimeManager {
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("contacts")

    suspend fun addContact(contact: Contact) {
        val key = databaseReference.push().key ?: return
        val newContact = contact.copy(id = key)
        databaseReference.child(key).setValue(newContact).await()
    }

    suspend fun deleteContact(contactId: String) {
        databaseReference.child(contactId).removeValue().await()
    }

    fun getContactsFlow(uid: String): Flow<List<Contact>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contacts = snapshot.children.mapNotNull { item ->
                    item.getValue(Contact::class.java)
                }.filter { it.uid == uid }
                trySend(contacts)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        databaseReference.addValueEventListener(listener)
        awaitClose { databaseReference.removeEventListener(listener) }
    }
}