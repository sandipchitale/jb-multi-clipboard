package dev.sandipchitale.jbmulticlipboard;

import com.intellij.codeInspection.ui.ListWrappingTableModel;

import java.awt.datatransfer.Transferable;
import java.util.List;

public interface SystemMultiClipboardService {
    List<Transferable> getClipboardTextTransferables();
    ListWrappingTableModel getTableModel();
}
