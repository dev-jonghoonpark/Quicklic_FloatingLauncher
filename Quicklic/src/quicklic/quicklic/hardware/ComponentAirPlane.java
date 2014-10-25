package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class ComponentAirPlane {

	private static final int AIRPLANE_MODE_OFF = 0;
	private static final int AIRPLANE_MODE_ON = 1;

	private Context context;

	public ComponentAirPlane(Context context)
	{
		this.context = context;
	}

	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : Airplane 상태에 따른 drawable 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public int getDrawable()
	{
		if ( !isEnable() )
		{
			return R.drawable.airplane_off;
		}
		else
		{
			return R.drawable.airplane_on;
		}
	}

	/**
	 * @함수명 : isEnable
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : Airplane on/off에 따른 상태 반환 (SDK 버전에 맞는 방법을 적용)
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public boolean isEnable()
	{
		if ( Build.VERSION.SDK_INT < 17 )
		{
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, AIRPLANE_MODE_OFF) == AIRPLANE_MODE_ON;
		}
		else
		{
			return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, AIRPLANE_MODE_OFF) == AIRPLANE_MODE_ON;
		}
	}

}
