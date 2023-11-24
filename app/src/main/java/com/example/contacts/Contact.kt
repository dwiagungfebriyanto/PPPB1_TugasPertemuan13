package com.example.contacts

import java.io.Serializable

data class Contact(
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = ""
) : Serializable