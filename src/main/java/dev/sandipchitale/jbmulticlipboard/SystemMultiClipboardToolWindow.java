package dev.sandipchitale.jbmulticlipboard;

import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SystemMultiClipboardToolWindow {

    @NotNull
    private final Project project;
    private final JPanel contentToolWindow;

    public SystemMultiClipboardToolWindow(@NotNull Project project) {
        this.project = project;
        ListWrappingTableModel clipboardTextsModel = ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).getTableModel();
        JBTable clipboardTexts = new JBTable(clipboardTextsModel);
        clipboardTexts.getTableHeader().setEnabled(false);

        contentToolWindow = new SimpleToolWindowPanel(true, true);
        JBScrollPane scrollPane = new JBScrollPane(clipboardTexts,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentToolWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton clearButton = new JButton(AllIcons.General.Remove);
        clearButton.setToolTipText("Clear Clips");
        clearButton.addActionListener((ActionEvent actionEvent) -> {
            ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).clearClipboardTextTransferables();
        });
        toolbar.add(clearButton);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        contentToolWindow.add(toolbar, BorderLayout.SOUTH);
    }

    public @Nullable JComponent getContent() {
        return contentToolWindow;
    }
}