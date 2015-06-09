package com.greladesign.examples.todoornottodo.squidb;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
            ListRowTodoViewHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(resource, parent, false);
                convertView.setTag(new ListRowTodoViewHolder(convertView));
            }

            holder = (ListRowTodoViewHolder) convertView.getTag();
            final CheckBox cbDone = holder.getDone();
            final TextView tvTask = holder.getTask();
            final ImageView ivPriority = holder.getPriority();

            final Todo item = getItem(position);

            if(cbDone != null) {
                //
                cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mListener != null && !mInternal) {
                            mListener.onCompletionChanged(position, isChecked);
                        }
                    }
                });

            }
            //Log.i(TAG, "item="+item);
            if (item != null) {
                if (tvTask != null) {
                    tvTask.setText(item.getTask());
                    if(item.isDone()) {
                        tvTask.setPaintFlags(tvTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvTask.setTextColor(getContext().getResources().getColor(R.color.task_color_done));
                    } else {
                        tvTask.setPaintFlags(tvTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        tvTask.setTextColor(getContext().getResources().getColor(R.color.task_color_not_done));
                    }
                }
                if (cbDone != null)
                {
                    mInternal = true;
                    cbDone.setChecked(item.isDone());
                    mInternal = false;
                }
                if (ivPriority != null) {
                    if(item.getPriority() == 0) {
                        ivPriority.setImageDrawable(getContext().getResources().getDrawable(R.drawable.todo_low));
                    } else if(item.getPriority() == 1) {
                        ivPriority.setImageDrawable(null/*getContext().getResources().getDrawable(R.drawable.empty)*/);
                    } else if(item.getPriority() == 2) {
                        ivPriority.setImageDrawable(getContext().getResources().getDrawable(R.drawable.todo_high));
                    }


                }
            }
            convertView.setOnCreateContextMenuListener(null);//! - this is the solution for problems with context menu not working

            return convertView;
    }
    private static class ListRowTodoViewHolder {
        private CheckBox mCbDone;
        private TextView mTvTask;
        private ImageView mIvPriority;

        public ListRowTodoViewHolder(View row) {


            prepareDoneCb(row.findViewById(R.id.cbDone));

            prepareTaskTv(row.findViewById(R.id.tvTask));

            preparePriorityIv(row.findViewById(R.id.ivPriority));

        }

        private void prepareDoneCb(View view) {
            try {
                mCbDone = (CheckBox) view;
            } catch (ClassCastException e) {
                throw new IllegalStateException(
                        getClass().getSimpleName() +" requires the resource ID to be a CheckBox", e);
            }
        }
        private void prepareTaskTv(View view) {
            try {
                mTvTask = (TextView) view;
            } catch (ClassCastException e) {
                throw new IllegalStateException(
                        getClass().getSimpleName() +" requires the resource ID to be a TextView", e);
            }
        }
        private void preparePriorityIv(View view) {
            try {
                mIvPriority = (ImageView) view;
            } catch (ClassCastException e) {
                throw new IllegalStateException(
                        getClass().getSimpleName() +" requires the resource ID to be a ImageView", e);
            }
        }

        public CheckBox getDone() {
            return mCbDone;
        }

        public TextView getTask() {
            return mTvTask;
        }

        public ImageView getPriority() {
            return mIvPriority;
        }
    }
}
