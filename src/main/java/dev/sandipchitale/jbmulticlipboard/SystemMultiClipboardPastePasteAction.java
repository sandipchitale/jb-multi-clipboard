package dev.sandipchitale.jbmulticlipboard;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.Transferable;
import java.util.List;

public class SystemMultiClipboardPastePasteAction extends AnAction implements DumbAware {

    private long lastPasteTimeMillis = Long.MIN_VALUE;
    private int index = 0;

    public SystemMultiClipboardPastePasteAction() {
        setEnabledInModalContext(true);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent actionEvent) {
        final DataContext dataContext = actionEvent.getDataContext();
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        if (editor == null || editor.isViewer()) return;

        long currentTimeMillis = System.currentTimeMillis();
        final ActionManager actionManager = ActionManager.getInstance();

        final AnAction pasteAction = actionManager.getAction(IdeActions.ACTION_EDITOR_PASTE_SIMPLE);
        List<Transferable> clipboardTextTransferables = ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).getClipboardTextTransferables();
        if (currentTimeMillis - lastPasteTimeMillis < 1000L) {
            index++;
            final AnAction undoAction = actionManager.getAction(IdeActions.ACTION_UNDO);
            actionManager.tryToExecute(
                    undoAction,
                    actionEvent.getInputEvent(),
                    actionEvent.getDataContext().getData(PlatformDataKeys.CONTEXT_COMPONENT),
                    actionEvent.getPlace(),
                    true
            );
        } else {
            index = 0;
        }
        Transferable transferable = clipboardTextTransferables.get(index % clipboardTextTransferables.size());
        CopyPasteManagerEx.getInstanceEx().moveContentToStackTop(transferable);
        actionManager.tryToExecute(
                pasteAction,
                actionEvent.getInputEvent(),
                actionEvent.getDataContext().getData(PlatformDataKeys.CONTEXT_COMPONENT),
                actionEvent.getPlace(),
                true
        );
        lastPasteTimeMillis = currentTimeMillis;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isEnabled(e));
    }

    private static boolean isEnabled(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        return (editor != null && !editor.isViewer());
    }
}
