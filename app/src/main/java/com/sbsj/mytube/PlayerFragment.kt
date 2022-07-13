package com.sbsj.mytube

import android.icu.text.CaseMap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.sbsj.mytube.adpater.VideoAdapter
import com.sbsj.mytube.databinding.FragmentLayoutBinding
import com.sbsj.mytube.dto.VideoDto
import com.sbsj.mytube.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs


class PlayerFragment : Fragment(R.layout.fragment_layout) {


    private var binding: FragmentLayoutBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private var player : SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentLayoutBinding = FragmentLayoutBinding.bind(view)
        initMotionLayoutEvent(fragmentLayoutBinding)
        initRecyclerView(fragmentLayoutBinding)
        initPlayer(fragmentLayoutBinding)
        initControlButton(fragmentLayoutBinding)
        getVideoList()

    }



    private fun initMotionLayoutEvent(fragmentLayoutBinding: FragmentLayoutBinding) {

        binding = fragmentLayoutBinding
        binding!!.playerMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {

                    (activity as MainActivity).also { activity ->
                        activity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress =
                            abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }
        })
    }

    private fun initRecyclerView(fragmentLayoutBinding: FragmentLayoutBinding) {
        videoAdapter = VideoAdapter(callback = { url, title ->
            play(url, title)
        })
        fragmentLayoutBinding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }
    private fun initPlayer(fragmentLayoutBinding: FragmentLayoutBinding) {

       context?.let {
           player = SimpleExoPlayer.Builder(it).build()
       }


        fragmentLayoutBinding.playerView.player = player
        binding?.let{
            player?.addListener(object: Player.EventListener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)

                    }else{
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }

    }
    private fun initControlButton(fragmentLayoutBinding: FragmentLayoutBinding) {
        fragmentLayoutBinding.bottomPlayerControlButton.setOnClickListener {
            val player = this.player?:return@setOnClickListener
            if(player.isPlaying){
                player.pause()
            }
            else{
                player.play()
            }
        }
    }


    private fun getVideoList() {
        val retrofit = RetrofitClient.retrofit

        retrofit.create(VideoService::class.java).also {
            it.listVideos()
                .enqueue(object : Callback<VideoDto> {
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("Main", "response fail")
                            return
                        }
                        response.body()?.let { videoDto ->
                            Log.d("Main", "response s${videoDto.videos}")
                            videoAdapter.submitList(videoDto.videos)
                        }

                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {

                    }

                })
        }
    }

    fun play(url: String, title: String) {

        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.setMediaSource(mediaSource)
            player?.prepare()  // 준비과정
            player?.play()
        }
        binding?.let {
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()

    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }
}