package com.beastek.tareas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;


import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.beastek.tareas.Note;

public class MainActivityNote extends AppCompatActivity {

    private NoteAdapter mNoteAdapter;

    private boolean mSound;
    private int mAnimOption;
    private SharedPreferences mPrefs;

    private Animation mAnimFlash;
    private Animation mAnimFadeIn;
    private int noteSelectedId =-1;  //initialize the note, if there is no note -1
    private Object mActionMode;


    int mIdBeep = -1;
    SoundPool mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_note);

        mNoteAdapter = new NoteAdapter();
        ListView listNote = (ListView) findViewById(R.id.list_view);
        listNote.setAdapter(mNoteAdapter);

        /*  listNote.setEmptyView(findViewById(R.id.empty_list_item));  sale una nota de texto
         <TextView
        android:id="@+id/empty_list_item"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone"
        android:text="There are NO tasks, add one if you want pressin the + top right icon" >
         </TextView>
         we put that statement if we want to include a TEXT MESSAGE, we provided an Image, empty_view_bg
         we use the following command....and use visibility = "gone" in the XML definition for @+id/empty_list_item
         */

        listNote.setEmptyView(findViewById(R.id.empty_list_item));


        // we make a beep sound when entering in the cardview defined within listview
        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPos, long id) {

              if(mSound){
                    mSp.play(mIdBeep, 1, 1, 0, 0, 1);
                }
                Toast.makeText(MainActivityNote.this, "Solo funciona cuando se toca en los bordes de la cardview", Toast.LENGTH_SHORT).show();

                //Recuperamos la nota de la posición pulsada por el usuario
                Note tempNote = mNoteAdapter.getItem(itemPos);

                //Creamos una instancia de show note
                DialogShowNote dialog = new DialogShowNote();
                dialog.sendNoteSelected(tempNote);
                dialog.show(getSupportFragmentManager(), "show_note");



            }
        });

        //we allow long Clickable in listview items
        listNote.setLongClickable(true);

        listNote.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int whichItem, long id) {
                noteSelectedId = whichItem;
                mActionMode = startActionMode(amc);
                view.setSelected(true);
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attr)
                    .build();
        } else {
            // for those below LOLLIPOP version, this SoundPool y deprecated now, but was enabled before lollipop
            mSp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try{
            AssetManager manager = this.getAssets();
            AssetFileDescriptor desc = manager.openFd("beep.ogg");
            mIdBeep = mSp.load(desc, 0);

        } catch (IOException e){
            e.printStackTrace();
        }


    }

    private ActionMode.Callback amc = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_crud_note, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId()== R.id.item_delete_note){
                deleteNote(noteSelectedId);
                mode.finish();
            }
            if (item.getItemId()==R.id.item_add_date_note){
                Toast.makeText(MainActivityNote.this, "We add a reminder to the note", Toast.LENGTH_LONG).show();

            }
            if(item.getItemId()==R.id.item_exit_note){
                Toast.makeText(MainActivityNote.this, "Salimos sin hacer nada", Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    };

    // delete the selected Item within the list view
    private void deleteNote(int whichItem){
        mNoteAdapter.deleteNote(whichItem);
    }

    //El método on Resume se llama tanto después del On Create, como cuando volvemos a la actividad después de pasar por otra
    @Override
    protected void onResume() {
        super.onResume();
        mPrefs = getSharedPreferences("EOL", MODE_PRIVATE);
        mSound = mPrefs.getBoolean("sound", true);
        mAnimOption = mPrefs.getInt("anim option", SettingsActivityNote.FAST);

        mAnimFlash = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash);
        mAnimFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        if (mAnimOption == SettingsActivityNote.FAST){
            mAnimFlash.setDuration(100);
        } else if(mAnimOption == SettingsActivityNote.SLOW){
            mAnimFlash.setDuration(1000);
        }

        mNoteAdapter.notifyDataSetChanged();

    }

    public void createNewNote(Note newNote){
        //Este método, recibirá una nueva nota creada por el diálogo pertinente...
        mNoteAdapter.addNote(newNote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent accion;

        if (id== R.id.action_add)
        {
            //Aquí debemos invocar una nueva instancia del diálogo para crear notas
            DialogNewNote dialog = new DialogNewNote();
            //Mostramos ese diálogo a través del manager
            dialog.show(getSupportFragmentManager(),"note_create");
        }else if (id== R.id.action_settings)
        {
            accion = new Intent(MainActivityNote.this, SettingsActivityNote.class);
            startActivity(accion);
        } else if (id== R.id.action_about)
        {
            accion = new Intent(MainActivityNote.this, AboutActivity.class);
            startActivity(accion);
        }
        return true;

    }


    @Override
    protected void onPause() {
        super.onPause();

        mNoteAdapter.saveNotes();
    }

    public class NoteAdapter extends BaseAdapter {

        List<Note> noteList = new ArrayList<Note>();
        private JSONSerializerNote mSerializer;

        public NoteAdapter(){
            mSerializer = new JSONSerializerNote("EOLJSON.json", MainActivityNote.this.getApplicationContext());

            try{
                noteList = mSerializer.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        public void saveNotes(){
            try{
                mSerializer.save(noteList);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Note getItem(int itemPos) {
            return noteList.get(itemPos);
        }

        @Override
        public long getItemId(int itemPos) {
            return itemPos;
        }

        @Override
        public View getView(int itemPos, View view, ViewGroup viewGroup) {

            //Aquí programaremos la lógica de las celdas de la lista
            if (view == null){
                //La vista todavía no ha sido accedida anteriormente
                //así que lo primero que hay que hacer es inflarla
                //a partir del layout list_item.xml
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item_note, viewGroup, false);
            }

            //Cuando estamos aquí, ya tenemos la vista bien definida
            //cargamos todos los widgets del layout

            CardView cardView = (CardView)  view.findViewById(R.id.cv_item_list_note);

            TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
            TextView textViewDescription = (TextView) view.findViewById(R.id.text_view_description);
            TextView mReminderTextView = (TextView) view.findViewById(R.id.tv_date_listview);

            ImageView ivImportant = (ImageView) view.findViewById(R.id.image_view_important);
            ImageView ivTodo = (ImageView) view.findViewById(R.id.image_view_todo);
            ImageView ivIdea = (ImageView) view.findViewById(R.id.image_view_idea);


            //y podemos proceder a ocultar las imágenes que sobren del layout...
            //y rellenar título y descripción de la tarea

            Note currentNote = noteList.get(itemPos);

            if (currentNote.isImportant()&&mAnimOption!= SettingsActivityNote.NONE){
                view.setAnimation(mAnimFlash);
            } else {
                view.setAnimation(mAnimFadeIn);
            }



            if (!currentNote.isImportant()){
                ivImportant.setVisibility(View.GONE);
            }
            if (!currentNote.isTodo()){
                ivTodo.setVisibility(View.GONE);
            }
            if (!currentNote.isIdea()){
                ivIdea.setVisibility(View.GONE);
            }

            textViewTitle.setText(currentNote.getTitle());
            textViewDescription.setText(currentNote.getDescription());
            mReminderTextView.setText("Es una cagada");

            return view;
        }



        public void addNote(Note n){
            noteList.add(n);
            notifyDataSetChanged();
        }


        public void deleteNote(int n){
            noteList.remove(n);
            notifyDataSetChanged();
        }



    }


}
