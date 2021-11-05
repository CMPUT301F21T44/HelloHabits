package com.github.cmput301f21t44.hellohabits.view.habit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Dialog Fragment for picking the date for start of Habit
 */
public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * Returns a new instance of the DatePickerFragment
     *
     * @param listener Callback for when the date is set
     * @return a new DatePickerFragment instance
     */
    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.mDateSetListener = listener;
        return fragment;
    }

    /**
     * Initializes the values for the DatePicker
     *
     * @param savedInstanceState a default Bundle
     * @return DatePickerDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue() - 1;
        int day = dateTime.getDayOfMonth();

        return new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
    }
}
