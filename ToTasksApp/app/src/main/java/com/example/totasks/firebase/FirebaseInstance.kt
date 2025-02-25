package nha.tu.tup.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import javax.inject.Singleton

class FirebaseInstance {
    @Singleton
    companion object{
        val firebaseFirestoreInstance = FirebaseFirestore.getInstance()

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
    }
}