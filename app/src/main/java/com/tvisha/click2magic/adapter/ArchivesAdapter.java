package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.ui.HistotyChatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ArchivesAdapter extends RecyclerView.Adapter<ArchivesAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    public String nextDate = "";
    public int nextPosition = -1;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    long mLastClickTime = 0;
    private Context mContext;
    private List<ActiveChat> data = new ArrayList<>();

    public ArchivesAdapter(Context context, List<ActiveChat> data) {
        mContext = context;
        this.data = data;

    }

    @NonNull
    @Override
    public ArchivesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.archieve_chat_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<ActiveChat> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ArchivesAdapter.ViewHolder holder, final int position) {

        try {

            if (data != null && position < data.size()) {

                final ActiveChat activeChat = data.get(position);
                if (activeChat != null) {
                    holder.line.setVisibility(View.GONE);
                    holder.dateLayout.setVisibility(View.GONE);
                    if (activeChat.getGuestName() != null) {

                        if (activeChat.getGuestName().length() > 40) {
                            holder.name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName().substring(0, 100) + "..."));
                        } else {
                            holder.name_tv.setText(Helper.getInstance().capitalize(activeChat.getGuestName()));
                        }

                    } else {
                        holder.name_tv.setText("");
                    }

                    String rat=activeChat.getRating();
                    String tag_name=activeChat.getTag_name();
                    String agentName = activeChat.getGuestName();
                    String startTime=activeChat.getStartTime();

                    Helper.getInstance().LogDetails("history===== ",startTime +" "+agentName+" "+rat+" "+tag_name);
                    //2019-07-19 11:32:41

                    if(startTime!=null && !startTime.trim().isEmpty())
                    {
                        holder.chat_time_tv.setText(startTime.substring(11,16));
                    }
                    else
                    {
                        holder.chat_time_tv.setText("");
                    }


                   if(rat!=null && !rat.trim().isEmpty()){
                       if(!rat.trim().equals("NA") && rat.length()>0 )
                       {


                           if(Float.parseFloat(rat)==0)
                           {
                               holder.ratingBar.setVisibility(View.GONE);
                               holder.na_image.setVisibility(View.GONE);
                           }
                           else
                           {
                               holder.na_image.setVisibility(View.GONE);
                               holder.ratingBar.setVisibility(View.VISIBLE);
                               holder.ratingBar.setStarBackgroundColor(mContext.getResources().getColor(R.color.ratingBarInactiveColor));
                               holder.ratingBar.setBorderColor(mContext.getResources().getColor(R.color.ratingBarInactiveColor));
                               holder.ratingBar.setFillColor(mContext.getResources().getColor(R.color.ratingBarActiveColor));
                               holder.ratingBar.setStarBorderWidth(1);
                               holder.ratingBar.setStepSize(1);
                               holder.ratingBar.setEnabled(false);
                               holder.ratingBar.setClickable(false);
                               holder.ratingBar.setFocusableInTouchMode(false);
                               holder.ratingBar.setIndicator(true);
                               holder.ratingBar.setDrawBorderEnabled(false);
                               float f=Float.parseFloat(rat);
                               holder.ratingBar.setNumberOfStars((int) f);
                               holder.ratingBar.setRating(Float.parseFloat(rat));
                               holder.ratingBar.setStarSize(25);
                           }




                       }
                       else if(rat.equals("NA") && !rat.equals("0"))
                       {
                           holder.na_image.setVisibility(View.VISIBLE);
                           holder.ratingBar.setVisibility(View.GONE);
                       }
                       else
                       {
                           holder.na_image.setVisibility(View.GONE);
                           holder.ratingBar.setVisibility(View.GONE);
                       }
                   }
                   else
                   {
                       holder.na_image.setVisibility(View.GONE);
                       holder.ratingBar.setVisibility(View.GONE);
                   }
                    if(tag_name!=null && !tag_name.trim().isEmpty())
                    {
                         holder.tag_name_tv.setVisibility(View.VISIBLE);

                        if(tag_name.length()>20)
                        {
                            holder.tag_name_tv.setText(Helper.getInstance().capitalize(tag_name.substring(0,20)+"..."));

                        }
                        else
                        {
                            holder.tag_name_tv.setText(Helper.getInstance().capitalize(tag_name));
                        }

                    }
                    else
                    {
                          holder.tag_name_tv.setVisibility(View.GONE);
                        holder.tag_name_tv.setText("");

                    }

                    if (agentName != null) {

                        if (agentName.length() > 40) {
                            holder.agent_name_tv.setText(Helper.getInstance().capitalize(agentName.substring(0, 100)) + "...");
                        } else {
                            holder.agent_name_tv.setText(Helper.getInstance().capitalize(agentName));
                        }
                    } else {
                        holder.agent_name_tv.setText("");
                    }


                    Helper.getInstance().LogDetails("ArchiveAdapter dateCheck",position+" "+agentName+"  "+activeChat.getStartTime()+" ");


                    if (activeChat.getStartTime() != null && !activeChat.getStartTime().isEmpty()) {
                        try {
                            String date = getDate(activeChat.getStartTime());
                            String curDate = getCurrentDateTime();
                            String yesterDay = getYesterdayDateTime();


                            if (curDate.equals(date) && position == 0) {

                                holder.dateLayout.setVisibility(View.VISIBLE);
                                holder.today_tv.setVisibility(View.VISIBLE);
                                holder.today_tv.setText("Today- ");

                                holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6) );


                            } else if (!curDate.equals(date) && position == 0) {
                                holder.dateLayout.setVisibility(View.VISIBLE);
                                if (date.equals(yesterDay)) {
                                    holder.today_tv.setVisibility(View.VISIBLE);
                                    holder.today_tv.setText("Yesterday- ");
                                    holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6)  );
                                } else {
                                    holder.today_tv.setVisibility(View.GONE);
                                    holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6) );
                                }



                            }



                            nextPosition = position - 1;
                            if (nextPosition >= 0 && nextPosition < getItemCount()) {
                                nextDate = getDate(data.get(nextPosition).getStartTime());

                                if (!nextDate.equals(date) ) {

                                    holder.dateLayout.setVisibility(View.VISIBLE);
                                    if (curDate.equals(date)) {

                                        holder.today_tv.setVisibility(View.VISIBLE);
                                        holder.today_tv.setText("Today- ");

                                        holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6)   );
                                        today = true;

                                    } else {
                                        if (date.equals(yesterDay)) {
                                            holder.today_tv.setVisibility(View.VISIBLE);
                                            holder.today_tv.setText("Yesterday- ");
                                            holder.date_tv.setText(date.substring(0, 2)+"' "+date.substring(3, 6) );
                                        } else {
                                            holder.today_tv.setVisibility(View.GONE);
                                            holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6)  );
                                        }


                                    }


                                }
                                //new ly added
                               else {

                                    if(position==0){

                                        holder.dateLayout.setVisibility(View.VISIBLE);
                                        if (curDate.equals(date)) {

                                            holder.today_tv.setVisibility(View.VISIBLE);
                                            holder.today_tv.setText("Today- ");

                                            holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6)  );
                                            today = true;

                                        } else {
                                            if (date.equals(yesterDay)) {
                                                holder.today_tv.setVisibility(View.VISIBLE);
                                                holder.today_tv.setText("Yesterday- ");
                                                holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6) );
                                            } else {
                                                holder.today_tv.setVisibility(View.GONE);
                                                holder.date_tv.setText( date.substring(0, 2)+"' "+date.substring(3, 6)  );
                                            }

                                        }
                                    }
                                    else
                                    {
                                        holder.dateLayout.setVisibility(View.GONE);
                                    }



                                }
                            }
                            nextPosition = position + 1;
                            if (nextPosition >= 0 && nextPosition < getItemCount()) {
                                nextDate = getDate(data.get(nextPosition).getStartTime());
                            }
                            if (!nextDate.equals(date)) {
                                holder.line.setVisibility(View.GONE);
                            }

                            else
                            {
                                holder.line.setVisibility(View.VISIBLE);
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        holder.dateLayout.setVisibility(View.GONE);
                    }



                    holder.tag_name_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openChat(position);
                        }
                    });



                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            openChat(position);

                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openChat(int position){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if(selectionMode){
            selectionMode=false;
            if (data != null && position < data.size()) {
                ActiveChat activeChat = getItem(position);
                Intent intent = new Intent(mContext, HistotyChatActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.putExtra(Values.IntentData.USER_DATA, activeChat);
                mContext.startActivity(intent);
            }
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

    public void setSelectionMode(boolean selectionMode){
        this.selectionMode=selectionMode;
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

    private String getCurrentDateTime() {
        try{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private String getYesterdayDateTime() {
        try{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private ActiveChat getItem(int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_tv)
        TextView name_tv;

        @BindView(R.id.agent_name_tv)
        TextView agent_name_tv;

        @BindView(R.id.date_tv)
        TextView date_tv;

        @BindView(R.id.today_tv)
        TextView today_tv;

        @BindView(R.id.tag_name_tv)
        TextView tag_name_tv;

        @BindView(R.id.chat_time_tv)
        TextView chat_time_tv;

        @BindView(R.id.line)
        View line;

        @BindView(R.id.na_image)
        ImageView na_image;

        @BindView(R.id.dateLayout)
        LinearLayout dateLayout;

        @BindView(R.id.rating_feed_back)
        SimpleRatingBar ratingBar ;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }


    }
}