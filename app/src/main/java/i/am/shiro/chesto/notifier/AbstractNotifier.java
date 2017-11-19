package i.am.shiro.chesto.notifier;

import java.util.LinkedList;
import java.util.List;

import i.am.shiro.chesto.listener.Listener;

/**
 * Created by Shiro on 11/5/2017.
 */

public abstract class AbstractNotifier<T extends Listener> {

    final List<T> listeners = new LinkedList<>();

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
}
