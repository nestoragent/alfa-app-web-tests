package com.template.lib.cucumber;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.junit.ExamplesRunner;
import cucumber.runtime.junit.ExecutionUnitRunner;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.model.CucumberBackground;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenario;
import cucumber.runtime.model.StepContainer;
import gherkin.formatter.model.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by sbt-velichko-aa on 12.07.17.
 */
@Slf4j
public class NewCucumber extends Cucumber {

    public static final String SECRET_DELIMITER = "°\u0000\u0000\u0000 ";
    private static final String PLUGIN_PACKAGE = "com.template";
    private static final String STRING_START_REGEX = "^";
    private static final ThreadLocal<CucumberFeature> CUCUMBER_FEATURE = new ThreadLocal();
    private static final ThreadLocal<CurrentClass> CURRENT_CLASS = new ThreadLocal();

    public NewCucumber(Class clazz) throws InitializationError, IOException, IllegalAccessException {
        super(clazz);
        CURRENT_CLASS.set(new CurrentClass(this.getTestClass()));
    }

    public static CucumberFeature getFeature() {
        return (CucumberFeature) CUCUMBER_FEATURE.get();
    }

    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        Object stepDefinitionsByPatternTranslated = new TreeMap();

        try {
            Runtime ex = (Runtime) FieldUtils.readField(this, "runtime", true);
            RuntimeGlue glue = (RuntimeGlue) ex.getGlue();
            CUCUMBER_FEATURE.set((CucumberFeature) FieldUtils.readField(child, "cucumberFeature", true));
            List children = (List) FieldUtils.readField(child, "children", true);
            Map stepDefinitionsByPattern = (Map) FieldUtils.readField(glue, "stepDefinitionsByPattern", true);
            StepContainer currentStepContainer = (StepContainer) FieldUtils.readField(CUCUMBER_FEATURE.get(), "currentStepContainer", true);
            CucumberBackground cucumberBackground = (CucumberBackground) FieldUtils.readField(CUCUMBER_FEATURE.get(), "cucumberBackground", true);
            if (((CurrentClass) CURRENT_CLASS.get()).isTranslated(this.getTestClass())) {
                stepDefinitionsByPatternTranslated = stepDefinitionsByPattern;
            } else {
                Iterator newChildren = stepDefinitionsByPattern.entrySet().iterator();

                while (newChildren.hasNext()) {
                    Map.Entry stepDefinitionEntry = (Map.Entry) newChildren.next();
                    StepDefinition childRunner = (StepDefinition) stepDefinitionEntry.getValue();
                    Method runners = (Method) FieldUtils.readField(childRunner, "method", true);
                    String exampleRunners = (String) stepDefinitionEntry.getKey();

                    try {
                        Class examples = runners.getDeclaringClass();
                        I18N newExamples = I18N.getI18n(examples, ((CucumberFeature) CUCUMBER_FEATURE.get()).getI18n().getLocale(), "i18n");
                        exampleRunners = newExamples.get(exampleRunners);
                        String canonicalName = examples.getCanonicalName();
                        if (canonicalName.contains("com.template.")) {
                            String exampleRunner = canonicalName.substring("com.template.".length(), canonicalName.indexOf(46, "com.template.".length()));
                            String cucumberScenario = "^" + exampleRunner + "°\u0000\u0000\u0000 " + (exampleRunners.startsWith("^") ? exampleRunners.substring(1) : exampleRunners);
                            ((Map) stepDefinitionsByPatternTranslated).put(cucumberScenario, childRunner);
                            Pattern newExampleRunners = Pattern.compile(cucumberScenario);
                            FieldUtils.writeField(childRunner, "pattern", newExampleRunners, true);
                            FieldUtils.writeField(childRunner, "argumentMatcher", new JdkPatternArgumentMatcher(newExampleRunners), true);
                        }
                    } catch (I18NRuntimeException var22) {
                        log.error("There is no bundle for translation class. Writing it as is.", var22);
                        ((Map) stepDefinitionsByPatternTranslated).put(exampleRunners, childRunner);
                    }
                }

                CURRENT_CLASS.set((new CurrentClass(this.getTestClass())).markTranslated());
            }

            ArrayList newChildren1 = new ArrayList();

            Object childRunner1;
            for (Iterator stepDefinitionEntry1 = children.iterator(); stepDefinitionEntry1.hasNext(); newChildren1.add(childRunner1)) {
                childRunner1 = stepDefinitionEntry1.next();
                if (childRunner1 instanceof ExecutionUnitRunner) {
                    FieldUtils.writeField(childRunner1, "runnerSteps", this.processSteps(((ExecutionUnitRunner) childRunner1).getRunnerSteps(), (Map) stepDefinitionsByPatternTranslated), true);
                    CucumberScenario runners2 = (CucumberScenario) FieldUtils.readField(childRunner1, "cucumberScenario", true);
                    FieldUtils.writeField(runners2, "steps", this.processSteps(runners2.getSteps(), (Map) stepDefinitionsByPatternTranslated), true);
                    FieldUtils.writeField(childRunner1, "cucumberScenario", runners2, true);
                } else {
                    Object runners1 = FieldUtils.readField(childRunner1, "runners", true);
                    Object exampleRunners1 = ((List) runners1).get(0);
                    List examples1 = (List) FieldUtils.readField(exampleRunners1, "runners", true);
                    ArrayList newExamples1 = new ArrayList();
                    Iterator canonicalName1 = examples1.iterator();

                    while (canonicalName1.hasNext()) {
                        ExecutionUnitRunner exampleRunner1 = (ExecutionUnitRunner) canonicalName1.next();
                        FieldUtils.writeField(exampleRunner1, "runnerSteps", this.processSteps(exampleRunner1.getRunnerSteps(), (Map) stepDefinitionsByPatternTranslated), true);
                        CucumberScenario cucumberScenario1 = (CucumberScenario) FieldUtils.readField(exampleRunner1, "cucumberScenario", true);
                        FieldUtils.writeField(cucumberScenario1, "steps", this.processSteps(cucumberScenario1.getSteps(), (Map) stepDefinitionsByPatternTranslated), true);
                        FieldUtils.writeField(exampleRunner1, "cucumberScenario", cucumberScenario1, true);
                        newExamples1.add(exampleRunner1);
                        FieldUtils.writeField(exampleRunners1, "runners", newExamples1, true);
                        ArrayList newExampleRunners1 = new ArrayList();
                        newExampleRunners1.add((ExamplesRunner) exampleRunners1);
                        List unModifiableExamplesRunners = Collections.unmodifiableList(newExampleRunners1);
                        FieldUtils.writeField(childRunner1, "runners", unModifiableExamplesRunners, true);
                    }
                }
            }

            FieldUtils.writeField(currentStepContainer, "steps", this.processSteps(currentStepContainer.getSteps(), (Map) stepDefinitionsByPatternTranslated), true);
            FieldUtils.writeField(CUCUMBER_FEATURE.get(), "currentStepContainer", currentStepContainer, true);
            if (cucumberBackground != null) {
                FieldUtils.writeField(cucumberBackground, "steps", this.processSteps(cucumberBackground.getSteps(), (Map) stepDefinitionsByPatternTranslated), true);
                FieldUtils.writeField(CUCUMBER_FEATURE.get(), "cucumberBackground", cucumberBackground, true);
            }

            FieldUtils.writeField(child, "children", newChildren1, true);
            FieldUtils.writeField(child, "cucumberFeature", CUCUMBER_FEATURE.get(), true);
            FieldUtils.writeField(glue, "stepDefinitionsByPattern", stepDefinitionsByPatternTranslated, true);
            FieldUtils.writeField(ex, "glue", glue, true);
            FieldUtils.writeField(this, "runtime", ex, true);
            super.runChild(child, notifier);
        } catch (Exception var23) {
            throw new CucumberException(var23);
        }
    }

    private List<Step> processSteps(List<Step> steps, Map<String, StepDefinition> stepDefinitionsByPatternTranslated) throws IllegalAccessException {
        ArrayList matchedStepDefsPatterns = new ArrayList();

        for (int i = 0; i < steps.size(); ++i) {
            matchedStepDefsPatterns.clear();
            Step step = (Step) steps.get(i);
            String stepName = step.getName();
            Iterator context = stepDefinitionsByPatternTranslated.entrySet().iterator();

            while (context.hasNext()) {
                Map.Entry isMatched = (Map.Entry) context.next();
                if (Pattern.compile(this.getPattern(((StepDefinition) isMatched.getValue()).getPattern())).matcher(stepName).matches()) {
                    matchedStepDefsPatterns.add(isMatched.getKey());
                }
            }

            if (!matchedStepDefsPatterns.isEmpty()) {
                if (matchedStepDefsPatterns.size() == 1) {
                    if (((String) matchedStepDefsPatterns.get(0)).contains("°\u0000\u0000\u0000 ")) {
                        FieldUtils.writeField(step, "name", this.getContext((String) matchedStepDefsPatterns.get(0)) + "°\u0000\u0000\u0000 " + stepName, true);
                    }
                } else {
                    String var11 = this.getContext(((Step) steps.get(i - 1)).getName());
                    boolean var12 = false;
                    Iterator var9 = matchedStepDefsPatterns.iterator();

                    while (var9.hasNext()) {
                        String matchedStepDefsPattern = (String) var9.next();
                        if (matchedStepDefsPattern.contains(var11)) {
                            var12 = true;
                            FieldUtils.writeField(step, "name", var11 + "°\u0000\u0000\u0000 " + stepName, true);
                        }
                    }

                    if (!var12) {
                        throw new RuntimeException(String.format("There isn\'t step %s in context %s", new Object[]{step.getName(), var11}));
                    }
                }

                steps.set(i, step);
            }
        }

        return steps;
    }

    private String getContext(String reg) {
        reg = reg.startsWith("^") ? reg.substring(1) : reg;
        String[] split = reg.split("°\u0000\u0000\u0000 ");
        return split.length > 1 ? split[0] : "";
    }

    private String getPattern(String reg) {
        String[] split = reg.split("°\u0000\u0000\u0000 ");
        return split.length > 1 ? "^" + split[1] : reg;
    }

    private List<StepDefinition> findUniques(Queue<StepDefinition> q) {
        ArrayList uniques = new ArrayList();

        while (q.peek() != null) {
            StepDefinition stepDefinition = (StepDefinition) q.remove();
            if (!q.contains(stepDefinition)) {
                uniques.add(stepDefinition);
            }
        }

        return uniques;
    }
}
