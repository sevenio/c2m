package com.tvisha.click2magic.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.Image;
import com.tvisha.click2magic.ui.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class ImageMessageAdapter extends RecyclerView.Adapter<ImageMessageAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String user_name = "";
    long mLastClickTime = 0;

    private Context context;
    private List<Image> data = new ArrayList<>();
    private List<Image> list = new ArrayList<>();


    public ImageMessageAdapter(Context context, List<Image> data) {
        this.context = context;
        this.list = data;
        this.data.addAll(data);

        user_name =  Session.getUserName(context);
    }

    @NonNull
    @Override
    public ImageMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.collateral_meesage_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<Image> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageMessageAdapter.ViewHolder viewHolder, final int position) {
        try {
            if (data != null && position < data.size()) {


                final Image Image = data.get(position);
                if (Image != null) {
                    viewHolder.message_tv.setText(Helper.getInstance().capitalize(Image.getTitle()));
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (context instanceof ChatActivity) {
                            ((ChatActivity) context).sendCollateral(ChatActivity.s3Url + Image.getPath(), Image.getTitle(), "");
                        }
                    }
                });


                String path = data.get(position).getPath();

                String attachmentType = "";
                if (attachmentType == null || attachmentType.trim().isEmpty()) {
                    int index = path.lastIndexOf(".");
                    String fileType = path.substring(index + 1);
                    if (fileType != null && !fileType.trim().isEmpty()) {
                        attachmentType = fileType.replace(" ", "");
                    }


                    Helper.getInstance().LogDetails("MESSAGE_TYPE_ATTACHMENT", "attachmentType null" + index + " " + path + attachmentType);

                }

                if (attachmentType.toLowerCase().equals("pdf")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
                } else if (attachmentType.toLowerCase().equals("doc") || attachmentType.toLowerCase().equals("docx")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc));
                } else if (attachmentType.toLowerCase().equals("odt")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                } else if (attachmentType.toLowerCase().equals("bmp")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                } else if (attachmentType.toLowerCase().equals("xls")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
                } else if (attachmentType.toLowerCase().equals("xml")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml));
                } else if (attachmentType.toLowerCase().equals("css")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_css));
                } else if (attachmentType.toLowerCase().equals("cad")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cad));
                } else if (attachmentType.toLowerCase().equals("sql")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sql));
                } else if (attachmentType.toLowerCase().equals("aac")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aac));
                } else if (attachmentType.toLowerCase().equals("avi")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avi));
                } else if (attachmentType.toLowerCase().equals("gif")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_gif));
                } else if (attachmentType.toLowerCase().equals("dat")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dat));
                } else if (attachmentType.toLowerCase().equals("dll")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dll));
                } else if (attachmentType.toLowerCase().equals("dmg")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dmg));
                } else if (attachmentType.toLowerCase().equals("eps")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eps));
                } else if (attachmentType.toLowerCase().equals("fla")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fla));
                } else if (attachmentType.toLowerCase().equals("flv")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flv));
                } else if (attachmentType.toLowerCase().equals("html")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
                } else if (attachmentType.toLowerCase().equals("iso")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_iso));
                } else if (attachmentType.toLowerCase().equals("js")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_js));
                } else if (attachmentType.toLowerCase().equals("mov")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mov));
                } else if (attachmentType.toLowerCase().equals("mp3")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp3));
                } else if (attachmentType.toLowerCase().equals("mp4")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp4));
                } else if (attachmentType.toLowerCase().equals("mpg")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mpg));
                } else if (attachmentType.toLowerCase().equals("php")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_php));
                } else if (attachmentType.toLowerCase().equals("ppt") || attachmentType.toLowerCase().equals("pptx")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt));
                } else if (attachmentType.toLowerCase().equals("ps")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ps));
                } else if (attachmentType.toLowerCase().equals("psd")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_psd));
                } else if (attachmentType.toLowerCase().equals("rar")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar));
                } else if (attachmentType.toLowerCase().equals("txt")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
                } else if (attachmentType.toLowerCase().equals("video")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
                } else if (attachmentType.toLowerCase().equals("zip")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip));
                } else if (attachmentType.toLowerCase().equals("wmv")) {
                    viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wmv));
                } else {
                    String imagePath = ChatActivity.s3Url + path;

                    if (imagePath != null && !imagePath.trim().isEmpty()) {
                        imagePath = imagePath.replace("\"", "");
                        RequestOptions options = new RequestOptions()
                                .error(R.drawable.ic_attachment_img)
                                .disallowHardwareConfig()
                                .priority(Priority.HIGH);
                        Glide.with(context)
                                .load(imagePath)
                                .apply(options)
                                .into(viewHolder.attachmentImage);

                    } else {
                        viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                    }
                    // viewHolder.attachmentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_img));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        } else {
            return 0;
        }
    }

    private Image getItem(int position) {
        try {
            if (data.size() >= position && position != -1) {
                try {
                    return data.get(position);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void search(String filtetText) {
        try{
        if (list != null && list.size() > 0) {
            if (data != null && data.size() > 0) {
                data.clear();
                notifyDataSetChanged();
                Helper.getInstance().LogDetails("searchMethod", "called " + data.size() + " " + list.size());
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPath() != null && Helper.getInstance().stringMatchWithFirstWordofString(filtetText.trim(), list.get(i).getPath().trim())) {
                    addItemToSearchList(list.get(i));
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetList(List<Image> Images) {
        Helper.getInstance().LogDetails("searchMethod", "resetList called " + data.size() + " " + list.size());
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }

        List<Image> temp = new ArrayList<>();
        temp.addAll(Images);

        if (temp != null && temp.size() > 0) {
            data.addAll(temp);
            Helper.getInstance().LogDetails("searchMethod", "resetList end " + data.size() + " " + list.size());
            notifyDataSetChanged();
        }
    }

    private void addItemToSearchList(Image Image) {
        try{
        if (data != null) {
            if (data.size() == 0) {

                data.add(Image);
                Helper.getInstance().LogDetails("searchMethod addItemToSearchList", " end " + data.size());

                notifyDataSetChanged();
            } else {
                if (data.size() > 0) {
                    boolean isPresent = false;

                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getAssetId() != null && Image.getAssetId() != null && data.get(i).getAssetId().equals(Image.getAssetId())) {
                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent) {
                        data.add(Image);
                    }

                    Helper.getInstance().LogDetails("searchMethod addItemToSearchList", "changeStatus end " + data.size());

                    notifyDataSetChanged();
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_tv)
        TextView message_tv;

        @BindView(R.id.attachmentImage)
        ImageView attachmentImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }


    }
}