package com.beastek.tareas.Consultas;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.beastek.tareas.R;

public class DialogoConfirmacionDeleteToDo extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_baseline_delete_sweep_24)
                .setTitle("Are you sure you want to delete that To Do")
                .setPositiveButton("Of course",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((DeleteToDos)getActivity()).accionAceptar();

                            }
                        }
                )
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((DeleteToDos)getActivity()).accionCancelar();
                            }
                        }
                )
                .create();
    }
}