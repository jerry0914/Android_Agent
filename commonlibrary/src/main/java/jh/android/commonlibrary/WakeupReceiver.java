package jh.android.commonlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WakeupReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bData = intent.getExtras();		
		Log.d("AT_Debug","Receive the wake up broadcast message...");
		intent.setAction("WakeUp");  
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(context,WakeupService.class);
		serviceIntent.setAction("WakeUp");
		context.startService(serviceIntent);
		Log.d("AT_Debug","WakeupReceiver send wake up request...");
	}
}
