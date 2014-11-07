package com.cooby.photoalbum;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooby.photoalbum.data.FolderItem;
import com.example.zphoto.R;

public class FolderViewAdapter extends BaseAdapter{
	private Context context;// 运行上下文
	private List<FolderItem> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private MediaManager mediaManager;
	
	static class ListItemView { // 自定义控件集合
		public ImageView folder_thumb;
		public TextView folder_name;
		public TextView folder_count;
		public ImageView folder_selected_iv;
	}
	
	public FolderViewAdapter(Context context, List<FolderItem> data) {
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
			convertView = listContainer.inflate(R.layout.sd_card_medial_folder_item, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.folder_thumb = (ImageView) convertView.findViewById(R.id.folder_thumb);
			listItemView.folder_name = (TextView) convertView.findViewById(R.id.folder_name);
			listItemView.folder_count = (TextView) convertView.findViewById(R.id.folder_count);
			listItemView.folder_selected_iv = (ImageView) convertView.findViewById(R.id.folder_selected_iv);
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView) convertView.getTag();
		}
		FolderItem item = listItems.get(position);
		listItemView.folder_count.setVisibility(View.GONE);
		if(item.getItemCount()>0){
			listItemView.folder_count.setVisibility(View.VISIBLE);
			listItemView.folder_count.setText(item.getItemCount()+"");
			mediaManager.loadContentBitmap(item.childMedias.get(0), listItemView.folder_thumb);			
		}
		listItemView.folder_name.setText(item.getDisplayName());
		listItemView.folder_selected_iv.setVisibility(View.GONE);
		if(item.isSelected()){
			listItemView.folder_selected_iv.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

}
