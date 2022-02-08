package dev.vality.deanonimus.db.exception;

public class ContractorNotFoundException extends RuntimeException {

    public ContractorNotFoundException(String id) {
        super("Not found with id: " + id);
    }

}
