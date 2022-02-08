package dev.vality.deanonimus.db.exception;

public class PartyNotFoundException extends RuntimeException {

    public PartyNotFoundException(String id) {
        super("Not found with id: " + id);
    }
}
