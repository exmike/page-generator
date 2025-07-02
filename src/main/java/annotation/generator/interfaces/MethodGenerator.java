package annotation.generator.interfaces;

import java.util.List;
import model.Page;

public interface MethodGenerator {

    void generateMethodsToPage(List<Page> pages);
}
