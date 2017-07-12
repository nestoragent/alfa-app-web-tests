package com.template.lib.aspects;

import com.template.lib.PageFactory;
import com.template.lib.annotations.ElementTitle;
import com.template.lib.exceptions.AutotestError;
import com.template.lib.exceptions.PageInitializationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.openqa.selenium.NoSuchElementException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by nestor on 11.07.2017.
 */
public class ExceptionAspect {

    static long lastFailureTimestamp = 0;

    /**
     * <p>
     * translateException.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.ProceedingJoinPoint} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.lang.Throwable if any.
     */
    @Around("execution(* *..*(..)) && within(com.template.*) && !within(com.template.bdd.util.Allure*) && !within(com.template.bdd.util.Allure*)")
    public Object translateException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception | AssertionError e) {
            //Add exceptions filter divided by || that are functional
            if ((e instanceof NoSuchElementException || e instanceof NullPointerException)
                    && null != PageFactory.getInstance().getCurrentPage()) {
                throw new AutotestError(getErrorText(e.getMessage()), e);
            } else {
                throw e;
            }
        }
    }

    private String getErrorText(String throwMessage) throws PageInitializationException, IllegalArgumentException, IllegalAccessException {
        String errorText = "";

        Field[] fields = PageFactory.getInstance().getCurrentPage().getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            Object currentObject = null;
            if (PageFactory.getInstance().getCurrentPage() != null) {
                currentObject = field.get(PageFactory.getInstance().getCurrentPage());
            }

            if (null != currentObject && throwMessage.contains(field.getName())) {
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof ElementTitle) {
                        errorText = "There is no element with title == " + ((ElementTitle) annotation).value();
                        break;
                    }
                }
            }
        }

        return errorText;
    }
}
