package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.app.Activity;
import android.net.wifi.WifiManager;

public class ComponentWifi extends Activity {

	WifiManager wifi;

	public ComponentWifi(WifiManager wifiManager)
	{
		this.wifi = wifiManager;
	}

	/**
	 * @함수명 : onWifi
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : wifi 켜기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void onWifi()
	{
		wifi.setWifiEnabled(true);
	}

	/**
	 * @함수명 : offWifi
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : wifi 끄기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void offWifi()
	{
		wifi.setWifiEnabled(false);
	}

	/**
	 * @함수명 : isEnabled
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : wifi 상태 반환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private boolean isEnabled()
	{
		return wifi.isWifiEnabled();
	}

	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : wifi 상태에 따른 drawable 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 24.
	 */
	public int getDrawable()
	{
		if ( !isEnabled() )
			return R.drawable.wifi_off;
		else
			return R.drawable.wifi_on;
	}

	/**
	 * @함수명 : controlWifi
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : wifi on/off 상태 전환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	public void controlWifi()
	{
		if ( !isEnabled() )
		{
			if ( wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED )
			{
				onWifi();
			}
		}
		else
		{
			offWifi();
		}
	}
}
