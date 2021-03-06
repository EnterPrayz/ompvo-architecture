package com.fifed.architecture.app.observers;


import com.fifed.architecture.datacontroller.interaction.core.Action;
import com.fifed.architecture.datacontroller.interaction.core.ErrorData;
import com.fifed.architecture.datacontroller.interaction.core.Model;

/**
 * Created by Fedir on 30.06.2016.
 */
public interface ObservableActivity {
    void registerObserver(ObserverActivity obsever);
    void unregisterObserver(ObserverActivity observer);
    void addAsPassiveObservers(ObserverActivity observer);
    void removePassiveObservers(ObserverActivity observer);
    boolean notifyOnBackPressed();
    void notifyObserversOnUpdateData(Model model);
    void notifyObserversOnError(ErrorData errorData);
    void notifyObserversOnPreloadFinshed(Action action);
}
