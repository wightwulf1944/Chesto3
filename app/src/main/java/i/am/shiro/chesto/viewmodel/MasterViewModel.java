package i.am.shiro.chesto.viewmodel;

import android.arch.lifecycle.ViewModel;

/**
 * Created by Shiro on 1/3/2018.
 */

public class MasterViewModel extends ViewModel {

    private boolean isLoaded;

    private String flowId;

    private String query;

    public MasterViewModel() {
        isLoaded = false;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        if (query == null) {
            this.query = "";
        } else {
            this.query = query;
        }
    }

    public void init() {
        // todo init here
        isLoaded = true;
    }
}
