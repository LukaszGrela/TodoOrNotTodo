package com.greladesign.examples.todoornottodo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.greladesign.examples.todoornottodo.R;

/**
 * Created by Łukasz 'Severiaan' Grela on 29/05/2015.
 * Based on example from Android User Interface Design book
 */
public class FilterButtons extends LinearLayout implements View.OnClickListener {


    private static final String TAG = "FilterButtons";
    private Button mSelectedButton;
    private OptionsChangedListener mListener;


    public interface OptionsChangedListener {
        void onOptionChanged(int btnId);
    }




    public FilterButtons(Context context, int labelsArrayResourceId) {
        super(context);
        init(context, null, labelsArrayResourceId);
    }
    public FilterButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }
    public FilterButtons(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context, attrs, -1);
    }

    public void setOnOptionChangedListener(OptionsChangedListener listener){
        mListener = listener;
    }

    /**
     * Select button with given index
     * @param i
     */
    public void selectButton(int i) {
        final Button button = (Button) findViewWithTag(i);
        if(button !=null)
        {
            toggleButtons(button);
        }
    }

    /**
     * Returns index of the selected button or -1 if nothing selected
     * @return
     */
    public int getSelection() {
        if (mSelectedButton != null) {
            return (int) mSelectedButton.getTag();
        }
        return -1;
    }

    private void init(Context context, AttributeSet attrs, int labelsArrayResourceId) {
        final CharSequence[] labels;
        if (attrs == null) {
            labels = context.getResources().getTextArray(labelsArrayResourceId);
        } else {
            final TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.FilterButtons);
            labels = customAttrs.getTextArray(R.styleable.FilterButtons_labels);
            customAttrs.recycle();
        }
        //
        if(labels == null) {
            throw new InflateException("FilterButtons requires a labels");
        }
        //
        for(int i=0; i<3; i++){
            final CharSequence label = labels[i];
            final Button button = new Button(context);
            addView(button);
            button.setOnClickListener(this);
            button.setTag(i);
            button.setText(label);

            if(i==0){
                mSelectedButton = button;
                mSelectedButton.setEnabled(!true);
            }
        }
    }

    private void toggleButtons(Button selected) {
        Log.i(TAG, "toggleButtons");
        for(int i=0; i<3; i++){
            final Button button = (Button) getChildAt(i);
            button.setEnabled(true);
            if(button == selected){
                mSelectedButton = button;
                mSelectedButton.setEnabled(!true);
            }
        }
    }
//--------------------------
//  View.OnClickListener
//-----------------------------
    @Override
    public void onClick(View view) {
        toggleButtons((Button) view);
        if(mListener!=null) {
            mListener.onOptionChanged((Integer) view.getTag());
        }
    }
//-----------------------------
//  View.OnClickListener
//--------------------------
}
