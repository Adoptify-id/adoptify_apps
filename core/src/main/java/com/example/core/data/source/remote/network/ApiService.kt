package com.example.core.data.source.remote.network

import com.example.core.data.source.remote.response.AddMedicalResponse
import com.example.core.data.source.remote.response.AddPetAdoptResponse
import com.example.core.data.source.remote.response.AddVaksinasiResponse
import com.example.core.data.source.remote.response.AddVirtualPetResponse
import com.example.core.data.source.remote.response.LoginResponse
import com.example.core.data.source.remote.response.MedicalRecordResponse
import com.example.core.data.source.remote.response.PetAdoptResponse
import com.example.core.data.source.remote.response.RegisterResponse
import com.example.core.data.source.remote.response.UserResponse
import com.example.core.data.source.remote.response.VaksinasiResponse
import com.example.core.data.source.remote.response.VirtualPetResponse
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(@FieldMap user: Map<String, Any>) : RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("welbeing/vPet/byUser")
    suspend fun getVirtualPet(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ) : VirtualPetResponse

    @Multipart
    @POST("welbeing/vPet/input")
    suspend fun insertVirtualPet(
        @Header("Authorization") token: String,
        @PartMap virtualPet: Map<String, @JvmSuppressWildcards RequestBody>
    ) : AddVirtualPetResponse

    @GET("record/vaksin/byUser")
    suspend fun getVaksinasi(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ) : VaksinasiResponse

    @FormUrlEncoded
    @POST("record/vaksin/input")
    suspend fun insertVaksinasi(
        @Header("Authorization") token: String,
        @FieldMap vaksinasi: Map<String, Any>
    ) : AddVaksinasiResponse

    @Multipart
    @PUT("user/update/{userId}")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @PartMap user : Map<String, @JvmSuppressWildcards RequestBody>
    ) : UserResponse

    @GET("user/detail")
    suspend fun getDetailProfile(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ) : UserResponse

    @GET("record/medical/byUser")
    suspend fun getMedicalRecord(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ) : MedicalRecordResponse

    @Multipart
    @POST("record/medical/input")
    suspend fun insertMedicalRecord(
        @Header("Authorization") token: String,
        @PartMap medical : Map<String, @JvmSuppressWildcards RequestBody>
    ) : AddMedicalResponse

    @Multipart
    @POST("pet/input")
    suspend fun insertPetAdopt(
        @Header("Authorization") token: String,
        @PartMap petAdopt : Map<String, @JvmSuppressWildcards RequestBody>
    ) : AddPetAdoptResponse

    @GET("pet/")
    suspend fun getListPet(
        @Header("Authorization") token: String,
    ) : PetAdoptResponse

    @GET("pet/detail")
    suspend fun getDetailPet(
        @Header("Authorization") token: String,
        @Query("petId") petId: Int
    ) : PetAdoptResponse

    @GET("foster/byUser")
    suspend fun getPetByUser(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ) : PetAdoptResponse

    @Multipart
    @PUT("pet/update/{petId}")
    suspend fun updatePet(
        @Header("Authorization") token: String,
        @Path("petId") petId: Int,
        @PartMap pet : Map<String, @JvmSuppressWildcards RequestBody>
    ) : PetAdoptResponse

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") token: String
    ) : LoginResponse


}