package com.juangabrielgomila.todolistjb;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by JuanGabriel on 20/11/17.
 */

public class DialogNewNote extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_new_note, null);

        final EditText editTitle = (EditText) dialogView.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) dialogView.findViewById(R.id.editDescription);

        final CheckBox checkBoxIdea = (CheckBox) dialogView.findViewById(R.id.checkBoxIdea);
        final CheckBox checkBoxTodo = (CheckBox) dialogView.findViewById(R.id.checkBoxTodo);
        final CheckBox checkBoxImportant = (CheckBox) dialogView.findViewById(R.id.checkBoxImportant);


        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);


        builder.setView(dialogView)
                .setMessage("Añadir una nueva nota");

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
                MainActivity callingActivity = (MainActivity) getActivity();
                //Notificaremos a la MA que hemos creado una nueva nota
                callingActivity.createNewNote(newNote);

                //Esto cierra el diálogo
                dismiss();
            }
        });

        //Una vez configurada nuestra alerta, le indicamos al builder que debe crearla en pantalla...
        return builder.create();
    }
}
