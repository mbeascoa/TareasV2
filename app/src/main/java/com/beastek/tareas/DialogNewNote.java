package com.beastek.tareas;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.*;
import static java.time.DayOfWeek.*;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;


public class DialogNewNote extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private String CombinationText;
    private boolean setDateButtonClickedOnce = false;
    private boolean setTimeButtonClickedOnce = false;
    private String mUserEnteredTittle;
    private String mUserEnteredDescription;
    private boolean mUserHasReminder;
    private Date mUserReminderDate;
    private LocalDate Today, mUserReminderDateLD;


    private static final String TAG = "Dialog New Note";
    private Date mLastEdited;

    private EditText mToDoTextBodyTitle, mToDoTextBodyDescription;

    private SwitchCompat mToDoDateSwitch;
    //    private TextView mLastSeenTextView;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private TextView mReminderTextView, reminderRemindMeTextView;

    private CheckBox checkBoxIdea, checkBoxTodo, checkBoxImportant;
    private EditText mDateEditText, mTimeEditText;
    private String mDefaultTimeOptions12H[], mDefaultTimeOptions24H[];
    private ImageButton reminderIconImageButton;
    private Button btnCancel, btnOk, mChooseDateButton, mChooseTimeButton, mCopyClipboard;

    private Note mUserToDoItem;
    private FloatingActionButton mToDoSendFloatingActionButton;
    public static final String DATE_FORMAT = "MMM d, yyyy";
    public static final String DATE_FORMAT_MONTH_DAY = "MMM d";
    public static final String DATE_FORMAT_TIME = "H:m";

    private LinearLayout mContainerLayout;
    private ImageView circle;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_new_note, null);
        mToDoTextBodyTitle = (EditText) dialogView.findViewById(R.id.userToDoEditTextTitle);
        mToDoTextBodyDescription = (EditText) dialogView.findViewById(R.id.userToDoDescription);


        reminderIconImageButton = (ImageButton) dialogView.findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView) dialogView.findViewById(R.id.userToDoRemindMeTextView);
        //this container is everything excepts title and desription....
        mContainerLayout = (LinearLayout) dialogView.findViewById(R.id.todoReminderAndDateContainerLayout);

        //this layout is the one that appears when Switch is clicked, then you can fill date and time
        mUserDateSpinnerContainingLinearLayout = (LinearLayout) dialogView.findViewById(R.id.toDoEnterDateLinearLayout);

        mToDoTextBodyTitle = (EditText) dialogView.findViewById(R.id.userToDoEditTextTitle);
        mToDoTextBodyDescription = (EditText) dialogView.findViewById(R.id.userToDoDescription);
        mToDoDateSwitch = (SwitchCompat) dialogView.findViewById(R.id.toDoHasDateSwitchCompat);

        mToDoSendFloatingActionButton = (FloatingActionButton) dialogView.findViewById(R.id.makeToDoFloatingActionButton);
        //filed where we show the reminder detail (date and time), final part of the screen
        mReminderTextView = (TextView) dialogView.findViewById(R.id.newToDoDateTimeReminderTextView);

        //Button for Copy to Clipboard
        mCopyClipboard = (Button) dialogView.findViewById(R.id.copyclipboard);

        //OnClickListener for CopyClipboard Button
        mCopyClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toDoTextContainer = mToDoTextBodyTitle.getText().toString();
                String toDoTextBodyDescriptionContainer = mToDoTextBodyDescription.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                CombinationText = "Title : " + toDoTextContainer + "\nDescription : " + toDoTextBodyDescriptionContainer + "\n -Copied From Entities Online";
                ClipData clip = ClipData.newPlainText("text", CombinationText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied To Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        checkBoxIdea = (CheckBox) dialogView.findViewById(R.id.checkBoxIdea);
        checkBoxTodo = (CheckBox) dialogView.findViewById(R.id.checkBoxTodo);
        checkBoxImportant = (CheckBox) dialogView.findViewById(R.id.checkBoxImportant);

        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        circle = (ImageView) dialogView.findViewById(R.id.circle_image);


        builder.setView(dialogView)
                .setMessage("Add a new note ");
        //si quisiéramos introducir una cabecera o mensaje pero quita espacio al dialog

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Please try again, whenever you consider, Note will not be created!!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creamos una nota vacía
                Note newNote = new Note();

                if (mToDoTextBodyTitle.length() <= 0) {
                    mToDoTextBodyTitle.setError(getString(R.string.todo_error));
                } else {

                    //Configuramos las 7 variables de la nota creada
                    newNote.setTitle(mToDoTextBodyTitle.getText().toString());
                    newNote.setDescription(mToDoTextBodyDescription.getText().toString());

                    newNote.setIdea(checkBoxIdea.isChecked());
                    newNote.setTodo(checkBoxTodo.isChecked());
                    newNote.setImportant(checkBoxImportant.isChecked());

                    int color = ColorGenerator.MATERIAL.getRandomColor();
                    newNote.setTodoColor(color);

                    if (mToDoDateSwitch.isChecked() && (newNote.getToDoDate() != null)) {
                        newNote.setReminder(true);
                    } else {
                        newNote.setReminder(false);
                    }

                    //I am casting to MainActivity who is the one thal called the dialog; Hago un casting a Main Activity, que es quien ha llamado al diálogo
                    MainActivityNote callingActivity = (MainActivityNote) getActivity();
                    //We will notify the Main Activity that we have created a new Note ; Notificaremos a la MA que hemos creado una nueva nota
                    callingActivity.createNewNote(newNote);
                    //This command close the dialog, Esto cierra el diálogo
                    dismiss();
                }
            }
        });

        //when clicking in the area that does not contain title and description then hides the keyboard
        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mToDoTextBodyTitle);
                hideKeyboard(mToDoTextBodyDescription);
            }
        });

        if (mUserHasReminder && (mUserReminderDate != null)) {
//            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
            setReminderTextView();
            setEnterDateLayoutVisibleWithAnimations(true);
        }
        if (mUserReminderDate == null) {
            mToDoDateSwitch.setChecked(false);
            mReminderTextView.setVisibility(View.INVISIBLE);
        }

        mToDoTextBodyTitle.requestFocus();
        mToDoTextBodyTitle.setText(mUserEnteredTittle);
        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mToDoTextBodyTitle.setSelection(mToDoTextBodyTitle.length());


        mToDoTextBodyTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredTittle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        mToDoTextBodyDescription.setSelection(mToDoTextBodyDescription.length());
        mToDoTextBodyDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // if the Switch is checked then we can edit the dates
        setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

        mToDoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));
        mToDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    mUserReminderDate = null;
                }
                mUserHasReminder = isChecked;
                setDateAndTimeEditText();
                setEnterDateLayoutVisibleWithAnimations(isChecked);
                hideKeyboard(mToDoTextBodyTitle);
                hideKeyboard(mToDoTextBodyDescription);
            }
        });


        mToDoSendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creamos una nota vacía
                Note newNote = new Note();

                if (mToDoTextBodyTitle.length() <= 0) {
                    mToDoTextBodyTitle.setError(getString(R.string.todo_error));
                } else {

                    //Configuramos las 7 variables de la nota creada
                    newNote.setTitle(mToDoTextBodyTitle.getText().toString());
                    newNote.setDescription(mToDoTextBodyDescription.getText().toString());

                    newNote.setIdea(checkBoxIdea.isChecked());
                    newNote.setTodo(checkBoxTodo.isChecked());
                    newNote.setImportant(checkBoxImportant.isChecked());
                    int color = ColorGenerator.MATERIAL.getRandomColor();
                    newNote.setTodoColor(color);

                    if (mToDoDateSwitch.isChecked() && (newNote.getToDoDate() != null)) {
                        newNote.setReminder(true);
                    } else {
                        newNote.setReminder(false);
                    }

                    //I am casting to MainActivity who is the one thal called the dialog; Hago un casting a Main Activity, que es quien ha llamado al diálogo
                    MainActivityNote callingActivity = (MainActivityNote) getActivity();
                    //We will notify the Main Activity that we have created a new Note ; Notificaremos a la MA que hemos creado una nueva nota
                    callingActivity.createNewNote(newNote);
                    //This command close the dialog, Esto cierra el diálogo
                    dismiss();
                }
            }
        });


        // filed that places the Date as string, from mUserReminderDate
        mDateEditText = (EditText) dialogView.findViewById(R.id.newTodoDateEditText);

        //Listener to Date picker
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(mToDoTextBodyTitle);
                hideKeyboard(mToDoTextBodyDescription);

                date = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DialogNewNote.this, year, month, day);
                datePickerDialog.show(getActivity().getFragmentManager(), "DateFragment");
            }
        });

        //field where we put the time from mUserReminderDate
        mTimeEditText = (EditText) dialogView.findViewById(R.id.newTodoTimeEditText);

        // listener to Edit Time
        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(mToDoTextBodyTitle);
                hideKeyboard(mToDoTextBodyDescription);
                date = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(DialogNewNote.this, hour, minute, DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show(getActivity().getFragmentManager(), "TimeFragment");
            }
        });

        // settle Date and Time,
        setDateAndTimeEditText();

        //Una vez configurada nuestro dialogo, le indicamos al builder que debe crearla en pantalla...
        return builder.create();

    }

    // Saves Data and Time Edit Text
    private void setDateAndTimeEditText() {
        mDateEditText.setText(getString(R.string.date_reminder_default));
//            mUserReminderDate = new Date();
        boolean time24 = DateFormat.is24HourFormat(getContext());
        Calendar cal = Calendar.getInstance();
        if (time24) {
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        } else {
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
        }
        cal.set(Calendar.MINUTE, 0);
        mUserReminderDate = cal.getTime();
        Log.d("Reminder Date", "Imagined Date: " + mUserReminderDate);
        String timeString;
        if (time24) {
            timeString = formatDate("k:mm", mUserReminderDate);
        } else {
            timeString = formatDate("h:mm a", mUserReminderDate);
        }
        mTimeEditText.setText(timeString);
    }


    public void hideKeyboard(EditText et) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;
