package quicklic.quicklic.util;

import quicklic.floating.api.R;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class DeviceAdminActivity extends Activity {

	private final int HARDWARE_POWER = 100;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		ComponentName componentName = new ComponentName(this, DeviceAdmin.class);

		// 디바이스 관리자 활성화 액티비티 호출
		Intent power = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		power.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		power.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.hardware_device_admin_description));
		startActivityForResult(power, HARDWARE_POWER);

	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

}
