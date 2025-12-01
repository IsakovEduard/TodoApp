package com.todo.controller.exception.customExceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg) { super(msg); }
}