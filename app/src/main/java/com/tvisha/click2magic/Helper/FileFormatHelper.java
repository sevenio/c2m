package com.tvisha.click2magic.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tvisha.click2magic.model.GalleryModel;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tvisha on 14/3/18.
 */

public class FileFormatHelper {
    public static final String FILE_FORMATS[] = {"jpg", "jpeg", "png", "gif", "bmp", "docx", "doc", "xls", "xlsx", "txt", "zip", "rar", "pdf", "json", "sql", "odt", "csv", "xml", "html", "css", "js", "mpeg", "mp3", "mp4", "3gpp"};
    public static final String FILE_FORMATS_FOR_STRING_MATCH = "jpg|jpeg|png|gif|bmp|docx|doc|xls|xlsx|txt|zip|rar|pdf|json|sql|odt|csv|xml|html|css|js|mpeg|mp3|mp4|3gpp";

    public static final int MAX_FILE_LENGTH = 5;

    private static FileFormatHelper ourInstance = new FileFormatHelper();

    String DIR_PARENT = "Click2Magic";

    public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };
    public static String[] mediaColumns = { MediaStore.Video.Media._ID };

    String root = Environment.getExternalStorageDirectory().toString() + "/." + DIR_PARENT + "/";




    String DIR_IMAGE    = "images";
    String DIR_VIDEOS   = "videos";
    String DIR_AUDIO    = "audio";
    String DIR_Files    = "files";
    String DIR_SECURITY = "security";

    public static FileFormatHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new FileFormatHelper();
        }



        return ourInstance;
    }
    public boolean isValidFileSize(Context context, Uri uri) {
        try {

            File file = new File(FilePathLocator.getPath(context, uri));
            long length = file.length();
            if (length > 0) {
                length = (length / 1024) / 1024;

                return length <= 20;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidFileDocument(Context context, Uri uri) {
        try {
            File file = new File(FilePathLocator.getPath(context, uri));
            long length = file.length();
            if (length > 0) {
                length = (length / 1024) / 1024;
                return length <= 5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidFileSize(String path) {
        try {

            File file = new File(path);

            long length = file.length();

            length = (length / 1024) / 1024;


            return length <= 20;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isVideo(String path) {
        try {
            if (path != null) {
                String filenameArray[] = path.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                return extension.toLowerCase().matches("mpeg|3gpp|mp4");
                //return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path)).matches("video/mpeg|video/3gpp|video/mp4");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public GalleryModel getFileData(GalleryModel model) {
        try {
            File file = new File(model.getPath());
            model.setFileName(file.getName());
            long fileLength = file.length();
            int size = (int) ((fileLength / 1024) / 1024);
            long remainder = ((fileLength / 1024) % 1024);

            String kb = String.valueOf(remainder);
            if (kb.length() > 2) {
                kb = kb.substring(0, 2);
            }

            String fileSize;
            if (size > 0) {
                fileSize = size + "." + kb + " mb";
            } else {
                fileSize = kb + " kb";
            }

            model.setFileSize(fileSize);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }


    public boolean isAudio(String path) {
        try {
            if (path != null) {
                String filenameArray[] = path.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                return extension.toLowerCase().matches("mpeg|mp3|wav|ogg|webm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidFileFromServerPath(String path) {
        try {
            if (path != null) {
                String filenameArray[] = path.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                return extension.matches(FILE_FORMATS_FOR_STRING_MATCH);
                //return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path)).matches("image/png|image/gif|image/jpeg|image/jpg|image/bmp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidFileFromMobilePath(Context context, Uri uri) {
        try {
            if (uri != null) {
                ContentResolver cR = context.getContentResolver();
                String extention = cR.getType(uri);
                if (extention != null) {
                    if (extention.matches("audio/mpeg")) {
                        return true;
                    } else {
                        extention = extention.substring(extention.indexOf("/") + 1, extention.length());
                        return extention.matches(FILE_FORMATS_FOR_STRING_MATCH);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFileIconUri(Context context, Uri uri) {
        try {
            if (uri != null) {
                ContentResolver cR = context.getContentResolver();
                String extention = cR.getType(uri);

                if (extention == null) {
                    File file = null;
                    if (FilePathLocator.getPath(context, uri)==null || FilePathLocator.getPath(context, uri).equals("null")) {
                        file = new File(String.valueOf(uri));
                    }else {
                        file = new File(FilePathLocator.getPath(context, uri));
                    }
                    String filenameArray[] = file.getName().split("\\.");
                    extention = filenameArray[filenameArray.length - 1];
                    extention = extention.toLowerCase();

                }
                if (extention != null) {
                    if (extention.matches("audio/mpeg")) {
                        return getIcon("audio/mpeg");
                    } else {
                        extention = extention.substring(extention.indexOf("/") + 1, extention.length());

                        return getFileType(extention);
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFileIconPaths(Context context, String path) {
        try {

            if (!path.contains(".")){
                path =FilePathLocator.getPath(context, Uri.parse(path));
                String filenameArray[] = path.split("\\.");
                String extention = filenameArray[filenameArray.length - 1];
                extention = extention.toLowerCase();
                return getFileType(extention);

            }else
            if (path!=null && !path.isEmpty() && !path.equals("null")) {
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
    public int getFileIconPath(String path) {
        try {

            if (path!=null && !path.isEmpty() && !path.equals("null")) {
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


    //integer code
    public int getFileTypeCodeFromPath(String name) {

        try {
            String filenameArray[] = name.split("\\.");
            String extention = filenameArray[filenameArray.length - 1];
            extention = extention.toLowerCase();
            return getFileType(extention);

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

            }else if (extention.matches("ppt|pptx")) {
                return Values.Gallery.GALLERY_PPT;
            }else if (extention.matches("v-card|octet_stream|vcard|vcf|x_vcard")) {
                return Values.Gallery.CONTACT_TYPE;
            }else {
                return Values.Gallery.GALLERY_OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getFileExtentonFromPath(String path) {
        try {
            String filenameArray[] = path.split("\\.");
            String extention = filenameArray[filenameArray.length - 1];
            extention = extention.toLowerCase();
            return extention;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getIcon(String extention) {
        try {
            if (extention == null)  //if null
                return 0;

            if (extention.matches("jpg|jpeg|png|gif|bmp")) {
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
            } else if (extention.matches("mp3|audio/mpeg")) {
                return Values.Gallery.GALLERY_AUDIO;
            } else if (extention.matches("mp4|mpeg|3gpp|mkv|flv|3gp|mov|avi|webm")) {
                return Values.Gallery.GALLERY_VIDEO;
            } else if (extention.matches("json")) {
                return Values.Gallery.GALLERY_JSON;
            } else if (extention.matches("sql")) {
                return Values.Gallery.GALLERY_SQL;
            } else if (extention.matches("odt")) {
                //return R.drawable.ic_attachment_file;
            } else if (extention.matches("csv")) {
                return Values.Gallery.GALLERY_CSV;
            } else if (extention.matches("xml")) {
                return Values.Gallery.GALLERY_XML;
            } else if (extention.matches("html")) {
                return Values.Gallery.GALLERY_HTML;
            } else if (extention.matches("js")) {
                return Values.Gallery.GALLERY_JS;
            } else if (extention.matches("v-card|octet_stream|vcard|vcf|x_vcard")) {
                return Values.Gallery.CONTACT_TYPE;
            }
            else {
                return Values.Gallery.GALLERY_OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public File createImageFile(String imageFileName) {


        try {
            File storageDir = null;
            int file_type = getFileIconPath(imageFileName);

            if (storageDir != null) {
                return new File(storageDir, imageFileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File createImageFile(String imageFileName, String userName,Context context) throws IOException {

        //userName = userName.replaceAll(" ", "");
        String current_time = String.valueOf(System.currentTimeMillis());
        try {
            File storageDir = null;
            int file_type = getFileIconPath(imageFileName);
            if (file_type == Values.Gallery.GALLERY_VIDEO) {
                storageDir = getVideosStorageDir(userName+"/"+current_time,context);
            } else if (file_type == Values.Gallery.GALLERY_IMAGE) {
                storageDir = getImageStorageDir(userName+"/"+current_time, context);
            } else if (file_type == Values.Gallery.GALLERY_AUDIO) {
                storageDir = getAudioStorageDir(userName+"/"+current_time,context);
            } else if (file_type != 0) {
                storageDir = getFilesStorageDir(userName+"/"+current_time,context);
            }
            if (storageDir != null) {


                /*File dir = new File(
                        Environment.getExternalStorageDirectory() + "/" + storageDir);

                if(!dir.exists())
                    dir.mkdirs();*/

                return new File(storageDir, imageFileName);
            }
            //   galleryAddPic(image);  to showup in gallery
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createFileFromUri(Context context, Uri uri) throws IOException {
        File dest = null;
        try {

            File storageDir = null;
            int file_type = getFileIconUri(context, uri);
            File oFile = new File(FilePathLocator.getPath(context, uri));
            String fileName = oFile.getName().replace(" ", "");

            if (storageDir != null) {
                dest = new File(storageDir, fileName);
                if (!dest.exists()) {
                    dest.createNewFile();
                } else {
                    dest = new File(storageDir, fileName);
                }
                copyFile(oFile, dest);
            }
            galleryAddPic(dest, context);
            return dest.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createFileFromPath(Context context, String path, String userName) throws IOException {
        File dest = null;
        try {
            userName = userName.replaceAll(" ", "");
            File storageDir = null;
            int file_type = getFileIconPath(path);
            File oFile = new File(path);
            String fileName = oFile.getName().replace(" ", "");
            // fileName=TimeHelper.getInstance().getTimeStamp()+"_"+fileName;
            /*if (file_type == R.drawable.ic_pic_video_file) {
                storageDir = getVideosStorageDir(userName);
            } else if (file_type == R.drawable.ic_pic_img_from_camera) {
                storageDir = getImageStorageDir(userName);
            } else if (file_type == R.drawable.ic_pic_contact) {
                storageDir = getAudioStorageDir(userName);
            } else if (file_type != 0) {
                storageDir = getFilesStorageDir(userName);
            }*/
            if (storageDir != null) {
                dest = new File(storageDir, fileName);
                if (!dest.exists()) {
                    dest.createNewFile();
                    copyFile(oFile, dest);
                }
            }
            galleryAddPic(dest, context);
            return dest.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public String createSecurityImageFileFromUri(Context context, Uri uri) throws IOException {
        File dest = null;
        try {

            File storageDir = null;
            String path = FilePathLocator.getPath(context, uri);
            if (path == null) {
                return null;
            }
            File oFile = new File(FilePathLocator.getPath(context, uri));
            String fileName = oFile.getName().replace(" ", "");
            storageDir = getSecurityFilesStorageDir();

            if (storageDir != null) {
                dest = new File(storageDir, fileName);
                if (dest.exists()) {
                    dest.delete();
                }
                dest.createNewFile();
                copyFile(oFile, dest);
            }

            return dest.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[(int) src.length()];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (Exception e) {
                return false;
            } finally {
                out.close();
            }
        } catch (Exception e) {
            return false;
        } finally {
            in.close();
        }

        return true;
    }

    public void galleryAddPic(File photoFile, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(String.valueOf(photoFile));
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        //view.setImageURI(Uri.fromFile(f));

        // return setPic(customer_photo,String.valueOf(photoFile));
    }

    /*without user name*/
    public File getImageStorageDir() {
        File file = new File(root, DIR_IMAGE);
        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getVideosStorageDir() {
        File file = new File(root, DIR_VIDEOS);
        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getAudioStorageDir() {
        File file = new File(root, DIR_AUDIO);
        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getFilesStorageDir() {
        File file = new File(root, DIR_Files);
        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getSecurityFilesStorageDir() {
        File file = new File(root, DIR_SECURITY);
        if (!file.mkdirs()) {

        }
        return file;
    }

    //with username
    public File getImageStorageDir(String userName,Context context) {
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(filesDir.getPath(), DIR_IMAGE + "/" + userName);
            }else {
                file = new File(filesDir.getPath(), DIR_IMAGE + "/" + "Me");
            }
        }
        else
        {
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(root, DIR_IMAGE + "/" + userName);
            }else {
                file = new File(root, DIR_IMAGE + "/" + "Me");
            }
        }


        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getVideosStorageDir(String userName,Context context) {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(filesDir.getPath(), DIR_VIDEOS + "/" + userName);
            }else {
                file = new File(filesDir.getPath(), DIR_VIDEOS + "/" + "Me");
            }
        }
        else
        {
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(root, DIR_VIDEOS + "/" + userName);
            }else {
                file = new File(root, DIR_VIDEOS + "/" + "Me");
            }
        }

        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getAudioStorageDir(String userName,Context context) {
        File file = null ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(filesDir.getPath(), DIR_AUDIO + "/" + userName);
            }else {
                file = new File(filesDir.getPath(), DIR_AUDIO + "/" + userName);
            }
        }
        else
        {
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(root, DIR_AUDIO + "/" + userName);
            }else {
                file = new File(root, DIR_AUDIO + "/" + userName);
            }
        }

        if (!file.mkdirs()) {

        }
        return file;
    }

    public File getFilesStorageDir(String userName,Context context) {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(filesDir.getPath(), DIR_Files + "/" + userName);
            }else {
                file = new File(filesDir.getPath(), DIR_Files + "/" + userName);
            }
        }
        else
        {
            if (userName!=null && !userName.trim().isEmpty() && !userName.equals("null")) {
                file = new File(root, DIR_Files + "/" + userName);
            }else {
                file = new File(root, DIR_Files + "/" + userName);
            }
        }

        if (!file.mkdirs()) {

        }
        return file;
    }

    /*compress image*/




    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public Uri getImageUriFromBitMap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(getRealPathFromURI(inContext, Uri.parse(path)));
    }

    public String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public String getFileSize(String path) {
        String file_size = "0kb";
        try {
            File file = new File(path);



            long fileLength = file.length();
            int size = (int) ((fileLength / 1024) / 1024);
            long remainder = ((fileLength / 1024) % 1024);

            String kb = String.valueOf(remainder);
            if (kb.length() > 2) {
                kb = kb.substring(0, 3);
            }

            String fileSize;
            if (size > 0) {
                fileSize = size + "." + kb + " mb";
            } else {
                fileSize = kb + " kb";
            }


            file_size = fileSize;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file_size;
    }


    public boolean isGif(String path) {
        try {
            String filenameArray[] = path.split("\\.");
            String extention = filenameArray[filenameArray.length - 1];
            extention = extention.toLowerCase();
            return extention.contains("gif");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void getThumbnailPathForLocalFile(Activity context,
                                             Uri fileUri, ImageView view) {

        long fileId = getFileId(context, fileUri);

        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        try {

            thumbCursor = context.managedQuery(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                Glide.with(view.getContext()).
                        load(thumbPath)
                        .into(view);

            }


        } finally {
        }

    }

    public static long getFileId(Activity context, Uri fileUri) {

        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null,
                null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int id = cursor.getInt(columnIndex);

            return id;
        }

        return 0;
    }
    public Bitmap getTheBitmapTHumbnail(Context context, Uri messageAttachment, boolean is_video) {
        Bitmap bitmap2=null;
        try {
            if (is_video) {


                bitmap2 = ThumbnailUtils.createVideoThumbnail(String.valueOf(messageAttachment), MediaStore.Video.Thumbnails.MICRO_KIND);
            }else {
                bitmap2 = ThumbnailUtils.createVideoThumbnail(String.valueOf(messageAttachment), Integer.parseInt(MediaStore.Images.Thumbnails.DATA));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap2;
    }

}
