package network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OAuthService {
    @FormUrlEncoded
    @POST("oauth2/token")
    fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<OAuthTokenResponse>
}

data class OAuthTokenResponse(val access_token: String)