package com.shyamanand.bookworm.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.shyamanand.bookworm.TAG

class FireStoreDatabase {

    private val firestore = FirebaseFirestore.getInstance()

    fun add(data: Any, collection: FirestoreCollection) {
        firestore.collection(collection.collectionName)
            .add(data)
            .addOnSuccessListener {
                Log.d(TAG, "data added with ID: ${it.id}")
            }
            .addOnFailureListener {
                Log.d(TAG, "Error adding document: ${it.message} -- ${it.stackTraceToString()}")
            }
    }

    fun get(collection: FirestoreCollection) {
        firestore.collection(collection.collectionName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.forEach { document ->
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                } else {
                    Log.w(TAG, "Error getting documents: \n" +
                            task.exception?.stackTraceToString()
                    )
                }
            }
    }
}