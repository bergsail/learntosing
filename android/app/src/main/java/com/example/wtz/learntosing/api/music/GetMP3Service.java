package com.example.wtz.learntosing.api.music;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by wtz on 15/10/23.
 */
public interface GetMP3Service {
    @GET("/temp/shinian_part{id}_std.mp3")
    @Headers({"Content-Type: audio/mp3"})
    Response getxml(@Path("id") String urlpath);
}
