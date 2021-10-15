package com.wekex.apps.homeautomation.Retrofit;


import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.model.AddSceneModel;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.AllDataResponseModelWithStatus;
import com.wekex.apps.homeautomation.model.GetAppHomeModel;
import com.wekex.apps.homeautomation.model.LoginRequestModel;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.data;
import com.wekex.apps.homeautomation.model.ir_remotes;
import com.wekex.apps.homeautomation.model.remote_model_codes;
import com.wekex.apps.homeautomation.model.remote_model_codes_add_request;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.model.whethermodel;

import org.json.JSONObject;


import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {


    String ADDSCENE = "api/Get/AddScene";
    String TRIGGERSCENE = "api/Get/triggerScene";
    String EDITSCENE = "api/Get/EditScene";
    String ADDGROUP = "api/Get/AddGroup";
    String UPDATEGROUP = "api/Get/EditGroup";
    String TRIGERGROUP = "api/Get/EditGroup";
    String LOGINUSER = "api/Login/userLogin";
    String GETRULELIST = "api/Get/getRule";

    String UPDATEDEVICE = "/api/Get/getDevices";

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(ADDSCENE)
    Observable<SuccessResponse> addNewScene(@Body JsonObject jsonObject);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<scene_model> getAllScene(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<SuccessResponse> triggerScene(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<SuccessResponse> delScene(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(EDITSCENE)
    Observable<SuccessResponse> editScene(@Body JsonObject jsonObject);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(ADDGROUP)
    Observable<SuccessResponse> AddGroup(@Body JsonObject jsonObject);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<SuccessResponse> delGroup(@Url String url);


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(UPDATEGROUP)
    Observable<SuccessResponse> UpdateGroup(@Body JsonObject jsonObject);


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<SuccessResponse> updateRoom(@Url String url);


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<GetAppHomeModel> getAllData(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<AllDataResponseModelWithStatus> getAllDataWithStatus(@Url String url);


    /* @Headers("Content-Type: application/json; charset=utf-8")
     @GET
     Call<String> getAllData(@Url String url);
 */
    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<scene_model> getGroups(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<SuccessResponse> triggerRoom(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<whethermodel> getWetherInfo(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<ArrayList<data>> getDeviceLogs(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<String> getDeviceOfRoom(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(LOGINUSER)
    Call<String> loginUser(@Body LoginRequestModel object);


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<String> getRemoteCategory(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Observable<remote_model_codes> getModelCode(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<String> getUserRemote(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("api/Get/AddUserRemote/")
    Call<String> AddNewRemote(@Query("device") String device, @Query("channel") String channel, @Body remote_model_codes_add_request _object);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("api/Get/EditUserRemote")
    Call<String> UpdateRemote(@Query("index") int index, @Query("device") String device, @Query("channel") String channel, @Body remote_model_codes_add_request _ir_remote_data);


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<String> getRoomDevice(@Url String url);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(UPDATEDEVICE)
    Call<String> getUpdatedData(@Body JsonObject jsonObject);
}
