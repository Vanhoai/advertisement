package com.app.advertisement

import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.ComponentActivity
import com.app.advertisement.models.Link
import com.app.advertisement.models.LinkResponse
import com.app.advertisement.services.ApiService
import io.reactivex.Emitter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import io.socket.client.IO;
import io.socket.client.Socket;

class MainActivity : ComponentActivity() {

    var apiService: ApiService? = null
    var simpleVideoView: VideoView? = null
    var mediaControls: MediaController? = null
    var videos: MutableList<String>? = ArrayList()
    var currentVideo: Int = 0
    var socket: Socket = IO.socket("http://192.168.1.173:8000")

    private fun socketIO() {
        try {
            socket.connect()

            socket.emit("CONNECT", "HELLO")


            socket.on("msgToClient", object : io.socket.emitter.Emitter.Listener {
                override fun call(vararg args: Any?) {
                    Log.d("Message", args.get(0).toString())
                    pauseVideo()
                }
            })
        } catch (error: Exception) {
            Log.e("Error", error.toString())
        }
    }

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

    private fun pauseVideo() {
        Log.d("Message", "Pause")
        simpleVideoView!!.pause()
    }
}
