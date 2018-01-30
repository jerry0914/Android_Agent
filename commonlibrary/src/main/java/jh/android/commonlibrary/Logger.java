package jh.android.commonlibrary;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class Logger {
	private static ArrayList<String> logQueue = new ArrayList<String>();
	private static Thread tdWriteLog = null;
	public static String LogcatTAG = "AT_DEBUG";

	public enum Log_Level {
		Error(0), Warning(1), Information(2), Debug(3), Verbose(4);
		private final int value;
		Log_Level(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}

	public static Log_Level CurrentLogLevel = Log_Level.Debug;
	private static String logPath = "";
	private static boolean runFlag = false;

	public static boolean IsLoggerRunning() {
		return runFlag;
	}

	private static Date dtPreviousWriteLogTime = new Date();
	private static boolean flag_WriteLogImmediately = false;
	private static final int checkLogQueueInterval = 5000;

	public static void setLogPath(String path) {
		int extensionIndex = path.lastIndexOf('.');
		if (extensionIndex > 0) {
			logPath = path.substring(0, extensionIndex);
		} else {
			logPath = path;
		}
		CreateNewFile(getRealLogPath());
	}

	public static String getLogPath() {
		return getRealLogPath();
	}

	public static String getLogcatPath() {
		return logcatPath;
	}

	private static String logcatPath = "";
	private static String timeStamp = GetCurrentTimeString();
	private static int log_index = 0;
	public static int maxLogSize_MB = 64;

	private static long getMaxLogSizeInByte() {
		return (long) maxLogSize_MB * 1024 * 1024;
	}

	private static Vector<LoggerMessageEventListener> listeners;

	public static void Start(String LogPath, int MaxLogFileSize_MB) {
		log_index = 0;
		timeStamp = GetCurrentTimeString();
		setLogPath(LogPath);
		maxLogSize_MB = MaxLogFileSize_MB;
		startLogger();
		WriteLog("Initialized", "Set log path = " + getRealLogPath()
				+ ", max log size per-file = " + maxLogSize_MB + "MB");
	}

	public static void Stop() {
		flag_WriteLogImmediately = true;
		try {
			Thread.sleep(1000);
			tdWriteLog = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		runFlag = false;
	}

	private static void startLogger() {
		runFlag = true;
		if (tdWriteLog == null) {
			tdWriteLog = new Thread(new Runnable() {
				@Override
				public void run() {
					FileWriter fw = null;
					CreateNewFile(getRealLogPath());
					while (runFlag && !tdWriteLog.isInterrupted()) {
						Date dtNow = new Date();
						if (logQueue.size() > 0
								&& ((dtNow.getTime()
										- dtPreviousWriteLogTime.getTime() >= checkLogQueueInterval) || flag_WriteLogImmediately)) {
							try {
								File file = new File(getRealLogPath());
								long fileSizeInBytes = file.length();
								if (fileSizeInBytes > getMaxLogSizeInByte()) {
									log_index++;
									CreateNewFile(getRealLogPath());
								}
								fw = new FileWriter(getRealLogPath(), true);
								while (logQueue.size() > 0) {
									String msg = logQueue.remove(0);
									fw.append(msg + "\r\n");
									if (listeners != null
											&& !listeners.isEmpty()) {
										LoggerMessageEventObject eventObj = new LoggerMessageEventObject(
												this, msg);
										Vector<LoggerMessageEventListener> targets;
										synchronized (this) {
											targets = (Vector<LoggerMessageEventListener>) listeners
													.clone();
										}
										// walk through the listener list and
										// call the sunMoved method in each
										Enumeration<LoggerMessageEventListener> e = targets
												.elements();
										while (e.hasMoreElements()) {
											LoggerMessageEventListener l = (LoggerMessageEventListener) e
													.nextElement();
											l.messageArrived(eventObj);
										}
									}
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								flag_WriteLogImmediately = false;
								dtPreviousWriteLogTime = new Date();
							} catch (Exception ex) {
							}
							try {
								fw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			tdWriteLog.start();
		}
	}

	private static String getRealLogPath() {
		String path = "";
		if (log_index == 0) {
			path = logPath + timeStamp + ".log";
		} else {
			path = logPath + timeStamp + "." + log_index;
		}
		return path;
	}

	public static boolean CreateNewFile(String filePath) {
		boolean result = false;
		File f = new File(filePath);
		File parent = f.getParentFile();
		if (!parent.exists()) {
			if (!parent.mkdirs()) {
				result = false;
			}
		} else {
			if (!f.exists()) {
				try {
					result = f.createNewFile();
				} catch (IOException ioex) {
					result = false;
				}
			} else {
				result = true;
			}
		}
		return result;
	}

	public static String GetCurrentTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss-SSS");
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;
	}

	public static void WriteLog(String header, String log, Log_Level loglevel,
			boolean writeImmediately, boolean writeToLogcat) {
		if (tdWriteLog != null && loglevel.value <= CurrentLogLevel.value) {
			String logMsg = "[" + GetCurrentTimeString() + "]" + "\t"
					+ loglevel.name().substring(0, 1) + "\t" + header + "\t"
					+ log;
			logQueue.add(logMsg);
			flag_WriteLogImmediately = writeImmediately;
			if (listeners != null && !listeners.isEmpty()) {
				LoggerMessageEventObject eventObj = new LoggerMessageEventObject(
						Logger.class, logMsg);
				Vector<LoggerMessageEventListener> targets;
				synchronized (Logger.class) {
					targets = (Vector<LoggerMessageEventListener>) listeners
							.clone();
				}
				// walk through the listener list and
				// call the sunMoved method in each
				Enumeration<LoggerMessageEventListener> e = targets.elements();
				while (e.hasMoreElements()) {
					LoggerMessageEventListener l = (LoggerMessageEventListener) e
							.nextElement();
					l.messageArrived(eventObj);
				}
			}
		}
		if (writeToLogcat) {
			writeToLogcat(loglevel, log);
		}
	}

	public static void WriteLog(String header, String log, Log_Level loglevel,
			boolean writeImmediately) {
		WriteLog(header, log, loglevel, writeImmediately, true);
	}

	public static void WriteLog(String header, String log, Log_Level loglevel) {
		WriteLog(header, log, loglevel, false);
	}

	public static void WriteLog(String header, String log) {
		WriteLog(header, log, Log_Level.Debug);
	}

	/** Register a listener for MaafUtils_RunningMessageEvent */
	synchronized static public void addRunningMessageListener(
			LoggerMessageEventListener lsn) {
		if (listeners == null) {
			listeners = new Vector<LoggerMessageEventListener>();
		}
		listeners.addElement(lsn);
	}

	/** Remove a listener for MaafUtils_RunningMessageEvent */
	synchronized static public void removeRunningMessageListener(
			LoggerMessageEventListener lsn) {
		if (listeners == null) {
			listeners = new Vector<LoggerMessageEventListener>();
		}
		listeners.removeElement(lsn);
	}

	private static void writeToLogcat(Log_Level level, String msg) {
		switch (level) {
		case Verbose:
			Log.v(LogcatTAG, msg);
			break;
		case Debug:
			Log.d(LogcatTAG, msg);
			break;
		case Information:
			Log.i(LogcatTAG, msg);
			break;
		case Warning:
			Log.w(LogcatTAG, msg);
			break;
		case Error:
			Log.e(LogcatTAG, msg);
			break;
		default:
			break;
		}
	}
	//{{ REGION Test1
	private void test1(){
		try {
			Thread.sleep(123);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//}} REGION Test1
	

	// {{----------------------------- Add for logcat start
	private static Process psLogcat;
	private static Process psLogcatEvent;
	private static Process psLogcatRadio;
	private static boolean logcat_RunFlag = false;

	public static boolean IsLogcatRunning() {
		return logcat_RunFlag;
	}

	public static void RunLogcat(String LogcatFolder) {
		RunLogcat(LogcatFolder, true, true);
	}

	public static void RunLogcat(String LogcatFolder, boolean runEventLog_Flag,
			boolean runRadioLog_Flag) {
		String currentTime = Logger.GetCurrentTimeString();
		logcatPath = LogcatFolder + "/logcat_" + currentTime + ".log";
		CreateNewFile(logcatPath);
		String[] cmdLogcat = new String[] { "logcat", "-v", "time", "-f",
				logcatPath, "-r409600", "-n500" };
		String[] cmdLogcatEvent = new String[] { "logcat", "-v", "time", "-b",
				"events", "-f",
				LogcatFolder + "/logcatEvent_" + currentTime + ".log",
				"-r409600", "-n500" };
		String[] cmdLogcatRadio = new String[] { "logcat", "-v", "time", "-b",
				"radio", "-f",
				LogcatFolder + "/logcatRadio_" + currentTime + ".log",
				"-r409600", "-n500" };
		try {
			psLogcat = Runtime.getRuntime().exec(cmdLogcat);
			if (runEventLog_Flag) {
				psLogcatEvent = Runtime.getRuntime().exec(cmdLogcatEvent);
			}

			if (runRadioLog_Flag) {
				psLogcatRadio = Runtime.getRuntime().exec(cmdLogcatRadio);
			}
		} catch (Exception e) {
		}
		logcat_RunFlag = true;
	}

	public static void StopLogcat() {
		if (psLogcat != null) {
			psLogcat.destroy();
		}
		if (psLogcatEvent != null) {
			psLogcatEvent.destroy();
		}
		if (psLogcatRadio != null) {
			psLogcatRadio.destroy();
		}
		logcat_RunFlag = false;
	}
	// }} ----------------------------- Add for logcat end
}
