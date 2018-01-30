package jh.android.commonlibrary;

public class AutoResetEvent
{
    private final Object _monitor = new Object();
    private volatile boolean _isOpen = false;

    public AutoResetEvent(boolean open)
    {
        _isOpen = open;
    }

    public void waitOne() throws InterruptedException
    {
        synchronized (_monitor) {
            while (!_isOpen) {
                _monitor.wait();
            }
            _isOpen = false;
        }
    }

    public void waitOne(long timeout) throws InterruptedException
    {
    	if(_isOpen)
    	{
	        synchronized (_monitor) {
	            long t = System.currentTimeMillis();
	            while (!_isOpen) {
	                _monitor.wait(timeout);
	                // Check for timeout
	                if (System.currentTimeMillis() - t >= timeout)
	                    break;
	            }
	            _isOpen = false;
	        }
    	}
    }

    public void set()
    {
    	if(!_isOpen)
    	{
	        synchronized (_monitor) {
	            _isOpen = true;
	            _monitor.notify();
	        }
    	}
    }

    public void reset()
    {
        _isOpen = false;
    }
}
