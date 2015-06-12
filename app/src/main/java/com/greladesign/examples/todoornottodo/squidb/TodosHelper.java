package com.greladesign.examples.todoornottodo.squidb;

import android.content.Context;
import android.net.Uri;

import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.UriNotifier;
import com.yahoo.squidb.sql.Criterion;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.SqlTable;
import com.yahoo.squidb.sql.Update;

import java.util.Set;

/**
 * Created by lukasz.grela on 12/06/2015.
 */
public class TodosHelper {
    private static TodosHelper INSTANCE = null;


    private static final Query TASKS_QUERY = Query.select(Todo.PROPERTIES)
            .from(Todo.TABLE)
            .freeze();

    private DatabaseDao mTodosDAO;
    private UriNotifier mNotifier = new UriNotifier() {
        @Override
        public void addUrisToNotify(Set<Uri> uris, SqlTable<?> table, String databaseName, DBOperation operation, AbstractModel modelValues, long rowId) {
            // Notifies some constant Uri for any update on the students table
            if (Todo.TABLE.equals(table)) {
                uris.add(Todo.CONTENT_URI);
            }
        }
    };

    private TodosHelper(Context context) {

        mTodosDAO = new DatabaseDao(new TodosDB(context));
        //register for uri notifications
        mTodosDAO.registerUriNotifier(mNotifier);
    }

    public static TodosHelper getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("Must call init(app context) first");
        }
        return INSTANCE;
    }

    public static TodosHelper init(Context appContext) {
        if (INSTANCE != null) {
            throw new RuntimeException("This method can be called only once!");
        }
        INSTANCE = new TodosHelper(appContext);
        return INSTANCE;
    }

    public boolean newTask(Todo todo) {
        return mTodosDAO.createNew(todo);
    }

    public boolean modifyTask(Todo todo) {
        return mTodosDAO.persist(todo);
    }

    /**
     * Returns number of incomplete tasks
     *
     * @return
     */
    public int countIncomplete() {
        return mTodosDAO.count(Todo.class, Todo.DONE.eq(false));
    }

    /**
     * Returns number of completed tasks
     *
     * @return
     */
    public int countComplete() {
        return mTodosDAO.count(Todo.class, Todo.DONE.eq(true));
    }

    /**
     * Updates the Todo with true in the DONE field
     *
     * @param todo
     * @return
     */
    public boolean completeTask(Todo todo) {
        return updateCompletion(todo, true);
    }

    /**
     * Updates the Todo with false in the DONE field
     *
     * @param todo
     * @return
     */
    public boolean reopenTask(Todo todo) {
        return updateCompletion(todo, false);
    }

    public boolean deleteTask(Todo todo) {
        if (todo != null) {
            final int result = mTodosDAO.deleteWhere(Todo.class, Todo.ID.eq(todo.getId()));
            if (result == -1 || result > 1) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int deleteComplete() {
        return mTodosDAO.deleteWhere(Todo.class, Todo.DONE.eq(true));
    }

    public boolean deleteAll() {
        final int results = mTodosDAO.deleteWhere(Todo.class, Criterion.all);
        return results > 0;
    }

    public DatabaseDao dao() {
        return mTodosDAO;
    }

    public boolean updateCompletion(Todo todo, boolean done) {
        if (todo != null) {
            final Criterion criterion = Todo.ID.eq(todo.getId());
            Update updateReopen = Update.table(Todo.TABLE).set(Todo.DONE, done).where(criterion);
            int result = mTodosDAO.update(updateReopen);
            if (result == -1 || result > 1) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Query getIncompleteTasksQuery() {
        return getFilteredTasksQuery(Todo.DONE.eq(false));
    }

    public Query getCompleteTasksQuery() {
        return getFilteredTasksQuery(Todo.DONE.eq(true));
    }

    public Query getFilteredTasksQuery(Criterion filterBy) {
        if (filterBy == null) {
            return TASKS_QUERY
                    .orderBy(Todo.PRIORITY.desc(), Todo.TASK.asc());
        }
        // Since the query is frozen, this will create a clone with the given filter
        return TASKS_QUERY.where(filterBy)
                .orderBy(Todo.PRIORITY.desc(), Todo.TASK.asc());
    }

}
