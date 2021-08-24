package com.beastek.tareas.Consultas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.beastek.tareas.R;

public class ActivityModificarToDo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_todo);

        Intent inta = getIntent();
        ToDos data = (ToDos) inta.getSerializableExtra("data");

        //Creamos una instancia de show note
        DialogModificarTodo dialog = new DialogModificarTodo();
        dialog.sendNoteSelected(data);
        dialog.show(getSupportFragmentManager(), "modify_note");
    }


   //03 modify existing Tarea
    public void modifyExistingToDo (ToDos existingToDo){
        //Este método, modificará una nueva nota creada por el diálogo pertinente...
        finish();
    }

}