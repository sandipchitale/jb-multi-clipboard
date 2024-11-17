package dev.sandipchitale.jbmulticlipboard;

import com.intellij.codeInspection.ui.ListWrappingTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

final public class SystemMultiClipboardServiceImpl implements SystemMultiClipboardService {

    private static Clipboard systemClipboard;
    private static String lastClipboardText = null;
    private static List<String> clipboardTexts;
    private static ListWrappingTableModel listWrappingTableModel;

    public SystemMultiClipboardServiceImpl() {
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboardTexts = new LinkedList<>();
        listWrappingTableModel = new ListWrappingTableModel(clipboardTexts, "");

        Timer timer = new Timer(1000, (ActionEvent e) -> {
            try {
                String clipboardText = (String) systemClipboard.getData(DataFlavor.stringFlavor);
                if (clipboardText != null && !clipboardText.equals(lastClipboardText)) {
                    if (clipboardTexts.contains(clipboardText)) {
                        clipboardTexts.remove(clipboardText);
                    }
                    clipboardTexts.add(0, clipboardText);
                    listWrappingTableModel.fireTableDataChanged();
                    lastClipboardText = clipboardText;
                }
            } catch (UnsupportedFlavorException | IOException ignore) {
            }
        });
        timer.start();
    }

    @Override
    public List<Transferable> getClipboardTextTransferables() {
        return clipboardTexts.stream().map(StringSelection::new).collect(Collectors.toList());
    }

    public ListWrappingTableModel getTableModel() {
        return listWrappingTableModel;
    }

    @Override
    public void clearClipboardTextTransferables() {
        clipboardTexts.clear();
        listWrappingTableModel.fireTableDataChanged();
    }

    @Override
    public void removeContent(Transferable content) {
        // TODO
    }
}
