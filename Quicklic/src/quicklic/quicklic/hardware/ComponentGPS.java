package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.content.Context;

public class ComponentGPS {

	private Context context;

	public ComponentGPS(Context context)
	{
		this.context = context;
	}

	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : GPS 상태에 따른 drawable 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public int getDrawable()
	{
		if ( !isEnable() )
		{
			return R.drawable.gps_off;
		}
		else
		{
			return R.drawable.gps_on;
		}
	}

	/**
	 * @함수명 : isEnable
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : GPS on/off에 따른 상태 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public boolean isEnable()
	{

		String gps = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if ( gps.contains("gps") )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
