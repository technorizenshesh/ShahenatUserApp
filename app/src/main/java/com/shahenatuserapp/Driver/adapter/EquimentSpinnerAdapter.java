package com.shahenatuserapp.Driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahenatuserapp.Driver.Model.EquimentModelData;
import com.shahenatuserapp.R;

import java.util.ArrayList;


public class EquimentSpinnerAdapter extends BaseAdapter {

    String[] code;
    Context context;
    String[] countryNames;
    TextView countrycode;
    int[] flags;
    ImageView icon;
    LayoutInflater inflter;
    private ArrayList<EquimentModelData> modelList;

    public EquimentSpinnerAdapter(Context applicationContext, ArrayList<EquimentModelData> modelList2) {
        this.context = applicationContext;
        this.modelList = modelList2;
        this.inflter = LayoutInflater.from(applicationContext);
    }

    public int getCount() {
        return this.modelList.size();
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = this.inflter.inflate(R.layout.spinner_layout, (ViewGroup) null);
        TextView textView = (TextView) view2.findViewById(R.id.textview);
        this.countrycode = textView;
        textView.setText(this.modelList.get(i).getName());
        return view2;
    }
}
