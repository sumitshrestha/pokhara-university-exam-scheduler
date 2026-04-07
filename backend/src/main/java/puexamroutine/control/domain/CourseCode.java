
//                              !! RAM !!

package puexamroutine.control.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.interfaces.CourseCodeInterface;

/**
 *
 * @author Sumit Shresth
 */
public class CourseCode implements CourseCodeInterface, java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseCode.class);
    
    public CourseCode(){        
    }
    
    public CourseCode( String courseCode ){            
        this.setCourseCode(courseCode.trim());
    }
    
    private boolean setCourseCode( String CourseCode ){        
        String[] array = CourseCode.split( String.valueOf( this.PrimarySeparator  ) );
        this.Prefix = array[0];
        
        boolean flag = false;
        int i=0;
        for( ;i<array[1].length();i++ ){
            if( array[1].charAt(i) == this.SecondarySeparator  ){
                flag = true;
                break;
            }
        }
        if( flag ){
            this.Code = array[1].substring( 0, i );
            this.Credit = array[1].substring( i+1 );
        }        
        if( this.Code == null || this.Credit == null || this.Prefix == null ){
            LOGGER.error("Malformed course code encountered: {}", CourseCode);
            LOGGER.error("System will exit now");
            System.exit(1);                        
        }
        this.Code = this.Code.trim();
        this.Credit = this.Credit.trim();
        this.Prefix = this.Prefix.trim().toUpperCase();
        return true;        
    }
    
    public void setPrefix ( final String prefix){
        this.Prefix = prefix.trim().toUpperCase();
    }
    
    public void setCode( final String code ){
        this.Code = code.trim();
    }
    
    public void setCourseCredit( final String Credit ){
        this.Credit = Credit.trim();
    }
    
    public final String getCode(){
        return this.Code;
    }
    
    public final String getPrefix(){
        return this.Prefix;
    }
    
    public final String getCourseCode(){
        return this.toString();
    }
    
    public final String toString(){
        return this.Prefix + PrimarySeparator + this.Code +this.SecondarySeparator+ this.Credit;
    }
    
    public final String getCourseCredit(){
        return this.Credit;
    }
    
    public final boolean equals( CourseCode c ){
        return this == c || this.toString().equals( c.toString() );
    }

    private String Prefix;
    private String Code,Credit;
}