package dev.vality.deanonimus.db.exception;

public class ContractNotFoundException extends RuntimeException {

    public ContractNotFoundException(String id) {
        super("Not found with id: " + id);
    }

}
