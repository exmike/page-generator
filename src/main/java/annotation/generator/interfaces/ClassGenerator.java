package annotation.generator.interfaces;

import com.squareup.javapoet.TypeSpec;
import java.util.List;
import model.Page;

public interface ClassGenerator {

    List<TypeSpec> generateClasses(List<Page> pages);


}
