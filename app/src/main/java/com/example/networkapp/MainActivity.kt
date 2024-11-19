package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

add // TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize file
        file = File(filesDir, "saved_comic.json")

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        // Automatically load saved comic if exists using BufferedReader
        if (file.exists()) {
            try {
                val bufferedReader = file.bufferedReader()
                val savedJson = bufferedReader.use { it.readText() }
                showComic(JSONObject(savedJson))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString()
            if (comicId.isNotEmpty()) {
                downloadComic(comicId)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val request = JsonObjectRequest(
            url,
            { response ->
                saveComic(response)
                showComic(response)
            },
            { error ->
                Toast.makeText(this, "Failed to load comic", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )
        requestQueue.add(request)
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Save comic to file
    private fun saveComic(comicObject: JSONObject) {
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(comicObject.toString().toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
