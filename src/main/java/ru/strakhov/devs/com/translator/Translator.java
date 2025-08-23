package ru.strakhov.devs.com.implementation;

import java.io.IOException;

public interface Translator {
    String translate(String text, String sourceLang, String targetLang) throws IOException,
            InterruptedException;
}
