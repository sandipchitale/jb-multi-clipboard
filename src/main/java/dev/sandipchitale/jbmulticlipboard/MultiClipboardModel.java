package dev.sandipchitale.jbmulticlipboard;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This model stores and manages the clips.
 *
 * @author Sandip V. Chitale
 */
public class MultiClipboardModel {

    private final Set<ChangeListener> listners = new CopyOnWriteArraySet<ChangeListener>();

    public void addChangeListener(ChangeListener changeListener) {
        listners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        listners.remove(changeListener);
    }

    private void fireChange() {
        for (ChangeListener changeListener : listners) {
            changeListener.stateChanged(new ChangeEvent(INSTANCE));
        }
    }

    private MultiClipboardModel() {
    }

    private static MultiClipboardModel INSTANCE;

    synchronized public static MultiClipboardModel getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MultiClipboardModel();
        }
        return INSTANCE;
    }

    enum Mode {
        PUSH, APPEND
    }

    ;

    private static final String[] NO_SELECTIONS = new String[0];

    private MultiClipboardModel.Mode mode = Mode.PUSH;

    private final List<String> clips = new LinkedList<String>();

    public Object[] getElements(Object inputElement) {
        return get();
    }

    private static final Object[] none = new Object[0];

    public Object[] getChildren(Object parentElement) {
        return none;
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        return false;
    }

    public String[] get() {
        if (clips.isEmpty()) {
            return NO_SELECTIONS;
        }
        return clips.toArray(NO_SELECTIONS);
    }

    public void add(String selection) {
        switch (mode) {
            case PUSH:
                push(selection);
                break;
            case APPEND:
                append(selection);
                break;
        }
    }

    public void replace(String selection) {
        switch (mode) {
            case PUSH:
                pop();
                break;
            case APPEND:
                removeLast();
                break;
        }
        add(selection);
    }

    public void removeAll() {
        int size = clips.size();
        if (size == 0) {
            return;
        }
        clips.clear();
        fireChange();
    }

    public void remove(List<String> clipsToRemove) {
        if (clips.removeAll(clipsToRemove)) {
            fireChange();
        }
    }

    private String remove(int index) {
        int size = clips.size();
        if (size == 0) {
            return null;
        }
        if (index < 0 || index >= size) {
            return null;
        }
        try {
            return clips.remove(index);
        } finally {
            fireChange();
        }
    }

    public void rotateUp() {
        int size = clips.size();
        if (size > 1) {
            rotate(-1);
        }
    }

    public void rotateDown() {
        int size = clips.size();
        if (size > 1) {
            rotate(1);
        }
    }

    public boolean moveUp(int i) {
        if (i > 0 && i < clips.size()) {
            Collections.swap(clips, i, i - 1);
            fireChange();
            return true;
        }
        return false;
    }

    public boolean moveDown(int i) {
        if (i >= 0 && i < (clips.size() - 1)) {
            Collections.swap(clips, i, i + 1);
            fireChange();
            return true;
        }
        return false;
    }

    public void replace(int index, String selection) {
        if (remove(index) != null) {
            insert(index, selection);
        }
    }

    private void push(String selection) {
        insert(0, selection);
    }

    public String peek() {
        if (clips.isEmpty()) {
            return null;
        }
        return clips.get(0);
    }

    String pop() {
        if (clips.isEmpty()) {
            return null;
        }
        try {
            return clips.remove(0);
        } finally {
            fireChange();
        }
    }

    private void append(String selection) {
        insert(-1, selection);
    }

    private void insert(int index, String selection) {
        if (clips.contains(selection)) {
            int i = clips.indexOf(selection);
            clips.remove(i);

        }
        clips.add((index == -1 ? clips.size() : index), selection);
        fireChange();
    }

    private void rotate(int distance) {
        Collections.rotate(clips, distance);
        fireChange();
    }

    private String removeLast() {
        int size = clips.size();
        if (size == 0) {
            return null;
        }
        return remove(size - 1);
    }

    @SuppressWarnings("unused")
    private MultiClipboardModel.Mode getMode() {
        return mode;
    }

    @SuppressWarnings("unused")
    private void setMode(MultiClipboardModel.Mode mode) {
        this.mode = mode;
    }

}