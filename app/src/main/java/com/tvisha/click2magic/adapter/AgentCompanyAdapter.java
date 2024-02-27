package com.tvisha.click2magic.adapter;


import android.content.Context;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.fragments.AgentsFragment;
import com.tvisha.click2magic.ui.HomeActivity;


import java.util.ArrayList;
import java.util.List;



public class AgentCompanyAdapter extends RecyclerView.Adapter<AgentCompanyAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<SitesInfo> data = new ArrayList<>();
    String role="";
    AgentsFragment agentsFragment;

    public AgentCompanyAdapter(Context context, List<SitesInfo> data, boolean isSelf,AgentsFragment agentsFragment) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.agentsFragment=agentsFragment;

        role= Session.getUserRole(context);
    }

    @NonNull
    @Override
    public AgentCompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.agents_company_row_design, parent, false);
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
    public void onBindViewHolder(@NonNull final AgentCompanyAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {


                final SitesInfo sitesInfo = data.get(position);
                List<ActiveAgent> activeAgentList = new ArrayList<>();
                activeAgentList = sitesInfo.getActiveAgents();
                List<ActiveAgent> allAgentList = new ArrayList<>();
                allAgentList = sitesInfo.getAgents();
                if (sitesInfo != null) {
                    holder.company_name_tv.setText(sitesInfo.getSiteName());
                    int isOnline = sitesInfo.getOnline_status();
                    if (isOnline == 1) {
                        holder.status.setChecked(true);
                        if(activeAgentList!=null && activeAgentList.size()>0)
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_bg));
                        }
                        else
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_all_corner_bg));
                        }
                        holder.company_name_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.agents_active_count_tv.setTextColor(mContext.getResources().getColor(R.color.white));

                       // holder.statusLayout.setBackgroundColor(mContext.getResources().getColor(R.color.siteNameActiveBgColor));
                        holder.status1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.online));
                    } else {
                        holder.status.setChecked(false);
                        //  holder.statusLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        if(activeAgentList!=null && activeAgentList.size()>0)
                        {
                            //holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_inactive_bg));
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_bg));
                        }
                        else
                        {
                           // holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_inactive_all_corner_bg));
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_all_corner_bg));
                        }
                      //  holder.company_name_tv.setTextColor(mContext.getResources().getColor(R.color.comapny_name_color));
                        holder.company_name_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                      //  holder.agents_active_count_tv.setTextColor(mContext.getResources().getColor(R.color.agentNameColor));
                        holder.agents_active_count_tv.setTextColor(mContext.getResources().getColor(R.color.white));

                       // holder.statusLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        holder.status1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.offline));
                    }
                }

                int activeAgentsCount=sitesInfo.getAgentsCount();
                if(activeAgentsCount>0)
                {
                    holder.agents_active_count_tv.setVisibility(View.VISIBLE);
                    if (allAgentList.size() == 1) {
                        holder.agents_active_count_tv.setText(allAgentList.size() + " Agent");
                    } else {
                        holder.agents_active_count_tv.setText(allAgentList.size() + " Agents");
                    }
                }
                else
                {
                    holder.agents_active_count_tv.setText("0" + " Agents");
                    holder.agents_active_count_tv.setVisibility(View.VISIBLE);
                }

                if(activeAgentList!=null && activeAgentList.size()>0 && allAgentList!=null && allAgentList.size()>0){

                    holder.agents_count_tv.setVisibility(View.GONE);

                  // holder. agents_active_count_tv.setText("  ("+activeAgentList.size()+"/"+allAgentList.size()+")");
                   holder.active_agents_count_tv.setText(activeAgentList.size()+"");
                   holder.total_agents_count_tv.setText(allAgentList.size()+"");
                    if (allAgentList.size() == 1) {
                        holder.agents_count_tv.setText(allAgentList.size() + " Agent");
                    } else {
                        holder.agents_count_tv.setText(allAgentList.size() + " Agents");
                    }
                }
                else
                {
                    holder.agents_count_tv.setVisibility(View.GONE);
                    holder.agents_count_tv.setText("");

                    if(allAgentList!=null && allAgentList.size()>0){
                       // holder. agents_active_count_tv.setText("  ("+"0"+"/"+allAgentList.size()+")");
                        holder.active_agents_count_tv.setText("0");
                        holder.total_agents_count_tv.setText(allAgentList.size()+"");
                    }
                    else
                    {
                        holder.active_agents_count_tv.setText("0");
                        holder.total_agents_count_tv.setText("0");
                      //  holder. agents_active_count_tv.setText("  ("+"0"+"/"+"0"+")");
                    }

                }



                if (activeAgentList != null && activeAgentList.size() > 0) {



                    holder.line.setVisibility(View.GONE);
                    holder.no_chats_image.setVisibility(View.GONE);
                    holder.activie_chat_recycler_view.setVisibility(View.VISIBLE);

                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    holder.activie_chat_recycler_view.setLayoutManager(layoutManager3);
                    holder.activie_chat_recycler_view.setNestedScrollingEnabled(false);
                    AgentUserAdapter agentUserAdapter = new AgentUserAdapter(mContext, activeAgentList, true,agentsFragment);
                    agentUserAdapter.selectionMode = true;
                    agentUserAdapter.setSitesInfo(sitesInfo);
                    holder.activie_chat_recycler_view.setAdapter(agentUserAdapter);
                    float h = mContext.getResources().getDimension(R.dimen.active_chat_list_height);
                    if (activeAgentList.size() > 4) {
                        holder.activie_chat_recycler_view.getLayoutParams().height = (int) h;
                    } else {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        holder.activie_chat_recycler_view.setLayoutParams(lparams);
                    }

                } else {

                    holder.line.setVisibility(View.GONE);
                    holder.no_chats_image.setVisibility(View.GONE);
                    holder.activie_chat_recycler_view.setVisibility(View.GONE);
                }


                final List<ActiveAgent> finalActiveAgentList = activeAgentList;
                holder.activie_chat_recycler_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                        int action = motionEvent.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_MOVE:
                                if (finalActiveAgentList != null && finalActiveAgentList.size() > 4) {
                                    recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                                }

                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean b) {

                    }
                });


                holder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mContext instanceof HomeActivity) {

                            if (Utilities.getConnectivityStatus(mContext) > 0) {
                                if (holder.status.isChecked()) {
                                    sitesInfo.setOnline_status(1);
                                } else {
                                    sitesInfo.setOnline_status(0);
                                }

                                ((HomeActivity) mContext).EmitAgentStatusUpdated(sitesInfo.getOnline_status() + "", sitesInfo.getSiteToken(), sitesInfo.getSiteId(), sitesInfo.getAccountId());
                            } else {
                                Toast.makeText(mContext, "Please check internet connection", Toast.LENGTH_LONG).show();
                                if (holder.status.isChecked()) {
                                    sitesInfo.setOnline_status(0);
                                    holder.status.setChecked(false);
                                } else {
                                    sitesInfo.setOnline_status(1);
                                    holder.status.setChecked(true);
                                }

                            }

                        }
                    }
                });
                isSelf=Session.getIsSelf(mContext);


                if (role != null && !role.isEmpty()) {
                    if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
                    {
                        if (isSelf) {
                          //  holder.status1.setVisibility(View.VISIBLE);
                            holder.status1.setVisibility(View.GONE);
                        } else {
                           // holder.status1.setVisibility(View.VISIBLE);
                           holder.status1.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        holder.status1.setVisibility(View.GONE);

                    }
                }
                else
                {
                    holder.status1.setVisibility(View.GONE);
                }


                holder.status1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isSelf) {



                            if (mContext instanceof HomeActivity) {

                                if (Utilities.getConnectivityStatus(mContext) > 0) {
                                    if (sitesInfo.getOnline_status() == 0) {
                                        sitesInfo.setOnline_status(1);
                                    } else {
                                        sitesInfo.setOnline_status(0);
                                    }

                                    ((HomeActivity) mContext).EmitAgentStatusUpdated(sitesInfo.getOnline_status() + "", sitesInfo.getSiteToken(), sitesInfo.getSiteId(), sitesInfo.getAccountId());
                                } else {
                                    Toast.makeText(mContext, "Please check internet connection", Toast.LENGTH_LONG).show();


                                }

                            }
                        }
                    }
                });

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

        @BindView(R.id.agents_count_tv)
        TextView agents_count_tv;

        @BindView(R.id.agents_active_count_tv)
        TextView agents_active_count_tv;

        @BindView(R.id.active_agents_count_tv)
        TextView active_agents_count_tv;

        @BindView(R.id.total_agents_count_tv)
        TextView total_agents_count_tv;

        @BindView(R.id.status)
        SwitchCompat status;

        @BindView(R.id.activie_chat_recycler_view)
        RecyclerView activie_chat_recycler_view;

        @BindView(R.id.no_chats_image)
        ImageView no_chats_image;

        @BindView(R.id.status1)
        ImageView status1;

        @BindView(R.id.statusLayout)
        LinearLayout statusLayout;

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