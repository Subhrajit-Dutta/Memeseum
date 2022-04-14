package com.example.memeshare

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
        val shareButton = findViewById<Button>(R.id.sharebutton)
        shareButton.setOnClickListener {
            shareMeme()

        }
        val nextButton = findViewById<Button>(R.id.nextbutton)
        nextButton.setOnClickListener {
            nextMeme()
        }
    }

    private fun shareMeme() {
        val imageView = findViewById<ImageView>(R.id.memeimageView)
        val drawable: Drawable = imageView.getDrawable()
        val bitmap = (drawable as BitmapDrawable).bitmap

        try {
            val file = File(
                applicationContext.externalCacheDir,
                File.separator.toString() + "memeimageView.jpg"
            )
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val photoURI: Uri = FileProvider.getUriForFile(
                applicationContext,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            intent.putExtra(Intent.EXTRA_STREAM, photoURI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/jpg"
            startActivity(Intent.createChooser(intent, "Share image via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun nextMeme() {
       loadMeme()
    }

    fun loadMeme(){
        // Instantiate the RequestQueue.
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"
        val imgview = findViewById<ImageView>(R.id.memeimageView)
        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
            val url = response.getString("url")

                Glide.with(this).load(url).listener(object : RequestListener<Drawable>{

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {progressBar.visibility = View.GONE
                        return false
                    }
                }).into(imgview)
            },
            Response.ErrorListener {

            })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
    fun shareMeme(view: View) {

    }

}