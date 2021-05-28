package com.wekex.apps.homeautomation.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.balsikandar.crashreporter.utils.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Comparator;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.ClientDefaults;

public class PathUtil {
    public static final String AUTHORITY = "YOUR_AUTHORITY.provider";
    private static final boolean DEBUG = false;
    public static final String DOCUMENTS_DIR = "documents";
    public static final String HIDDEN_PREFIX = ".";
    static final String TAG = "FileUtils";
    public static Comparator<File> sComparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
        }
    };
    public static FileFilter sDirFilter = file -> file.isDirectory() && !file.getName().startsWith(PathUtil.HIDDEN_PREFIX);
    public static FileFilter sFileFilter = file -> file.isFile() && !file.getName().startsWith(PathUtil.HIDDEN_PREFIX);

    private PathUtil() {
    }

    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }
        int dot = uri.lastIndexOf(HIDDEN_PREFIX);
        if (dot >= 0) {
            return uri.substring(dot);
        }
        return "";
    }

    public static boolean isLocal(String url) {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://");
    }

    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getPathWithoutFilename(File file) {
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            return file;
        }
        String filename = file.getName();
        String filepath = file.getAbsolutePath();
        String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
        if (pathwithoutname.endsWith(MqttTopic.TOPIC_LEVEL_SEPARATOR)) {
            pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
        }
        return new File(pathwithoutname);
    }

    public static String getMimeType(File file) {
        String extension = getExtension(file.getName());
        if (extension.length() > 0) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }
        return "application/octet-stream";
    }

    @TargetApi(19)
    public static String getMimeType(Context context, Uri uri) {
        return getMimeType(new File(getPath(context, uri)));
    }

    public static boolean isLocalStorageDocument(Uri uri) {
        return AUTHORITY.equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /* JADX INFO: finally extract failed */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String str = "_data";
        try {
            Cursor cursor2 = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (cursor2 == null || !cursor2.moveToFirst()) {
                if (cursor2 != null) {
                    cursor2.close();
                }
                return null;
            }
            String string = cursor2.getString(cursor2.getColumnIndexOrThrow("_data"));
            if (cursor2 != null) {
                cursor2.close();
            }
            return string;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) {
        int i = 0;
        if (!(VERSION.SDK_INT >= 19) || !DocumentsContract.isDocumentUri(context, uri)) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } else if (isLocalStorageDocument(uri)) {
            return DocumentsContract.getDocumentId(uri);
        } else {
            if (isExternalStorageDocument(uri)) {
                String[] split = DocumentsContract.getDocumentId(uri).split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory());
                    sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
                    sb.append(split[1]);
                    return sb.toString();
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                String[] contentUriPrefixesToTry = {"content://downloads/public_downloads", "content://downloads/my_downloads"};
                int length = contentUriPrefixesToTry.length;
                while (i < length) {
                    try {
                        String path = getDataColumn(context, ContentUris.withAppendedId(Uri.parse(contentUriPrefixesToTry[i]), Long.valueOf(id).longValue()), null, null);
                        if (path != null) {
                            return path;
                        }
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File file = generateFileName(getFileName(context, uri), getDocumentCacheDir(context));
                String destinationPath = null;
                if (file != null) {
                    destinationPath = file.getAbsolutePath();
                    try {

                        saveFileFromUri(context, uri, destinationPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return destinationPath;
            } else if (isMediaDocument(uri)) {
                String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                String type = split2[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String str = "_id=?";
                return getDataColumn(context, contentUri, "_id=?", new String[]{split2[1]});
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    public static String getReadableFileSize(int size) {
        DecimalFormat dec = new DecimalFormat("###.#");
        String str = " KB";
        String str2 = " MB";
        String str3 = " GB";
        float fileSize = 0.0f;
        String suffix = " KB";
        if (size > 1024) {
            fileSize = (float) (size / 1024);
            if (fileSize > 1024.0f) {
                fileSize /= 1024.0f;
                if (fileSize > 1024.0f) {
                    fileSize /= 1024.0f;
                    suffix = " GB";
                } else {
                    suffix = " MB";
                }
            }
        }
        return dec.format(fileSize) + suffix;
    }

    public static Intent createGetContentIntent() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        intent.addCategory("android.intent.category.OPENABLE");
        return intent;
    }

    public static Intent getViewIntent(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, AUTHORITY, file);
        Intent intent = new Intent("android.intent.action.VIEW");
        String url = file.toString();
        if (url.contains(".doc") || url.contains(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(".zip") || url.contains(".rar")) {
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.contains(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.contains(Constants.FILE_EXTENSION)) {
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(ClientDefaults.MAX_MSG_SIZE);
        intent.addFlags(1);
        intent.addFlags(2);
        return intent;
    }

    public static File getDownloadsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public static File getDocumentCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), DOCUMENTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logDir(context.getCacheDir());
        logDir(dir);
        return dir;
    }

    private static void logDir(File dir) {
    }

    @Nullable
    public static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }
        File file = new File(directory, name);
        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf(46);
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }
            int index = 0;
            while (file.exists()) {
                index++;
                StringBuilder sb = new StringBuilder();
                sb.append(fileName);
                sb.append('(');
                sb.append(index);
                sb.append(')');
                sb.append(extension);
                file = new File(directory, sb.toString());
            }
        }
        try {
            if (!file.createNewFile()) {
                return null;
            }
            logDir(directory);
            return file;
        } catch (IOException e) {
            Log.w(TAG, e);
            return null;
        }
    }

    private static void saveFileFromUri(Context context, Uri uri, String destinationPath) throws IOException {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is2 = context.getContentResolver().openInputStream(uri);
            BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
            byte[] buf = new byte[1024];
            is2.read(buf);
            do {
                bos2.write(buf);
            } while (is2.read(buf) != -1);
            if (is2 != null) {
                try {
                    is2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            bos2.close();
        } catch (IOException e2) {
            e2.printStackTrace();
            if (is != null) {
                is.close();
            }
            if (bos != null) {
                bos.close();
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                    throw th;
                }
            }
            if (bos != null) {
                bos.close();
            }
            throw th;
        }
    }

    public static byte[] readBytesFromFile(String filePath) throws IOException {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[((int) file.length())];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        return bytesArray;
    }

    public static File createTempImageFile(Context context, String fileName) throws IOException {
        return File.createTempFile(fileName, ".jpg", new File(context.getCacheDir(), DOCUMENTS_DIR));
    }

    @TargetApi(19)
    public static String getFileName(@NonNull Context context, Uri uri) {
        if (context.getContentResolver().getType(uri) != null || context == null) {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor == null) {
                return null;
            }
            int nameIndex = returnCursor.getColumnIndex("_display_name");
            returnCursor.moveToFirst();
            String filename = returnCursor.getString(nameIndex);
            returnCursor.close();
            return filename;
        }
        String path = getPath(context, uri);
        if (path == null) {
            return getName(uri.toString());
        }
        return new File(path).getName();
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(47) + 1);
    }
}
