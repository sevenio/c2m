package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;


public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<SitesInfo> data = new ArrayList<>();

    public CompanyAdapter(Context context, List<SitesInfo> data, boolean isSelf) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;


    }

    @NonNull
    @Override
    public CompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.company_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<SitesInfo> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void setIsSelfStatus(boolean isSelf) {
        this.isSelf = isSelf;
    }

    @Override
    public void onBindViewHolder(@NonNull final CompanyAdapter.ViewHolder holder, final int position) {

        try {
            if (data != null && position < data.size()) {


                final SitesInfo sitesInfo = data.get(position);


                Helper.getInstance().LogDetails("updateAgentStatusInfo ", sitesInfo.getSiteId() + " --- " + sitesInfo.getOnline_status());

                if (sitesInfo != null) {
                    final String siteId = sitesInfo.getSiteId();


                    if (isSelf) {
                        Helper.getInstance().LogDetails("CompanyAdapter", isSelf + " " + position + " " );
                        holder.itemView.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(position==0){
                            params.setMargins(0,0,15,0);
                        }
                        else
                        {
                            params.setMargins(0,0,15,0);
                        }

                        holder.itemView.setLayoutParams(params);
                      //  holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    } else {

                        if (sitesInfo.isPresent()) {
                            holder.itemView.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if(position==0){
                                params.setMargins(0,0,15,0);
                            }
                            else
                            {
                                params.setMargins(0,0,15,0);
                            }
                            holder.itemView.setLayoutParams(params);
                           // holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }


                        Helper.getInstance().LogDetails("CompanyAdapter", isSelf + " " + position  + "  " + siteId + " " + sitesInfo.isPresent()+" "+sitesInfo.getSiteName());


                    }


                    holder.company_name_tv.setText(sitesInfo.getSiteName());
                    if (sitesInfo.getUnread_message_count() == 0) {
                        holder.unread_count_tv.setVisibility(View.GONE);
                    } else {
                        holder.unread_count_tv.setVisibility(View.VISIBLE);
                        if (sitesInfo.getUnread_message_count() > 99) {
                            holder.unread_count_tv.setText("99+");

                        } else {
                            holder.unread_count_tv.setText(sitesInfo.getUnread_message_count() + "");
                        }

                    }


                    if (mContext instanceof ChatActivity) {
                        if (ChatActivity.current_site_id != null) {
                            String sid = ChatActivity.current_site_id;

                            String siteToken = sitesInfo.getSiteToken();

                            if (siteId != null && sid != null && !siteId.trim().isEmpty() && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                ChatActivity.current_site_name=sitesInfo.getSiteName();
                                ChatActivity.siteToken = sitesInfo.getSiteToken();
                               // holder.itemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.company_active_bg));
                                //ChatActivity.siteToken = siteToken;
                                holder.line.setVisibility(View.VISIBLE);
                                ((ChatActivity) mContext).scrollCompanyList(position);
                            } else {
                                holder.line.setVisibility(View.INVISIBLE);
                              //  holder.itemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.company_inactive_bg));
                            }
                        }
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<ActiveChat> activeChatArrayList = new ArrayList<>();


                            if (sitesInfo.getActiveChats() != null && sitesInfo.getActiveChats().size() > 0) {
                                activeChatArrayList = sitesInfo.getActiveChats();
                            } else {
                                activeChatArrayList = sitesInfo.getActiveChats();
                            }

                            if (ChatActivity.current_site_id != null && sitesInfo.getSiteId() != null && !ChatActivity.current_site_id.equals(sitesInfo.getSiteId())) {
                                ChatActivity.current_site_id = sitesInfo.getSiteId();
                                ChatActivity.siteToken = sitesInfo.getSiteToken();
                                ((ChatActivity) mContext).updateUserList(activeChatArrayList);
                                notifyDataSetChanged();
                            }


                        }
                    });


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

    private SitesInfo getItem(int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.company_name_tv)
        TextView company_name_tv;

        @BindView(R.id.unread_count_tv)
        TextView unread_count_tv;

        @BindView(R.id.itemLayout)
        RelativeLayout itemLayout;

        @BindView(R.id.line)
        View line;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);



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