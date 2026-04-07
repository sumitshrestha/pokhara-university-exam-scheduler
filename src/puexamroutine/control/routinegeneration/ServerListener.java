
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import puexamroutine.control.interfaces.*;
import java.rmi.*;
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
                return this.isCancel || this.remoteListener.isCancelled();
            else
                return false;
        }
        catch( RemoteException e ){
            return true;
        }
    }
    
    public boolean isPaused(){
        return this.isPaused;
    }
    
    public void cancel(){
        //System.out.println("cancel called...");
        this.isCancel = true;
    }
    
    public void pause(){
        this.isPaused = true;
    }
    
    public void unPause(){
        this.isPaused = false;
    }
    
    private final RemoteShedulerListener remoteListener;
    private boolean isCancel = false;
    private boolean isPaused = false;
}