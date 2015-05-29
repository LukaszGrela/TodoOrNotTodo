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

import java.util.Random;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.MainActivityHandler, NewTodoDialogListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        randomTask.setTask("Random Task - "+randInt(0,1979));
        randomTask.setPriority(randInt(0,2));

        if(!app.dao().createNew(randomTask)) {
            Log.e(TAG, "Failed to add task.");
        }
        */

    }

    @Override
    public void onFilterChanged(MainActivityFragment.FilterOption option) {
        switch (option) {
            case ACTIVE:
                Log.i(TAG, "Show Active tasks");
                break;
            case COMPLETED:
                Log.i(TAG, "Show Completed tasks");
                break;
            case ALL:
            default:
                Log.i(TAG, "Show All tasks");
                break;
        }
    }


    private void openAddTodoDialogue() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NewTodoDialogFragment();
        dialog.show(getSupportFragmentManager(), "NewTodoDialogFragment");
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public void onTodoAdded(Todo todo) {
        final TodoOrNotTodo app = (TodoOrNotTodo)getApplication();

        if(!app.dao().createNew(todo)) {
            Log.e(TAG, "Failed to add task.");
        }
    }
}
