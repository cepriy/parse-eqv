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
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;




public class Parse {

    public static void checkDateandGiveAccess() throws IOException, ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date current = new Date();
        System.out.println(dateFormat.format(current)); //2014/08/06 15:59:48
        Date limitDate = new SimpleDateFormat("yyyy/MM/dd").parse("2020/11/9");
        System.out.println(dateFormat.format(limitDate));
        if (current.after(limitDate)) {
            File f = new File("TextsBiling");
            f.delete();
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "Wy are sorry, the term of academic patent has expired");
            System.out.println("After");
        } else {
            System.out.println("Before");
            EquiveFrame.createFrame();
        }
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, ParseException {

        System.out.println("Start prog " + new Date());

        checkDateandGiveAccess();
//
//        parseeqv.SearchEquive.Conn();
//        parseeqv.SearchEquive.DB();
//        parseeqv.SearchEquive.GetRegCorrespondances();
//
//        parseeqv.SearchEquive.fillPhrasesPartidas();
//
//        parseeqv.SearchEquive.analyzeSplitPhrases();
//        parseeqv.SearchEquive.pRintSplitPhrases();
//        File file2 = new File("Results_Fokin_S_B_pseudeterms_equivalences_tool.txt");
//        PrintWriter pr = new PrintWriter(file2.getAbsolutePath());
//        EquiveWriter.saveResults("", pr, file2);


//        parseeqv.SearchEquive.Conn();
//        parseeqv.SearchEquive.DB();
//        long t1= System.currentTimeMillis();
//        parseeqv.SearchEquive.GetAllorigWords();
//
//           System.out.println("GetWords vypolnialas " + (System.currentTimeMillis() - t1));
//
//           parseeqv.SearchEquive.GetRegCorrespondances();
//        String str1 = "( En el marco de la OSCE# ) las Partes Contratantes promover?n el fortalecimiento de la democracia# importancia el pluralismo pol?tico# y las normas y principios del Estado de Derecho y la protecci?n de los derechos humanos y libertades fundamentales.";
//        String str2 = "( У рамках процесу НБСЄ ) Високі Договірні Сторони сприятимуть зміцненню демократії# важливість політичного плюралізму# і норм і принципів правової держави# і також захисту прав людини і основних свобод.";
//        String [] s1= str1.split(" ");
//
//        String [] s2= str2.split(" ");
//        String [] [] arr={{"","", "", ""},{"","", "", ""}, {"","", "", ""}};
//
//        boolean b = parseeqv.SearchEquive.partirFraseNew(s1, s2);
//
//
////  boolean a=  parseeqv.SearchEquive.partirFrase(s1,s2,false );
//             parseeqv.SearchEquive.pRintSplitPhrases();


        //boolean b=  parseeqv.SearchEquive.partirFrase(parseeqv.SearchEquive.reverseArr(s1),parseeqv.SearchEquive.reverseArr(s2) );

        //   parseeqv.SearchEquive.fillPhrasesPartidas();


        //   parseeqv.SearchEquive.fillPhrasesPartidas();

        // parseeqv.SearchEquive.analyzeSplitPhrases();
        //  parseeqv.SearchEquive.pRintSplitPhrases();


//parseeqv.SearchEquive.SearchAfterRegCorrespondance();
        // parseeqv.SearchEquive.printPotential();
        //  parseeqv.SearchEquive.SearchRealEquivalences2();
        //   parseeqv.SearchEquive.analyzeSplitPhrases();
        //  parseeqv.SearchEquive.printPotential();
        //      parseeqv.SearchEquive.cleanMapTree();

        // System.out.println("Programa vypolnialas " + (System.currentTimeMillis() - t1));
//        parseeqv.SearchEquive.activateButtons();
//      parseeqv.SearchEquive.viewAndSaveEquivalences();


//        Statement stmt = parseeqv.SearchEquive.conn.createStatement();
//        stmt.execute("create table if not exists 'TABLE1' ( 'orig' text, 'trans' text);");
//        stmt.execute("insert into 'TABLE1' ('orig', 'trans') values ('name1', 'name2'); ");
//        //stmt.execute("INSERT INTO TABLE1 (orig, trans) " +             "VALUES (set[0], set[1]);");
//        // stmt.executeUpdate(sql);
//        stmt.close();

        System.out.println("End prog " + new Date());
    }
}