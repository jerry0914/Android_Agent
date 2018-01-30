package jh.android.agent;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import jh.android.commonlibrary.Logger;
import jh.android.commonlibrary.Logger.Log_Level;

public class MainService extends Service {
	protected static Context MainServiceContext;
	private final int notifyID = 9487; // �q�����ѧO���X
	
	/*
	public MainService(String name) {
		super(name);
	}
	
	public MainService() {
		super("MainService");
	}
	*/
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		MainServiceContext = getApplicationContext();
		Logger.LogcatTAG = jh.android.agent.StaticVariables.LogTag;
		Logger.Start(jh.android.agent.StaticVariables.LogFolder, 128);
		Log.d(Logger.LogcatTAG, "LogFoler=" + Logger.getLogPath());
		jh.android.agent.FileIO.InitializeAPIResults();
		IntentFilter filter = new IntentFilter();
		filter.addAction(jh.android.agent.StaticVariables.LogTag);
		registerReceiver(receiver, filter);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Logger.WriteLog("Main",
				"jh.android.agent.MainService onDestroy...");
		Logger.Stop();
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	/*
	@Override
	protected void onHandleIntent(Intent intent) {

		try
		{
			Logger.WriteLog("Main","MainService_onHandleIntent, intent action = "+intent.getAction()+"parameters="+intent.getExtras().toString(),Log_Level.Debug,false,true);
			String action = intent.getAction();
			String key = "-1";
			String parameter1 = "";
			String parameter2 = "";
			String parameter3 = "";
			String parameter4 = "";
			String parameter5 = "";
			String parameter6 = "";
			String parameter7 = "";
			String replyMsg = "";
			MainServiceContext = getApplicationContext();
			switch (action) 
			{
				case "Initialize":
					FileIO.InitializeAPIResults();
					Logger.LogcatTAG = StaticVariables.LogTag;
					Logger.Start(StaticVariables.LogFolder, 128);
					Log.d(Logger.LogcatTAG, "LogFoler=" + Logger.getLogPath());					
					break;
				case "GetTopActivity":					
					replyMsg = SystemFunctions.GetTopActivity("pkg+cls");
					break;
				case "CheckTopActivity":
					parameter1 = intent.getExtras().getString("ActivityName");
					if(intent.hasExtra("Timeout")){
						parameter2 = intent.getExtras().getString("Timeout");
						replyMsg = SystemFunctions.CheckTopActivity(parameter1,parameter2);
					}
					else{
						replyMsg = SystemFunctions.CheckTopActivity(parameter1,"0");
					}					
					break;
			}
			if(intent.hasExtra(StaticVariables.ReplyKey)){
				key = intent.getExtras().getString(StaticVariables.ReplyKey);
				FileIO.WriteAPI_Result(StaticVariables.ReplyFolder+key, replyMsg);
			}
		}
		catch(Exception ex)
		{
			Logger.WriteLog(StaticVariables.LogTag,"MainService_onHandleIntent exception: " +ex.getMessage().toString(),Log_Level.Error,true,true);
			return;
		}
	}	
	*/
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		   @Override
		   public void onReceive(Context context, Intent intent) {
			   try{
			   		if(intent.hasExtra("Command")){
						String command = intent.getExtras().getString("Command");
						String index = "-1";
						String parameter1 = "";
						String parameter2 = "";
						String parameter3 = "";
						String parameter4 = "";
						String parameter5 = "";
						String parameter6 = "";
						String parameter7 = "";
						String replyMsg = "";						
						switch (command){
							// {{ System Functions
							case "GetTopActivity":					
								replyMsg = jh.android.agent.SystemFunctions.GetTopActivity("pkg+cls");
								break;
							case "CheckTopActivity":
								parameter1 = intent.getExtras().getString("ActivityName");
								if(intent.hasExtra("Timeout")){
									parameter2 = intent.getExtras().getString("Timeout");
									replyMsg = jh.android.agent.SystemFunctions.CheckTopActivity(parameter1,parameter2);
								}
								else{
									replyMsg = jh.android.agent.SystemFunctions.CheckTopActivity(parameter1,"0");
								}					
								break;
							case "GetDownloadFolder":
								replyMsg = jh.android.agent.SystemFunctions.GetDownloadFolder();
								break;								
							case "GetFileSize":
								parameter1 = intent.getExtras().getString("Path");
								long size = jh.android.agent.SystemFunctions.GetFileSize(parameter1);
								replyMsg = String.valueOf(size);
								break;
							// }} System Functions
								
							//{{ Wifi Functions
							case "CreateWiFiApConfig":
								parameter1 = intent.getExtras().getString("SSID");
								parameter2 = intent.getExtras().getString("Pwd");
								parameter3 = intent.getExtras().getString("SecurityMethod");
								replyMsg = jh.android.agent.WifiFunctions.CreateWiFiApConfig(parameter1, parameter2, parameter3);
								break;
							case "CreateWiFiApConfigEnterprise":								
								parameter1 = intent.getExtras().getString("SSID");
								parameter2 = intent.getExtras().getString("Id");
								parameter3 = intent.getExtras().getString("Pwd");
								parameter4 = intent.getExtras().getString("EapMethod");
								parameter5 = intent.getExtras().getString("Phase2");
								replyMsg = jh.android.agent.WifiFunctions.CreateWiFiApConfig_Enterprise(parameter1, parameter2, parameter3, parameter4, parameter5);
								break;
							case "RemoveWiFiConfiguredNetwork":
								parameter1 = intent.getExtras().getString("SSID");
								replyMsg = jh.android.agent.WifiFunctions.RemoveWiFiConfiguredNetwork(parameter1);
								break;
							//}} Wifi Functions
								
							//{{ Wwan Functions
							case "SetMobileDataEnable":
								parameter1 = intent.getExtras().getString("Enabled");
								boolean enabled = Boolean.parseBoolean(parameter1);
								jh.android.agent.WwanFunctions.SetMobileDataEnabled(enabled);
							case "GetSimState":
								replyMsg = jh.android.agent.WwanFunctions.GetSimState();
								break;
							case "GetMobileNetworkType":
								replyMsg = jh.android.agent.WwanFunctions.GetMobileNetworkType();
								break;
							case "IsSimCardReady":
								replyMsg = jh.android.agent.WwanFunctions.IsSimCardReady();
								break;								
							//}} Wwan Functions
						}
						String logmsg = "command="+command;
						logmsg+=parameter1.length()>0?"; parameter1="+parameter1:"";
						logmsg+=parameter2.length()>0?"; parameter2="+parameter2:"";
						logmsg+=parameter3.length()>0?"; parameter3="+parameter3:"";
						logmsg+=parameter4.length()>0?"; parameter4="+parameter4:"";
						logmsg+=parameter5.length()>0?"; parameter5="+parameter5:"";
						logmsg+=parameter6.length()>0?"; parameter6="+parameter6:"";
						logmsg+=parameter7.length()>0?"; parameter7="+parameter7:"";
						Logger.WriteLog("Main","ST_Agent command received,"+logmsg);
						if(intent.hasExtra(jh.android.agent.StaticVariables.ReplyIndex)){
							index = intent.getExtras().getString(jh.android.agent.StaticVariables.ReplyIndex);
							jh.android.agent.FileIO.WriteAPI_Result(jh.android.agent.StaticVariables.ReplyFolder+index, replyMsg);
						}
			   		}
				}
				catch(Exception ex)
				{
					Logger.WriteLog(jh.android.agent.StaticVariables.LogTag,"ST_Agent command received exception: " +ex.getMessage().toString(),Log_Level.Error,true,true);
					return;
				}
		   }
	};
	
	
	/*
	private void processReceiveData(DataEventObject obj) {
		String replyMsg = null;
		String sendMsg = null;
		// sequenceNo 1~1024 = request from DUT
		Logger.WriteLog(
				"Main",
				"MainService.processReceiveData:("
						+ String.valueOf(obj.SequenceNo) + ")[" + obj.Header
						+ "]" + obj.Data);
		if (obj.SequenceNo > 0 && obj.SequenceNo <= 1024) {
			try {
				Map mData = obj.TransferDataToKeyValuePair(); // Transfer parameters to
														// Map data type
				switch (obj.Header) {

				// {{ System Functions
				case "GetTopActivity":
					replyMsg = SystemFunctions.GetTopActivity("pkg+cls");
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "CheckTopActivity":
					// Parameter=activity_name
					String activity_name = String.valueOf(mData
							.get("activity_name"));
					// Parameter=timeout
					String timeout = String.valueOf(mData.get("timeout"));
					replyMsg = SystemFunctions.CheckTopActivity(activity_name,
							timeout);
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				// }} System Functions

				// {{ WiFi Functions
				case "SetWiFiState":
					String state = String.valueOf(mData.get("state"));
					replyMsg = WifiFunctions.SetWiFiState(state);
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "GetWiFiState":
					replyMsg = WifiFunctions.GetWiFiState();
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "IsWiFiConnected":
					replyMsg = String.valueOf(WifiFunctions.IsWiFiConnected());
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "RemoveAllWiFiConfiguredNetworks":
					replyMsg = String.valueOf(WifiFunctions.RemoveAllWiFiConfiguredNetworks());
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "IsWiFiAPExist":
					String ap_name = String.valueOf(mData.get("ap_name"));
					replyMsg = String.valueOf(WifiFunctions.IsWiFiAPExist(ap_name));
					sendMsg = processFunctionReplyMessage(replyMsg);
				case "CreateWiFiApConfig":
					String ssid = String.valueOf(mData.get("ssid"));
					String pwd = String.valueOf(mData.get("pwd"));
					String security_method = String.valueOf(mData.get("security_method"));
					replyMsg = WifiFunctions.CreateWiFiApConfig(ssid, pwd, security_method);
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				case "CreateWiFiApConfig_Enterprise":
					ssid = String.valueOf(mData.get("ssid"));
					String id = String.valueOf(mData.get("id"));
					pwd = String.valueOf(mData.get("pwd"));
					String eap_method = String.valueOf(mData.get("eap_method"));
					String phase_2 = String.valueOf(mData.get("phase_2"));
					replyMsg = WifiFunctions.CreateWiFiApConfig_Enterprise(ssid, id, pwd, eap_method, phase_2);
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				// }} WiFi Functions
				
				case "Template":
					// This is a template
					String arg1 = String.valueOf(mData.get("arg1"));
					// replyMsg = SystemFunctions.FUNCTION_NAME(arg1);
					sendMsg = processFunctionReplyMessage(replyMsg);
					break;
				default:
					break;
				}
				Logger.WriteLog("Main", "ReplyMsg=" + sendMsg);
				if (sendMsg != null) {

				}
			} catch (Exception ex) {
				Logger.WriteLog("Main",
						"processReceiveData exception:" + ex.getMessage(),
						Log_Level.Error);
			}
		}

		else {
			// Reserved;
		}
	}

	private String processFunctionReplyMessage(String replyMsg) {
		String sendMsg = "";
		// Reply NG or Error
		if (replyMsg == StaticVariables.ReplyTag_Error
				|| replyMsg == StaticVariables.ReplyTag_NotOk) {
			sendMsg = StaticVariables.Reply_Tag + Connection.KW_KeyValueSpilter
					+ replyMsg;
		}
		// Reply OK
		else {
			sendMsg = StaticVariables.Reply_Tag + Connection.KW_KeyValueSpilter
					+ StaticVariables.ReplyTag_Ok
					+ Connection.KW_ParametersSpilter;
			sendMsg += StaticVariables.Reply_Result
					+ Connection.KW_KeyValueSpilter + replyMsg;
		}
		return sendMsg;
	}
	*/
}
