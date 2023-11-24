package com.example.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private lateinit var adapterContact: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Menginisialisasi adapterContact dengan daftar kontak kosong
        adapterContact = ContactAdapter(emptyList()) { contact ->
            // Menavigasi ke ContactDetailActivity ketika item kontak diklik
            startActivity(
                Intent(this@SearchActivity, ContactDetailActivity::class.java)
                    .putExtra("contact", contact)
            )
        }

        with(binding) {
            // Mengatur RecyclerView dengan adapterContact dan layout manager
            rvContact.apply {
                adapter = adapterContact
                layoutManager = LinearLayoutManager(this@SearchActivity)
            }

            // Menambahkan TextWatcher ke EditText untuk mendeteksi perubahan teks saat melakukan pencarian
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // Ketika teks berubah, panggil metode untuk memperbarui hasil pencarian
                    updateSearchResults(s.toString())
                }
            })

            // Meminta fokus pada EditText
            edtSearch.requestFocus()

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    // Metode untuk memperbarui hasil pencarian
    private fun updateSearchResults(keyword: String) {
        val searchResults = ArrayList<Contact>()
        // Mengecek apakah keyword tidak kosong
        if (keyword.isNotEmpty()) {
            ContactFirebase.contactListLiveData.observe(this) { contacts ->
                for (contact in contacts) {
                    // Mencari bernasarkan atribut nama
                    if (contact.name.contains(keyword, ignoreCase = true)) {
                        searchResults.add(contact)
                    }
                }
            }
        }

        // Update RecyclerView dengan hasil pencarian
        val listSize = adapterContact.updateData(searchResults)
        // Atur visibilitas RecyclerView berdasarkan apakah ada hasil pencarian atau tidak
        binding.rvContact.visibility = if (listSize > 0) View.VISIBLE else View.GONE
    }
}