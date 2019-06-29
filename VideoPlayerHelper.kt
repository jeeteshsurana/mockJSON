package rs.highlande.app.tatatu.util.helper

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import rs.highlande.app.tatatu.util.Constant


/**
 * Created by Abhin.
 */
class VideoPlayerHelper(var mExoPlayer: PlayerView, var context: Context) {

    private var mSimpleExoPlayer: SimpleExoPlayer? = null
    private var mediaSource: ExtractorMediaSource? = null
    private var mDefaultDataSourceFactory: DefaultDataSourceFactory? = null

    //play the video player
    fun videoPlay(videoUrl: String? = null, img_play: AppCompatImageView) {
        Log.d(Constant.Tag,"VideoUrl-->$videoUrl")
        if (mExoPlayer.player == null) {
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
            mExoPlayer.player = mSimpleExoPlayer
            mExoPlayer.requestFocus()
            mExoPlayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            mDefaultDataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))
        }

        img_play.visibility = View.GONE

        //set the media
        mediaSource = if (videoUrl.isNullOrBlank()) {
            ExtractorMediaSource.Factory(mDefaultDataSourceFactory).createMediaSource(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"))
        } else {
            ExtractorMediaSource.Factory(mDefaultDataSourceFactory).createMediaSource(Uri.parse(videoUrl))
        }

        mSimpleExoPlayer?.shuffleModeEnabled = true
        mExoPlayer.player.isCurrentWindowDynamic
        val loopingSource = LoopingMediaSource(mediaSource) //run in loop
        mSimpleExoPlayer?.prepare(loopingSource)
        start()
    }


    //ExoPlayer Play
    fun isPlaying(): Boolean {
        return if (mExoPlayer.player != null) {
            mExoPlayer.player?.playWhenReady!!
        } else {
            false
        }
    }

    //ExoPlayer Start
    fun start() {
        mExoPlayer.player?.playWhenReady = true
    }

    //ExoPlayer Pause
    fun pause(img_play: AppCompatImageView) {
        if (mExoPlayer.player != null && isPlaying()) {
            mExoPlayer.player?.playWhenReady = false
            img_play.visibility = View.VISIBLE
        }
    }

    fun resume(img_play: AppCompatImageView) {
        if (!isPlaying()) {
            start()
            img_play.visibility = View.GONE
        }
    }

    //ExoPlayer Stop
    fun stop() {
        if (mExoPlayer.player != null && isPlaying()) {
            mExoPlayer.player.playWhenReady = false
        }
        mExoPlayer.player = null
        mSimpleExoPlayer?.release()
        mSimpleExoPlayer = null
    }

    //ExoPlayer Current Position
    fun getCurrentPosition(): Long {
        return if (mExoPlayer.player.duration == 0L) 0
        else mExoPlayer.player.currentPosition
    }

    //ExoPlayer total duration
    fun getDuration(): Long {
        return if (mExoPlayer.player.duration == 0L) 0
        else mExoPlayer.player.duration
    }

    fun onClick(img_play: AppCompatImageView) {
        if (isPlaying()) {
            img_play.visibility = View.VISIBLE
            pause(img_play)
        } else {
            img_play.visibility = View.GONE
            start()
        }
    }
}