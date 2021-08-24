package com.beastek.tareas.Consultas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.beastek.tareas.R;


public class Adaptador  extends RecyclerView.Adapter<Adaptador.ViewHolder>{

    private Date reminderdate;
    private String descriptiond,titled, fechaComoCadena;
    private List<com.beastek.tareas.Consultas.ToDos> listaToDos;
    int posicionseleccionada = 0;

    public Adaptador(List<com.beastek.tareas.Consultas.ToDos> ListaToDo) {
        this.listaToDos= ListaToDo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_todo_cardview,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                posicionseleccionada = position;

                if (posicionseleccionada == position) {
                    holder.tvTitleTodo.setTextColor(Color.RED);


                } else {
                    holder.tvTitleTodo.setTextColor(Color.DKGRAY);
                }
                notifyDataSetChanged();
                //Notificamos cambios para que el contenedr se entere y refresque los datos
                Intent i = new Intent(holder.itemView.getContext(), Detalle_ToDo.class);

                i.putExtra("NUMEROTODO", listaToDos.get(position).getTodoIdentifier());
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount(){
        return listaToDos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitleTodo, tvDescriptionTodo, tvReminderTodo;
        private CardView cvRelleno;
        public ViewHolder(View v){
            super(v);
            tvTitleTodo=(TextView) v.findViewById(R.id.tv_Title_CardView_ToDo);
            tvDescriptionTodo =(TextView) v.findViewById(R.id.tv_Description_CardView_ToDo);
            tvReminderTodo =(TextView) v.findViewById(R.id.tv_Reminder_CardView_ToDo);
            cvRelleno = (CardView) v.findViewById(R.id.card_datos);

        }
    }
}
