package parseeqv;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergiy on 03.04.2017.
 */
class SearchEquive {

    public static TreeMap<String, String> hMapOrTr = new TreeMap<>();
    public static String pathorig;
    public static String pathtrans;

    public static boolean newSessionListenerAdded = false;

    public static Connection conn;
    public static ResultSet resSet2;

    // public static boolean areKeyWords=false;

    public static boolean txtTypeSelected = false;
    public static boolean bActiobLisChooser = false;
    public static JFileChooser chooser = new JFileChooser();
    public static int readLinesCounter = 0;
    public static int amountOfMatches = 0;

    // Filtry
    public static int regCorrAmount;
    public static int additionalRegCorrCounter = 0;
    public static int maxAmountToBeAnalyzedByUser = 77;
    public static int iMaxEquivLength = 105;
    public static double acceptableCoefficientDifference = 1.42;
    public static int showedEquivNum = 0;
    public static boolean Registr = true;
    public static int iMinCoincidenceNum = 4;
    public static int shortSentenceLimit = 10;
    public static short minSentenceLength = 5;
    public static boolean optionIncludeShortSentencesAutomatically = true;
    public static boolean forHumanUser = false;
    public static int lengthForRecursionLimit = 30;
    public static int contextDepth = 7;
    public static int splittedFragmLength = 60;
    public static boolean maintainCommas = false;

    public static String curTable = "AllRegCorr";
    public static String curField1;
    public static String curField2;
    public static boolean btSavedLstnr = false;


    public static int savedFileCounter = 0;
    public static int analyzedEquivalenceCount = 0;
    public static boolean bAcceptListner = false;
    public static boolean bIgnoreListner = false;
    public static boolean bSaveExit = false;
    public static int totalRegEqCount = 0;
    public static TreeMap<String, Integer> potentialEquivToBeAnalyzed = new TreeMap<>();
    public static TreeMap<String, String> mapRegCorrespondances = new TreeMap();

