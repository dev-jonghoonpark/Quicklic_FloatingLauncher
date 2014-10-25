package quicklic.quicklic.favorite;

import java.util.List;

import quicklic.floating.api.R;
import quicklic.quicklic.util.ViewHolder;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ApkAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private int layout;
	private List<PackageInfo> packageList;
	private PackageManager packageManager;

	public ApkAdapter(Context context, int layout, List<PackageInfo> packageList, PackageManager packageManager)
	{
		super();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
		this.packageList = packageList;
		this.packageManager = packageManager;
	}

	public int getCount()
	{
		return packageList.size();
	}

	public Object getItem( int position )
	{
		return packageList.get(position);
	}

	public long getItemId( int position )
	{
		return position;
	}

	/**
	 * @함수명 : getView
	 * @매개변수 : int position, View convertView, ViewGroup parent
	 * @기능(역할) : ListView에 추가 될 view의 정의 (Package Label과 Package Icon 추가)
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	public View getView( int position, View convertView, ViewGroup parent )
	{
		TextView apkName;

		if ( convertView == null )
		{
			convertView = inflater.inflate(layout, parent, false);
		}

		apkName = ViewHolder.get(convertView, R.id.appname_TextView);

		PackageInfo packageInfo = (PackageInfo) getItem(position);

		Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
		String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
		appIcon.setBounds(0, 0, 80, 80);
		appIcon.setDither(true);

		apkName.setCompoundDrawables(appIcon, null, null, null);
		apkName.setCompoundDrawablePadding(5);
		apkName.setText(appName);

		return convertView;
	}
}