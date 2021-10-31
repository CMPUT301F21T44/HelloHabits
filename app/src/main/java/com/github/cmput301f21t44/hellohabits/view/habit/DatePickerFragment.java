package com.github.cmput301f21t44.hellohabits.view.habit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue() - 1;
        int day = dateTime.getDayOfMonth();

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
