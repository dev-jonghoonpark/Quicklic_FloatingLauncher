package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class ComponentRotate {

	private final int STATE_ON = 0;
	private final int STATE_OFF = 1;
	private final String ACCELEROMETER_ROTATION;
	private final ContentResolver CONTENT_RESOLVER;

	public ComponentRotate(Context context)
	{
		ACCELEROMETER_ROTATION = Settings.System.ACCELEROMETER_ROTATION;
		CONTENT_RESOLVER = context.getContentResolver();
	}

	/**
	 * @함수명 : onRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate 켜기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void onRotate()
	{
		Settings.System.putInt(CONTENT_RESOLVER, ACCELEROMETER_ROTATION, STATE_ON);
	}

	/**
	 * @함수명 : offRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate 끄기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void offRotate()
	{
		Settings.System.putInt(CONTENT_RESOLVER, ACCELEROMETER_ROTATION, STATE_OFF);
	}

	/**
	 * @함수명 : getState
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : 현재 rotate 상태 반환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 25.
	 */
	private int getState()
	{
		return Settings.System.getInt(CONTENT_RESOLVER, ACCELEROMETER_ROTATION, STATE_ON);
	}

	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : rotate 상태에 따른 drawable 반환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	public int getDrawable()
	{
		if ( getState() == STATE_OFF )
		{
			return R.drawable.rotate_on;
		}
		else
		{
			return R.drawable.rotate_off;
		}
	}

	/**
	 * @함수명 : controlRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate on/off 상태 전환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	public void controlRotate()
	{
		if ( getState() == STATE_OFF )
		{
			onRotate();
		}
		else
		{
			offRotate();
		}
	}

}
