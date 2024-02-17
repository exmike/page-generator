package util;

import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static String PACKAGE_NAME = "page.generated";


    //ErrorPageGen -> errorPage().
    public static String replaceGen(String className) {
        className = StringUtils.uncapitalize(className);
        return className.replace("Gen", "").concat("().");
    }

}
