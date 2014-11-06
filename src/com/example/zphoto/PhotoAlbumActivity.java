package com.example.zphoto;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.widget.GridView;

import com.cooby.photoalbum.ContentMediaItemAdapter;
import com.cooby.photoalbum.data.FolderItem;
import com.cooby.photoalbum.data.MediaItem;
import com.cooby.photoalbum.loader.MediaLoader;

public class PhotoAlbumActivity extends Activity{
	/**  The maximum number of folders that are displayed thumbnail*/
	public static final int MAX_THUMBNAIL_CNT = 4;
	
	GridView mGridView;
	ContentMediaItemAdapter cAdapter;
	
	protected static ArrayList<FolderItem> sFolderItems = new ArrayList<FolderItem>();
	
	protected static List<MediaItem> dataItems = new ArrayList<MediaItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sd_card_media_folder_preview);
		
		mGridView = (GridView) findViewById(R.id.media_in_folder_gv);
		cAdapter=new ContentMediaItemAdapter(PhotoAlbumActivity.this,dataItems);
		mGridView.setAdapter(cAdapter);
		refreshMedia();
	}
	

	public void refreshMedia() {
		sFolderItems.clear();
		dataItems.clear();
		ContentResolver cr = getContentResolver();
		ArrayList<FolderItem> list =   MediaLoader.getFolderItems(cr);
		if(list.size()>0){
			sFolderItems.add(new FolderItem("ËùÓÐÍ¼Æ¬"));
			sFolderItems.addAll(list);
			runImageLoader(cr);
		}
		dataItems.addAll(sFolderItems.get(0).childMedias);
		cAdapter.notifyDataSetChanged();
	}
	
	public static void runImageLoader(ContentResolver cr) {
		FolderItem allfoderItem = sFolderItems.get(0);
		for (int folderIndex = 1; folderIndex < sFolderItems.size(); folderIndex++) {
			FolderItem foderItem = sFolderItems.get(folderIndex);
			ArrayList<MediaItem> items = MediaLoader.getMediaItems(foderItem.getId(), cr);
			foderItem.childMedias.addAll(items);
			allfoderItem.childMedias.addAll(items);
		}
	}
}
