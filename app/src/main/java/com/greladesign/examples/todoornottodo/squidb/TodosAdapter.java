package com.greladesign.examples.todoornottodo.squidb;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.greladesign.examples.todoornottodo.R;
import com.yahoo.squidb.utility.SquidCursorAdapter;

public class TodosAdapter extends SquidCursorAdapter<Todo> {

    private TodoRowActionListener mListener;
    private static final String TAG = "TodosAdapter";
    private int mRowResource;
    private boolean mInternal = false;

    public interface TodoRowActionListener {
        void onCompletionChanged(int position, boolean state);
    }


    public TodosAdapter(Context context) {
        super(context, new Todo());
        mRowResource = R.layout.list_row_todo;
    }
    public TodosAdapter(Context context, int rowResource) {
        this(context);
        mRowResource = rowResource;
    }

    public void setTodoRowActionListener(TodoRowActionListener listener) {
        mListener = listener;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mRowResource);
    }

    private View createViewFromResource(final int position, View convertView, ViewGroup parent,
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
            if(cbDone != null){

                cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(mListener != null && !mInternal) {
                            mListener.onCompletionChanged(position, isChecked);
                        }
                    }
                });

            }
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    getClass().getSimpleName() +" requires the resource ID to be a TextView", e);
        }
        //Log.i(TAG, "item="+item);
        if (item != null) {
            if (tvTask != null) {
                tvTask.setText(item.getTask());
                if(item.isDone()) {
                    tvTask.setPaintFlags(tvTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvTask.setPaintFlags(tvTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
            if (cbDone != null)
            {
                mInternal = true;
                cbDone.setChecked(item.isDone());
                mInternal = false;
            }
        }
        return view;
    }
}
