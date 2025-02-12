package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    INACTIVE_MEMBER_FORBIDDEN(403, "Inactive member cannot perform this action"),
    QUESTION_NOT_FOUND(404, "Question not found"),
    QUESTION_EXISTS(409, "Question exists"),
    AUTHOR_ONLY_ACCESS(403, "Only the author has access to this post"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    ANSWER_EXISTS(404, "Answer exists"),
    CANNOT_CHANGE_ORDER(403, "Order can not change"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
