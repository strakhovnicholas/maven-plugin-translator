package ru.strakhov.devs.com.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.strakhov.devs.com.translator.implementation.LibreTranslateTranslator;

import java.io.IOException;

public abstract class Translator {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    public abstract String translate(String text, String sourceLang, String targetLang);
}
