package dev.sandipchitale.jbmulticlipboard;

import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.Transferable;
import java.util.List;

public interface SystemMultiClipboardService {
    List<Transferable> getClipboardTextTransferables();

    DefaultTableModel getTableModel();

    void clearClipboardTextTransferables();

    void removeContent(Transferable content);
}
