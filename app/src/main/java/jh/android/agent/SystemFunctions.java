package jh.android.agent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import jh.android.agent.MainService;
import jh.android.agent.StaticVariables;
import jh.android.commonlibrary.Logger;
import jh.android.commonlibrary.Logger.Log_Level;

public class SystemFunctions {
	private static ActivityManager getActivityManager() {
		Logger.WriteLog("Action", "get ActivityManager");
		ActivityManager am = (ActivityManager) MainService.MainServiceContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		return am;
	}

	public static String CheckTopActivity(String activityName,
			String str_Timeout) {
		int timeOut = Integer.parseInt(str_Timeout);
		try {
			String currentActivity;
			if (activityName.contains("/")) {
				for (int i = 0; i < timeOut; i++) {
					currentActivity = GetTopActivity("pkg+cls");
					Logger.WriteLog("Action",
							"CheckTopActivity : TargetActivityName="
									+ activityName + ";"
									+ "CurrentActivityName=" + currentActivity);
					if (activityName.equals(currentActivity)) {
						return StaticVariables.ReplyTag_Ok;
					} else {
						Thread.sleep(1000);
					}
				}// end-of-forloop

			} else {
				for (int i = 0; i < timeOut; i++) {
					currentActivity = GetTopActivity("clsName");
					if (activityName.equals(currentActivity)) {
						return StaticVariables.ReplyTag_Ok;
					} else {
						Thread.sleep(1000);
					}
				}// end-of-forloop
			}
			return StaticVariables.ReplyTag_NotOk;
		} catch (Exception e) {
			Logger.WriteLog("SysFun",
					"CheckTopActivity ex:" + e.getMessage(), Log_Level.Error,
					true);
			return StaticVariables.ReplyTag_Error;
		}
	}

	public static String GetTopActivity(String cmd) {
		String rtnValue = StaticVariables.ReplyTag_NotOk;
		try {
			Logger.WriteLog("Action", "Enter into GetTopActivity");
			ActivityManager am = getActivityManager();
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			Logger.WriteLog(
					"Action",
					"Top activity name = " + cn.getPackageName() + "/"
							+ cn.getClassName());
			if (cmd.equals("clsName")) {
				rtnValue = cn.getClassName();
			} else if (cmd.equals("pkg+cls")) {
				rtnValue = cn.getPackageName() + "/" + cn.getShortClassName();
			} else if (cmd.equals("pkg"))
				rtnValue = cn.getPackageName();
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.WriteLog("SysFun",
					"GetTopActivity exception = " + ex.getMessage(),
					Log_Level.Error, true);
			rtnValue = StaticVariables.ReplyTag_Error;
		}
		return rtnValue;
	}

	public static String GetDownloadFolder(){		
		String  folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
		Logger.WriteLog("Action", "GetDownloadFolder ="+folder);
		return folder;
	}
	
	public static long GetFileSize(String path){
		long filesize = 0;
		try{		
			File file = new File(Uri.parse(path).getPath());
			if(file.exists())
			{
				Logger.WriteLog("Action","GetFileSize file :"+file.getPath()+" exists.");
				if(file.isDirectory()){
					for(File subFile : file.listFiles()){
						long subSize = GetFileSize(subFile.toString());
						if(subSize>0){
							filesize+=subSize;
						}					
					}			
				}
				else{
					filesize = file.length();
				}
			}
		}
		catch(Exception e){
			Logger.WriteLog("SysFun","GetFileSize exception = "+e.getMessage(),Log_Level.Error,true);
		}
		Logger.WriteLog("Action","Get \""+path+"\" size ="+filesize);
		return filesize;
	}
}
