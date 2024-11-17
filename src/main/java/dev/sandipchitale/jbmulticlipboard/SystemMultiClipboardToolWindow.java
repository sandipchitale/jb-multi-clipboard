package dev.sandipchitale.jbmulticlipboard;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SystemMultiClipboardToolWindow {

    private static Clipboard systemClipboard;
    private static final String[] lastClipboardText = {null};

    @NotNull
    private final Project project;
    private final JPanel contentToolWindow;
    private final Timer timer;

    public SystemMultiClipboardToolWindow(@NotNull Project project) {
        this.project = project;
        if (systemClipboard == null) {
            systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }

        DefaultTableModel clipboardTextsModel = new DefaultTableModel(new String[]{"Clips"}, 0);
        JBTable clipboardTexts = new JBTable(clipboardTextsModel);
        timer = new Timer(1000, (ActionEvent e) -> {
            try {
                String clipboardText = (String) SystemMultiClipboardToolWindow.systemClipboard.getData(DataFlavor.stringFlavor);
                if (clipboardText != null && !clipboardText.equals(lastClipboardText[0])) {
                    clipboardTextsModel.addRow(new String[]{clipboardText});
                    lastClipboardText[0] = clipboardText;
                }
            } catch (UnsupportedFlavorException | IOException ignore) {
            }
        });
        timer.start();

        contentToolWindow = new SimpleToolWindowPanel(true, true) {
            @Override
            public void removeNotify() {
                timer.stop();
                super.removeNotify();
            }
        };
        JBScrollPane scrollPane = new JBScrollPane(clipboardTexts,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentToolWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton clearButton = new JButton(AllIcons.General.Remove);
        clearButton.setToolTipText("Clear Clips");
        clearButton.addActionListener((ActionEvent actionEvent) -> {
            clipboardTextsModel.setRowCount(0);
        });
        toolbar.add(clearButton);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        contentToolWindow.add(toolbar, BorderLayout.NORTH);
    }

    public @Nullable JComponent getContent() {
        return contentToolWindow;
    }
}
