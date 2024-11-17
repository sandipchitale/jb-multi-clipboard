package dev.sandipchitale.jbmulticlipboard;

import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

public class SystemMultiClipboardToolWindow {

    private static Clipboard systemClipboard;
    private static final String[] lastClipboardText = {null};

    @NotNull
    private final Project project;
    private final JPanel contentToolWindow;

    public SystemMultiClipboardToolWindow(@NotNull Project project) {
        this.project = project;
        if (systemClipboard == null) {
            systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }

        ListWrappingTableModel clipboardTextsModel = ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).getTableModel();
        JBTable clipboardTexts = new JBTable(clipboardTextsModel);

        contentToolWindow = new SimpleToolWindowPanel(true, true);
        JBScrollPane scrollPane = new JBScrollPane(clipboardTexts,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentToolWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton clearButton = new JButton(AllIcons.General.Remove);
        clearButton.setToolTipText("Clear Clips");
        clearButton.addActionListener((ActionEvent actionEvent) -> {
            int rowCount = clipboardTextsModel.getRowCount();
            java.util.List<Transferable> clipboardTextTransferables = new ArrayList<>(rowCount);
            for (int i = 0; i < rowCount; i++) {
                String clipboardTextAtI = (String) clipboardTextsModel.getValueAt(i, 0);
                clipboardTextTransferables.add(new StringSelection(clipboardTextAtI));
            }
            SystemMultiClipboardContentChooser.setClipboardTextTransferables(clipboardTextTransferables);
            SystemMultiClipboardContentChooser systemMultiClipboardContentChooser = new SystemMultiClipboardContentChooser(project,
                    "Choose System Content to Paste",
                    true);
            if (systemMultiClipboardContentChooser.showAndGet()) {
                java.util.List<Transferable> selectedContents = systemMultiClipboardContentChooser.getSelectedContents();
                selectedContents.forEach((Transferable transferable) -> {
                    if (transferable instanceof StringSelection stringSelection) {
                        try {
                            Messages.showInfoMessage((String) stringSelection.getTransferData(DataFlavor.stringFlavor), "Will Paste");
                        } catch (UnsupportedFlavorException | IOException ignore) {
                        }
                    }
                });
            }
        });
        toolbar.add(clearButton);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        contentToolWindow.add(toolbar, BorderLayout.NORTH);
    }

    public @Nullable JComponent getContent() {
        return contentToolWindow;
    }
}