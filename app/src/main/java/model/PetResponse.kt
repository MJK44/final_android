package model

import com.google.gson.annotations.SerializedName

data class PetResponse(
    @SerializedName("animals") val animals: List<Animal>,
    @SerializedName("pagination") val pagination: Pagination
)

data class Animal(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("location") val location: String,
    @SerializedName("breeds") val breeds: Breeds,
    @SerializedName("age") val age: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("size") val size: String,
    @SerializedName("coat") val coat: String?,
    @SerializedName("attributes") val attributes: Attributes,
    @SerializedName("photos") val photos: List<Photo>?,
    @SerializedName("description") val description: String?,
    var isFavorited: Boolean = false // Added to track favorite status
)

data class Breeds(
    @SerializedName("primary") val primary: String
)

data class Attributes(
    @SerializedName("spayed_neutered") val spayedNeutered: Boolean,
    @SerializedName("house_trained") val houseTrained: Boolean
)

data class Photo(
    @SerializedName("small") val small: String?,
    @SerializedName("medium") val medium: String?,
    @SerializedName("large") val large: String?
)

data class Pagination(
    @SerializedName("count_per_page") val countPerPage: Int,
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_pages") val totalPages: Int
)
