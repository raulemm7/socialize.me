package org.example.labx.domain.validators;


import org.example.labx.domain.Utilizator;

import java.util.Collection;
import java.util.Optional;

/**
 * Validates instances of the {@link Utilizator} class.
 * Ensures that user attributes meet specified criteria and that users are unique.
 */
public class UtilizatorValidator implements Validator<Utilizator> {
    private final static int PASSWORD_SIZE = 8;
    /**
     * Validates the specified {@link Utilizator} entity against a collection of existing entities.
     *
     * @param entity the {@link Utilizator} entity to validate
     * @throws ValidationException if the entity's first name or last name is invalid,
     *         or if the user already exists in the collection
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        Optional.ofNullable(entity.getFirstName())
                .filter(name -> !name.trim().isEmpty())
                .orElseThrow(() -> new FirstNameException("Invalid first name!"));

        Optional.ofNullable(entity.getLastName())
                .filter(name -> !name.trim().isEmpty())
                .orElseThrow(() -> new LastNameException("Invalid last name!"));

        Optional.ofNullable(entity.getEmail())
                .filter(email -> !email.trim().isEmpty())
                .filter(email -> email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
                .orElseThrow(() -> new EmailException("Invalid email!"));

        Optional.ofNullable(entity.getPassword())
                .filter(password -> !password.trim().isEmpty())
                .orElseThrow(() -> new PasswordException("Invalid password!"));

        if(entity.getPassword().length() < PASSWORD_SIZE)
            throw new PasswordException("Password must be at least " + PASSWORD_SIZE + " characters");
    }
}

