package org.example.socialnetwork.observer;

import org.example.socialnetwork.events.Event;

public interface Observer<E extends Event>{

    void update(E eveniment);
}
