package editor;

import editor.resources.ResourceLoader;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class EditorMain {

    private static Popup popup;
    public static JFrame frame = null;

    public static int getRow(int pos, JTextComponent editor) {
        int rn = (pos==0) ? 1 : 0;
        try {
            int offs=pos;
            while( offs>0) {
                offs= Utilities.getRowStart(editor, offs)-1;
                rn++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rn;
    }

    public static int getColumn(int pos, JTextComponent editor) {
        try {
            return pos-Utilities.getRowStart(editor, pos)+1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void Editor(String filename) throws FileNotFoundException {
        File f = new File(filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream in = null;
        in = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        try {
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileOutput = new String(data);
        fileOutput.replace("\r", "");

        Editor editor = new Editor();
        frame = new JFrame("DLANG Editor");
        frame.setContentPane(editor.getEditorPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        editor.getEditorPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel text = new JLabel("You've clicked at: " + e.getPoint());
                popup = PopupFactory.getSharedInstance().getPopup(e.getComponent(), text, e.getXOnScreen(), e.getYOnScreen());
                popup.show();
            }
        });

        Icon saveIcon = new ImageIcon(new ImageIcon(ResourceLoader.class.getResource("save.png")).getImage()
                .getScaledInstance(16, 16,
                        Image.SCALE_FAST));
        Icon runIcon = new ImageIcon(new ImageIcon(ResourceLoader.class.getResource("run.png")).getImage()
                .getScaledInstance(16, 16,
                        Image.SCALE_FAST));
        JButton saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save");
        saveButton.setPreferredSize(new Dimension(16, 16));
        JButton runButton = new JButton(runIcon);
        runButton.setToolTipText("Run");
        runButton.setPreferredSize(new Dimension(16, 16));

        editor.getEditorText().setText(fileOutput);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = filename;
                String toWrite = editor.getEditorText().getText();

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
                try {
                    String toWrite = editor.getEditorText().getText();

                    File f = new File(filename);
                    try {
                        f.createNewFile();
                        FileWriter rw = new FileWriter(f);
                        rw.write(toWrite);
                        rw.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    Runtime rt = Runtime.getRuntime();
                    String[] commands = {"cmd", "/c", "dlang", filename + ""};
                    Process proc = rt.exec(commands);

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(proc.getErrorStream()));

                    editor.getEditorOutput().setText("");

                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        editor.getEditorOutput().append(s + "\n");
                        frame.pack();
                    }

                } catch (IOException r) {
                    r.printStackTrace();
                }
            }
        });

        editor.getEditorToolbar().add(saveButton);
        editor.getEditorToolbar().add(runButton);

        frame.pack();
        frame.setVisible(true);
    }

}
