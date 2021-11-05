package com.github.cmput301f21t44.hellohabits.view.habit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * This function returns a new instance of the DatePickerFragment
     *
     * @param listener a listener listening to the fragment
     * @return a new DatePickerFragment instance
     */
    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.mDateSetListener = listener;
        return fragment;
    }

    /**
     * This function creates a new dialog for the DatePicker
     *
     * @param savedInstanceState a default Bundle
     * @return a new dialog to select a date based on today's date
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue() - 1;
        int day = dateTime.getDayOfMonth();

        return new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
    }
}
