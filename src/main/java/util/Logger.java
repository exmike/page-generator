package util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

public class Logger {

    private Messager messager;
    private boolean debug = false;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void debug(String message, Object... args) {
        if (debug) {
            messager.printMessage(Kind.NOTE, String.format(message, args));
        }
    }

    public void error(String message, Object... args) {
        messager.printMessage(Kind.ERROR, String.format(message, args));
    }

    public void info(String message, Object... args) {
        messager.printMessage(Kind.NOTE, String.format(message, args));
    }

    public void warn(String message, Object... args) {
        messager.printMessage(Kind.WARNING, String.format(message, args));
    }

}
