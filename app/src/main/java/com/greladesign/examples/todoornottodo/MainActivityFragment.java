package com.greladesign.examples.todoornottodo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.greladesign.examples.todoornottodo.squidb.SupportSquidCursorLoader;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosAdapter;
import com.greladesign.examples.todoornottodo.ui.FilterButtons;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Criterion;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<Todo>> {{}


    private static final int LOADER_ID = 1;

    private static final String TAG = "MainActivityFragment";
    private ListView mList;
    private TodosAdapter.TodoRowActionListener mTodoRowActionListener = new TodosAdapter.TodoRowActionListener() {
        @Override
        public void onCompletionChanged(int position, boolean state) {
            Log.i(TAG, "TodoRowActionListener(int position="+position+", boolean state="+state+")");
            final Todo todo = mAdapter.getItem(position);
            if(todo != null) {
                final TodoOrNotTodo app = (TodoOrNotTodo)getActivity().getApplication();
                final DatabaseDao dao = app.dao();
                Criterion criterion = Todo.ID.eq(todo.getId());
                Update update = Update.table(Todo.TABLE).set(Todo.DONE, state).where(criterion);
                final int result = dao.update(update);
                if(result == -1 || result > 1) {
                    Log.e(TAG, "Failed to update Todo, result="+result);
                }
            }
        }
    };
    private FilterButtons mFilterGroup;
    private FilterButtons.OptionsChangedListener mFilterOptionChanged = new FilterButtons.OptionsChangedListener() {
        @Override
        public void onOptionChanged(int btnId) {
            filterList(btnId);
        }
    };
    /**
     * Holds currently selected filter option
     */
    private FilterOption mCurrentFilterOption = FilterOption.ALL;
    private TextView mStatus;
    private Button mBtnClear;

    /**
     * Filter option values
     */
    public enum FilterOption {
        ALL(0), ACTIVE(1), COMPLETED(2);
        private int index;
        private FilterOption(int index){
            this.index = index;
        }

        /**
         * Returns FilterOption matching index assigned to enum or FilterOption.ALL when can't match index
         * @param index
         * @return
         */
        public static FilterOption validate(int index) {
            switch (index){
                case 2:
                    return COMPLETED;
                case 1:
                    return ACTIVE;
                case 0:
                default:
                /* do nothing already set as "all" */
                    return ALL;
            }
        }
    }

    public interface MainActivityHandler {
        void onAddTask();
        void onClearTasks();
    }

    private MainActivityHandler sDummyCallback = new MainActivityHandler() {
        @Override
        public void onAddTask() {}

        @Override
        public void onClearTasks() {

        }
    };
    private MainActivityHandler mCallback = sDummyCallback;
    private TodosAdapter mAdapter;



    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        mAdapter = new TodosAdapter(getActivity());
        mAdapter.setTodoRowActionListener(mTodoRowActionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        findViews(view);
        initList();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach");
        if(!(activity instanceof MainActivityHandler)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks, 'MainActivityHandler' interface.");
        }
        mCallback = (MainActivityHandler) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
        mCallback = sDummyCallback;
    }

    private void findViews(View view) {
        mStatus = (TextView) view.findViewById(R.id.tvStatus);
        mStatus.setText("");
        mList = (ListView) view.findViewById(R.id.listView);
        mBtnClear = (Button) view.findViewById(R.id.btnClear);
        mBtnClear.setVisibility(View.GONE);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onClearTasks();
                }
            }
        });
        Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onAddTask();
                }
            }
        });

        mFilterGroup = (FilterButtons) view.findViewById(R.id.filterGroup);
        mFilterGroup.setOnOptionChangedListener(mFilterOptionChanged);
    }

    private void initList() {
        Log.i(TAG, "initList");
        mList.setAdapter(mAdapter);
        //
        final int filterSelectionIndex = mCurrentFilterOption.index;
        mFilterGroup.selectButton(filterSelectionIndex);
        filterList(filterSelectionIndex);
    }

    private void filterList(int btnId){
        Log.i(TAG, "filterList("+btnId+")");
        final LoaderManager lm = getLoaderManager();
        mCurrentFilterOption = FilterOption.validate(btnId);
        lm.restartLoader(LOADER_ID, null, this);
    }

    /**
     * Update the task's left counter and show/hide the clear completed task button
     */
    private void updateTaskCount() {

        final TodoOrNotTodo app = (TodoOrNotTodo)getActivity().getApplication();
        final DatabaseDao dao = app.dao();

        final int count = dao.count(Todo.class, Todo.DONE.eq(false));
        final String label = count + " item(s) left.";
        Log.i(TAG, label);

        mStatus.setText(label);
        final int completed = dao.count(Todo.class, Todo.DONE.eq(true));

        if(completed > 0) {
            mBtnClear.setVisibility(View.VISIBLE);
        } else {
            mBtnClear.setVisibility(View.GONE);
        }
    }

    /* INTERFACE LoaderManager.LoaderCallbacks */
    @Override
    public Loader<SquidCursor<Todo>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader("+id+")");

        final Query query = Query.select(Todo.PROPERTIES);

        switch (mCurrentFilterOption){
            case ACTIVE://not done
                query.where(Todo.DONE.eq(false));
                break;
            case COMPLETED://done
                query.where(Todo.DONE.eq(true));
                break;
            case ALL:
            default:
                /* do nothing already set as "all" */
                break;
        }

        final TodoOrNotTodo app = (TodoOrNotTodo)getActivity().getApplication();
        final DatabaseDao dao = app.dao();
        final Context context = app.getApplicationContext();
        final SupportSquidCursorLoader<Todo> loader = new SupportSquidCursorLoader<Todo>(context, dao, Todo.class, query);
        loader.setNotificationUri(Todo.CONTENT_URI);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<SquidCursor<Todo>> loader, SquidCursor<Todo> data) {
        Log.i(TAG, "onLoadFinished");
        mAdapter.swapCursor(data);
        updateTaskCount();
    }

    @Override
    public void onLoaderReset(Loader<SquidCursor<Todo>> loader) {
        Log.i(TAG, "onLoaderReset");
        mAdapter.swapCursor(null);
    }
}
