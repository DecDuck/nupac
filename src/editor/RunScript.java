package editor;

import master.Master;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static master.Master.RunLine;

public class RunScript implements Runnable {

    public Thread t;

    public String textArea;
    public String result = "";

    public RunScript(String textArea){
        t = new Thread(this, "nupac");
        this.textArea = textArea;
    }

    @Override
    public void run() {
        String[] file = textArea.split("\n");
        Master.file = file;

        Master.Run(file);

        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                EditorMain.editor.getEditorOutput().setText(result);
            }
        });
    }

    public void start(){
        t.start();
    }
}
