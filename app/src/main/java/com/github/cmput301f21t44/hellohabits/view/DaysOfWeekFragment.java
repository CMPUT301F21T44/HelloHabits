package com.github.cmput301f21t44.hellohabits.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.github.cmput301f21t44.hellohabits.R;

import java.util.ArrayList;
import java.util.List;

public class DaysOfWeekFragment extends DialogFragment {

    private boolean[] mSelectedItems;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSelectedItems = new boolean[getResources().getStringArray(R.array.days_in_week).length];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Reminders");

        builder.setMultiChoiceItems(R.array.days_in_week, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                String[] items = getActivity().getResources().getStringArray(R.array.days_in_week);

                if (isChecked) {
                    mSelectedItems[which] = true;
                }
                else if(mSelectedItems[which] == true){
                    mSelectedItems[which] = false;
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        return builder.create();
    }

}
