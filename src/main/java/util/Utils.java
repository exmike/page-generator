package util;

import com.squareup.javapoet.ParameterSpec;
import java.util.List;

public class Utils {

    public static String PACKAGE_NAME = "page.generated";

    //todo mb rework
    public static String formatParamListToString(List<ParameterSpec> parameterSpecs) {
        //[aboba],[kek] -> aboba, kek
        return parameterSpecs.stream()
            .map(parameterSpec -> parameterSpec.name)
            .toList()
            .toString()
            .replace("[", "").replace("]", "");
    }

}
