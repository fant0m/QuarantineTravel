package com.example.quarantinetravel.api

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quarantinetravel.util.Generator
import org.json.JSONArray
import org.json.JSONObject


class KiwiApi constructor(context: Context) {
    private var queue = HttpQueue.getInstance(
        context.applicationContext
    )
    private lateinit var popularLocations : JSONArray

    companion object {
        const val API_URL = "http://api.skypicker.com"
        const val LOCATION_URL = "/locations?"
        const val FLIGHTS_URL = "/flights?"
    }

    fun getLocations (term: String, responseListener: ResponseListener) {
        val url = API_URL + LOCATION_URL + "term=${term}&locale=en-US&location_types=airport&limit=10&active_only=true&sort=name"

        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            responseListener.onFetchResponse(response)
        }, Response.ErrorListener {
            responseListener.onFetchResponse(null)
        })

        queue.addToRequestQueue(request)
    }

    fun getFlights (from: String, to: String, dateFrom: String, dateTo: String, directFlight: Int, limit: Int, responseListener: ResponseListener) {
        val url = API_URL + FLIGHTS_URL + "fly_from=${from}&fly_to=${to}&v=3&date_from=${dateFrom}&date_to=${dateTo}&direct_flights=${directFlight}&limit=${limit}&vehicle_type=aircraft&partner=picky"

        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            responseListener.onFetchResponse(response)
        }, Response.ErrorListener {
            responseListener.onFetchResponse(null)
        })

        queue.addToRequestQueue(request)
    }

    fun getPopularLocations (number: Int, responseListener : ResponseListener) {
        if (this::popularLocations.isInitialized) {
            return getRandomPopularLocations(number, responseListener)
        }

        val terms = arrayOf("london_gb", "new-york-city_ny_us", "moscow_cf_ru", "singapore_sg")
        val randomTerm = terms.random()

        val url = API_URL + LOCATION_URL+ "type=top_destinations&term=$randomTerm&locale=en-US&limit=100&sort=sort=name&active_only=true&source_popularity=searches"
        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            if (response == null) {
                responseListener.onFetchResponse(null)
            } else {
                popularLocations = response.getJSONArray("locations")
                getRandomPopularLocations(number, responseListener)
            }
        }, Response.ErrorListener {
            responseListener.onFetchResponse(null)
        })

        queue.addToRequestQueue(request)
    }

    private fun getRandomPopularLocations (number: Int, responseListener : ResponseListener) {
        val airports = JSONArray()
        val locations = JSONObject()
        val locationsLength = popularLocations.length()
        if (locationsLength >= number) {
            val chosenIndexes = IntArray(number)
            var filled = 0

            while (filled < number) {
                val randomIndex = Generator.randomNumber(0, locationsLength - 1)
                if (!chosenIndexes.contains(randomIndex + 1)) {
                    val location: JSONObject = popularLocations[randomIndex] as JSONObject

                    airports.put(location)

                    chosenIndexes[filled] = randomIndex + 1
                    filled++
                }
            }

            locations.put("airports", airports)
            responseListener.onFetchResponse(locations)
        } else {
            responseListener.onFetchResponse(null)
        }
    }
}
