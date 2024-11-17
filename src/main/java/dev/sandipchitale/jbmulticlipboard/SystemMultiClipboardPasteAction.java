package dev.sandipchitale.jbmulticlipboard;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.ide.DataManager;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.ContentChooser;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.UIBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class SystemMultiClipboardPasteAction extends AnAction implements DumbAware {

    public SystemMultiClipboardPasteAction() {
        setEnabledInModalContext(true);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        Component focusedComponent = e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        if (!(focusedComponent instanceof JComponent)) return;

        final ContentChooser<Transferable> chooser = new SystemMultiClipboardPasteAction.SystemMultiClipboardContentChooser(project);

        if (!chooser.getAllContents().isEmpty()) {
            chooser.show();
        } else {
            chooser.close(DialogWrapper.CANCEL_EXIT_CODE);
        }

        if (chooser.getExitCode() == DialogWrapper.OK_EXIT_CODE || chooser.getExitCode() == getPasteSimpleExitCode()) {
            java.util.List<Transferable> selectedContents = chooser.getSelectedContents();
            CopyPasteManagerEx copyPasteManager = CopyPasteManagerEx.getInstanceEx();
            if (selectedContents.size() == 1) {
                copyPasteManager.moveContentToStackTop(selectedContents.get(0));
            } else {
                copyPasteManager.setContents(new StringSelection(chooser.getSelectedText()));
            }
            if (editor != null) {
                if (editor.isViewer()) return;

                final AnAction pasteAction = ActionManager.getInstance().getAction(IdeActions.ACTION_EDITOR_PASTE_SIMPLE);
                AnActionEvent newEvent = new AnActionEvent(e.getInputEvent(),
                        DataManager.getInstance().getDataContext(focusedComponent),
                        e.getPlace(), e.getPresentation(),
                        ActionManager.getInstance(),
                        e.getModifiers());
                pasteAction.actionPerformed(newEvent);
            } else {
                final Action pasteAction = ((JComponent) focusedComponent).getActionMap().get(DefaultEditorKit.pasteAction);
                if (pasteAction != null) {
                    pasteAction.actionPerformed(new ActionEvent(focusedComponent, ActionEvent.ACTION_PERFORMED, ""));
                }
            }
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        final boolean enabled = isEnabled(e);
        if (ActionPlaces.isPopupPlace(e.getPlace())) {
            e.getPresentation().setVisible(enabled);
        } else {
            e.getPresentation().setEnabled(enabled);
        }
    }

    private static boolean isEnabled(@NotNull AnActionEvent e) {
        Object component = e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
        if (!(component instanceof JComponent)) return false;
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) return !editor.isViewer();
        Action pasteAction = ((JComponent) component).getActionMap().get(DefaultEditorKit.pasteAction);
        return pasteAction != null;
    }

    private static int getPasteSimpleExitCode() {
        return DialogWrapper.NEXT_USER_EXIT_CODE;
    }

    static class SystemMultiClipboardContentChooser extends ContentChooser<Transferable> {

        SystemMultiClipboardContentChooser(Project project) {
            super(project, "Choose System Clipboard Content to Paste", true, true);
            setOKButtonText(ActionsBundle.actionText(IdeActions.ACTION_EDITOR_PASTE_SIMPLE));
            setOKButtonMnemonic('P');
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{
                    getOKAction(),
                    getCancelAction()};
        }

        @Override
        protected String getStringRepresentationFor(final Transferable content) {
            try {
                return (String) content.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e1) {
                return "";
            }
        }

        @NotNull
        @Override
        protected List<Transferable> getContents() {
            return ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).getClipboardTextTransferables();
        }

        @Override
        protected void removeContentAt(final Transferable content) {
            ApplicationManager.getApplication().getService(SystemMultiClipboardService.class).removeContent(content);
        }
    }
}