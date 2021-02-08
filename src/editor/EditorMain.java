package editor;

import editor.autocomplete.AutoSuggestor;
import editor.resources.ResourceLoader;
import master.Master;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import static master.Master.RunLine;

public class EditorMain {

    private static Popup popup;
    public static JFrame frame = null;
    public static Editor editor;
    private static JTextArea textArea;
    public static AutoSuggestor autoSuggestor;

    public static void Editor(String filename){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                File f = new File(filename);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileInputStream in = null;
                try {
                    in = new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] data = new byte[(int) f.length()];
                try {
                    in.read(data);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileOutput = new String(data);
                fileOutput.replace("\r", "");

                editor = new Editor();
                frame = new JFrame("DLANG Editor");
                frame.setContentPane(editor.getEditorPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                textArea = editor.getEditorText();

                ArrayList<String> words = new ArrayList<String>();
                {
                    words.add("var");
                    words.add("set");
                    words.add("jmp");
                    words.add("jump");
                    words.add("print");
                    words.add("args");
                    words.add("maths");
                    words.add("math");
                    words.add("wait");
                    words.add("loop");
                    words.add("string");
                    words.add("append");
                    words.add("replace");
                    words.add("check");
                    words.add("file");
                    words.add("write");
                    words.add("read");
                    words.add("delete");
                    words.add("system");
                    words.add("notify");
                    words.add("popup");
                    words.add("date");
                    words.add("network");
                    words.add("html");
                    words.add("tcp");
                    words.add("host");
                    words.add("client");
                    words.add("send");
                    words.add("receive");
                    words.add("connect");
                    words.add("disconnect");
                }

                autoSuggestor = new AutoSuggestor(textArea, frame, words, Color.WHITE.brighter(), Color.BLACK.darker(), Color.BLACK.brighter(), 0.75f);

                Icon saveIcon = new ImageIcon(new ImageIcon(ResourceLoader.class.getResource("save.png")).getImage()
                        .getScaledInstance(16, 16,
                                Image.SCALE_SMOOTH));
                Icon runIcon = new ImageIcon(new ImageIcon(ResourceLoader.class.getResource("run.png")).getImage()
                        .getScaledInstance(16, 16,
                                Image.SCALE_SMOOTH));
                JButton saveButton = new JButton(saveIcon);
                saveButton.setToolTipText("Save");
                saveButton.setPreferredSize(new Dimension(16, 16));
                JButton runButton = new JButton(runIcon);
                runButton.setToolTipText("Run");
                runButton.setPreferredSize(new Dimension(16, 16));

                textArea.setText(fileOutput);
                textArea.addKeyListener(new KeyAdapter(){
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_TAB){
                            e.consume();
                        }else{
                            super.keyTyped(e);
                        }
                    }
                });

                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String fileName = filename;
                        String toWrite = textArea.getText();

                        File f = new File(fileName);
                        try {
                            f.createNewFile();
                            FileWriter rw = new FileWriter(f);
                            rw.write(toWrite);
                            rw.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });

                runButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String toWrite = textArea.getText();

                        File f = new File(filename);
                        try {
                            f.createNewFile();
                            FileWriter rw = new FileWriter(f);
                            rw.write(toWrite);
                            rw.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        new RunScript(textArea.getText()).start();

                    }
                });

                editor.getEditorToolbar().add(saveButton);
                editor.getEditorToolbar().add(runButton);

                frame.pack();
                frame.setVisible(true);
            }
        });
    }

}
