package com.example.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ItemContactBinding

// Membuat alias untuk tipe data fungsi onClickContact
typealias OnClickContact = (Contact) -> Unit

class ContactAdapter(
    private var listContact: List<Contact>,
    private val onClickContact: OnClickContact
) : RecyclerView.Adapter<ContactAdapter.ItemContactViewHolder>() {

    // Metode untuk memperbarui data kontak dan memberi tahu adaptor
    fun updateData(newContacts: List<Contact>): Int {
        listContact = newContacts
        notifyDataSetChanged()
        return listContact.size
    }

    inner class ItemContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Metode untuk mengikat data kontak ke tampilan
        fun bind(data: Contact) {
            with(binding) {
                txtFirstLetter.text = data.name.substring(0, 1)
                txtName.text = data.name

                // Menangani klik item kontak
                itemView.setOnClickListener {
                    onClickContact(data)
                }

                // Menyembunyikan divider untuk item terakhir dalam daftar
                if (adapterPosition == listContact.size - 1) {
                    divider.visibility = View.GONE
                } else {
                    divider.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listContact.size
    }

    override fun onBindViewHolder(holder: ItemContactViewHolder, position: Int) {
        holder.bind(listContact[position])
    }
}