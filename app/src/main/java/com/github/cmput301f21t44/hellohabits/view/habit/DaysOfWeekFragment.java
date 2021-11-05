package com.github.cmput301f21t44.hellohabits.view.habit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.github.cmput301f21t44.hellohabits.R;

public class DaysOfWeekFragment extends DialogFragment {

    private boolean[] mSelectedItems;
    OnConfirmCallback mConfirmCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Reminders");

        builder.setMultiChoiceItems(R.array.days_in_week, mSelectedItems,
                (dialog, which, isChecked) -> {
                    if (isChecked) {
                        mSelectedItems[which] = true;
                    } else if (mSelectedItems[which]) {
                        mSelectedItems[which] = false;
                    }
                });

        builder.setPositiveButton("OK", (dialog, which) ->
                mConfirmCallback.onConfirm(mSelectedItems));

        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }

    public static DaysOfWeekFragment newInstance(boolean[] days, OnConfirmCallback callback) {
        DaysOfWeekFragment newFragment = new DaysOfWeekFragment();
        newFragment.mSelectedItems = days;
        newFragment.mConfirmCallback = callback;
        return newFragment;
    }

    public interface OnConfirmCallback {
        void onConfirm(boolean[] days);
    }
}
