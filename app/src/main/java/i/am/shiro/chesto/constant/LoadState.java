package i.am.shiro.chesto.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static i.am.shiro.chesto.constant.LoadState.ERROR;
import static i.am.shiro.chesto.constant.LoadState.LOADING;
import static i.am.shiro.chesto.constant.LoadState.SUCCESS;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Shiro on 2/12/2018.
 */

@Retention(SOURCE)
@IntDef({SUCCESS, LOADING, ERROR})
public @interface LoadState {
    int SUCCESS = 0;
    int LOADING = 1;
    int ERROR = -1;
}
