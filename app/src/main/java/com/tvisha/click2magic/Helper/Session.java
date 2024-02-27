package com.tvisha.click2magic.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tvisha.click2magic.api.post.SitesInfo;



import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class Session {

    private static final String PREF_IS_LOGIN = "is_login";
    private static final String PREF_SESSION = "feoun_s";
    private static final String PREF_USER_ID = "user_id";
    private static final String PREF_USER_NAME = "user_name";
    private static final String PREF_USER_PHONE = "user_phone";
    private static final String PREF_USER_PIC = "user_pic";
    private static final String PREF_USER_EMAIL = "user_email";

    private static final String PREF_TRANSACTION_ID = "transaction_id";
    private static final String PREF_FORGOT_EMAIL_PHONE = "forgot_user_name_phone";

    private static final String PREF_RESTAURANT_POST_COUNT = "post_count_";
    private static final String PREF_RESTAURANT_FOLLOWER_COUNT = "follower_count_";
    private static final String FCM_TOKEN = "fcm_token";
    private static final String DEVICE_ID = "device_id";
    private static final String FCM_UPDATE = "fcm_update";

    public static final String SP_USER_DISPLAY_NAME="user_display_name";
    public static final String SP_USER_ROLE="user_role";
    public static final String SP_TM_USER_ID="tm_user_id";
    public static final String SP_ACCOUNT_ID="account_id";
    public static final String SP_SITE_ID="site_id";
    public static final String SP_COMPANY_TOKEN="company_token";
    public static final String SP_COMPANY_SOCKET_KEY="company_socket_key";
    public static final String SP_COMPANY_NAME="company_name";
    public static final String SP_IS_SELF = "is_self";
    public static final String SP_ALL_SITE_IDS="all_site_ids";
    public static final String SP_OTHETR_USERID="other_user_id";

    public static final String SP_OTHER_USER_DISPLAY_NAME="other_user_display_name";
    public static final String SP_OTHER_ACCOUNT_ID="other_account_id";
    public static final String SP_OTHER_SITE_ID="other_site_id";

    public static final String SP_OTHER_USER_ROLE="other_user_role";
    public static final String SP_OTHER_TM_USER_ID="other_tm_user_id";

    public static final String SP_SITE_TOKEN="site_token";
    public static final String ACCESS_TOKEN="access_token";
    public static final String SOCKET_ID="socket_id";
    public static final String SYNC_MESSAGE_BY_TIME="sync_message_by_time";

    //Banner
    private static final String PREF_BAR_BANNER = "bar_banner";
    private static final String PREF_DRIVE_IN_BANNER = "drive_in_banner";
    private static final String PREF_EVENT_BANNER = "event_banner";
    private static final String PREF_LOUNGE_BANNER = "lounge_banner";
    private static final String PREF_PUB_BANNER = "pub_banner";
    private static final String PREF_RESORT_BANNER = "resort_banner";
    private static final String PREF_RESTAURANT_BANNER = "restaurant_banner";

    //Aws
    private static final String PREF_END_POINT = "end_point";
    private static final String PREF_RESIZE_BUCKET_NAME = "resize_bucket_name";
    private static final String PREF_SECRET_KEY = "secret_key";
    private static final String PREF_ANDROID_FILE_PATH = "android_file_path";
    private static final String PREF_BUCKET_NAME = "bucket_name";
    private static final String PREF_ACCESS_KEY = "access_key";
    private static final String PREF_REGION = "region";
    public static final String SP_SITE_INFO = "siteInfo";
    public static final String SP_OTHERS_SITE_INFO = "othersSiteInfo";
    public static final String SP_NOTIFICATION_TIME = "notification_time";


    private static Session ourInstance = new Session();

    public static Session getInstance() {
        if (ourInstance == null) {
            ourInstance = new Session();
        }
        return ourInstance;
    }

    private Session() {

    }

    private static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(PREF_SESSION, Context.MODE_PRIVATE);
    }

    public static void createLoginSession(Context context) {
        getPreference(context).edit().putBoolean(PREF_IS_LOGIN, true).apply();
    }

    public static void endLoginSession(Context context) {
        getPreference(context).edit().putBoolean(PREF_IS_LOGIN, false).apply();
    }

    public static void saveSiteInfoList(Context context, List<SitesInfo> list, String key) {
        try{
            if (list != null) {

                Gson gson = new Gson();
                String json = gson.toJson(list);
                Helper.getInstance().LogDetails("getLogin details saveSiteInfoList",json);
                getPreference(context).edit().putString(key, json).apply();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveUserID(Context context, String userID){
        getPreference(context).edit().putString(PREF_USER_ID, userID).apply();
    }

    public static void saveNotificationTime(Context context, long notificationTime){
        getPreference(context).edit().putLong(SP_NOTIFICATION_TIME, notificationTime).apply();
    }
    public static void saveUserRole(Context context, String userID){
        getPreference(context).edit().putString(SP_USER_ROLE, userID).apply();
    }
  public static void saveOtherUserRole(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHER_USER_ROLE, userID).apply();
    }
    public static void saveOtherUserTmUserId(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHER_TM_USER_ID, userID).apply();
    }

    public static void saveTmUserId(Context context, String userID){
        getPreference(context).edit().putString(SP_TM_USER_ID, userID).apply();
    }

    public static void saveOtherUserId(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHETR_USERID, userID).apply();
    }

    public static void saveOtherUserDisplayName(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHER_USER_DISPLAY_NAME, userID).apply();
    }

   public static void saveAccountId(Context context, String userID){
        getPreference(context).edit().putString(SP_ACCOUNT_ID, userID).apply();
    }
   public static void saveOtherUserAccountId(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHER_ACCOUNT_ID, userID).apply();
    }
  public static void saveOtherUserSiteId(Context context, String userID){
        getPreference(context).edit().putString(SP_OTHER_SITE_ID, userID).apply();
    }

    public static void saveSiteId(Context context, String userID){
        getPreference(context).edit().putString(SP_SITE_ID, userID).apply();
    }

    public static void saveCompanyToken(Context context, String userID){
        getPreference(context).edit().putString(SP_COMPANY_TOKEN, userID).apply();
    }

    public static void saveCompanySocketToken(Context context, String userID){
        getPreference(context).edit().putString(SP_COMPANY_SOCKET_KEY, userID).apply();
    }

    public static void saveCompanyName(Context context, String userID){
        getPreference(context).edit().putString(SP_COMPANY_NAME, userID).apply();
    }
       public static void saveAllSiteIds(Context context, String userID){
        getPreference(context).edit().putString(SP_ALL_SITE_IDS, userID).apply();
    }

    public static void saveSiteToken(Context context, String userID){
        getPreference(context).edit().putString(SP_SITE_TOKEN, userID).apply();
    }

    public static void saveAccessToken(Context context, String userID){
        getPreference(context).edit().putString(ACCESS_TOKEN, userID).apply();
    }
    public static void saveSocketId(Context context, String userID){
        getPreference(context).edit().putString(SOCKET_ID, userID).apply();
    }

    public static void saveSyncMessageTime(Context context, String userID){
        getPreference(context).edit().putString(SYNC_MESSAGE_BY_TIME, userID).apply();
    }

    public static void setIsSelf(Context context, boolean isself){
        getPreference(context).edit().putBoolean(SP_IS_SELF, isself).apply();
    }

    public static void saveFcmToken(Context context, String fcmToken){
        getPreference(context).edit().putString(FCM_TOKEN, fcmToken).apply();
    }

    public static void saveDeviceId(Context context, String deviceId){
        getPreference(context).edit().putString(DEVICE_ID, deviceId).apply();
    }

    public static void saveFcmUpdate(Context context, boolean isUpdate){
        getPreference(context).edit().putBoolean(FCM_UPDATE, isUpdate).apply();
    }

    public static String getFcmToken(Context context){
        return getPreference(context).getString(FCM_TOKEN, "");
    }

    public static String getDeviceId(Context context){
        return getPreference(context).getString(DEVICE_ID, "");
    }

    public static long getNotificationTime(Context context){
        return getPreference(context).getLong(SP_NOTIFICATION_TIME, 0l);
    }


    public static boolean getFcmUpdate(Context context){
        return getPreference(context).getBoolean(FCM_UPDATE, false);
    }


    public static void saveUserPhone(Context context, String userName){
        getPreference(context).edit().putString(PREF_USER_PHONE, userName).apply();
    }

    public static void saveUserPic(Context context, String pic){
        getPreference(context).edit().putString(PREF_USER_PIC, pic).apply();
    }

    public static void saveEndPoint(Context context, String end_point){
        getPreference(context).edit().putString(PREF_END_POINT, end_point).apply();
    }

    public static void saveResizeBucketName(Context context, String bucket_name){
        getPreference(context).edit().putString(PREF_RESIZE_BUCKET_NAME, bucket_name).apply();
    }

    public static void saveSecretKey(Context context, String secret_key){
        getPreference(context).edit().putString(PREF_SECRET_KEY, secret_key).apply();
    }

    public static void saveRegion(Context context, String region){
        getPreference(context).edit().putString(PREF_REGION, region).apply();
    }

    public static void saveAndroidFilePath(Context context, String android_file_path){
        getPreference(context).edit().putString(PREF_ANDROID_FILE_PATH, android_file_path).apply();
    }

    public static void saveBucketName(Context context, String bucket_name){
        getPreference(context).edit().putString(PREF_BUCKET_NAME, bucket_name).apply();
    }

    public static void saveAccessKey(Context context, String access_key){
        getPreference(context).edit().putString(PREF_ACCESS_KEY, access_key).apply();
    }

    public static void saveTransactionID(Context context, String access_key){
        getPreference(context).edit().putString(PREF_TRANSACTION_ID, access_key).apply();
    }

    public static void saveForgot_Name_Email(Context context, String email_phone){
        getPreference(context).edit().putString(PREF_FORGOT_EMAIL_PHONE, email_phone).apply();
    }

    public static void saveUserEmail(Context context, String email){
        getPreference(context).edit().putString(PREF_USER_EMAIL, email).apply();
    }

    public static void saveBarBanners(Context context, Set<String> setBar){
        getPreference(context).edit().putStringSet(PREF_BAR_BANNER, setBar).apply();
    }

    public static void saveDriveInBanners(Context context, Set<String> setDriveIn){
        getPreference(context).edit().putStringSet(PREF_DRIVE_IN_BANNER, setDriveIn).apply();
    }

    public static void saveEventsBanners(Context context, Set<String> setEvent){
        getPreference(context).edit().putStringSet(PREF_EVENT_BANNER, setEvent).apply();
    }

    public static void saveLoungeBanners(Context context, Set<String> setLounge){
        getPreference(context).edit().putStringSet(PREF_LOUNGE_BANNER, setLounge).apply();
    }

    public static void savePubBanners(Context context, Set<String> setPub){
        getPreference(context).edit().putStringSet(PREF_PUB_BANNER, setPub).apply();
    }

    public static void saveResortBanners(Context context, Set<String> setResort){
        getPreference(context).edit().putStringSet(PREF_RESORT_BANNER, setResort).apply();
    }

    public static void saveRestaurantBanners(Context context, Set<String> setRestaurant){
        getPreference(context).edit().putStringSet(PREF_RESTAURANT_BANNER, setRestaurant).apply();
    }


    public static void saveCurrentPostsCount(Context context, String postCount){
        getPreference(context).edit().putString(PREF_RESTAURANT_POST_COUNT, postCount).apply();
    }

    public static void saveCurrentFollowersCount(Context context, String followersCount){
        getPreference(context).edit().putString(PREF_RESTAURANT_FOLLOWER_COUNT, followersCount).apply();
    }

  /*  public static void saveEditUser(Context context, Profile userDetails){
        Gson gson = new Gson();
        String userJson = gson.toJson(userDetails);
        getPreference(context).edit().putString(PREF_EDIT_USER_PROFILE, userJson).apply();
    }

    public static Profile getEditUser(Context context){
        Gson gson = new Gson();
        String userJson = getPreference(context).getString(PREF_EDIT_USER_PROFILE, "");
        //return getPreference(context).getString(PREF_EDIT_USER_PROFILE, "");
        return gson.fromJson(userJson, Profile.class);
    }*/

    public static Set<String> getBarBanners(Context context){
        return getPreference(context).getStringSet(PREF_BAR_BANNER, null);
    }

    public static Set<String> getDriveInBanners(Context context){
        return getPreference(context).getStringSet(PREF_DRIVE_IN_BANNER, null);
    }

    public static Set<String> getEventsBanners(Context context){
        return getPreference(context).getStringSet(PREF_EVENT_BANNER, null);
    }

    public static Set<String> getLoungeBanners(Context context){
        return getPreference(context).getStringSet(PREF_LOUNGE_BANNER, null);
    }

    public static Set<String> getPubBanners(Context context){
        return getPreference(context).getStringSet(PREF_PUB_BANNER, null);
    }

    public static Set<String> getResortBanners(Context context){
        return getPreference(context).getStringSet(PREF_RESORT_BANNER, null);
    }

    public static Set<String> getRestaurantBanners(Context context){
        return getPreference(context).getStringSet(PREF_RESTAURANT_BANNER, null);
    }

    public static String getCurrentPostsCount(Context context){
        return getPreference(context).getString(PREF_RESTAURANT_POST_COUNT, "");
    }


    public static String getCurrentFollowersCount(Context context){
        return getPreference(context).getString(PREF_RESTAURANT_FOLLOWER_COUNT, "");
    }

    public static String getForgot_Name_Email(Context context){
        return getPreference(context).getString(PREF_FORGOT_EMAIL_PHONE, "");
    }

    public static String getTransactionID(Context context){
        return getPreference(context).getString(PREF_TRANSACTION_ID, "");
    }

    public static String getAccessKey(Context context){
        return getPreference(context).getString(PREF_ACCESS_KEY, "");
    }

    public static String getUserEmail(Context context){
        return getPreference(context).getString(PREF_USER_EMAIL, "");
    }

    public static String getBucketName(Context context){
        return getPreference(context).getString(PREF_BUCKET_NAME, "");
    }

    public static String getAndroidFilePath(Context context){
        return getPreference(context).getString(PREF_ANDROID_FILE_PATH, "");
    }

    public static String getRegion(Context context){
        return getPreference(context).getString(PREF_REGION, "");
    }

    public static String getSecretKey(Context context){
        return getPreference(context).getString(PREF_SECRET_KEY, "");
    }

    public static String getResizeBucketName(Context context){
        return getPreference(context).getString(PREF_RESIZE_BUCKET_NAME, "");
    }

    public static String getEndPoint(Context context){
        return getPreference(context).getString(PREF_END_POINT, "");
    }

    public static String getUserPhone(Context context){
        return getPreference(context).getString(PREF_USER_PHONE, "");
    }

    public static void saveUserName(Context context, String userName){
        getPreference(context).edit().putString(PREF_USER_NAME, userName).apply();
    }


    public static void saveUserDisplayName(Context context, String userName){
        getPreference(context).edit().putString(SP_USER_DISPLAY_NAME, userName).apply();
    }

    public static String getUserPic(Context context){
        return getPreference(context).getString(PREF_USER_PIC, "");
    }

    public static String getUserName(Context context){
        return getPreference(context).getString(PREF_USER_NAME, "");
    }
    public static String getUserRole(Context context){
        return getPreference(context).getString(SP_USER_ROLE, "");
    }
  public static String getOtherUserRole(Context context){
        return getPreference(context).getString(SP_OTHER_USER_ROLE, "");
    }

    public static String getOtherUserTmUserId(Context context){
        return getPreference(context).getString(SP_OTHER_TM_USER_ID, "");
    }


    public static String getTmUserId(Context context){
        return getPreference(context).getString(SP_TM_USER_ID, "");
    }

    public static String getOtherUserId(Context context){
        return getPreference(context).getString(SP_OTHETR_USERID, "");
    }
  public static String getOtherUserDisplayName(Context context){
        return getPreference(context).getString(SP_OTHER_USER_DISPLAY_NAME, "");
    }


    public static String getAccountId(Context context){
        return getPreference(context).getString(SP_ACCOUNT_ID, "");
    }

    public static String getOtherUserAccountId(Context context){
        return getPreference(context).getString(SP_OTHER_ACCOUNT_ID, "");
    }
    public static String getOtherUserSiteId(Context context){
        return getPreference(context).getString(SP_OTHER_SITE_ID, "");
    }

    public static String getSiteId(Context context){
        return getPreference(context).getString(SP_SITE_ID, "");
    }

    public static String getCompanyToken(Context context){
        return getPreference(context).getString(SP_COMPANY_TOKEN, "");
    }
  public static String getCompanySocketToken(Context context){
        return getPreference(context).getString(SP_COMPANY_SOCKET_KEY, "");
    }
