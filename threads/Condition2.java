package nachos.threads;

import java.util.LinkedList;
import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 * 
 * <p>
 * You must implement this.
 * 
 * @see nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     * 
     * @param conditionLock
     *            the lock associated with this condition variable. The current
     *            thread must hold this lock whenever it uses <tt>sleep()</tt>,
     *            <tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;

        this.waitQueue = new LinkedList<KThread>();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The current
     * thread must hold the associated lock. The thread will automatically
     * reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
    	
    	//If the current thread not have a lock, then abort
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        //release condition lock
        conditionLock.release();
        
        //release condition lock
        final boolean intStatus = Machine.interrupt().disable();
        
        //add current thread to wait queue
        waitQueue.add(KThread.currentThread());
        
        //put the current thread to sleep
        KThread.sleep();
        
        //restore interrupt
        Machine.interrupt().restore(intStatus);
        
        //acquire condition lock
        conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
    	
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        
        //if wait queue is not empty, then disable interrupt.
        if (!waitQueue.isEmpty()) {
        	
        	//The interrupt are disable
        	final boolean intStatus = Machine.interrupt().disable();
        	
        	//remove the first element from wait queue 
            waitQueue.pop().ready();
            
            //restore interrupt
            Machine.interrupt().restore(intStatus);
        }
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        
        //while wait queue is not empty
        //invoke wake() 
        while (!waitQueue.isEmpty())
            wake();
    }

    private Lock conditionLock;
    private LinkedList<KThread> waitQueue;
}
