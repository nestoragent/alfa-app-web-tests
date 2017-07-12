package com.template.stepDefinitions;

import com.template.lib.Page;
import com.template.lib.PageFactory;
import com.template.lib.allure.NewAllureReporter;
import com.template.lib.allurehelper.OnFailureScheduler;
import com.template.lib.annotations.ElementTitle;
import com.template.lib.datajack.Stash;
import com.template.lib.exceptions.FactoryRuntimeException;
import com.template.lib.support.ClassUtilsExt;
import com.template.lib.support.FieldUtilsExt;
import com.template.lib.support.Props;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.reflections.Reflections;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class SetupStepDefs {

    private void initStash() {
        //login
        if (null != System.getProperty("login") && !"".equals(System.getProperty("login")))
            Stash.put("login", System.getProperty("login"));

        //password
        if (null != System.getProperty("password") && !"".equals(System.getProperty("password")))
            Stash.put("password", System.getProperty("password"));
    }

    @Before()
    public void setUp() {

        initStash();
        //Apply failure callback
        NewAllureReporter.applyFailureCallback(OnFailureScheduler.class);

//        try {
//            String tasksToKill = Props.get("tasks.to.kill");
//            if (!PageFactory.isSharingProcessing() && !"".equals(tasksToKill)) {
//                for (String task : tasksToKill.split(",")) {
//                    if (SystemUtils.IS_OS_WINDOWS) {
//                        Runtime.getRuntime().exec("taskkill /IM " + task.trim() + " /F");
//                    } else {
//                        Runtime.getRuntime().exec("killall " + task.trim());
//                    }
//                }
//            }
//        } catch (IOException e) {
//            log.debug("Failed to kill one of task to kill", e);
//        }

        String aspectDisabled = Props.get("page.aspect.disabled");
        if (!"".equals(aspectDisabled)) {
            PageFactory.setAspectsDisabled(Boolean.parseBoolean(aspectDisabled));
        }

        PageFactory.getDriver();
        PageFactory.getInstance();

        Reflections reflections;
        reflections = new Reflections(PageFactory.getPagesPackage());

        Collection<String> allClassesString = reflections.getStore().get("SubTypesScanner").values();
        Set<Class<?>> allClasses = new HashSet();
        for (String clazz : allClassesString) {
            try {
                allClasses.add(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                log.warn("Cannot add all classes to set from package storage", e);
            }
        }

        for (Class<?> page : allClasses) {
            List<Class> supers = ClassUtilsExt.getSuperclassesWithInheritance(page);
            if (!supers.contains(Page.class) && !supers.contains(HtmlElement.class)) {
                if (page.getName().contains("$")) {
                    continue; //We allow private additional classes but skip it if its not extends Page
                } else {
                    throw new FactoryRuntimeException("Class " + page.getName() + " is not extended from Page class. Check you webdriver.pages.package property.");
                }
            }
            List<Field> fields = FieldUtilsExt.getDeclaredFieldsWithInheritance(page);
            Map<Field, String> fieldsMap = new HashMap<>();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if (fieldType.equals(WebElement.class)) {

                    ElementTitle titleAnnotation = field.getAnnotation(ElementTitle.class);
                    if (titleAnnotation != null) {
                        fieldsMap.put(field, titleAnnotation.value());
                    } else {
                        fieldsMap.put(field, field.getName());
                    }
                }
            }

            PageFactory.getPageRepository().put((Class<? extends Page>) page, fieldsMap);
        }
    }

//    @After
    public void tearDown() {
        PageFactory.dispose();
    }

}
