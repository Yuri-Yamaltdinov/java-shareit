package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;

    public EntityNotFoundException(Class<?> entityClass, String message) {
        super(message);
        this.entityName = entityClass.getSimpleName();
    }

    public String getEntityName() {
        return entityName;
    }
}
