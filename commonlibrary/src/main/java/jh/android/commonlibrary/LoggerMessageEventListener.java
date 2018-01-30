package jh.android.commonlibrary;
import java.util.EventListener;

public interface LoggerMessageEventListener extends EventListener{
	 public void messageArrived(LoggerMessageEventObject e);
}
