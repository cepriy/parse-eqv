package parseeqv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by Sergiy on 21.12.2017.
 */
class EquiveFrame extends JFrame {
    public static JButton btSaveExit = new JButton("Save and Exit");
    public static JTextArea orig = new JTextArea(2, 30);
    public static JTextArea trans = new JTextArea(2, 30);

    public static JPanel pnlorig = new JPanel(new GridLayout(8, 8));
    public static JButton btAccept = new JButton("Accept");
    public static JButton btIgnore = new JButton("Ignore");

    public static JComboBox combLang1 = new JComboBox(ParseConstants.arrCombLang);

    public static JComboBox combLang2 = new JComboBox(ParseConstants.arrCombLang);
    public static JComboBox combCoefficient = new JComboBox(ParseConstants.arrCoefficients);

    public static JTextField chsnorig = new JTextField(10);
    public static JTextField chsntrans = new JTextField(10);

    public static PrintWriter res;
    public static JButton btSaveAutomatic = new JButton(ParseConstants.RetreiveEquivesButtonStr);
    static JButton btStart = new JButton(ParseConstants.StartButtonStr);
    public static JButton btNewSession = new JButton(ParseConstants.newSessionButtonStr);
    static JButton btChooseorig = new JButton(ParseConstants.originalFileButtonStr);
    static JButton btChoosetrans = new JButton(ParseConstants.translationFileButtonStr);
    public static JCheckBox prevAnalysisChecqbx = new JCheckBox(ParseConstants.PreviousAnalysisStr);
    public static JCheckBox forHumanUser = new JCheckBox("");
    public static boolean btStartListenerAdded = false;

    public static File fileResults;

    static JComboBox combEquiveLength = new JComboBox(ParseConstants.equiveLengthArray);
    static JComboBox combFileType = new JComboBox(ParseConstants.fileTypes);

    public void setFrameOptions() {
        combEquiveLength.setSelectedIndex(5);
        EquiveFrame.combLang1.setSelectedIndex(0);
        EquiveFrame.combLang2.setSelectedIndex(1);

        chsnorig.setText(null);
        chsntrans.setText(null);

        orig.setText(null);
        trans.setText(null);
        combFileType.setSelectedIndex(0);
        combEquiveLength.setSelectedIndex(4);
        forHumanUser.setSelected(false);
        prevAnalysisChecqbx.setSelected(true);
        EquiveFrame.combCoefficient.setSelectedIndex(1);
        btNewSession.setText(ParseConstants.newSessionButtonStr);

    }

