package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.Item

object ItemDummyData {
    val items = listOf(
        Item(1, "Item Pertama", "Deskripsi untuk item pertama."),
        Item(2, "Item Kedua", "Deskripsi untuk item kedua."),
        Item(3, "Item Ketiga", "Deskripsi untuk item ketiga."),
        Item(4, "Item Keempat", "Deskripsi untuk item keempat."),
        Item(5, "Item Kelima", "Deskripsi untuk item kelima.")
    )

    fun getById(id: Int): Item? = items.find { it.id == id }
}
