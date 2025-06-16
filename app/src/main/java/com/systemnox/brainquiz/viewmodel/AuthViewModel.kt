package com.systemnox.brainquiz.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _user = mutableStateOf<FirebaseUser?>(null)
    val user: State<FirebaseUser?> get() = _user
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _authMessage = mutableStateOf<String?>(null)
    val authMessage: State<String?> get() = _authMessage

    init {
        _user.value = FirebaseAuth.getInstance().currentUser
    }

    // Register new user
    fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    _user.value = firebaseUser

                    val userMap = hashMapOf(
                        "uid" to (firebaseUser?.uid ?: ""),
                        "name" to name,
                        "email" to email,
                        "created_at" to FieldValue.serverTimestamp()
                    )

                    firebaseUser?.uid?.let { uid ->
                        firestore.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                _authMessage.value = "Registration successful"
                            }
                            .addOnFailureListener { e ->
                                _authMessage.value = "User registered but failed to save profile: ${e.message}"
                            }
                    }

                } else {
                    _authMessage.value = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _authMessage.value = e.localizedMessage
            }
    }

//    login user
    fun login(email: String, password: String) {
        _isLoading.value = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = FirebaseAuth.getInstance().currentUser

                } else {
                    _authMessage.value = task.exception?.localizedMessage
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _authMessage.value = e.localizedMessage
            }
    }

    fun sendPasswordReset(email: String) {
        _isLoading.value = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _authMessage.value = "Reset link sent to your email"
                } else {
                    _authMessage.value = task.exception?.localizedMessage ?: "Failed to send reset link"
                }
            }.addOnFailureListener { e ->
                _isLoading.value = false
                _authMessage.value = e.localizedMessage
            }
    }


    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _user.value = null
    }

    fun clearMessage() {
        _authMessage.value = null
    }
}
