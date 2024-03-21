package org.example.socialnetwork.domain.validators;
import org.example.socialnetwork.domain.Utilizator;


public class UtilizatorValidator implements Validator<Utilizator> {

    /**
     * Valideaza nume/prenume
     * @param name, String,nevid,doar din litere
     * @return true, daca numele e valid,false altfel
     */

    private boolean validName(String name) {
        if (name == null) {
            return false;
        }
        if (name.length() == 0) {
            return false;
        }
        for (char character : name.toCharArray()) {
            if (!(Character.isLetter(character))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Valideaza un username
     * @param userName, String, fara ' '
     * @return true, daca username e valid,false altfel
     */
    private boolean validUserName(String userName) {
        if (userName == null) {
            return false;
        }
        if (userName.length() == 0) {
            return false;
        }

        for (char character : userName.toCharArray()) {
            if (character == ' ') {
                return false;
            }
        }
        return true;
    }
    /**
     * Valideaza utilizator
     * @param entity, Utilizator
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String errorMessage = "";
        if (!validName(entity.getFirstName())) {
            errorMessage += "Invalid first name!\n";
        }
        if (!validName(entity.getLastName())) {
            errorMessage += "Invalid last name!\n";
        }
        if (!validUserName(entity.getUserName())) {
            errorMessage += "Invalid user name!\n";
        }

        if (errorMessage.length() != 0) {
            throw new ValidationException(errorMessage);
        }
    }
}

