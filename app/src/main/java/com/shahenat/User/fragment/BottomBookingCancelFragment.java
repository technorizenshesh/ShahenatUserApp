package com.shahenat.User.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shahenat.User.CancelActivity;
import com.shahenat.R;
import com.shahenat.databinding.FragmentBookingCancelBinding;


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