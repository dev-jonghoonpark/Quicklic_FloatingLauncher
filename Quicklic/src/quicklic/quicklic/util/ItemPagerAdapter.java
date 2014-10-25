/**
 * 도움 by Kylee
 */
package quicklic.quicklic.util;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

public class ItemPagerAdapter extends PagerAdapter {

	private ArrayList<FrameLayout> quicklicPageArrayList;
	private int pagerCount = 0;

	public ItemPagerAdapter(Context context, int pagerCount, ArrayList<FrameLayout> quicklicPageArrayList)
	{
		super();

		this.pagerCount = pagerCount;
		this.quicklicPageArrayList = quicklicPageArrayList;
	}

	@Override
	public int getCount()
	{
		return pagerCount;
	}

	@Override
	public boolean isViewFromObject( View view, Object object )
	{
		return view == object;
	}

	@Override
	public Object instantiateItem( View view, int position )
	{
		((ViewPager) view).addView(quicklicPageArrayList.get(position), 0);

		return quicklicPageArrayList.get(position);
	}

	@Override
	public void destroyItem( View view, int position, Object object )
	{
		((ViewPager) view).removeView((View) object);
	}

	@Override
	public void restoreState( Parcelable parcelable, ClassLoader classLoader )
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate( View view )
	{
	}

	@Override
	public void finishUpdate( View view )
	{
	}
}
