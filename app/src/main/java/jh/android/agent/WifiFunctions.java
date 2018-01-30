package jh.android.agent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;

import java.util.List;

import jh.android.agent.MainService;
import jh.android.agent.StaticVariables;
import jh.android.commonlibrary.Logger;
import jh.android.commonlibrary.Logger.Log_Level;


public class WifiFunctions {
	private static ActivityManager getActivityManager() {
		Logger.WriteLog("WifiFun", "get ActivityManager");
		ActivityManager am = (ActivityManager) MainService.MainServiceContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		return am;
	}

	private static WifiManager getWifiManager() {
		Logger.WriteLog("WifiFun", "get WifiManager");
		WifiManager wifiManager = (WifiManager) MainService.MainServiceContext
				.getSystemService(Context.WIFI_SERVICE);
		return wifiManager;
	}

	public static String SetWiFiState(String State) {
		String retval = StaticVariables.ReplyTag_NotOk;
		try {
			WifiManager wifiManager = getWifiManager();
			if (State.equalsIgnoreCase("ON"))
				wifiManager.setWifiEnabled(true);
			else
				wifiManager.setWifiEnabled(false);
			retval = StaticVariables.ReplyTag_Ok;
		} catch (Exception ex) {
			Logger.WriteLog("WifiFun", "SetWiFiState ex: " + ex.toString(),
					Log_Level.Error, true);
			retval = StaticVariables.ReplyTag_Error;
		}
		return retval;
	}

	public static String GetWiFiState() {
		String wifiStateString;
		try {
			WifiManager wifiManager = getWifiManager();
			int state = wifiManager.getWifiState();
			switch (state) {
			case WifiManager.WIFI_STATE_DISABLING:
				wifiStateString = "Disabling";
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				wifiStateString = "Disabled";
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				wifiStateString = "Enabling";
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				wifiStateString = "Enabled";
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				wifiStateString = "Unknown";
				break;
			default:
				wifiStateString = "No_Define";
				break;
			}
		} catch (Exception ex) {
			Logger.WriteLog("WifiFun",
					"GetWiFiState exception: " + ex.toString(), Log_Level.Error);
			wifiStateString = "ERROR";
		}
		return wifiStateString;
	}

	public static boolean IsWiFiConnected() {
		try {
			ConnectivityManager conMan = (ConnectivityManager) MainService.MainServiceContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conMan.getNetworkInfo(1);
			if (netInfo.isConnected())
				return true;
		} catch (Exception e) {
			Logger.WriteLog("WifiFun",
					"IsWiFiConnected exception=" + e.getMessage());
			return false;
		}
		return false;
	}

//	public static String RemoveAllWiFiConfiguredNetworks() {
//		String result = StaticVariables.ReplyTag_NotOk;
//		try {
//			WifiManager iwm = getWifiManager();
//			if (iwm != null) {
//				Iterable<WifiConfiguration> configs = iwm
//						.getConfiguredNetworks();
//				for (WifiConfiguration config : configs) {
//					Logger.WriteLog("WifiFun", "WifiConfiguration: " + config);
//					Logger.WriteLog(
//							"WifiFun",
//							"RemoveNetwork: "
//									+ iwm.removeNetwork(config.networkId));					
//					Thread.sleep(500);
//				}
//				iwm.saveConfiguration();
//				configs = iwm.getConfiguredNetworks();
//				if (((List<WifiConfiguration>) configs).size() == 0)
//					result = StaticVariables.ReplyTag_Ok;
//			} else {
//				Logger.WriteLog("WifiFun",
//						"IWifiManager is null, can't operate");
//				result = StaticVariables.ReplyTag_Ok;
//			}
//
//		} catch (Exception e) {
//			result = StaticVariables.ReplyTag_Error;
//			Logger.WriteLog(
//					"WifiFun",
//					"RemoveAllWiFiConfiguredNetworks exception:" + e.toString(),
//					Log_Level.Error);
//		}
//		return result;
//	}
	
