package com.shahenatuserapp.User.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shahenatuserapp.R;
import com.shahenatuserapp.User.CancelActivity;
import com.shahenatuserapp.User.HomeActiivity;
import com.shahenatuserapp.databinding.FragmentBookingCancelBinding;


public class BottomBookingCancelFragment extends BottomSheetDialogFragment {


    Context context;

    FragmentBookingCancelBinding binding;

    public BottomBookingCancelFragment(Context context) {
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupDialog(Dialog dialog, int style) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_booking_cancel, null, false);

        binding.RRCacel.setOnClickListener(v -> {

            startActivity(new Intent(getActivity(), CancelActivity.class));

        });

        dialog.setContentView(binding.getRoot());

    }
}