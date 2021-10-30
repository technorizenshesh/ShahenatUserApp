package com.shahenatuserapp.User.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.model.CategoryModel;
import com.shahenatuserapp.User.model.GetPriceModelData;
import com.shahenatuserapp.User.model.NearestDriverModel;

import java.util.ArrayList;


public class NearByAvaiableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<GetPriceModelData> modelList;
    private OnItemClickListener mItemClickListener;

    int pos=0;

    public NearByAvaiableAdapter(Context context, ArrayList<GetPriceModelData> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<GetPriceModelData> modelList) {
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
            final GetPriceModelData model = getItem(position);
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

            genericViewHolder.name.setText(model.getEquipmentName());
            genericViewHolder.txtPrice.setText(model.getPriceKm());
            genericViewHolder.txtEstimateTime.setText(model.getEstimateTime()+" Min");

            if(model.getVehicleImage()!=null)
            {
                Glide.with(mContext).load(model.getVehicleImage()).placeholder(R.drawable.buldozer)
                    .into(genericViewHolder.Vechl_img);
            }

        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private GetPriceModelData getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, GetPriceModelData model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llPard;
        ImageView Vechl_img;
        TextView name;
        TextView txtPrice;
        TextView txtEstimateTime;

        public ViewHolder(final View itemView) {
            super(itemView);

          this.llPard=itemView.findViewById(R.id.llPard);
          this.Vechl_img=itemView.findViewById(R.id.Vechl_img);
          this.name=itemView.findViewById(R.id.name);
          this.txtPrice=itemView.findViewById(R.id.txtPrice);
          this.txtEstimateTime=itemView.findViewById(R.id.txtEstimateTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });
        }
    }


}

