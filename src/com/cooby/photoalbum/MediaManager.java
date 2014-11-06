package com.cooby.photoalbum;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.cooby.photoalbum.data.MediaItem;
import com.cooby.photoalbum.loader.MediaLoader;
import com.cooby.photoalbum.loader.ThumbnailCache;
/**
 * �첽�̼߳���ͼƬ������
 * ʹ��˵����
 * BitmapManager bmpManager;
 * bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
 * bmpManager.loadBitmap(imageURL, imageView);
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
public class MediaManager {  
	  
    private static ExecutorService pool;  
    private static Map<ImageView, String> imageViews;  
    private Bitmap defaultBmp;  
    
    static {  
        pool = Executors.newFixedThreadPool(5);  //�̶��̳߳�
        imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }  
    
    public MediaManager(){}
    
    public MediaManager(Bitmap def) {
    	this.defaultBmp = def;
    }
    
    /**
     * ����Ĭ��ͼƬ
     * @param bmp
     */
    public void setDefaultBmp(Bitmap bmp) {  
    	defaultBmp = bmp;  
    }   
 
    
    /**
     * ����ͼƬ-��ָ����ʾͼƬ�ĸ߿�
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void loadContentBitmap(MediaItem item, ImageView imageView) {  
        imageViews.put(imageView, item.getId()+"");  
        Bitmap bitmap = ThumbnailCache.INSTANCE.getFolderBitmap(item.getId());
   
        if (bitmap != null) {  
			//��ʾ����ͼƬ
            imageView.setImageBitmap(bitmap);  
        } else {  
    		imageView.setImageBitmap(defaultBmp);
    		queueJob(item, imageView);
        }  
    }  
  
 
    
    /**
     * �������м���ͼƬ
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void queueJob(final MediaItem item, final ImageView imageView) {  
        /* Create handler in UI thread. */  
        final Handler handler = new Handler() {  
            public void handleMessage(Message msg) {  
                String tag = imageViews.get(imageView);  
                if (tag != null && tag.equals(item.getId()+"")) {  
                    if (msg.obj != null) {  
                    	Bitmap result = (Bitmap)msg.obj;
                        imageView.setImageBitmap(result);  
                        ThumbnailCache.INSTANCE.putFolderBitmap(item.getId(), result);
                    } 
                }  
            }  
        };  
  
        pool.execute(new Runnable() {   
            public void run() {  
                Message message = Message.obtain();  
                message.obj = MediaLoader.getThumbnail(item); 
                handler.sendMessage(message);  
            }  
        });  
    } 
}