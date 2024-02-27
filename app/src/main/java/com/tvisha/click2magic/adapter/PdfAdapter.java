package com.tvisha.click2magic.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.ui.DocsActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder>{
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    private List<File> items=new ArrayList<>();
    private Context context;
    public PdfAdapter(Context context, List<File> items) {
        this.items = items;
        this.context = context;

    }

    @Override
    public PdfAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pdf_row_design, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PdfAdapter.ViewHolder viewHolder, final int i) {
        File file=items.get(i);
        if(file!=null){
            if(file.getName()!=null){
                viewHolder.pdf_name_tv.setText(file.getName());

                long fileSizeInBytes = file.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;

                if(fileSizeInMB>=1)
                {
                    viewHolder.pdf_size_tv.setText(fileSizeInMB+"Mb");
                }
                else
                {
                    viewHolder.pdf_size_tv.setText(fileSizeInKB+"Kb");
                }

                String path = items.get(i).getPath();

                String attachmentType ="";
              
                if (attachmentType == null || attachmentType.trim().isEmpty()) {
                    int index = path.lastIndexOf(".");
                    String fileType = path.substring(index + 1);
                    if (fileType != null && !fileType.trim().isEmpty()) {
                        attachmentType = fileType.replace(" ", "");
                    }

                    Helper.getInstance().LogDetails("PdfAdapter", "attachmentType null" + index + " " + path + attachmentType);

                }

                if(attachmentType!=null && !attachmentType.trim().isEmpty())
                {
                if (attachmentType.toLowerCase().equals("pdf")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
                } else if (attachmentType.toLowerCase().equals("doc") || attachmentType.toLowerCase().equals("docx")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doc));
                } else if (attachmentType.toLowerCase().equals("odt")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                } else if (attachmentType.toLowerCase().equals("bmp")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attachment_file));
                } else if (attachmentType.toLowerCase().equals("xls")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
                } else if (attachmentType.toLowerCase().equals("xml")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xml));
                } else if (attachmentType.toLowerCase().equals("css")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_css));
                } else if (attachmentType.toLowerCase().equals("cad")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cad));
                } else if (attachmentType.toLowerCase().equals("sql")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sql));
                } else if (attachmentType.toLowerCase().equals("aac")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_aac));
                } else if (attachmentType.toLowerCase().equals("avi")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avi));
                } else if (attachmentType.toLowerCase().equals("gif")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_gif));
                } else if (attachmentType.toLowerCase().equals("dat")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dat));
                } else if (attachmentType.toLowerCase().equals("dll")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dll));
                } else if (attachmentType.toLowerCase().equals("dmg")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dmg));
                } else if (attachmentType.toLowerCase().equals("eps")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eps));
                } else if (attachmentType.toLowerCase().equals("fla")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fla));
                } else if (attachmentType.toLowerCase().equals("flv")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flv));
                } else if (attachmentType.toLowerCase().equals("html")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_html));
                } else if (attachmentType.toLowerCase().equals("iso")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_iso));
                } else if (attachmentType.toLowerCase().equals("js")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_js));
                } else if (attachmentType.toLowerCase().equals("mov")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mov));
                } else if (attachmentType.toLowerCase().equals("mp3")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp3));
                } else if (attachmentType.toLowerCase().equals("mp4")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mp4));
                } else if (attachmentType.toLowerCase().equals("mpg")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mpg));
                } else if (attachmentType.toLowerCase().equals("php")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_php));
                } else if (attachmentType.toLowerCase().equals("ppt") || attachmentType.toLowerCase().equals("pptx")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ppt));
                } else if (attachmentType.toLowerCase().equals("ps")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ps));
                } else if (attachmentType.toLowerCase().equals("psd")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_psd));
                } else if (attachmentType.toLowerCase().equals("rar")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rar));
                } else if (attachmentType.toLowerCase().equals("txt")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
                } else if (attachmentType.toLowerCase().equals("video")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
                } else if (attachmentType.toLowerCase().equals("zip")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zip));
                } else if (attachmentType.toLowerCase().equals("wmv")) {
                    viewHolder.attachmentTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wmv));
                }
                }


            }




        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(context instanceof DocsActivity){
                  ((DocsActivity) context).sendAttachment(items.get(i).getPath());
              }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setList(List<File> pics) {
        if (items!=null && items.size()>0){
            items.clear();
        }
        items.addAll(pics);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.pdf_name_tv)
         TextView pdf_name_tv;

        @BindView(R.id.pdf_size_tv)
        TextView pdf_size_tv;

        @BindView(R.id.attachmentTypeImage)
        ImageView attachmentTypeImage;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);



        }
    }

}

