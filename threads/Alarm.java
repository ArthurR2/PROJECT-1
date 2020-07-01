package nachos.threads;

import nachos.machine.*;

import java.util.ArrayList;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	
	private ArrayList<SleepingThread> SleepingThreads = new ArrayList<SleepingThread>(); 
    
	/**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
	//KThread.currentThread().yield();
    	for (int thread = 0; thread < SleepingThreads.size(); thread++) {
    		if (SleepingThreads.get(thread).checkTime()) {
    			if (SleepingThreads.get(thread).thread != null) {
    				SleepingThreads.get(thread).thread.ready();
    			}
    		}
    	}
    	
    	KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
    /**
	long wakeTime = Machine.timer().getTime() + x;
	while (wakeTime > Machine.timer().getTime())
	    KThread.yield();
	*/
    	boolean intStatus = Machine.interrupt().disable();
    
    	SleepingThread slpThr = new SleepingThread(KThread.currentThread(), 
    			Machine.timer().getTime() + x);
    
    	SleepingThreads.add(slpThr);
	
	
    	KThread.currentThread().sleep();
	
    	Machine.interrupt().restore(intStatus);
    }
    
    /**
     * This is a class to store threads that are put to sleep.
     * It links these threads to the time they went to sleep and
     * to the time they are supposed to wake up
     * 
     * @author Enrique Rodicio
     *
     */
    private class SleepingThread {
    	private KThread thread;
    	private long wakeTime;
    	
    	/**
    	 * Creates a new sleeping thread
    	 * @param thread the thread that will be put to sleep
    	 * @param wakeTime the time the thread should wake up
    	 */
    	private SleepingThread(KThread thread, long wakeTime) {
    		this.thread = thread;
    		this.wakeTime = wakeTime;
    	}
    	
    	/**
    	 * Checks whether the wake time of the thread has been reached
    	 * @return false if time not reached, true if time reached
    	 */
    	private boolean checkTime()
    	{
    		if (wakeTime > Machine.timer().getTime()) {
    			return false;
    		}
    		else {
    			return true;
    		}
    	}
    	
    }
    
}
