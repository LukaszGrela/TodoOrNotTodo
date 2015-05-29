package com.greladesign.examples.todoornottodo;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.greladesign.examples.todoornottodo.squidb.SupportSquidCursorLoader;
import com.greladesign.examples.todoornottodo.squidb.Todo;
import com.greladesign.examples.todoornottodo.squidb.TodosAdapter;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<Todo>> {


    private static final int LOADER_ID = 1;
    private static final String TAG = "MainActivityFragment";
    private ListView mList;

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
        Button btnFilterAll = (Button) view.findViewById(R.id.btnShowAll);
        btnFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(FilterOption.ALL);
                }
            }
        });
        Button btnFilterActive = (Button) view.findViewById(R.id.btnShowActive);
        btnFilterActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(FilterOption.ACTIVE);
                }
            }
        });
        Button btnFilterCompleted = (Button) view.findViewById(R.id.btnShowCompleted);
        btnFilterCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onFilterChanged(FilterOption.COMPLETED);
                }
            }
        });
    }

    private void initList() {

        mList.setAdapter(mAdapter);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);
    }
    /* INTERFACE LoaderManager.LoaderCallbacks */
    @Override
    public Loader<SquidCursor<Todo>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");

        final Query allTodosQuery = Query.select(Todo.PROPERTIES);
        final TodoOrNotTodo app = (TodoOrNotTodo)getActivity().getApplication();
        final DatabaseDao dao = app.dao();
        final Context context = app.getApplicationContext();
        final SupportSquidCursorLoader<Todo> loader = new SupportSquidCursorLoader<Todo>(context, dao, Todo.class, allTodosQuery);
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
