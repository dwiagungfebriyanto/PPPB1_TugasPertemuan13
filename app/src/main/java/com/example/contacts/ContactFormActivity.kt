package com.example.contacts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.contacts.databinding.ActivityContactFormBinding

class ContactFormActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityContactFormBinding.inflate(layoutInflater)
    }
    // Mendeklarasikan companion object untuk menyimpan nama ekstra pada intent
    companion object {
        const val EXTRA_CONTACT = "extra_contact"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            // Memeriksa apakah ada ekstra "contact" dalam intent
            if (intent.hasExtra("contact")) {
                // Jika ada, berarti ini adalah mode pengeditan kontak
                val contact = intent.getSerializableExtra("contact") as Contact
                // Mengisi EdetText dengan data kontak yang ada
                edtName.setText(contact.name)
                edtPhone.setText(contact.phone)
                edtEmail.setText(contact.email)

                btnSave.setOnClickListener {
                    // Membuat objek Contact yang telah diubah
                    val editedContact = Contact(
                        id = contact.id,
                        name = edtName.text.toString(),
                        phone = edtPhone.text.toString(),
                        email = edtEmail.text.toString()
                    )
                    // Memastikan Id contact tidak kosong sebelum memperbarui kontak
                    if (contact.id.isNotEmpty()) {
                        // Memperbarui kontak di database
                        updateContact(editedContact)
                        val intent = Intent()
                        intent.putExtra(EXTRA_CONTACT, editedContact)
                        setResult(Activity.RESULT_OK, intent)
                        // Menutup activity FormContactActivity
                        finish()
                    }
                }
            } else {
                // Jika tidak ada ekstra "contact", berarti ini adalah mode pembuatan kontak baru
                btnSave.setOnClickListener {
                    // Memeriksa apakah semua input telah diisi
                    if (edtName.text.toString() != "" && edtPhone.text.toString() != "" && edtEmail.text.toString() != "") {
                        // Jika ya, membuat objek Contact baru
                        val newContact = Contact(
                            name = edtName.text.toString(),
                            phone = edtPhone.text.toString(),
                            email = edtEmail.text.toString()
                        )
                        // Menyimpan kontak baru ke database
                        addContact(newContact)
                    } else {
                        // Jika ada input yang kosong, tampilkan pesan kesalahan
                        Toast.makeText(this@ContactFormActivity, "Something is empty.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Menangani klik tombol Cancel
            btnCancel.setOnClickListener {
                onBackPressed()
            }
        }
    }

    // Metode untuk menambahkan kontak baru ke Firestore
    private fun addContact(contact: Contact) {
        ContactFirebase.contactCollectionRef.add(contact).addOnSuccessListener { documentReference ->
            // Mendapatkan ID yang dibuat oleh Firestore
            val createdContactId = documentReference.id
            contact.id = createdContactId
            // Menyimpan kembali ID ke dokumen kontak
            documentReference.set(contact).addOnSuccessListener {
                // Membuka activity DetailContactActivity
                startActivity(
                    Intent(this@ContactFormActivity, ContactDetailActivity::class.java)
                        .putExtra("contact", contact)
                )
                // Menutup activity FormContactActivity
                finish()
            }.addOnFailureListener {
                Log.d("ContactFormActivity", "Error updating contact id: ", it)
            }
        }.addOnFailureListener {
            Log.d("ContactFormActivity", "Error adding contact id: ", it)
        }
    }

    // Metode untuk memperbarui kontak yang ada di Firestore
    private fun updateContact(contact: Contact) {
        ContactFirebase.contactCollectionRef.document(contact.id).set(contact).addOnFailureListener {
            Log.d("ContactFormActivity", "Error updating contact", it)
        }
    }
}