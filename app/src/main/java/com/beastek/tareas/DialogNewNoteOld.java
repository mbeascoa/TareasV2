package com.beastek.tareas;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Date;


public class DialogNewNoteOld extends DialogFragment {

    private String CombinationText;
    private boolean setDateButtonClickedOnce = false;
    private boolean setTimeButtonClickedOnce = false;
    private String mUserEnteredText;
    private String mUserEnteredDescription;
    private boolean mUserHasReminder;
    private Date mUserReminderDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_new_note_old, null);
        final EditText editTitle = (EditText) dialogView.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) dialogView.findViewById(R.id.editDescription);




        //Button for Copy to Clipboard
        final Button mCopyClipboard = (Button) dialogView.findViewById(R.id.copyclipboard);

        //OnClickListener for CopyClipboard Button
        mCopyClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toDoTextContainer = editTitle.getText().toString();
                String toDoTextBodyDescriptionContainer = editDescription.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                CombinationText = "Title : " + toDoTextContainer + "\nDescription : " + toDoTextBodyDescriptionContainer + "\n -Copied From Entities Online";
                ClipData clip = ClipData.newPlainText("text", CombinationText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied To Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageButton reminderIconImageButton = (ImageButton) dialogView.findViewById(R.id.userToDoReminderIconImageButton);
        final TextView reminderRemindMeTextView = (TextView) dialogView.findViewById(R.id.userToDoRemindMeTextView);
        final SwitchCompat mToDoDateSwitch = (SwitchCompat) dialogView.findViewById(R.id.toDoHasDateSwitchCompat);
        final TextView mReminderTextView = (TextView) dialogView.findViewById(R.id.altaToDoTimeReminderTV);


        final CheckBox checkBoxIdea = (CheckBox) dialogView.findViewById(R.id.checkBoxIdea);
        final CheckBox checkBoxTodo = (CheckBox) dialogView.findViewById(R.id.checkBoxTodo);
        final CheckBox checkBoxImportant = (CheckBox) dialogView.findViewById(R.id.checkBoxImportant);


        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);



        builder.setView(dialogView)
                .setMessage("Add a new note ");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creamos una nota vacía
                Note newNote  = new Note();

                //Configuramos las 5 variables de la nota creada
                newNote.setTitle(editTitle.getText().toString());
                newNote.setDescription(editDescription.getText().toString());

                newNote.setIdea(checkBoxIdea.isChecked());
                newNote.setTodo(checkBoxTodo.isChecked());
                newNote.setImportant(checkBoxImportant.isChecked());


                //Hago un casting a Main Activity, que es quien ha llamado al diálogo
                MainActivityNote callingActivity = (MainActivityNote) getActivity();
                //Notificaremos a la MA que hemos creado una nueva nota
                callingActivity.createNewNote(newNote);

                //Esto cierra el diálogo
                dismiss();
            }
        });

        //Una vez configurada nuestra alerta, le indicamos al builder que debe crearla en pantalla...
        return builder.create();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
