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
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.fragments.ArchivesFragment;
import com.tvisha.click2magic.ui.HomeActivity;


import java.util.ArrayList;
import java.util.List;


public class ArchiveSitesAdapter extends RecyclerView.Adapter<ArchiveSitesAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<SitesInfo> data = new ArrayList<>();
    ArchivesFragment archivesFragment;

    public ArchiveSitesAdapter(Context context, List<SitesInfo> data, boolean isSelf,ArchivesFragment archivesFragment) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.archivesFragment=archivesFragment;


    }

    @NonNull
    @Override
    public ArchiveSitesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.site_row_design, parent, false);
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
    public void onBindViewHolder(@NonNull final ArchiveSitesAdapter.ViewHolder holder, final int position) {

        Helper.getInstance().LogDetails("ArchiveSitesAdapter","called");

        try {
            if (data != null && position < data.size()) {


                final SitesInfo sitesInfo = data.get(position);


                Helper.getInstance().LogDetails("ArchiveSitesAdapter ", sitesInfo.getSiteId() + " --- " + sitesInfo.getOnline_status());

                if (sitesInfo != null) {
                    final String siteId = sitesInfo.getSiteId();


                    if (isSelf) {
                        Helper.getInstance().LogDetails("ArchiveSitesAdapter if", isSelf + " " + position + " " );
                        holder.itemView.setVisibility(View.VISIBLE);
                        /*RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(position==0){
                            params.setMargins(0,0,0,0);
                        }
                        else
                        {
                            params.setMargins(15,0,0,0);
                        }

                        holder.itemView.setLayoutParams(params);*/
                      //  holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    } else {
                        Helper.getInstance().LogDetails("ArchiveSitesAdapter else", isSelf + " " + position + " " );
                        if (sitesInfo.isPresent()) {
                            holder.itemView.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                      /*     if(position==0){
                               params.setMargins(0,0,0,0);
                           }
                           else
                           {
                               params.setMargins(15,0,0,0);
                           }

                            holder.itemView.setLayoutParams(params);*/
                           // holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }


                        Helper.getInstance().LogDetails("ArchiveSitesAdapter", isSelf + " " + position  + "  " + siteId + " " + sitesInfo.isPresent());


                    }


                    holder.company_name_tv.setText(sitesInfo.getSiteName());





                    if(mContext instanceof HomeActivity){
                        if(archivesFragment!=null){
                            if (archivesFragment.current_site_id != null) {
                                String sid = archivesFragment.current_site_id;
                                String siteToken = sitesInfo.getSiteToken();

                                if (siteId != null && sid != null && !siteId.trim().isEmpty() && !sid.trim().isEmpty() && siteId.equals(sid)) {
                                   // holder.itemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.company_active_bg));
                                    holder.line.setVisibility(View.VISIBLE);
                                    archivesFragment.scrollCompanyList(position);
                                } else {
                                    holder.line.setVisibility(View.INVISIBLE);
                                    //holder.itemLayout.setBackground(mContext.getResources().getDrawable(R.drawable.company_inactive_bg));
                                }
                            }
                        }
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<ActiveAgent> activeAgentArrayList = new ArrayList<>();


                            if (sitesInfo.getAllAgents() != null && sitesInfo.getAllAgents().size() > 0) {
                                activeAgentArrayList = sitesInfo.getAllAgents();
                            } else {
                                activeAgentArrayList = sitesInfo.getAllAgents();
                            }



                            if(mContext instanceof HomeActivity)
                            {
                                if(archivesFragment!=null)
                                {
                                    if (archivesFragment.current_site_id != null && sitesInfo.getSiteId() != null && !archivesFragment.current_site_id.equals(sitesInfo.getSiteId())) {
                                        archivesFragment.current_site_id = sitesInfo.getSiteId();
                                        archivesFragment.updateUserList(activeAgentArrayList);
                                    notifyDataSetChanged();
                                }
                                }

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

        @BindView(R.id.itemLayout)
        RelativeLayout itemLayout;

        @BindView(R.id.line)
        View line;


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