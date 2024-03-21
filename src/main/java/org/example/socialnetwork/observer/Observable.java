package org.example.socialnetwork.observer;

import org.example.socialnetwork.domain.Utilizator;
import org.example.socialnetwork.events.Event;

import java.util.Optional;

public interface Observable<E extends Event>{

    void addObserver(Observer<E> e);

    void removeObserver(Observer<E> e);

    void notifyObservers(E t);
}
