package quicklic.quicklic.favorite;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import quicklic.floating.api.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ApkListActivity extends Activity implements OnItemClickListener
{
	private PackageManager packageManager;
	private ApkAdapter apkAdapter;

	private RelativeLayout mainRelativeLayout;
	private ListView apkListView;
	private Button systemButton;
	private Button userButton;

	private List<PackageInfo> packageInstalled;
	private List<PackageInfo> packageSystem;

	private boolean isSystem;
	private boolean isUser;
	private boolean isAdded;

	private PreferencesManager preferencesManager;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_apklist_favorite);

		initialize();
		initializeApkListView();
		changeAppListView(false);
	}

	@Override
	protected void onDestroy()
	{
		super.onPause();
		Intent intent = new Intent(getApplicationContext(), QuicklicFavoriteService.class);
		intent.putExtra("page", getIntent().getIntExtra("page", 0));
		intent.putExtra("add", isAdded);
		startService(intent);
	};

	/**
	 * @함수명 : ApkAsyncTask
	 * @매개변수 : List<PackageInfo>
	 * @기능(역할) : List를 정렬 하는 동안 [불러오는 중...] 다이얼로그 띄워줌
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 26.
	 */
	private class ApkAsyncTask extends AsyncTask<List<PackageInfo>, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(ApkListActivity.this);

		@Override
		protected void onPreExecute()
		{
			Dialog.setMessage(getResources().getString(R.string.favorite_loading));
			Dialog.show();
		}

		@Override
		protected Void doInBackground( List<PackageInfo>... list )
		{
			Collections.sort(list[0], comparator);
			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
			if ( Dialog != null )
			{
				Dialog.dismiss();
			}

			apkAdapter.notifyDataSetChanged();
		}
	}

	private void initialize()
	{
		isSystem = false;
		isUser = false;
		isAdded = false;

		mainRelativeLayout = (RelativeLayout) findViewById(R.id.favorite_main_RelativeLayout);
		apkListView = (ListView) findViewById(R.id.favorite_app_ListView);
		systemButton = (Button) findViewById(R.id.favorite_system_Button);
		userButton = (Button) findViewById(R.id.favorite_user_Button);

		packageManager = getPackageManager();
		preferencesManager = new PreferencesManager(this);

		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (point.x * 0.7f), (int) (point.y * 0.7f));
		mainRelativeLayout.setLayoutParams(params);

		int buttonHeight;
		if ( getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0 )
			buttonHeight = (int) (point.x * 0.12f);
		else
			buttonHeight = (int) (point.y * 0.12f);

		systemButton.setHeight(buttonHeight);
		userButton.setHeight(buttonHeight);

		systemButton.setEnabled(true);
		userButton.setEnabled(false);

		systemButton.setOnClickListener(clickListener);
		userButton.setOnClickListener(clickListener);
	}

	/**
	 * @함수명 : initializeApkListView
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 디바이스에 설치된 Package를 가져와서 분류
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	private void initializeApkListView()
	{
		List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		packageInstalled = new ArrayList<PackageInfo>();
		packageSystem = new ArrayList<PackageInfo>();

		for ( PackageInfo packageInfo : packageList )
		{
			if ( !isSystemPackage(packageInfo) )
			{
				packageInstalled.add(packageInfo);
			}
			else
			{
				packageSystem.add(packageInfo);
			}
		}
	}

	// Comparator : 파일명으로 List를 정렬하기 위해 사용되는 비교자
	private Comparator<PackageInfo> comparator = new Comparator<PackageInfo>()
	{
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare( PackageInfo object1, PackageInfo object2 )
		{
			String firstString = packageManager.getApplicationLabel(object1.applicationInfo).toString();
			String secondString = packageManager.getApplicationLabel(object2.applicationInfo).toString();
			return collator.compare(firstString, secondString);
		}
	};

	/**
	 * @함수명 : changeAppListView
	 * @매개변수 : boolean isApp
	 * @반환 : void
	 * @기능(역할) : System App과 User App을 카테고리로 나누어 리스트 전환 (처음 불러올 때만 정렬) / isApp이 true면 System App, false면 User App이다.
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 26.
	 */
	@SuppressWarnings("unchecked")
	private void changeAppListView( boolean isApp )
	{
		if ( isApp )
		{
			if ( !isSystem )
			{
				new ApkAsyncTask().execute(packageSystem);
				isSystem = true;
			}
			apkAdapter = new ApkAdapter(this, R.layout.apklist_item, packageSystem, packageManager);
		}
		else
		{
			if ( !isUser )
			{
				new ApkAsyncTask().execute(packageInstalled);
				isUser = true;
			}
			apkAdapter = new ApkAdapter(this, R.layout.apklist_item, packageInstalled, packageManager);
		}
		apkListView.setAdapter(apkAdapter);
		apkListView.setOnItemClickListener(this);
	}

	/**
	 * @함수명 : isSystemPackage
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : System Application인지 판별
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 2.
	 */
	private boolean isSystemPackage( PackageInfo pkgInfo )
	{
		return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long row )
	{
		PackageInfo packageInfo = (PackageInfo) parent.getItemAtPosition(position);
		isAdded = preferencesManager.setPreference(packageInfo.packageName, getApplicationContext());

		finish();
	}

	OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			if ( v == userButton )
			{
				userButton.setTextColor(Color.parseColor("#ff00B4CC"));
				systemButton.setTextColor(Color.WHITE);

				userButton.setBackgroundColor(Color.WHITE);
				systemButton.setBackgroundColor(Color.parseColor("#ff00B4CC"));

				userButton.setEnabled(false);
				systemButton.setEnabled(true);

				changeAppListView(false);
			}
			else if ( v == systemButton )
			{
				userButton.setTextColor(Color.WHITE);
				systemButton.setTextColor(Color.parseColor("#ff00B4CC"));

				userButton.setBackgroundColor(Color.parseColor("#ff00B4CC"));
				systemButton.setBackgroundColor(Color.WHITE);

				userButton.setEnabled(true);
				systemButton.setEnabled(false);

				changeAppListView(true);
			}
		}
	};

}