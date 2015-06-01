package com.greladesign.examples.todoornottodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.greladesign.examples.todoornottodo.squidb.Todo;

import static com.greladesign.examples.todoornottodo.Utils.randInt;

public class NewTodoDialogFragment extends DialogFragment {

    private static NewTodoDialogListener sDummyListener = new NewTodoDialogListener() {
        @Override
        public void onTodoAdded(Todo todo) {

        }
    };
    private NewTodoDialogListener mListener = sDummyListener;
    public interface NewTodoDialogListener {
        void onTodoAdded(Todo todo);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.fragment_dialog_new_todo, null);
        final EditText mTask = (EditText) view.findViewById(R.id.task);
        final RadioGroup mPriorityGroup = (RadioGroup) view.findViewById(R.id.priorityGroup);
        final RadioButton radio = (RadioButton) view.findViewWithTag("1");//
        if(radio!=null){
            radio.setChecked(true);
        }

        builder.setView(view)
                        // Add action buttons
                        .setPositiveButton(R.string.dialog_new_todo_add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //create new task and pass it back
                                final int selected =
                                        mPriorityGroup.getCheckedRadioButtonId();
                                final RadioButton radio = (RadioButton) view.findViewById(selected);
                                int priority = Integer.parseInt((String) radio.getTag());
                                //
                                if(mListener != null){
                                    final Todo todo = new Todo();
                                    final String task = mTask.getText().toString();
                                    //temp
                                    if("".equals(task)) {

                                        todo.setTask("Random Task - "+randInt(0,1979));
                                        todo.setPriority(randInt(0,2));

                                    } else {
                                        //temp
                                        todo.setDate(System.currentTimeMillis());
                                        todo.setPriority(priority);
                                        todo.setTask(task);
                                    }
                                    mListener.onTodoAdded(todo);
                                }
                                //
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismiss();
                            }
                        })
                        .setTitle(R.string.dialog_new_todo_title);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NewTodoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = sDummyListener;
    }
}
