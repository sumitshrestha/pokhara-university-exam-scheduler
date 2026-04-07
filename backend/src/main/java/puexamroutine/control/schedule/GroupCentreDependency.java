
//                              !! RAM !!

package puexamroutine.control.schedule;

import java.util.Iterator;
import puexamroutine.control.domain.Group;

/**
 * This class contains the dependency of a particular group with other groups.
 *
 * @author Sumit Shresth
 */
public class GroupCentreDependency {
     
    public GroupCentreDependency( final puexamroutine.control.domain.Group grp, final puexamroutine.control.domain.list.GroupList grplist ){
        this.Group = grp;
        this.GroupList = grplist;        
    }
    
/////////////////////////////////////////////////////////////////////////////////
/////////////       CODE FOR INPUTTING      /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
    public void add( puexamroutine.control.domain.Group grp, int NetOverLapCent){
        if( ifInitially(NetOverLapCent, grp) )return;
        
        java.util.Iterator<puexamroutine.control.domain.Group> SortGrpListItr = this.SortedGroupList.iterator();        
        puexamroutine.control.domain.Group sortgrp=null;
        while( SortGrpListItr.hasNext() ){             
            sortgrp = SortGrpListItr.next();
            int sortgrp_netcent = this.GroupsOverlappingCentreMap.get( sortgrp );
            if( sortgrp_netcent < NetOverLapCent ){
                continue;
            }
            else
                if( this.onLessThan(sortgrp_netcent, NetOverLapCent, grp, sortgrp))
                    return;                
                else
                    if( onEqualOverlappingCentres(SortGrpListItr, NetOverLapCent, sortgrp, grp))
                        return;                                
        }
        if( ! this.SortedGroupList.contains(grp) ){
            insert( grp, NetOverLapCent );
        }
    }

    private void insert(Group grp, int NetOverLapCent) {
        this.SortedGroupList.add(grp);
        this.GroupsOverlappingCentreMap.put(grp, NetOverLapCent);
    }
    
    private void add(Group grp, Group sortgrp, int NetOverLapCent) {
        int index = this.SortedGroupList.indexOf(sortgrp);
        this.SortedGroupList.add(index, grp);
        this.GroupsOverlappingCentreMap.put(grp, NetOverLapCent);
    }
    
    private boolean ifInitially(int NetOverLapCent, Group grp) {
        if (this.SortedGroupList.isEmpty()) {
            insert( grp, NetOverLapCent );
            return true;
        }
        return false;
    }
    
    private boolean onEqualOverlappingCentres(Iterator<Group> SortGrpListItr, int NetOverLapCent, Group sortgrp, Group grp) {
        do {
            if (onEqualOverlappingCentres(NetOverLapCent, SortGrpListItr, grp, sortgrp)) {
                return true;
            }
        } while (SortGrpListItr.hasNext());
        return false;
    }

    private boolean onEqualOverlappingCentres(int NetOverLapCent, Iterator<Group> SortGrpListItr, Group grp, Group sortgrp) {
        if (this.GroupList.getTotalCandidates(sortgrp) <= this.GroupList.getTotalCandidates(grp)) {
            add(grp, sortgrp, NetOverLapCent);
            return true;
        }
        else {
            boolean t = SortGrpListItr.hasNext();
            if( !t ){
                insert( grp, NetOverLapCent );
                return true;
            }
            sortgrp = SortGrpListItr.next();
            int sortgrp_netcent = this.GroupsOverlappingCentreMap.get( sortgrp );
            return this.onLessThan( sortgrp_netcent, NetOverLapCent, grp, sortgrp);
        }        
    }
    
    private final boolean onLessThan( int sortgrp_netcent, int NetOverLapCent, puexamroutine.control.domain.Group grp, puexamroutine.control.domain.Group sortgrp ){
        if( sortgrp_netcent > NetOverLapCent ){
            add(grp, sortgrp ,NetOverLapCent );
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    /////////////       CODE FOR ACCESSING      /////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public void resetIterator(){
        this.SortedGroupItr = this.SortedGroupList.iterator();        
    }
    
    public puexamroutine.control.domain.Group getNextOptimalGroup(){
        return this.SortedGroupItr.next();
    }
    
    public boolean hasNext(){
        return this.SortedGroupItr.hasNext();
    }
    
    public java.util.List<puexamroutine.control.domain.Group> getSortedOptimalGroup(){
        return this.SortedGroupList;
    }
    
    public final puexamroutine.control.domain.Group getGroup(){
        return this.Group;
    }
    
    public boolean isSet( puexamroutine.control.domain.Group grp ){
        return this.GroupsOverlappingCentreMap.containsKey(grp);
    }
    
    public int getNetOverlappingCentres( puexamroutine.control.domain.Group grp ){
        if( !this.isSet(grp)) return -1;
        return this.GroupsOverlappingCentreMap.get(grp);
    }
    
    /**
     * This is an important function that is used to get the groups that can be feasibly kept with it in same time without affecting the constraints(as far as possible not sure  i need to check)
     * @return The list of groups that are sorted
     */
    public java.util.List<puexamroutine.control.domain.Group> getFeasibleSortedGroupList(){
        java.util.List<puexamroutine.control.domain.Group> Feas = new java.util.LinkedList<puexamroutine.control.domain.Group>();
        java.util.Iterator<puexamroutine.control.domain.Group> SortGrpItr = this.SortedGroupList.iterator();
        while( SortGrpItr.hasNext() ){
            puexamroutine.control.domain.Group tempGrp = SortGrpItr.next();
            if( this.isFeasible( tempGrp )){
                Feas.add( tempGrp );
            }
            else
                break;// come out
        }
        return Feas;
    }
    
    /**
     * This method checks if the specified group can be feasibly attended with this group.
     * 
     * @param grp The specified group to be checked with
     * @return true if feasible else false
     */
    private boolean isFeasible( puexamroutine.control.domain.Group grp ){
        int cent1 = this.GroupsOverlappingCentreMap.get( grp );
        java.util.Iterator<java.lang.Integer> OverCentItr = this.GroupsOverlappingCentreMap.values().iterator();
        int total =0;
        while( OverCentItr.hasNext() ){
            total += OverCentItr.next();            
        }
        int avg = total/this.SortedGroupList.size();
        return avg > 1 && cent1 > avg;
    }
    
    private final puexamroutine.control.domain.Group Group;    
    private final puexamroutine.control.domain.list.GroupList GroupList;
    
    private java.util.HashMap<puexamroutine.control.domain.Group, java.lang.Integer > GroupsOverlappingCentreMap = new java.util.HashMap<puexamroutine.control.domain.Group, java.lang.Integer >();    
    private java.util.LinkedList<puexamroutine.control.domain.Group> SortedGroupList = new java.util.LinkedList<puexamroutine.control.domain.Group>();
    
    private java.util.Iterator<puexamroutine.control.domain.Group> SortedGroupItr;        
}