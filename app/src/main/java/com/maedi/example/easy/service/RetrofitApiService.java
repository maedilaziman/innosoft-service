package com.maedi.example.easy.service;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Maedi on 7/19/2019.
 */

public interface RetrofitApiService {

    // Fetch all services category
    //@GET(DataStatic.uri_service_services_all_typecategory)
    //Single<Object> fetchServicesAllCategory();

    //@GET("posts?")
    //Single<Object> exampleFetchWithQuery(@Query("userId") String service);
    //Single<Object> -> will be error becuase we use JSONArrayConverterFactory
    //if we want not error, we must use JSONObjectConverterFactory

    @GET("posts?")
    Single<ResponseBody> exampleFetchWithQuery(@Query("userId") String service);

    @POST("posts")
    Call<ResponseBody> exampleFetchWithRequestBody(@Body RequestBody jsonObject);

    //@Multipart
    //@POST(DataStatic.uri_insert_service_gallery)
    //Call<JsonElement> uploadFile(@Part List<MultipartBody.Part> file);
    //Call<ResponseBody> uploadFile(@Part List<MultipartBody.Part> file);
}
