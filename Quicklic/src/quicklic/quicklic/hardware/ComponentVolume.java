package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.media.AudioManager;

public class ComponentVolume {

	private AudioManager audioManager;
	private int ringMaxVolume;

	public ComponentVolume(AudioManager audioManager)
	{
		this.audioManager = audioManager;

		ringMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
	}

	/**
	 * @함수명 : upVolume
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 소리 키우기
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 24.
	 */
	public void upVolume()
	{
		audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	/**
	 * @함수명 : downVolume
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 소리 줄이기
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 24.
	 */
	public void downVolume()
	{
		audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : Ringer mode에 따른 drawable 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 24.
	 */
	public int getDrawable()
	{
		switch ( audioManager.getRingerMode() )
		{
		case AudioManager.RINGER_MODE_SILENT:
			return R.drawable.sound_mute;

		case AudioManager.RINGER_MODE_VIBRATE:
			return R.drawable.sound_vibrate;

		case AudioManager.RINGER_MODE_NORMAL:
			return R.drawable.sound_on;

		default:
			return R.drawable.sound_on;
		}
	}

	/**
	 * @함수명 : getVolume
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : 현재 음량값을 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	private int getVolume()
	{
		return audioManager.getStreamVolume(AudioManager.STREAM_RING);
	}

	/**
	 * @함수명 : isMaxVolume
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : 음량이 최대치인지 판별
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public boolean isMaxVolume()
	{
		return getVolume() == ringMaxVolume;
	}

	/**
	 * @함수명 : isMinVolume
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : 음량이 최소치인지 판별
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public boolean isMinVolume()
	{
		return getVolume() == 0;
	}

	/**
	 * @함수명 : controlVolume
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Ringer mode에 따른 상태 변화
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 24.
	 */
	public void controlVolume()
	{
		switch ( audioManager.getRingerMode() )
		{
		case AudioManager.RINGER_MODE_SILENT: // 음소거
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;

		case AudioManager.RINGER_MODE_VIBRATE: // 진동
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;

		case AudioManager.RINGER_MODE_NORMAL: // 일반
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;

		default:
			break;
		}
	}
}
