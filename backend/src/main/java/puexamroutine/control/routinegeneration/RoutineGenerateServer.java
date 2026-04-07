
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.list.UniversityDataBean;
import puexamroutine.control.schedule.Result;
import puexamroutine.control.interfaces.*;
import java.rmi.*;
import java.rmi.server.ServerNotActiveException;
import java.rmi.registry.*;
import java.net.*;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 *
 * @author Sumit Shresth
 */
public class RoutineGenerateServer extends java.rmi.server.UnicastRemoteObject
                                   implements RoutineGenerateServerInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutineGenerateServer.class);
    
    public RoutineGenerateServer( int port ) throws RemoteException {    
        final String thisHost;
        try {
            InetAddress t = InetAddress.getLocalHost(); 
            thisAddress= t.toString();                                    
            thisHost = t.getHostName();
        }
        catch(Exception e){
            throw new RemoteException("can't get inet address.");
        }
        try{
            this.thisPort = port;            
            // create the registry and bind the name and object.
            registry = LocateRegistry.createRegistry( thisPort );            
            registry.rebind( this.SERVER_NAME, this );
            this.displayMessage("Scheduler server started."+"\nRunning at "+this.thisAddress+":"+this.thisPort);
        }
        catch(RemoteException e){
            throw e;
        }
    }

    /**
     * 
     * @param Time the server time in second
     */
    public Result generateRoutine( final String ClientID, UniversityDataBean Data, final int Min_Gap, final int Max_Gap,long Time ) throws RemoteException{
        try{                       
            if( ! this.isRegistered(ClientID) )
                return new Result(false, null, null, null,CLIENT_NOT_REGISTERED    );
            else
                this.displayMessage(ClientID + " requested for scheduling for "+Time+"seconds...");
            RoutineGenerator currentGenerator = new RoutineGenerator();
            ServerListener listener = new ServerListener( this.getDummyRemoteShedulerListener() );
            currentGenerator.setListener( listener );            
            //this.ClientListeners.put( ClientID, listener );
            this.ClientRequestList.put(ClientID, new ClientRequestBean(ClientID, currentGenerator, listener, Time ) );
            //System.out.println("Scheduling started...");
            boolean state = currentGenerator.generate(Data, Min_Gap, Max_Gap);
            this.ClientRequestList.remove( ClientID );
            if (!state) {
                this.displayMessage( ClientID+" requested Scheduling ended ABNORMALLY..." );
                return new Result(false, null, null, null,currentGenerator.getErrorMessage()  );            
            } else {
                displayMessage( ClientID+" requested Scheduling ended Normally..." );
                return new Result(state, currentGenerator.getCalander(), currentGenerator.getGroupProgramRoutine(), currentGenerator.getUnScheduledGroup(),null  );
            }
        }
        catch( Exception e ){
            LOGGER.error("Exception while generating routine for client {}", ClientID, e);
            return new Result(false, null, null, null,"scheduling could not be performed due to " + e.getMessage()  );
        }
    }
    
    public void cancel(final String ClientID) throws RemoteException{        
        try{            
            this.displayMessage( ClientID+" requested for cancelling Scheduling..." );            
            this.ClientRequestList.get( ClientID ).cancelRequest();
        }
        catch( Exception e ){
            LOGGER.error("Exception while cancelling routine for client {}", ClientID, e);
        }
    }
    
    public boolean continueScheduling( final String ClientID, final long extraTime ) throws RemoteException{        
        this.displayMessage(ClientID+ " requested for continuing processing by "+extraTime+" secs" );
        return this.ClientRequestList.get( ClientID ).continueProcessing( extraTime );
    }
        
    public synchronized String register() throws RemoteException{       
        //final String client = this.getClientHost();
        //if( client.equals( IP ) ){            
        this.displayMessage( "request for registeration was made" );
        final String id = getRegistrationNumber();            
        this.registeredClients.add( id );
        return id;
        /*}
        else
            throw new RemoteException( "wrong client IP provided" );*/
    }

    private void displayMessage(final String Message ) {
        LOGGER.info("{}\t{}", this.now(), Message);
    }
    
    private RemoteShedulerListener getDummyRemoteShedulerListener(){
        return new RemoteShedulerListener(){
            public void notify( SystemStepState state ) throws RemoteException{
                // do nothing
            }
            
            public boolean isCancelled() throws RemoteException{
                return false;
            }            
        };
    }
    
    public static String getClientHost() throws ServerNotActiveException {        
        return java.rmi.server.RemoteServer.getClientHost();
    }

    private String getRegistrationNumber() {
        String id = null;
        do {
            id = getRandomString( this.CLIENT_ID_MAX_LEN );
            //System.out.println("this is id "+ id );
        } while ( this.isRegistered(id) );
        return id;
    }
    
    private boolean isRegistered( final String id ){        
        return id != null && this.registeredClients.contains(id);
    }
        
    private final String getRandomString( final int max ) {
        Random r = new Random();
        return Long.toString(Math.abs(r.nextLong()), max );
    }
    
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return '['+sdf.format(cal.getTime())+']';
    }
    
    private int thisPort;
    private String thisAddress;    
    private Registry registry;    // rmi registry for lookup the remote objects.    
    private HashMap<String, ClientRequestBean> ClientRequestList = new HashMap<String, ClientRequestBean>();
    private ArrayList<String> registeredClients = new ArrayList<String>();
    private final int CLIENT_ID_MAX_LEN = 35;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";  
}