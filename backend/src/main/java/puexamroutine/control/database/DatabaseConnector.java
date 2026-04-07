
//                          !! RAM !!

package puexamroutine.control.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sumit Shresth
 */
public class DatabaseConnector implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnector.class);
    
    /**
     * As MySQL is used to develop this system, So, The default Database URL is MySQL Database URL.
     * @return Default Database URL for this System
     */
    public static final String getDefaultDatabaseURL(){
        return "jdbc:mysql://127.0.0.1:3306/";
    }
    
    /**
     * As the Driver used was MYSQL, So, the Default Driver Name is MySQL Driver name.
     * @return Default name of Driver 
     */
    public static final String getDefaultDriverName(){
        return "com.mysql.cj.jdbc.Driver";
    }
    
    public static final String getDefaultUserName(){
        return "root";
    }
    
    public static final String getDefaultPassword(){
        return "root";
    }
    
    public DatabaseConnector( final String DriverNm, final String url ){        
        this.DatabaseURL = url;
        this.DriverName = DriverNm;
    }
    
    public DatabaseConnector(){
        this.DatabaseURL = this.getDefaultDatabaseURL();
        this.DriverName = this.getDefaultDriverName();
    }
    
    @Override
    public void close(){
        this.disconnect();
    }
    
    public boolean connect( final String user, char[] passd ){
        try{
            Class.forName(DriverName);            
            this.Conn = java.sql.DriverManager.getConnection( this.DatabaseURL, user, String.valueOf(passd)  );            
            return true;
        }
        catch( Exception e ){
            LOGGER.error("Cannot connect to database", e);
            return false;
        }
    }
    
    public void setDatabaseURL( final String URL ){
        this.DatabaseURL = URL;
    }
    
    public java.sql.Connection getConnection(){
        return this.Conn;
    }
    
    public void disconnect(){
        try{
            this.Conn.close();
        }
        catch( Exception e ){
            LOGGER.warn("Error while disconnecting from database", e);
        }
    }
    
    private java.sql.Connection Conn;
    private String DatabaseURL;
    private String DriverName;
}