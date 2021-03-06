package com.fifed.architecture.app.mvp.presenters.intefaces;


import com.fifed.architecture.datacontroller.interaction.core.Action;

/**
 * Created by Fedir on 30.06.2016.
 */
public interface Presenter {
    void onUserMadeAction(Action action);
    void notifyObserverIsDestroyed(String observerTag);
    void onPreloadAction(Action action);
    void onPreloadFinished(Action action);
    void onPresenterDestroy();

}
