package dev.sandipchitale.jbmulticlipboard;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class SystemMultiClipboardToolWindow extends SimpleToolWindowPanel {

    public SystemMultiClipboardToolWindow(@NotNull Project project) {
        super(true, true);
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

        setContent(ScrollPaneFactory.createScrollPane(clipboardTextsTable));

        ActionManager actionManager = ActionManager.getInstance();
        ToolWindowEx systemMultiClipboardToolWindow = (ToolWindowEx) ToolWindowManager.getInstance(project).getToolWindow("System Clipboard History");
        SystemMultiClipboardRemoveAllAction systemMultiClipboardRemoveAllAction =
                (SystemMultiClipboardRemoveAllAction) actionManager.getAction("SystemMultiClipboardRemoveAllAction");
        Objects.requireNonNull(systemMultiClipboardToolWindow).setTitleActions(java.util.List.of(systemMultiClipboardRemoveAllAction));
    }
}