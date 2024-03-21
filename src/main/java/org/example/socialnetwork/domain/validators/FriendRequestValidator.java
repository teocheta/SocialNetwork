package org.example.socialnetwork.domain.validators;

import org.example.socialnetwork.domain.FriendRequest;
import org.example.socialnetwork.domain.Utilizator;

public class FriendRequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        String errorMessage = "";
        Utilizator firstUser = entity.getId().getLeft();
        Utilizator secondUser = entity.getId().getRight();
        if (firstUser == null || secondUser == null || firstUser.equals(secondUser)) {
            errorMessage += "Cerere invalida!\n";
        }
        if (errorMessage.length() > 0) {
            throw new ValidationException(errorMessage);
        }
    }

}