//        int currentYear = calendar.get(Calendar.YEAR);
//        int currentMonth = calendar.get(Calendar.MONTH);
//        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            Toast.makeText(getContext(), "We only add dates from now on. Not in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        if (DateFormat.is24HourFormat(getContext())) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {

            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
//        setDateAndTimeEditText();
        setDateEditText();
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

//        if(DateFormat.is24HourFormat(this) && hour == 0){
//            //done for 24h time
//                hour = 24;
//        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("Miguel Beascoa", "Time set: " + hour);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();

        setReminderTextView();
//        setDateAndTimeEditText();
        setTimeEditText();
    }

    public void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
        mDateEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(getContext())) {
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";

        }
        mTimeEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setReminderTextView() {
        if (mUserReminderDate != null) {
            mReminderTextView.setVisibility(View.VISIBLE);
            if (mUserReminderDate.before(new Date())) {
                Log.d("SET REMINDER", "DATE is " + mUserReminderDate);
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = mUserReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPmString = "";

            if (DateFormat.is24HourFormat(getContext())) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        } else {
            mReminderTextView.setVisibility(View.INVISIBLE);

        }
    }


    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        setTime(hour, minute);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        setDate(year, month, day);
    }

    public void setEnterDateLayoutVisible(boolean checked) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
        if (checked) {
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        } else {
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        }

    }
}
