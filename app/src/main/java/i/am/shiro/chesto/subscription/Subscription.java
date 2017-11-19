package i.am.shiro.chesto.subscription;

import i.am.shiro.chesto.listener.Listener;
import i.am.shiro.chesto.notifier.AbstractNotifier;

/**
 * Created by Shiro on 11/11/2017.
 */

public class Subscription {

    private AbstractNotifier notifier;

    private Listener listener;

    public Subscription(AbstractNotifier notifier, Listener listener) {
        this.notifier = notifier;
        this.listener = listener;
    }

    public void unsubscribe() {
        notifier.removeListener(listener);
        notifier = null;
        listener = null;
    }

}
