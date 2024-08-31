package jp.ac.it_college.std.s23020.pokemonapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {
    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Call<Pokemon>
}

