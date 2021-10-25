package com.shahenatuserapp.Driver.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.CategoryModel;

import java.util.ArrayList;


public class WalletPaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<CategoryModel> modelList;
    private OnItemClickListener mItemClickListener;


    public WalletPaymentHistoryAdapter(Context context, ArrayList<CategoryModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<CategoryModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itme_payment_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final CategoryModel model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;

//            genericViewHolder.txtName.setText(model.getName());
       //     genericViewHolder.imgNew.setImageResource(model.getImg());
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

        TextView txtName;
        ImageView imgNew;

        public ViewHolder(final View itemView) {
            super(itemView);

         // this.txtName=itemView.findViewById(R.id.txtName);
        //  this.imgNew=itemView.findViewById(R.id.imgNew);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });
        }
    }


}

