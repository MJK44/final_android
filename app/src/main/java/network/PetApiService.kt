package network

import model.PetResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PetApiService {
    @GET("animals")
    fun listPets(
        @Header("Authorization") authHeader: String,
        @Query("type") type: String,  // Specify the animal type dynamically when making the call
        @Query("limit") limit: Int
    ): Call<PetResponse>
}