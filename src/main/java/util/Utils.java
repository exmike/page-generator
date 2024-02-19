package util;

import com.squareup.javapoet.ParameterSpec;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String PACKAGE_NAME = "page.generated";

    //todo mb rework
    public static String formatParamListToString(List<ParameterSpec> parameterSpecs) {
        List<String> strings = new ArrayList<>();
        parameterSpecs.forEach(parameterSpec -> strings.add(parameterSpec.name));
        //[aboba],[kek] -> aboba, kek
        return strings.toString().replace("[", "").replace("]", "");
    }

}