    public void run() {
        final Thread t1 = new Thread(MainBlock::onStart);

        this.setTitle("ParseEqv 1.0 || Fokin.S.B. ©");
        this.setBackground(Color.RED);
        this.setBackground(new Color(0, 0, 0));
        this.setOpacity(1);
        trans.setBackground(Color.orange);
        orig.setBackground(Color.YELLOW);

        SearchEquive.forHumanUser = false;
        forHumanUser.setText(ParseConstants.forHumanUser);
        if (!btStartListenerAdded) btStart.addActionListener(e -> {
            System.out.println(SearchEquive.curField1);
            //    JOptionPane.showMessageDialog(parseeqv.SearchEquive.pnlorig, parseeqv.SearchEquive.curField1);

            //     JOptionPane.showMessageDialog(parseeqv.SearchEquive.pnlorig, parseeqv.SearchEquive.curField2);

            btStartListenerAdded = true;

            if ("1.35".equals(EquiveFrame.combCoefficient.getSelectedItem())) {
                SearchEquive.acceptableCoefficientDifference = 1.35;
            }
            if ("1.40".equals(EquiveFrame.combCoefficient.getSelectedItem())) {
                SearchEquive.acceptableCoefficientDifference = 1.40;
            }
            if ("1.45".equals(EquiveFrame.combCoefficient.getSelectedItem())) {
                SearchEquive.acceptableCoefficientDifference = 1.45;
            }

            System.out.println("Coeff: " + SearchEquive.acceptableCoefficientDifference);
            fileResults = new File("Results_" + SearchEquive.acceptableCoefficientDifference + " " + EquiveFrame.combLang1.getSelectedItem() + "-" + EquiveFrame.combLang2.getSelectedItem() + "_ParseEqv_Fokin_S_B_All_rights_reserved" + SearchEquive.savedFileCounter + ".txt");

            try {
                res = new PrintWriter(fileResults.getAbsolutePath());
            } catch (FileNotFoundException e12) {
                JOptionPane.showMessageDialog(pnlorig, "Problems when writing to file");
            }

            if (t1.isAlive()) {
                t1.interrupt();
            } else {
                Thread t11 = new Thread(MainBlock::onStart);
                t11.setName("stop_listener");
                t11.start();
            }
            if (EquiveFrame.combLang1.getSelectedItem() == EquiveFrame.combLang2.getSelectedItem())
                JOptionPane.showMessageDialog(pnlorig, "original and translational languages should differ");
            btNewSession.setText("STOP");
            btNewSession.setBackground(Color.RED);

            if (combEquiveLength.getSelectedIndex() == 0) SearchEquive.iMaxEquivLength = 40;
            if (combEquiveLength.getSelectedIndex() == 1) SearchEquive.iMaxEquivLength = 50;
            if (combEquiveLength.getSelectedIndex() == 2) SearchEquive.iMaxEquivLength = 60;
            if (combEquiveLength.getSelectedIndex() == 3) SearchEquive.iMaxEquivLength = 70;
            if (combEquiveLength.getSelectedIndex() == 4) SearchEquive.iMaxEquivLength = 80;
            if (combEquiveLength.getSelectedIndex() == 5) SearchEquive.iMaxEquivLength = 90;
            if (combEquiveLength.getSelectedIndex() == 6) SearchEquive.iMaxEquivLength = 100;
            if (combEquiveLength.getSelectedIndex() == 7) SearchEquive.iMaxEquivLength = 110;
            if (combEquiveLength.getSelectedIndex() == 8) SearchEquive.iMaxEquivLength = 240;
            if (combFileType.getSelectedIndex() == 1) {
                SearchEquive.txtTypeSelected = false;
                EquiveFrame.btChoosetrans.setEnabled(false);
            }
            if (combFileType.getSelectedIndex() == 0) SearchEquive.txtTypeSelected = true;
        });

        if (!SearchEquive.newSessionListenerAdded) {
            btNewSession.addActionListener(event -> {
                if (btNewSession.getText().equals(ParseConstants.clearSessionStr)) {
                    setFrameOptions();
                }
                SearchEquive.analyzedEquivalenceCount = 0;
                SearchEquive.newSessionListenerAdded = true;
                btSaveAutomatic.setEnabled(false);
                btSaveAutomatic.setBackground(Color.LIGHT_GRAY);
                btStart.setEnabled(false);
                if (t1.isAlive()) {
                    t1.interrupt();
                }

                orig.setText("");
                trans.setText("");

                SearchEquive.hMapOrTr.clear();
                SearchEquive.potentialEquivToBeAnalyzed.clear();
                SearchEquive.bAcceptListner = false;
                SearchEquive.bIgnoreListner = false;
                SearchEquive.bSaveExit = false;
                if (!btNewSession.getText().equals("STOP")) {
                    btStart.setEnabled(true);
                }
                btNewSession.setText(ParseConstants.newSessionButtonStr);
                btNewSession.setBackground(Color.LIGHT_GRAY);
                if (btNewSession.getText().equals(ParseConstants.newSessionButtonStr)) {
                    System.out.println(ParseConstants.newSessionButtonStr);
                    System.out.println(btNewSession.getText());
                    btNewSession.setText(ParseConstants.clearSessionStr);
                }
            });
        }

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                String ObjButtons[] = {ParseConstants.yesStr, ParseConstants.noStr};
                int PromptResult = JOptionPane.showOptionDialog(null, ParseConstants.wannaSaveFile, "", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
//
                    try {
                        SearchEquive.saveResults(res, fileResults);
                    } catch (IOException ee) {
                        JOptionPane.showMessageDialog(pnlorig, ParseConstants.problemsWithFileStr);
                    }
                    res.close();
                    System.exit(0);
                } else {
                    System.exit(0);
                }
            }
        });
        this.setLocationRelativeTo(null);
        this.setBounds(400, 200, 700, 490);
        this.setResizable(true);
        this.add(pnlorig);
        JPanel pnlStartComb = new JPanel();
        pnlStartComb.setBackground(Color.DARK_GRAY);

       setFrameOptions();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        pnlorig.add(pnlStartComb, c);
        pnlStartComb.add(btStart);
        pnlStartComb.add(combLang1);
        pnlStartComb.add(combLang2);
        JTextField textCoefFiltre = new JTextField(ParseConstants.coefFiltr);
        textCoefFiltre.setBorder(null);
        pnlStartComb.add(textCoefFiltre);
        pnlStartComb.add(combCoefficient);

        pnlStartComb.add(btNewSession);
        JTextField textEquiveLength = new JTextField(ParseConstants.SelectLengthEquivStr);
        textEquiveLength.setBorder(null);
        pnlStartComb.add(textEquiveLength);

        pnlStartComb.add(combEquiveLength);
        pnlStartComb.add(prevAnalysisChecqbx);
        pnlStartComb.add(forHumanUser);
        pnlStartComb.add(combFileType);

        btIgnore.setEnabled(false);
        btAccept.setEnabled(false);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 1;
        c3.gridy = 1;
        pnlorig.add(orig, c3);

        GridBagConstraints c4 = new GridBagConstraints();
        c4.gridx = 1;
        c4.gridy = 2;

        pnlorig.add(trans, c4);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 2;
        c1.gridy = 2;

        pnlorig.add(btAccept, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 4;
        c2.gridy = 2;
        pnlorig.add(btIgnore, c2);

        GridBagConstraints c6 = new GridBagConstraints();
        c6.gridx = 6;
        c6.gridy = 2;
        pnlorig.add(btSaveAutomatic, c6);
        GridBagConstraints c5 = new GridBagConstraints();
        c5.gridx = 5;
        c5.gridy = 2;
        pnlorig.add(btSaveExit, c5);

        JPanel pnl78 = new JPanel();
        GridBagConstraints c7 = new GridBagConstraints();
        c7.gridx = 7;
        c7.gridy = 1;
        pnlorig.add(pnl78, c7);

//        GridBagConstraints c7=new GridBagConstraints();
//        c7.gridx=7;
//        c7.gridy=1;

        chsnorig.setBorder(null);
        pnl78.add(chsnorig);
        pnl78.add(btChooseorig);
//
//        GridBagConstraints c8=new GridBagConstraints();
//        c8.gridx=7;
//        c8.gridy=2;

        pnl78.add(btChoosetrans);
        pnl78.add(chsntrans);
        chsntrans.setBorder(null);

        btSaveAutomatic.setEnabled(false);

        if (!SearchEquive.bSaveExit) {
            btSaveExit.addActionListener(e -> {
                res.close();
                System.exit(0);
            });
        }

        this.setVisible(true);

        if (!SearchEquive.bActiobLisChooser) {
            btChooseorig.addActionListener(e -> {
                SearchEquive.bActiobLisChooser = true;

                if (EquiveFrame.combFileType.getSelectedIndex() == 0) btChoosetrans.setEnabled(true);

                File workingDirectory = new File(System.getProperty("user.dir"));
                SearchEquive.chooser.setCurrentDirectory(workingDirectory);
                SearchEquive.chooser.setDialogTitle("Choose the original");
                SearchEquive.chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                SearchEquive.chooser.setAcceptAllFileFilterUsed(false);

                if (SearchEquive.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): " + SearchEquive.chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + SearchEquive.chooser.getSelectedFile().getAbsolutePath());

                    chsnorig.setText(SearchEquive.chooser.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("No Selection ");
                }
                SearchEquive.pathorig = SearchEquive.chooser.getSelectedFile().getAbsolutePath();
            });
        }

        if (!SearchEquive.bActiobLisChooser) {
            btChoosetrans.addActionListener(e -> {
                SearchEquive.bActiobLisChooser = true;

                File workingDirectory = new File(System.getProperty("user.dir"));
                SearchEquive.chooser.setCurrentDirectory(workingDirectory);
                SearchEquive.chooser.setDialogTitle("Sélectionnez la traduction");
                SearchEquive.chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                SearchEquive.chooser.setAcceptAllFileFilterUsed(false);

                if (SearchEquive.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): " + SearchEquive.chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + SearchEquive.chooser.getSelectedFile());
                    chsntrans.setText(SearchEquive.chooser.getSelectedFile().getName());
                } else {
                    System.out.println("Aucune sélection n'a été réalisée");
                }
                SearchEquive.pathtrans = SearchEquive.chooser.getSelectedFile().getAbsolutePath();
            });
        }
    }
}

