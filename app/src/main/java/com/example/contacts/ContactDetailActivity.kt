package com.example.contacts

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.contacts.ContactFormActivity.Companion.EXTRA_CONTACT
import com.example.contacts.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityContactDetailBinding.inflate(layoutInflater)
    }
    // Mendeklarasikan variabel untuk menyimpan contact
    lateinit var contact: Contact
    // Mendeklarasikan variabel untuk TextView pada dialog konfirmasi
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    // Mendeklarasikan launcher
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Memperbarui tampilan jika hasil aktivitas ContactFormActivity adalah OK
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            contact = data?.getSerializableExtra(EXTRA_CONTACT) as Contact

            with(binding) {
                // Mengisi data kontak ke tampilan
                txtFirstLetter.setText(contact.name.substring(0, 1))
                txtName.setText(contact.name)
                txtPhone.setText(contact.phone)
                txtEmail.setText(contact.email)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            // Mengambil objek Contact dari intent
            contact = intent.getSerializableExtra("contact") as Contact
            // Mengisi data kontak ke tampilan
            txtFirstLetter.setText(contact.name.substring(0, 1))
            txtName.setText(contact.name)
            txtPhone.setText(contact.phone)
            txtEmail.setText(contact.email)

            // Menangani klik tombol Edit
            btnEdit.setOnClickListener {
                // Membuat intent untuk ContactFormActivity dengan objek Contact yang akan di-edit
                val intent = Intent(this@ContactDetailActivity, ContactFormActivity::class.java).putExtra("contact", contact)
                // Menjalankan aktivitas ContactFormActivity dengan launcher
                launcher.launch(intent)
            }

            // Membuat objek Dialog untuk konfirmasi penghapusan
            val dialog = Dialog(this@ContactDetailActivity)
            // Menangani klik tombol Delete
            btnDelete.setOnClickListener {
                // Menampilkan dialog konfirmasi penghapusan
                dialog.setContentView(R.layout.dialog_delete)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.window!!.attributes.gravity = Gravity.BOTTOM
                dialog.window!!.attributes.windowAnimations = R.style.animation
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                cancelTxt = dialog.findViewById(R.id.txt_cancel)
                yesTxt = dialog.findViewById(R.id.txt_yes)

                // Menangani klik tombol Cancel pada dialog
                cancelTxt.setOnClickListener {
                    dialog.dismiss()
                }

                // Menangani klik tombol Yes pada dialog
                yesTxt.setOnClickListener {
                    // Menghapus kontak dan menutup activity
                    deleteContact(contact)
                    finish()
                }

                // Menampilkan dialog
                dialog.show()
            }

            // Menangani klik tombol Back
            btnBack.setOnClickListener {
                onBackPressed()
            }

            // Menangani klik tombol Phone
            btnPhone.setOnClickListener {
                // Membuat intent untuk melakukan panggilan telepon
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${contact.phone}")
                startActivity(intent)
            }

            // Menangani klik tombol Message
            btnMessage.setOnClickListener {
                // Membuat intent untuk mengirim pesan SMS
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phone, null))
                startActivity(intent)
            }

            // Menangani klik tombol Email
            btnEmail.setOnClickListener {
                // Membuat intent untuk mengirim email
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:${contact.email}")
                startActivity(intent)
            }
        }
    }

    // Metode untuk menghapus kontak dari Firestore
    private fun deleteContact(contact: Contact) {
        if (contact.id.isEmpty()) {
            Log.d("MainActivity", "Error delete item: contact Id is empty!")
            return
        }
        // Menghapus dokumen kontak dari Firestore berdasarkan ID
        ContactFirebase.contactCollectionRef.document(contact.id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting contact", it)
        }
    }
}