package com.greladesign.examples.todoornottodo;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.greladesign.examples.todoornottodo.NewTodoDialogFragment.NewTodoDialogListener;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosHelper;


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


            TodosHelper.getInstance().deleteAll();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddTask() {
        openAddTodoDialogue();

    }

    @Override
    public void onEditTask(Todo model) {
        openEditTodoDialogue(model);
    }


    @Override
    public void onClearTasks() {
        /* TODO: we could check if user is sure to remove them all */

        final int deleted = TodosHelper.getInstance().deleteComplete();
        Log.i(TAG, "Deleted " + deleted + " tasks.");

    }



    private void openAddTodoDialogue() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NewTodoDialogFragment();

        dialog.show(getSupportFragmentManager(), "NewTodoDialogFragment");
    }

    private void openEditTodoDialogue(Todo model) {

        // Create an instance of the dialog fragment and show it
        final DialogFragment dialog = new NewTodoDialogFragment();
        final Bundle arguments = new Bundle();
            arguments.putLong("id", model.getId());
            arguments.putString("task", model.getTask());
            arguments.putBoolean("done", model.isDone());
            arguments.putInt("priority", model.getPriority());
            arguments.putLong("started", model.getDate());
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "NewTodoDialogFragment");

    }

    @Override
    public void onTodoAdded(Todo todo) {

        if (!TodosHelper.getInstance().newTask(todo)) {
            Log.e(TAG, "Failed to add task.");
        }
    }

    @Override
    public void onTodoModified(Todo todo) {

        if (!TodosHelper.getInstance().modifyTask(todo)) {
            Log.e(TAG, "Failed to modify task.");
        }
    }
}
