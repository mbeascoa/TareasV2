package com.beastek.tareas;

import android.app.Dialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;


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

        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView) dialogView.findViewById(R.id.txtDescription);

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



        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

        builder.setView(dialogView)
                .setMessage("Tu nota: ");

        //El botón OK simplemente cierra la nota
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return builder.create();
    }
}
