package dev.sandipchitale.jbmulticlipboard;

import com.intellij.ide.PasteProvider;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class SystemMultiClipboardPasteProvider implements PasteProvider {
    @Override
    public void performPaste(@NotNull DataContext dataContext) {
        Messages.showInfoMessage("Custom paste provider", "System Multi Clipboard");
    }

    @Override
    public boolean isPastePossible(@NotNull DataContext dataContext) {
        return false;
    }

    @Override
    public boolean isPasteEnabled(@NotNull DataContext dataContext) {
        return true;
    }
}
