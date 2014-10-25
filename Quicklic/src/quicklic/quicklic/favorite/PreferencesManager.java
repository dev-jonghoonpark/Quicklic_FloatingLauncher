package quicklic.quicklic.favorite;

import quicklic.floating.api.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class PreferencesManager extends Activity
{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private static final String APP_NUM = "app_num";
	private static final String APP_DATA = "app_data";

	public PreferencesManager(Context context)
	{
		pref = context.getSharedPreferences(APP_DATA, MODE_PRIVATE);
		editor = pref.edit();
		if ( pref.getInt(APP_NUM, -1) < 0 )
		{
			editor.putInt(APP_NUM, 0);
			commit();
		}
	}

	public void commit()
	{
		editor.commit();
	}

	/**
	 * @함수명 : getNumPreferences
	 * @매개변수 : Context context
	 * @반환 : int
	 * @기능(역할) : Favorite에 등록 되어진 어플리케이션의 개수를 반환
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public int getNumPreferences( Context context )
	{
		return pref.getInt(APP_NUM, 0);
	}

	/**
	 * @함수명 : setPreference
	 * @매개변수 : String pkgName, Context context
	 * @반환 : boolean
	 * @기능(역할) : Favorite에 사용되는 어플리케이션 추가
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public boolean setPreference( String pkgName, Context context )
	{
		int num = getNumPreferences(context);

		for ( int i = 0; i < num; i++ )
		{
			if ( getAppPreferences(context, i).equals(pkgName) )
			{
				Toast.makeText(context, R.string.favorite_duplication_warning, Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		editor.putString(APP_DATA + num, pkgName);
		editor.putInt(APP_NUM, ++num);
		commit();

		return true;
	}

	/**
	 * @함수명 : getAppPreferences
	 * @매개변수 : Context context, int pkgNum
	 * @반환 : String
	 * @기능(역할) : Favorite에서 선택된 어플리케이션의 PackageName을 반환
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public String getAppPreferences( Context context, int pkgNum )
	{
		return pref.getString(APP_DATA + pkgNum, null);
	}

	/**
	 * @함수명 : removeAppPreferences
	 * @매개변수 : Context context, int pkgNum
	 * @반환 : void
	 * @기능(역할) : Favorite에서 선택된 어플리케이션 삭제
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public void removeAppPreferences( Context context, int pkgNum )
	{
		int num = getNumPreferences(context);

		for ( int j = pkgNum; j < num; j++ )
		{
			editor.putString(APP_DATA + j, getAppPreferences(context, j + 1));
		}
		editor.remove(APP_DATA + num);
		editor.putInt(APP_NUM, --num);
		commit();
	}
}