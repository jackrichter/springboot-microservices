package net.javaguides.departmentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DepartmentAlreadyExistsException extends RuntimeException{
    private String message;

    public DepartmentAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
