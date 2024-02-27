package com.tvisha.click2magic.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tooltip.Tooltip;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.TrackData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisitPagesAdapter extends RecyclerView.Adapter<VisitPagesAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<TrackData> data = new ArrayList<>();
    String dateFormat = "dd MMM yy HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    public VisitPagesAdapter(Context context, List<TrackData> data, boolean isSelf) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;


    }

    @NonNull
    @Override
    public VisitPagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.visit_history_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<TrackData> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void setIsSelfStatus(boolean isSelf) {
        this.isSelf = isSelf;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final VisitPagesAdapter.ViewHolder holder, final int position) {

        final TrackData trackData=data.get(position);
        try {
            if(trackData!=null) {
                if (position == data.size() - 1) {
                    holder.line.setVisibility(View.GONE);
                } else {
                    holder.line.setVisibility(View.VISIBLE);
                }

                final String date =trackData.getCreatedAt();
                if (date != null && !date.trim().isEmpty()) {
                    try {
                        //yyyy-MM-dd HH:mm:ss
                        String dt = getDate(date);
                        //dd MMM yy HH:mm:ss
                        if (dt != null) {
                            holder.data_tv.setText(dt.substring(0, 6) + "," + dt.substring(7));
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                String name=trackData.getTitle();
                if(name!=null && !name.trim().isEmpty())
                {
                    if(name.length()>30)
                    {
                        holder.page_name_tv.setText(name.substring(0,30)+"...");
                    }
                    else
                    {
                        holder.page_name_tv.setText(name);
                    }


                }
                else
                {

                    holder.page_name_tv.setText("");
                }

                String timeSpent=trackData.getTimeSpent();

                Helper.getInstance().LogDetails("timeSpentData",timeSpent+"   "+trackData.getTitle());

                if(timeSpent!=null && !timeSpent.trim().isEmpty())
                {
                    int seconds=Integer.parseInt(timeSpent);

                    int hours=seconds/(60*60);
                    int minutes=seconds/60;
                    int remainSeconds=seconds%60;

                    if(minutes<10 && remainSeconds<10){
                        holder.time_tv.setText("0"+minutes+":0"+remainSeconds);
                    }
                    else if(minutes<10){
                        holder.time_tv.setText("0"+minutes+":"+remainSeconds);
                    }
                    else if(remainSeconds<10){
                        holder.time_tv.setText(minutes+":0"+remainSeconds);
                    }
                    else
                    {
                        holder.time_tv.setText(minutes+":"+remainSeconds);
                    }



                }
                else
                {
                    holder.time_tv.setText("00"+":00");
                }

           /*     holder.page_name_tv.setOnHoverListener(new View.OnHoverListener() {
                    @Override
                    public boolean onHover(View v, MotionEvent event) {
                        String url =data.get(position).getUrl();
                        Helper.getInstance().LogDetails("page_name_tv","hove");
                        if(url!=null && !url.trim().isEmpty())
                        {
                            TooltipCompat.setTooltipText(holder.page_name_tv, url);

                            holder.page_name_tv.setTooltipText(url);
                        }
                        else
                        {
                            holder.page_name_tv.setTooltipText("");
                            TooltipCompat.setTooltipText(holder.page_name_tv, "");
                        }
                        return false;
                    }
                });*/
                holder.page_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url =data.get(position).getUrl();


                          /*  if(url!=null && !url.trim().isEmpty())
                            {
                                new SimpleTooltip.Builder(mContext)
                                        .anchorView(holder.page_name_tv)
                                        .text(url)
                                        .gravity(Gravity.TOP)
                                        .animated(false)
                                        .backgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
                                        .arrowColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
                                        .textColor(mContext.getResources().getColor(R.color.white))
                                        .padding(mContext.getResources().getDimension(R.dimen.very_large_margin))
                                        .margin(mContext.getResources().getDimension(R.dimen.very_large_margin))
                                        .transparentOverlay(true)
                                        .build()
                                        .show();
                                //TooltipCompat.setTooltipText(holder.page_name_tv, url);

                            }
*/
                            if(url!=null && !url.trim().isEmpty())
                            {
                                Tooltip tooltip = new Tooltip.Builder(holder.page_name_tv)
                                        .setText(url)
                                        .setArrowEnabled(true)
                                        .setCancelable(true)
                                        .setGravity(Gravity.TOP)
                                        .setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
                                        .setTextColor(mContext.getResources().getColor(R.color.white))
                                        .setCornerRadius(mContext.getResources().getDimension(R.dimen.padding_3dp))
                                        .setArrowWidth(mContext.getResources().getDimension(R.dimen.padding_8dp))
                                        .setArrowHeight(mContext.getResources().getDimension(R.dimen.padding_8dp))
                                      //  .setTypeface(TypeFaceProvider.getTypeFace(mContext,"fonts/Poppins-Regular.ttf"))
                                        .show();
                            }





                    }
                });


                holder.urlImageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url =data.get(position).getUrl();


                        if (url != null && !url.trim().isEmpty()) {
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "http://" + url;
                            }
                            if (URLUtil.isValidUrl(url)) {
                                if (Helper.getConnectivityStatus(mContext)) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    mContext.startActivity(browserIntent);
                                } else {
                                    Toast.makeText(mContext, "Please check internet connection", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, "Url is not valid", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, "Url is empty", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public int getItemCount() {

        if(data!=null && data.size()>0){
            return data.size();
        }
        else
        {
            return 0;
        }




    }

    private TrackData getItem(int position) {
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
    private String getDate(String date) throws ParseException {
        try{
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            // c.add(Calendar.DATE, 1);
            Date nextDate = c.getTime();
            return sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.page_name_tv)
        TextView page_name_tv;

        @BindView(R.id.data_tv)
        TextView data_tv;

        @BindView(R.id.time_tv)
        TextView time_tv;

        @BindView(R.id.line)
        View line;

        @BindView(R.id.linkLayout)
        LinearLayout linkLayout;

        @BindView(R.id.pageLayout)
        RelativeLayout pageLayout;

        @BindView(R.id.urlImageLayout)
        RelativeLayout urlImageLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);




        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();


        }
    }

}