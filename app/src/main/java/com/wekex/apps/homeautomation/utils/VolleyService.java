package com.wekex.apps.homeautomation.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.Constants.BASEURL;
import static com.wekex.apps.homeautomation.utils.Utils.NOTAVAILABLE;

public class VolleyService {

   private static VolleyListener mResultCallback = null;

    private VolleyListener mAdapterCallback;

    public void VolleyService(VolleyListener callback) {
        this.mAdapterCallback = callback;
    }
   private static String TAG = "VolleyService";


    public static void postDataVolley(String url, final JSONObject sendObj,final Context mContext){
         mResultCallback = (VolleyListener) mContext;
        try {
            url = BASEURL + url;
            Log.d(TAG, "postDataVolley: "+url);
            RequestQueue queue = Volley.newRequestQueue(mContext);

            final String finalUrl = url;
            StringRequest jsonObj = new StringRequest(POST,url, response -> {

                if (mResultCallback != null)
                    mResultCallback.notifySuccess(finalUrl,response);
            }, error -> {
                error.getStackTrace();
                mResultCallback.notifySuccess(finalUrl,"Volley Error " + NOTAVAILABLE);
            }){
             /*   @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setHeaders(0,mContext);
                }*/

                @Override
                protected Map<String, String> getParams() {
                    HashMap<String,String> params = new HashMap<>();
                    params.put("data",sendObj.toString());
                    Log.d(TAG, "getParams: "+params);
                    return params;
                }
            };

            queue.add(jsonObj);

        }catch(Exception e){
                e.getStackTrace();
        }
    }

    public static void postDataUser(String url, final JSONObject sendObj,final Context mContext){
         mResultCallback = (VolleyListener) mContext;
        try {
            url = BASEURL + url;
            Log.d(TAG, sendObj.toString()+" postDataVolley: "+url);
            RequestQueue queue = Volley.newRequestQueue(mContext);

            final String finalUrl = url;
            StringRequest jsonObj = new StringRequest(POST, url, response -> {

                if (mResultCallback != null)
                    mResultCallback.notifySuccess(finalUrl,response);
            }, error -> {
                error.getStackTrace();
                mResultCallback.notifySuccess(finalUrl,"Volley Error "+NOTAVAILABLE);
            }){
               /* @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                      return setHeaders(1,mContext);
                }*/

                @Override
                protected Map<String, String> getParams() {
                    HashMap<String,String> params = new HashMap<>();
                    params.put("data",sendObj.toString());
                    Log.d(TAG, "getParams: "+params);
                    return params;
                }
            };

            queue.add(jsonObj);

        }catch(Exception e){
                e.getStackTrace();
        }
    }

    public static void getDataVolley(String url ,final Context mContext){
        Log.d(TAG, "getDataVolley: "+url);
        mResultCallback = (VolleyListener) mContext;
        try {
            url = BASEURL+"Get/" + url;
            RequestQueue queue = Volley.newRequestQueue(mContext);

            final String finalUrl = url;
            StringRequest jsonObj = new StringRequest(GET, url, response -> {
                if (mResultCallback != null)
                    mResultCallback.notifySuccess(finalUrl,response);
            }, error -> {
                error.getStackTrace();
                Log.d(TAG, error.getMessage()+" onErrorResponse: "+error.toString());
                mResultCallback.notifySuccess(finalUrl,"Volley Error "+NOTAVAILABLE);
            })/*{
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setHeaders(0,mContext);
                }
            }*/;

            queue.add(jsonObj);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void getDataUser(String url,final Context mContext){
        mResultCallback = (VolleyListener) mContext;
        try {
            url = BASEURL+"Get/" + url;
            Log.d(TAG, "getDataUser: "+url);
            RequestQueue queue = Volley.newRequestQueue(mContext);

            final String finalUrl = url;
            StringRequest jsonObj = new StringRequest(GET, url, response -> {
                if (mResultCallback != null)
                    mResultCallback.notifySuccess(finalUrl,response);
            }, error -> {
                error.getStackTrace();
                mResultCallback.notifySuccess(finalUrl,"Volley Error "+NOTAVAILABLE);
            })/*{
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setHeaders(1,mContext);
                }
            }*/;

            queue.add(jsonObj);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
