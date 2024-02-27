package com.tvisha.click2magic.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;

public class AttachmentViewActivity extends AppCompatActivity {

    ImageView image_preview;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_view);
        initViews();
        handleIntent();
        updateViews();
    }

    private void updateViews() {

        try {

            if (path != null && !path.trim().isEmpty()) {
                path = path.replace("\"", "");
                Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", path);
                Glide.with(AttachmentViewActivity.this)
                        .load(path)
                        .into(image_preview);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleIntent() {
        try{
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            path = bundle.getString(Values.IntentData.ATTACHMENT_PATH);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initViews() {
        image_preview = findViewById(R.id.image_preview);
    }
}
