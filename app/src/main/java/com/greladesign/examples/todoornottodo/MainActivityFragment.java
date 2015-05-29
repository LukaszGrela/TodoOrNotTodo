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
import android.widget.ToggleButton;

import com.greladesign.examples.todoornottodo.squidb.SupportSquidCursorLoader;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosAdapter;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Criterion;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;

import static com.greladesign.examples.todoornottodo.MainActivityFragment.FilterOption.ACTIVE;
import static com.greladesign.examples.todoornottodo.MainActivityFragment.FilterOption.ALL;
import static com.greladesign.examples.todoornottodo.MainActivityFragment.FilterOption.COMPLETED;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<Todo>> {


    private static final int LOADER_ID_ALL = 1;
    private static final int LOADER_ID_ACTIVE = 2;
    private static final int LOADER_ID_COMPLETED = 3;

    private static final String TAG = "MainActivityFragment";
    private ListView mList;
    private TodosAdapter.TodoRowActionListener mTodoRowActionListener = new TodosAdapter.TodoRowActionListener() {
        @Override
        public void onCompletionChanged(int position, boolean state) {
            Log.i(TAG, "onCompletionChanged(int position="+position+", boolean state="+state+")");
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
    private ToggleButton mBtnFilterCompleted;
    private ToggleButton mBtnFilterActive;
    private ToggleButton mBtnFilterAll;

    public enum FilterOption {
        ALL, ACTIVE, COMPLETED
    }

    public interface MainActivityHandler {
        void onAddTask();
        void onFilterChanged(FilterOption option);
    }

    private MainActivityHandler sDummyCallback = new MainActivityHandler() {
        @Override
        public void onAddTask() {}

        @Override
        public void onFilterChanged(FilterOption option) {}
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
        mCallback = sDummyCallback;
        //mAdapter.setTodoRowActionListener(null);
    }

    private void findViews(View view) {
        mList = (ListView) view.findViewById(R.id.listView);
        Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onAddTask();
                }
            }
        });
        mBtnFilterAll = (ToggleButton) view.findViewById(R.id.btnShowAll);
        mBtnFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(ALL);
                }
                filterList((ToggleButton)view, ALL);
            }
        });
        mBtnFilterActive = (ToggleButton) view.findViewById(R.id.btnShowActive);
        mBtnFilterActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(ACTIVE);
                }
                filterList((ToggleButton)view, ACTIVE);
            }
        });
        mBtnFilterCompleted = (ToggleButton) view.findViewById(R.id.btnShowCompleted);
        mBtnFilterCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(COMPLETED);
                }
                filterList((ToggleButton)view, COMPLETED);
            }
        });
    }

    private void initList() {

        mList.setAdapter(mAdapter);
        filterList(mBtnFilterAll, ALL);
    }

    private void toggleButtons(ToggleButton selected) {
        mBtnFilterAll.setSelected(false);
        mBtnFilterActive.setSelected(false);
        mBtnFilterCompleted.setSelected(false);
        if(selected != null ){
            selected.setSelected(true);
        }
    }

    private void filterList(ToggleButton selected, FilterOption option) {
        toggleButtons(selected);
        final LoaderManager lm = getLoaderManager();
        switch (option) {
            case ALL:
                lm.initLoader(LOADER_ID_ALL, null, this);
                break;
            case ACTIVE:
                lm.initLoader(LOADER_ID_ACTIVE, null, this);
                break;
            case COMPLETED:
                lm.initLoader(LOADER_ID_COMPLETED, null, this);
                break;
        }
    }

    /* INTERFACE LoaderManager.LoaderCallbacks */
    @Override
    public Loader<SquidCursor<Todo>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");

        final Query query = Query.select(Todo.PROPERTIES);

        switch (id){
            case LOADER_ID_ACTIVE:
                query.where(Todo.DONE.eq(false));
                break;
            case LOADER_ID_COMPLETED:
                query.where(Todo.DONE.eq(true));
                break;
            case LOADER_ID_ALL:
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
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<SquidCursor<Todo>> loader) {
        mAdapter.swapCursor(null);
    }
}
