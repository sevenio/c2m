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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.fragments.ArchivesFragment;
import com.tvisha.click2magic.ui.HomeActivity;


import java.util.ArrayList;
import java.util.List;




public class ArchiveAgentsAdapter extends RecyclerView.Adapter<ArchiveAgentsAdapter.ViewHolder> {

    public static String tmUserId = "";
    List<ActiveAgent> data = new ArrayList<>();
    long mLastClickTime = 0;


    boolean isSelf = true;
    private Context mContext;

    String loginUserId="";
    ArchivesFragment archivesFragment;

    public ArchiveAgentsAdapter(Context context, List<ActiveAgent> data, boolean isSelf,ArchivesFragment archivesFragment) {

        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.archivesFragment=archivesFragment;
        loginUserId= Session.getUserID(context);

    }

    @Override
    public ArchiveAgentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.archive_agent_row_design, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArchiveAgentsAdapter.ViewHolder holder, final int position) {
        try {


            if (data != null && position < data.size()) {


                final ActiveAgent activeAgent = data.get(position);
                Helper.getInstance().LogDetails("archiveAgentList","ArchiveAgentsAdapter called "+position+"  "+activeAgent.getUserName());
                if (data.get(position).getUserName() != null && !data.get(position).getUserName().trim().isEmpty() && !data.get(position).getUserName().trim().equals("null")) {


                    if(data.get(position).getUserName().equals("All"))
                    {
                        holder.supportImage.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.supportImage.setVisibility(View.GONE);
                    }


                    if (data.get(position).getUserName().length() > 40) {
                        holder.customer_name.setText(Helper.getInstance().capitalize(data.get(position).getUserName().substring(0, 40) + "..."));
                    } else {
                        holder.customer_name.setText(Helper.getInstance().capitalize(data.get(position).getUserName()));
                    }


                } else {
                    holder.supportImage.setVisibility(View.GONE);
                    Helper.getInstance().LogDetails("archiveAgentList else else",data.get(position).getUserName()+" "+position);
                    holder.customer_name.setText("");
                }


                if(mContext instanceof HomeActivity)
                {
                    if(archivesFragment!=null){
                        if (archivesFragment.archive_active_agent_id != null && archivesFragment.archive_active_agent_id.equals(data.get(position).getAgentId())) {
                            holder.customer_name.setTextColor(ContextCompat.getColor(mContext, R.color.archiveAgentActiveTextColor));
                            holder.main_lay.setBackground(ContextCompat.getDrawable(mContext, R.drawable.archive_agent_active_bg));
                            holder.supportImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_support_active));
                            archivesFragment.scrollList(position);

                        }
                        else {
                            holder.customer_name.setTextColor(ContextCompat.getColor(mContext, R.color.archiveAgentInActiveTextColor));
                            holder.main_lay.setBackground(ContextCompat.getDrawable(mContext, R.drawable.archive_agent_inactive_bg));
                            holder.supportImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_support));
                        }
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
            Helper.getInstance().LogDetails("archiveAgentList getItemCount", "if ");
            return data.size();
        } else {
            Helper.getInstance().LogDetails("archiveAgentList getItemCount", "else");
            return 0;
        }


    }

    public void setList(List<ActiveAgent> entries) {

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

    private ActiveAgent getItem(int position) {
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



        @BindView(R.id.main_lay)
         LinearLayout main_lay;

        @BindView(R.id.supportImage)
        ImageView supportImage;



        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            try{




                if (mContext instanceof HomeActivity) {
                    if(archivesFragment!=null) {
                        ActiveAgent activeChat = getItem(getAdapterPosition());

                        if (activeChat != null) {

                            if (archivesFragment.archive_active_agent_id != null) {
                                if (activeChat.getAgentId().equals(archivesFragment.archive_active_agent_id)) {
                                    return;
                                }
                            }

                            int position = getAdapterPosition();
                            if (position < data.size() && data.get(position) != null) {
                                archivesFragment.archive_active_agent_id = activeChat.getAgentId();
                                archivesFragment.updateArchivesList(activeChat, position);
                                notifyDataSetChanged();
                            }

                        }
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}