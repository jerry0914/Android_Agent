package jh.android.commonlibrary;

import java.util.EventObject;

public class LoggerMessageEventObject extends EventObject 
{
	 private String myMessage;
	 public LoggerMessageEventObject(Object source, String Message)
	 {
		 super(source);
		 myMessage = Message;
	 }
	 public String Message()
	 {
		 return myMessage;
	 }
}
