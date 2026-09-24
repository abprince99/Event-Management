package com.example.eventmanagement.events.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.eventmanagement.events.data.models.Events
import com.example.eventmanagement.events.ui.EventActivity
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun eventsCollection() =
        firestore
            .collection("users")
            .document(auth.currentUser!!.uid)
            .collection("events")

    suspend fun createEvent(event: Events): Result<Unit> {

        return try {

            val document = eventsCollection().document()

            val eventWithId = event.copy(
                id = document.id
            )

            document.set(eventWithId).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    fun listenToEvents(
        onSuccess: (List<Events>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {

        val user = auth.currentUser ?: return null

        return firestore
            .collection("users")
            .document(user.uid)
            .collection("events")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val events = snapshot.documents.mapNotNull { document ->
                        document.toObject(Events::class.java)
                    }

                    onSuccess(events)
                }
            }
    }

    suspend fun updateEvent(event: Events): Result<Unit> {
        return try {
            eventsCollection()
                .document(event.id)
                .set(event)
                .await()
            Result.success (Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection()
                .document(eventId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}