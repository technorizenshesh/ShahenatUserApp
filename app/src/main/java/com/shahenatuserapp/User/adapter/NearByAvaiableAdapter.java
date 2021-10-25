package com.shahenatuserapp.User.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.CategoryModel;

import java.util.ArrayList;


public class NearByAvaiableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<CategoryModel> modelList;
    private OnItemClickListener mItemClickListener;

    int pos=0;

    public NearByAvaiableAdapter(Context context, ArrayList<CategoryModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<CategoryModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itme_near_by, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final CategoryModel model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;


            if(position == pos)
            {
                genericViewHolder.llPard.setBackgroundResource(R.drawable.border_yellow_new);
            }else
            {
                genericViewHolder.llPard.setBackgroundResource(R.drawable.btn_bg_gray);
            }

            genericViewHolder.llPard.setOnClickListener(v -> {

                pos = position;
                notifyDataSetChanged();

            });

             genericViewHolder.name.setText(model.getName());
            genericViewHolder.img.setImageResource(model.getImg());
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private CategoryModel getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, CategoryModel model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llPard;
        ImageView img;
        TextView name;

        public ViewHolder(final View itemView) {
            super(itemView);

          this.llPard=itemView.findViewById(R.id.llPard);
          this.img=itemView.findViewById(R.id.img);
          this.name=itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });
        }
    }


}

