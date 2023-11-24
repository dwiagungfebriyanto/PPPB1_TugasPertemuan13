package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            // Menangani klik tombol untuk menambahkan kontak baru
            btnAddContact.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, ContactFormActivity::class.java)
                )
            }
            // Menangani klik tombol untuk mencari kontak
            btnSearchContact.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, SearchActivity::class.java)
                )
            }
        }

        // Memanggil metode untuk mengamati perubahan pada daftar kontak
        observeContacts()
        // Memanggil metode untuk mengambil semua kontak dari Firebase Firestore
        ContactFirebase.gelAllContacts()
    }

    // Metode untuk mengamati perubahan pada daftar kontak
    private fun observeContacts() {
        ContactFirebase.contactListLiveData.observe(this) { contacts ->
            // Membuat adapter kontak dengan menggunakan data terbaru
            val adapterContact = ContactAdapter(contacts) { contact ->
                // Membuka activity detail kontak saat kontak diklik
                startActivity(
                    Intent(this@MainActivity, ContactDetailActivity::class.java)
                        .putExtra("contact", contact)
                )
            }
            // Menerapkan adapter ke RecyclerView
            binding.rvContact.apply {
                adapter = adapterContact
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }
}