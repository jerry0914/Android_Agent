package jh.android.commonlibrary;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WakeupService extends IntentService {
	public WakeupService() {
		super("WakeService");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		String action = intent.getAction();
		if(action.compareTo("Init")==0){
			Log.d("AT_Debug", "WakeupService - Init");
			long delayTime = intent.getIntExtra("Delay",0);
			setAlarmManager("WakeUp",delayTime);
		}
		else if(action.compareTo("WakeUp")==0){
			Log.d("AT_Debug", "WakeupService - WakeUp");
			Context currentContext = getApplicationContext();	
			SystemUtility.SetWakeLock(currentContext,true);
			try{
				Thread.sleep(2000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}				
			if(!SystemUtility.IsScreenOn(currentContext)){
				SystemUtility.SetWakeLock(currentContext,true);
			}
		}
	}
	
	private void setAlarmManager(String type,long sleepTime) {
		Intent serviceIntent = new Intent(getApplicationContext(), WakeupReceiver.class);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pending = PendingIntent.getBroadcast( this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + sleepTime,pending);	
	}
}
