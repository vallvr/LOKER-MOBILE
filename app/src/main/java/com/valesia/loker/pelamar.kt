package com.valesia.loker

data class pelamar(
    val infoloker: infoloker, // Menyimpan objek infoloker
    val namaPelamar: String,
    val emailPelamar: String,
    val linkCV: String,
    var status: String = "Pending" // Status pelamar, bisa "Pending", "Diterima", atau "Tidak Diterima"
) {
    // Nama pekerjaan akan diambil dari objek infoloker
    val namaPekerjaan: String
        get() = infoloker.namaPekerjaan
}
