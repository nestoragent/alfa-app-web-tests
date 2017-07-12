package com.template.stepDefinitions;

import com.template.lib.PageFactory;
import com.template.lib.extensions.DriverExtension;
import cucumber.api.java.en.When;

/**
 * Created by nestor on 12.07.2017.
 */
public class CommonStepDefs {

    @When("^user go to the facebook$")
    public void init_current_page(String who) {
        PageFactory.getDriver().get("https://www.facebook.com/login");
        DriverExtension.waitForPageToLoad();
    }
}
