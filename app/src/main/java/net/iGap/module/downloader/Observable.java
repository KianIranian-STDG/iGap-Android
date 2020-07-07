package net.iGap.module.downloader;


import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private List<Observer<T>> observers = new ArrayList<>();

    public void addObserver(Observer<T> observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(T data) {
        for (Observer<T> observer : observers) {
            observer.onUpdate(data);
        }
    }

    public void removeAdd() {
        observers.clear();
    }
}
