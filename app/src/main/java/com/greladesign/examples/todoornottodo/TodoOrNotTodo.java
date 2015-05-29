package com.greladesign.examples.todoornottodo;

import android.app.Application;
import android.net.Uri;

import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosDB;
import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.UriNotifier;
import com.yahoo.squidb.sql.SqlTable;

import java.util.Set;

/**
 * Created by Łukasz 'Severiaan' Grela on 28/05/2015.
 */
public class TodoOrNotTodo extends Application {

    private DatabaseDao mTodosDAO;
    private UriNotifier mNotifier = new UriNotifier() {
        @Override
        public void addUrisToNotify(Set<Uri> uris, SqlTable<?> table, String databaseName, DBOperation operation, AbstractModel modelValues, long rowId) {
            // Notifies some constant Uri for any update on the students table
            if (Todo.TABLE.equals(table)) {
                uris.add(Todo.CONTENT_URI);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        mTodosDAO = new DatabaseDao(new TodosDB(getApplicationContext()));
        //register for uri notifications
        mTodosDAO.registerUriNotifier(mNotifier);
    }

    public synchronized DatabaseDao dao() {
        return mTodosDAO;
    }
}
