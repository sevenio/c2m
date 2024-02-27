package com.tvisha.click2magic.Helper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.tvisha.click2magic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;


public class Helper {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 1000 meters  //1KM

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 3; // 3 minutes
    private static final float MIN_DISTANCE_BETWEEN_LOCATION = 1000.0f; // 1000 meters  //1KM
    public static boolean isin_popup = false;
    private static Helper ourInstance = new Helper();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    Toast toast;

    public static Helper getInstance() {
        if (ourInstance == null) {
            ourInstance = new Helper();
        }
        return ourInstance;
    }

    public static JSONObject stringToJsonObject(String data) {
        if (data != null && data.length() > 0) {
            try {
                JSONObject data_object = new JSONObject(data);
                return data_object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        /*final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;*/
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    public static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        return width;
    }

    public static int cellDeviderSize(int cellWidth) {
        if (cellWidth <= 320) {
            return 100;
        } else if (cellWidth <= 480) {
            return 120;
        } else if (cellWidth <= 720) {
            return 150;
        } else if (cellWidth > 720) {
            return 200;
        } else {
            return 100;
        }
    }
    //ProgressBar progressBar;

    public static int getMediaCellCountByWidth(Context context) {
        int cellWidth = Helper.getScreenWidth(context);
        int numOdParts = cellWidth / cellDeviderSize(cellWidth);
        return numOdParts;
    }

    public static int getMediaCellSize(Context context) {
        int cellWidth = Helper.getScreenWidth(context);
        int numOdParts = cellWidth / cellDeviderSize(cellWidth);
        cellWidth = cellWidth / 3;
        return cellWidth;
    }
    /*public static JSONObject stringToJsonObject(String data) {
        if (data != null) {

            try {
                JSONObject data_object = new JSONObject(data);
                return data_object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }*/


    /*public static boolean checkStoragePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )) {
            return false;
        } else {
            return true;
        }
    }*/

    private static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        //int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return height;
    }
    /*public static void pushToast(Context context, String message)
    {
        Toast toast=null;
        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
            //toast.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

            toast.getView().setPadding(20, 20, 20, 20);
            textView.setTextColor(ContextCompat.getColor(context,android.R.color.white));
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    //type Converstion
    public static String arrayToString(List<String> arrayString) {
        String convertedData = null;
        try {
            if (arrayString != null && arrayString.size() > 0) {
                convertedData = ",";
                for (String strng : arrayString) {
                    if (strng != null) {
                        convertedData = convertedData + strng + ",";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedData;
    }

    public static String arrayToStringWithoutPrifixPostFix(List<String> arrayString) {
        String convertedData = null;
        try {
            if (arrayString != null && arrayString.size() > 0) {
                for (String strng : arrayString) {
                    if (strng != null) {
                        if (convertedData != null) {
                            convertedData = convertedData + "," + strng;
                        } else {
                            convertedData = strng;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedData;
    }

    public static List<String> stringToArray(String str_array) {
        if (str_array != null && !str_array.equals("null")) {
            String temp_array[] = str_array.split(",");
            List<String> result_array = new ArrayList<String>();
            for (int i = 0; i < temp_array.length; i++) {
                if (temp_array[i] != null && temp_array[i].length() != 0) {
                    result_array.add(temp_array[i].replace(" ", ""));
                }
            }

            if (result_array.size() != 0) {
                return result_array;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public static String listToJsonString(List<String> data) {
        try {
            if (data != null && data.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : data) {
                    if (tag != null && tag.trim().length() > 0) {
                        jsonArray.put(tag);
                    }
                }
                return String.valueOf(jsonArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONArray stringToJsonArray(String data) {
        if (data != null) {
            try {
                JSONArray data_array = new JSONArray(data);
                return data_array;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    //validations
    public static boolean validEmail(String email) {
        if (email.length() != 0) {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(email);
            return m.matches();
        } else {
            return false;
        }
    }

    public static boolean isValidName(String name) {
        if (name != null) {
            if (name.length() >= 3 && !isNumeric(name)) {
                CharSequence inputStr = name;
                Pattern pattern = Pattern.compile(new String("^[a-zA-Z\\s]*$"));
                Matcher matcher = pattern.matcher(inputStr);
                return matcher.matches();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isValidPhoneNumber(String str) {
        if (str != null && (str.startsWith("7") || str.startsWith("8") || str.startsWith("9"))) {
            return true;
        } else {
            return false;
        }
    }

    /*public String getRealPathFromURI(Context context,Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }*/
    public static boolean isAppInBackground(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);

        if (!tasks.isEmpty()) {

            ComponentName topActivity = tasks.get(0).topActivity;


            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    public boolean stringMatchWithFirstWordofString(String search, String name) {
        String regex = "(?i)\\b" + search + ".*?\\b";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        return m.find();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getListOfNotifications(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();

            if (barNotifications.length == 1) {
                notificationManager.cancelAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAttachmentIcon(int attachmentIcon) {
        try {

            switch (attachmentIcon) {
                case Values.Gallery.GALLERY_PDF:
                    return R.drawable.ic_pdf;
                case Values.Gallery.GALLERY_DOC:
                    return R.drawable.ic_doc;
                case Values.Gallery.GALLERY_XLS:
                    return R.drawable.ic_xls;
                case Values.Gallery.GALLERY_HTML:
                    return R.drawable.ic_html;
                case Values.Gallery.GALLERY_PPT:
                    return R.drawable.ic_ppt;
                case Values.Gallery.GALLERY_LINK:
                    return R.drawable.ic_link;
                case Values.Gallery.GALLERY_TXT:
                    return R.drawable.ic_txt;
                case Values.Gallery.GALLERY_SQL:
                    return R.drawable.ic_sql;
                case Values.Gallery.GALLERY_ZIP:
                    return R.drawable.ic_zip;
                case Values.Gallery.GALLERY_CSV:
                    return R.drawable.ic_css;
                case Values.Gallery.GALLERY_JS:
                    return R.drawable.ic_js;
                case Values.Gallery.GALLERY_AUDIO:
                    return R.drawable.ic_mp3;
                default:
                    return R.drawable.ic_other;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFileIconPath(String path) {
        try {
            if (path != null && !path.isEmpty() && !path.equals("null")) {
                String filenameArray[] = path.split("\\.");
                String extention = filenameArray[filenameArray.length - 1];
                extention = extention.toLowerCase();
                return getFileType(extention);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getFileType(String extention) {
        try {
            if (extention == null)  //if null
                return 0;

            if (extention.matches("jpg|jpeg|png|gif|bmp|svg")) {
                return Values.Gallery.GALLERY_IMAGE;

            } else if (extention.matches("docx|doc")) {
                return Values.Gallery.GALLERY_DOC;

            } else if (extention.matches("xls|xlsx")) {
                return Values.Gallery.GALLERY_XLS;

            } else if (extention.matches("txt")) {
                return Values.Gallery.GALLERY_TXT;

            } else if (extention.matches("zip|rar")) {
                return Values.Gallery.GALLERY_ZIP;

            } else if (extention.matches("pdf")) {
                return Values.Gallery.GALLERY_PDF;

            } else if (extention.matches("json")) {
                return Values.Gallery.GALLERY_JSON;

            } else if (extention.matches("sql")) {
                return Values.Gallery.GALLERY_SQL;

            } else if (extention.matches("odt")) {
                return Values.Gallery.GALLERY_ODT;

            } else if (extention.matches("csv")) {
                return Values.Gallery.GALLERY_CSV;

            } else if (extention.matches("xml")) {
                return Values.Gallery.GALLERY_XML;

            } else if (extention.matches("html")) {
                return Values.Gallery.GALLERY_HTML;

            } else if (extention.matches("js")) {
                return Values.Gallery.GALLERY_JS;

            } else if (extention.matches("mp3|audio/mpeg")) {
                return Values.Gallery.GALLERY_AUDIO;

            } else if (extention.matches("mp4|mpeg|3gpp|3gp|mkv|flv|mov|avi|webm")) {
                return Values.Gallery.GALLERY_VIDEO;

            } else if (extention.matches("ppt|pptx")) {
                return Values.Gallery.GALLERY_PPT;
            } else if (extention.matches("v-card|octet_stream|vcard|vcf|x_vcard")) {
                return Values.Gallery.CONTACT_TYPE;
            } else {
                return Values.Gallery.GALLERY_OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*public static JSONObject stringToJsonObject(String data) {

        if (data != null && data.length() > 0) {
            try {
                JSONObject data_object = new JSONObject(data);
                return data_object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }*/

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAppForground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }

    public Boolean isActivityRunning(Context context, Class activityClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {

            if (task.topActivity.getClassName().trim().toLowerCase().equals("com.tvisha.click2magic.dialog.dialogactivity")) {
                return true;
            }
        }

        return false;
    }

    public boolean isBackStackExist(Context context) {
        ActivityManager mngr = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        return taskList.size() > 0;
    }

    public void pushToast(Context context, String msg) {
        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String captilizeText(String text) {
        try {
            if (text == null) return "";
            String[] strArray = text.split(" ");
            StringBuilder builder = new StringBuilder();
            for (String s : strArray) {
                if (s != null && !s.trim().isEmpty() && !s.equals(" ")) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public String getPersentage(int obatained, int total) {
        int percentage = (int) (obatained * 100) / total;
        if (percentage > 0) {
            return String.valueOf(percentage);
        } else {
            return "0";
        }
    }

    public JSONArray listToJsonArray(List<String> data) {
        try {
            JSONArray array = new JSONArray();
            int i = 0;
            for (String object : data) {
                array.put(object);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public String getImagePath(String path) {
        return Api.BASE_IMG_PATH + path;
    }*/

    public boolean isValidUrl(String url) {
        if (url != null) {

            if (URLUtil.isValidUrl(url)) {
                return true;
            } else if (URLUtil.isHttpsUrl(url)) {
                return true;
            } else if (URLUtil.isHttpUrl(url)) {
                return true;
            } else if (url.toLowerCase().startsWith("www.")) {
                return true;
            }
        }

        return false;
    }

    //keyboard handling
    public void openKeyBoard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeKeyBoard(Context context, View view) {
        try {

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void shareLocationToOtherApps(Context context, SharedLocationObject object) {
        Intent shareIntent = new Intent();
        Double latitude = object.getPoints().latitude;
        Double longitude = object.getPoints().longitude;
        String ShareSub = object.getLocationName() + " \n " + object.getLocation();
        String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, ShareSub + "\n" + "\n" + uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share to"));
    }*/

    public void shareMessageToOtherApps(Context context, String message) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    public void shareFileToOtherApps(Context context, String filePath, boolean download, int file_type) {
        Uri uri = null;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        File file = new File(filePath);
        if (download) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.tvisha.troopmessenger.fileprovider", file);
            } else {
                uri = Uri.fromFile(new File(filePath));
            }
        }

        if (uri != null && !uri.equals("null")) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        if (file_type == Values.Gallery.GALLERY_IMAGE) {
            shareIntent.setType("image/*");
        } else if (file_type == Values.Gallery.GALLERY_VIDEO) {
            shareIntent.setType("video/*");
        } else {
            shareIntent.setType("application/*");
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    public void shareFileToOtherApps(Context context, ArrayList<Uri> fileUris) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    public boolean call(Context context, String phone_num) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_num));
                context.startActivity(callIntent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkStoragePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkCameraPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkLocationPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkContactPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                )) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAccessibilityEnabled(Context context) {
        String LOGTAG = "TroopMessenger :";
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = "com.tvisha.troopmessenger/com.tvisha.troopmessenger.service.NotificatoinAccessibilityService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(LOGTAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d(LOGTAG, "***ACCESSIBILIY IS ENABLED***: ");
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    Log.d(LOGTAG, "Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                        Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Log.d(LOGTAG, "***END***");
        } else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

    public String replaceSpecialChar(String s) {
        try {
            if (s != null && s.length() > 0) {
                // s = "hellow";
                s = Helper.getInstance().convetSpecialCharacterToemojies(s);
                SpannableString spannableString = new SpannableString(s);
                //spannableString.setSpan(new RelativeSizeSpan(2f),);
                /*s = encodeEmoji(s);

                s = decodeEmoji(s);*/
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String addPencil(String message) {
        String covertmessage = message + "..." + "\uD83D\uDD89";
        return covertmessage;
    }

    public String replaceString(String message) {

        String convertMessage = message;
        convertMessage = convertMessage.replace(" &lsquo; ", "‘").replace(" :\u000f ", "☺️").replace(" \u0017 ", "\uD83D\uDE17")
                .replace(" &bull; ", "•").replace(" &pi; ", "π").replace(" &radic; ", "√").replace(" &div; ", "÷").replace(" &times; ", "×").replace(" &para; ", "¶").replace(" ∆&pound; ", "∆")
                .replace(" &ldquo; ", "").replace(" &nbsp; ", " ")
                .replace(" &cent; ", "£").replace(" &euro; ", "¢").replace(" &yen; ", "¥").replace(" &deg; ", "°")
                .replace(" &copy; ", "©").replace(" &circledR; ", "®").replace(" &trade; ", "™").replace(" &check; ", "✓")
                .replace(" &gt; ", ">").replace(" &lt; ", "<").replace(" &apos; ", "'").replace(" &quot; ", "\"")
                .replace(" &amp; ", "&").replace(" &apos; ", "'").replace(" &quot; ", "\"").replace(" &lt; ", "<").replace(" &gt; ", ">").replace(" &#10; ", "\n").replace(" &nbsp; ", " ")
                .replace(" \u0003 ", " \uD83D\uDE03").replace(" \u0004 ", "\uD83D\uDE04").replace(" \u0001 ", "\uD83D\uDE01")
                .replace(" \u0006 ", "\uD83D\uDE06").replace(" \u0000 ", "\uD83D\uDE00").replace(" 9\u000f ", "☹️")
                .replace(" \u0015 ", "\uD83D\uDE15")
                .replace(" \u0019 ", " \uD83D\uDE19").replace(" \u0016 ", " \uD83D\uDE16").replace("\u0011 ", "\uD83E\uDD11")
                .replace(" :smile: ", "\uD83D\uDE04").replace(" :-P ", "\uD83D\uDE1B").replace(" =P ", "\uD83D\uDE1B")
                .replace(" :blush: ", "\uD83D\uDE0A").replace(" :smiley: ", "\uD83D\uDE03")
                .replace(" :) ", "\uD83D\uDE42").replace(" :-) ", "\uD83D\uDE42").replace(" :smirk: ", "\uD83D\uDE0F").replace(" :slight_smile: ", "\uD83D\uDE42").replace(" =] ", "\uD83D\uDE42")
                .replace(" =) ", "\uD83D\uDE42").replace(" :] ", "\uD83D\uDE42")
                .replace(" ':) ", "\uD83D\uDE05").replace(" :sweat_smile: ", "\uD83D\uDE05").replace(" ':-) ", "\uD83D\uDE05").replace(" '=) ", "\uD83D\uDE05").replace(" ':D ", "\uD83D\uDE05")
                .replace(" ':-D ", "\uD83D\uDE05").replace(" '=D ", "\uD83D\uDE05")
                .replace(" :/ ", "\uD83D\uDE15").replace(" :disappointed: ", "\uD83D\uDE1E").replace(" :( ", "\uD83D\uDE1E")
                .replace(" :heart_eyes: ", "\uD83D\uDE0D").replace(" :kissing_heart: ", "\uD83D\uDE18")
                .replace(" :kissing_closed_eyes: ", "\uD83D\uDE1A").replace(" :flushed: ", "\uD83D\uDE33")
                .replace(" :laughing: ", "\uD83D\uDE06").replace(" :D ", "\uD83D\uDE03").replace(" :-D ", "\uD83D\uDE03").replace(" =D ", "\uD83D\uDE03").replace(" >:) ", "\uD83D\uDE06").replace(" >;) ", "\uD83D\uDE06")
                .replace(" >:-) ", "\uD83D\uDE06").replace(" >=) ", "\uD83D\uDE06").replace(" :wink: ", "\uD83D\uDE09").replace(" ;-) ", "\uD83D\uDE09").replace(" *-) ", "\uD83D\uDE09").replace(" *) ", "\uD83D\uDE09")
                .replace(" ;-] ", "\uD83D\uDE09").replace(" ;] ", "\uD83D\uDE09").replace(" ;D ", "\uD83D\uDE09").replace(" ;^ ) ", "\uD83D\uDE09")
                .replace(" :sweat: ", "\uD83D\uDE13").replace(" ':( ", "\uD83D\uDE13").replace("' :-( ", "\uD83D\uDE13").replace(" '=( ", "\uD83D\uDE13")
                .replace(" :kissing_heart: ", "\uD83D\uDE18").replace(" :* ", "\uD83D\uDE18").replace(" :-* ", "\uD83D\uDE18").replace(" =* ", "\uD83D\uDE18").replace(" :^* ", "\uD83D\uDE18")
                .replace(" :heart: ", "❤️").replace(" <3 ", "❤").replace(" :') ", "\uD83D\uDE02")
                .replace(" :joy: ", "\uD83D\uDE02").replace(" :'-) ", "\uD83D\uDE02")
                .replace(" :stuck_out_tongue_winking_eye: ", "\uD83D\uDE1C").replace(" >:P ", "\uD83D\uDE1C").replace(" X-P ", "\uD83D\uDE1C").replace(" x-p ", "\uD83D\uDE1C")
                .replace(" :disappointed: ", "\uD83D\uDE1E").replace(" >:[ ", "\uD83D\uDE1E").replace(" :-( ", "\uD83D\uDE1E").replace(" :( ", "\uD83D\uDE1E").replace(" :-[ ", "\uD83D\uDE1E").replace(" :[ ", "\uD83D\uDE1E").replace(" =( ", "\uD83D\uDE1E")
                .replace(" :angry: ", "\uD83D\uDE20").replace(" >:( ", "\uD83D\uDE20").replace(" >:-( ", "\uD83D\uDE20").replace(" :@ ", "\uD83D\uDE20")
                .replace(" :cry: ", "\uD83D\uDE22").replace(" :'( ", "\uD83D\uDE22").replace(" :'-( ", "\uD83D\uDE22").replace(" ;( ", "\uD83D\uDE22").replace(" ;-( ", "\uD83D\uDE22")
                .replace(" :persevere: ", "\uD83D\uDE23").replace(" >.< ", "\uD83D\uDE23")
                .replace(" :fearful: ", "\uD83D\uDE28").replace(" :flushed: ", "\uD83D\uDE33").replace(" :$ ", "\uD83D\uDE33").replace(" =$ ", "\uD83D\uDE33")
                .replace(" :dizzy_face: ", "\uD83D\uDE35").replace(" #-) ", "\uD83D\uDE35").replace("  #) ", "\uD83D\uDE35").replace(" %-) ", "\uD83D\uDE35").replace(" %) ", "\uD83D\uDE35").replace(" X) ", "\uD83D\uDE35").replace(" X-) ", "\uD83D\uDE35")
                .replace(" :ok_woman: ", "\uD83D\uDE46").replace(" *\\0 ", "\uD83D\uDE46").replace(" \\0/ ", "\uD83D\uDE46").replace(" *\\O ", "\uD83D\uDE46").replace(" \\O/ ", "\uD83D\uDE46")
                .replace(" :innocent: ", "\uD83D\uDE07").replace(" O:-) ", "\uD83D\uDE07").replace(" 0:-3 ", "\uD83D\uDE07").replace(" 0:3 ", "\uD83D\uDE07").replace(" 0:-) ", "\uD83D\uDE07").replace(" 0:) ", "\uD83D\uDE07").replace(" 0;^) ", "\uD83D\uDE07").replace(" O:-) ", "\uD83D\uDE07").replace(" O:) ", "\uD83D\uDE07").replace(" O;-) ", "\uD83D\uDE07").replace(" O=) ", "\uD83D\uDE07").replace(" 0;-) ", "\uD83D\uDE07").replace(" O:-3 ", "\uD83D\uDE07").replace(" O:3 ", "\uD83D\uDE07")
                .replace(" :sunglasses: ", "\uD83D\uDE0E").replace(" B-) ", "\uD83D\uDE0E").replace(" B) ", "\uD83D\uDE0E").replace(" 8) ", "\uD83D\uDE0E").replace(" 8-) ", "\uD83D\uDE0E").replace(" B-D ", "\uD83D\uDE0E").replace(" 8-D ", "\uD83D\uDE0E")
                .replace(" :expressionless: ", "\uD83D\uDE11").replace(" -_- ", "\uD83D\uDE11").replace(" -__- ", "\uD83D\uDE11").replace(" -___- ", "\uD83D\uDE11")
                .replace(" :confused: ", "\uD83D\uDE15").replace(" >:\\ ", "\uD83D\uDE15").replace(" >:/ ", "\uD83D\uDE15").replace(" :-/ ", "\uD83D\uDE15").replace(" :-. ", "\uD83D\uDE15").replace(" :/ ", "\uD83D\uDE15").replace(" :\\ ", "\uD83D\uDE15").replace(" =/ ", "\uD83D\uDE15").replace(" =\\ ", "\uD83D\uDE15").replace(" :L ", "\uD83D\uDE15").replace(" =L ", "\uD83D\uDE15")
                .replace(" :stuck_out_tongue: ", "\uD83D\uDE1B").replace(" :P ", "\uD83D\uDE1B").replace(" :-P ", "\uD83D\uDE1B").replace(" =P ", "\uD83D\uDE1B").replace(" :-p ", "\uD83D\uDE1B").replace(" :p ", "\uD83D\uDE1B").replace(" =p ", "\uD83D\uDE1B").replace(" :-Þ ", "\uD83D\uDE1B").replace(" :Þ ", "\uD83D\uDE1B").replace(" :þ ", "\uD83D\uDE1B").replace(" :-þ ", "\uD83D\uDE1B").replace(" :-b ", "\uD83D\uDE1B").replace(" :b ", "\uD83D\uDE1B").replace(" D: ", "\uD83D\uDE1B")
                .replace(" :open_mouth: ", "\uD83D\uDE2E").replace(" :-O ", "\uD83D\uDE2E").replace(" :O ", "\uD83D\uDE2E").replace(" :-o ", "\uD83D\uDE2E").replace(" :o ", "\uD83D\uDE2E").replace(" O_O ", "\uD83D\uDE2E").replace(" >:O ", "\uD83D\uDE2E")
                .replace(" :no_mouth: ", "\uD83D\uDE36").replace(" :-X ", "\uD83D\uDE36").replace(" :X ", "\uD83D\uDE36").replace(" :-# ", "\uD83D\uDE36").replace(" :# ", "\uD83D\uDE36").replace(" =X ", "\uD83D\uDE36").replace(" =x ", "\uD83D\uDE36").replace(" :x ", "\uD83D\uDE36").replace(" :-x ", "\uD83D\uDE36").replace(" =# ", "\uD83D\uDE36")
                .replace(" </3 ", "\uD83D\uDC94").replace(" :broken_heart: ", "\uD83D\uDC94")
                .replace(" \t ", "\uD83D\uDE09").replace(" \u001f ", "\uD83D\uDE1F")
                .replace(" :flag_tl: ", "\uD83C\uDDF9\uD83C\uDDF1").replace(" :kiss_mm: ", "\uD83D\uDC68\u200D❤️\u200D\uD83D\uDC8B\u200D\uD83D\uDC68")
                .replace(" :kiss_woman_man: ", "\uD83D\uDC69\u200D❤️\u200D\uD83D\uDC8B\u200D\uD83D\uDC68")
                .replace(" :woman_zombie: ", "\uD83E\uDDDF\u200D♀️").replace(" :man_zombie: ", "\uD83E\uDDDF\u200D♂️")
                .replace(" :kiss_ww: ", "\uD83D\uDC69\u200D❤️\u200D\uD83D\uDC8B\u200D\uD83D\uDC69")
                .replace(" :family_mmbb: ", "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66")
                .replace(" :family_mmgb: ", "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
                .replace(" :family_mmgg: ", "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67")
                .replace(" :family_mwbb: ", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66")
                .replace(" :family_mwgb: ", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
                .replace(" :family_mwgg: ", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67")
                .replace(" :family_wwbb: ", "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66")
                .replace(" :family_wwgb: ", "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
                .replace(" :family_wwgg: ", "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67")
                .replace(" :couple_mm: ", "\uD83D\uDC68\u200D❤️\u200D\uD83D\uDC68").replace(" :couple_with_heart_woman_man: ", "\uD83D\uDC69\u200D❤️\u200D\uD83D\uDC68")
                .replace(" :couple_ww: ", "\uD83D\uDC69\u200D❤️\u200D\uD83D\uDC69").replace(" :family_man_boy_boy: ", "\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66")
                .replace(" :family_man_girl_boy: ", "\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
                .replace(" :family_man_girl_girl: ", "\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67").replace(" :family_man_woman_boy: ", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66")
                .replace(" :family_mmb: ", "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66").replace(" :family_mmg: ", "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67")
                .replace(" :family_mwg: ", "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67").replace(" :family_woman_boy_boy: ", "\uD83D\uDC69\u200D\uD83D\uDC66\u200D\uD83D\uDC66")
                .replace(" :family_woman_girl_boy: ", "\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66").replace(" :family_woman_girl_girl: ", "\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC67")
                .replace(" :family_wwb: ", "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC66").replace(" :family_wwg: ", "\uD83D\uDC69\u200D\uD83D\uDC69\u200D\uD83D\uDC67")
                .replace(" :blond-haired_man_tone1: ", "\uD83D\uDC71\uD83C\uDFFB\u200D♂️").replace(" :blond-haired_woman_tone1: ", "\uD83D\uDC71\uD83C\uDFFB\u200D♀️")
                .replace(" :man_bowing_tone1: ", "\uD83D\uDE47\uD83C\uDFFB\u200D♂️").replace(" :man_construction_worker_tone1: ", "\uD83D\uDC77\uD83C\uDFFB\u200D♂️")
                .replace(" :man_detective_tone1: ", "\uD83D\uDD75\uD83C\uDFFB\u200D♂️").replace(" :man_elf_tone1: ", "\uD83E\uDDDD\uD83C\uDFFB\u200D♂️")
                .replace(" :man_facepalming_tone1: ", "\uD83E\uDD26\uD83C\uDFFB\u200D♂️").replace(" :man_fairy_tone1: ", "\uD83E\uDDDA\uD83C\uDFFB\u200D♂️")
                .replace(" :man_frowning_tone1: ", "\uD83D\uDE4D\uD83C\uDFFB\u200D♂️").replace(" :man_gesturing_no_tone1: ", "\uD83D\uDE45\uD83C\uDFFB\u200D♂️")
                .replace(" :man_gesturing_ok_tone1: ", "\uD83D\uDE46\uD83C\uDFFB\u200D♂️").replace(" :man_getting_face_massage_tone1: ", "\uD83D\uDC86\uD83C\uDFFB\u200D♂️")
                .replace(" :man_getting_haircut_tone1: ", "\uD83D\uDC87\uD83C\uDFFB\u200D♂️").replace(" :man_guard_tone1: ", "\uD83D\uDC82\uD83C\uDFFB\u200D♂️")
                .replace(" :man_health_worker_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D⚕️").replace(" :man_judge_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D⚖️")
                .replace(" :man_mage_tone1: ", "\uD83E\uDDD9\uD83C\uDFFB\u200D♂️").replace(" :man_pilot_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D✈️")
                .replace(" :man_police_officer_tone1: ", "\uD83D\uDC6E\uD83C\uDFFB\u200D♂️").replace(" :man_pouting_tone1: ", "\uD83D\uDE4E\uD83C\uDFFB\u200D♂️")
                .replace(" :man_raising_hand_tone1: ", "\uD83D\uDE4B\uD83C\uDFFB\u200D♂️").replace(" :man_running_tone1: ", "\uD83C\uDFC3\uD83C\uDFFB\u200D♂️")
                .replace(" :man_shrugging_tone1: ", "\uD83E\uDD37\uD83C\uDFFB\u200D♂️")
                .replace(" :person_running_tone1: ", "\uD83C\uDFC3\uD83C\uDFFB").replace(" :bath_tone1: ", "\uD83D\uDEC0\uD83C\uDFFB")
                .replace(" :person_in_bed_tone1: ", "\uD83D\uDECC\uD83C\uDFFB").replace(" :snowboarder_tone1: ", "\uD83C\uDFC2\uD83C\uDFFB")
                .replace(" :person_bouncing_ball_tone1: ", "⛹\uD83C\uDFFB").replace(" :man_surfing: ", "\uD83C\uDFC4\u200D♂️")
                .replace(" :man_swimming: ", "\uD83C\uDFCA\u200D♂️").replace(" :men_wrestling: ", "\uD83E\uDD3C\u200D♂️").replace(" :woman_biking: ", "\uD83D\uDEB4\u200D♀️")
                .replace(" :woman_cartwheeling: ", "\uD83E\uDD38\u200D♀️").replace(" :woman_climbing: ", "\uD83E\uDDD7\u200D♀️")
                .replace(" :woman_in_lotus_position: ", "\uD83E\uDDD8\u200D♀️").replace(" :woman_in_steamy_room: ", "\uD83E\uDDD6\u200D♀️")
                .replace(" :woman_juggling: ", "\uD83E\uDD39\u200D♀️").replace(" :woman_mountain_biking: ", "\uD83D\uDEB5\u200D♀️")
                .replace(" :woman_playing_handball: ", "\uD83E\uDD3E\u200D♀️").replace(" :woman_playing_water_polo: ", "\uD83E\uDD3D\u200D♀️")
                .replace(" :woman_rowing_boat: ", "\uD83D\uDEA3\u200D♀️").replace(" :woman_surfing: ", "\uD83C\uDFC4\u200D♀️").replace(" :woman_swimming: ", "\uD83C\uDFCA\u200D♀️")
                .replace(" :women_wrestling: ", "\uD83E\uDD3C\u200D♀️").replace(" :breast_feeding_tone1: ", "\uD83E\uDD31\uD83C\uDFFB").replace(" :horse_racing_tone1: ", "\uD83C\uDFC7\uD83C\uDFFB")
                .replace(" :person_biking_tone1: ", "\uD83D\uDEB4\uD83C\uDFFB").replace(" :person_climbing_tone1: ", "\uD83E\uDDD7\uD83C\uDFFB").replace(" :person_doing_cartwheel_tone1: ", "\uD83E\uDD38\uD83C\uDFFB")
                .replace(" :person_golfing_tone1: ", "\uD83C\uDFCC\uD83C\uDFFB").replace(" :person_in_lotus_position_tone1: ", "\uD83E\uDDD8\uD83C\uDFFB")
                .replace(" :person_in_steamy_room_tone1: ", "\uD83E\uDDD6\uD83C\uDFFB").replace(" :person_juggling_tone1: ", "\uD83E\uDD39\uD83C\uDFFB")
                .replace(" :person_lifting_weights_tone1: ", "\uD83C\uDFCB\uD83C\uDFFB").replace(" :person_mountain_biking_tone1: ", "\uD83D\uDEB5\uD83C\uDFFB")
                .replace(" :person_playing_handball_tone1: ", "\uD83E\uDD3E\uD83C\uDFFB").replace(" :person_playing_water_polo_tone1: ", "\uD83E\uDD3D\uD83C\uDFFB")
                .replace(" :person_rowing_boat_tone1: ", "\uD83D\uDEA3\uD83C\uDFFB").replace(" :person_surfing_tone1: ", "\uD83C\uDFC4\uD83C\uDFFB")
                .replace(" :person_swimming_tone1: ", "\uD83C\uDFCA\uD83C\uDFFB").replace(" :man_rowing_boat: ", "\uD83D\uDEA3\u200D♂️").replace(" :man_playing_water_polo: ", "\uD83E\uDD3D\u200D♂️").replace(" :man_playing_handball: ", "\uD83E\uDD3E\u200D♂️")
                .replace(" :man_mountain_biking: ", "\uD83D\uDEB5\u200D♂️").replace(" :man_juggling: ", "\uD83E\uDD39\u200D♂️").replace(" :man_in_steamy_room: ", "\uD83E\uDDD6\u200D♂️")
                .replace(" :man_in_lotus_position: ", "\uD83E\uDDD8\u200D♂️").replace(" :man_climbing: ", "\uD83E\uDDD7\u200D♂️").replace(" :man_cartwheeling: ", "\uD83E\uDD38\u200D♂️")
                .replace(" :man_biking: ", "\uD83D\uDEB4\u200D♂️").replace(" :woman_bouncing_ball: ", "⛹️\u200D♀️").replace(" :man_bouncing_ball: ", "⛹️\u200D♂️")
                .replace(" :woman_lifting_weights: ", "\uD83C\uDFCB️\u200D♀️").replace(" :woman_golfing: ", "\uD83C\uDFCC️\u200D♀️").replace(" :woman_bouncing_ball_tone1: ", "⛹\uD83C\uDFFB\u200D♀️")
                .replace(" :man_lifting_weights: ", "\uD83C\uDFCB️\u200D♂️").replace(" :man_golfing: ", "\uD83C\uDFCC️\u200D♂️").replace(" :man_bouncing_ball_tone1: ", "⛹\uD83C\uDFFB\u200D♂️")
                .replace(" :woman_swimming_tone1: ", "\uD83C\uDFCA\uD83C\uDFFB\u200D♀️").replace(" :woman_surfing_tone1: ", "\uD83C\uDFC4\uD83C\uDFFB\u200D♀️").replace(" :woman_rowing_boat_tone1: ", "\uD83D\uDEA3\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_playing_water_polo_tone1: ", "\uD83E\uDD3D\uD83C\uDFFB\u200D♀️").replace(" :woman_playing_handball_tone1: ", "\uD83E\uDD3E\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_mountain_biking_tone1: ", "\uD83D\uDEB5\uD83C\uDFFB\u200D♀️").replace(" :woman_lifting_weights_tone1: ", "\uD83C\uDFCB\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_juggling_tone1: ", "\uD83E\uDD39\uD83C\uDFFB\u200D♀️").replace(" :woman_in_steamy_room_tone1: ", "\uD83E\uDDD6\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_in_lotus_position_tone1: ", "\uD83E\uDDD8\uD83C\uDFFB\u200D♀️").replace(" :woman_golfing_tone1: ", "\uD83C\uDFCC\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_climbing_tone1: ", "\uD83E\uDDD7\uD83C\uDFFB\u200D♀️").replace(" :woman_cartwheeling_tone1: ", "\uD83E\uDD38\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_biking_tone1: ", "\uD83D\uDEB4\uD83C\uDFFB\u200D♀️").replace(" :man_biking_tone1: ", "\uD83D\uDEB4\uD83C\uDFFB\u200D♂️").replace(" :man_cartwheeling_tone1: ", "\uD83E\uDD38\uD83C\uDFFB\u200D♂️")
                .replace(" :man_climbing_tone1: ", "\uD83E\uDDD7\uD83C\uDFFB\u200D♂️").replace(" :man_golfing_tone1: ", "\uD83C\uDFCC\uD83C\uDFFB\u200D♂️").replace(" :man_in_lotus_position_tone1: ", "\uD83E\uDDD8\uD83C\uDFFB\u200D♂️")
                .replace(" :man_in_steamy_room_tone1: ", "\uD83E\uDDD6\uD83C\uDFFB\u200D♂️").replace(" :man_juggling_tone1: ", "\uD83E\uDD39\uD83C\uDFFB\u200D♂️").replace(" :man_lifting_weights_tone1: ", "\uD83C\uDFCB\uD83C\uDFFB\u200D♂️")
                .replace(" :man_mountain_biking_tone1: ", "\uD83D\uDEB5\uD83C\uDFFB\u200D♂️").replace(" :man_playing_handball_tone1: ", "\uD83E\uDD3E\uD83C\uDFFB\u200D♂️").replace(" :man_playing_water_polo_tone1: ", "\uD83E\uDD3D\uD83C\uDFFB\u200D♂️")
                .replace(" :man_rowing_boat_tone1: ", "\uD83D\uDEA3\uD83C\uDFFB\u200D♂️").replace(" :man_surfing_tone1: ", "\uD83C\uDFC4\uD83C\uDFFB\u200D♂️").replace(" :man_swimming_tone1: ", "\uD83C\uDFCA\uD83C\uDFFB\u200D♂️")
                .replace(" &circledR; ", " ®️ ").replace(" &spades; ", " ♠️ ").replace(" &trade; ", " ™️ ").replace(" &EmptySmallSquare; ", " ◻️ ")
                .replace(" &EmptyVerySmallSquare; ", "️ ▫ ").replace(" &hookleftarrow; ", " ↩️️ ").replace(" &harr; ", " ↔ ").replace(" &hearts; ", " ♥️ ")
                .replace(" &diamondsuit; ️", " ♦️ ").replace(" &copy; ", " ©️ ").replace(" &clubs; ", " ♣️ ").replace(" :two: ", "2️⃣")
                .replace(" :zero: ", "0️⃣").replace(" &cudarrr; ", " ⤵️ ").replace(" &LowerLeftArrow; ", " ↙️️ ").replace(" &LowerRightArrow;️ ", " ↘ ")
                .replace(" &hookrightarrow; ", " ↪️ ").replace(" &updownarrow; ", " ↕️ ️").replace(" &nearr; ", " ↗ ️").replace(" &nwarr; ", " ↖ ").replace(" &FilledSmallSquare; ", " ◼️️ ").replace(" &blacksquare; ", " ▪️ ").replace(" &blacksquare; ", " ▪ ")
                .replace(" :three: ", "3️⃣").replace(" :six: ", "6️⃣").replace(" :seven: ", "7️⃣").replace(" :one: ", "1️⃣").replace(" :nine: ", "9️⃣").replace(" :hash: ", "#️⃣").replace(" :four: ", "4️⃣").replace(" :five: ", "5️⃣").replace(" :eight: ", "8️⃣").replace(" :asterisk: ", "*️⃣")
                .replace(" :eye_in_speech_bubble: ", "\uD83D\uDC41️\u200D\uD83D\uDDE8️").replace(" :united_nations: ", "\uD83C\uDDFA\uD83C\uDDF3")
                .replace(" :flag_zw: ", "\uD83C\uDDFF\uD83C\uDDFC").replace(" :flag_za: ", "\uD83C\uDDFF\uD83C\uDDF2").replace(" :flag_yt: ", "\uD83C\uDDFF\uD83C\uDDE6")
                .replace(" :flag_ye: ", "\uD83C\uDDFE\uD83C\uDDF9").replace(" :flag_xk: ", "\uD83C\uDDFE\uD83C\uDDEA").replace(" :flag_ws: ", "\uD83C\uDDFD\uD83C\uDDF0")
                .replace(" :flag_wf: ", "\uD83C\uDDFC\uD83C\uDDEB").replace(" :flag_vu: ", "\uD83C\uDDFB\uD83C\uDDFA").replace(" :flag_vn: ", "\uD83C\uDDFB\uD83C\uDDF3")
                .replace(" :flag_vi: ", "\uD83C\uDDFB\uD83C\uDDEE").replace(" :flag_vg: ", "\uD83C\uDDFB\uD83C\uDDEC").replace(" :flag_ve: ", "\uD83C\uDDFB\uD83C\uDDEA")
                .replace(" :flag_vc: ", "\uD83C\uDDFB\uD83C\uDDE8").replace(" :flag_va: ", "\uD83C\uDDFB\uD83C\uDDE6").replace(" :flag_sn: ", "\uD83C\uDDF8\uD83C\uDDF3")
                .replace(" :flag_so: ", "\uD83C\uDDF8\uD83C\uDDF4").replace(" :flag_sr: ", "\uD83C\uDDF8\uD83C\uDDF7").replace(" :flag_ss: ", "\uD83C\uDDF8\uD83C\uDDF8")
                .replace(" :flag_st: ", "\uD83C\uDDF8\uD83C\uDDF9").replace(" :flag_sv: ", "\uD83C\uDDF8\uD83C\uDDFB").replace(" :flag_sx: ", "\uD83C\uDDF8\uD83C\uDDFD")
                .replace(" :flag_sy: ", "\uD83C\uDDF8\uD83C\uDDFE").replace(" :flag_sz: ", "\uD83C\uDDF8\uD83C\uDDFF").replace(" :flag_ta: ", "\uD83C\uDDF9\uD83C\uDDE6")
                .replace(" :flag_tc: ", "\uD83C\uDDF9\uD83C\uDDE8").replace(" :flag_td: ", "\uD83C\uDDF9\uD83C\uDDE9").replace(" :flag_tf: ", "\uD83C\uDDF9\uD83C\uDDEB")
                .replace(" :flag_tg: ", "\uD83C\uDDF9\uD83C\uDDEC").replace(" :flag_th: ", "\uD83C\uDDF9\uD83C\uDDED").replace(" :flag_tj: ", "\uD83C\uDDF9\uD83C\uDDEF")
                .replace(" :flag_tk: ", "\uD83C\uDDF9\uD83C\uDDF0").replace(" :flag_tl: ", "\uD83C\uDDF9\uD83C\uDDF1").replace(" :flag_tm: ", "\uD83C\uDDF9\uD83C\uDDF2")
                .replace(" :flag_tn: ", "\uD83C\uDDF9\uD83C\uDDF3").replace(" :flag_to: ", "\uD83C\uDDF9\uD83C\uDDF4").replace(" :flag_tr: ", "\uD83C\uDDF9\uD83C\uDDF7")
                .replace(" :flag_tt: ", "\uD83C\uDDF9\uD83C\uDDF9").replace(" :flag_tv: ", "\uD83C\uDDF9\uD83C\uDDFB").replace(" :flag_tw: ", "\uD83C\uDDF9\uD83C\uDDFC")
                .replace(" :flag_tz: ", "\uD83C\uDDF9\uD83C\uDDFF").replace(" :flag_ua: ", "\uD83C\uDDFA\uD83C\uDDE6").replace(" :flag_ug: ", "\uD83C\uDDFA\uD83C\uDDEC")
                .replace(" :flag_um: ", "\uD83C\uDDFA\uD83C\uDDF2").replace(" :flag_us: ", "\uD83C\uDDFA\uD83C\uDDF8").replace(" :flag_uy: ", "\uD83C\uDDFA\uD83C\uDDFE")
                .replace(" :flag_uz: ", "\uD83C\uDDFA\uD83C\uDDFF").replace(" :flag_tg: ", "\uD83C\uDDF9\uD83C\uDDEC").replace(" :flag_th: ", "\uD83C\uDDF9\uD83C\uDDED").replace(" :flag_tj: ", "\uD83C\uDDF9\uD83C\uDDEF").replace(" :flag_tk: ", "\uD83C\uDDF9\uD83C\uDDF0").replace(" :flag_tl: ", "\uD83C\uDDF9\uD83C\uDDF1").replace(" :flag_tm: ", "\uD83C\uDDF9\uD83C\uDDF2").replace(" :flag_tn: ", "\uD83C\uDDF9\uD83C\uDDF3")
                .replace(" :flag_sm: ", "\uD83C\uDDF8\uD83C\uDDF2").replace(" :flag_sl: ", "\uD83C\uDDF8\uD83C\uDDF1").replace(" :flag_sk: ", "\uD83C\uDDF8\uD83C\uDDF0").replace(" :flag_sj: ", "\uD83C\uDDF8\uD83C\uDDEF").replace(" :flag_si: ", "\uD83C\uDDF8\uD83C\uDDEE").replace(" :flag_sh: ", "\uD83C\uDDF8\uD83C\uDDED").replace(" :flag_sg: ", "\uD83C\uDDF8\uD83C\uDDEC").replace(" :flag_se: ", "\uD83C\uDDF8\uD83C\uDDEA").replace(" :flag_sd: ", "\uD83C\uDDF8\uD83C\uDDE9").replace(" :flag_sc: ", "\uD83C\uDDF8\uD83C\uDDE8").replace(" :flag_sb: ", "\uD83C\uDDF8\uD83C\uDDE7").replace(" :flag_sa: ", "\uD83C\uDDF8\uD83C\uDDE6").replace(" :flag_sm: ", "\uD83C\uDDF8\uD83C\uDDE6").replace(" :flag_rw: ", "\uD83C\uDDF7\uD83C\uDDFC").replace(" :flag_ru: ", "\uD83C\uDDF7\uD83C\uDDFA").replace(" :flag_rs: ", "\uD83C\uDDF7\uD83C\uDDF8").replace(" :flag_ro: ", "\uD83C\uDDF7\uD83C\uDDF4").replace(" :flag_re: ", "\uD83C\uDDF7\uD83C\uDDEA").replace(" :flag_qa: ", "\uD83C\uDDF6\uD83C\uDDE6").replace(" :flag_py: ", "\uD83C\uDDF5\uD83C\uDDFE")
                .replace(" :flag_pw: ", "\uD83C\uDDF5\uD83C\uDDFC").replace(" :flag_pt: ", "\uD83C\uDDF5\uD83C\uDDF9").replace(" :flag_ps: ", "\uD83C\uDDF5\uD83C\uDDF8").replace(" :flag_pr: ", "\uD83C\uDDF5\uD83C\uDDF7").replace(" :flag_pn: ", "\uD83C\uDDF5\uD83C\uDDF3").replace(" :flag_pm: ", "\uD83C\uDDF5\uD83C\uDDF2").replace(" :flag_pl: ", "\uD83C\uDDF5\uD83C\uDDF1").replace(" :flag_pk: ", "\uD83C\uDDF5\uD83C\uDDF0").replace(" :flag_ph: ", "\uD83C\uDDF5\uD83C\uDDED").replace(" :flag_pg: ", "\uD83C\uDDF5\uD83C\uDDEC").replace(" :flag_pf: ", "\uD83C\uDDF5\uD83C\uDDEB").replace(" :flag_pe: ", "\uD83C\uDDF5\uD83C\uDDEA").replace(" :flag_pa: ", "\uD83C\uDDF5\uD83C\uDDE6")
                .replace(" :flag_mt: ", "\uD83C\uDDF2\uD83C\uDDF9").replace(" :flag_om: ", "\uD83C\uDDF4\uD83C\uDDF2").replace(" :flag_nz: ", "\uD83C\uDDF3\uD83C\uDDFF").replace(" :flag_nu: ", "\uD83C\uDDF3\uD83C\uDDFA").replace(" :flag_nr: ", "\uD83C\uDDF3\uD83C\uDDF7").replace(" :flag_np: ", "\uD83C\uDDF3\uD83C\uDDF5").replace(" :flag_no: ", "\uD83C\uDDF3\uD83C\uDDF4").replace(" :flag_nl: ", "\uD83C\uDDF3\uD83C\uDDF1").replace(" :flag_ni: ", "\uD83C\uDDF3\uD83C\uDDEE").replace(" :flag_ng: ", "\uD83C\uDDF3\uD83C\uDDEC").replace(" :flag_ne: ", "\uD83C\uDDF3\uD83C\uDDEA").replace(" :flag_nc: ", "\uD83C\uDDF3\uD83C\uDDE8").replace(" :flag_na: ", "\uD83C\uDDF3\uD83C\uDDE6").replace(" :flag_mz: ", "\uD83C\uDDF2\uD83C\uDDFF").replace(" :flag_my: ", "\uD83C\uDDF2\uD83C\uDDFE").replace(" :flag_mx: ", "\uD83C\uDDF2\uD83C\uDDFD").replace(" :flag_mw: ", "\uD83C\uDDF2\uD83C\uDDFC").replace(" :flag_mv: ", "\uD83C\uDDF2\uD83C\uDDFB").replace(" :flag_mu: ", "\uD83C\uDDF2\uD83C\uDDFA")
                .replace(" :flag_ms: ", "\uD83C\uDDF2\uD83C\uDDF8").replace(" :flag_mr: ", "\uD83C\uDDF2\uD83C\uDDF7").replace(" :flag_mq: ", "\uD83C\uDDF2\uD83C\uDDF6").replace(" :flag_mp: ", "\uD83C\uDDF2\uD83C\uDDF5").replace(" :flag_mo: ", "\uD83C\uDDF2\uD83C\uDDF4").replace(" :flag_mn: ", "\uD83C\uDDF2\uD83C\uDDF3").replace(" :flag_mm: ", "\uD83C\uDDF2\uD83C\uDDF2").replace(" :flag_ml: ", "\uD83C\uDDF2\uD83C\uDDF1").replace(" :flag_mk: ", "\uD83C\uDDF2\uD83C\uDDF0").replace(" :flag_mh: ", "\uD83C\uDDF2\uD83C\uDDED").replace(" :flag_mg: ", "\uD83C\uDDF2\uD83C\uDDEC").replace(" :flag_mf: ", "\uD83C\uDDF2\uD83C\uDDEB")
                .replace(" :flag_me: ", "\uD83C\uDDF2\uD83C\uDDEA").replace(" :flag_md: ", "\uD83C\uDDF2\uD83C\uDDE9").replace(" :flag_mc: ", "\uD83C\uDDF2\uD83C\uDDE8").replace(" :flag_ma: ", "\uD83C\uDDF2\uD83C\uDDE6").replace(" :flag_ly: ", "\uD83C\uDDF1\uD83C\uDDFE").replace(" :flag_lv: ", "\uD83C\uDDF1\uD83C\uDDFB").replace(" :flag_lu: ", "\uD83C\uDDF1\uD83C\uDDFA").replace(" :flag_lt: ", "\uD83C\uDDF1\uD83C\uDDF9").replace(" :flag_ls: ", "\uD83C\uDDF1\uD83C\uDDF8").replace(" :flag_lr: ", "\uD83C\uDDF1\uD83C\uDDF7").replace(" :flag_lk: ", "\uD83C\uDDF1\uD83C\uDDF0").replace(" :flag_li: ", "\uD83C\uDDF1\uD83C\uDDEE").replace(" :flag_lc: ", "\uD83C\uDDF1\uD83C\uDDE8").replace(" :flag_lb: ", "\uD83C\uDDF1\uD83C\uDDE7").replace(" :flag_la: ", "\uD83C\uDDF1\uD83C\uDDE6")
                .replace(" :flag_kz:  ", "\uD83C\uDDF0\uD83C\uDDFF").replace(" :flag_ky: ", "\uD83C\uDDF0\uD83C\uDDFE").replace(" :flag_kw: ", "\uD83C\uDDF0\uD83C\uDDFC").replace(" :flag_kr: ", "\uD83C\uDDF0\uD83C\uDDF7").replace(" :flag_kp: ", "\uD83C\uDDF0\uD83C\uDDF5").replace(" :flag_kn: ", "\uD83C\uDDF0\uD83C\uDDF3").replace(" :flag_km: ", "\uD83C\uDDF0\uD83C\uDDF2").replace(" :flag_ki: ", "\uD83C\uDDF0\uD83C\uDDEE").replace(" :flag_kh: ", "\uD83C\uDDF0\uD83C\uDDED").replace(" :flag_kg: ", "\uD83C\uDDF0\uD83C\uDDEC").replace(" :flag_ke: ", "\uD83C\uDDF0\uD83C\uDDEA").replace(" :flag_jp: ", "\uD83C\uDDEF\uD83C\uDDF5").replace(" :flag_jo: ", "\uD83C\uDDEF\uD83C\uDDF4").replace(" :flag_jm: ", "\uD83C\uDDEF\uD83C\uDDF2").replace(" :flag_je: ", "\uD83C\uDDEF\uD83C\uDDEA")
                .replace(" :flag_it: ", "\uD83C\uDDEE\uD83C\uDDF9").replace(" :flag_is: ", "\uD83C\uDDEE\uD83C\uDDF8")
                .replace(" :flag_gd: ", "\uD83C\uDDEC\uD83C\uDDE9").replace(" :flag_ge: ", "\uD83C\uDDEC\uD83C\uDDEA").replace(" :flag_gf: ", "\uD83C\uDDEC\uD83C\uDDEB").replace(" :flag_gg: ", "\uD83C\uDDEC\uD83C\uDDEC").replace(" :flag_gh: ", "\uD83C\uDDEC\uD83C\uDDED").replace(" :flag_gi: ", "\uD83C\uDDEC\uD83C\uDDEE").replace(" :flag_gl: ", "\uD83C\uDDEC\uD83C\uDDF1").replace(" :flag_gm: ", "\uD83C\uDDEC\uD83C\uDDF2").replace(" :flag_gn: ", "\uD83C\uDDEC\uD83C\uDDF3").replace(" :flag_gp: ", "\uD83C\uDDEC\uD83C\uDDF5").replace(" :flag_gq: ", "\uD83C\uDDEC\uD83C\uDDF6").replace(" :flag_gr: ", "\uD83C\uDDEC\uD83C\uDDF7").replace(" :flag_gs: ", "\uD83C\uDDEC\uD83C\uDDF8").replace(" :flag_gt: ", "\uD83C\uDDEC\uD83C\uDDF9").replace(" :flag_gu: ", "\uD83C\uDDEC\uD83C\uDDFA").replace(" :flag_gw: ", "\uD83C\uDDEC\uD83C\uDDFC").replace(" :flag_gy: ", "\uD83C\uDDEC\uD83C\uDDFE").replace(" :flag_hk: ", "\uD83C\uDDED\uD83C\uDDF0").replace(" :flag_hm: ", "\uD83C\uDDED\uD83C\uDDF2")
                .replace(" :flag_hn: ", "\uD83C\uDDED\uD83C\uDDF3").replace(" :flag_hr: ", "\uD83C\uDDED\uD83C\uDDF7").replace(" :flag_ht: ", "\uD83C\uDDED\uD83C\uDDF9").replace(" :flag_hu: ", "\uD83C\uDDED\uD83C\uDDFA").replace(" :flag_ic: ", "\uD83C\uDDEE\uD83C\uDDE8").replace(" :flag_id: ", "\uD83C\uDDEE\uD83C\uDDE9").replace(" :flag_ie: ", "\uD83C\uDDEE\uD83C\uDDEA").replace(" :flag_il: ", "\uD83C\uDDEE\uD83C\uDDF1").replace(" :flag_im: ", "\uD83C\uDDEE\uD83C\uDDF2").replace(" :flag_in: ", "\uD83C\uDDEE\uD83C\uDDF3").replace(" :flag_io: ", "\uD83C\uDDEE\uD83C\uDDF4").replace(" :flag_iq: ", "\uD83C\uDDEE\uD83C\uDDF6").replace(" :flag_ir: ", "\uD83C\uDDEE\uD83C\uDDF7").replace(" :flag_gb: ", "\uD83C\uDDEC\uD83C\uDDE7").replace(" :flag_ga: ", "\uD83C\uDDEC\uD83C\uDDE6").replace(" :flag_fr: ", "\uD83C\uDDEB\uD83C\uDDF7").replace(" :flag_fo: ", "\uD83C\uDDEB\uD83C\uDDF4").replace(" :flag_fm: ", "\uD83C\uDDEB\uD83C\uDDF2").replace(" :flag_fk: ", "\uD83C\uDDEB\uD83C\uDDF0")
                .replace(" :flag_fj: ", "\uD83C\uDDEB\uD83C\uDDEF").replace(" :flag_fi: ", "\uD83C\uDDEB\uD83C\uDDEE").replace(" :flag_eu: ", "\uD83C\uDDEA\uD83C\uDDFA").replace(" :flag_et: ", "\uD83C\uDDEA\uD83C\uDDF9").replace(" :flag_es: ", "\uD83C\uDDEA\uD83C\uDDF8").replace(" :flag_er:", "\uD83C\uDDEA\uD83C\uDDF7").replace(" :flag_eh: ", "\uD83C\uDDEA\uD83C\uDDED").replace(" :flag_eg: ", "\uD83C\uDDEA\uD83C\uDDEC").replace(" :flag_ee: ", "\uD83C\uDDEA\uD83C\uDDEA").replace(" :flag_ec: ", "\uD83C\uDDEA\uD83C\uDDE8").replace(" :flag_ea: ", "\uD83C\uDDEA\uD83C\uDDE6").replace(" :flag_dz: ", "\uD83C\uDDE9\uD83C\uDDFF").replace(" :flag_do: ", "\uD83C\uDDE9\uD83C\uDDF4").replace(" :flag_dm: ", "\uD83C\uDDE9\uD83C\uDDF2").replace(" :flag_dk: ", "\uD83C\uDDE9\uD83C\uDDF0")
                .replace(" :flag_dj: ", "\uD83C\uDDE9\uD83C\uDDEF").replace(" :flag_dg: ", "\uD83C\uDDE9\uD83C\uDDEC").replace(" :flag_de: ", "\uD83C\uDDE9\uD83C\uDDEA").replace(" :flag_cz: ", "\uD83C\uDDE8\uD83C\uDDFF").replace(" :flag_cy: ", "\uD83C\uDDE8\uD83C\uDDFE").replace(" :flag_cx: ", "\uD83C\uDDE8\uD83C\uDDFD").replace(" :flag_cw: ", "\uD83C\uDDE8\uD83C\uDDFC").replace(" :flag_cv: ", "\uD83C\uDDE8\uD83C\uDDFB").replace(" :flag_cu: ", "\uD83C\uDDE8\uD83C\uDDFA").replace(" :flag_cr: ", "\uD83C\uDDE8\uD83C\uDDF7").replace(" :flag_cp: ", "\uD83C\uDDE8\uD83C\uDDF5").replace(" :flag_bb: ", "\uD83C\uDDE7\uD83C\uDDE7").replace(" :flag_bd: ", "\uD83C\uDDE7\uD83C\uDDE9").replace(" :flag_be: ", "\uD83C\uDDE7\uD83C\uDDEA").replace(" :flag_bf: ", "\uD83C\uDDE7\uD83C\uDDEB").replace(" :flag_bg: ", "\uD83C\uDDE7\uD83C\uDDEC").replace(" :flag_bh: ", "\uD83C\uDDE7\uD83C\uDDED").replace(" :flag_bi: ", "\uD83C\uDDE7\uD83C\uDDEE").replace(" :flag_bj: ", "\uD83C\uDDE7\uD83C\uDDEF").replace(" :flag_bl: ", "\uD83C\uDDE7\uD83C\uDDF1").replace(" :flag_bm: ", "\uD83C\uDDE7\uD83C\uDDF2")
                .replace(" :flag_bn: ", "\uD83C\uDDE7\uD83C\uDDF3").replace(" :flag_bo: ", "\uD83C\uDDE7\uD83C\uDDF4").replace(" :flag_bq: ", "\uD83C\uDDE7\uD83C\uDDF6").replace(" :flag_br: ", "\uD83C\uDDE7\uD83C\uDDF7").replace(" :flag_bs: ", "\uD83C\uDDE7\uD83C\uDDF8").replace(" :flag_bt: ", "\uD83C\uDDE7\uD83C\uDDF9").replace(" :flag_bv: ", "\uD83C\uDDE7\uD83C\uDDFB").replace(" :flag_bw: ", "\uD83C\uDDE7\uD83C\uDDFC").replace(" :flag_by: ", "\uD83C\uDDE7\uD83C\uDDFE").replace(" :flag_bz: ", "\uD83C\uDDE7\uD83C\uDDFF").replace(" :flag_ca: ", "\uD83C\uDDE8\uD83C\uDDE6").replace(" :flag_cc: ", "\uD83C\uDDE8\uD83C\uDDE8").replace(" :flag_cd: ", "\uD83C\uDDE8\uD83C\uDDE9").replace(" :flag_cf: ", "\uD83C\uDDE8\uD83C\uDDEB").replace(" :flag_cg: ", "\uD83C\uDDE8\uD83C\uDDEC").replace(" :flag_ch: ", "\uD83C\uDDE8\uD83C\uDDED").replace(" :flag_ci: ", "\uD83C\uDDE8\uD83C\uDDEE").replace(" :flag_ck: ", "\uD83C\uDDE8\uD83C\uDDF0").replace(" :flag_cl: ", "\uD83C\uDDE8\uD83C\uDDF1").replace(" :flag_cm: ", "\uD83C\uDDE8\uD83C\uDDF2")
                .replace(" :flag_cn: ", "\uD83C\uDDE8\uD83C\uDDF3").replace(" :flag_co: ", "\uD83C\uDDE8\uD83C\uDDF4").replace(" :england: ", "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F").replace(" :scotland: ", "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F").replace(" :wales: ", "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F").replace(" :rainbow_flag: ", "\uD83C\uDFF3️\u200D\uD83C\uDF08").replace(" :flag_ac: ", "\uD83C\uDDE6\uD83C\uDDE8").replace(" :flag_ad: ", "\uD83C\uDDE6\uD83C\uDDE9").replace(" :flag_ae: ", "\uD83C\uDDE6\uD83C\uDDEA").replace(" :flag_af: ", "\uD83C\uDDE6\uD83C\uDDEB").replace(" :flag_ag: ", "\uD83C\uDDE6\uD83C\uDDEC").replace(" :flag_ai: ", "\uD83C\uDDE6\uD83C\uDDEE").replace(" :flag_al: ", "\uD83C\uDDE6\uD83C\uDDF1").replace(" :flag_am: ", "\uD83C\uDDE6\uD83C\uDDF2").replace(" :flag_ao: ", "\uD83C\uDDE6\uD83C\uDDF4").replace(" :flag_aq: ", "\uD83C\uDDE6\uD83C\uDDF6").replace(" :flag_ar:", "\uD83C\uDDE6\uD83C\uDDF7").replace(" :flag_as: ", "\uD83C\uDDE6\uD83C\uDDF8").replace(" :flag_at: ", "\uD83C\uDDE6\uD83C\uDDF9").replace(" :flag_au: ", "\uD83C\uDDE6\uD83C\uDDFA")
                .replace(" :flag_aw: ", "\uD83C\uDDE6\uD83C\uDDFC").replace(" :flag_ax: ", "\uD83C\uDDE6\uD83C\uDDFD").replace(" :flag_az: ", "\uD83C\uDDE6\uD83C\uDDFF").replace(" :flag_ba: ", "\uD83C\uDDE7\uD83C\uDDE6")
                .replace(" &female;  ", " ♀️ ").replace(" :woman_with_headscarf_tone1: ", "\uD83E\uDDD5\uD83C\uDFFB").replace(" :woman_tone1: ", "\uD83D\uDC69\uD83C\uDFFB").replace(" :vampire_tone1: ", "\uD83E\uDDDB\uD83C\uDFFB").replace(" :selfie_tone1: ", "\uD83E\uDD33\uD83C\uDFFB").replace(" :santa_tone1: ", "\uD83C\uDF85\uD83C\uDFFB").replace(" :princess_tone1: ", "\uD83D\uDC78\uD83C\uDFFB").replace(" :prince_tone1: ", "\uD83E\uDD34\uD83C\uDFFB").replace(" :pregnant_woman_tone1: ", "\uD83E\uDD30\uD83C\uDFFB").replace(" :pray_tone1: ", "\uD83D\uDE4F\uD83C\uDFFB").replace(" :fairy_tone1: ", "\uD83E\uDDDA\uD83C\uDFFB").replace(" :girl_tone1: ", "\uD83D\uDC67\uD83C\uDFFB").replace(" :guard_tone1: ", "\uD83D\uDC82\uD83C\uDFFB").replace(" :mage_tone1: ", "\uD83E\uDDD9\uD83C\uDFFB").replace(" :man_dancing_tone1: ", "\uD83D\uDD7A\uD83C\uDFFB").replace(" :man_in_business_suit_levitating_tone1: ", "\uD83D\uDD74\uD83C\uDFFB").replace(" :man_in_tuxedo_tone1: ", "\uD83E\uDD35\uD83C\uDFFB").replace(" :man_tone1: ", "\uD83D\uDC68\uD83C\uDFFB").replace(" :man_with_chinese_cap_tone1: ", "\uD83D\uDC72\uD83C\uDFFB").replace(" :merperson_tone1: ", "\uD83E\uDDDC\uD83C\uDFFB").replace(" :mrs_claus_tone1: ", "\uD83E\uDD36\uD83C\uDFFB").replace(" :muscle_tone1: ", "\uD83D\uDCAA\uD83C\uDFFB").replace(" :nail_care_tone1: ", "\uD83D\uDC85\uD83C\uDFFB").replace(" :nose_tone1: ", "\uD83D\uDC43\uD83C\uDFFB")
                .replace(" :older_adult_tone1: ", "\uD83E\uDDD3\uD83C\uDFFB").replace(" :older_man_tone1: ", "\uD83D\uDC74\uD83C\uDFFB").replace(" :older_woman_tone1: ", "\uD83D\uDC75\uD83C\uDFFB").replace(" :person_bowing_tone1: ", "\uD83D\uDE47\uD83C\uDFFB").replace(" :person_facepalming_tone1: ", "\uD83D\uDC69\uD83C\uDFFB").replace(" :person_frowning_tone1: ", "\uD83D\uDE4D\uD83C\uDFFB").replace(" :person_gesturing_no_tone1: ", "\uD83D\uDE45\uD83C\uDFFB").replace(" :person_gesturing_ok_tone1: ", "\uD83D\uDE46\uD83C\uDFFB").replace(" :person_getting_haircut_tone1: ", "\uD83D\uDC87\uD83C\uDFFB").replace(" :person_getting_massage_tone1: ", "\uD83D\uDC86\uD83C\uDFFB").replace(" :person_pouting_tone1: ", "\uD83D\uDE4E\uD83C\uDFFB").replace(" :person_raising_hand_tone1: ", "\uD83D\uDE4B\uD83C\uDFFB").replace(" :person_running_tone1: ", "\uD83C\uDFC3\uD83C\uDFFB").replace(" :person_shrugging_tone1: ", "\uD83E\uDD37\uD83C\uDFFB").replace(" :person_tipping_hand_tone1: ", "\uD83D\uDC81\uD83C\uDFFB").replace(" :person_walking_tone1: ", "\uD83D\uDEB6\uD83C\uDFFB").replace(" :person_wearing_turban_tone1: ", "\uD83D\uDC73\uD83C\uDFFB").replace(" :police_officer_tone1: ", "\uD83D\uDC6E\uD83C\uDFFB").replace(" :elf_tone1: ", "\uD83E\uDDDD\uD83C\uDFFB").replace(" :ear_tone1: ", "\uD83D\uDC42\uD83C\uDFFB")
                .replace(" :detective_tone1: ", "\uD83D\uDD75\uD83C\uDFFB").replace(" :dancer_tone1: ", "\uD83D\uDC83\uD83C\uDFFB").replace(" :construction_worker_tone1: ", "\uD83D\uDC77\uD83C\uDFFB").replace(" :child_tone1: ", "\uD83E\uDDD2\uD83C\uDFFB").replace(" :bride_with_veil_tone1: ", "\uD83D\uDC70\uD83C\uDFFB").replace(" :boy_tone1: ", "\uD83D\uDC66\uD83C\uDFFB").replace(" :blond_haired_person_tone1: ", "\uD83D\uDC71\uD83C\uDFFB").replace(" :bearded_person_tone1: ", "\uD83E\uDDD4\uD83C\uDFFB").replace(" :baby_tone1: ", "\uD83D\uDC76\uD83C\uDFFB").replace(" :angel_tone1: ", "\uD83D\uDC7C\uD83C\uDFFB").replace(" :adult_tone1: ", "\uD83E\uDDD1\uD83C\uDFFB").replace(" :woman_technologist: ", "\uD83D\uDC69\u200D\uD83D\uDCBB").replace(" :woman_teacher: ", "\uD83D\uDC69\u200D\uD83C\uDFEB").replace(" :woman_student: ", "\uD83D\uDC69\u200D\uD83C\uDF93").replace(" :woman_singer: ", "\uD83D\uDC69\u200D\uD83C\uDFA4").replace(" :woman_scientist: ", "\uD83D\uDC69\u200D\uD83D\uDD2C").replace(" :woman_office_worker: ", "\uD83D\uDC69\u200D\uD83D\uDCBC").replace(" :woman_mechanic: ", "\uD83D\uDC69\u200D\uD83D\uDD27").replace(" :woman_firefighter: ", "\uD83D\uDC69\u200D\uD83D\uDE92")
                .replace(" :woman_farmer: ", "\uD83D\uDC69\u200D\uD83C\uDF3E").replace(" :woman_factory_worker: ", "\uD83D\uDC69\u200D\uD83C\uDFED").replace(" :woman_cook: ", "\uD83D\uDC69\u200D\uD83C\uDF73").replace(" :woman_astronaut: ", "\uD83D\uDC69\u200D\uD83D\uDE80").replace(" :woman_artist: ", "\uD83D\uDC69\u200D\uD83C\uDFA8")
                .replace(" :man_technologist: ", "\uD83D\uDC68\u200D\uD83D\uDCBB").replace(" :man_teacher: ", "\uD83D\uDC68\u200D\uD83C\uDFEB").replace(" :man_student: ", "\uD83D\uDC68\u200D\uD83C\uDF93").replace(" :man_singer: ", "\uD83D\uDC68\u200D\uD83C\uDFA4").replace(" :man_scientist: ", "\uD83D\uDC68\u200D\uD83D\uDD2C").replace(" :man_office_worker: ", "\uD83D\uDC68\u200D\uD83D\uDCBC").replace(" :woman_genie: ", "\uD83E\uDDDE\u200D♀️").replace(" :woman_gesturing_no: ", "\uD83D\uDE45\u200D♀️").replace(" :woman_gesturing_ok: ", "\uD83D\uDE46\u200D♀️").replace(" :woman_getting_face_massage: ", "\uD83D\uDC86\u200D♀️").replace(" :woman_getting_haircut: ", "\uD83D\uDC87\u200D♀️").replace(" :woman_guard: ", "\uD83D\uDC82\u200D♀️").replace(" :woman_health_worker: ", "\uD83D\uDC69\u200D⚕️").replace(" :woman_judge: ", "\uD83D\uDC69\u200D⚖️").replace(" :woman_mage: ", "\uD83E\uDDD9\u200D♀️").replace(" :woman_pilot: ", "\uD83D\uDC69\u200D✈️").replace(" :woman_police_officer: ", "\uD83D\uDC6E\u200D♀️️").replace(" :woman_pouting: ", "\uD83D\uDE4E\u200D♀️")
                .replace(" :woman_raising_hand: ", "\uD83D\uDE4B\u200D♀️").replace(" :woman_running: ", "\uD83C\uDFC3\u200D♀️").replace(" :woman_shrugging: ", "\uD83E\uDD37\u200D♀️")
                .replace(" :woman_tipping_hand: ", "\uD83D\uDC81\u200D♀️").replace(" :woman_vampire: ", "\uD83E\uDDDB\u200D♀️").replace(" :woman_walking: ", "\uD83D\uDEB6\u200D♀️").replace(" :woman_wearing_turban: ", "\uD83D\uDC73\u200D♀️")
                .replace(" :women_with_bunny_ears_partying: ", "\uD83D\uDC6F\u200D♀️").replace(" :family_man_girl: ", "\uD83D\uDC68\u200D\uD83D\uDC67").replace(" :family_man_boy: ", "\uD83D\uDC68\u200D\uD83D\uDC66").replace(" :family_woman_boy: ", "\uD83D\uDC69\u200D\uD83D\uDC66").replace(" :family_woman_girl: ", "\uD83D\uDC69\u200D\uD83D\uDC67").replace(" :man_artist: ", "\uD83D\uDC68\u200D\uD83C\uDFA8").replace(" :man_astronaut: ", "\uD83D\uDC68\u200D\uD83D\uDE80")
                .replace(" :man_cook: ", "\uD83D\uDC68\u200D\uD83C\uDF73️").replace(" :man_factory_worker: ", "\uD83D\uDC68\u200D\uD83C\uDFED").replace(" :man_farmer: ", "\uD83D\uDC68\u200D\uD83C\uDF3E️").replace(" :man_firefighter: ", "\uD83D\uDC68\u200D\uD83D\uDE92️").replace(" :man_mechanic: ", "\uD83D\uDC68\u200D\uD83D\uDD27").replace(" :woman_frowning: ", "\uD83D\uDE4D\u200D♀️️").replace(" :woman_fairy: ", "\uD83E\uDDDA\u200D♀️️").replace(" :woman_facepalming: ", "\uD83E\uDD26\u200D♀️️").replace(" :woman_elf: ", "\uD83E\uDDDD\u200D♀️️").replace(" :woman_construction_worker: ", "\uD83D\uDC77\u200D♀️️")
                .replace(" :woman_bowing: ", "\uD83D\uDE47\u200D♀️️").replace(" :men_with_bunny_ears_partying: ", "\uD83D\uDC6F\u200D♂️").replace(" :man_wearing_turban: ", "\uD83D\uDC73\u200D♂️️").replace(" :men_with_bunny_ears_partying: ", "\uD83D\uDC68\u200D\uD83D\uDE92️").replace(" :man_walking: ", "\uD83D\uDEB6\u200D♂️").replace(" :man_vampire: ", "\uD83E\uDDDB\u200D♂️").replace(" :man_tipping_hand: ", "\uD83D\uDC81\u200D♂️️️").replace(" :man_shrugging: ", "\uD83E\uDD37\u200D♂️️️").replace(" :man_running: ", "\uD83C\uDFC3\u200D♂️️️").replace(" :man_raising_hand: ", "\uD83D\uDE4B\u200D♂️️️")
                .replace(" :man_pouting: ", "\uD83D\uDE4E\u200D♂️").replace(" :merman: ", "\uD83E\uDDDC\u200D♂️").replace(" :mermaid: ", "\uD83E\uDDDC\u200D♀️").replace(" :man_police_officer: ", "\uD83D\uDC6E\u200D♂️️").replace(" :man_pilot: ", "\uD83D\uDC68\u200D✈️").replace(" :man_mage: ", "\uD83E\uDDD9\u200D♂️️️").replace(" :man_judge: ", "\uD83D\uDC68\u200D⚖️").replace(" :man_health_worker: ", "\uD83D\uDC68\u200D⚕️️️").replace(" :man_guard: ", "\uD83D\uDC82\u200D♂️").replace(" :man_getting_haircut: ", "\uD83D\uDC87\u200D♂️️️")
                .replace(" :man_getting_face_massage: ", "\uD83D\uDC86\u200D♂️").replace(" :man_gesturing_ok: ", "\uD83D\uDE46\u200D♂️").replace(" :man_gesturing_no: ", "\uD83D\uDE45\u200D♂️️").replace(" :man_genie: ", "\uD83E\uDDDE\u200D♂️").replace(" :man_frowning: ", "\uD83D\uDE4D\u200D♂️").replace(" :man_fairy: ", "\uD83E\uDDDA\u200D♂️").replace(" :man_facepalming: ", "\uD83E\uDD26\u200D♂️").replace(" :woman_detective: ", "\uD83D\uDD75️\u200D♀️").replace(" :man_artist_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDFA8").replace(" :man_astronaut_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDE80")
                .replace(" :man_cook_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF73️").replace(" :man_factory_worker_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDFED").replace(" :man_farmer_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF3E️").replace(" :man_firefighter_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDE92").replace(" :man_mechanic_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDD27").replace(" :man_office_worker_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC️️").replace(" :man_scientist_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDD2C️️").replace(" :man_singer_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDFA4️️").replace(" :man_student_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDF93️️").replace(" :man_teacher_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDFEB️️")
                .replace(" :man_technologist_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB").replace(" :woman_artist_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDFA8").replace(" :woman_astronaut_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDE80️").replace(" :woman_cook_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDF73").replace(" :woman_factory_worker_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDFED").replace(" :woman_farmer_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDF3E").replace(" :woman_firefighter_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDE92").replace(" :woman_mechanic_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDD27️️").replace(" :woman_office_worker_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC️️").replace(" :woman_scientist_tone1: ", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83C\uDFEB️️")
                .replace(" :woman_singer_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDFA4").replace(" :woman_student_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDF93").replace(" :woman_teacher_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDFEB️").replace(" :woman_technologist_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBB").replace(" :blond-haired_man: ", "\uD83D\uDC71\u200D♂️").replace(" :blond-haired_woman: ", "\uD83D\uDC71\u200D♀️").replace(" :man_bowing: ", "\uD83D\uDE47\u200D♂️").replace(" :man_construction_worker: ", "\uD83D\uDC77\u200D♂️").replace(" :man_elf: ", "\uD83E\uDDDD\u200D♂️")
                .replace(" :man_detective: ", "\uD83D\uDD75️\u200D♂️").replace(" :woman_wearing_turban_tone1: ", "\uD83D\uDC73\uD83C\uDFFB\u200D♀️").replace(" :woman_walking_tone1: ", "\uD83D\uDEB6\uD83C\uDFFB\u200D♀️").replace(" :woman_vampire_tone1: ", "\uD83E\uDDDB\uD83C\uDFFB\u200D♀️").replace(" :woman_tipping_hand_tone1: ", "\uD83D\uDC81\uD83C\uDFFB\u200D♀️").replace(" :woman_shrugging_tone1: ", "\uD83E\uDD37\uD83C\uDFFB\u200D♀️").replace(" :woman_running_tone1: ", "\uD83C\uDFC3\uD83C\uDFFB\u200D♀️").replace(" :woman_raising_hand_tone1: ", "\uD83D\uDE4B\uD83C\uDFFB\u200D♀️").replace(" :woman_pouting_tone1: ", "\uD83D\uDE4E\uD83C\uDFFB\u200D♀️").replace(" :woman_police_officer_tone1: ", "\uD83D\uDC6E\uD83C\uDFFB\u200D♀️").replace(" :woman_pilot_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D✈️").replace(" :woman_judge_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D⚖️").replace(" :woman_health_worker_tone1: ", "\uD83D\uDC69\uD83C\uDFFB\u200D⚕️").replace(" :woman_guard_tone1: ", "\uD83D\uDC82\uD83C\uDFFB\u200D♀️").replace(" :woman_getting_haircut_tone1: ", "\uD83D\uDC87\uD83C\uDFFB\u200D♀️")
                .replace(" :woman_getting_face_massage_tone1: ", "\uD83D\uDC86\uD83C\uDFFB\u200D♀️").replace(" :woman_gesturing_ok_tone1: ", "\uD83D\uDE46\uD83C\uDFFB\u200D♀️").replace(" :woman_gesturing_no_tone1: ", "\uD83D\uDE45\uD83C\uDFFB\u200D♀️").replace(" :woman_frowning_tone1: ", "\uD83D\uDE4D\uD83C\uDFFB\u200D♀️").replace(" :woman_fairy_tone1: ", "\uD83E\uDDDA\uD83C\uDFFB\u200D♀️").replace(" :woman_facepalming_tone1: ", "\uD83E\uDD26\uD83C\uDFFB\u200D♀️").replace(" :woman_elf_tone1: ", "\uD83E\uDDDD\uD83C\uDFFB\u200D♀️").replace(" :woman_detective_tone1: ", "\uD83D\uDD75\uD83C\uDFFB\u200D♀️").replace(" :woman_construction_worker_tone1: ", "\uD83D\uDC77\uD83C\uDFFB\u200D♀️").replace(" :woman_bowing_tone1: ", "\uD83D\uDE47\uD83C\uDFFB\u200D♀️").replace(" :merman_tone1: ", "\uD83E\uDDDC\uD83C\uDFFB\u200D♂️").replace(" :mermaid_tone1: ", "\uD83E\uDDDC\uD83C\uDFFB\u200D♀️").replace(" :man_wearing_turban_tone1: ", "\uD83D\uDC73\uD83C\uDFFB\u200D♂️").replace(" :man_walking_tone1: ", "\uD83D\uDEB6\uD83C\uDFFB\u200D♂️").replace(" :man_vampire_tone1: ", "\uD83E\uDDDB\uD83C\uDFFB\u200D♂️").replace(" :man_tipping_hand_tone1: ", "\uD83D\uDC81\uD83C\uDFFB\u200D♂️")
                .replace(" &phone; ", " ☎️ ").replace(" &rsquo; ", "'").replace(" &rdquo; ", "'");
        return convertMessage.trim();
    }

    public String convetSpecialCharacterToemojies(String s) {
        StringBuilder message = new StringBuilder();
        try {
            if (s != null && !s.isEmpty() && !s.equals("null")) {
                if (s.contains(" ")) {
                    String[] splitting = s.split(" ");
                    for (int i = 0; i < splitting.length; i++) {
                        String messageone = replaceString(" " + splitting[i] + " ");
                        message.append(" ").append(messageone);
                    }
                } else {
                    String messageone = replaceString(" " + s + " ");
                    message.append(messageone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    public boolean copyText(Context context, String string) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(string);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", string);
                clipboard.setPrimaryClip(clip);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public JSONObject getDeviceMetaData() {
        try {
            JSONObject metaData = new JSONObject();
            metaData.put("OS_Version", System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL);
            metaData.put("OS_API Level", Build.VERSION.SDK_INT);
            metaData.put("Device", Build.DEVICE);
            metaData.put("Model", Build.MODEL);
            metaData.put("Product", Build.PRODUCT);
            metaData.put("Brand", Build.BRAND);
            metaData.put("manufacturer", Build.MANUFACTURER);

            return metaData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLocationName(Context context, String name, double latitude, double longitude) {

        String location_name = name;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                location_name = addresses.get(0).getAddressLine(1);

                if (location_name == null || location_name.isEmpty() || location_name.equals("null")) {
                    location_name = addresses.get(0).getAddressLine(0);
                    if (location_name == null || location_name.isEmpty() || location_name.equals("null")) {
                        location_name = name;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location_name;
    }



    public String localTimeToIndiaTime(String date) {
        String ind_time = "";
        SimpleDateFormat dateFormat = null;
        try {
            if (date != null && !date.isEmpty() && !date.equals("null")) {

                //date = "2017-12-14 13:01:00";
                if (date.contains(".")) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                } else {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                Date date1 = dateFormat.parse(date);
                dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                ind_time = dateFormat.format(date1);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ind_time;
    }

    public String indiaTimeTolocalTime(String date) {

        String local_time = "";

        try {
            if (date != null && !date.isEmpty() && !date.equals("null")) {

                DateFormat formatterUTC;
                if (date.contains(".")) {
                    formatterUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
//
                } else {
                    formatterUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                formatterUTC.setTimeZone(TimeZone.getDefault());
                Date date11 = formatterUTC.parse(date);


                DateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                dateFormats.setTimeZone(TimeZone.getDefault());

                local_time = dateFormats.format(date11);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return local_time;
    }

    public String getLastSeenFormat(String last_seen) {
        String last_seen_string = "";
        try {
            if (last_seen != null && !last_seen.trim().isEmpty() && !last_seen.equals("null")) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd");
                String[] date = last_seen.split(" ");
                String server_date = date[0];
                Calendar calendar = Calendar.getInstance();
                String dates = dateFormats.format(calendar.getTime());

                if (dates.trim().equals(server_date.trim())) {
                    SimpleDateFormat dateFormatss = new SimpleDateFormat("HH:mm:ss");
                    Date date1 = dateFormatss.parse(date[1]);
                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                    String date_time = _12HourSDF.format(date1);
                    last_seen_string = "last seen " + date_time.toLowerCase();

                } else {
                    SimpleDateFormat dateFormatss = new SimpleDateFormat("HH:mm:ss");
                    Date date1 = dateFormatss.parse(date[1]);
                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                    String date_time = _12HourSDF.format(date1);
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
                    String last_seen_date = new SimpleDateFormat("dd/MM/yyyy").format(date2);


                    last_seen_string = "last seen " + last_seen_date + " " + date_time.toLowerCase();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return last_seen_string;
    }





    public void enablePopMenuIcons(androidx.appcompat.widget.PopupMenu popup) {
        try {
            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = androidx.appcompat.widget.PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popup);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                //
                // In either case, an exception indicates something is wrong with the reflection code, or the
                // structure of the PopupMenu class or its dependencies has changed.
                //
                // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                // but in the case that they do, we simply can't force icons to display, so log the error and
                // show the menu normally.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTheFirstCharFromEachWord(String name) {
        String userName = name;
        String user_name = "";

        try {
            String[] myName = name.split(" ");

            if (myName.length > 1) {
                for (int i = 0; i < myName.length; i++) {
                    String s = myName[i];
                    if (s != null && !s.trim().isEmpty() && !s.trim().equals(" ")) {

                        user_name = user_name + s.charAt(0);
                    }
                }
            } else {
                if (userName.length() > 1) {
                    user_name = userName.substring(0, 2);
                } else {
                    user_name = userName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (user_name != null && !user_name.isEmpty()) {
            String names = userName;
            if (user_name.length() > 2) {
                user_name = user_name.substring(0, 2);
            }
        }

        return user_name;
    }

    public String getTheUrlFormString(String text) {
        String url_string = "";

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            url_string = url_string + "\n" + urlStr;
            //links.add(urlStr);
        }
        return url_string;
    }

    /*    private String capitalize(String capString){
            StringBuffer capBuffer = new StringBuffer();
            Matcher capMatcher = Pattern.compile("([a-z-éá])([a-z-éá]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()){
                capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }

            return capMatcher.appendTail(capBuffer).toString();
        }*/
    public String getThecountMembers(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return getThecountMembers(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + getThecountMembers(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;


        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri); // context needed

                File photoFile = createTemporalFileFrom(context, inputStream, uri);
                int sizes = (int) ((photoFile.length() / 1024) / 1024);

                if (sizes < 5) {
                    filePath = photoFile.getPath();
                } else {

                    pushToast(context, "File size should not be more than 5 MB");
                }
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private File createTemporalFileFrom(Context context, InputStream inputStream, Uri uri) throws IOException {
        File targetFile = null;
        String extention_name = null;
        String raw_data_path[] = uri.toString().split("/");
        String file_name = raw_data_path[raw_data_path.length - 1].replaceAll("[^A-Za-z0-9 .]", "_").replace("\\", "");

        if (file_name.contains("_")) {
            file_name = file_name.substring(file_name.lastIndexOf("_") + 1);

        }


        String currnet_time = String.valueOf(System.currentTimeMillis());
        file_name = currnet_time + "_" + file_name;


        ContentResolver cR = context.getContentResolver();
        String extention = cR.getType(uri);
        if (extention != null && !extention.isEmpty() && !extention.equals("null")) {
            String splite_file[] = extention.split("/");
            extention_name = splite_file[splite_file.length - 1];
        } else {
            String file_uri = String.valueOf(uri);
            extention = file_uri.substring(file_uri.lastIndexOf(".") + 1);
            extention_name = extention;
        }

        if (extention_name != null && !extention_name.isEmpty() && !extention_name.equals("null")) {
            if (extention_name.contains("android.package-archive")) {
                extention_name = "apk";
            } else if (extention_name.contains("spreadsheetml.sheet")) {
                extention_name = "xlsx";
            }
        }
        if (extention_name != null && !extention_name.isEmpty() && !extention_name.equals("null")) {
            file_name = replaceExtensions(file_name, extention_name);
        }

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            //if (file_name.contains(".mp4") || file_name.contains(".mpeg") || file_name.contains("."))
            if (extention_name != null && !extention_name.isEmpty() && !extention_name.equals("null") && extention_name.contains(".")) {
                targetFile = createTemporalFile(context, file_name);
            } else if (extention_name != null && !extention_name.isEmpty() && !extention_name.equals("null")) {
                targetFile = createTemporalFile(context, file_name + "." + extention_name);
            } else {
                targetFile = createTemporalFile(context, file_name);
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return targetFile;
    }

    public File createTemporalFile(Context context, String filename) {
        return new File(context.getExternalCacheDir(), filename); // context needed
    }

    public String replaceExtensions(String file_name, String extention) {

        try {
            file_name = file_name.replaceAll("." + extention, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file_name;
    }

    public void LogDetails(String title, String content) {
        try {

           System.out.println("Mounika Reddy  " + title + " data === " + content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public void closeKeyboard1(Context context, Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}


