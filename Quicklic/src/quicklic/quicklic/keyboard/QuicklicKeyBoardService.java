package quicklic.quicklic.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import quicklic.floating.api.R;
import quicklic.quicklic.main.QuicklicMainService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QuicklicKeyBoardService extends Service {

	private static final int DOUBLE_PRESS_INTERVAL = 300;
	private static final int LIMITED_MOVE_DISTANCE = 10;
	private static float KEY_HEIGHT_RATE;
	private static float KEY_WIDTH_RATE;
	private static final float VIEW_ALPHA = 0.8f;
	private static final int MAX_TASK_NUM = 300;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	private PackageManager packageManager;

	private LinearLayout keyboardLinearLayout;
	private FrameLayout leftIconFrameLayout;
	private FrameLayout rightIconFrameLayout;

	private int keyboardHeight;
	private int keyboardWidth;
	private int deviceWidth;
	private int deviceHeight;

	private Button leftButton;
	private Button rightButton;
	private Button moveButton;
	private Button exitButton;

	private Intent intent;

	private boolean isDoubleClicked = false;
	private boolean isMoved = false;
	private Timer timer;
	private long lastPressTime;

	private ActivityManager activityManager;
	private ArrayList<String> packageArrayList;
	private ArrayList<String> tempArrayList;
	private int packageIndex;
	private int appCount;

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		displayMetrics();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		// 이미 실행중인 Service 가 있다면, 추가 수행 금지
		if ( startId == 1 || flags == 1 )
		{
			try
			{
				initialize(intent);
				createManager();
				displayMetrics();
				createKeyBoard();
				getRunningTaskList();
				resetKeyBoard();
				addViewInWindowManager();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			stopService(intent);
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( keyboardLinearLayout != null )
			windowManager.removeView(keyboardLinearLayout);
	}

	private void displayMetrics()
	{
		Display windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		// Device의 Display에서 width와 height 구하기
		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;

		// 화면 회전의 방향에 따른 floating resize
		if ( windowDisplay.getRotation() == Surface.ROTATION_0 )
		{
			KEY_HEIGHT_RATE = 0.125f;
			KEY_WIDTH_RATE = 0.25f;
		}
		else
		{
			KEY_HEIGHT_RATE = 0.07f;
			KEY_WIDTH_RATE = 0.14f;
		}

		keyboardHeight = (int) (deviceWidth * KEY_HEIGHT_RATE) << 1;
		keyboardWidth = (int) (deviceWidth * KEY_WIDTH_RATE);
	}

	private void initialize( Intent intent )
	{
		this.intent = intent;
		timer = new Timer();

		packageManager = getPackageManager();
		packageArrayList = new ArrayList<String>();
		packageIndex = 0;
		appCount = 0;

		KEY_HEIGHT_RATE = 0.125f;
		KEY_WIDTH_RATE = 0.25f;
	}

	private void createManager()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	}

	private void addViewInWindowManager()
	{
		/* WindowManager.LayoutParams.TYPE_PHONE : Window를 최상위로 유지
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE :  다른 영역에 TouchEvent가 발생했을 때 인지 하지 않음
		 */
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				PixelFormat.RGBA_8888);

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		layoutParams.width = keyboardWidth;
		layoutParams.height = keyboardHeight;
		layoutParams.x = deviceWidth - layoutParams.width;
		layoutParams.y = deviceHeight - layoutParams.height;

		// WindowManager에 layoutParams속성을 갖는 Quicklic ImageView 추가
		windowManager.addView(keyboardLinearLayout, layoutParams);
	}

	/**
	 * @함수명 : resetKeyBoard
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 현재 앱을 기준으로 왼쪽과 오른쪽 버튼을 누를 시 실행 될 앱의 아이콘을 등록 / JELLY_BEAN이상의 경우에만 가능 / JELLY_BEAN 이상이 아니면, 기본 제공
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 22.
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void resetKeyBoard()
	{
		try
		{
			if ( packageArrayList.size() > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
			{
				String leftPackge = packageArrayList.get(checkUnderBound(packageIndex));
				String rightPackge = packageArrayList.get(checkUpperBound(packageIndex + 1));
				leftIconFrameLayout.setBackground(packageManager.getApplicationIcon(leftPackge));
				rightIconFrameLayout.setBackground(packageManager.getApplicationIcon(rightPackge));
			}
			else
			{
				leftIconFrameLayout.setBackgroundColor(Color.TRANSPARENT);
				rightIconFrameLayout.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @함수명 : createKeyBoard
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : KeyBoard 레이아웃 생성
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 21.
	 */
	private void createKeyBoard()
	{
		keyboardLinearLayout = new LinearLayout(this);
		leftIconFrameLayout = new FrameLayout(this);
		rightIconFrameLayout = new FrameLayout(this);
		LinearLayout backSectionLinearLayout = new LinearLayout(this);
		LinearLayout firstSectionLinearLayout = new LinearLayout(this);
		LinearLayout secondSectionLinearLayout = new LinearLayout(this);

		FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams sectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

		sectionLayoutParams.weight = 0.5f;
		buttonLayoutParams.weight = 0.5f;

		leftButton = new Button(this);
		rightButton = new Button(this);
		moveButton = new Button(this);
		exitButton = new Button(this);

		keyboardLinearLayout.setOrientation(LinearLayout.VERTICAL);
		keyboardLinearLayout.setBackgroundColor(Color.TRANSPARENT);
		keyboardLinearLayout.setWeightSum(1);
		keyboardLinearLayout.setLayoutParams(frameLayoutParams);

		leftIconFrameLayout.setLayoutParams(buttonLayoutParams);
		rightIconFrameLayout.setLayoutParams(buttonLayoutParams);

		backSectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
		backSectionLinearLayout.setBackgroundColor(Color.WHITE);
		backSectionLinearLayout.setAlpha(VIEW_ALPHA);
		backSectionLinearLayout.setWeightSum(1);
		backSectionLinearLayout.setPadding(2, 2, 2, 2);
		backSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		firstSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		firstSectionLinearLayout.setBackgroundColor(Color.BLACK);
		firstSectionLinearLayout.setAlpha(VIEW_ALPHA);
		firstSectionLinearLayout.setWeightSum(1);
		firstSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		secondSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		secondSectionLinearLayout.setBackgroundColor(Color.BLACK);
		secondSectionLinearLayout.setAlpha(VIEW_ALPHA);
		secondSectionLinearLayout.setWeightSum(1);
		secondSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		leftButton.setHeight(keyboardHeight);
		leftButton.setLayoutParams(frameLayoutParams);
		leftButton.setTextAppearance(this, R.style.KeyBoard_Button);
		leftButton.setBackgroundResource(R.drawable.key_left);

		rightButton.setHeight(keyboardHeight);
		rightButton.setLayoutParams(frameLayoutParams);
		rightButton.setTextAppearance(this, R.style.KeyBoard_Button);
		rightButton.setBackgroundResource(R.drawable.key_right);

		moveButton.setHeight(keyboardHeight);
		moveButton.setLayoutParams(buttonLayoutParams);
		moveButton.setTextAppearance(this, R.style.KeyBoard_Button);
		moveButton.setBackgroundResource(R.drawable.key_move);

		exitButton.setHeight(keyboardHeight);
		exitButton.setLayoutParams(buttonLayoutParams);
		exitButton.setTextAppearance(this, R.style.KeyBoard_Button);
		exitButton.setBackgroundResource(R.drawable.key_exit);

		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);
		moveButton.setOnClickListener(clickListener);
		exitButton.setOnClickListener(clickListener);

		moveButton.setOnTouchListener(onTouchListener);

		firstSectionLinearLayout.addView(moveButton);
		firstSectionLinearLayout.addView(exitButton);

		leftIconFrameLayout.addView(leftButton);
		rightIconFrameLayout.addView(rightButton);

		secondSectionLinearLayout.addView(leftIconFrameLayout);
		secondSectionLinearLayout.addView(rightIconFrameLayout);

		backSectionLinearLayout.addView(firstSectionLinearLayout);
		backSectionLinearLayout.addView(secondSectionLinearLayout);

		keyboardLinearLayout.addView(backSectionLinearLayout);
	}

	/**
	 * @함수명 : checkUpperBound
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : PackageName List의 upper 경계 검사해서 오류 방지
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 22.
	 */
	private int checkUpperBound( int index )
	{
		if ( index >= packageArrayList.size() )
			return packageArrayList.size() - 1;
		return index;
	}

	/**
	 * @함수명 : checkUnderBound
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : PackageName List의 under 경계 검사해서 오류 방지
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 22.
	 */
	private int checkUnderBound( int index )
	{
		if ( index <= 0 )
			return 0;
		return index - 1;
	}

	/**
	 * @함수명 : getLauncherName
	 * @매개변수 :
	 * @반환 : String
	 * @기능(역할) : Launcher 이름 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 22.
	 */
	private String getLauncherName()
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return resolveInfo.activityInfo.packageName;
	}

	/**
	 * @함수명 : getTopPackageName
	 * @매개변수 :
	 * @반환 : String
	 * @기능(역할) : 현재 상위 Package 이름 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 22.
	 */
	private String getTopPackageName()
	{
		List<RunningTaskInfo> taskinfo = activityManager.getRunningTasks(MAX_TASK_NUM);
		return taskinfo.get(0).topActivity.getPackageName();
	}

	/**
	 * @함수명 : getRunningTaskList
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 현재 실행중인 Task의 목록을 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 18.
	 */
	private void getRunningTaskList()
	{
		tempArrayList = new ArrayList<String>(packageArrayList); // 이전 리스트 임시 보관
		packageArrayList.clear(); // 초기화

		String launcherName = getLauncherName();
		List<RunningTaskInfo> taskinfo = activityManager.getRunningTasks(MAX_TASK_NUM);

		int runningTaskCount = 0;
		boolean isExist = false;

		for ( int i = 0; i < taskinfo.size(); i++ )
		{
			String packageName = taskinfo.get(i).topActivity.getPackageName();

			// 시스템 앱 또는 항상 실행중인 기본 앱 걸러내기 : 사용자가 직접 실행한 앱만 추가하기 위함
			// 또한, 이전에 갖고 있던 Task 앱 목록을 제외한 나머지를 추가하여, 중복 추가를 배제함
			isExist = tempArrayList.contains(packageName);
			if ( !packageName.matches(launcherName + "|" + getString(R.string.keyboard_except_list)) && !isExist )
			{
				tempArrayList.add(packageName);
				runningTaskCount++;
			}
			if ( isExist )
			{
				runningTaskCount++;
			}
		}

		if ( runningTaskCount == 0 )
			tempArrayList.clear();

		packageArrayList.addAll(tempArrayList); // 새로 구성된 리스트 복사

		// 현재 보고 있는 앱의 위치로 초기화
		packageIndex = packageArrayList.indexOf(getTopPackageName());
		if ( packageIndex < 0 )
			packageIndex = 0;
		if ( packageIndex >= packageArrayList.size() )
			packageIndex = packageArrayList.size();

		appCount = packageArrayList.size();
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		/**
		 * @함수명 : onClick
		 * @매개변수 : View v
		 * @기능(역할) : 키보드에 등록된 버튼에 대한 어플리케이션 간 전환 및 키보드 서비스의 이동과 종료
		 * @작성자 : THYang
		 * @작성일 : 2014. 6. 18.
		 */
		@SuppressLint("HandlerLeak")
		@Override
		public void onClick( View v )
		{
			if ( v == moveButton )
			{
				long pressTime = System.currentTimeMillis();

				/* Double Clicked
				 * 현재 누른 시간과 마지막으로 누른 시간을 감산한 결과가
				 * DOUBLE_PRESS_INTERVAL보다 작으면 Double Clicked.
				 */
				if ( (pressTime - lastPressTime) <= DOUBLE_PRESS_INTERVAL )
				{
					// Double Clicked 인 경우, 핸들러가 실행되도 Single Click 작업 하지 않음.
					isDoubleClicked = true;
				}
				// Single Clicked
				else
				{
					isDoubleClicked = false;

					// Handler 에 Single Click 시 수행할 작업을 등록
					Message message = new Message();
					Handler handler = new Handler()
					{
						public void handleMessage( Message message )
						{
							// Double Clicked 가 아니고 객체의 이동 수행을 하지 않았다면 실행.
							if ( !isDoubleClicked && isMoved == false )
							{
								// TODO Single Clicked
							}
						}
					};
					// DOUBLE_PRESS_INTERVAL 시간동안 Handler 를 Delay 시킴.
					handler.sendMessageDelayed(message, DOUBLE_PRESS_INTERVAL);
				}
				// 현재 누른 시간을 마지막 누른 시간으로 저장
				lastPressTime = pressTime;
			}
			else if ( v == exitButton )
			{
				// 서비스 종료
				stopService(intent);

				// MainActivity 시작
				Intent intent = new Intent(QuicklicKeyBoardService.this, QuicklicMainService.class);
				startService(intent);
			}
			else
			{
				getRunningTaskList();
				resetKeyBoard();
				if ( appCount == 0 )
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_move_no, Toast.LENGTH_SHORT).show();
					return;
				}

				try
				{
					if ( v == leftButton ) // Button '<'
					{
						if ( packageArrayList.get(0).equals(getTopPackageName()) )
						{
							Toast.makeText(getApplicationContext(), R.string.keyboard_move_first, Toast.LENGTH_SHORT).show();
							return;
						}
						else if ( (packageIndex - 1) >= 0 ) // 리스트의 처음이 아니면,
						{
							packageIndex--; // 이전 앱 실행
						}
					}
					else if ( v == rightButton ) // Button '>'
					{
						if ( packageArrayList.get(appCount - 1).equals(getTopPackageName()) )
						{
							Toast.makeText(getApplicationContext(), R.string.keyboard_move_finish, Toast.LENGTH_SHORT).show();
							return;
						}
						else if ( (packageIndex + 1) != appCount ) // 리스트의 끝이 아니면,
						{
							packageIndex++; // 다음 앱 실행
						}
					}
					Intent intent = packageManager.getLaunchIntentForPackage(packageArrayList.get(packageIndex));
					startActivity(intent);
				}
				catch (Exception e)
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_run_no, Toast.LENGTH_SHORT).show();
				}
				resetKeyBoard();
			}
		}
	};

	private OnTouchListener onTouchListener = new OnTouchListener()
	{
		private int initialX;
		private int initialY;
		private float initialTouchX;
		private float initialTouchY;
		private int moveTouchX;
		private int moveTouchY;

		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			try
			{
				if ( v == moveButton )
				{
					switch ( event.getAction() )
					{
					case MotionEvent.ACTION_DOWN:
						initialX = layoutParams.x;
						initialY = layoutParams.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;

					case MotionEvent.ACTION_MOVE:
						moveTouchX = (int) (event.getRawX() - initialTouchX);
						moveTouchY = (int) (event.getRawY() - initialTouchY);
						layoutParams.x = initialX + moveTouchX;
						layoutParams.y = initialY + moveTouchY;
						windowManager.updateViewLayout(keyboardLinearLayout, layoutParams);

						// 터치 감지 : X와 Y좌표가 10이하인 경우에는 움직임이 없다고 판단하고 single touch 이벤트 발생.
						isMoved = true;
						if ( Math.abs(moveTouchX) < LIMITED_MOVE_DISTANCE && Math.abs(moveTouchY) < LIMITED_MOVE_DISTANCE )
							isMoved = false;
						break;

					case MotionEvent.ACTION_UP:
						if ( isMoved )
						{
							timer.schedule(new TimerTask()
							{
								@Override
								public void run()
								{
									// DOUBLE_PRESS_INTERVAL+100 milliseconds 가 지나가면, 다시 클릭 가능해짐. 
									isMoved = false;
								}
							}, DOUBLE_PRESS_INTERVAL + 100);
						}
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
	};
}
