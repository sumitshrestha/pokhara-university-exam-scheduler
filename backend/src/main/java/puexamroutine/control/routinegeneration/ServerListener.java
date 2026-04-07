
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import puexamroutine.control.interfaces.*;
import java.rmi.*;
import java.util.concurrent.atomic.AtomicBoolean;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;

/**
 * This class is makes this server the listner of the schduler code.
 * Since, the server is remote it has remote listener.
 * But, The scheduler has not to bother about the remoteness.
 * So, To encapsulate this info, This class is made.
 * It takes the remote listner and itself becomes local listener to the scheduler.
 * 
 * @author Sumit Shresth
 */
class ServerListener implements DomainListener{
    
    public ServerListener( final RemoteShedulerListener listener ){
        this.remoteListener = listener;
    }
    
    public void notify( SystemStepState state ){
        try{
            if( this.remoteListener != null )
                this.remoteListener.notify(state);
        }
        catch( RemoteException e ){            
        }
    }
    
    public boolean isCancelled(){
        try{
            if( this.remoteListener != null )
                return this.isCancel.get() || this.remoteListener.isCancelled();
            else
                return false;
        }
        catch( RemoteException e ){
            return true;
        }
    }
    
    public boolean isPaused(){
        return this.isPaused.get();
    }
    
    public void cancel(){
        //System.out.println("cancel called...");
        this.isCancel.set(true);
    }
    
    public void pause(){
        this.isPaused.set(true);
    }
    
    public void unPause(){
        this.isPaused.set(false);
    }
    
    private final RemoteShedulerListener remoteListener;
    private final AtomicBoolean isCancel = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
}