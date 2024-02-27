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
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.api.post.model.Category;

import java.util.ArrayList;

public class CategoriesFlowAdapter extends RecyclerView.Adapter<CategoriesFlowAdapter.ViewHolder> {

    private ArrayList<Category> categoryArrayList;
    private int itemPosition = -1;
    private Context context;

    public OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCartItemClick(View view, int position);
    }

    public void setListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public CategoriesFlowAdapter(ArrayList<Category> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
        itemPosition = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chip_categories_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryArrayList.get(position);

        if(category != null){
            if(category.getCategoryName().length()>15)
            {
                holder.category.setText("    "+Helper.getInstance().capitalize(category.getCategoryName().substring(0,15))+"..."+"    ");
            }
            else
            {
                holder.category.setText("    "+Helper.getInstance().capitalize(category.getCategoryName())+"    ");
            }

        }

        final int finPos = position;
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPosition = finPos;
                notifyDataSetChanged();
            }
        });

        if(itemPosition == position){

            holder.category.setBackground(context.getResources().getDrawable(R.drawable.tag_active_bg));
            holder.category.setTextColor(context.getResources().getColor(R.color.white));

            //For the first call
            if(listener != null){
                listener.onCartItemClick(holder.itemView, position);
            }
        }
        else{

            holder.category.setBackground(context.getResources().getDrawable(R.drawable.tag_inactive_bg));
            holder.category.setTextColor(context.getResources().getColor(R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.category)
        TextView category;

        @BindView(R.id.root_cat_view)
        View rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.root_cat_view:
                    if(listener != null){
                        listener.onCartItemClick(view, getAdapterPosition());
                    }
                    break;
            }
        }
    }
}
