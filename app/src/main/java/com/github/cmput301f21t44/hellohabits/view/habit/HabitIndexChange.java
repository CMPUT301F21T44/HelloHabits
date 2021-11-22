package com.github.cmput301f21t44.hellohabits.view.habit;

import android.view.View;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;

/**
 * POJO for holding data on a change in Habit index
 */
public class HabitIndexChange {
    public static int HABIT_ID_TAG = R.id.habit_id;
    public static int HABIT_INDEX_TAG = R.id.priority;
    private final int oldIndex;
    private final int newIndex;
    private final String habitId;

    public HabitIndexChange(String habitId, int oldIndex, int newIndex) {
        this.habitId = habitId;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public static HabitIndexChange fromView(View view, int layoutIndex) {
        String habitId = (String) view.getTag(HABIT_ID_TAG);
        int oldIndex = (int) view.getTag(HABIT_INDEX_TAG);
        return new HabitIndexChange(habitId, oldIndex, layoutIndex);
    }

    public static void setTags(View view, Habit habit) {
        view.setTag(HABIT_INDEX_TAG, habit.getIndex());
        view.setTag(HABIT_ID_TAG, habit.getId());
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    public String getHabitId() {
        return habitId;
    }
}
