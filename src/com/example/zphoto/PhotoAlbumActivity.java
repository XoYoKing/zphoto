package com.example.zphoto;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.cooby.photoalbum.ContentMediaItemAdapter;
import com.cooby.photoalbum.data.FolderItem;
import com.cooby.photoalbum.data.MediaItem;
import com.cooby.photoalbum.loader.MediaLoader;
import com.example.zphoto.FolderItemPopupWindow.ItemClickListener;

public class PhotoAlbumActivity extends Activity{
	/**  The maximum number of folders that are displayed thumbnail*/
	public static final int MAX_THUMBNAIL_CNT = 4;
	
	private GridView mGridView;
	private ContentMediaItemAdapter cAdapter;
	
	private static ArrayList<FolderItem> sFolderItems = new ArrayList<FolderItem>();
	
	private static List<MediaItem> dataItems = new ArrayList<MediaItem>();
	
	private LinearLayout media_folder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sd_card_media_folder_preview);
		
		media_folder = (LinearLayout) this.findViewById(R.id.media_folder);
		mGridView = (GridView) findViewById(R.id.media_in_folder_gv);
		cAdapter=new ContentMediaItemAdapter(PhotoAlbumActivity.this,dataItems);
		mGridView.setAdapter(cAdapter);
		
		media_folder.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				FolderItemPopupWindow i = new FolderItemPopupWindow(PhotoAlbumActivity.this,sFolderItems);
				i.setItemClickListener(new ItemClickListener(){
					public void onItemClick(FolderItem item) {
						refreshItem(item.childMedias);
					}
				});
				i.showAsDropDown(media_folder);
			}
		});
		initFolderItem();
	}
	
	public void initFolderItem() {
		ContentResolver cr = getContentResolver();
		ArrayList<FolderItem> list =   MediaLoader.getFolderItems(cr);
		if(list.size()>0){
			sFolderItems.add(new FolderItem("ËùÓÐÍ¼Æ¬",true));
			sFolderItems.addAll(list);
			runImageLoader(cr);
		}
		refreshItem(sFolderItems.get(0).childMedias);
	}
	
	public void refreshItem(List<MediaItem> i) {
		dataItems.clear();
		dataItems.addAll(i);
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
