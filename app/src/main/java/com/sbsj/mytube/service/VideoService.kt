package com.sbsj.mytube.service

import com.sbsj.mytube.dto.VideoDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

interface VideoService {
    @GET("v3/be00d46a-cefb-46eb-a8ac-a5943f3139cf")
    fun listVideos(): Call<VideoDto>
}