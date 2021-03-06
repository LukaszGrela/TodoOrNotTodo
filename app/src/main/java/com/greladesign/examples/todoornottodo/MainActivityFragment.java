package com.greladesign.examples.todoornottodo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.greladesign.examples.multibutton.ui.MultiButton;
import com.greladesign.examples.todoornottodo.squidb.SupportSquidCursorLoader;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosAdapter;
import com.greladesign.examples.todoornottodo.squidb.TodosHelper;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;


/**
 * Main activity fragment holds the "filter" buttons, todo list and add/clear buttons
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<Todo>> {


    private static final int LOADER_ID = 1;

    private static final String TAG = "MainActivityFragment";
    private ListView mList;
    private TodosAdapter.TodoRowActionListener mTodoRowActionListener = new TodosAdapter.TodoRowActionListener() {
        @Override
        public void onCompletionChanged(int position, boolean state) {
            Log.i(TAG, "TodoRowActionListener(int position="+position+", boolean state="+state+")");
            final Todo todo = mAdapter.getItem(position);

            if (TodosHelper.getInstance().updateCompletion(todo, state) == false) {
                Log.e(TAG, "Failed to update Todo.");
            }
        }
    };
    private MultiButton mFilterGroup;
    private MultiButton.OptionsChangedListener mFilterOptionChanged = new MultiButton.OptionsChangedListener() {
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
                    return ALL;
            }
        }
    }

    public interface MainActivityHandler {
        void onAddTask();
        void onEditTask(Todo model);
        void onClearTasks();
    }

    private MainActivityHandler sDummyCallback = new MainActivityHandler() {
        @Override
        public void onAddTask() {}

        @Override
        public void onEditTask(Todo model) {}

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
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        //
        final int filterSelectionIndex = mCurrentFilterOption.index;
        mFilterGroup.selectButton(filterSelectionIndex);
        filterList(filterSelectionIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
        mCallback = sDummyCallback;
        unregisterForContextMenu(mList);
    }

    private void findViews(View view) {
        Log.i(TAG, "findViews()");
        mStatus = (TextView) view.findViewById(R.id.tvStatus);
        mStatus.setText("");
        mList = (ListView) view.findViewById(R.id.listView);
        registerForContextMenu(mList);
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

        mFilterGroup = (MultiButton) view.findViewById(R.id.filterGroup);
        mFilterGroup.setOnOptionChangedListener(mFilterOptionChanged);
    }

    private void initList() {
        Log.i(TAG, "initList");
        mList.setAdapter(mAdapter);
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
        final TodosHelper helper = TodosHelper.getInstance();
        final int count = helper.countIncomplete();
        final String label = count + " item(s) left.";
        Log.i(TAG, label);

        mStatus.setText(label);
        //
        final int completed = helper.countComplete();

        if(completed > 0) {
            mBtnClear.setVisibility(View.VISIBLE);
        } else {
            mBtnClear.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if(v.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            //
            final Todo todo = (Todo)mList.getItemAtPosition(info.position);
            Log.i(TAG, todo.toString());

            final MenuInflater inflater = getActivity().getMenuInflater();
            if(todo.isDone()) {
                inflater.inflate(R.menu.todos_context_done, menu);
            } else {
                inflater.inflate(R.menu.todos_context_undone, menu);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //
        final TodosHelper helper = TodosHelper.getInstance();
        final Todo todo = (Todo)mList.getItemAtPosition(info.position);
        Log.i(TAG, todo.toString());
        final int id = item.getItemId();

        switch (id) {
            case R.id.contextmenu_delete:
                helper.deleteTask(todo);
                return true;
            case R.id.contextmenu_edit:
                Log.w(TAG, "TODO edit option");
                if(mCallback != null){
                    mCallback.onEditTask(todo.clone());
                }
                return true;
            case R.id.contextmenu_reopen:
                if (helper.reopenTask(todo) == false) {
                    Log.e(TAG, "Failed to update Todo.");
                }
                return true;
            case R.id.contextmenu_finish:
                if (helper.completeTask(todo) == false) {
                    Log.e(TAG, "Failed to update Todo.");
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /* INTERFACE LoaderManager.LoaderCallbacks */
    @Override
    public Loader<SquidCursor<Todo>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader("+id+")");

        final TodosHelper helper = TodosHelper.getInstance();
        Query query;
        switch (mCurrentFilterOption){
            case ACTIVE://not done
                query = helper.getIncompleteTasksQuery();
                break;
            case COMPLETED://done
                query = helper.getCompleteTasksQuery();
                break;
            case ALL:
            default:
                query = helper.getFilteredTasksQuery(null);
                break;
        }

        final TodoOrNotTodo app = (TodoOrNotTodo)getActivity().getApplication();
        final Context context = app.getApplicationContext();
        final SupportSquidCursorLoader<Todo> loader = new SupportSquidCursorLoader<Todo>(context, TodosHelper.getInstance().dao(), Todo.class, query);
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
