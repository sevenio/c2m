package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.os.SystemClock;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;


public class TopRecyclerAdapter extends RecyclerView.Adapter<TopRecyclerAdapter.ViewHolder> {

    public static String tmUserId = "";
    List<ActiveChat> data = new ArrayList<>();
    long mLastClickTime = 0;

    int select_position;
    boolean isSelf = true;
    private Context mContext;

    public TopRecyclerAdapter(Context context, List<ActiveChat> data, boolean isSelf) {

        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
    }

    @Override
    public TopRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.top_recycler_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TopRecyclerAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {


                final ActiveChat activeChat = data.get(position);
                if (data.get(position).getGuestName() != null && !data.get(position).getGuestName().trim().isEmpty() && !data.get(position).getGuestName().trim().equals("null")) {
                    if (data.get(position).getGuestName().length() > 40) {
                        holder.customer_name.setText(Helper.getInstance().capitalize(data.get(position).getGuestName().substring(0, 40) + "..."));
                    } else {
                        holder.customer_name.setText(Helper.getInstance().capitalize(data.get(position).getGuestName()));
                    }

                } else {
                    holder.customer_name.setText("");
                }
                if (data.get(position).getUnread_message_count() == 0) {

                    holder.message_count.setText("");
                    holder.message_count.setVisibility(View.GONE);

                } else {
                    holder.message_count.setVisibility(View.VISIBLE);
                    if (data.get(position).getUnread_message_count() > 99) {
                        holder.message_count.setText("99+");
                    } else {
                        holder.message_count.setText(data.get(position).getUnread_message_count() + "");
                    }

                }
                int online=data.get(position).getOnline();
                if (ChatActivity.chat_id != null && ChatActivity.chat_id.equals(data.get(position).getChatId())) {
                    holder.customer_name.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    holder.main_lay.setBackground(ContextCompat.getDrawable(mContext, R.drawable.user_name_active_rect_bg));

                    ((ChatActivity) mContext).scrollList(position);
                    if(online==1){
                        holder.userStatusLine.setBackgroundColor(mContext.getResources().getColor(R.color.onlineViewColor));
                    }
                    else
                    {
                        holder.userStatusLine.setBackgroundColor(mContext.getResources().getColor(R.color.offlineViewColor));
                    }

                } else {

                    holder.customer_name.setTextColor(ContextCompat.getColor(mContext, R.color.single_chat_user_name_color));
                    holder.main_lay.setBackground(ContextCompat.getDrawable(mContext, R.drawable.user_name_inactive_rect_bg));

                    if(online==1){
                        holder.userStatusLine.setBackgroundColor(mContext.getResources().getColor(R.color.onlineViewColor));
                    }
                    else
                    {
                        holder.userStatusLine.setBackgroundColor(mContext.getResources().getColor(R.color.offlineViewColor));
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        if (data != null && data.size() > 0) {
            Helper.getInstance().LogDetails("getItemCount", "if ");
            return data.size();
        } else {
            Helper.getInstance().LogDetails("getItemCount", "else");
            return 0;
        }


    }

    public void setList(List<ActiveChat> entries) {

        try{

        if (data != null && data.size() > 0) {
            data.clear();
        }
        if (entries != null && entries.size() > 0) {
            data.addAll(entries);
            // data=entries;
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private ActiveChat getItem(int position) {
        try {
            if (data.size() > position && position != -1) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.customer_name)
         TextView customer_name;

        @BindView(R.id.message_count)
        TextView message_count;

        @BindView(R.id.userStatusLine)
        View userStatusLine;

        @BindView(R.id.main_lay)
         RelativeLayout main_lay;




        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            ButterKnife.bind(this,v);



        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            try{

            if (mContext instanceof ChatActivity) {
                ActiveChat activeChat = getItem(getAdapterPosition());

                if (activeChat != null) {

                    if (ChatActivity.activeChat != null) {
                        if (activeChat.getTmVisitorId() == ChatActivity.activeChat.getTmVisitorId()) {
                            return;
                        }
                    }

                    int position = getAdapterPosition();

                    if (position < data.size() && data.get(position) != null) {
                        data.get(position).setUnread_message_count(0);
                        notifyDataSetChanged();

                        String siteId = data.get(position).getSiteId();
                        if (ChatActivity.sitesInfoList != null && position < ChatActivity.sitesInfoList.size() && ChatActivity.sitesInfoList.get(position) != null) {
                            for (int i = 0; i < ChatActivity.sitesInfoList.size(); i++) {
                                String sid = ChatActivity.sitesInfoList.get(i).getSiteId();
                                if (siteId != null && sid != null && siteId.equals(sid)) {
                                    if (ChatActivity.sitesInfoList.get(i).getActiveChats() != null && ChatActivity.sitesInfoList.get(i).getActiveChats().size() > 0 && position< ChatActivity.sitesInfoList.get(i).getActiveChats().size()) {
                                        ChatActivity.sitesInfoList.get(i).getActiveChats().get(position).setUnread_message_count(0);
                                        if (mContext instanceof ChatActivity) {
                                            ((ChatActivity) mContext).saveSiteData();
                                        }

                                        break;
                                    }

                                }
                            }

                        }
                        notifyDataSetChanged();
                        ((ChatActivity) mContext).updateChatList(activeChat, position);
                    }

                }


            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}