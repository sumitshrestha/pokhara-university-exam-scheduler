
//                              !! RAM !!

package puexamroutine.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;
import java.rmi.*;
import java.rmi.registry.*;
import puexamroutine.control.domain.list.UniversityDataBean;
import puexamroutine.control.interfaces.*;
import puexamroutine.control.schedule.Result;
import puexamroutine.control.routinegeneration.RoutineGenerateServerInterface;
import java.awt.event.*;

/**
 *
 * @author Sumit Shresth
 */
public class RemoteController implements RemoteShedulerListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteController.class);
    
    private final boolean DEBUG = false;

    public void initialize( DomainListener list ){
        this.UiListener = list;        
    }
    
    public void notify( SystemStepState state ) throws RemoteException{
        if( this.UiListener != null )
            this.UiListener.notify(state);
    }
     
    public boolean isCancelled() throws RemoteException{
        if( this.UiListener != null )
            return this.UiListener.isCancelled();
        else
            return false;
    }
    
    public Result scheduleRemoteRoutine( final String ip, final int port, final UniversityDataBean Data, final int Min_Gap, final int Max_Gap  ){
        try{
           // get the “registry”
           Registry registry=LocateRegistry.getRegistry( ip,(new Integer( port )).intValue() );
           // look up the remote object
           rmiServer=(RoutineGenerateServerInterface)(registry.lookup( RoutineGenerateServerInterface.SERVER_NAME ));           
           notifyUserListener();
           this.watchForCancel();           
           this.watchForTimeContinuation();
           Result rs;
           do{
               if( this.ID == null ){
                   if(this.DEBUG)LOGGER.debug("Registering for the first time");
                   this.ID = rmiServer.register();
               }
                // call the remote method
                rs = rmiServer.generateRoutine( this.ID, Data, Min_Gap, Max_Gap , this.ProcessingTime );
                if(this.DEBUG)LOGGER.debug("Result obtained from remote scheduler");
                final String error = rs.getError();
                if( RoutineGenerateServerInterface.CLIENT_NOT_REGISTERED.equals( error ) ){
                    if(this.DEBUG)LOGGER.debug("Client not registered, registering again");
                    this.ID = rmiServer.register();
                }
                else{
                    this.scheduling_end = true; //signal others abt ending of scheduling
                    return rs;
                }
           }
           while( true );           
       }
       catch(RemoteException e){
           LOGGER.error("Remote scheduling failed", e);
           return new Result(false,null,null,null,e.getMessage());
       }
       catch(NotBoundException e){
           LOGGER.error("Remote scheduler binding failed", e);
           return new Result(false,null,null,null,e.getMessage());
       }
    }
    
    private void notifyUserListener() {        
        SystemStepState state = new SystemStepState();
        state.setPredictable(false);
        this.UiListener.notify(state);
    }
    
    private void watchForCancel(){
        Thread t = new Thread( new java.lang.Runnable() {
            public void run() {
                try{
                    scheduling_end = false;
                    while( ! ( UiListener.isCancelled() || scheduling_end ) ){
                    }                    
                    if( UiListener.isCancelled() ){
                        if(DEBUG)LOGGER.debug("Cancellation requested by UI");
                        rmiServer.cancel( ID );
                    }
                }
                catch( Exception e ){
                    LOGGER.error("Error while cancelling remote scheduling", e);
                }
            }
        });
        //t.setPriority( Thread.MAX_PRIORITY );
        t.start();
    }
    
    private void watchForTimeContinuation(){
        final int time = (int)this.ProcessingTime * 1000;        
        javax.swing.Timer timer = new javax.swing.Timer( time, new java.awt.event.ActionListener() {        
            public void actionPerformed( ActionEvent e) {
                if( ! scheduling_end ){                    
                    // if server says it cannot continue scheduling (returns false), it means the previous proecessing request has been cancelled. so, scheduling ended obtained by not'in return result
                    try{
                        if(DEBUG)LOGGER.debug("Attempting to continue scheduling");
                        scheduling_end = ! rmiServer.continueScheduling(ID, ProcessingTime);
                        if(DEBUG)LOGGER.debug("Continuation call completed");
                    }
                    catch( Exception ex ){
                        LOGGER.error("Exception in continue timer", ex);
                    }
                }
                else{
                    ( (javax.swing.Timer)e.getSource() ).stop();
                }                    
            }        
        });
        timer.start();
    }
    
    private DomainListener UiListener;
    private RoutineGenerateServerInterface rmiServer;
    private String ID=null;
    private boolean scheduling_end;
    private long ProcessingTime = 20;//seconds
}