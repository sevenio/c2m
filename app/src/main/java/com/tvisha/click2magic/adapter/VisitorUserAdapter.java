package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.services.kms.model.TagException;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.fragments.HomeFragment;
import com.tvisha.click2magic.ui.ChatActivity;
import com.tvisha.click2magic.ui.HomeActivity;


import java.util.ArrayList;
import java.util.List;

public class VisitorUserAdapter extends RecyclerView.Adapter<VisitorUserAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    String site_ids="";
    private List<ActiveChat> data = new ArrayList<>();
    HomeFragment homeFragment;
    SitesInfo sitesInfo=new SitesInfo();

    public VisitorUserAdapter(Context context, List<ActiveChat> data, boolean isSelf,HomeFragment homeFragment) {

        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.homeFragment=homeFragment;


    }

    public void setSiteInfo(SitesInfo siteInfo){
        this.sitesInfo=siteInfo;
    }

    @NonNull
    @Override
    public VisitorUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.active_user_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<ActiveChat> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void setIsSelfStatus(boolean isSelf) {
        this.isSelf = isSelf;
    }

    @Override
    public void onBindViewHolder(@NonNull final VisitorUserAdapter.ViewHolder holder, final int position) {

        try {
            if (data != null && position < data.size()) {

                if(position==0)
                {
                    if(sitesInfo.getOnline_status()==1)
                    {
                        holder.line.setVisibility(View.GONE);
                        holder.fullLine.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.line.setVisibility(View.GONE);
                        holder.fullLine.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    holder.line.setVisibility(View.VISIBLE);
                    holder.fullLine.setVisibility(View.GONE);
                }



                final ActiveChat activeUserChatData = data.get(position);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChat(position);
                    }
                });

                if (activeUserChatData != null) {

                    if (activeUserChatData.getEmail() != null) {
                        holder.email_tv.setText(" " + activeUserChatData.getEmail());
                    } else {
                        holder.email_tv.setText("");
                    }

                    if (activeUserChatData.getGuestName() != null) {


                        if (activeUserChatData.getGuestName().length() > 40) {
                            holder.user_name_tv.setText(Helper.getInstance().capitalize(activeUserChatData.getGuestName().substring(0, 40) + "..."));
                        } else {
                            holder.user_name_tv.setText(Helper.getInstance().capitalize(activeUserChatData.getGuestName()));
                        }

                    } else {
                        holder.user_name_tv.setText("");
                    }

                    if (activeUserChatData.getVisitCount() != null) {
                        holder.visit_count_tv.setText(activeUserChatData.getVisitCount() + " visit");
                    } else {
                        holder.visit_count_tv.setText("");
                    }


                    if (activeUserChatData.getUnread_message_count() > 0) {
                        Helper.getInstance().LogDetails("VisitorUserAdapter","count if"+activeUserChatData.getUnread_message_count()+" "+position);
                        holder.unread_count_tv.setVisibility(View.VISIBLE);
                        if (activeUserChatData.getUnread_message_count() > 99) {
                            holder.unread_count_tv.setText("99+");
                        } else {
                            holder.unread_count_tv.setText(activeUserChatData.getUnread_message_count() + "");
                        }

                    } else {
                        Helper.getInstance().LogDetails("VisitorUserAdapter","count else"+activeUserChatData.getUnread_message_count()+" "+position);
                        holder.unread_count_tv.setVisibility(View.INVISIBLE);
                    }


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        if (data != null) {
            return data.size();

        } else {
            return 0;

        }


    }

    private void openChat(int pos) {
        try{
        if (selectionMode) {
            selectionMode = false;
            ActiveChat activeChat = getItem(pos);

            if (activeChat != null) {
                int position = pos;
                activeChat.setUnread_message_count(0);
                if (position < data.size() && data.get(position) != null) {
                    data.get(position).setUnread_message_count(0);
                    notifyDataSetChanged();
                }
                String siteId = data.get(position).getSiteId();


                if (mContext instanceof HomeActivity) {
                    if(homeFragment!=null)
                    {
                        if (homeFragment.sitesInfoList != null && position < homeFragment.sitesInfoList.size() && homeFragment.sitesInfoList.get(position) != null) {
                            for (int i = 0; i < homeFragment.sitesInfoList.size(); i++) {
                                String sid = homeFragment.sitesInfoList.get(i).getSiteId();
                                if (siteId != null && sid != null && siteId.equals(sid)) {
                                    if (homeFragment.sitesInfoList.get(i).getActiveChats() != null) {
                                        if (homeFragment.sitesInfoList.get(i).getActiveChats().size() > 0 && position < homeFragment.sitesInfoList.get(i).getActiveChats().size()) {

                                            homeFragment.sitesInfoList.get(i).getActiveChats().get(position).setUnread_message_count(0);
                                            homeFragment.saveSiteData();
                                        }

                                    }

                                }
                                break;
                            }
                        }

                    }
                    }




                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(Values.IntentData.USER_DATA, activeChat);
                intent.putExtra(Values.IntentData.POSITION, position);
                intent.putExtra(Values.IntentData.IS_SELF, isSelf);
                intent.putExtra(Values.IntentData.SELECTED_AGENT_SITE_ID, site_ids);
                intent.putExtra(Values.MyActions.NOTIFICATION, false);
                intent.putExtra(Values.MyActions.MAIN_NOTIFICATION,false);
                intent.putParcelableArrayListExtra(Values.IntentData.USER_LIST, (ArrayList<? extends Parcelable>) data);
                mContext.startActivity(intent);
            }

        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void agentSiteId(String site_ids){
        this.site_ids=site_ids;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.email_tv)
        TextView email_tv;

        @BindView(R.id.visit_count_tv)
        TextView visit_count_tv;

        @BindView(R.id.user_name_tv)
        TextView user_name_tv;

        @BindView(R.id.unread_count_tv)
        TextView unread_count_tv;

        @BindView(R.id.line)
        View line;

        @BindView(R.id.fullLine)
        View fullLine;

        @BindView(R.id.unread_rl_view)
        RelativeLayout unread_rl_view;

        @BindView(R.id.item)
        LinearLayout item;

        @BindView(R.id.ll_view)
        LinearLayout ll_view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);



        }

        @Override
        public void onClick(View v) {

            /*if (SystemClock.elapsedRealtime() - mLastClickTime < 50) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();*/

            openChat(getAdapterPosition());


        }

        @Override
        public boolean onLongClick(View v) {


            openChat(getAdapterPosition());

            return false;
        }


    }

}