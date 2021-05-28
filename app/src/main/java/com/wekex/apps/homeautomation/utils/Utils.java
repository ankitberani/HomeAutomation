package com.wekex.apps.homeautomation.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import id.zelory.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wekex.apps.homeautomation.utils.PathUtil.getDataColumn;
import static com.wekex.apps.homeautomation.utils.PathUtil.isDownloadsDocument;
import static com.wekex.apps.homeautomation.utils.PathUtil.isExternalStorageDocument;
import static com.wekex.apps.homeautomation.utils.PathUtil.isMediaDocument;


public class Utils {
    public static final String DATA = "data";
    public static final String HEADING = "heading";
    public static final String HOMESCENE = "homescene";
    public static final String ICON = "icon";

    /* renamed from: NA */
    public static final String f198NA = "na";
    public static String NOTAVAILABLE = "NA";
    public static final String SCENEID = "sceneid";
    public static final String SUBHEADING = "subheading";
    private static String TAG = "Utils";

    public static JSONArray addhomescene(Context context, String sceneId, String heading, String description, String icon, int pos) {
        String data = Constants.savetoShared(context).getString(HOMESCENE, f198NA);
        JSONArray jsonArray = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put(SCENEID, sceneId);
            jo.put(HEADING, heading);
            jo.put(SUBHEADING, description);
            jo.put(ICON, icon);
            if (data.equals(f198NA)) {
                jsonArray = new JSONArray();
                jsonArray.put(pos - 1, jo);
            } else {
                jsonArray = new JSONArray(data);
                jsonArray.put(pos - 1, jo);
            }
            jsonArray.put(pos, createscenebutton());
            Constants.savetoShared(context).edit().putString(HOMESCENE, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static String getParsedDate(String day) {
        return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(getDate(day));
    }

    @Nullable
    private static Date getDate(String day) {
        try {
            return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = 19)
    public static JSONArray removehomescene(Context context, String sceneId) {
        String data = Constants.savetoShared(context).getString(HOMESCENE, f198NA);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(data);
        sb.append(" removehomescene: ");
        sb.append(sceneId);
        Log.d(str, sb.toString());
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString(SCENEID).equals(sceneId)) {
                    jsonArray.remove(i);
                }
            }
            Constants.savetoShared(context).edit().putString(HOMESCENE, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONArray getprofiles(Context context) {
        String data = Constants.savetoShared(context).getString(HOMESCENE, f198NA);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getprofiles: ");
        sb.append(data);
        Log.d(str, sb.toString());
        JSONArray jsonArray = new JSONArray();
        if (data.equals(f198NA) || data.equals("[]")) {
            return jsonArray.put(createscenebutton());
        }
        try {
            jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONObject createscenebutton() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(SCENEID, "add");
            jo.put(HEADING, "Add");
            jo.put(SUBHEADING, "");
            jo.put(ICON, "add");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("createscenebutton: ");
        sb.append(jo);
        Log.d(str, sb.toString());
        return jo;
    }

    public static Bitmap compressedBitmap(Activity activity, String filepath) {
        Bitmap bm = null;
        try {
            bm = new Compressor(activity).compressToBitmap(new File(filepath));

            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("ImageToString: ");
            sb.append(bm.getByteCount());
            Log.d(str, sb.toString());
            bm.compress(CompressFormat.JPEG, 50, new ByteArrayOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Uri uri, Context context) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
