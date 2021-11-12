package com.shahenat.User.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shahenat.User.model.ScheduleRide;
import com.shahenat.R;

import java.util.ArrayList;


public class AvalibilityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<ScheduleRide.Result> modelList;
    private OnItemClickListener mItemClickListener;


    public AvalibilityAdapter(Context context, ArrayList<ScheduleRide.Result> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<ScheduleRide.Result> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itme_category_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final ScheduleRide.Result model = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.txtName.setText(model.getEquipmentName());
            genericViewHolder.txtPrice.setText(model.getPriceKm());
            genericViewHolder.txtDescription.setText(model.getDescription());

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

    private ScheduleRide.Result getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, ScheduleRide.Result model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtPrice;
        TextView txtDescription;
        ImageView Vechl_img;

        public ViewHolder(final View itemView) {
            super(itemView);

          this.txtName=itemView.findViewById(R.id.txtName);
          this.txtPrice=itemView.findViewById(R.id.txtPrice);
          this.txtDescription=itemView.findViewById(R.id.txtDescription);
          this.Vechl_img=itemView.findViewById(R.id.Vechl_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });
        }
    }


}

