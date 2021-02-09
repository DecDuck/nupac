package editor;

import master.Master;

import javax.swing.*;
import java.io.IOException;

import static master.Master.RunLine;

public class RunScript implements Runnable {

    public Thread t;

    public String textArea;
    public String result = "";

    public RunScript(String textArea){
        t = new Thread(this, "DLANG RUN");
        this.textArea = textArea;
    }

    @Override
    public void run() {
        String[] file = textArea.split("\n");
        Master.file = file;

        for(int i = 0; i < file.length; i++){
            try {
                Object[] ob = RunLine(file[i].replace("\r", ""), i, false);
                i += (int) ob[0];
                if(((String)ob[1]).length() > 0){
                    result = result + ob[1] + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