public static String getCompanyName(Context context){
        return getPreference(context).getString(SP_COMPANY_NAME, "");
    }

    public static String getAllSiteIds(Context context){
        return getPreference(context).getString(SP_ALL_SITE_IDS, "");
    }

    public static String getSiteToken(Context context){
        return getPreference(context).getString(SP_SITE_TOKEN, "");
    }

    public static String getAccessToken(Context context){
        return getPreference(context).getString(ACCESS_TOKEN, "");
    }


    public static String getSocketId(Context context){
        return getPreference(context).getString(SOCKET_ID, "");
    }

    public static String getSyncMessageByTime(Context context){
        return getPreference(context).getString(SYNC_MESSAGE_BY_TIME, "");
    }

    public static boolean getIsSelf(Context context){
        return getPreference(context).getBoolean(SP_IS_SELF, true);
    }



    public static  List<SitesInfo> getSiteInfoList(Context context, String key) {
        try{

            Gson gson = new Gson();
           // String json = prefs.getString(key, null);
            String json =getPreference(context).getString(key, null);
            Type type = new TypeToken<List<SitesInfo>>() {
            }.getType();
            return gson.fromJson(json, type);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public static String getUserDisplayName(Context context){
        return getPreference(context).getString(SP_USER_DISPLAY_NAME, "");
    }

    public static String getUserID(Context context){
        return getPreference(context).getString(PREF_USER_ID, "");
    }



    public static boolean getLoginStatus(Context context){
        return getPreference(context).getBoolean(PREF_IS_LOGIN, false);
    }




    public static void logout(Context context){
        getPreference(context).edit().clear().apply();
    }


}
