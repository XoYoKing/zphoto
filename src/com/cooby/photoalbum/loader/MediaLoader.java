package com.cooby.photoalbum.loader;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;

import com.cooby.photoalbum.data.FolderItem;
import com.cooby.photoalbum.data.ImageItem;
import com.cooby.photoalbum.data.MediaItem;
import com.cooby.photoalbum.data.MediaUtils;

public class MediaLoader {

	/**
	 * Get folder items.
	 * @param cr ContentResolver
	 * @return folder items ArrayList
	 */
	public static ArrayList<FolderItem> getFolderItems(ContentResolver cr) {
		return MediaLoader.runFolderItemCompare(
				new ArrayList<FolderItem>(getAllMediaIncludeFolder(cr).values()));
	}	
	
	/**
	 * Get all of the folders contained information media.
	 * @return Media folder 
	 */
	private static Map<Long, FolderItem> getAllMediaIncludeFolder(final ContentResolver cr) {
		Map<Long, FolderItem> mediaFolders = getIncludeMediaFolder(MediaItem.IMAGE, cr);
		
		return mediaFolders;
	}
	
	/**
	 * Get the folders that contain the media information.
	 * @param mediaType Media type MediaItem.IMAGE, MediaItem.VIDEO
	 * @param cr ContentResolver
	 * @return Media folder Map
	 */
	public static Map<Long, FolderItem> getIncludeMediaFolder(final int mediaType, final ContentResolver cr) {
		final Map<Long, FolderItem> folders = new HashMap<Long, FolderItem>();
		final Cursor folderCrusor = cr.query(
				MediaUtils.getContentUri(mediaType), 
				MediaUtils.getBucketColumns(mediaType), 
				"", null, "");
		
		if (folderCrusor == null) return folders;
		
		while (folderCrusor.moveToNext()) {
			long bucketId = folderCrusor.getLong(MediaUtils.COL_ID);
			if (folders.get(bucketId) == null) {
				FolderItem folderItem = new FolderItem(folderCrusor);
				
				folderItem.setItemCount(MediaLoader.getItemCount(folderItem.getId(), cr));
				folders.put(bucketId, folderItem);
			}
		}
		
		folderCrusor.close();
		return folders;
	}
	
	 /**
     * bucket_Id to get the number of items.
     * @param bucketId bucket_Id
     * @param cr ContentResolver
     * @return number of items
     */
	public static int getItemCount(long bucketId, ContentResolver cr) {
		int count = 0;
		Cursor cursor = getFolderImageCursor(bucketId, cr);
		if (cursor != null) {
			count += cursor.getCount();
			cursor.close();
		}
		
		return count;
	}
	
