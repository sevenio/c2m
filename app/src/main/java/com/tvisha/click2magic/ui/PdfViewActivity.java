package com.tvisha.click2magic.ui;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.tvisha.click2magic.R;


import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PdfViewActivity extends AppCompatActivity {
    @BindView(R.id.pdfView)
    PDFView pdfView;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        ButterKnife.bind(this);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        //getIntentData();
        //openP(file);

    }

/*
    private void getIntentData() {
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){

            position=bundle.getInt("position");
            file=CropActivity.fileList.get(position);
            if(file!=null){

                Helper.getInstance().LogDetails("getIntentData","file not null");
                openP(file);
            }
            else
            {
                Helper.getInstance().LogDetails("getIntentData","file  null");
            }
        }
    }
*/


    private void openP(File file){
        pdfView.fromFile(file)
                .pages(0) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();

    }
}
