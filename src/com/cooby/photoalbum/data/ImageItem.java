package com.cooby.photoalbum.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Class that contains information about the image
 *
 */
public class ImageItem extends MediaItem {
	int mOrientation;
	int mThumbnailOrientation;
	
	boolean mIsSAM;
	boolean mInitSAM;

	/**
	 * {@link Cursor} to create a image items.
	 * @param cursor MediaStore of the cursor
	 */
	public ImageItem(Cursor cursor) {
		super(cursor);
		
		mOrientation = cursor.getInt(MediaUtils.COL_ORIENTATION);
		mThumbnailOrientation = cursor.getInt(MediaUtils.COL_ORIENTATION);
	}
	
	/**
	 * only view Item from web
	 * @param path The file path
	 */
	public ImageItem(String path) {
		super(path);
	}
	
	/**
	 * {@link Parcel} to create a image items.
	 * @param in {@link Parcel}
	 */
	public ImageItem(Parcel in) {
		super(in);
		
		mOrientation = in.readInt();
		mThumbnailOrientation = in.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(mOrientation);
		dest.writeInt(mThumbnailOrientation);
    }
	
	/**
	 * Parcel creator
	 */
	public static final Parcelable.Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
		public MediaItem createFromParcel(Parcel source) {
        	
            return new ImageItem(source);
        }
        @Override
		public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
	
    private int getThumbnailOrientation() {
        return mThumbnailOrientation;
    }

    private void setThumbnailOrientation(int thumbnailOrientation) {
        this.mThumbnailOrientation = thumbnailOrientation;
    }

	
	@Override
	public int getType() {
		return IMAGE;
	}
	
	/**
	 * get the image orientation value
	 * @return orientation value
	 */
	public int getOrientation() {
		return mOrientation;
	}
	
	/**
	 * get the image rotation value
	 * @return orientation value
	 */
	public int getRotation() {
        return mOrientation;
	}
	
	/**
	 * Set the image rotate value
	 * @param rotation rotate value
	 */
	public void setRotation(int rotation) {
		mOrientation = rotation;
	}

	/**
	 * check the rotation image
	 * @return if rotation image is true Otherwise false
	 */
	public boolean isRotation() {
		return (mOrientation == 180 || mOrientation == 90 || mOrientation == 270);
	}

	

	
	/**
	 * check the sam file init 
	 * @return init if true Otherwise false 
	 */
	public boolean isInitSam() {
		return mInitSAM;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
}
