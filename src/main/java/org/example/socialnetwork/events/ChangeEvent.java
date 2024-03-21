package org.example.socialnetwork.events;

import org.example.socialnetwork.domain.Utilizator;

public class ChangeEvent implements Event{
    private ChangeEventType type;

    private Object data,oldData;

    public ChangeEvent(ChangeEventType type, Object data){
        this.type = type;
        this.data = data;
    }

    public ChangeEvent(ChangeEventType type, Utilizator data, Utilizator oldData){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return this.type;
    }

    public Object getData(){
        return this.data;
    }
    public Object getOldData(){
        return this.oldData;
    }
}
