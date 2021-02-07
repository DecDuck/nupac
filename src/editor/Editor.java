package editor;

import javax.swing.*;

public class Editor {
    private JPanel editorPanel;
    private JToolBar editorToolbar;
    private JTextPane editorText;
    private JTextArea editorOutput;

    public JTextArea getEditorOutput() {
        return editorOutput;
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public JToolBar getEditorToolbar() {
        return editorToolbar;
    }

    public JTextPane getEditorText() {
        return editorText;
    }
}
