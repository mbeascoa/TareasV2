package com.juangabrielgomila.todolistjb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter mNoteAdapter;

    private boolean mSound;
    private int mAnimOption;
    private SharedPreferences mPrefs;

    private Animation mAnimFlash;
    private Animation mAnimFadeIn;

    int mIdBeep = -1;
    SoundPool mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNoteAdapter = new NoteAdapter();
        ListView listNote = (ListView) findViewById(R.id.list_view);
        listNote.setAdapter(mNoteAdapter);

        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPos, long id) {

                if(mSound){
                    mSp.play(mIdBeep, 1, 1, 0, 0, 1);
                }

                //Recuperamos la nota de la posición pulsada por el usuario
                Note tempNote = mNoteAdapter.getItem(itemPos);

                //Creamos una instancia de show note
                DialogShowNote dialog = new DialogShowNote();
                dialog.sendNoteSelected(tempNote);
                dialog.show(getFragmentManager(),"SN");
            }
        });


        listNote.setLongClickable(true);

        listNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int whichItem, long id) {
                mNoteAdapter.deleteNote(whichItem);
                return false;
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



    //El método on Resume se llama tanto después del On Create, como cuando volemos a la actividad después de pasar por otra
    @Override
    protected void onResume() {
        super.onResume();
        mPrefs = getSharedPreferences("To Do List JB", MODE_PRIVATE);
        mSound = mPrefs.getBoolean("sound", true);
        mAnimOption = mPrefs.getInt("anim option", SettingsActivity.FAST);

        mAnimFlash = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash);
        mAnimFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        if (mAnimOption == SettingsActivity.FAST){
            mAnimFlash.setDuration(100);
        } else if(mAnimOption == SettingsActivity.SLOW){
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            //Aquí debemos invocar una nueva instancia del diálogo para crear notas
            DialogNewNote dialog = new DialogNewNote();
            //Mostramos ese diálogo a través del manager
            dialog.show(getFragmentManager(),"note_create");
        }


        if (item.getItemId() == R.id.action_setings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }


        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();

        mNoteAdapter.saveNotes();
    }

    public class NoteAdapter extends BaseAdapter {

        List<Note> noteList = new ArrayList<Note>();
        private JSONSerializer mSerializer;

        public NoteAdapter(){
            mSerializer = new JSONSerializer("ToDoListsJB.json", MainActivity.this.getApplicationContext());

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
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
            }

            //Cuando estamos aquí, ya tenemos la vista bien definida
            //cargamos todos los widgets del layout

            TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
            TextView textViewDescription = (TextView) view.findViewById(R.id.text_view_description);

            ImageView ivImportant = (ImageView) view.findViewById(R.id.image_view_important);
            ImageView ivTodo = (ImageView) view.findViewById(R.id.image_view_todo);
            ImageView ivIdea = (ImageView) view.findViewById(R.id.image_view_idea);

            //y podemos proceder a ocultar las imágenes que sobren del layout...
            //y rellenar título y descripción de la tarea

            Note currentNote = noteList.get(itemPos);

            if (currentNote.isImportant()&&mAnimOption!=SettingsActivity.NONE){
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
