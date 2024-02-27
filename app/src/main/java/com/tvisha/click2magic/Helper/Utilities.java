package com.tvisha.click2magic.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajkumar on 13/7/17.
 */
public class Utilities {

    public static Date convertString2Date(String time) throws ParseException {


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = sdf.parse(time);

        return date;
    }


    public static void closeKeyboard(Context context,Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidDate(String inDate) {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            pe.printStackTrace();
            return false;
        }
        return true;
    }


    public static void openKeyboard(Context context,EditText editText)
    {
        editText.requestFocus();
        ((InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE))
                .showSoftInput(editText, InputMethodManager.SHOW_FORCED);

    }


    public static int getDeviceHeight(Activity ctx) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (width > height) {
            return height;
        } else {
            return width;
        }
    }

    public static int getDeviceWidth(Activity ctx) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (width < height) {
            return height;
        } else {
            return width;
        }
    }

    public static boolean validEmail(String email) {
        if (email.trim().length() > 0) {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern p = Pattern.compile(ePattern);
            Matcher m = p.matcher(email);
            return m.matches();
        } else {
            return false;
        }
    }

    public static ColorStateList getColorStateList(int checkedColor, int offColor) {
        return new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_checked, android.R.attr.state_pressed}
                , new int[]{-android.R.attr.state_checked, android.R.attr.state_pressed}
                , new int[]{android.R.attr.state_checked}
                , new int[]{-android.R.attr.state_checked}
        }, new int[]{checkedColor, offColor, checkedColor, offColor}
        );
    }

    public static boolean isValidName(String name) {
        if (name != null) {
            if (name.length() >= 3 && !Utilities.isNumeric(name)) {
                CharSequence inputStr = name;
                //Pattern pattern = Pattern.compile(new String("^[a-zA-Z\\s]*$"));
                String regex = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(inputStr);
                return matcher.matches();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isValidPhone(String phone) {

        String mobile_pattren="^[789]\\d{9}$";
        if (null != phone) {
            phone = phone.trim();
            if (phone.matches(mobile_pattren)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static int isNullCheck(int val) {
        return 0 != val ? val : 0;
    }

    public static Date isNullDate(Date val) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(String.valueOf(val));
        } catch (ParseException pe) {
            pe.printStackTrace();
            return dateFormat.parse("2000-01-01 00:00:00");
        }
    }

    public static String isNullCheck(String val, String x) {
        //return  (val != null ? val :"");
        return (val != null ? val : x);
    }

    public static String getDateTime(Date val) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = val;
        return dateFormat.format(date);
    }

    public static String getCurrent_time() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static boolean validName(String name) {
        if (null != name) {
            if (name.length() >= 3) {
                CharSequence inputStr = name;
                String regex = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(inputStr);
                return matcher.matches();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean validPassword(String pwd) {
        if (pwd.length() >= 3) {
            String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(pwd);
            return matcher.find();
        } else {
            return false;
        }
    }

    public static boolean validAlphaNumeric(String pwd) {
        if (pwd.length() >= 2) {
            String PASSWORD_PATTERN = "^[a-zA-Z0-9_]*$";
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(pwd);
            return matcher.find();
        } else {
            return false;
        }
    }




    /*------ Network status checking --*/
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context)
    {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return TYPE_NOT_CONNECTED;
    }
    /*------ Network status checking  complete--*/

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static void setSharedPref(Context applicationContext, String key, String value) {
        SharedPreferences sp = applicationContext.getSharedPreferences("userrecord", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setSharedPrefInt(Context applicationContext, String key, int value) {
        SharedPreferences sp = applicationContext.getSharedPreferences("userrecord", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static double checkDimension(Context context) {

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        try {
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }


    public static java.sql.Date convertString2SQLDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsed = null;
        try {
            parsed = format.parse(date);
            return new java.sql.Date(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static boolean isTabletDevice(Context activityContext) {

        boolean device_large = ((activityContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
        DisplayMetrics metrics = new DisplayMetrics();
        Activity activity = (Activity) activityContext;
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (device_large) {
            //Tablet
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_TV) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_280) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_400) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_560) {
                return true;
            } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
                return true;
            }
        } else {
            //Mobile
        }
        return false;
    }


    public static int NORMAL_STRING = 0;
    public static int EMAIL = 1;
    public static int NAME = 2;
    public static int DATE = 3;
    public static int TIME = 4;
    public static int PHONE = 5;
    public static int NUMBER = 6;
    public static int URL = 7;
    public static int GUEST_SIGN = 8;


    public static int MANDATORY = 1;
    public static int NotCheckable = 0;
    public static int CheckIfValEnter = 2;


    public static String isFieldValid(String val, int minLength, int Input_Type, int Field_Type) {
        val = val.trim();
        if (Field_Type == NotCheckable) {
            return "true";
        } else if (Field_Type == CheckIfValEnter) {
            if(val.length() == 0 ) {
                return "true";
            }else if (val.length() >= minLength) {
                if (Input_Type == NORMAL_STRING) {
                    return true+"";
                } else if (Input_Type == EMAIL && !validEmail(val)) {
                    return "Please provide valid Email.";
                } else if (Input_Type == NAME && !isValidName(val)) {
                    return "Please provide valid name.";

                } else if (Input_Type == DATE && !isValidDate(val)) {
                    return "Please provide valid date.";

                } else if (Input_Type == PHONE && !isValidPhone(val)) {
                    return "Please provide phone number.";

                } else if (Input_Type == NUMBER && !isNumeric(val)) {
                    return "Please provide numeric values.";

                }else  if (Input_Type == URL && !isValidUrl(val)) {
                    return "Please provide valid url.";

                } else{
                    return "true";
                }
            }else{
                return "Please provide minimum of "+minLength+" characters";
            }
        }else if (Field_Type == MANDATORY) {

            if (val.length() >= minLength) {
                if (Input_Type == NORMAL_STRING) {
                    return true+"" ;
                } else if (Input_Type == EMAIL && !validEmail(val)) {
                    return "Please provide valid Email.";
                } else if (Input_Type == NAME && !isValidName(val)) {
                    return "Please provide valid name.";

                } else if (Input_Type == DATE && !isValidDate(val)) {
                    return "Please provide valid date.";

                } else if (Input_Type == PHONE && !isValidPhone(val)) {
                    return "Please provide valid phone number.";

                } else if (Input_Type == NUMBER && !isNumeric(val)) {
                    return "Please provide numeric values.";

                } else if (Input_Type == URL && !isValidUrl(val)) {
                    return "Please provide valid url.";

                } else {
                    return "true";
                }
            }else{
                return "please provide minimum of " + minLength + " characters.";
            }
        }
        return "false";
    }
    public static boolean isValidUrl(String val)
    {
        return true;
    }

       /* public static int isMandatoryField(Context ctx, String field_name){
            try {
                List<String> mandatoryFields = new TinyDB(ctx).getListString("mandatory_fields");
                if(mandatoryFields.contains(field_name)){

                    return Utilities.MANDATORY;
                }else{

                    return Utilities.CheckIfValEnter;
                }
            }catch (Exception e){
                e.printStackTrace();
                return Utilities.CheckIfValEnter;
            }
        }*/

   public static boolean isMemorySizeAvailableAndroid(long download_bytes, boolean isExternalMemory) {
        boolean isMemoryAvailable = false;
        long freeSpace = 0;

        // if isExternalMemory get true to calculate external SD card available size
        if(isExternalMemory){
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }else{
            // find phone available size
            try {
                StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }

        return isMemoryAvailable;
    }

    public static String getCurrentDateTimeNew() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }



}
