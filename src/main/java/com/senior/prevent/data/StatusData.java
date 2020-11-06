package com.senior.prevent.data;

import java.util.stream.Stream;

public enum StatusData {
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    OTHER(303),
    NOT_MODIFIED(304),
    TEMPORARY_REDIRECT(307),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND (404),
    NOT_ALLOWED(405),
    INTERNAL_ERROR(500),
    NOT_IMPLEMENTED(501);
    private int status;

    StatusData(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static StatusData of(int status) {
        return Stream.of(StatusData.values())
                .filter(s -> s.getStatus() == status)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static boolean is(int status){
        return Stream.of(StatusData.values()).anyMatch(s ->s.getStatus() == status);
    }
}
