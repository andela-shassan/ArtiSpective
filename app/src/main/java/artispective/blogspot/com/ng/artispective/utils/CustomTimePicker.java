package artispective.blogspot.com.ng.artispective.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Nobest on 23/06/2016.
 */
public class CustomTimePicker extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener listener =
                (TimePickerDialog.OnTimeSetListener) getActivity();

        return new TimePickerDialog(getActivity(), listener, hour, minute, false);
    }

}
