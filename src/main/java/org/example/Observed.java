package org.example;

import org.example.interfaces.IObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class Observed {
    protected List<IObserver> iObserverList = new ArrayList<>();

    public void addObserver(IObserver o){
        System.out.println("dodawanie do listy");
        if (!iObserverList.contains(o)){
            iObserverList.add(o);
        }
    }

    public void deleteObserver(IObserver o) {
        iObserverList.remove(o);
    }

    public void notifyObservers(String source, String eventType){
        System.out.println(iObserverList.size());
        if (!iObserverList.isEmpty()){
            for (IObserver o : iObserverList){
                o.reagujNaZdarzenie(source, eventType);
            }
        }

    }

}
