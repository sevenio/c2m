package com.tvisha.click2magic.adapter;


import android.content.Context;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.SitesInfo;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import java.util.ArrayList;
import java.util.List;

public class InQueueCompanyAdapter extends RecyclerView.Adapter<InQueueCompanyAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<SitesInfo> data = new ArrayList<>();

    public InQueueCompanyAdapter(Context context, List<SitesInfo> data, boolean isSelf) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;


    }

    @NonNull
    @Override
    public InQueueCompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull final InQueueCompanyAdapter.ViewHolder holder, final int position) {


        ArrayList<ActiveChat> activeChatArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        // holder.activie_chat_recycler_view.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        holder.activie_chat_recycler_view.setLayoutManager(layoutManager3);
        holder.activie_chat_recycler_view.setNestedScrollingEnabled(false);
        InQueueUserAdapter inQueueUserAdapter = new InQueueUserAdapter(mContext, activeChatArrayList, true);
        inQueueUserAdapter.selectionMode = true;
        holder.activie_chat_recycler_view.setAdapter(inQueueUserAdapter);


    }

    @Override
    public int getItemCount() {

        return data.size();


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

        @BindView(R.id.status)
        Switch status;

        @BindView(R.id.activie_chat_recycler_view)
        RecyclerView activie_chat_recycler_view;

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