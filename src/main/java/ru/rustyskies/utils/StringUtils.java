package ru.rustyskies.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j;

@UtilityClass
@Log4j
public class StringUtils {

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.warn("Unable to convert \"" + str + "\" into a number");
            return 0;
        }
    }
}
