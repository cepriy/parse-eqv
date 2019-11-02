package parseeqv;
/**
 * Created by Sergiy on 09.02.2017.
 * <p>
 * Created by Sergiy on 09.05.2016.
 */
/**
 * Created by Sergiy on 09.05.2016.
 */


// TO DO: Clean DB AFTER SESSION (PROPOSE THIS OPTION TO THE USER)


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;




public class Parse {

    public static void checkDateandGiveAccess() throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date current = new Date();
        System.out.println(dateFormat.format(current)); //2014/08/06 15:59:48
        Date limitDate = new SimpleDateFormat("yyyy/MM/dd").parse("2020/12/9");
        System.out.println(dateFormat.format(limitDate));
        if (current.after(limitDate)) {
            File f = new File("TextsBiling");
            f.delete();
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "Wy are sorry, the term of academic patent has expired");
            System.out.println("After");
        } else {
            System.out.println("Before");
             new EquiveFrame().run();
        }
    }


    public static void main(String[] args) throws ParseException {

        System.out.println("Start prog " + new Date());

        checkDateandGiveAccess();

        System.out.println("End prog " + new Date());
    }
}