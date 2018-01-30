package jh.android.commonlibrary;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SystemUtility {
	
	public static void RunShellCommand(String[] cmds){
		ShellUtils.execCommand(cmds, false);
	}
	
	
	public static long GetAvailableMemory(Context context){
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.availMem;
	}
	
	public static android.os.Debug.MemoryInfo GetApplicationMemoryInfo(Context context,String appName){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		RunningAppProcessInfo targetAppInfo = null;		
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses)
		{
			if(runningAppProcessInfo.processName.contains(appName))
			{
				targetAppInfo = runningAppProcessInfo;
				break;
			}
		}
		if(targetAppInfo==null)
		{
			return null;
		}
		else {
			return GetApplicationMemoryInfo(context , targetAppInfo.pid);
		}
	}
	
	public static android.os.Debug.MemoryInfo GetApplicationMemoryInfo(Context context,int ProcessID){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(new int[]{ProcessID});
		if(memoryInfoArray.length>0)
		{
			return memoryInfoArray[0];
		}
		else 
		{			
			return null;	
		}	
	}
	
    public static void StayAwake(Window window,boolean stayAwake){
    	if(stayAwake)
    	{
    		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
    	else {
    		window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  		
		}    
    }
    
	public static boolean IsScreenOn(Context context){
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		try{			
			return pm.isScreenOn();
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	public static void SetDisplayTimeout(Context context ,int screenOffTimeout){
	    int time;
	    switch (screenOffTimeout) {
	    case 0:
	    case 15:
	    case 15000:
	        time = 15000;
	        break;
	    case 1:
	    case 30:
	    case 30000:
	        time = 30000;
	        break;
	    case 2:
	    case 60:
	    case 60000:
	        time = 60000;
	        break;
	    case 3:
	    case 120:
	    case 120000:
	        time = 120000;
	        break;
	    case 4:
	    case 600:
	    case 600000:
	        time = 600000;
	        break;
	    case 5:
	    case 1800:
	    case 1800000:
	        time = 1800000;
	        break;
	    default:
	        time = -1;
	    }
	    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
	}
	
	private static WakeLock wl = null;
	public static boolean IsWakeLocked()
	{
		return wl==null?false:wl.isHeld();
	}
	
	@SuppressLint("Wakelock") 
	public static void SetWakeLock(Context context, boolean wakeup){
		if(wl==null){
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WakeLock");
		}
		if(wakeup){
			if(!wl.isHeld()){
				wl.acquire();		
			}
		}
		else{
			SetDisplayTimeout(context,15000);
			if(wl.isHeld()){
				wl.release();
			}
		}
	}
		
	public static void setWakeUp_AlarmManager(Context context,int interval_InMilliseconds){
//		Bundle bundle = new Bundle();
//		setAlarmManager(context,WakeupService.class,"Init",bundle,interval_InMilliseconds,false);
		Intent i = new Intent(context, WakeupService.class);
		i.setAction("Init");
		i.putExtra("Delay", interval_InMilliseconds);
		context.startService(i);
	}
	
	public static void cancelWakeUp_AlarmManager(Context context){		
		cancelAlarmManager(context,WakeupService.class,"Init");		
	}
	
	

	public static void setAlarmManager(Context context, Class<?> cls, String action, Bundle bundle,int interval_InMilliseconds,boolean repeatable){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);	
	    Intent intent = new Intent(context, cls);
	    intent.setAction(action);
	    intent.putExtras(bundle);
	    PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);	         
	    if(repeatable){
	        long triggerAtTime = (new Date()).getTime(); 
	    	am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval_InMilliseconds, pi); 
	    }
	    else{
	    	Calendar cal = Calendar.getInstance();
		    cal.add(Calendar.MILLISECOND, interval_InMilliseconds);	 
	    	am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
	    }
    }
	
	public static void cancelAlarmManager(Context context, Class<?> cls,String action){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);  
        intent.setAction(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);  		
		am.cancel(pi);
	}
}
