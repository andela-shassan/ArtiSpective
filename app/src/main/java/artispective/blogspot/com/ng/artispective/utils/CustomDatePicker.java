package artispective.blogspot.com.ng.artispective.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nobest on 23/06/2016.
 */
public class CustomDatePicker extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        DatePickerDialog.OnDateSetListener listener =
                (DatePickerDialog.OnDateSetListener) getActivity();

        return new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

}
