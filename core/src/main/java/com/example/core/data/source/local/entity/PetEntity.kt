package com.example.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name_pet")
    val namePet: String,

    @ColumnInfo(name = "fotoPet")
    val fotoPet: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "age")
    val age: Int,

    @ColumnInfo(name = "ras")
    val ras: String,

    @ColumnInfo(name="isAdopt")
    val isAdopt: Boolean? = null,

    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean? = null
)