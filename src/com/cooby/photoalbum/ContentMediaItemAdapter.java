package com.cooby.photoalbum;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cooby.photoalbum.data.MediaItem;
import com.example.zphoto.R;

public class ContentMediaItemAdapter extends BaseAdapter{
	private Context context;// 运行上下文
	private List<MediaItem> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private MediaManager mediaManager;
	
	static class ListItemView { // 自定义控件集合
		public ImageView media_thumb;
	}
	
	public ContentMediaItemAdapter(Context context, List<MediaItem> data) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = data;
		this.mediaManager = new MediaManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.pic_thumb_bg));
	}

	
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.sd_card_media_folder_preview_item, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.media_thumb = (ImageView) convertView.findViewById(R.id.media_thumb);
			
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView) convertView.getTag();
		}
		MediaItem item = listItems.get(position);
		mediaManager.loadContentBitmap(item, listItemView.media_thumb);
		
		return convertView;
	}
}
