package com.aceplussolutions.myosetpaing.videoplayerwithkotlin

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    private var simpleExoPlayerView: PlayerView? = null
    private var player: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var shouldAutoPlay: Boolean = true
    private var mainHandler: Handler? = null
    private var bandwidthMeter: BandwidthMeter? = null

    private val videos = ArrayList<Video>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createPlaylist()
        simpleExoPlayerView = findViewById(R.id.playerView)
        mainHandler = Handler()
        bandwidthMeter = DefaultBandwidthMeter()

    }

    fun createPlaylist() {

        for (i in 0..4) {

            val video = Video("https://www.radiantmediaplayer.com/media/bbb-360p.mp4")
            videos.add(video)
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSource = DefaultDataSourceFactory(this, Util.getUserAgent(this, "VideoPlayer"))
        val mediaSource = ExtractorMediaSource.Factory(dataSource).createMediaSource(uri)
        return mediaSource
    }

    private fun initPlayer() {
        simpleExoPlayerView?.requestFocus()
        val videoTrackSelector = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(videoTrackSelector)
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        simpleExoPlayerView?.player = player
        val mediaSources = arrayOfNulls<MediaSource>(videos.size)
        for (i in 0 until videos.size) {
            mediaSources[i] = buildMediaSource(Uri.parse(videos.get(i).videoUrl))
        }
        val mediaSource = if (mediaSources.size == 1)
            mediaSources[0]
        else
            ConcatenatingMediaSource(*mediaSources)
        player?.prepare(mediaSource)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initPlayer()
        }

    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initPlayer()
        }
    }

    private fun releasePlayer() {
        shouldAutoPlay = player?.playWhenReady!!
        player?.release()
        player = null
        trackSelector = null
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT>23){
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT>23)
            releasePlayer()
    }
}
