package com.greladesign.examples.todoornottodo;

import android.app.Application;

import com.greladesign.examples.todoornottodo.squidb.TodosHelper;

/**
 * Created by Łukasz 'Severiaan' Grela on 28/05/2015.
 */
public class TodoOrNotTodo extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //
        TodosHelper.init(getApplicationContext());
    }
}
