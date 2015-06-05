package com.greladesign.examples.todoornottodo;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.greladesign.examples.todoornottodo.NewTodoDialogFragment.NewTodoDialogListener;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.yahoo.squidb.sql.Criterion;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.MainActivityHandler, NewTodoDialogListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogFragment dialog = (DialogFragment) getSupportFragmentManager().getFragment(Bundle.EMPTY, "NewTodoDialogFragment");
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {

            final TodoOrNotTodo app = (TodoOrNotTodo)getApplication();
            app.dao().deleteWhere(Todo.class, Criterion.all);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddTask() {
        openAddTodoDialogue();
        /*
        final TodoOrNotTodo app = (TodoOrNotTodo)getApplication();

        final Todo randomTask = new Todo();
        randomTask.setDate(System.currentTimeMillis());

        if(!app.dao().createNew(randomTask)) {
            Log.e(TAG, "Failed to add task.");
        }
        */

    }

    @Override
    public void onClearTasks() {
        /* we could check if user is sure to remove them all */

        final TodoOrNotTodo app = (TodoOrNotTodo)getApplication();
        final int deleted = app.dao().deleteWhere(Todo.class, Todo.DONE.eq(true));
        Log.i(TAG, "Deleted "+deleted+" tasks.");
    }



    private void openAddTodoDialogue() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NewTodoDialogFragment();
        dialog.show(getSupportFragmentManager(), "NewTodoDialogFragment");
    }

    @Override
    public void onTodoAdded(Todo todo) {
        final TodoOrNotTodo app = (TodoOrNotTodo)getApplication();

        if(!app.dao().createNew(todo)) {
            Log.e(TAG, "Failed to add task.");
        }
    }
}
