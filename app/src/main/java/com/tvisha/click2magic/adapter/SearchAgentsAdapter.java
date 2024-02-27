package com.tvisha.click2magic.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.AgentInfo;
import com.tvisha.click2magic.ui.FilterActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class SearchAgentsAdapter extends RecyclerView.Adapter<SearchAgentsAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String user_name = "";
    long mLastClickTime = 0;

    private Context mContext;
    private List<AgentInfo> data = new ArrayList<>();
    private List<AgentInfo> list = new ArrayList<>();


    public SearchAgentsAdapter(Context context, List<AgentInfo> data) {
        mContext = context;
        this.list = data;
        this.data.addAll(data);

        user_name = Session.getUserName(context);
    }

    @NonNull
    @Override
    public SearchAgentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.search_agent_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<AgentInfo> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAgentsAdapter.ViewHolder holder, final int position) {

        try {
            if (data != null && position < data.size()) {


                final AgentInfo activeAgent = data.get(position);
                if (activeAgent != null) {
                    holder.agent_name_tv.setText(Helper.getInstance().capitalize(activeAgent.getUserName()));
                    if (activeAgent.isChecked()) {
                        holder.checkbox.setChecked(true);
                    } else {
                        holder.checkbox.setChecked(false);
                    }

                    holder.checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Helper.getInstance().LogDetails("setOnClickListener", "called " + holder.checkbox.isChecked());
                            data.get(position).setChecked(holder.checkbox.isChecked());
                            activeAgent.setChecked(holder.checkbox.isChecked());
                            if (mContext instanceof FilterActivity) {
                                ((FilterActivity) mContext).changeStatusTemp(activeAgent);
                            }

                        }
                    });
                }
            }
        } catch (Exception e) {

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

    public void search(String filtetText) {
        try{
        if (list != null && list.size() > 0) {
            if (data != null && data.size() > 0) {
                data.clear();
                notifyDataSetChanged();
                Helper.getInstance().LogDetails("searchMethod", "called " + data.size() + " " + list.size());
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUserName() != null && Helper.getInstance().stringMatchWithFirstWordofString(filtetText.trim(), list.get(i).getUserName().trim())) {
                    addItemToSearchList(list.get(i));
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetList(List<AgentInfo> activeAgents) {
        try{
        Helper.getInstance().LogDetails("searchMethod", "resetList called " + data.size() + " " + list.size());
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }

        List<AgentInfo> temp = new ArrayList<>();
        temp.addAll(activeAgents);

        if (temp != null && temp.size() > 0) {
            data.addAll(temp);
            Helper.getInstance().LogDetails("searchMethod", "resetList end " + data.size() + " " + list.size());
            notifyDataSetChanged();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addItemToSearchList(AgentInfo activeAgent) {
        try{
        if (data != null) {
            if (data.size() == 0) {

                data.add(activeAgent);
                Helper.getInstance().LogDetails("searchMethod addItemToSearchList", " end " + data.size());

                notifyDataSetChanged();
            } else {
                if (data.size() > 0) {
                    boolean isPresent = false;

                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getUserId() != null && activeAgent.getUserId() != null && data.get(i).getUserId().equals(activeAgent.getUserId())) {
                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent) {
                        data.add(activeAgent);
                    }

                    Helper.getInstance().LogDetails("searchMethod addItemToSearchList", "changeStatus end " + data.size());

                    notifyDataSetChanged();
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.agent_name_tv)
        TextView agent_name_tv;

        @BindView(R.id.user_image)
        ImageView user_image;

        @BindView(R.id.checkbox)
        CheckBox checkbox;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }


    }
}