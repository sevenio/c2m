package com.tvisha.click2magic.ui;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.adapter.PdfAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.gauriinfotech.commons.Commons;

public class DocsActivity extends AppCompatActivity  {

    @BindView(R.id.back_lay)
    LinearLayout back_lay;

    @BindView(R.id.pdf_recyclerView)
    RecyclerView pdf_recyclerView;

    @BindView(R.id.moreLable)
    TextView moreLable;

    @BindView(R.id.actionLable)
    TextView actionLable;

    @BindView(R.id.noDocuments)
    TextView noDocuments;

    @OnClick(R.id.back_lay)
    void back(){
        onBackPressed();
    }

    @OnClick(R.id.moreLable)
    void more(){
        doc();
    }

    public  static List<File> fileList=new ArrayList<>();
    RecyclerView.LayoutManager pdf_manager;
    PdfAdapter pdfAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        ButterKnife.bind(this);
        initViews();
        getPdfFiles();


    }



    private void initViews() {
        try {
            back_lay = findViewById(R.id.back_lay);
            moreLable = findViewById(R.id.moreLable);
            noDocuments = findViewById(R.id.noDocuments);
            moreLable.setVisibility(View.VISIBLE);
            actionLable = findViewById(R.id.actionLable);
            actionLable.setText("Documents");
            pdf_recyclerView = (RecyclerView) findViewById(R.id.pdf_recyclerView);
            pdf_recyclerView.setHasFixedSize(true);
            pdf_manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            pdf_recyclerView.setLayoutManager(pdf_manager);
            pdf_recyclerView.setNestedScrollingEnabled(false);
            pdfAdapter = new PdfAdapter(DocsActivity.this, fileList);
            pdf_recyclerView.setAdapter(pdfAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getPdfFiles() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            Helper.getInstance().LogDetails("Docs", "getPdfFiles root" + root.toString());
            File myDir = new File(root);
            if (fileList != null && fileList.size() > 0) {
                fileList.clear();
                pdfAdapter.notifyDataSetChanged();
            }
            if (myDir != null) {
                Helper.getInstance().LogDetails("Docs", "getPdfFiles root not null");
                Search_Dir(myDir);
                if (fileList != null && fileList.size() > 0) {
                    noDocuments.setVisibility(View.GONE);
                } else {
                    noDocuments.setVisibility(View.VISIBLE);
                }
                Helper.getInstance().LogDetails("Docs", "getPdfFiles file list size" + fileList.size());
            } else {
                Helper.getInstance().LogDetails("Docs", "getPdfFiles root null");
            }
            pdfAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Search_Dir(File dir) {
        try {
            String pdfPattern = ".pdf";
            File FileList[] = dir.listFiles();
            if (FileList != null) {
                Helper.getInstance().LogDetails("Docs", "Search_Dir file list size" + FileList.length);
                for (int i = 0; i < FileList.length; i++) {

                    if (FileList[i].isDirectory()) {
                        Search_Dir(FileList[i]);
                    } else {
                        if (FileList[i].getName().endsWith(".pdf") || FileList[i].getName().endsWith(".xls") || FileList[i].getName().endsWith(".odt")) {
                            //here you have that file.
                            fileList.add(FileList[i]);
                            Helper.getInstance().LogDetails("Docs Search_Dir ", "Pdf Name " + FileList[i].getName() + " " + FileList[i].getAbsolutePath());

                        } else {
                            // fileList.add(FileList[i]);
                            Helper.getInstance().LogDetails("Docs Search_Dir ", "Pdf Name " + FileList[i].getName() + " " + FileList[i].getAbsolutePath());
                        }
                    }
                }
            } else {
                Helper.getInstance().LogDetails("Docs", "Search_Dir file list size null");
            }
        }catch ( Exception e){
            e.printStackTrace();
        }
    }


    public void sendAttachment(String path) {
        try {
            Intent intent = new Intent();
            intent.putExtra(Values.IntentData.DOC_PATH, path);
            setResult(RESULT_OK, intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doc() {

        try {

            String[] mimeTypes =
                    {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                            "text/plain",
                            "application/pdf",
                            "application/zip"};

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType("file/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            startActivityForResult(intent, Values.MyActionsRequestCode.ATTACHMENT_DOC);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case Values.MyActionsRequestCode.ATTACHMENT_DOC:
                        Helper.getInstance().LogDetails("onActivityResult", "ATTACHMENT_DOC");

                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        if(myFile!=null){
                            String path = myFile.getPath();

                        }

                        if (uri != null) {
                            Helper.getInstance().LogDetails("onActivityResult", "uri not null");

                            if(isGoogleDriveUri(uri))
                            {
                                String  doc_path=getDriveFilePath(data.getData(),DocsActivity.this);
                                if (doc_path != null) {
                                    Helper.getInstance().LogDetails("onActivityResult", "getDriveFilePath doc_path not null"+doc_path);
                                    sendAttachment(doc_path);
                                }
                            }
                            else
                            {
                                String  doc_path = getRealPathFromURI(data.getData());
                                // String doc_path = Commons.getPath(uri, DocsActivity.this);
                                if (doc_path != null) {
                                    Helper.getInstance().LogDetails("onActivityResult", "getRealPathFromURI doc_path not null"+doc_path);
                                    sendAttachment(doc_path);
                                }
                                else {
                                    doc_path=getPDFPath(data.getData());
                                    if (doc_path != null) {
                                        Helper.getInstance().LogDetails("onActivityResult", "getPDFPath doc_path not null"+doc_path);
                                        sendAttachment(doc_path);
                                    }
                                    else
                                    {
                                        doc_path = Commons.getPath(uri, DocsActivity.this);
                                        if(doc_path!=null){
                                            Helper.getInstance().LogDetails("onActivityResult", "Commons doc_path not null"+doc_path);
                                            sendAttachment(doc_path);
                                        }
                                    }

                                }
                            }

                        }


                        break;

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }
    private static String getDriveFilePath(Uri uri, Context context) {
        try {
            Uri returnUri = uri;
            Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             *     * move to the first row in the Cursor, get the data,
             *     * and display it.
             * */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            String size = (Long.toString(returnCursor.getLong(sizeIndex)));
            File file = new File(context.getCacheDir(), name);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read = 0;
                int maxBufferSize = 1 * 1024 * 1024;
                int bytesAvailable = inputStream.available();

                //int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }

                inputStream.close();
                outputStream.close();
                Log.e("File Path", "Path " + file.getPath());
                Log.e("File Size", "Size " + file.length());
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
            return file.getPath();
        }catch (Exception e){

            e.printStackTrace();
            return "";
        }

    }
    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        }
    }

    public String getPDFPath(Uri uri){

        try {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
