package com.todo.controller.exception.custom;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg) { super(msg); }
}