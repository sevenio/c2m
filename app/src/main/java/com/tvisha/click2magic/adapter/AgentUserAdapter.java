package com.tvisha.click2magic.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvisha.click2magic.Helper.Helper;
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



public class AgentUserAdapter extends RecyclerView.Adapter<AgentUserAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    String loginTmUserId="",loginUserRole="";
    private List<ActiveAgent> data = new ArrayList<>();
    AgentsFragment agentsFragment;
    SitesInfo sitesInfo=new SitesInfo();

    public AgentUserAdapter(Context context, List<ActiveAgent> data, boolean isSelf,AgentsFragment agentsFragment) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;
        this.agentsFragment=agentsFragment;
        loginTmUserId =  Session.getTmUserId(context);
        loginUserRole =  Session.getUserRole(context);
    }

    @NonNull
    @Override
    public AgentUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.agent_user_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<ActiveAgent> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }
    public void setSitesInfo(SitesInfo sitesInfo){
        this.sitesInfo=sitesInfo;
    }

    public void setIsSelfStatus(boolean isSelf) {
        this.isSelf = isSelf;
    }

    @Override
    public void onBindViewHolder(@NonNull final AgentUserAdapter.ViewHolder holder, final int position) {

        try {
            if (data != null && position < data.size()) {

                if(position==0)
                {
                    if(sitesInfo.getOnline_status()==1)
                    {
                        holder.line.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.line.setVisibility(View.GONE);
                    }
                }
                else
                {
                    holder.line.setVisibility(View.VISIBLE);
                }




                final ActiveAgent activeUserChatData = data.get(position);
                if(activeUserChatData.getIsOnline().equals("1"))
                {
                    holder.online_status.setText("Online");
                }
                else
                {
                    holder.online_status.setText("Offline");
                }

                if (activeUserChatData != null) {

                    if (activeUserChatData.getUserName() != null) {

                        if(activeUserChatData.getTmUserId()!=null && loginTmUserId!=null && activeUserChatData.getTmUserId().equals(loginTmUserId))
                        {
                            holder.user_name_tv.setText("Self");
                        }
                        else
                        {

                            if (activeUserChatData.getUserName().length() > 40) {
                                holder.user_name_tv.setText(Helper.getInstance().capitalize(activeUserChatData.getUserName().substring(0, 40) + "..."));
                            } else {
                                holder.user_name_tv.setText(Helper.getInstance().capitalize(activeUserChatData.getUserName()));
                            }
                        }


                    } else {
                        holder.user_name_tv.setText("");
                    }

                    if (!activeUserChatData.getActive_chat_count().equals("0")) {

                        holder.active_user_count_tv.setVisibility(View.VISIBLE);
                        holder.active_user_count_tv.setText(activeUserChatData.getActive_chat_count());
                    }
                    else
                    {
                        holder.active_user_count_tv.setVisibility(View.INVISIBLE);
                        holder.active_user_count_tv.setText("");
                    }


                    if (loginUserRole != null && !loginUserRole.isEmpty()) {
                        if(Integer.parseInt(loginUserRole)== Values.UserRoles.ADMIN ||  Integer.parseInt(loginUserRole)==Values.UserRoles.SUPERVISOR)
                        {
                          holder.menuImage.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.menuImage.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        holder.menuImage.setVisibility(View.INVISIBLE);
                    }

                    final String role = activeUserChatData.getRole();
                    if (role != null && !role.isEmpty()) {
                        switch (Integer.parseInt(role)) {
                            case Values.UserRoles.ADMIN:
                                holder.supervisor_tv.setText("ADMIN");
                                holder.role_tv.setText("");
                                holder.role_tv.setVisibility(View.GONE);

                                break;
                            case Values.UserRoles.SUPERVISOR:
                                holder.supervisor_tv.setText("SUPERVISOR");
                                holder.role_tv.setText("S");
                                holder.role_tv.setVisibility(View.VISIBLE);

                                break;
                            case Values.UserRoles.MODERATOR:
                                holder.supervisor_tv.setText("MODERATOR");
                                holder.role_tv.setText("M");
                                holder.role_tv.setVisibility(View.VISIBLE);

                                break;
                            case Values.UserRoles.AGENT:
                                holder.supervisor_tv.setText("AGENT");
                                holder.role_tv.setText("A");
                                holder.role_tv.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    holder.menuImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
                            PopupMenu popup = new PopupMenu(wrapper, view);
                            //PopupMenu popup = new PopupMenu(mContext, holder.menuImage);


                            popup.getMenuInflater()
                                    .inflate(R.menu.status_menu, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {

                                    agentsFragment.showConfirmDialog(activeUserChatData.getAgentId(),position,activeUserChatData.getUser_site_id(),sitesInfo.getSiteToken());
                                    return true;
                                }
                            });

                            popup.show();
                        }
                    });


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                                if(Integer.parseInt(loginUserRole)!=Values.UserRoles.AGENT)
                                {
                                    if(loginUserRole!=null && !loginUserRole.trim().isEmpty())
                                    {

                                            if(mContext instanceof HomeActivity){
                                                if (role != null && !role.isEmpty()) {
                                                    if(Integer.parseInt(role)!=Values.UserRoles.ADMIN)
                                                    {
                                                        if(Utilities.getConnectivityStatus(mContext)>0)
                                                        {
                                                            if(mContext instanceof HomeActivity)
                                                            {
                                                                ((HomeActivity) mContext).showAgentView(data.get(position),true);
                                                            }



                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(mContext,"Please check internet connection",Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                    else
                                                    {
                                                        if(data.get(position).getTmUserId()!=null && loginTmUserId!=null && data.get(position).getTmUserId().equals(loginTmUserId))
                                                        {
                                                            if(Utilities.getConnectivityStatus(mContext)>0)
                                                            {
                                                                if(mContext instanceof  HomeActivity)
                                                                {
                                                                    ((HomeActivity) mContext).showAgentView(data.get(position),true);
                                                                }

                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(mContext,"Please check internet connection",Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                    }

                            }
                             else
                                {
                                    if(data.get(position).getTmUserId()!=null && loginTmUserId!=null && data.get(position).getTmUserId().equals(loginTmUserId))
                                    {
                                        if(Utilities.getConnectivityStatus(mContext)>0)
                                        {
                                            if(mContext instanceof  HomeActivity)
                                            {
                                                ((HomeActivity) mContext).showAgentView(data.get(position),true);
                                            }

                                        }
                                        else
                                        {
                                            Toast.makeText(mContext,"Please check internet connection",Toast.LENGTH_LONG).show();
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

    private ActiveAgent getItem(int position) {
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

        @BindView(R.id.email_tv)
        TextView email_tv;

        @BindView(R.id.visit_count_tv)
        TextView visit_count_tv;

        @BindView(R.id.user_name_tv)
        TextView user_name_tv;

        @BindView(R.id.active_user_count_tv)
        TextView active_user_count_tv;

        @BindView(R.id.supervisor_tv)
        TextView supervisor_tv;

        @BindView(R.id.role_tv)
        TextView role_tv;

        @BindView(R.id.online_status)
        TextView online_status;

        @BindView(R.id.line)
        View line;

        @BindView(R.id.menuImage)
        ImageView menuImage;

        @BindView(R.id.unread_rl_view)
        RelativeLayout unread_rl_view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            /*if (SystemClock.elapsedRealtime() - mLastClickTime < 100) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();*/


        }
    }

}