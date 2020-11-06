package com.senior.prevent.populator;

public interface UploadPopulator<SOURCE, TARGET> {

    void populate(SOURCE source, TARGET target);
}
