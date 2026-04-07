
//                              !! RAM !!

package puexamroutine.ui.components;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;

/**
 *
 * @author Sumit Shresth
 */
public class PrintingService implements Printable{
    
    public PrintingService( Container f) {
        frameToPrint = f;
    }

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        /* Now print the window and its visible contents */
        frameToPrint.printAll(g);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    private Container frameToPrint;
}