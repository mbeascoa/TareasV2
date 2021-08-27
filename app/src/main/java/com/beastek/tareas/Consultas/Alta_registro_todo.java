package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.beastek.tareas.Note;
import com.beastek.tareas.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class Alta_registro_todo extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private EditText editTitle, editDescription, mDateEditText, mTimeEditText;
    private Validaciones objetoValidar;  //objeto de nuestra clase validaciones
    private String CombinationText, tit, des, strCol, strDateti, strIden;   //dtsconvertido   Date to String convertido en formato "dd-MM-yyyy HH:mm:ss"
    private boolean ide, tod, imp, mUserHasReminder;
    private Date mUserReminderDate;
    private Button mCopyClipboard, btnCancel, btnOk;
    private ImageButton reminderIconImageButton;
    private TextView reminderRemindMeTextView, mReminderTextView, arroba;
    private SwitchCompat mToDoDateSwitch;
    private CheckBox checkBoxIdea, checkBoxTodo, checkBoxImportant;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_registro_todo);

        // Bundle bundle =getIntent().getExtras();  si hubiera parametros para recuperar
        //enlazo controles a la vista

        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (EditText) findViewById(R.id.editDescription);

        arroba =(TextView)findViewById(R.id.tv_arroba);
        // filed that places the Date as string, from mUserReminderDate
        mDateEditText = (EditText) findViewById(R.id.altaTodoDateET);

        //Listener to Date picker
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(editTitle);
                hideKeyboard(editDescription);

                date = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        Alta_registro_todo.this,
                        year,
                        month,
                        day);
                datePickerDialog.show(getFragmentManager(), "DateFragment");
            }
        });

        //field where we put the time from mUserReminderDate
        mTimeEditText = (EditText) findViewById(R.id.altaTodoTimeET);

        // listener to Edit Time
        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(editTitle);
                hideKeyboard(editDescription);
                date = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        Alta_registro_todo.this,
                        hour,
                        minute,
                        DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show(getFragmentManager(), "TimeFragment");
            }

        });

        // settle Date and Time,
        setDateAndTimeEditText();



        //Button for Copy to Clipboard
        mCopyClipboard = (Button) findViewById(R.id.copyclipboard);

        //OnClickListener for CopyClipboard Button
        mCopyClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toDoTextContainer = editTitle.getText().toString();
                String toDoTextBodyDescriptionContainer = editDescription.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                CombinationText = "Title : " + toDoTextContainer + "\nDescription : " + toDoTextBodyDescriptionContainer + "\n -Copied From Entities Online";
                ClipData clip = ClipData.newPlainText("text", CombinationText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Alta_registro_todo.this, "Copied To Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        reminderIconImageButton = (ImageButton) findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView) findViewById(R.id.userToDoRemindMeTextView);  // this textiew is for reminder, nothing to do with it
        mToDoDateSwitch = (SwitchCompat) findViewById(R.id.toDoHasDateSwitchCompat);

        mReminderTextView = (TextView) findViewById(R.id.altaToDoTimeReminderTV);  //TextView where we place the dinamic message with the text

        checkBoxIdea = (CheckBox) findViewById(R.id.checkBoxIdea);
        checkBoxTodo = (CheckBox) findViewById(R.id.checkBoxTodo);
        checkBoxImportant = (CheckBox) findViewById(R.id.checkBoxImportant);



        checkBoxIdea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxIdea.isChecked()) {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                } else {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                }
            }

        });

        checkBoxTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxTodo.isChecked()) {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                } else {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                }
            }

        });

        checkBoxImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxImportant.isChecked()) {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                } else {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                }
            }
        });


        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);

        objetoValidar = new Validaciones();
        mDateEditText.setVisibility(View.GONE);
        mTimeEditText.setVisibility(View.GONE);
        arroba.setVisibility(View.GONE);
        mCopyClipboard.setVisibility(View.GONE);


        mToDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                    mDateEditText.setVisibility(View.GONE);
                    mTimeEditText.setVisibility(View.GONE);
                    arroba.setVisibility(View.GONE);
                    mReminderTextView.setVisibility(View.GONE);
                    mCopyClipboard.setVisibility(View.GONE);
                    mUserReminderDate = null;
                } else {
                    mUserHasReminder = isChecked;
                    hideKeyboard(editTitle);
                    hideKeyboard(editDescription);
                    mDateEditText.setVisibility(View.VISIBLE);
                    mTimeEditText.setVisibility(View.VISIBLE);
                    arroba.setVisibility(View.VISIBLE);
                    mReminderTextView.setVisibility(View.VISIBLE);
                    mCopyClipboard.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(editTitle);
                hideKeyboard(editDescription);
                finish();
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //validating editext title and description are not empty
                if (!objetoValidar.Vacio(editTitle) && !objetoValidar.Vacio(editDescription)) {
                    //Creamos una nota vacía
                    Note newNote = new Note();

                    try {
                        //configuring the 9 variables that newNote has to be needed to complete the whole information
                        //title, description, idea, todo, important, reminder, date, color, identifier
                        tit = editTitle.getText().toString();
                        newNote.setTitle(tit);
                        des = editDescription.getText().toString();
                        newNote.setDescription(des);
                        ide = checkBoxIdea.isChecked();
                        newNote.setIdea(ide);
                        tod = checkBoxTodo.isChecked();
                        newNote.setTodo(tod);
                        imp = checkBoxImportant.isChecked();
                        newNote.setImportant(imp);
                        newNote.setReminder(mUserHasReminder);
                        // settle Date and Time gathered information from date and time picker
                        if (mUserReminderDate!= null) {
                        newNote.setToDoDate(mUserReminderDate);
                            // Se pueden definir formatos diferentes con la clase DateFormat
                            // Obtenemos la fecha y la hora con el formato yyyy-MM-dd HH:mm:ss
                        SimpleDateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        strDateti = fechaHora.format(mUserReminderDate);
                        System.out.println(strDateti);
                        //mensajePersonalizado (strDateti);

                       } else {
                           //mensajePersonalizado("We have NOT gathered the date specified in the Dialog!");
                        }
                        //mUserHasReminder is defined by the switch button, clicked or not


                        int color = ColorGenerator.MATERIAL.getRandomColor();
                        newNote.setTodoColor(color);
                        strCol = color + "";  //convert the int to a string
                        //currentNote.set identifierr;
                        strIden = generateKey();
                        newNote.setTodoIdentifier(strIden);
                        //mensajePersonalizado(strIden);

                        mostrarDialogoConfirmacion(view);


                    } catch (Exception e) {
                        // manage exceptions
                        Toast.makeText(Alta_registro_todo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(Alta_registro_todo.this, "Please, enter a Title or a description", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Saves Data and Time Edit Text
    private void setDateAndTimeEditText() {
        mDateEditText.setText(getString(R.string.date_reminder_default));
//            mUserReminderDate = new Date();
        boolean time24 = DateFormat.is24HourFormat(getApplicationContext());
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

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            Toast.makeText(Alta_registro_todo.this, "We only add dates from now on. Not in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        if (DateFormat.is24HourFormat(getApplicationContext())) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
        setDateEditText();
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("Miguel Beascoa", "Time set: " + hour);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();

        setReminderTextView();

        setTimeEditText();
    }

    public void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
        mDateEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(getApplicationContext())) {
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

            if (DateFormat.is24HourFormat(getApplicationContext())) {
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

    // provides a string with the date in the specified formatString
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



    //03
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            //mensajePersonalizado("Entrando a buscar datos");
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urls[0]);
            //HttpPost httppost = new HttpPost("https://sheet.best/api/sheets/b706d82d-0d00-475f-a9d4-626566535083)");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
                nameValuePairs.add(new BasicNameValuePair("JSON_TITLE", tit));
                nameValuePairs.add(new BasicNameValuePair("JSON_DESCRIPTION", des));
                nameValuePairs.add(new BasicNameValuePair("JSON_IDEA", String.valueOf(ide)));
                nameValuePairs.add(new BasicNameValuePair("JSON_TODO", String.valueOf(tod)));
                nameValuePairs.add(new BasicNameValuePair("JSON_IMPORTANT", String.valueOf(imp) ));
                nameValuePairs.add(new BasicNameValuePair("JSON_HASREMINDER", String.valueOf(mUserHasReminder)));
                nameValuePairs.add(new BasicNameValuePair("JSON_DATETIME", strDateti));
                nameValuePairs.add(new BasicNameValuePair("JSON_COLOR", strCol));
                nameValuePairs.add(new BasicNameValuePair("JSON_IDENTIFIER", strIden));

                //httppost.setHeader("Content-Type", "application/json");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);



            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return "Executed";
            // return recuperarContenido(urls[0]);


        }



        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


        }
    }

    //04
    public void cerrarVentana(View view) {
        finish();
    }

    //05
    public void mostrarDialogoConfirmacion(View view) {
        DialogoConfirmacionAltaToDo confirmacion = new DialogoConfirmacionAltaToDo();
        confirmacion.show(getFragmentManager(), "Cuadro confirmación");
    }

    //06
    public void accionAceptar() {

        String url = GSAPI;
        new HttpAsyncTask().execute(url);
        mensajePersonalizado("Adding a ToDo in Google Sheets, thanks!");
        finish();

    }

    //07
    public void accionCancelar() {
        mensajePersonalizado("Cancelling , not adding the ToDo");
        finish();
    }

    //08 creación del mensaje personalizado Toast según sea aceptado o cancelado

    public void mensajePersonalizado(String opcion) {
        Toast mensaje = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.capa_toast,
                (ViewGroup) findViewById(R.id.lytLayout));

        TextView txtMsg = (TextView) layout.findViewById(R.id.txtMensaje);
        txtMsg.setText(opcion);

        mensaje.setDuration(Toast.LENGTH_SHORT);
        mensaje.setView(layout);
        mensaje.show();
    }
    //08 crea un string con identificador unico
    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}
