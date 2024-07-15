package com.example.core.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.core.data.source.local.entity.PetEntity

@Dao
interface PetsDao {
    @Query("SELECT * FROM pet WHERE favorite = 1")
    fun getFavoritePets(): LiveData<List<PetEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPet(pet: PetEntity)

    @Delete
    suspend fun deletePet(pet: PetEntity)
}