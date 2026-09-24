package com.example.eventmanagement.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val token = com.google.firebase.messaging
                .FirebaseMessaging
                .getInstance()
                .token
                .await()

            saveFcmToken(token)

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveFcmToken(token: String):
            Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(
                    Exception("User is not logged in")
                )
            firestore
                .collection("users")
                .document(user.uid)
                .set(
                    mapOf( "fcmToken" to token ),
                    com.google.firebase.firestore.SetOptions.merge()
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() =
        firebaseAuth.currentUser

    fun logout() {
        firebaseAuth.signOut()
    }
}