    public void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:CorrespondancesDB");
    }

    public static void DB() {
        showedEquivNum = 0;
    }


    public static void sortPotential(String sEquiv) {
        sEquiv = sEquiv.replace(",", "").trim();
        if (potentialEquivToBeAnalyzed.containsKey(sEquiv)) {
            potentialEquivToBeAnalyzed.put(sEquiv, potentialEquivToBeAnalyzed.get(sEquiv) + 1);
        } else {
            potentialEquivToBeAnalyzed.put(sEquiv, 1);
        }

    }

    public static void printPotential() {
        for (Map.Entry<String, Integer> m : potentialEquivToBeAnalyzed.entrySet()) {
            System.out.println("Potentials: " + m.getKey() + " " + m.getValue());
            // txtAr.append(wordCount.getKey() + " " + wordCount.getValue() + "\n");
        }
    }

    public static String CleanExpression(String expression) {
        if (!Registr) expression = expression.toLowerCase();

        String strTemp = "";
        int i = 0;
        if ((int) expression.charAt(0) == 32) i = 1;//Proskakivaiem pervyi probel
        for (; i < expression.length(); i++) {
            if ((i == expression.length() - 1 && (((int) expression.charAt(i) == 32)) || (expression.charAt(i) == '\0') || ((expression.charAt(i) == '.')) || ((expression.charAt(i) == ',') && !maintainCommas) || (expression.charAt(i) == ':') || (expression.charAt(i) == '#') || (expression.charAt(i) == ';') || (expression.charAt(i) == '*')) || (expression.charAt(i) == '(') || (expression.charAt(i) == ')')) {
                break; // TAKE OFF THE LAST SYMBOL IF ONE OF THE ABOVE INDICATED
            }

            if ((int) expression.charAt(i) == 32 && (int) expression.charAt(i - 1) == 32) {
            } else                           //REDUCE TWO SPACES IN A ROW
            {
                strTemp = strTemp + expression.charAt(i);
            }
        }

        return strTemp;
    }

    public static void saveIfEqualString(String origFragment, String transFragment) {


        mapRegCorrespondances.put(origFragment, transFragment);

        additionalRegCorrCounter++;

        totalRegEqCount++;
    }

    public static void analyzeSplitPhrases() {

        for (int i = 0; i < hMapOrTr.size(); i++) {
            System.out.println("Sentences size "+hMapOrTr.size());
            String orig = (String) hMapOrTr.keySet().toArray()[i].toString().toLowerCase();
            String[] phrorig = orig.split(" ");
            String trans = hMapOrTr.get(hMapOrTr.keySet().toArray()[i]).toLowerCase();
            String[] phrtrans = trans.split(" ");
            if (orig.length() < splittedFragmLength && trans.length() < splittedFragmLength)// little fragments a more likely to give precise results
                analyzeCurrentSplittedSentence(phrorig, phrtrans);

        }
    }

    public static void analyzeCurrentSplittedSentence(String[] phrorig, String[] phrtrans) {
        int phr_orig_lngth = phrorig.length;
        int phr_transl_lngth = phrtrans.length;

        for (int orig_count = 0; orig_count < phr_orig_lngth; orig_count++) {

            for (int transl_count = 0; transl_count < contextDepth && transl_count < phr_transl_lngth; transl_count++) {
                if (isValidEquivalent(phrorig[orig_count], phrtrans[transl_count]))
                    sortPotential(phrorig[orig_count] + "#" + phrtrans[transl_count]);
                if (orig_count < (phr_orig_lngth - 2) && isValidEquivalent(phrorig[orig_count] + " " + phrorig[orig_count + 1], phrtrans[transl_count]))
                    sortPotential(phrorig[orig_count] + " " + phrorig[orig_count + 1] + "#" + phrtrans[transl_count]);
                if (orig_count < (phr_orig_lngth - 2) && transl_count < (phr_transl_lngth - 2) && isValidEquivalent(phrorig[orig_count] + " " + phrorig[orig_count + 1], phrtrans[transl_count] + " " + phrtrans[transl_count + 1]))
                    sortPotential(phrorig[orig_count] + " " + phrorig[orig_count + 1] + "#" + phrtrans[transl_count] + " " + phrtrans[transl_count + 1]);
                if (transl_count < (phr_transl_lngth - 2) && isValidEquivalent(phrorig[orig_count], phrtrans[transl_count] + " " + phrtrans[transl_count + 1]))
                    sortPotential(phrorig[orig_count] + "#" + phrtrans[transl_count] + " " + phrtrans[transl_count + 1]);
                if (transl_count < (phr_transl_lngth - 2) && isValidEquivalent(phrorig[orig_count], phrtrans[transl_count + 1]))
                    sortPotential(phrorig[orig_count] + "#" + phrtrans[transl_count + 1]);
                if (orig_count < (phr_orig_lngth - 2) && isValidEquivalent(phrorig[orig_count + 1], phrtrans[transl_count]))
                    sortPotential(phrorig[orig_count + 1] + "#" + phrtrans[transl_count]);
                saveIfEqualString(phrorig[orig_count], phrtrans[transl_count]);
                if (transl_count < (phr_transl_lngth - 2)) saveIfEqualString(phrorig[orig_count], phrtrans[transl_count + 1]);
                if (orig_count < (phr_orig_lngth - 2)) saveIfEqualString(phrorig[orig_count + 1], phrtrans[transl_count]);
            }
        }
    }

    public static String firstCharToLowerCase(String string) {
        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        string = new String(c);
        return string;
    }


    public static boolean areKeyWordsContainingSentences(String sentenceOr, String sentenceTr) {

        for (int i = 0; i < ParseConstants.keyWordsMarkers.length; i++) {
            if (sentenceOr.contains(ParseConstants.keyWordsMarkers[i])) {
                for (int k = 0; k < ParseConstants.keyWordsNames.length; k++) {
                    for (int l = 0; l < ParseConstants.keyWordsNames.length; l++) {
                        if (sentenceOr.equals(ParseConstants.keyWordsNames[k]) && sentenceTr.equals(ParseConstants.keyWordsNames[l])) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void parseKeyWordsSentence(String sentenceOr, String sentenceTr) {

        String[] keyTermsorig = sentenceOr.split(", ");
        String[] keyTermstrans = sentenceTr.split(", ");

        for (int i = 0; i < keyTermsorig.length && i < keyTermstrans.length; i++) {
            if (i == 0) {
                for (int f = 0; f < ParseConstants.keyWordsNames.length; f++) // DELETES "Keywords" and similar from equivalents
                {
                    keyTermsorig[i] = keyTermsorig[i].replace(ParseConstants.keyWordsNames[f], "");
                    keyTermstrans[i] = keyTermstrans[i].replace(ParseConstants.keyWordsNames[f], "");
                }
            }
            hMapOrTr.put(keyTermsorig[i], keyTermstrans[i]);
        }
    }

    public static void includeShortSentencesAutomatically(String orig, String trans) {
        if (orig.length() < shortSentenceLimit && orig.length() > minSentenceLength && trans.length() < shortSentenceLimit && trans.length() > minSentenceLength) {
            hMapOrTr.put(firstCharToLowerCase(CleanExpression(orig)), firstCharToLowerCase(CleanExpression(trans)));
        }
    }

    public static String[] splitCleanSentence(String sentence) {
        String strorigTmp[];
        if (sentence.contains("("))
            sentence = sentence.replace("(", "parenthesisone ");
        sentence = sentence.replace(")", " secparenthesis");
        if (maintainCommas) {
            strorigTmp = sentence.split("[\\s.#;\\n\\t]+");
        } else strorigTmp = sentence.split("[\\s.#;,\\n\\t]+");
        return strorigTmp;
    }

    public static void readOriginalTranslFilesTxt() throws ClassNotFoundException, SQLException, IOException {

        File forig = new File(pathorig);
        File ftrans = new File(pathtrans);
        Scanner sc1 = new Scanner(forig, "UTF-8");
        Scanner sc2 = new Scanner(ftrans, "UTF-8");

        while (sc1.hasNext() && sc2.hasNext()) {
            String orig_line = sc1.nextLine();
            String trans_line = sc2.nextLine();

            filtreSentences(orig_line, trans_line);
        }
        if (!EquiveFrame.forHumanUser.isSelected()) forHumanUser = false;
        readLinesCounter = 0;
        amountOfMatches = 0;// CONNECT AGAIN RECURSION WHEN DOING PRELIMINAR ANALYSIS
    }

    public static void filtreSentences(String origLine, String transLine) {
        String strorigTmp[] = splitCleanSentence(origLine);

        String strtransTmp[] = splitCleanSentence(transLine);
        readLinesCounter++;
        if (optionIncludeShortSentencesAutomatically) includeShortSentencesAutomatically(origLine, transLine);

        if (areKeyWordsContainingSentences(strorigTmp[0], strtransTmp[0])) parseKeyWordsSentence(origLine, transLine);
        // For parsing correclty keywords lists in articles
        else {

            amountOfMatches += splitSentenceToEquivalents(strorigTmp, strtransTmp);
        }
        checkForWrongLanguageCombination(amountOfMatches, readLinesCounter);
    }

    public static void checkForWrongLanguageCombination(int amountOfMatches, int readLinesCounter) {
        if (amountOfMatches == 0 && readLinesCounter == 5) {
            String ObjButtons[] = {"Yes", "Countinue"};
            int PromptResult = JOptionPane.showOptionDialog(null, ParseConstants.wrongCombination, "", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
            if (PromptResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public static void readOriginalTranslFilesCsv() throws IOException {

        File flCSV = new File(pathorig);
        Scanner sc = new Scanner(flCSV, "UTF-8");
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] splLine = line.split(",");
            if (splLine.length == 2) {
                String orig = splLine[0];
                String trans = splLine[1];
                filtreSentences(orig, trans);
            }
        }
        if (!EquiveFrame.forHumanUser.isSelected()) forHumanUser = false;
        readLinesCounter = 0;
        amountOfMatches = 0;
    }

    public static void initFieldWithCurrentEquivSet(int count) {
        System.out.println("potentialEquivToBeAnalyzed "+potentialEquivToBeAnalyzed);
        if (count >= potentialEquivToBeAnalyzed.size()) {
            finishUserAnalysis();
            return;
        }
        Object key = potentialEquivToBeAnalyzed.keySet().toArray()[count];
        int coincidencesNum = potentialEquivToBeAnalyzed.get(potentialEquivToBeAnalyzed.keySet().toArray()[count]);

        if (coincidencesNum < iMinCoincidenceNum) {
            initFieldWithCurrentEquivSet(++analyzedEquivalenceCount);
        } else {
            String str = (String) key;
            String[] set = str.split("#");
            EquiveFrame.orig.setText(set[0]);
            EquiveFrame.trans.setText(set[1]);
        }
    }

    public static void saveUserEquiveToDatabase(String origEqv, String transEqv) throws SQLException {
//        PreparedStatement statement = conn.prepareStatement(
//                "INSERT INTO " + curTable + " (" + curField1 + "," + curField2 + ")    " +
//                        "            VALUES(?,?)");
//        statement.setString(1, origEqv);
//        statement.setString(2, transEqv);
//        statement.executeUpdate();


    }

    public static void finishUserAnalysis() {
        if (!EquiveFrame.forHumanUser.isSelected()) {
            forHumanUser = false;// CONNECT RECURSION WHEN DOING PRELIMINAR ANALYSIS
        }
        EquiveFrame.btAccept.setEnabled(false);
        EquiveFrame.btIgnore.setEnabled(false);

        System.out.println("Getting reg_correspondances amount: "+regCorrAmount);
        proposeSaveAutomatically();
        potentialEquivToBeAnalyzed.clear();

    }


    public static void acceptBtnActions() {
        bAcceptListner = true;

        System.out.println("MAX AMOUNTS "+showedEquivNum + " analyzed "+analyzedEquivalenceCount);
        if (showedEquivNum >= maxAmountToBeAnalyzedByUser ) {
            finishUserAnalysis();
        } else {
            try {
                String strFromOrig = EquiveFrame.orig.getText();
                String strFromTransl = EquiveFrame.trans.getText();
                hMapOrTr.put(strFromOrig, strFromTransl);
                saveUserEquiveToDatabase(strFromOrig, strFromTransl);
                initFieldWithCurrentEquivSet(analyzedEquivalenceCount);
                analyzedEquivalenceCount++;
                showedEquivNum++;
            } catch (SQLException e4) {
                JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO SQL");
            }
        }
    }

    public static void ignoreButtonActions() {
        bIgnoreListner = true;
        if (showedEquivNum >= maxAmountToBeAnalyzedByUser) {
            finishUserAnalysis();
        } else
            initFieldWithCurrentEquivSet(++showedEquivNum);
    }

    public static void activateButtons() {
        EquiveFrame.btIgnore.setEnabled(true);
        EquiveFrame.btAccept.setEnabled(true);
        if (!bAcceptListner) {
            EquiveFrame.btAccept.addActionListener(e -> acceptBtnActions());
        }

        if (!bIgnoreListner) {
            EquiveFrame.btIgnore.addActionListener(e -> ignoreButtonActions());
        }
    }

    public static void proposeSaveAutomatically() {

        JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "Starts saving automatically");

        EquiveFrame.btSaveAutomatic.setEnabled(true);
        EquiveFrame.btStart.setEnabled(true);
        EquiveFrame.btSaveAutomatic.setBackground(new Color(170, 80, 70));
        try {
            if (txtTypeSelected) readOriginalTranslFilesTxt();
            else readOriginalTranslFilesCsv();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO CLASS JDBC OR NO FILE OR NO SQL1");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, " NO FILE");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO SQL1");
        }

        if (!btSavedLstnr) EquiveFrame.btSaveAutomatic.addActionListener(e -> {
            btSavedLstnr = true;
            try {
                saveResults(EquiveFrame.res, EquiveFrame.fileResults);
            } catch (IOException e4) {
                JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO FILE");
            }
        });

        btSavedLstnr = true;
    }


    public static void saveResults(PrintWriter prWr, File file) throws IOException {

        savedFileCounter++;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            int countEquiv = 0;

            try {
                //   prWr.println("Coefficiente: " + acceptableCoefficientDifference);
                for (int i = 0; i < hMapOrTr.size(); i++) {
                    if (hMapOrTr.keySet().toArray()[i] != null && hMapOrTr.get(hMapOrTr.keySet().toArray()[i]) != null) {

                        String sorig = (String) hMapOrTr.keySet().toArray()[i];
                        String strans = hMapOrTr.get(hMapOrTr.keySet().toArray()[i]);

                        if (sorig.length() < 2 || strans.length() < 2) continue;

                        sorig = sorig.replace("parenthesisone ", "").replace(" secparenthesis", "");
                        strans = strans.replace("parenthesisone ", "").replace(" secparenthesis", "");

                        prWr.println(sorig + "," + strans + ",");
                        countEquiv++;
                    }
                }
                JOptionPane.showMessageDialog(EquiveFrame.pnlorig, ParseConstants.resultsSavedStr + " " + countEquiv + ParseConstants.equivalentsStr);

            } finally {
                prWr.close();
                System.out.println("File " + file.getAbsolutePath() + " saved");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO FILE");
            throw new RuntimeException(e);
        }



        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
            Runtime.getRuntime().exec(cmd);
        }
        else {
            Desktop.getDesktop().open(file);
        }
    }

    public static boolean atLeastOneCombinationMatches(Matcher matcherOr1, Matcher matcherOr2, Matcher matcherOr3, Matcher matcherTr1, Matcher matcherTr2, Matcher matcherTr3)

    {
        if (((matcherTr3 != null && matcherTr3.matches() && matcherOr3.matches()))
                || ((matcherTr3 != null && matcherTr3.matches() && matcherOr2.matches()))
                || ((matcherTr3 != null && matcherTr3.matches() && matcherOr1.matches()))
                || ((matcherTr2 != null && matcherTr2.matches() && matcherOr3.matches()))
                || (matcherTr2 != null && matcherTr2.matches() && matcherOr1.matches())
                || ((matcherTr2 != null && matcherTr2.matches() && matcherOr2.matches()))
                || (matcherTr1 != null && matcherTr1.matches() && matcherOr1.matches())
                || ((matcherTr1 != null && matcherTr1.matches() && matcherOr3.matches()))
                || ((matcherTr1 != null && matcherTr1.matches() && matcherOr2.matches()))


                )
            return true;
        else
            return false;
    }

    public static int splitSentenceToEquivalents(String[] storig, String[] sttrans) {
        int indOr;
        int indCorr;
        int indTransl = 0;
        int matchesCounter = 0;
        String equiveOr1 = "";
        String equiveTr1 = "";
        String equiveOr2 = "";
        String equiveTr2 = "";
        String equiveOr3 = "";
        String equiveTr3 = "";
        Pattern ptrn1, ptrn2;
        Matcher mtch1 = null;
        Matcher mtch2 = null;
        Matcher mtch1twoWords = null;
        Matcher mtch2twoWords = null;
        Matcher mtch1threeWords = null;
        Matcher mtch2threeWords = null;
        String tmporigMatch = "";
        for (indOr = 0; indOr < storig.length; indOr++) {
            //for (indCorr = 0; indCorr < totalRegEqCount; indCorr++) {
                for ( Map.Entry<String, String> entry : mapRegCorrespondances.entrySet()){
                ptrn1 = Pattern.compile(entry.getKey());
                if (indOr < storig.length) {
                    mtch1 = ptrn1.matcher(storig[indOr]);
                    tmporigMatch = storig[indOr];
                }
                if (indOr < storig.length - 1) mtch1twoWords = ptrn1.matcher(storig[indOr] + " " + storig[indOr + 1]);
                if (indOr < storig.length - 2)
                    mtch1threeWords = ptrn1.matcher(storig[indOr] + " " + storig[indOr + 1] + " " + storig[indOr + 2]);
                if (atLeastOneCombinationMatches(mtch1, mtch1twoWords, mtch1threeWords, mtch1, mtch1twoWords, mtch1threeWords)
                        ) {

                    for (indTransl = 0; indTransl < sttrans.length; indTransl++) {

                        ptrn2 = Pattern.compile(entry.getValue());
                        if (indTransl < sttrans.length) mtch2 = ptrn2.matcher(sttrans[indTransl]);
                        if (indTransl < sttrans.length - 1)
                            mtch2twoWords = ptrn2.matcher(sttrans[indTransl] + " " + sttrans[indTransl + 1]);
                        if (indTransl < sttrans.length - 2)
                            mtch2threeWords = ptrn2.matcher(sttrans[indTransl] + " " + sttrans[indTransl + 1] + " " + sttrans[indTransl + 2]);
                        if (
                                atLeastOneCombinationMatches(mtch1, mtch1twoWords, mtch1threeWords, mtch2, mtch2twoWords, mtch2threeWords)
                                ) {
                            hMapOrTr.put(tmporigMatch, sttrans[indTransl]);
                            matchesCounter++;

                            for (int i = 0; i < indOr && i < storig.length; i++) {
                                equiveOr1 = equiveOr1 + " " + storig[i];
                            }

                            for (int i = indOr; i < storig.length; i++) {
                                equiveOr2 = equiveOr2 + " " + storig[i];
                            }

                            for (int i = indOr + 1; i < storig.length; i++) {
                                equiveOr3 = equiveOr3 + " " + storig[i];
                            }
                            for (int i = 0; i < indTransl; i++) {
                                equiveTr1 = equiveTr1 + " " + sttrans[i];
                            }
                            for (int i = indTransl; i < sttrans.length; i++) {
                                equiveTr2 = equiveTr2 + " " + sttrans[i];
                            }
                            for (int i = indTransl + 1; i < sttrans.length; i++) {
                                equiveTr3 = equiveTr3 + " " + sttrans[i];
                            }
                            indOr++;

                            if (isValidEquivalent(equiveOr1, equiveTr1) && (!forHumanUser)) {
                                hMapOrTr.put(firstCharToLowerCase(equiveOr1.trim()), firstCharToLowerCase(equiveTr1.trim()));
                            }
                            if (isValidEquivalent(equiveOr2, equiveTr2)) {
                                hMapOrTr.put(firstCharToLowerCase(equiveOr2.trim()), firstCharToLowerCase(equiveTr2.trim()));
                            }
                            if (isValidEquivalent(equiveOr3, equiveTr3)) {
                                hMapOrTr.put(firstCharToLowerCase(equiveOr3.trim()), firstCharToLowerCase(equiveTr3.trim()));
                            }

                            if (equiveOr1.split(" ").length < lengthForRecursionLimit && !forHumanUser) {
                                splitSentenceToEquivalents(equiveOr1.split(" "), equiveTr1.split(" "));
                                splitSentenceToEquivalents(equiveOr3.split(" "), equiveTr3.split(" "));
                            }
                            equiveOr1 = "";
                            equiveTr1 = "";
                            equiveOr2 = "";
                            equiveTr2 = "";
                            equiveOr3 = "";
                            equiveTr3 = "";
                        }
                        if (indOr >= storig.length - 1 || indTransl >= sttrans.length - 1) break;
                    }
                }
            }
        }
        return matchesCounter;
    }

    public static boolean isValidEquivalent(String orStr, String trStr) {
        if (orStr != null && trStr != null
                && orStr.trim().length() > 0 && trStr.trim().length() > 0
                && orStr.trim().length() < iMaxEquivLength
                && (double) orStr.trim().length() / (double) trStr.trim().length() < acceptableCoefficientDifference
                && (double) trStr.trim().length() / (double) orStr.trim().length() < acceptableCoefficientDifference
                ) return true;
        else
            return false;
    }

    public static void pRintSplitPhrases() {
        for (int i = 0; i < hMapOrTr.size(); i++) {
            if (hMapOrTr.keySet().toArray()[i] != null) {
            }
        }
    }

    public static int GetRegCorrespondances() {

        curField1 = EquiveFrame.combLang1.getSelectedItem().toString().toUpperCase(); //TELLING THE FIELD NAME WHICH IS THE SAME AS THE NAME OF A DROP-OUT LIST ITEM BUT IN SMALL LETTERS
        curField2 = EquiveFrame.combLang2.getSelectedItem().toString().toUpperCase();

        try (final Statement statement = conn.createStatement()) {
            totalRegEqCount = 0;
//            String strRegAmount = regCorrAmount + "";
//            int deletedRows = statement.executeUpdate("DELETE FROM " + curTable + " WHERE rowid >" + strRegAmount);
//            System.out.println("Deleted rows "+deletedRows);
            resSet2 = statement.executeQuery("SELECT * FROM " + curTable + "");
            while (resSet2.next()) {

                mapRegCorrespondances.put(reduceNotionalWord(resSet2.getString(curField1)), reduceNotionalWord(resSet2.getString(curField2)) );
                totalRegEqCount++;
            }
        } catch (SQLException e4) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "NO CLASS JDBC 1");
        }

        return totalRegEqCount;
    }

    public static String reduceNotionalWord(String word) {
        if (word.length() > 5)
            word = word.substring(0, word.length() - 2) + "[а-яa-zіїє0-9]+";

        if (word.length() == 4)
            word = word.substring(0, word.length() - 1) + "[а-яa-zіїє0-9]+";

        return word;
    }

}