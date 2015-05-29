package com.greladesign.examples.todoornottodo.squidb;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.greladesign.examples.todoornottodo.R;
import com.yahoo.squidb.utility.SquidCursorAdapter;

public class TodosAdapter extends SquidCursorAdapter<Todo> {

    private static final String TAG = "TodosAdapter";
    private int mRowResource;

    public TodosAdapter(Context context) {
        super(context, new Todo());
        mRowResource = R.layout.list_row_todo;
    }
    public TodosAdapter(Context context, int rowResource) {
        this(context);
        mRowResource = rowResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mRowResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        View view;
        TextView tvTask;
        CheckBox cbDone;

        if (convertView == null) {
            view = getLayoutInflater().inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        Todo item = getItem(position);

        try {
            tvTask = (TextView) view.findViewById(R.id.tvTask);
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    getClass().getSimpleName() +" requires the resource ID to be a TextView", e);
        }
        try {
            cbDone = (CheckBox) view.findViewById(R.id.cbDone);
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    getClass().getSimpleName() +" requires the resource ID to be a TextView", e);
        }
        Log.i(TAG, "item="+item);
        if (item != null) {
            if (tvTask != null) tvTask.setText(item.getTask());
            if (cbDone != null) cbDone.setChecked(item.isDone());
        }
        return view;
    }
}
