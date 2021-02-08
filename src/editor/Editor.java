package editor;

import javax.swing.*;

public class Editor {
    private JPanel editorPanel;
    private JToolBar editorToolbar;
    private JTextArea editorOutput;
    private JTextArea editorText;
    private JScrollPane editorScrollPlane;

    public JTextArea getEditorOutput() {
        return editorOutput;
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public JToolBar getEditorToolbar() {
        return editorToolbar;
    }

    public JTextArea getEditorText() {
        return editorText;
    }
}
