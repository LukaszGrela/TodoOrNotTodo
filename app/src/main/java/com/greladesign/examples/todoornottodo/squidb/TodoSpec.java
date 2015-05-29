package com.greladesign.examples.todoornottodo.squidb;

import android.net.Uri;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className = "Todo", tableName = "todos")
public class TodoSpec {

    public static final Uri CONTENT_URI = Uri.parse("content://com.greladesign.examples.squidbtodo/todo");

    @ColumnSpec(constraints = "NOT NULL")
    public String task;
    public long date;
    @ColumnSpec(defaultValue = "0")
    public boolean done;
    @ColumnSpec(defaultValue = "1")
    public int priority;

}
