package jp.ac.it_college.std.s23020.pokemonapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var pokemon1Name: EditText
    private lateinit var pokemon2Name: EditText
    private lateinit var compareButton: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokemon1Name = findViewById(R.id.pokemon1_name)
        pokemon2Name = findViewById(R.id.pokemon2_name)
        compareButton = findViewById(R.id.compare_button)
        resultText = findViewById(R.id.result_text)

        compareButton.setOnClickListener {
            val pokemon1 = pokemon1Name.text.toString().trim()
            val pokemon2 = pokemon2Name.text.toString().trim()

            // デバッグ用ログ
            Log.d("MainActivity", "ポケモン1の名前: $pokemon1")
            Log.d("MainActivity", "ポケモン2の名前: $pokemon2")

            if (pokemon1.isNotEmpty() && pokemon2.isNotEmpty()) {
                comparePokemonWeights(pokemon1, pokemon2)
            } else {
                resultText.text = "ポケモンの名前を入力してください。"
            }
        }
    }

    private fun comparePokemonWeights(pokemon1: String, pokemon2: String) {
        val apiService = RetrofitClient.apiService

        val call1 = apiService.getPokemon(pokemon1)
        val call2 = apiService.getPokemon(pokemon2)

        call1.enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful) {
                    val pokemon1Weight = response.body()?.weight ?: 0
                    Log.d("MainActivity", "ポケモン1の重さ: $pokemon1Weight")

                    call2.enqueue(object : Callback<Pokemon> {
                        override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                            if (response.isSuccessful) {
                                val pokemon2Weight = response.body()?.weight ?: 0
                                Log.d("MainActivity", "ポケモン2の重さ: $pokemon2Weight")

                                val result = when {
                                    pokemon1Weight > pokemon2Weight -> "$pokemon1 の方が重いです。"
                                    pokemon1Weight < pokemon2Weight -> "$pokemon2 の方が重いです。"
                                    else -> "両方のポケモンの重さは同じです。"
                                }
                                resultText.text = result
                            } else {
                                Log.e("MainActivity", "ポケモン2の情報を取得できませんでした: ${response.errorBody()?.string()}")
                                resultText.text = "ポケモン2の情報を取得できませんでした。"
                            }
                        }

                        override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                            Log.e("MainActivity", "ポケモン2の情報取得に失敗しました", t)
                            resultText.text = "ネットワークエラーが発生しました。"
                        }
                    })
                } else {
                    Log.e("MainActivity", "ポケモン1の情報を取得できませんでした: ${response.errorBody()?.string()}")
                    resultText.text = "ポケモン1の情報を取得できませんでした。"
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Log.e("MainActivity", "ポケモン1の情報取得に失敗しました", t)
                resultText.text = "ネットワークエラーが発生しました。"
            }
        })
    }
}
