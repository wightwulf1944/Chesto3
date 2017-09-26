package i.am.shiro.chesto.engine;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Subaru Tashiro on 7/12/2017.
 */

final class SubscriberList {

    private final List<SearchSubscriber> subscribers = new LinkedList<>();

    void addSubscriber(SearchSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    void removeSubscriber(SearchSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    void notifyLoading(boolean isLoading) {
        for (SearchSubscriber subscriber : subscribers) {
            subscriber.notifyLoading(isLoading);
        }
    }

    void notifyError() {
        subscribers.forEach(SearchSubscriber::notifyError);
    }

    void notifyPostAdded(int index) {
        for (SearchSubscriber subscriber : subscribers) {
            subscriber.notifyPostAdded(index);
        }
    }

    void notifyPostUpdated(int index) {
        for (SearchSubscriber subscriber : subscribers) {
            subscriber.notifyPostUpdated(index);
        }
    }

    void notifyCleared() {
        subscribers.forEach(SearchSubscriber::notifyCleared);
    }
}
