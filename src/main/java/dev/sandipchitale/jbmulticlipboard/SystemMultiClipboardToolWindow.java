package dev.sandipchitale.jbmulticlipboard;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SystemMultiClipboardToolWindow {

    @NotNull
    private final JPanel contentToolWindow;

    public SystemMultiClipboardToolWindow(@NotNull Project project) {
        DefaultTableModel clipboardTextsTableModel = ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).getTableModel();
        JBTable clipboardTextsTable = new JBTable(clipboardTextsTableModel);

        Action deleteClipboardTextAtIndex = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JTable table = (JTable) actionEvent.getSource();
                clipboardTextsTableModel.removeRow(Integer.parseInt(actionEvent.getActionCommand()));
            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(clipboardTextsTable, deleteClipboardTextAtIndex, 1);

        TableColumn column = clipboardTextsTable.getColumnModel().getColumn(1);
        column.setMinWidth(100);
        column.setWidth(100);
        column.setMaxWidth(100);

        clipboardTextsTable.setTableHeader(null);

        contentToolWindow = new SimpleToolWindowPanel(true, true);
        JBScrollPane scrollPane = new JBScrollPane(clipboardTextsTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentToolWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton clearButton = new JButton(AllIcons.Actions.DeleteTag);
        Border border = clearButton.getBorder();
        clearButton.setToolTipText("Remove All");
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