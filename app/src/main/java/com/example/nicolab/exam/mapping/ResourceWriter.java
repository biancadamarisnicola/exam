package com.example.nicolab.exam.mapping;

import java.io.IOException;

/**
 * Created by bianca on 30.11.2016.
 */
public interface ResourceWriter<E, Writer> {
    void write(E e, Writer writer) throws IOException;
}
