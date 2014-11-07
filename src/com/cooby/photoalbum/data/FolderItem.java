package com.cooby.photoalbum.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The folder that contains the information class
 */
public class FolderItem extends MediaObject implements Parcelable {
	public static final int NEW_FOLDER_ID = 0;
	
    private long mBucketId;

    private String mDisplayName;
	
    private boolean mSelected = false;

	private String mPath;
	
	private int mItemCount;
	
	public List<MediaItem> childMedias = new ArrayList<MediaItem>(); 
	
	
	/**
	 * {@link Cursor} to create a folder items.
	 * @param cursor MediaStore of the cursor
	 */
	public FolderItem(Cursor cursor) {
		mBucketId =  cursor.getLong(0);
		mPath = cursor.getString(1);
		mDisplayName = cursor.getString(2);
	}
	
	/**
	 * Creates a folder with the name of an item.
	 * @param name Create a folder name
	 */
	public FolderItem(String name) {
		mBucketId =  NEW_FOLDER_ID;
		mDisplayName = name;
	}
	
	public FolderItem(String name,boolean selected) {
		mBucketId =  NEW_FOLDER_ID;
		mDisplayName = name;
		mSelected = selected;
	}
	
	/**
	 * {@link Parcel} to create a folder items.
	 * @param in {@link Parcel}
	 */
	public FolderItem(Parcel in) {
		mBucketId = in.readLong();
	    mDisplayName = in.readString();
		mPath = in.readString();
		mItemCount = in.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mBucketId);
		dest.writeString(mDisplayName);
		dest.writeString(mPath);
		dest.writeInt(mItemCount);
    }
	
	/**
	 * Parcel creator
	 */
	public static final Parcelable.Creator<FolderItem> CREATOR = new Creator<FolderItem>() {
        @Override
		public FolderItem createFromParcel(Parcel source) {
        	
            return new FolderItem(source);
        }
        @Override
		public FolderItem[] newArray(int size) {
            return new FolderItem[size];
        }
    };

    @Override
    public long getId() {
        return mBucketId;
    }

    /**
     * Set bucket id
     * @param id the bucket id
     */
    public void setId(long id) {
        this.mBucketId = id;
    }

   
    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Set the display name.
     * @param name display name
     */
    public void setDisplayName(String name) {
        this.mDisplayName = name;
    }

   
	@Override
	public String getPath() {
		File file = new File(mPath);
		if (file.isFile()) {
			return mPath.substring(0, mPath.lastIndexOf("/")+1);
		} else {
			return mPath;
		}
	}
	
	public String getFolderPath() {
		File file = new File(mPath);
		String name = file.getName();
		String path = mPath.replace(name, "");
		return path;
	}
	
	public String getFilePath() {
		return mPath;
	}	

	

	@Override
	public boolean isSelected() {
		return mSelected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.mSelected = selected;
	}

	/**
	 * Media to get the count that is included in the folder.
	 * @return Media count
	 */
	public int getItemCount() {
		return mItemCount;
	}

	/**
	 * Set the media, the number of which is included in the folder.
	 * @param count Media count
	 */
	public void setItemCount(int count) {
		mItemCount = count;
	}

	
	@Override
	public int getType() {
		return FOLDER;
	}

	/**
	 * set the path
	 * @param path  Set the path
	 */
	public void setPath(String path) {
		mPath = path;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
