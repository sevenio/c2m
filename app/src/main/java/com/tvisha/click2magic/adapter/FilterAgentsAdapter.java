package com.tvisha.click2magic.adapter;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.AgentInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FilterAgentsAdapter extends RecyclerView.Adapter<FilterAgentsAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String user_name = "";
    long mLastClickTime = 0;

    private Context mContext;
    private List<AgentInfo> data = new ArrayList<>();
    private List<AgentInfo> list = new ArrayList<>();


    public FilterAgentsAdapter(Context context, List<AgentInfo> data) {
        mContext = context;
        this.data = data;
        this.list = data;

        user_name =  Session.getUserName(context);
    }

    @NonNull
    @Override
    public FilterAgentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.filter_agent_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<AgentInfo> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterAgentsAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {


                AgentInfo activeAgent = data.get(position);
                if (activeAgent != null) {

                    if (activeAgent != null) {
                        if (position < 2) {
                            holder.agent_name_tv.setVisibility(View.VISIBLE);
                            if (position == 0) {
                                if(data.size()==1)
                                {
                                    holder.agent_name_tv.setText(Helper.getInstance().capitalize(activeAgent.getUserName()) );
                                }
                                else
                                {
                                    holder.agent_name_tv.setText(Helper.getInstance().capitalize(activeAgent.getUserName())+",");
                                }

                            } else {
                                holder.agent_name_tv.setText(Helper.getInstance().capitalize(activeAgent.getUserName()) );
                            }
                        } else if (position == 2) {
                            holder.agent_name_tv.setVisibility(View.VISIBLE);
                            int remain = getItemCount() - 2;
                            holder.agent_name_tv.setText("+" + remain);
                        } else {
                            holder.agent_name_tv.setVisibility(View.GONE);
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
            return data.size();
        } else {
            return 0;
        }

    }

    private AgentInfo getItem(int position) {
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

        @BindView(R.id.agent_name_tv)
        TextView agent_name_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }


    }
}