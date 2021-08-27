package com.beastek.tareas.Consultas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;


import com.beastek.tareas.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdaptadorDetalle extends RecyclerView.Adapter<AdaptadorDetalle.ViewHolder>{
    private Date reminderdate;
    private String descriptiond,titled, fechaComoCadena;
    private CheckBox cbidea, cdtodo, cbimportant;
    private Boolean idead, todod, importantd;
    private List<ToDos> listaToDos;
    int posicionseleccionada = 0;

    public AdaptadorDetalle(List<ToDos> ListaToDo) {
        this.listaToDos= ListaToDo;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tododetalle_cardview,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        idead = listaToDos.get(position).isIdea();
        holder.cbidea.setChecked(idead);

        todod= listaToDos.get(position).isTodo();
        holder.cbtodo.setChecked(todod);

        importantd= listaToDos.get(position).isImportant();
        holder.cbimportant.setChecked(importantd);

        titled= listaToDos.get(position).getTitle();
        holder.tvTitleTodo.setText(titled);

        descriptiond= listaToDos.get(position).getDescription();
        holder.tvDescriptionTodo.setText(descriptiond);

        reminderdate = listaToDos.get(position).getToDoDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        fechaComoCadena = sdf.format(reminderdate);
        System.out.println(fechaComoCadena);
        holder.tvReminderTodo.setText(fechaComoCadena);

        holder.cvRelleno.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            //Recuperamos la nota de la posición pulsada por el usuario
                ToDos tempToDo = listaToDos.get(position);
                Intent i = new Intent(holder.itemView.getContext(), ActivityModificarToDo.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", tempToDo);
                i.putExtras(bundle);
                holder.itemView.getContext().startActivity(i);

                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount(){
        return listaToDos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitleTodo, tvDescriptionTodo, tvReminderTodo;
        private CheckBox cbidea, cbtodo, cbimportant;
        private CardView cvRelleno;
        public ViewHolder(View v){
            super(v);
            tvTitleTodo=(TextView) v.findViewById(R.id.tv_Title_CardView_ToDo);
            tvDescriptionTodo =(TextView) v.findViewById(R.id.tv_Description_CardView_ToDo);
            tvReminderTodo =(TextView) v.findViewById(R.id.tv_Reminder_CardView_ToDo);
            cbidea = (CheckBox) v.findViewById(R.id.checkBoxIdea);
            cbtodo = (CheckBox) v.findViewById(R.id.checkBoxTodo);
            cbimportant = (CheckBox) v.findViewById(R.id.checkBoxImportant);
            cvRelleno = (CardView) v.findViewById(R.id.card_datos);
        }

    }

}
