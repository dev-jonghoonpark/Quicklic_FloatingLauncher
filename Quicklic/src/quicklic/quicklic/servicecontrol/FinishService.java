package quicklic.quicklic.servicecontrol;

import quicklic.floating.api.FloatingService;
import quicklic.floating.api.R;
import quicklic.quicklic.favorite.QuicklicFavoriteService;
import quicklic.quicklic.hardware.QuicklicHardwareService;
import quicklic.quicklic.keyboard.QuicklicKeyBoardService;
import quicklic.quicklic.main.QuicklicMainService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FinishService extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		stopServices();
		Toast.makeText(getApplicationContext(), R.string.quit_quicklic, Toast.LENGTH_SHORT).show();

		finish();
	}

	/**
	 * @함수명 : stopServices
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 실행 되는 모든 Service를 종료
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 8. 21.
	 */
	private void stopServices()
	{
		Intent intent;

		intent = new Intent(FinishService.this, FloatingService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicMainService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicFavoriteService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicHardwareService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicKeyBoardService.class);
		stopService(intent);
	}
}
