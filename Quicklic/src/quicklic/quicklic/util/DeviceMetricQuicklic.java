package quicklic.quicklic.util;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceMetricQuicklic extends Service {

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	private Display windowDisplay;
	private int deviceWidth;
	private int deviceHeight;

	@Override
	public IBinder onBind( Intent arg0 )
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		displayMetrics();
		createLayoutParams();
		super.onCreate();
	}

	/**
	 * @함수명 : onConfigurationChanged
	 * @매개변수 : Configuration newConfig
	 * @기능(역할) : 화면 회전시 레이아웃 비율 유지
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 21.
	 */
	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		displayMetrics();
		createLayoutParams();
		super.onConfigurationChanged(newConfig);
	}

	protected int getOrientation()
	{
		return windowDisplay.getRotation();
	}

	/**
	 * @함수명 : getDeviceWidth
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : device 가로길이 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	protected int getDeviceWidth()
	{
		return deviceWidth;
	}

	/**
	 * @함수명 : getDeviceHeight
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : device 세로길이 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	protected int getDeviceHeight()
	{
		return deviceHeight;
	}

	/**
	 * @함수명 : getUWindowManager
	 * @매개변수 :
	 * @반환 : WindowManager
	 * @기능(역할) : windowManager 객체 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 29.
	 */
	protected WindowManager getUWindowManager()
	{
		return windowManager;
	}

	/**
	 * @함수명 : displayMetrics
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 사용자의 Display 사이즈 정보 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	protected void displayMetrics()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;
	}

	private void createLayoutParams()
	{
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				PixelFormat.RGBA_8888);

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.width = deviceWidth;
		layoutParams.height = deviceHeight;
	}

	public WindowManager.LayoutParams getLayoutParams()
	{
		return layoutParams;
	}

	public WindowManager getWindowManager()
	{
		return windowManager;
	}
}
