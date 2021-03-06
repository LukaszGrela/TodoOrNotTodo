package com.commelius.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;

public class Utils {
	// ===========================================================
	// Constants
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	public static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 
	@SuppressWarnings("unused")
	private static final String TAG = "Utils";
	// ===========================================================
	// Fields
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	public static final SimpleDateFormat utcFormat;
	static {
		utcFormat = new SimpleDateFormat(UTC_FORMAT);
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	//   - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - PUBLIC - 
	/**
	 * Returns the UTC formatted (yyyy-MM-dd'T'HH:mm:ss.SSS'Z') string from
	 * "now" Date
	 * 
	 * @return
	 */
	public static String getUtcNowString() {
		return utcFormat.format(new Date());
	}

	/**
	 * Create hash with SHA512 algorithm of the given string.
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static String sha512(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(s.getBytes());
		byte[] bytes = md.digest();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16)
					.substring(1);
			buffer.append(tmp);
		}
		return buffer.toString();
	}

	/**
	 * Returns list of specifiers used by the android
	 * 
	 * @param c
	 * @return
	 */
	public static List<String> getBucketList(Context c) {
		if (c == null) {
			throw new IllegalArgumentException(
					"Method getBucketList requires valid context.");
		}

		return chooseBucketList(c, EnumSet.of(BucketFlags.DENSITY, BucketFlags.SCREEN_SIZE, BucketFlags.VERSION_API, BucketFlags.LANGUAGE, BucketFlags.WIDTH, BucketFlags.HEIGHT, BucketFlags.ORIENTATION));
	}

	public enum BucketFlags {
		DENSITY, SCREEN_SIZE, VERSION_API, LANGUAGE, WIDTH, HEIGHT, ORIENTATION, SDK_NAME;

		public static final EnumSet<BucketFlags> ALL = EnumSet.allOf(BucketFlags.class);
	}

	public static List<String> chooseBucketList(Context c, EnumSet<BucketFlags> flags) {
		if (c == null) {
			throw new IllegalArgumentException(
					"Method chooseBucketList requires valid context.");
		}

		ArrayList<String> result = new ArrayList<String>();

		if (flags.contains(BucketFlags.DENSITY)) result.add(getDensityBucket(c));

		if (flags.contains(BucketFlags.SCREEN_SIZE)) result.add(getScreenSizeBucket(c));

		if (flags.contains(BucketFlags.VERSION_API)) result.add(getVersionAPIBucket(c));

		if (flags.contains(BucketFlags.SDK_NAME))
			result.add(sdkToName(android.os.Build.VERSION.SDK_INT));

		if (flags.contains(BucketFlags.LANGUAGE)) result.add(getLanguageBucket(c));

		if (flags.contains(BucketFlags.WIDTH)) result.add(getWidthBucket(c));

		if (flags.contains(BucketFlags.HEIGHT)) result.add(getHeightBucket(c));

		if (flags.contains(BucketFlags.ORIENTATION)) result.add(getOrientationBucket(c));

		return result;
	}


	/**
	 * Converts the SDK_INT into the String code name e.g. 3 -> Cupcake
	 * 
	 * @param sdk
	 * @return
	 */
	public static String sdkToName(int sdk) {
		switch (sdk) {
			case 1 /*android.os.Build.VERSION_CODES.BASE*/:
			return "Base";
			case 2 /*android.os.Build.VERSION_CODES.BASE_1_1*/:
			return "Base 1.1";
			case 3 /*android.os.Build.VERSION_CODES.CUPCAKE*/:
			return "Cupcake";
			case 10000 /*android.os.Build.VERSION_CODES.CUR_DEVELOPMENT*/:
			return "Android(Current Development)";
			case 4 /*android.os.Build.VERSION_CODES.DONUT*/:
			return "Donut";
			case 5 /*android.os.Build.VERSION_CODES.ECLAIR*/:
			return "Eclair";
			case 6 /*android.os.Build.VERSION_CODES.ECLAIR_0_1*/:
			return "Eclair 2.0.1";
			case 7 /*android.os.Build.VERSION_CODES.ECLAIR_MR1*/:
			return "Eclair 2.1";
			case 8 /*android.os.Build.VERSION_CODES.FROYO*/:
			return "Froyo";
			case 9 /*android.os.Build.VERSION_CODES.GINGERBREAD*/:
			return "Gingerbread";
			case 10 /*android.os.Build.VERSION_CODES.GINGERBREAD_MR1*/:
			return "Gingerbread 2.3.3";
			case 11 /*android.os.Build.VERSION_CODES.HONEYCOMB*/:
			return "Honeycomb";
			case 12 /*android.os.Build.VERSION_CODES.HONEYCOMB_MR1*/:
			return "Honeycomb 3.1";
			case 13 /*android.os.Build.VERSION_CODES.HONEYCOMB_MR2*/:
			return "Honeycomb 3.2";
			case 14 /*android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH*/:
			return "Ice Cream Sandwich";
			case 15 /*android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1*/:
			return "Ice Cream Sandwich 4.0.3";
			case 16 /*android.os.Build.VERSION_CODES.JELLY_BEAN*/:
			return "Jelly Bean";
			case 17 /*android.os.Build.VERSION_CODES.JELLY_BEAN_MR1*/:
			return "Jelly Bean 4.2";
			case 18 /*android.os.Build.VERSION_CODES.JELLY_BEAN_MR2*/:
			return "Jelly Bean 4.3";
			case 19 /*android.os.Build.VERSION_CODES.KITKAT*/:
				return "KitKat 4.4";
			case 20 /*android.os.Build.VERSION_CODES.KITKAT_WATCH*/:
				return "KitKat Watch 4.4";
			case 21 /*android.os.Build.VERSION_CODES.LOLLIPOP*/:
				return "Lollipop 5.0";
			case 22 /*android.os.Build.VERSION_CODES.LOLLIPOP_MR1*/:
				return "Lollipop 5.1";
		default:
			return "Android(unknown)";
		}
	}

	
	/**
	 * Returns true when the date is between the from and to dates as in (from <= date < to)
	 * 
	 * @param date
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean isDateInRange(Date date, Date from, Date to) {
		long date_ms = date.getTime();
		return (from.getTime() <= date_ms && date_ms < to.getTime());
	}
	
	
	//  PROTECTED - PROTECTED - PROTECTED - PROTECTED - PROTECTED 
	//  PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE - PRIVATE 
	private static String getOrientationBucket(Context c) {
		switch (c.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			return "land";
		case Configuration.ORIENTATION_PORTRAIT:
			return "port";
		}
		return "";
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private static String getWidthBucket(Context c) {
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return "w" + size.x;
		} else {
			return "w" + display.getWidth();
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private static String getHeightBucket(Context c) {
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int sdkInt = android.os.Build.VERSION.SDK_INT;

		if (sdkInt >= 13/*android.os.Build.VERSION_CODES.HONEYCOMB_MR2*/) {
			Point size = new Point();
			display.getSize(size);
			return "h" + size.y;
		} else {
			return "h" + display.getHeight();
		}
	}

	private static String getLanguageBucket(Context c) {
		String locale = c.getResources().getConfiguration().locale
				.getLanguage();
		return locale;
	}

	private static String getVersionAPIBucket(Context c) {
		return "v" + android.os.Build.VERSION.SDK_INT;
	}

	private static String getScreenSizeBucket(Context c) {
		int screenSize = c.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			return "xlarge";
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return "large";
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return "normal";
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return "small";
		}
		return "";
	}

	private static String getDensityBucket(Context c) {
		int density = c.getResources().getDisplayMetrics().densityDpi;

		switch (density) {
		case DisplayMetrics.DENSITY_LOW:
			return "ldpi";
		case DisplayMetrics.DENSITY_MEDIUM:
			return "mdpi";
		case DisplayMetrics.DENSITY_HIGH:
			return "hdpi";
		case DisplayMetrics.DENSITY_XHIGH:
			return "xhdpi";
		case DisplayMetrics.DENSITY_XXHIGH:
			return "xxhdpi";
		case DisplayMetrics.DENSITY_XXXHIGH:
			return "xxxhdpi";
		}
		return "density:" + density;
	}

	// ===========================================================
	// Interface implementation
	// ===========================================================

	// ===========================================================
	// Overridden Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
