package com.timonsarakinis.vmwriter;
public enum VmSegmentType {

    ARG("argument"), CONST("constant"), LOCAL("local"), THIS("this"), THAT("that"), STATIC("static"), TEMP("temp"), POINTER("pointer");

    private final String segment;

    VmSegmentType(String segment) {
        this.segment = segment;
    }

    public String getSegment() {
        return segment;
    }
}
