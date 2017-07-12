package com.template.lib.allure;

import gherkin.formatter.model.Step;
import ru.yandex.qatools.allure.cucumberjvm.AllureReporter;

/**
 * Created by nestor on 11.07.2017.
 */
public class NewAllureReporter extends AllureReporter {
    public NewAllureReporter() {
    }

    public String getStepName(Step step) {
        return step.getName().split("°\u0000\u0000\u0000 ").length > 1?step.getKeyword() + step.getName().split("°\u0000\u0000\u0000 ")[1]:step.getKeyword() + step.getName();
    }
}
