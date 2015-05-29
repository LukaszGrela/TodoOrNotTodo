package com.greladesign.examples.todoornottodo.squidb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yahoo.squidb.data.AbstractDatabase;
import com.yahoo.squidb.sql.Table;


public class TodosDB extends AbstractDatabase {
    private static final int VERSION = 1;
    private static final String DB_NAME = "todos.db";
    /**
     * Create a new AbstractDatabase
     *
     * @param context the Context, must not be null
     */
    public TodosDB(Context context) {
        super(context);
    }

    @Override
    protected String getName() {
        return DB_NAME;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[]{
                Todo.TABLE
        };
    }

    @Override
    protected boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return false;
    }
}
