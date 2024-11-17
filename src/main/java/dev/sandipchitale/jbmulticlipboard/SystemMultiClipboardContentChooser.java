package dev.sandipchitale.jbmulticlipboard;

import com.intellij.openapi.editor.actions.ContentChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public class SystemMultiClipboardContentChooser extends ContentChooser<Transferable> {

    static List<Transferable> clipboardTextTransferables;

    static void setClipboardTextTransferables(List<Transferable> clipboardTextTransferables) {
        SystemMultiClipboardContentChooser.clipboardTextTransferables = clipboardTextTransferables;
    }

    public SystemMultiClipboardContentChooser(Project project, @NlsContexts.DialogTitle String title, boolean useIdeaEditor) {
        super(project, title, useIdeaEditor);
    }

    @Override
    protected void removeContentAt(Transferable content) {
    }

    @Override
    protected @Nullable @NlsSafe String getStringRepresentationFor(Transferable content) {
        try {
            return (String)content.getTransferData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException | IOException e1) {
            return "";
        }
    }

    @Override
    protected java.util.@NotNull List<Transferable> getContents() {
        return clipboardTextTransferables;
    }
}