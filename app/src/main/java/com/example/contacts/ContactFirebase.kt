package com.example.contacts

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

// Object ContactFirebase berfungsi sebagai objek tunggal untuk mengelola komunikasi dengan Firebase Firestore
object ContactFirebase {
    // Referensi ke koleksi kontak di Firestore
    val contactCollectionRef = FirebaseFirestore.getInstance().collection("contacts")
    // MutableLiveData untuk daftar kontak yang akan diobservasi oleh activity
    val contactListLiveData : MutableLiveData<List<Contact>> by lazy {
        MutableLiveData<List<Contact>>()
    }

    // Metode untuk mendapatkan semua kontak dan memulai pengamatan perubahan
    fun gelAllContacts() {
        observeContactChanges()
    }

    // Metode untuk mengamati perubahan pada koleksi kontak di Firestore
    fun observeContactChanges() {
        // Menambahkan pendengar untuk snapshot (perubahan) pada koleksi kontak
        contactCollectionRef.addSnapshotListener { snapshots, error ->
            // Memeriksa apakah terdapat kesalahan
            if (error != null) {
                Log.d("ContactFirebase", "Error listening for contact changes:", error)
            }
            // Mengonversi setiap snapshot ke objek Contact
            val contacts = snapshots?.toObjects(Contact::class.java)
            // Memeriksa apakah daftar kontak tidak null
            if (contacts != null) {
                // Menyortir daftar kontak berdasarkan atribut nama secara alfabetis (ignore case)
                val sortedContacts = contacts.sortedBy { it.name.toLowerCase() }
                // Memperbarui MutableLiveData dengan daftar kontak yang baru
                contactListLiveData.postValue(sortedContacts)
            }
        }
    }
}