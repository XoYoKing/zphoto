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
	private Context context;// ����������
	private List<MediaItem> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private MediaManager mediaManager;
	
	static class ListItemView { // �Զ���ؼ�����
		public ImageView media_thumb;
	}
	
	public ContentMediaItemAdapter(Context context, List<MediaItem> data) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
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
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(R.layout.sd_card_media_folder_preview_item, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
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
