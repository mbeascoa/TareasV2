package com.beastek.tareas;

import android.app.Dialog;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DialogShowNote extends DialogFragment {

    private com.beastek.tareas.Note mNote;

    public void sendNoteSelected(com.beastek.tareas.Note noteSelected){
        this.mNote = noteSelected;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_show_note, null);

        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle_show_note);
        TextView txtDescription = (TextView) dialogView.findViewById(R.id.txtDescription_show_note);
        TextView txtReminder = (TextView) dialogView.findViewById(R.id.txtReminder_show_note);
        TextView txtDateTime = (TextView) dialogView.findViewById(R.id.txtDate_show_note);

        txtTitle.setText(mNote.getTitle());
        txtDescription.setText(mNote.getDescription());


        ImageView ivImportant = (ImageView) dialogView.findViewById(R.id.imageViewImportant);
        ImageView ivTodo = (ImageView) dialogView.findViewById(R.id.imageViewTodo);
        ImageView ivIdea = (ImageView) dialogView.findViewById(R.id.imageViewIdea);


        //Cada imagen se oculta si la nota no es de ese tipo
        if (!mNote.isImportant()){
            ivImportant.setVisibility(View.GONE);
        }
        if (!mNote.isTodo()){
            ivTodo.setVisibility(View.GONE);
        }
        if(!mNote.isIdea()){
            ivIdea.setVisibility(View.GONE);
        }


       if (!mNote.HasReminder()){
            txtReminder.setVisibility(View.GONE);
            txtDateTime.setVisibility(View.GONE);
        } else {
           txtReminder.setVisibility(View.VISIBLE);
           txtDateTime.setVisibility(View.VISIBLE);
           txtReminder.setText("Yo have to manage this task on :");
            //includes the date and time to you show note
            Date date = mNote.getToDoDate();
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
            txtDateTime.setTextColor(getResources().getColor(R.color.mdtp_accent_color_dark));
            txtDateTime.setText(finalString);
        }



        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

        builder.setView(dialogView)
                .setMessage("Your task is : ");

        //El botón OK simplemente cierra la nota
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //Once we have configured our Dialog, we let the builder that has to generate the window, Una vez configurada nuestro dialogo, le indicamos al builder que debe crearla en pantalla....
        return builder.create();
    }

    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }
}
