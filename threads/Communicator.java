package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */


public class Communicator {

    private Lock communicatorMutex;
    private Condition2  speakersToSleep;
    private Condition2 listenersToSleep;
    private Condition2 currentSpeaker;
    private boolean occupied;
    private Integer message;
    private int listening;
    private int speaker;
    /**
     * Allocate a new communicator
     */
    public Communicator() {

        //Create the necesary lock used by the condition
        communicatorMutex = new Lock();

        //Create the condition for speakers.
        speakersToSleep=new Condition2(communicatorMutex);

        //Create the condition for listeners.
        listenersToSleep=new Condition2(communicatorMutex);

        //Create the currentSpeaker object.
        currentSpeaker=new Condition2(communicatorMutex);

        occupied=false;

        // Integer buffer
        message = null;

        //counter listening
        listening = 0;

        //counter speaker
        speaker  = 0;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param word the integer to transfer.
     */
    public void speak(int word) {

        //speaker acquires the lock
        communicatorMutex.acquire();

        //incremment the speaker variable.
        speaker++;

        // While message is not null,
        // then the speaker goes to sleep
        while (message != null){
            speakersToSleep.sleep();
        }

        //set flag that word is ready
        occupied=true;

        message =word;

        // wake up the listeners
        listenersToSleep.wake();

        //sleep the currentSpeaker.
        currentSpeaker.sleep();

        //decremment the speaker variable.
        speaker--;

        //speaker releases the lock
        communicatorMutex.release();
        
        
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return the integer transferred.
     */
    public int listen() {

        //speaker acquires the lock
        communicatorMutex.acquire();

        // increase the number of listener by one
        listening++;

        // while message is not null
        // then the listener goes to sleep

        while(message == null){
            listenersToSleep.sleep();
        }

        int receivedMessage;

        //Retreive the word and inform any speakers it is safe to write again.
        receivedMessage = message.intValue();

        message = null;

        //decrement the number of listener by one
        listening--;

        // wake up the speakers.
        speakersToSleep.wake();

        //wake the currentSpeaker.
        currentSpeaker.wake();


        //speaker releases the lock
        communicatorMutex.release();

        //return the integer transferred
        return receivedMessage;
    }

}
