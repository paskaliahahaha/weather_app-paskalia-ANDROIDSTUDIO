package com.example.weatherapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var weatherApi: WeatherApi
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var cityNameTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var weatherIconImageView: ImageView

    private val apiKey = "d8c2cba5521e7ee01adf9f02c3797281"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        cityNameTextView = findViewById(R.id.cityName)
        temperatureTextView = findViewById(R.id.temperature)
        descriptionTextView = findViewById(R.id.description)
        weatherIconImageView = findViewById(R.id.weatherIcon)

        getWeatherData("Manado")


        searchButton.setOnClickListener {
            val cityName = searchEditText.text.toString()
            if (cityName.isNotEmpty()) {
                getWeatherData(cityName)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherData(city: String) {
        val call = weatherApi.getCurrentWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()

                    weatherData?.let {
                        displayWeather(it)
                    }
                } else {

                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to fetch weather data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun displayWeather(weatherData: WeatherData) {
        cityNameTextView.text = weatherData.name
        val temperature = (weatherData.main.temp - 273.15).toInt()
        temperatureTextView.text = "$temperature°C"
        descriptionTextView.text = weatherData.weather[0].description

        val iconCode = weatherData.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"
        Picasso.get().load(iconUrl).into(weatherIconImageView)
    }
}
