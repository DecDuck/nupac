package editor;

import com.formdev.flatlaf.FlatDarkLaf;
import editor.resources.ResourceLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class EditorMain {

    private static Popup popup;

    public static void Editor(String filename) throws FileNotFoundException {
        FlatDarkLaf.install();
        File f = new File(filename);
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

        Editor editor = new Editor();
        JFrame frame = new JFrame("DLANG Editor");
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
                    Runtime.getRuntime().exec("cmd /c dlang " + filename).waitFor();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        editor.getEditorToolbar().add(saveButton);
        editor.getEditorToolbar().add(runButton);

        frame.pack();
        frame.setVisible(true);
    }

}
