package org.example.socialnetwork.domain.validators;
import org.example.socialnetwork.domain.Prietenie;
import org.example.socialnetwork.domain.Utilizator;

public class PrietenieValidator implements Validator<Prietenie>{
    /**
     * valideaza prietenie
     * @param entity, Prietenie
     * @throws ValidationException -daca prietenia e invalida
     */
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String errorMessage = "";
        Utilizator firstUser = entity.getId().getLeft();
        Utilizator secondUser = entity.getId().getRight();
        if (firstUser == null || secondUser == null || firstUser.equals(secondUser)) {
            errorMessage += "Prietenie invalida!\n";
        }
        if (!errorMessage.isEmpty()) {
            throw new ValidationException(errorMessage);
        }
    }
}

