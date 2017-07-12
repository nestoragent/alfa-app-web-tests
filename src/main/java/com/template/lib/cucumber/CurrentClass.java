package com.template.lib.cucumber;


import org.junit.runners.model.TestClass;

/**
 * Created by sbt-velichko-aa on 12.07.17.
 */
public class CurrentClass {
    private TestClass testClass;
    private boolean isTranslated = false;

    public CurrentClass(TestClass testClass) {
        this.testClass = testClass;
    }

    public CurrentClass markTranslated() {
        this.isTranslated = true;
        return this;
    }

    public boolean isTranslated(TestClass testClass) {
        return testClass.equals(this.testClass) && isTranslated;
    }

    public TestClass getTestClass() {
        return this.testClass;
    }
}
