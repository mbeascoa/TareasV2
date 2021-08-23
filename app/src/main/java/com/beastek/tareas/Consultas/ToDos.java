package com.beastek.tareas.Consultas;


import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.io.Serializable;



public class ToDos  {

    private String mTitle;
    private String mDescription;
    private boolean mIdea;
    private boolean mTodo;
    private boolean mImportant;
    private boolean mHasReminder;
    private Date mToDoDate;
    private int mTodoColor;
    private String mTodoIdentifier;


    private static final String JSON_TITLE = "title";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_IDEA = "idea";
    private static final String JSON_TODO = "todo";
    private static final String JSON_IMPORTANT = "important";
    private static final String JSON_HASREMINDER = "reminder";
    private static final String JSON_DATETIME = "datetime";
    private static final String JSON_COLOR = "todocolor";
    private static final String JSON_IDENTIFIER ="todoidentifier";


    //Constructor base vacío
    public ToDos(){
    }



    public ToDos(String todoTitle, String todoDescription, boolean todoIdea, boolean todoTodo, boolean todoImportant , boolean hasReminder, Date toDoDate, int toDoColor, String toDoUuid) {
        mTitle = todoTitle;
        mDescription = todoDescription;
        mIdea = todoIdea;
        mTodo= todoTodo;
        mImportant = todoImportant;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mTodoColor = toDoColor;
        mTodoIdentifier = toDoUuid ;

    }




    //Constructor para crear una nota a partir de un objeto JSON
    public ToDos(JSONObject jo) throws JSONException{
        mTitle = jo.getString(JSON_TITLE);
        mDescription = jo.getString(JSON_DESCRIPTION);
        mIdea = jo.getBoolean(JSON_IDEA);
        mTodo = jo.getBoolean(JSON_TODO);
        mImportant = jo.getBoolean(JSON_IMPORTANT);
        mHasReminder = jo.getBoolean(JSON_HASREMINDER);
        mTodoColor = jo.getInt(JSON_COLOR);
        // mTodoIdentifier = UUID.fromString(jo.getString(JSON_IDENTIFIER));
        mTodoIdentifier= jo.getString(JSON_IDENTIFIER);
        if (jo.has(JSON_DATETIME)) {
            mToDoDate = new Date(jo.getLong(JSON_DATETIME));
        }



    }


    //El método toma las 7 variables de la nota y las serializa en un objeto tipo JSON
    public JSONObject convertToDosToJSON() throws JSONException{

        JSONObject jo = new JSONObject();

        jo.put(JSON_TITLE, mTitle);
        jo.put(JSON_DESCRIPTION, mDescription);
        jo.put(JSON_IDEA, mIdea);
        jo.put(JSON_TODO, mTodo);
        jo.put(JSON_IMPORTANT, mImportant);
        jo.put(JSON_HASREMINDER, mHasReminder);
        if (mToDoDate != null) {
            jo.put(JSON_DATETIME, mToDoDate.getTime());
        }

        jo.put(JSON_COLOR, mTodoColor);
        jo.put(JSON_IDENTIFIER, mTodoIdentifier);
        return jo;
    }

        /*
        if (mTodoIdentifier != null) {
            jo.put(JSON_IDENTIFIER, mTodoIdentifier.toString());
        }

         */




    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean isIdea() {
        return mIdea;
    }

    public void setIdea(boolean mIdea) {
        this.mIdea = mIdea;
    }

    public boolean isTodo() {
        return mTodo;
    }

    public void setTodo(boolean mTodo) {
        this.mTodo = mTodo;
    }

    public boolean isImportant() {
        return mImportant;
    }

    public void setImportant(boolean mImportant) {
        this.mImportant = mImportant;
    }

    public boolean HasReminder() {
        return mHasReminder;
    }

    public void setReminder(boolean mHasReminder) {
        this.mHasReminder = mHasReminder;
    }

    public Date getToDoDate() {
        return mToDoDate;
    }


    public void setToDoDate(Date mToDoDate) {
        this.mToDoDate = mToDoDate;
    }


    public int getTodoColor() {
        return mTodoColor;
    }

    public void setTodoColor(int mTodoColor) {
        this.mTodoColor = mTodoColor;
    }

    public String getTodoIdentifier() {
        return mTodoIdentifier;
    }

    public void setTodoIdentifier(String mTodoIdentifier) {
        this.mTodoIdentifier = mTodoIdentifier;
    }

    @Override
    public String toString() {
        return "ToDos{" +
                "mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mIdea=" + mIdea +
                ", mTodo=" + mTodo +
                ", mImportant=" + mImportant +
                ", mHasReminder=" + mHasReminder +
                ", mToDoDate=" + mToDoDate +
                ", mTodoColor=" + mTodoColor +
                ", mTodoIdentifier='" + mTodoIdentifier + '\'' +
                '}';
    }
}

