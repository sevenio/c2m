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

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.Helper.Utilities;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.Helper.progressButton.ProgressButton;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.ActiveAgent;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.fragments.HomeFragment;
import com.tvisha.click2magic.ui.BasicActivity;
import com.tvisha.click2magic.ui.HomeActivity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class VisitorCompanyAdapter extends RecyclerView.Adapter<VisitorCompanyAdapter.ViewHolder> {
    public  boolean isSelf = true;
    public static boolean selectionMode = true;
    long mLastClickTime = 0;
    private Context mContext;
    String site_ids="",role="";
    private List<SitesInfo> data = new ArrayList<>();
    HomeFragment homeFragment;

    public VisitorCompanyAdapter(Context context, List<SitesInfo> data, boolean isSelf,HomeFragment homeFragment) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.homeFragment=homeFragment;
        role= Session.getUserRole(context);

    }

    @NonNull
    @Override
    public VisitorCompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.visitors_company_row_design, parent, false);
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
    public void onBindViewHolder(@NonNull final VisitorCompanyAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {
                final SitesInfo sitesInfo = data.get(position);
                isSelf = Session.getIsSelf(mContext);


                //Helper.getInstance().LogDetails("updateAgentStatusInfo ",sitesInfo.getSiteId()+" --- "+sitesInfo.getOnline_status());

                if (sitesInfo != null) {
                    int dp=(int)mContext.getResources().getDimension(R.dimen.small_margin);
                    RecyclerView.LayoutParams layoutParams =new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,0,0,dp);

                    if (isSelf) {
                        Helper.getInstance().LogDetails("VisitorCompanyAdapter",isSelf+" "+position+" "+site_ids);
                        holder.itemView.setVisibility(View.VISIBLE);

                        holder.itemView.setLayoutParams(layoutParams);
                    } else {

                        if(sitesInfo.isPresent())
                        {
                            holder.itemView.setVisibility(View.VISIBLE);
                            holder.itemView.setLayoutParams(layoutParams);
                        }
                        else{
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }

                        String siteId=sitesInfo.getSiteId();
                        Helper.getInstance().LogDetails("VisitorCompanyAdapter",isSelf+" "+position+" "+site_ids+"  "+siteId +" "+sitesInfo.isPresent());


                    }

                    holder.company_name_tv.setText(sitesInfo.getSiteName());

                    List<ActiveChat> activeChatArrayList = new ArrayList<>();
                    activeChatArrayList = sitesInfo.getActiveChats();


                    int isOnline = sitesInfo.getOnline_status();
                    if (isOnline == 1) {
                        holder.status.setChecked(true);
                        if(activeChatArrayList!=null && activeChatArrayList.size()>0)
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_bg ));
                        }
                        else
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_active_all_corner_bg ));
                        }

                        holder.company_name_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.agents_count_tv.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.status1.setBackground(mContext.getResources().getDrawable(R.drawable.online));
                    } else {
                        holder.status.setChecked(false);
                        if(activeChatArrayList!=null && activeChatArrayList.size()>0)
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_inactive_bg));
                        }
                        else
                        {
                            holder.statusLayout.setBackground(mContext.getResources().getDrawable(R.drawable.site_name_inactive_all_corner_bg));
                        }
                        holder.company_name_tv.setTextColor(mContext.getResources().getColor(R.color.comapny_name_color));
                        holder.agents_count_tv.setTextColor(mContext.getResources().getColor(R.color.visitorNameColor));

                       // holder.statusLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        holder.status1.setBackground(mContext.getResources().getDrawable(R.drawable.offline));
                    }




                if (activeChatArrayList != null && activeChatArrayList.size() > 0) {
                    //  Helper.getInstance().LogDetails("VisitorCompanyAdapter" ,"size "+activeChatArrayList.size()+" "+position);
                    holder.line.setVisibility(View.GONE);
                    holder.no_chats_image.setVisibility(View.GONE);
                    holder.activie_chat_recycler_view.setVisibility(View.VISIBLE);
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    // holder.activie_chat_recycler_view.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));


          /* Collections.sort(activeChatArrayList, new Comparator<ActiveChat>() {
                 public int compare(ActiveChat o1, ActiveChat o2) {
                     try {

                         return getDate(o2.getChatModel().getCreated_at()).compareTo(getDate(o1.getChatModel().getCreated_at()));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     return 0;
                 }
             });*/

                    layoutManager3.setAutoMeasureEnabled(true);
                    holder.activie_chat_recycler_view.setLayoutManager(layoutManager3);
                    VisitorUserAdapter activeChatsAdapter = new VisitorUserAdapter(mContext, activeChatArrayList, true,homeFragment);
                    activeChatsAdapter.selectionMode = true;
                    activeChatsAdapter.agentSiteId(site_ids);
                    activeChatsAdapter.setSiteInfo(sitesInfo);



                    holder.activie_chat_recycler_view.setNestedScrollingEnabled(true);
                    holder.activie_chat_recycler_view.setAdapter(activeChatsAdapter);
                    float h = mContext.getResources().getDimension(R.dimen.active_chat_list_height);
                    if (activeChatArrayList.size() > 4) {
                        holder.activie_chat_recycler_view.getLayoutParams().height = (int) h;
                    } else {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        holder.activie_chat_recycler_view.setLayoutParams(lparams);
                    }
                } else {
                    // Helper.getInstance().LogDetails("VisitorCompanyAdapter" ,"size empty"+" "+position);
                    holder.line.setVisibility(View.GONE);
                    holder.no_chats_image.setVisibility(View.GONE);
                    holder.activie_chat_recycler_view.setVisibility(View.GONE);
                }


                List<ActiveAgent> allAgentList = new ArrayList<>();
                allAgentList = sitesInfo.getAllAgents();
                int agentsCount=sitesInfo.getActiveAgentsCount();
                if (agentsCount>0) {
                    holder.agents_count_tv.setVisibility(View.VISIBLE);
                    holder.agents_active_count_tv.setVisibility(View.GONE);
                    if (allAgentList.size() == 1) {
                        holder.agents_count_tv.setText(agentsCount + " Agent");
                    } else {
                        holder.agents_count_tv.setText(agentsCount+ " Agents");
                    }

                    holder.agents_active_count_tv.setText("  (" +agentsCount + ")");
                   // holder.agents_active_count_tv.setText(allAgentList.size()+"" );

                } else {
                    holder.agents_count_tv.setVisibility(View.VISIBLE);
                    holder.agents_active_count_tv.setVisibility(View.GONE);
                    holder.agents_count_tv.setText("0 Agents");
                    holder.agents_active_count_tv.setText("");
                }

                Helper.getInstance().LogDetails("AgentCount ",allAgentList.size()+" "+agentsCount);

                List<ActiveAgent> activeAgentList = new ArrayList<>();
                activeAgentList = sitesInfo.getActiveAgents();

                if (activeAgentList != null && activeAgentList.size() > 0) {

                    holder.activie_agents_recycler_view.setVisibility(View.VISIBLE);
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    layoutManager3.setAutoMeasureEnabled(true);
                    holder.activie_agents_recycler_view.setLayoutManager(layoutManager3);
                    ActiveAgentsAdapter activeAgentsAdapter = new ActiveAgentsAdapter(mContext, activeAgentList);
                    activeAgentsAdapter.selectionMode = true;
                    holder.activie_agents_recycler_view.setNestedScrollingEnabled(true);
                    holder.activie_agents_recycler_view.setAdapter(activeAgentsAdapter);

                } else {

                    holder.activie_agents_recycler_view.setVisibility(View.GONE);

                }


                final List<ActiveChat> finalActiveChatArrayList = activeChatArrayList;
                holder.activie_chat_recycler_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                        int action = motionEvent.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_MOVE:
                                if (finalActiveChatArrayList != null && finalActiveChatArrayList.size() > 4) {
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

                    if (role != null && !role.isEmpty()) {
                       if(Integer.parseInt(role)!=Values.UserRoles.MODERATOR)
                       {
                           if (isSelf) {
                               holder.status1.setVisibility(View.VISIBLE);
                           } else {
                               holder.status1.setVisibility(View.VISIBLE);
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

                        holder.status1.setVisibility(View.GONE);
                        holder.progressButton.enableLoadingState();
                        holder.progressButton.setVisibility(View.VISIBLE);
                        if(isSelf) {


                            if (mContext instanceof HomeActivity) {


                                if (Utilities.getConnectivityStatus(mContext) > 0) {
                                    if(BasicActivity.mSocket!=null && !BasicActivity.mSocket.connected())
                                    {
                                        BasicActivity.mSocket.connect();
                                    }
                                    if(BasicActivity.mSocket!=null && BasicActivity.mSocket.connected())
                                    {
                                        int status=0;

                                        if (sitesInfo.getOnline_status() == 0) {
                                           // sitesInfo.setOnline_status(1);
                                            status=1;
                                        } else {
                                           // sitesInfo.setOnline_status(0);
                                            status=0;
                                        }


                                        ((HomeActivity) mContext).EmitAgentStatusUpdated(status+"", sitesInfo.getSiteToken(), sitesInfo.getSiteId(), sitesInfo.getAccountId());
                                        //notifyDataSetChanged();
                                    }

                                } else  {
                                    Toast.makeText(mContext, "Please check internet connection", Toast.LENGTH_LONG).show();

                                }

                            }
                        }
                        holder.status1.setVisibility(View.VISIBLE);
                        holder.progressButton.disableLoadingState();
                        holder.progressButton.setVisibility(View.GONE);
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

    private void sortList(List<ActiveChat> activeChatArrayList) {
        Collections.sort(activeChatArrayList, new Comparator<ActiveChat>() {
            public int compare(ActiveChat o1, ActiveChat o2) {
                try {

                    return getDate(o1.getChatModel().getCreated_at()).compareTo(getDate(o2.getChatModel().getCreated_at()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private Date getDate(String date) throws ParseException {
        try{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.getTime();
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
    public void agentSiteId(String site_ids){
        this.site_ids=site_ids;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.company_name_tv)
        TextView company_name_tv;

        @BindView(R.id.agents_count_tv)
        TextView agents_count_tv;

        @BindView(R.id.agents_active_count_tv)
        TextView agents_active_count_tv;

        @BindView(R.id.status)
        SwitchCompat status;

        @BindView(R.id.status1)
        ProgressButton status1;

        @BindView(R.id.progressButton)
        ProgressButton progressButton;

        @BindView(R.id.activie_chat_recycler_view)
        RecyclerView activie_chat_recycler_view;

        @BindView(R.id.activie_agents_recycler_view)
        RecyclerView activie_agents_recycler_view;

        @BindView(R.id.no_chats_image)
        ImageView no_chats_image;

        @BindView(R.id.statusLayout)
        LinearLayout statusLayout;

        @BindView(R.id.itemLayout)
        LinearLayout itemLayout;

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