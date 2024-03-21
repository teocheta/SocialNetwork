package org.example.socialnetwork.domain.validators;

import org.example.socialnetwork.domain.Message;
import org.example.socialnetwork.domain.Utilizator;

import java.util.List;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String errorMessage = "";
        List<Utilizator> to = entity.getTo();
        String mesaj = entity.getMesaj();
        if(to.isEmpty()){
            errorMessage+="recievers list is empty!\n";
        }
        if(mesaj.isEmpty()){
            errorMessage+="empty message!\n";
        }
        if(!errorMessage.isEmpty()){
            throw new ValidationException(errorMessage);
        }


    }
}
