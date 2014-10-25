package quicklic.quicklic.favorite;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.util.BaseQuicklic;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicFavoriteService extends BaseQuicklic {

	private PreferencesManager preferencesManager;
	private PackageManager packageManager;

	private ArrayList<Item> imageArrayList;
	private ArrayList<String> pkgArrayList;

	private boolean delEnabled;
	private boolean isAdded;

	private int item_count;
	private int current_page;

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		setIsMain(false);
		initialize(intent);
		initializeImage();

		return START_NOT_STICKY;
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		resetQuicklic();
	}

	private void initializeImage()
	{
		setCenterView();
		initializeView();
		initializeViewPager();
	}

	/**
	 * @함수명 : resetQuicklic
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 화면을 새로고침
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 21.
	 */
	private void resetQuicklic()
	{
		isAdded = false;

		// 현재 보고 있는 viewPager의 page를 기억
		current_page = getViewPager().getCurrentItem();

		if ( getQuicklicFrameLayout() != null )
		{
			getQuicklicFrameLayout().removeAllViews();
		}

		initializeImage();
	}

	private void initialize( Intent intent )
	{
		preferencesManager = new PreferencesManager(this);
		packageManager = getPackageManager();

		imageArrayList = new ArrayList<Item>();
		pkgArrayList = new ArrayList<String>();

		current_page = intent.getIntExtra("page", 0);
		isAdded = intent.getBooleanExtra("add", false);
	}

	private void initializeView()
	{
		getPreference();
		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	/**
	 * @함수명 : initializeViewPager
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 추가/삭제 모드변경 또는 앱 추가 시 상황에 맞는 page 위치 이동
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 8. 21.
	 */
	private void initializeViewPager()
	{
		// 추가 모드
		if ( !delEnabled )
		{
			// 앱 선택 안함 : 현재 페이지 유지
			if ( !isAdded )
			{
				getViewPager().setCurrentItem(current_page);
			}
			// 앱 선택 : 추가되는 페이지로 이동 (마지막 페이지)
			else
			{
				getViewPager().setCurrentItem(getViewCount());
			}
		}
		// 삭제 모드
		else
		{
			// 현재 페이지 유지
			getViewPager().setCurrentItem(current_page);
		}
	}

	/**
	 * @함수명 : setCenterView
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 삭제모드에 따라서 + / - 를 전환
	 * @작성자 : JHPark, THYang
	 * @작성일 : 2014. 6. 26.
	 */
	private void setCenterView()
	{
		ImageView imageView = new ImageView(this);

		if ( !delEnabled )
			imageView.setBackgroundResource(R.drawable.favorite_add);
		else
			imageView.setBackgroundResource(R.drawable.favorite_delete);

		imageView.setId(0);
		imageView.setOnClickListener(clickListener);
		imageView.setOnLongClickListener(onLongClickListener);

		setCenterView(imageView);
	}

	/**
	 * @함수명 : getPreference
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Favorite에 보여줄 어플리케이션을 모두 가져오기
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 22.
	 */
	private void getPreference()
	{
		pkgArrayList.clear();
		imageArrayList.clear();
		item_count = preferencesManager.getNumPreferences(this);

		for ( int i = 0; i < item_count; i++ )
		{
			String packageName = preferencesManager.getAppPreferences(this, i);
			pkgArrayList.add(packageName);
			try
			{
				Drawable appIcon = packageManager.getApplicationIcon(packageName);
				imageArrayList.add(new Item(i, appIcon));
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @함수명 : stopService
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 현재 서비스 종료
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 21.
	 */
	private void stopService()
	{
		Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteService.class);
		stopService(stopIntent);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			// Center Button Click
			if ( v == getCenterView() )
			{
				if ( !delEnabled )
				{

					getWindowManager().removeView(getDetectLayout());

					if ( isItemFull(item_count) ) // check full count
					{
						Toast.makeText(getApplicationContext(), R.string.err_limited_item_count, Toast.LENGTH_SHORT).show();
					}

					Intent apk = new Intent(QuicklicFavoriteService.this, ApkListActivity.class);
					apk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					apk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					apk.putExtra("page", getViewPager().getCurrentItem());
					startActivity(apk);

					stopService();
				}
			}
			// Application Click
			else
			{
				if ( !delEnabled ) // ADD Mode
				{
					/* 실행할 수 없는 앱을 추가한 상태에서
					 *  앱 실행을 요청했을 때,
					 *  예외처리를 통해 service는 죽지 않으며, 사용자에게 Toast로 알림.
					 */
					try
					{
						// 앱 실행시 Favorite 액티비티 서비스 제거
						String packageName = preferencesManager.getAppPreferences(getApplicationContext(), v.getId());
						Intent runIntent = packageManager.getLaunchIntentForPackage(packageName);
						startActivity(runIntent);

						setFloatingVisibility(true);
						stopService();

						getWindowManager().removeView(getDetectLayout());
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.favorite_run_no, Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					preferencesManager.removeAppPreferences(getApplicationContext(), v.getId());
					resetQuicklic();
				}
			}
		}
	};

	// 추가 / 삭제 모드를 구분
	private OnLongClickListener onLongClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick( View v )
		{
			if ( delEnabled )
			{
				delEnabled = false;
				Toast.makeText(getApplicationContext(), R.string.favorite_disable_delete, Toast.LENGTH_SHORT).show();
			}
			else
			{
				delEnabled = true;
				Toast.makeText(getApplicationContext(), R.string.favorite_enable_delete, Toast.LENGTH_SHORT).show();
			}
			resetQuicklic();
			return true;
		}
	};

}