	public static String RemoveWiFiConfiguredNetwork(String SSID) {
		String result = StaticVariables.ReplyTag_NotOk;
		boolean is_ok = false;
		boolean bFound = false;
		try {
			WifiManager iwm = getWifiManager();
			if (iwm != null) {
				Iterable<WifiConfiguration> configs = iwm
						.getConfiguredNetworks();
				for (WifiConfiguration config : configs) {
					bFound = config.SSID == SSID;
				    if(bFound){				    	
				    	is_ok = iwm.removeNetwork(config.networkId);
				    	if(is_ok){
				    		result = StaticVariables.ReplyTag_Ok;
				    		iwm.saveConfiguration();
				    	}
				    	else{
				    		result = StaticVariables.ReplyTag_NotOk;
				    	}
				    	Logger.WriteLog(
								"WifiFun",
								"Try to remove wifi config \""+config.SSID+"\", result="+result);
						break;
				    }
				}
				if(!bFound){
					result = StaticVariables.ReplyTag_Ok;
				}
			} else {
				Logger.WriteLog("WifiFun",
						"IWifiManager is null, can't operate");
				result = StaticVariables.ReplyTag_Ok;
			}

		} catch (Exception e) {
			result = StaticVariables.ReplyTag_Error;
			Logger.WriteLog(
					"WifiFun",
					"RemoveWiFiConfiguredNetwork exception:" + e.toString(),
					Log_Level.Error);
		}
		return result;
	}

	public static String IsWiFiAPExist(String TargetApName) {
		boolean IsWiFiApExist = false;
		int retryCount = 0;
		try {
			WifiManager wifiManager = getWifiManager();
			List<ScanResult> aplist = wifiManager.getScanResults();
			while (retryCount < 3 && !IsWiFiApExist) {
				retryCount++;
				for (int i = 0; i < aplist.size(); i++) {
					if (aplist.get(i).SSID.equals(TargetApName)) {
						IsWiFiApExist = true;
						break;
					} else
						IsWiFiApExist = false;
				}
			}
		} catch (Exception ex) {
			Logger.WriteLog("WifiFun", "IsWiFiAPExist ex:" + ex.toString());
			ex.printStackTrace();
			return StaticVariables.ReplyTag_Error;
		}
		return String.valueOf(IsWiFiApExist);
	}

