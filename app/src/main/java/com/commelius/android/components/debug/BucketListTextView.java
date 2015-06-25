package com.commelius.android.components.debug;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.commelius.android.utils.Utils;

public class BucketListTextView extends TextView {
	// ===========================================================
	// Constants
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 
	@SuppressWarnings("unused")
	private static final String TAG = "BucketListTextView";

	// ===========================================================
	// Fields
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 
	final private Context fContext;


	// ===========================================================
	// Constructors
	// ===========================================================
	public BucketListTextView(Context context) {
		super(context);
		fContext = context;
		getBucketList();
	}
	
	public BucketListTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		fContext = context;
		getBucketList();
	}
	
	public BucketListTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		fContext = context;
		getBucketList();
	}


	// ===========================================================
	// Methods
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 
	private void getBucketList() {


		if( isInEditMode() )
		{
			setText("bucket list");
		} else {

			//setText(Utils.getBucketList(fContext).toString());
			setText(Utils.chooseBucketList(fContext, Utils.BucketFlags.ALL).toString());
		}
		
	}

	// ===========================================================
	// Interface implementation
	// ===========================================================

	// ===========================================================
	// Overridden Methods
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
