package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.ActiveChat;

import java.util.ArrayList;
import java.util.List;

public class InQueueUserAdapter extends RecyclerView.Adapter<InQueueUserAdapter.ViewHolder> {
    public static boolean selectionMode = true;

    long mLastClickTime = 0;
    boolean isSelf = true;
    private Context mContext;
    private List<ActiveChat> data = new ArrayList<>();

    public InQueueUserAdapter(Context context, List<ActiveChat> data, boolean isSelf) {
        mContext = context;
        this.data = data;
        this.isSelf = isSelf;


    }

    @NonNull
    @Override
    public InQueueUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inqueue_user_row_design, parent, false);
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
    public void onBindViewHolder(@NonNull final InQueueUserAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {

        //return data.size();

        return 5;

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView email_tv, visit_count_tv, user_name_tv, unread_count_tv;
        View line;
        RelativeLayout unread_rl_view;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            email_tv = (TextView) itemView.findViewById(R.id.email_tv);
            line = (View) itemView.findViewById(R.id.line);
            visit_count_tv = (TextView) itemView.findViewById(R.id.visit_count_tv);
            user_name_tv = (TextView) itemView.findViewById(R.id.user_name_tv);
            unread_rl_view = (RelativeLayout) itemView.findViewById(R.id.unread_rl_view);
            unread_count_tv = (TextView) itemView.findViewById(R.id.unread_count_tv);

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