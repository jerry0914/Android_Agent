package jh.android.agent;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jh.android.commonlibrary.Logger;
import jh.android.commonlibrary.Logger.Log_Level;

public class WwanFunctions {
	
	public static String SetMobileDataEnabled(boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String retval = "NG";
		try {
			final ConnectivityManager conman = (ConnectivityManager)  jh.android.agent.MainService.MainServiceContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		    final Class<?> conmanClass = Class.forName(conman.getClass().getName());
		    final java.lang.reflect.Field connectivityManagerField = conmanClass.getDeclaredField("mService");
		    connectivityManagerField.setAccessible(true);
		    final Object connectivityManager = connectivityManagerField.get(conman);
		    final Class<?> connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
		    final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		    setMobileDataEnabledMethod.setAccessible(true);
		    setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
		    retval = "OK";
		}
		catch (Exception ex) {
			Logger.WriteLog("WwanFun", "SetMobileDataEnabled ex: " + ex.toString(),
					Log_Level.Error, true);
			retval = "ERROR";
		}
	    return retval;
	}
	
	public static String GetMobileNetworkType(){
		String category = "";
		String type = "";
		TelephonyManager teleMan =  
				(TelephonyManager) jh.android.agent.MainService.MainServiceContext.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = teleMan.getNetworkType();
		switch (networkType)
		{
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			category = "2G";
			type = "1xRTT";
			break;      
		case TelephonyManager.NETWORK_TYPE_CDMA:
			category = "2G";
			type = "CDMA";
			break;      
		case TelephonyManager.NETWORK_TYPE_EDGE:
			category = "2G";
			type = "EDGE";
			break;  
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			category = "3G";
			type = "eHRPD";
			break;      
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			category = "3G";
			type = "EVDO rev. 0";
			break;  
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			category = "3G";
			type = "EVDO rev. A";
			break;  
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			category = "3G";
			type = "EVDO rev. B";
			break;  
		case TelephonyManager.NETWORK_TYPE_GPRS:
			category = "2G";
			type = "GPRS";
	 	  break;      
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			category = "3G";
			type = "HSDPA";
			break;      
		case TelephonyManager.NETWORK_TYPE_HSPA:
			category = "3G";
			type = "HSPA";
			break;          
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			category = "3G";
			type = "HSPA+";
			break;          
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			category = "3G";
			type = "HSUPA";
			break;          
		case TelephonyManager.NETWORK_TYPE_IDEN:
			category = "2G";
			type = "iDen";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			category = "4G";
			type = "LTE";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			category = "3G";
			type = "UMTS";
			break;          
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			category = "Unknown";
			type = "Unknown";
			break;
		}
	Logger.WriteLog("WwanFun","GetMobileType = "+category+":"+type);
	return category+":"+type;
	}
	
	public static String IsSimCardReady(){
		int sim_state = __getSimState();
		return Boolean.toString(sim_state==TelephonyManager.SIM_STATE_READY);
	}
	
	public static String GetSimState(){
		int sim_state = __getSimState();
		String str_sim_state = "";
		switch (sim_state) {
		case TelephonyManager.SIM_STATE_ABSENT:
			str_sim_state = "No SIM card is available in the device.";
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			str_sim_state = "Locked, requires a network PIN to unlock";
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			str_sim_state = "Locked, requires the user's SIM PIN to unlock";
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			str_sim_state = "Locked, requires the user's SIM PUK to unlock";
			break;
		case TelephonyManager.SIM_STATE_READY:
			str_sim_state = "Ready";
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			str_sim_state = "Unknow";
		default:
			break;
		}
		return sim_state+":"+str_sim_state;
	}
	
	private static int __getSimState(){
		TelephonyManager teleMan =  
				(TelephonyManager) jh.android.agent.MainService.MainServiceContext.getSystemService(Context.TELEPHONY_SERVICE);
		int sim_state = teleMan.getSimState();
		return sim_state;
	}
}
