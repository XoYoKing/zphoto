package com.cooby.photoalbum.data;

import android.net.Uri;
import android.provider.MediaStore;

public class MediaUtils {
	public static final int COL_ID				= 0;
	public static final int COL_DATA 			= 1;
	public static final int COL_DISPLAY_NAME   	= 2;
	public static final int COL_DATE_TAKEN   	= 3;
	public static final int COL_LATITUDE   		= 4;
	public static final int COL_LONGITUDE   	= 5;
	public static final int COL_TITLE		   	= 6;
	public static final int COL_MIME_TYPE		= 7;
	public static final int COL_ORIENTATION   	= 8;
	public static final int COL_DURATION   		= 8;
	public static final int COL_RESOLUTION   	= 9;
	
	
	public static final String[] BUCKET_IMAGE_COLUMNS = {
		MediaStore.Images.ImageColumns.BUCKET_ID,
		MediaStore.Images.ImageColumns.DATA,
		MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME 
		
	};
	
	public static final String[] BUCKET_VIDEO_COLUMNS = {
		MediaStore.Video.VideoColumns.BUCKET_ID,
		MediaStore.Video.VideoColumns.DATA,
		MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME 
	};
	
	public static final String[] IMAGE_COLUMNS = {
		MediaStore.Images.ImageColumns._ID,
		MediaStore.Images.ImageColumns.DATA,
		MediaStore.Images.ImageColumns.DISPLAY_NAME,
		MediaStore.Images.ImageColumns.DATE_TAKEN,
		MediaStore.Images.ImageColumns.LATITUDE,
		MediaStore.Images.ImageColumns.LONGITUDE,
		MediaStore.Images.ImageColumns.TITLE,
		MediaStore.Images.ImageColumns.MIME_TYPE,
		MediaStore.Images.ImageColumns.ORIENTATION
	};

	/**
	 * <p>Get data URI</p>
	 * @param mediaType - Image or Video
	 * @return data URI
	 */
	public static Uri getContentUri(int mediaType) {
		return (mediaType == MediaItem.IMAGE) 
				? MediaStore.Images.Media.EXTERNAL_CONTENT_URI 
				: MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	}
	
	/**
	 * <p>Get data columns</p>
	 * @param mediaType - Image or Video
	 * @return data columns
	 */
	public static String[] getBucketColumns(int mediaType) {
		return (mediaType == MediaItem.IMAGE) 
				? BUCKET_IMAGE_COLUMNS 
				: BUCKET_VIDEO_COLUMNS;
	}

}
