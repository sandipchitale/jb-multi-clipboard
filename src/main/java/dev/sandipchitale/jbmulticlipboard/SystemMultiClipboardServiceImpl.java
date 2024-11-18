package dev.sandipchitale.jbmulticlipboard;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

final public class SystemMultiClipboardServiceImpl implements SystemMultiClipboardService {

    private static Clipboard systemClipboard;
    private static String lastClipboardText = null;
    private static DefaultTableModel clipboardTextsTableModel;

    public SystemMultiClipboardServiceImpl() {
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboardTextsTableModel = new DefaultTableModel(
                new Object[]{
                        "Clips",
                        " "
                }, 0) {

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 1) {
                    return Icon.class;
                }
                return String.class;  //other columns accept String values
            }
        };


        Timer timer = new Timer(1000, (ActionEvent e) -> {
            try {
                String clipboardText = (String) systemClipboard.getData(DataFlavor.stringFlavor);
                if (clipboardText != null && !clipboardText.equals(lastClipboardText)) {
                    clipboardTextsTableModel.insertRow(0, new Object[]{
                            clipboardText,
                            AllIcons.Actions.DeleteTag
                    });
                    lastClipboardText = clipboardText;
                }
            } catch (UnsupportedFlavorException | IOException ignore) {
            }
        });
        timer.start();
    }

    @Override
    public List<Transferable> getClipboardTextTransferables() {
        List<Transferable> clipboardTextTransferables = new LinkedList<>();
        for (int i = 0; i < clipboardTextsTableModel.getRowCount(); i++) {
            clipboardTextTransferables.add(new StringSelection((String) clipboardTextsTableModel.getValueAt(i, 0)));
        }
        return clipboardTextTransferables;
    }

    public DefaultTableModel getTableModel() {
        return clipboardTextsTableModel;
    }

    @Override
    public void clearClipboardTextTransferables() {
        clipboardTextsTableModel.setRowCount(0);
    }

    @Override
    public void removeContent(Transferable content) {
        // TODO
    }
}