class MainBlock {
    public static void onStart() {
        SearchEquive searchEquive = new SearchEquive();

        if (EquiveFrame.forHumanUser.isSelected()) SearchEquive.forHumanUser = true;

        if (SearchEquive.pathorig == null) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "Sélectionez l'original");
            EquiveFrame.btStart.setEnabled(true);
        } else if (SearchEquive.pathtrans == null && SearchEquive.txtTypeSelected) {
            JOptionPane.showMessageDialog(EquiveFrame.pnlorig, "Sélectionez la traduction");
            EquiveFrame.btStart.setEnabled(true);
        } else {
            try {
                searchEquive.getConnection();
                searchEquive.DB();
              if (EquiveFrame.prevAnalysisChecqbx.isSelected())  SearchEquive.forHumanUser=true;// DISCONNECT RECURSION WHEN DOING PRELIMINAR ANALYSIS
                SearchEquive.GetRegCorrespondances();
                if (SearchEquive.txtTypeSelected) SearchEquive.readOriginalTranslFilesTxt();
                else {
                    SearchEquive.readOriginalTranslFilesCsv();
                }

                if (EquiveFrame.prevAnalysisChecqbx.isSelected()) {
                    System.out.println("IS SELECTED");
                    SearchEquive.analyzeSplitPhrases();
                    System.out.println("SIZE "+SearchEquive.potentialEquivToBeAnalyzed.size());
                //    parseeqv.SearchEquive.viewAndSaveEquivalences();
                    SearchEquive.initFieldWithCurrentEquivSet(SearchEquive.showedEquivNum);
                    SearchEquive.analyzedEquivalenceCount++;
                    SearchEquive.showedEquivNum++;
                    SearchEquive.activateButtons();
                } else {
                    System.out.println("PROPOSE SAVE 1");
                    SearchEquive.proposeSaveAutomatically();
                }
                SearchEquive.pRintSplitPhrases();


            } catch (IOException | ClassNotFoundException | SQLException e5) {
                // JOptionPane.showMessageDialog(parseeqv.SearchEquive.pnlorig, "NO CLASS JDBC OR NO FILE OR NO SQL");
                JOptionPane.showMessageDialog(EquiveFrame.pnlorig, e5.getMessage());
            }
        }
    }
}