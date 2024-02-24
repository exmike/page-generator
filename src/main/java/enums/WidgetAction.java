package enums;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Deprecated
@RequiredArgsConstructor
public enum WidgetAction {
    BUTTON("button"),
    LABEL("label");

    private final String actionName;

    public static WidgetAction contains(String fieldName) {
        for (WidgetAction value : values()) {
            if (StringUtils.containsIgnoreCase(fieldName, value.toString())) {
                return value;
            }
        }
        throw new RuntimeException("in enum contains");//todo
    }

    public static WidgetAction getActionByClassName(String className) {
        switch (WidgetAction.contains(className)) {
            case BUTTON -> {
                return WidgetAction.BUTTON;
            }
            case LABEL -> {
                return WidgetAction.LABEL;
            }
            default -> throw new RuntimeException("in getActionByClassName method");
        }
    }


    public String toString() {
        return this.actionName;
    }
}
