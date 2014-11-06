package com.example.zphoto;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cooby.photoalbum.FolderViewAdapter;
import com.cooby.photoalbum.data.FolderItem;


public class FolderItemPopupWindow implements OnItemClickListener{
	private List<FolderItem> folderList;
    private PopupWindow popupWindow;
    private LayoutInflater mLayoutInflater;
	private ListView listView;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener{
    	public void onItemClick(FolderItem  item);
    }
    
    public void setItemClickListener(ItemClickListener listener){
    	mItemClickListener = listener;
    };
    
    public FolderItemPopupWindow(Context context,List<FolderItem> folderList){
    	this.folderList = folderList;
    	mLayoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = mLayoutInflater.inflate(R.layout.sd_card_media_folder, null);
    	
    	listView=(ListView)view.findViewById(R.id.media_lv);
    	listView.setAdapter(new FolderViewAdapter(context,folderList));
    	listView.setOnItemClickListener(this);
    	
    	popupWindow=new PopupWindow(view,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    
    
    public void showAsDropDown(View view){
    	if(folderList.size()>0)
    	    popupWindow.showAsDropDown(view, 0,3);
	    	// 使其聚集
			popupWindow.setFocusable(true);
			// 设置允许在外点击消失
			popupWindow.setOutsideTouchable(true);
			// 刷新状态
			popupWindow.update();
    }
    
    public void dismiss(){
    	popupWindow.dismiss();
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		for(FolderItem item:folderList){
			item.setSelected(false);
		}
		folderList.get(position).setSelected(true);
		if(mItemClickListener!=null)
			mItemClickListener.onItemClick(folderList.get(position));
		
		dismiss();
	}



}