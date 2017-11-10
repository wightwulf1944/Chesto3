package i.am.shiro.chesto.notifiers;

import java.util.LinkedList;
import java.util.List;

import i.am.shiro.chesto.listeners.Listener;

/**
 * Created by Shiro on 11/5/2017.
 */

public abstract class AbstractNotifier<T extends Listener> {

    protected List<T> listeners = new LinkedList<>();

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
}
