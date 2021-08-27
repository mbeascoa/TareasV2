package com.beastek.tareas.Consultas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.beastek.tareas.R;
import com.beastek.tareas.Consultas.DeleteToDo;


public class Adaptador  extends RecyclerView.Adapter<Adaptador.ViewHolder>{

    private Date reminderdate;
    private String descriptiond,titled, fechaComoCadena, identifier;
    private List<com.beastek.tareas.Consultas.ToDos> listaToDos;
    int posicionseleccionada = 0;

    public Adaptador(List<com.beastek.tareas.Consultas.ToDos> ListaToDo) {
        this.listaToDos= ListaToDo;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
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
                    identifier = listaToDos.get(position).getTodoIdentifier();
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

        holder.cvRelleno.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ToDos tempToDo = listaToDos.get(position);
                String identifier = tempToDo.getTodoIdentifier().toString();
                removeItem(position);

                Intent i = new Intent(holder.itemView.getContext(), DeleteToDos.class);

                i.putExtra("IDENTIFIERTODO", identifier);
                holder.itemView.getContext().startActivity(i);




               /*
                DeleteToDo del = new DeleteToDo();

                del.DeleteTodo(identifier);

                */

                return false;
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
        public RelativeLayout layoutABorrar;
        public ViewHolder(View itemView){
            super(itemView);
            tvTitleTodo=(TextView) itemView.findViewById(R.id.tv_Title_CardView_ToDo);
            tvDescriptionTodo =(TextView) itemView.findViewById(R.id.tv_Description_CardView_ToDo);
            tvReminderTodo =(TextView) itemView.findViewById(R.id.tv_Reminder_CardView_ToDo);
            cvRelleno = (CardView) itemView.findViewById(R.id.card_datos);

        }
    }


    public void removeItem(int position){
        listaToDos.remove(position);
        notifyItemRemoved(position);
    }



}

