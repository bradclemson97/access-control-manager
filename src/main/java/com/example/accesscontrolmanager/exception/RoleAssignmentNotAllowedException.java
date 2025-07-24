package com.example.accesscontrolmanager.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Exception thrown when a user cannot assign a role due to missing
 * role_inheritance type=ASSIGNMENT entry
 */
public class RoleAssignmentNotAllowedException extends ResponseStatusException {

    public static final HttpStatusCode CODE = FORBIDDEN;

    public RoleAssignmentNotAllowedException(String message) { super(CODE, message); }
}
