package com.greladesign.examples.multibutton.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.greladesign.examples.todoornottodo.R;

/**
 * Created by Łukasz 'Severiaan' Grela on 29/05/2015.
 * Based on example from Android User Interface Design book
 */
public class MultiButton extends LinearLayout {


    private static final String TAG = "MultiButton";

    private AttributeSet mAttrs = null;
    private Context mContext = null;
    private int mLabelsResourceId = -1;
    private CharSequence[] mLabels = null;

    private Button mSelectedButton;
    private OptionsChangedListener mListener;


    public interface OptionsChangedListener {
        void onOptionChanged(int btnId);
    }


    public MultiButton(Context context, int labelsArrayResourceId) {
        super(context);
        init(context, null, labelsArrayResourceId);
    }

    public MultiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "MultiButton(Context context, AttributeSet attrs)");
        init(context, attrs, -1);
    }

    public MultiButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "MultiButton(Context context, AttributeSet attrs, int defStyle)");
        init(context, attrs, -1);
    }

    public void setOnOptionChangedListener(OptionsChangedListener listener) {
        mListener = listener;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        prepareViews();
    }


    /**
     * Select button with given tag (integer)
     *
     * @param tag
     */
    public void selectButton(int tag) {
        final Button button = (Button) findViewWithTag(tag);
        if (button != null) {
            toggleButtons(button);
        }
    }

    /**
     * Returns index of the selected button or -1 if nothing selected
     *
     * @return
     */
    public int getSelection() {
        if (mSelectedButton != null) {
            return (int) mSelectedButton.getTag();
        }
        return -1;
    }

    private void prepareViews() {
        Log.i(TAG, "prepareViews()");
        Log.i(TAG, "mContext = " + mContext);
        Log.i(TAG, "mAttrs = " + mAttrs);
        Log.i(TAG, "mLabelsResourceId = " + mLabelsResourceId);

        final int count = getChildCount();
        if (isInEditMode() && count == 0) {
            mLabels = new CharSequence[3];
            mLabels[0] = "Label 1(" + count + ")";
            mLabels[1] = "Label 2(" + count + ")";
            mLabels[2] = "Label 3(" + count + ")";
        }
        //
        if (mLabels == null && count == 0) {
            //no children from XML and no labels - error
            throw new InflateException("MultiButton requires a labels or children set in layout XML");
        } else if (count == 0 && mLabels != null) {
            //no children from XML but there is a labels list - create buttons
            final LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
            //
            for (int i = 0; i < mLabels.length; i++) {
                final CharSequence label = mLabels[i];
                final Button button = new Button(mContext);
                addView(button);
                button.setOnClickListener(mButtonClicks);
                button.setTag(i);
                button.setText(label);
                button.setLayoutParams(params);
            }
        } else {
            //static (through layout XML) - validate if TAG's in place if not add and sent warning
            for (int j = 0; j < count; j++) {
                try {
                    final Button button = (Button) getChildAt(j);
                    button.setOnClickListener(mButtonClicks);//attach listener
                    try {
                        final int tag = Integer.parseInt((String) button.getTag());//it will be a string as it is inflated from XML
                        button.setTag(tag);
                    } catch (NumberFormatException nfe) {
                        throw new InflateException("MultiButton children (Button's) need the tag to be set as an Integer. Button '" + button.getText() + "' doesn't have the tag or it is not an int.");
                    }
                } catch (ClassCastException e) {
                    /* ignore non-buttons */
                }
            }
        }
        //auto select first
        selectButtonByTag(0);
    }

    private void init(Context context, AttributeSet attrs, int labelsArrayResourceId) {
        Log.i(TAG, "init(Context context, AttributeSet attrs, int labelsArrayResourceId)");
        mContext = context;
        mAttrs = attrs;
        mLabelsResourceId = labelsArrayResourceId;

        if (mAttrs == null) {
            if (mLabelsResourceId != -1)
                mLabels = mContext.getResources().getTextArray(mLabelsResourceId);
        } else {
            final TypedArray customAttrs = mContext.obtainStyledAttributes(mAttrs, R.styleable.MultiButton);
            mLabels = customAttrs.getTextArray(R.styleable.MultiButton_labels);
            customAttrs.recycle();
        }
    }

    private void toggleButtons(Button selected) {
        Log.i(TAG, "toggleButtons");
        mSelectedButton = null;
        final int length = getChildCount();
        for (int i = 0; i < length; i++) {
            try {
                final Button button = (Button) getChildAt(i);
                button.setEnabled(true);
                if (button == selected) {
                    mSelectedButton = button;
                    mSelectedButton.setEnabled(!true);
                }
            } catch (ClassCastException e) {
                    /* ignore non-buttons */
            }
        }
    }

    private void selectButtonByTag(int tag) {
        final Button selected = (Button) findViewWithTag(tag);
        if (selected != null) {
            toggleButtons(selected);
        }
    }

    //--------------------------
//  View.OnClickListener
//-----------------------------
    private View.OnClickListener mButtonClicks = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleButtons((Button) v);
            if (mListener != null) {
                mListener.onOptionChanged((Integer) v.getTag());
            }
        }
    };
//-----------------------------
//  View.OnClickListener
//--------------------------
}