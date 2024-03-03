package main;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class myformatter extends Formatter {

    public myformatter() {
    }

    public String format(LogRecord rec) {
        return (rec.getMessage());
    }

}
