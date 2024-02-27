package com.tvisha.click2magic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Session;
import com.tvisha.click2magic.R;

import com.tvisha.click2magic.ui.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SmartRepliesMessageAdapter extends RecyclerView.Adapter<SmartRepliesMessageAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String user_name = "";
    long mLastClickTime = 0;

    private Context mContext;
    private List<String> data = new ArrayList<>();



    public SmartRepliesMessageAdapter(Context context, List<String> data) {
        mContext = context;
        this.data=data;
        user_name =  Session.getUserName(context);
    }

    @NonNull
    @Override
    public SmartRepliesMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.smart_replies_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<String> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final SmartRepliesMessageAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {


                 String message = data.get(position);
                if (message != null) {
                    if(!message.isEmpty())
                    {
                        holder.message_tv.setText(Helper.getInstance().capitalize(message));
                    }
                    else
                    {
                        holder.message_tv.setText("");
                    }

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof ChatActivity) {
                            Helper.getInstance().LogDetails("sendHistoryToFirebase adapter",message);
                            ((ChatActivity) mContext).sendSmartRepliesMessage(message);
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

    private String getItem(int position) {
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

        @BindView(R.id.message_tv)
        TextView message_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }


    }
}