
package com.leysoft.util;

public class Constants {

    private Constants() {
    }

    public static final String PREFIX_ROLE = "ROLE_";
    public static final String HEADER_NAME = "Authorization";
    public static final String PREFIX_BEARER = "Bearer ";
    public static final String APPLICATION_JSON = "application/json";

    public static class Message {

        private Message() {
        }

        public static final String SUCCESSFUL_AUTHENTICATION = "Welcome";
        public static final String UNSUCCESSFUL_AUTHENTICATION = "Authentication error";
    }

    public static class Name {

        private Name() {
        }

        public static final String MESSAGE_NAME = "message";
        public static final String ERROR_NAME = "error";
        public static final String TOKEN_NAME = "token";
        public static final String ROLES_NAME = "roles";
        public static final String USERNAME_NAME = "username";
        public static final String PASW_NAME = "username";
    }
}
