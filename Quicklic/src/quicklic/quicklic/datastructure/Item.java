/*
 * Favorite 서비스에서 사용되어지며, Quicklic 액티비티에 추가되어지는 어플리케이션의 데이터를 저장
 */

package quicklic.quicklic.datastructure;

import android.graphics.drawable.Drawable;

public class Item {
	private int viewId;
	private int drawResId;
	private Drawable iconDrawable;

	public Item(int viewId, Drawable iconDrawable)
	{
		super();
		this.viewId = viewId;
		this.drawResId = 0;
		this.iconDrawable = iconDrawable;
	}

	public Item(int viewId, int drawResId)
	{
		super();
		this.viewId = viewId;
		this.drawResId = drawResId;
		this.iconDrawable = null;
	}

	public int getViewId()
	{
		return viewId;
	}

	public void setViewId( int viewId )
	{
		this.viewId = viewId;
	}

	public int getDrawResId()
	{
		return drawResId;
	}

	public void setDrawResId( int drawResId )
	{
		this.drawResId = drawResId;
	}

	public Drawable getIconDrawable()
	{
		return iconDrawable;
	}

	public void setIconDrawable( Drawable iconDrawable )
	{
		this.iconDrawable = iconDrawable;
	}

}
