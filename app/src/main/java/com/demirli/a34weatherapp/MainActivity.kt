package com.demirli.a34weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private var cityList = ArrayList<String>()
    private var cityKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityName_et.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                try {
                    if(s.toString() != ""){
                        val download = TaskForAutoComplate()
                        val url = "https://dataservice.accuweather.com/locations/v1/cities/autocomplete?apikey=${Constants.ACCUWEATHER_API_KEY}&q=${s.toString()}"
                        download.execute(url)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        getWeather_btn.setOnClickListener {
            getWeather()
        }
    }

    fun getWeather() {
        try {
            val download = TaskForCurrentConditions()
            //val key = "182536"
            val url = "https://dataservice.accuweather.com/currentconditions/v1/${cityKey}?apikey=${Constants.ACCUWEATHER_API_KEY}"
            download.execute(url)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    inner class TaskForAutoComplate(): AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            var result = ""

            var url: URL

            var httpURLConnection: HttpURLConnection

            url = URL(params[0])

            httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            var data = inputStreamReader.read()

            while (data > 0) {
                val character = data.toChar()
                result += character

                data = inputStreamReader.read()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setAutoComplateAdapter(result)
        }
    }

    fun setAutoComplateAdapter(result: String?){
        cityList.clear()
        val jsonArray = JSONArray(result)

        for (i in 0 until jsonArray.length()){
            val jsonObject = JSONObject(jsonArray.getString(i))
            cityList.add(jsonObject.getString("LocalizedName"))
            cityKey = jsonObject.getString("Key")
        }

        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1,cityList)
        cityName_et.setAdapter(adapter)
    }

    inner class TaskForCurrentConditions(): AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            var result = ""

            var url: URL

            var httpURLConnection: HttpURLConnection

            url = URL(params[0])

            httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            var data = inputStreamReader.read()

            while (data > 0) {
                val character = data.toChar()
                result += character

                data = inputStreamReader.read()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setConditions(result)
        }
    }

    fun setConditions(result: String?){
        val jsonArray = JSONArray(result)
        val jsonObject = JSONObject(jsonArray.getString(0))

        val jsonObjectTemperature = JSONObject( jsonObject.getString("Temperature"))
        val jsonObjectMetric = JSONObject(jsonObjectTemperature.getString("Metric"))

        val jsonObjectMetricTemperature = jsonObjectMetric.getString("Value")
        val weatherCondition = jsonObject.getString("WeatherText")
        val weatherIconNumber = jsonObject.getString("WeatherIcon")

        temprature_tv.setText("Temprature: " + jsonObjectMetricTemperature + "° C")
        weatherCondition_tv.setText("Weather Condition: " + weatherCondition)

        val weatherIconNumberEdittedFormat ="%02d".format(weatherIconNumber.toInt())
        Picasso.get().load("https://developer.accuweather.com/sites/default/files/${weatherIconNumberEdittedFormat}-s.png").into(weatherIcon_iv)
    }

}
