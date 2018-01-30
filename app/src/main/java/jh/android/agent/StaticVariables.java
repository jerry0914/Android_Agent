package jh.android.agent;

import android.annotation.SuppressLint;
import android.os.Build;

@SuppressLint("SdCardPath")
public class StaticVariables {
	public final static String LogTag = "ST_Agent";
	public final static String LogFolder = "/sdcard/usi/st/androidagent/Logs/";
	
	public final static String ReplyFolder = "/sdcard/usi/st/androidagent/Replies/";
	public final static String ReplyIndex = "Reply_Index";
	
	//public final static String ReplyTag = "reply_tag";
	//public final static String ReplyResult = "result";
	public final static String ReplyTag_Ok ="OK";
	public final static String ReplyTag_NotOk ="NG";
	public final static String ReplyTag_Error ="ERROR";	
	
	public final static String deviceModel = Build.MODEL;	
	public final static int DeviceSDK =Build.VERSION.SDK_INT;
}
