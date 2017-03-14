package com.tim.room.rest;

import com.tim.room.model.Categories;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.model.User;
import com.tim.room.model.UserResponse;
import com.tim.room.model.YouDaoTrans;
import com.tim.room.model.imageRequest;
import com.tim.room.model.imageResultResponse;
import com.tim.room.model.imageSendResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RESTFulService {
    @Headers({"Authorization: CloudSight yadDzGB0XBbi8NyWs4PyVw",
            "Content-Type: application/json"
    })
    @POST("image_requests")
    Observable<imageSendResponse> sendImage(@Body imageRequest request);

    @Headers("Authorization: CloudSight yadDzGB0XBbi8NyWs4PyVw")
    @GET("image_responses/{token}")
    Observable<imageResultResponse> imageResponse(@Path("token") String token);


    @GET("getString")
    Observable<ResponseBody> getString();

    @GET("getString")
    Observable<ResponseBody> getString2();


//    @Headers("Content-Type: application/json")
//    @POST("getValue/addEmployee")
//    Observable<ResponseBody> addEmployee(@Body Employee em);

    @Headers("Content-Type: application/json")
    @POST("login")
    Observable<UserResponse> login(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("updateUser")
    Observable<Boolean> updateUser(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("addUser")
    Observable<User> addUser(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("addItem")
    Observable<Boolean> addItem(@Body Items item);

    @GET("findManCategories")
    Observable<List<Categories>> findManCategories();

    @GET("findWomenCategories")
    Observable<List<Categories>> findWomenCategories();

    @GET("findAllItemsTest/{userid}")
    Observable<List<Items>> findAllItemsTest(@Path("userid") String user_id);

    @Headers("Content-Type: application/json")
    @POST("findAllItems")
    Observable<ArrayList<ItemSeries>> findAllItems(@Body User user);

    @GET("findSubCategoriesById/{cate_id}")
    Observable<ArrayList<Categories>> findSubCategoriesById(@Path("cate_id") Integer cate_id);

    @Headers("Content-Type: application/json")
    @POST("updateItem")
    Observable<Boolean> updateItem(@Body Items item);

    @GET("findAllGlobalItems")
    Observable<List<Items>> findAllGlobalItems();

    @GET("findAlmostOverdueItem/{date}/{userId}")
    Observable<List<Items>> findAlmostOverdueItem(@Path("date") Integer date, @Path("userId") String user_id);

    @GET("openapi.do?keyfrom=eeeeeeeeeeeeeee&key=573473787&type=data&doctype=json&only=translate&version=1.1&q={word}")
    Observable<YouDaoTrans> transWord(@Path("word") String word);
}