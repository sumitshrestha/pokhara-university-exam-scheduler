
//                          !! RAM !!

package puexamroutine.control.domain.routine;

import puexamroutine.control.domain.*;
import puexamroutine.control.domain.list.*;
import java.util.*;

/**
 * This class is the routine for a particular group.
 * This is essential class since, a particular exam may span in multiple program, which can create difficulty when shifting exams for one program that may affect other program.
 *
 * @author Sumit Shresth
 */
public class ProgramGroupRoutine implements java.io.Serializable{
    
    public ProgramGroupRoutine( final ProgramList prgs ){
        this.grp = prgs.getGroup();
        this.prgList = prgs;
        Iterator<Program> prgItr = prgs.getPrograms().iterator();
        while( prgItr.hasNext() ){
            Program prg = prgItr.next();
            this.ProgramRoutineMap.put( prg, new ProgramRoutine(prg) );
        }
    }
    
    public final Group getGroup(){
        return this.grp;
    }
    
    public final ProgramList getPrograms(){
        return this.prgList;
    }
    
    public final ProgramRoutine getProgramRoutine( Program prg ){
        return this.ProgramRoutineMap.get(prg);
    }
    
    public final Collection<ProgramRoutine> getProgramRoutine(){
        return this.ProgramRoutineMap.values();
    }
    
    private final Group grp;
    private final ProgramList prgList;
    
    private HashMap<Program, ProgramRoutine> ProgramRoutineMap = new HashMap<Program, ProgramRoutine>();
}