package com.beastek.tareas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.WorkManager;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import com.beastek.tareas.MainActivityNote.NoteAdapter.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class MainSetNotification extends AppCompatActivity {

    private Button btnGuardar, btnEliminar, selefecha, selehora, btnOk;
    private EditText  txtTitle, txtDescription;
    private TextView tvfecha, tvhora,txtReminder,txtDateTime, tvAMPM;
    private ImageView ivImportant, ivTodo, ivIdea;
    private JSONSerializerNote mSerializer;
    private CheckBox checkBoxIdea, checkBoxTodo,checkBoxImportant ;
    private Calendar  actual , reminderCalendar;
    private int y,mon,d,h,min;
    private Date mUserReminderDate;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setnotifications);

        Bundle bundle = getIntent().getExtras();
        int noteSelectedId = bundle.getInt("SELECTEDNOTE");



        List<Note> noteList = new ArrayList<Note>();


        mSerializer = new JSONSerializerNote("EOLJSON.json", MainSetNotification.this.getApplicationContext());

        try {
            noteList = mSerializer.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Note currentNote = noteList.get(noteSelectedId);

        Log.d("Notificacion llegando", currentNote.toString());

        txtTitle = (EditText) findViewById(R.id.txtTitle_show_note);
        txtDescription = (EditText) findViewById(R.id.txtDescription_show_note);
        txtReminder = (TextView) findViewById(R.id.txtReminder_show_note);
        txtDateTime = (TextView) findViewById(R.id.txtDate_show_note);
        tvAMPM = (TextView) findViewById(R.id.tv_ampm);

        txtTitle.setText(currentNote.getTitle());
        txtDescription.setText(currentNote.getDescription());


        ivImportant = (ImageView) findViewById(R.id.imageViewImportant);
        ivTodo = (ImageView) findViewById(R.id.imageViewTodo);
        ivIdea = (ImageView) findViewById(R.id.imageViewIdea);

        checkBoxIdea = (CheckBox) findViewById(R.id.checkBoxIdea);
        checkBoxTodo = (CheckBox) findViewById(R.id.checkBoxTodo);
        checkBoxImportant = (CheckBox) findViewById(R.id.checkBoxImportant);


        //Cada imagen se oculta si la nota no es de ese tipo
        if (!currentNote.isImportant()) {
            ivImportant.setVisibility(View.GONE);
        }
        if (!currentNote.isTodo()) {
            ivTodo.setVisibility(View.GONE);
        }
        if (!currentNote.isIdea()) {
            ivIdea.setVisibility(View.GONE);
        }


        if (!currentNote.HasReminder()) {
            txtReminder.setVisibility(View.GONE);
            txtDateTime.setVisibility(View.GONE);
        } else {
            txtReminder.setVisibility(View.VISIBLE);
            txtDateTime.setVisibility(View.VISIBLE);
            txtReminder.setText("Yo have to manage this task on :");
            //includes the date and time to you show note
            Date date = currentNote.getToDoDate();
            String dateString = formatDate("d MMM, yyyy", date);

            String timeString;
            String amPmString = " ";

            if (DateFormat.is24HourFormat(getApplicationContext())) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);

            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            txtDateTime.setTextColor(getResources().getColor(R.color.mdtp_accent_color_dark));
            txtDateTime.setText(finalString);
        }


        btnOk = (Button) findViewById(R.id.btn_salir);


        //El botón OK simplemente cierra la nota
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salir(noteSelectedId);

                finish();
            }
        });

        selefecha = findViewById(R.id.btn_selefecha);
        selehora = findViewById(R.id.btn_selehora);
        tvfecha = findViewById(R.id.tv_fecha);
        tvhora = findViewById(R.id.tv_hora);

        //actual date
        actual = Calendar.getInstance();
        //date to remind
        reminderCalendar = Calendar.getInstance();

        selefecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int anio = actual.get(Calendar.YEAR);
                int mes = actual.get(Calendar.MONTH);
                int dia = actual.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int mon, int d) {
                        setDate(y,mon,d);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = format.format(reminderCalendar.getTime());
                        tvfecha.setText(strDate);
                        mUserReminderDate= reminderCalendar.getTime();
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        selehora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hora = actual.get(Calendar.HOUR_OF_DAY);
                int minutos = actual.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int h, int min) {
                        setTime( h, min);
                        reminderCalendar.set(Calendar.HOUR_OF_DAY, h);
                        reminderCalendar.set(Calendar.MINUTE, min);
                        tvhora.setText(String.format("%02d:%02d", h, min));
                        mUserReminderDate= reminderCalendar.getTime();
                    }
                }, hora, minutos, true);
                timePickerDialog.show();
            }
        });


        btnGuardar = findViewById(R.id.btn_guardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag1 = generateKey();

                Long Alerttime = reminderCalendar.getTimeInMillis() - System.currentTimeMillis();
                String alerta = String.valueOf(Alerttime);
                Toast.makeText(MainSetNotification.this, alerta, Toast.LENGTH_SHORT).show();
                int random = (int) (Math.random() * 50 + 1);
                Data data = GuardarData("Notificacion Alerta Tarea", "Este es el detalle", random);
                Workmanagernoti.GuardarNoti(Alerttime, data, "tag1");
                Toast.makeText(MainSetNotification.this, "Alarma Guardada.", Toast.LENGTH_SHORT).show();
            }
        });

        btnEliminar = findViewById(R.id.btn_eliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EliminarNoti("tag1");
                Toast.makeText(MainSetNotification.this, "Alarma Eliminada.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String generateKey() {

        return UUID.randomUUID().toString();
    }


    private void EliminarNoti(String tag) {
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);
        Toast.makeText(MainSetNotification.this, "Alarma Eliminada!", Toast.LENGTH_SHORT).show();
    }


    private Data GuardarData(String titulo, String detalle, int idnoti) {
        return new Data.Builder()
                .putString("titulo", titulo)
                .putString("detalle", detalle)
                .putInt("idnoti", idnoti).build();


    }

    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);


    }

    // verify that everything is OK, and save NOTE
    public void salir( int noteSelectedId) {

        //Creamos una nota vacía
        Note currentNote = new Note();

        String mToDoTextBodyTitle =txtTitle.getText().toString();
        String mToDoTextBodyDescription = txtDescription.getText().toString();

        if (mToDoTextBodyTitle.length() <= 0) {
            txtTitle.setText(getString(R.string.todo_error));
            txtTitle.setTextColor(Color.RED);
        } else {

            //Configuramos las 7 variables de la nota creada
            currentNote.setTitle(txtTitle.getText().toString());
            currentNote.setDescription(txtDescription.getText().toString());

            currentNote.setIdea(checkBoxIdea.isChecked());
            currentNote.setTodo(checkBoxTodo.isChecked());
            currentNote.setImportant(checkBoxImportant.isChecked());
            int color = ColorGenerator.MATERIAL.getRandomColor();
            currentNote.setTodoColor(color);
            if (actual.getTime().before(reminderCalendar.getTime())){
                currentNote.setReminder(true);
                currentNote.setToDoDate(mUserReminderDate);
            } else {
                currentNote.setReminder(false);
                Log.i("Note saves as :", currentNote.toString());
            }



            //currentNote.set identifierr;
            String identifier = java.util.UUID.randomUUID().toString();
            currentNote.setTodoIdentifier(identifier);

            noteList.set(noteSelectedId, currentNote);
        }
    }
    public void setDate(int year, int month, int day) {
        Calendar actual = Calendar.getInstance();
        int hour, minute;


        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(actual)) {
            Toast.makeText(MainSetNotification.this, "We only add dates from now on. Not in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            reminderCalendar.setTime(mUserReminderDate);
        }

        if (DateFormat.is24HourFormat(getApplicationContext())) {
            hour = reminderCalendar.get(Calendar.HOUR_OF_DAY);
        } else {

            hour = reminderCalendar.get(Calendar.HOUR);
        }
        minute = reminderCalendar.get(Calendar.MINUTE);

        reminderCalendar.set(year, month, day, hour, minute);
        mUserReminderDate = reminderCalendar.getTime();
        setDateEditText();

    }

    // settles the time
    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }
        if (reminderCalendar.before(actual)) {
            Toast.makeText(MainSetNotification.this, "We only add dates from now on. Not in the past", Toast.LENGTH_SHORT).show();
            return;
        } else {
            int year = reminderCalendar.get(Calendar.YEAR);
            int month = reminderCalendar.get(Calendar.MONTH);
            int day = reminderCalendar.get(Calendar.DAY_OF_MONTH);
            Log.d("Miguel Beascoa", "Time set: " + hour);
            calendar.set(year, month, day, hour, minute, 0);
            mUserReminderDate = reminderCalendar.getTime();
            setTimeEditText();
        }

    }
    public void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
        tvfecha.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";

        }
        tvhora.setText(formatDate(dateFormat, mUserReminderDate));
    }
}
