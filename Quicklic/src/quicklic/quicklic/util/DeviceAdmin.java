/**
 * 도움 by Kylee
 */
package quicklic.quicklic.util;

import quicklic.floating.api.R;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdmin extends DeviceAdminReceiver {

	@Override
	public void onEnabled( Context context, Intent intent )
	{
		Toast.makeText(context, R.string.hardware_device_admin_active, Toast.LENGTH_SHORT).show();
		super.onEnabled(context, intent);
	}

	@Override
	public void onDisabled( Context context, Intent intent )
	{
		Toast.makeText(context, R.string.hardware_device_admin_deactive, Toast.LENGTH_SHORT).show();
		super.onDisabled(context, intent);
	}

}