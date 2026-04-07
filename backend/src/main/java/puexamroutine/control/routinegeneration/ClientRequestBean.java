
//                          !! RAM !!

package puexamroutine.control.routinegeneration;

import java.awt.event.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a single request from any client.
 * This class holds all necessary informatin and functionalities necesary for representing a request and its current state.
 * It is used to deal with tasks to be performed with it.
 * <br>
 * This class was explicitly developed to implement the concept of clent crashing in client-server application.
 * Since, Heavy processing is being done by the system, it needs to see that client are serious enough to continue processing otherwise it would be wastage to continue marathon processing by server.
 * This is because client once requesting the server does nothing but waits for reply from server.
 * This allows client to know of server's presence but not vice versa.
 * That is, Server does not knows if client is still alive or not.
 * In such case, server continues to process till result is obtained.
 * But, if client system is not responding or has crashed, then it is simply useless to carry on processing by server.
 * <br>
 * So, <strong>SERVER MUST KNOW ABOUT CLIENT'S PRESENCE</strong>
 * <br>
 * To implement this, I have modified the signature of server and developed this code.
 * This class takes the responsibility to keep track of client at each and every time after client requests it for processing
 * This is achived by using the continue processing method in the signature.
 * It allows the system to continue processing by specified amount.
 * Thid method is regularly called by the client to server to know server of its presence.
 * Initially, while requesting for processing, client sends the time for which it must process.
 * After the specified time is elapsed, this class will put the processing to pause state so that it pauses temporarily.
 * This happens only if the client has not specified the continue time for current request.
 * If it has then it continus it for specified amount of time.
 * For this a unlimited sized buffer is maintained to hold the incomming continue time.
 * There is no ahead time gap (jitter) but a late time gap (Maximum end to end delay) for continue request arrivals.
 * Once, the processing is paused (when no continue time is specified), the timing is implicilty sets that time outs if this class does not unpauses the processing within maximum tolerable time out time.
 * So, the process is gauranteed to end even if requesting client fails abnorammly (by failing to call continue processing method periodically)
 * <br><br>
 * NOTE: The above specified problem could also be solved using Nelson's method where server explicilty asks client about its presence.
 * But, it has overhead as server too need to know of client which becomes problem in multiple client case.
 * <br>
 * 
 * @see RoutineGenerateServerInterface
 * @see IndependentRegularCourseListAnalyzer
 * 
 * @author Sumit Shresth
 */
public class ClientRequestBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRequestBean.class);
    
    private final boolean DEBUG = false;
    
    public ClientRequestBean( final String client, final RoutineGenerator gen, final ServerListener listener, final long initial_Time ){
        
        this.RQ_ClientID = client;
        this.ServiceListener = listener;        
        this.Generator = gen;
        this.TargetTime = initial_Time;                
        
        //System.out.println("timer will tick @"+ ((int)this.TargetTime) * this.timeUnit);
        this.timer = new javax.swing.Timer( ((int)this.TargetTime) * this.timeUnit, new java.awt.event.ActionListener() {
        
            public void actionPerformed( ActionEvent e) {
                onTick();
            }
        
        });
        
        this.timer.start();        
    }
    
    public final String getClient(){
        return this.RQ_ClientID;
    }
        
    public void cancelRequest(){        
        this.ServiceListener.cancel();
    }
    
    public synchronized boolean continueProcessing( final long time ){
        
        if( this.ServiceListener.isCancelled() )
            return false;
        
        this.TimeList.offer(time);
        
        if( this.ServiceListener.isPaused() ){
            synchronized (this.timerLock) {
                restartPausedProcessing();
            }
        }
        
        return true;
    }
        
        private final void onTick(){
            synchronized (this.timerLock) {
            this.timer.stop();
            this.onSingleServiceTimeCompletion();
            }
    }
    
    private void onSingleServiceTimeCompletion(){
        if( this.TimeList.isEmpty() ){
            try{
                if(this.DEBUG)LOGGER.debug("{} time has expired; pausing processing", this.RQ_ClientID);
                this.ServiceListener.pause();
            }
            catch( Exception e ){
                if(this.DEBUG)LOGGER.debug("Exception while pausing request {}; cancelling it", this.RQ_ClientID, e);
                this.cancelRequest();
            }
        }
        else{
            restartProcessing();
        }
    }
        
    private void restartPausedProcessing() {
        this.ServiceListener.unPause();
        this.restartProcessing();
    }

    private void restartProcessing() {
        Long nextTime = this.TimeList.poll();
        if (nextTime == null) {
            this.ServiceListener.pause();
            return;
        }
        this.TargetTime = nextTime;
        this.timer.setInitialDelay(((int) this.TargetTime) * this.timeUnit);
        this.timer.setDelay(((int) this.TargetTime) * this.timeUnit);
        this.timer.restart();
    }
    
    private final String RQ_ClientID;    
    private final ServerListener ServiceListener;
    private final RoutineGenerator Generator;
    private Queue<java.lang.Long> TimeList = new ConcurrentLinkedQueue<java.lang.Long>();
    private javax.swing.Timer timer = null;
    private final Object timerLock = new Object();
    private final int timeUnit = 1000;//1 second unit
    private long TargetTime;    
}