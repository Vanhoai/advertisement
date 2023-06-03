package com.app.advertisement

import android.media.MediaParser
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import com.app.advertisement.models.Link
import com.app.advertisement.models.LinkResponse
import com.app.advertisement.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    var apiService: ApiService? = null
    var simpleVideoView: VideoView? = null
    var mediaControls: MediaController? = null
    var videos: MutableList<String>? = ArrayList()
    var currentVideo: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        apiService = ApiService()
        simpleVideoView = findViewById<View>(R.id.videoView) as VideoView

        // set controller
        if (mediaControls == null) {
            mediaControls = MediaController(this)
            mediaControls!!.setAnchorView(this.simpleVideoView)
        }
        simpleVideoView!!.setMediaController(mediaControls)

        simpleVideoView!!.setOnPreparedListener { mp ->
            mp?.setOnCompletionListener {
                if (currentVideo < videos?.size!! - 1) {
                    currentVideo++
                }
                playVideo()
            }
        }

        // call when server emit refresh new video
        getLinkVideo()
    }

    private fun getLinkVideo() {
        apiService?.api?.getLinkVideo("?device_code=abc")?.enqueue(object : Callback<LinkResponse> {
            override fun onResponse(call: Call<LinkResponse>, response: Response<LinkResponse>) {
                val links: List<Link>? = response.body()?.data
                if (links != null) {
                    for (link in links) {
                        videos?.add(link.link)
                    }

                    playVideo()
                }
            }

            override fun onFailure(call: Call<LinkResponse>, t: Throwable) {

            }
        })
    }

    private fun playVideo() {

        val url = videos?.get(currentVideo)

        simpleVideoView!!.setVideoURI(Uri.parse(url))
        simpleVideoView!!.requestFocus()
        simpleVideoView!!.start()
    }
}