	/**
	 * Get the folder image cursor.
	 * @param To get the cursor bucketId
	 * @param cr ContentResolver
	 * @return cursor
	 */
	public static Cursor getFolderImageCursor(long bucketId, final ContentResolver cr) {
		String where = MediaStore.Images.ImageColumns.BUCKET_ID + "=?";
		String whereValue[] = {String.valueOf(bucketId)};
		return  cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaUtils.IMAGE_COLUMNS, where, whereValue, "");
	}
	
	public static ArrayList<FolderItem> runFolderItemCompare(ArrayList<FolderItem> items) {
		Collections.sort(items, COMPARE_FOLDER_NAME);
		return items;
	}
	
	/**
	 * Compared with the name of the folder
	 */
	final static Comparator<FolderItem> COMPARE_FOLDER_NAME = new Comparator<FolderItem>() {

		@Override
		public int compare(FolderItem lhs, FolderItem rhs) {
		    return lhs.getDisplayName().compareTo(rhs.getDisplayName());
		}
	};
	
	/**
	 * bucket id to get media items.
	 * Get its items are sorted.
	 * @param bucketId bucket id
	 * @param cr ContentResolver
	 * @return media items
	 */
	public static ArrayList<MediaItem> getMediaItems(long bucketId, final ContentResolver cr) {
		ArrayList<MediaItem> items = new ArrayList<MediaItem>();
		Cursor imageCursor = getFolderImageCursor(bucketId, cr);
		if (imageCursor != null) {
			while (imageCursor.moveToNext()) {
				items.add(MediaItem.createItem(imageCursor, MediaItem.IMAGE));
			}
			imageCursor.close();
		}
		return items;
	}
	
	/**
	 * Get a thumbnail.
	 * @param item  MediaItem
	 * @return thumbnail
	 */
	public static Bitmap getThumbnail(MediaItem item) {
		Bitmap bm = null;
		BitmapFactory.Options options = ImageConfig.getDefaultThumbOptions();

		Bitmap bitmap = null;
		
		if (ImageConfig.isExitfThumb()) {
			ExifInterface exif = null;
            byte [] thumbData = null;
            try {
                exif = new ExifInterface(item.getPath());
                if (exif != null) {
                    thumbData = exif.getThumbnail();
                }
            } catch (Throwable t) {
                Log.w("Bitmap", "fail to get exif thumb", t);
            }
            
            if (thumbData != null) {
                bitmap = decodeIfBigEnough(thumbData, options, ImageConfig.getThumbSize());
            }
		}
        
		if (bitmap == null) {
			bitmap = decodeThumbnail(item.getPath(), options, ImageConfig.getThumbSize());
		}
        
        if (bitmap == null) return null;

        ImageItem imageItem = (ImageItem) item;
        if (imageItem.isRotation()) {
            bitmap = rotateBitmap(bitmap, imageItem.getRotation(), true);
        }
        
        bm = resizeAndCropCenter(bitmap, 
        		ImageConfig.getThumbCropSizeWidth(), ImageConfig.getThumbCropSizeHight(), true);
            
    	return bm;
	}
	
	 public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
			if (bitmap == null) return null;
			 
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			if (w == size && h == size) return bitmap;

			// scale the image so that the shorter side equals to the target;
			// the longer side will be center-cropped.
			float scale = (float) size / Math.min(w, h);

			Bitmap target = Bitmap.createBitmap(size, size,
					ImageConfig.THUMB_CONFIG);
			int width = Math.round(scale * bitmap.getWidth());
			int height = Math.round(scale * bitmap.getHeight());
			Canvas canvas = new Canvas(target);
			canvas.translate((size - width) / 2f, (size - height) / 2f);
			canvas.scale(scale, scale);
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
			canvas.drawBitmap(bitmap, 0, 0, paint);
			if (recycle) bitmap.recycle();
			return target;
		}
	 
	 public static Bitmap resizeAndCropCenter(Bitmap bitmap, float cw, float ch, boolean recycle) {
			if (bitmap == null) return null;
			 
			float w = bitmap.getWidth();
			float h = bitmap.getHeight();
			if (w == cw && h == ch) return bitmap;

			// scale the image so that the shorter side equals to the target;
			// the longer side will be center-cropped.
         float scale = ch/h;
			if (w < h) scale = cw/w;
			
			Bitmap target = Bitmap.createBitmap((int)cw, (int)ch,
					ImageConfig.THUMB_CONFIG);
			int width = Math.round(scale * bitmap.getWidth());
			int height = Math.round(scale * bitmap.getHeight());
			Canvas canvas = new Canvas(target);
			canvas.translate((cw - width) / 2f, (ch - height) / 2f);
			canvas.scale(scale, scale);
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
			canvas.drawBitmap(bitmap, 0, 0, paint);
			if (recycle) bitmap.recycle();
			return target;
		}
	
	/**
	 * Convert it to a bitmap.
	 * @param path convert file path
	 * @param options Options
	 * @param size file size
	 * @return Bitmap
	 */
	public static Bitmap decodeThumbnail(String path, Options options, int size) {
		 FileInputStream fis = null;
	        try {
	            fis = new FileInputStream(path);
	            FileDescriptor fd = fis.getFD();
	            return decodeThumbnail(fd, options, size);
	        } catch (Exception ex) {
	            return null;
	        } finally {
	            if (fis != null) {
	            	try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }
	}
	

	/**
	 * Convert it to a bitmap.
	 * @param fd FileDescriptor
	 * @param options Options
	 * @param targetSize size
	 * @return Bitmap
	 */
	 public static Bitmap decodeThumbnail(FileDescriptor fd, Options options, int targetSize) {
        if (options == null) options = new Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        int w = options.outWidth;
        int h = options.outHeight;

        // We center-crop the original image as it's micro thumbnail. In this case,
        // we want to make sure the shorter side >= "targetSize".
        float scale = (float) targetSize / Math.min(w, h);
        options.inSampleSize = computeSampleSizeLarger(scale);

        // For an extremely wide image, e.g. 300x30000, we may got OOM when decoding
        // it for TYPE_MICROTHUMBNAIL. So we add a max number of pixels limit here.
        final int MAX_PIXEL_COUNT = 640000; // 400 x 1600
        if ((w / options.inSampleSize) * (h / options.inSampleSize) > MAX_PIXEL_COUNT) {
            options.inSampleSize = computeSampleSize(
                    FloatMath.sqrt((float) MAX_PIXEL_COUNT / (w * h)));
        }

        options.inJustDecodeBounds = false;

        Bitmap result = BitmapFactory.decodeFileDescriptor(fd, null, options);
        if (result == null) return null;

        // We need to resize down if the decoder does not support inSampleSize
        // (For example, GIF images)
        float scale1 = ((float) targetSize) / (Math.min(result.getWidth(), result.getHeight()));

        if (scale <= 0.5) result = resizeBitmapByScale(result, scale1, true);
        return result;
    }
	 
	 public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
	        int width = Math.round(bitmap.getWidth() * scale);
	        int height = Math.round(bitmap.getHeight() * scale);
	        if (width == bitmap.getWidth()
	                && height == bitmap.getHeight()) return bitmap;
	        Bitmap target = Bitmap.createBitmap(width, height, ImageConfig.THUMB_CONFIG);
	        Canvas canvas = new Canvas(target);
	        canvas.scale(scale, scale);
	        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
	        canvas.drawBitmap(bitmap, 0, 0, paint);
	        if (recycle) bitmap.recycle();
	        return target;
	    }
	 
		//Find the min x that 1 / x >= scale
		public static int computeSampleSizeLarger(float scale) {
		    int initialSize = (int) FloatMath.floor(1f / scale);
		    if (initialSize <= 1) return 1;
		
		    return initialSize <= 8
		            ? prevPowerOf2(initialSize)
		            : initialSize / 8 * 8;
		}
		
		//Find the max x that 1 / x <= scale.
		public static int computeSampleSize(float scale) {
		    int initialSize = Math.max(1, (int) FloatMath.ceil(1 / scale));
		    return initialSize <= 8
		            ? nextPowerOf2(initialSize)
		            : (initialSize + 7) / 8 * 8;
		}
		

		 // Returns the next power of two.
	    // Returns the input if it is already power of 2.
	    // Throws IllegalArgumentException if the input is <= 0 or
	    // the answer overflows.
	    public static int nextPowerOf2(int n) {
	        if (n <= 0 || n > (1 << 30)) throw new IllegalArgumentException();
	        n -= 1;
	        n |= n >> 16;
	        n |= n >> 8;
	        n |= n >> 4;
	        n |= n >> 2;
	        n |= n >> 1;
	        return n + 1;
	    }
	
	/**
     * Decodes the bitmap from the given byte array if the image size is larger than the given
     * requirement.
     *
     * Note: The returned image may be resized down. However, both width and height must be
     * larger than the <code>targetSize</code>.
     */
    public static Bitmap decodeIfBigEnough(byte[] data,
            Options options, int targetSize) {
        if (options == null) options = new Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        
//        if (options.outWidth < targetSize || options.outHeight < targetSize) {
//            return null;
//        }
        options.inSampleSize = computeSampleSizeLarger(
                options.outWidth, options.outHeight, targetSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
    
    // This computes a sample size which makes the longer side at least
    // minSideLength long. If that's not possible, return 1.
    public static int computeSampleSizeLarger(int w, int h,
            int minSideLength) {
        int initialSize = Math.max(w / minSideLength, h / minSideLength);
        if (initialSize <= 1) return 1;

        return initialSize <= 8
                ? prevPowerOf2(initialSize)
                : initialSize / 8 * 8;
    }
    
    // Returns the previous power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0
    public static int prevPowerOf2(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return Integer.highestOneBit(n);
    }
	
	public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
        if (rotation == 0) return source;
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
        if (recycle) source.recycle();
        return bitmap;
    }
}
