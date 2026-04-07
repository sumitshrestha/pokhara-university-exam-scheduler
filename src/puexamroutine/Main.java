
//                  !! RAM !!

package puexamroutine;

import java.awt.event.ActionEvent;
import puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution;
import puexamroutine.control.database.DatabaseConnector;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;
import puexamroutine.ui.MainFrame;
import java.awt.SplashScreen;
import javax.swing.*;          
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;

/**
 *
 * @author Sumit Shresth
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {   
//        args = new String[]{"3232"};
        if( args.length > 0){
            try{
                new puexamroutine.control.routinegeneration.RoutineGenerateServer( Integer.parseInt( args[0] ) );
            }
            catch( Exception e ){
                e.printStackTrace();
            }
        }
        else
            new Main().createUI();
    }

    private void createUI(){
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    initLookAndFeel();
                    frame = new MainFrame();
                    frame.setVisible(true);
                    SplashScreen s = SplashScreen.getSplashScreen();
                    if (s != null) {
                        s.close();
                    }
                    Thread.setDefaultUncaughtExceptionHandler(MemoryErrorHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private static boolean firstTesting( ){
        try{            
            String u="root";
            String p="root";
            
            puexamroutine.control.Controller c = new puexamroutine.control.Controller( DatabaseConnector.getDefaultDatabaseURL(), DatabaseConnector.getDefaultDriverName(), u, p.toCharArray(), puexamroutine.control.database.DatabaseReader.getDefaultCourseCodeField(), puexamroutine.control.database.DatabaseReader.getDefaultProgramField(), puexamroutine.control.database.DatabaseReader.getDefaultRegularCourseTableName(), puexamroutine.control.database.DatabaseReader.getDefaultCourseTable(), puexamroutine.control.database.DatabaseReader.getDefaultSemesterFieldName(), puexamroutine.control.database.DatabaseReader.getDefaultDatabaseName(), puexamroutine.control.database.DatabaseReader.getDefaultBackTable(), puexamroutine.control.database.DatabaseReader.getDefaultCandidateIDField(), puexamroutine.control.database.DatabaseReader.getDefaultProgramTableName(), puexamroutine.control.database.DatabaseReader.getDefaultFacultyFieldName(), puexamroutine.control.database.DatabaseReader.getDefaultLevelFieldName(),2,5 );
            c.setListener( new puexamroutine.control.interfaces.DomainListener() {
                public void notify(SystemStepState state) {                    
                }

                public boolean isCancelled() {
                    return false;
                }
                
                public boolean isPaused(){
                    return false;
                }
            });
            time = 0;
            finishflag = false;
            timer.start();
            System.out.println("\n\nThe timer is started...");
            c.start(false); 
            finishflag = true;
            System.out.println( "\n\nTotal processing time was " + time + "minutes" );
            System.out.println ( "Was it too much???" );
            return true;
        }
        catch( Exception e ){
            System.out.println( "Exception in 1st test ::"+ e.getMessage() );
            return false;
        }
    }
    
    private static boolean secondTesting( String vertex ){
        try{
            int input=0;
            do{
                int ver = Integer.parseInt( vertex );
                //int ver = 20;
                puexamroutine.control.routinegeneration.graph.domain.util.RandomGraph Randomer = new puexamroutine.control.routinegeneration.graph.domain.util.RandomGraph( ver );
        
                puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph = Randomer.getRandomGraph();
                
                puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorer painter = new puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorerOptimize1(null);
                painter.initialize(Graph, new puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer() {

                    public boolean isFeasible(GraphColoringSolution OptGraph) {
                        return true;
                    }

                    public boolean isOptimal(GraphColoringSolution OptGraph) {
                        return false;
                    }

                    public boolean isMoreOptimal(GraphColoringSolution NewGraph, GraphColoringSolution OldGraph) {
                        return true;
                    }
                });
        
        time = 0;
        finishflag = false;
        
        timer.start();
        
        painter.color();
        
        finishflag = true;
        
        System.out.println("It took " + time + "Seconds to complete");
        
                puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface temp = painter.getOptimumGraph();
        if( temp == null ){
            System.out.println("temp is null");
        }
        else
            temp.report();
                
        System.out.println("\n\n do u wanna continue??");
        input = System.in.read();
            }
            while( input != 1 );
            return true;
        }
        catch( Exception e){
            System.out.println( "Specify maximum vertex as argument " + e.getMessage() );
            return false;
        }
    }
    
    private static void stop(){
        timer.stop();
    }
    
    private static void initLookAndFeel(){
        String lookAndFeel = null;
        try{
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
                //lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                UIManager.setLookAndFeel(lookAndFeel);
                JFrame.setDefaultLookAndFeelDecorated(true);
        } 
            
        catch (ClassNotFoundException e) {
            System.err.println("Couldn't find class for specified look and feel:" + lookAndFeel);
            System.err.println("Did you include the L&F library in the class path?");
            System.err.println("Using the default look and feel.");
        } 
            
            catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } 
            
            catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    
    /**
     * This method is called when the system resources is taken up completly and system is unable to continue
     * So, instead of simply ending abruplty, This func will degrade gracefully
     */
    private void degradeGracefully( Thread t,Throwable r ){
//        JOptionPane.showMessageDialog( null, "There was serious error in system. \nThe system will shutdown.\nsorry for inconvinience\n"+r.getMessage(), "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE );        
        //this.frame.degradeGracefully();        
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nThere was system error.So, System will shut down\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n".toUpperCase());
        JOptionPane.showMessageDialog( null, "There was serious error in system. \nThe system will restart.\nsorry for inconvinience\n".toUpperCase(), "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE );        
        System.gc();
        //this.createUI();
    }
    
    private static long time;
    static javax.swing.Timer timer = new javax.swing.Timer( 60000, new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if( finishflag )
                stop();
            else
                time++;
            }
    });
    static boolean finishflag;
    private Thread.UncaughtExceptionHandler MemoryErrorHandler = new Thread.UncaughtExceptionHandler(){
        public void uncaughtException(Thread t, Throwable e){            
            degradeGracefully( t, e );
        }
    };
    private MainFrame frame;
}