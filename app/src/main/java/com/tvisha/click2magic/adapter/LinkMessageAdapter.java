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
import com.tvisha.click2magic.api.post.model.Link;
import com.tvisha.click2magic.ui.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class LinkMessageAdapter extends RecyclerView.Adapter<LinkMessageAdapter.ViewHolder> {
    public static boolean selectionMode = true;
    public boolean today = false;
    String dateFormat = "dd MMM yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
    String user_name = "";
    long mLastClickTime = 0;
    private Context mContext;
    private List<Link> data = new ArrayList<>();
    private List<Link> list = new ArrayList<>();


    public LinkMessageAdapter(Context context, List<Link> data) {
        mContext = context;
        this.list = data;
        this.data.addAll(data);
        user_name = Session.getUserName(context);
    }

    @NonNull
    @Override
    public LinkMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.link_message_row_design, parent, false);
        return new ViewHolder(v);
    }

    public void setAgentsList(List<Link> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final LinkMessageAdapter.ViewHolder holder, final int position) {
        try {
            if (data != null && position < data.size()) {

                if (position == data.size() - 1) {
                    holder.line.setVisibility(View.GONE);
                } else {
                    holder.line.setVisibility(View.VISIBLE);
                }

                final Link link = data.get(position);
                if (link != null) {
                    holder.message_tv.setText(link.getPath());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof ChatActivity) {
                            ((ChatActivity) mContext).sendLinkMessage(link.getPath());
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

    private Link getItem(int position) {
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
                if (list.get(i).getTitle() != null && Helper.getInstance().stringMatchWithFirstWordofString(filtetText.trim(), list.get(i).getTitle().trim())) {
                    addItemToSearchList(list.get(i));
                }

            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetList(List<Link> links) {
        Helper.getInstance().LogDetails("searchMethod", "resetList called " + data.size() + " " + list.size());
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }

        List<Link> temp = new ArrayList<>();
        temp.addAll(links);

        if (temp != null && temp.size() > 0) {
            data.addAll(temp);
            Helper.getInstance().LogDetails("searchMethod", "resetList end " + data.size() + " " + list.size());
            notifyDataSetChanged();
        }
    }

    private void addItemToSearchList(Link link) {
        try{
        if (data != null) {
            if (data.size() == 0) {

                data.add(link);
                Helper.getInstance().LogDetails("searchMethod addItemToSearchList", " end " + data.size());

                notifyDataSetChanged();
            } else {
                if (data.size() > 0) {
                    boolean isPresent = false;

                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getAssetId() != null && link.getAssetId() != null && data.get(i).getAssetId().equals(link.getAssetId())) {
                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent) {
                        data.add(link);
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

        @BindView(R.id.message_tv)
        TextView message_tv;

        @BindView(R.id.line)
        View line;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }


    }
}