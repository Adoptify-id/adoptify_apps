package com.example.core.domain.model

data class FormDetail(
    val msg: String? = null,
    val data: List<FormData?>? = null,
    val status: Int? = null
)

data class FormData(
    val umur: Int? = null,
    val ppk1: String? = null,
    val ppk2: String? = null,
    val ppk10: String? = null,
    val rangePendapatan: String? = null,
    val noWa: String? = null,
    val ppk7: String? = null,
    val ppk8: String? = null,
    val ppk9: String? = null,
    val ppk3: String? = null,
    val ppk4: String? = null,
    val ppk5: String? = null,
    val ppk6: String? = null,
    val umum4: String? = null,
    val umum5: String? = null,
    val umum6: String? = null,
    val umum1: String? = null,
    val umum2: String? = null,
    val umum3: String? = null,
    val reqId: Int? = null,
    val pekerjaan: String? = null,
    val medsos: String? = null,
    val rl1: String? = null,
    val name: String? = null,
    val rl3: String? = null,
    val rl2: String? = null,
    val rl5: String? = null,
    val rl4: String? = null,
    val domisili: String? = null,
    val kartuIdentitas: String? = null,
    val statusReqId: Int? = null,
    val statusPaymentId: Int? = null,
    val statusPickupId: Int? = null,
    val kodePengajuan: String? = null,
    val buktiPickup: String? = null,
    val suratKomitmen: String? = null,
    val transfer: String? = null,
    val kategori: String? = null
)