	public static String CreateWiFiApConfig(String SSID, String Pwd,
			String SecurityMethod) {
		String result = StaticVariables.ReplyTag_NotOk;
		SSID = SSID.trim();
		Pwd = Pwd.trim();
		SecurityMethod = SecurityMethod.trim().toLowerCase();
		long checkStartTime = java.lang.System.currentTimeMillis();
		try {
			// {{ REGION Enable WiFi
			WifiManager wimanager = getWifiManager();
			if (wimanager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
				wimanager.setWifiEnabled(true);
				while (java.lang.System.currentTimeMillis() - checkStartTime < (10 * 1000)) {
					try {
						if (wimanager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
							break;
						} else
							Thread.sleep(500);
					} catch (Exception e) {
						Logger.WriteLog("WifiFun",
								"Fail to enable WiFI,retrying...",
								Log_Level.Warning);
					}
				}
			}
			if (wimanager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
				Logger.WriteLog("WifiFun", "WiFi could not be ENABLED!",
						Log_Level.Error);
				return StaticVariables.ReplyTag_NotOk;
			}
			// }} REGION Enable WiFi
			WifiConfiguration wfc = new WifiConfiguration();
			wfc.SSID = "\"" + SSID + "\"";
			// {{ REGION WPA
			if (SecurityMethod.startsWith("wpa")) {
				wfc.hiddenSSID = true;
				// wfc.priority = 40;
				wfc.preSharedKey = "\"" + Pwd + "\"";
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				// wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				wfc.allowedPairwiseCiphers
						.set(WifiConfiguration.PairwiseCipher.CCMP);
				wfc.allowedPairwiseCiphers
						.set(WifiConfiguration.PairwiseCipher.TKIP);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				wfc.status = WifiConfiguration.Status.ENABLED;
				if (SecurityMethod.contains("eap")) {
					wfc.allowedKeyManagement
							.set(WifiConfiguration.KeyMgmt.WPA_EAP);
				} else {
					wfc.allowedKeyManagement
							.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				}
				result = String.valueOf(BuildWiFiConnection(wimanager, wfc));
			}
			// }} REGION WPA

			// {{ REGION WEP
			else if (SecurityMethod.equals("wep")) {
				wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wfc.allowedAuthAlgorithms
						.set(WifiConfiguration.AuthAlgorithm.OPEN);
				wfc.allowedAuthAlgorithms
						.set(WifiConfiguration.AuthAlgorithm.SHARED);
				if (Pwd.length() != 0) {
					int length = Pwd.length();
					if ((length == 10 || length == 26 || length == 58)
							&& Pwd.matches("[0-9A-Fa-f]*")) {
						wfc.wepKeys[0] = Pwd;
					} else {
						wfc.wepKeys[0] = '"' + Pwd + '"';
					}
				}
				result = String.valueOf(BuildWiFiConnection(wimanager, wfc));
			}
			// }} REGION WEP

			// {{ REGION None
			else if (SecurityMethod.equals("null")
					|| SecurityMethod.length() == 0
					|| SecurityMethod.equals("none")) {
				wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				result = String.valueOf(BuildWiFiConnection(wimanager, wfc));
			}
			// }} REGION None
			else {
				result = StaticVariables.ReplyTag_NotOk;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.WriteLog("WifiFun", "ConnectToWiFiAP ex:" + ex.getMessage()
					+ " " + ex.toString(), Log_Level.Error);
			result = StaticVariables.ReplyTag_Error;
		}
		return result;
	}

	public static String CreateWiFiApConfig_Enterprise(String SSID, String Id,String Pwd,
			String EAP_Method, String Phase_2) {
		String result = StaticVariables.ReplyTag_NotOk;
		WifiManager wimanager = getWifiManager();
		SSID = SSID.trim();
		Pwd = Pwd.trim();
		EAP_Method = EAP_Method.trim().toLowerCase();
		Phase_2 = Phase_2.trim().toLowerCase();
		WifiConfiguration wfc = new WifiConfiguration();
		wfc.SSID = "\"" + SSID + "\"";
		wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
		wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wfc.status = WifiConfiguration.Status.ENABLED;
		wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		// {{ REGION EAP
		if (EAP_Method.contains("peap")) {
			wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
		} else if (EAP_Method.contains("ttls")) {
			wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
		} else if (EAP_Method.contains("tls")) {
			wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);
		} else if (EAP_Method.contains("pwd")) {
			wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PWD);
		} else if (EAP_Method.contains("none")) {
			wfc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.NONE);
		} else {
			// Unknown command, return NG;
			return StaticVariables.ReplyTag_NotOk;
		}
		// }} REGION EAP

		// {{ REGION Phase2
		if (Phase_2.contains("mschapv2") || Phase_2.contains("mschap2")) {
			wfc.enterpriseConfig
					.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
		} else if (Phase_2.contains("mschap")) {
			wfc.enterpriseConfig
					.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAP);
		} else if (Phase_2.contains("gtc")) {
			wfc.enterpriseConfig
					.setPhase2Method(WifiEnterpriseConfig.Phase2.GTC);
		} else if (Phase_2.contains("pap")) {
			wfc.enterpriseConfig
					.setPhase2Method(WifiEnterpriseConfig.Phase2.PAP);
		} else if (Phase_2.contains("none")) {
			wfc.enterpriseConfig
					.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
		} else {
			// Unknown command, return NG;
			return StaticVariables.ReplyTag_NotOk;
		}		
		// }} REGION Phase2
		wfc.enterpriseConfig.setIdentity(Id);
		wfc.enterpriseConfig.setPassword(Pwd);
		result = String.valueOf(BuildWiFiConnection(wimanager, wfc));	
		return result;
	}

	private static String BuildWiFiConnection(WifiManager wimanager,
			WifiConfiguration wfc) {
		String rtnValue = StaticVariables.ReplyTag_NotOk;
		try {
			int ID = wimanager.addNetwork(wfc);
			if (ID != -1) {
				wimanager.disconnect();
				wimanager.enableNetwork(ID, true);
				wimanager.saveConfiguration();
				boolean bReconnect = wimanager.reconnect();
				if(bReconnect){
					rtnValue = StaticVariables.ReplyTag_Ok;
				}
				else{
					rtnValue = StaticVariables.ReplyTag_NotOk;
					Logger.WriteLog("WifiFun","WIFI reconnect failed!",Log_Level.Error);
				}
			}
		} catch (Exception ex) {
			rtnValue = StaticVariables.ReplyTag_Error;
			Logger.WriteLog("WifiFun",
					"BuildWiFiConnection ex:" + ex.toString(), Log_Level.Error);
		}
		return rtnValue;
	}
}
