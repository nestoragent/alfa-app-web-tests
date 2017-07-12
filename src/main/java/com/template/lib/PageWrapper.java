package com.template.lib;

import com.google.common.reflect.ClassPath;
import com.template.lib.annotations.PageEntry;
import com.template.lib.exceptions.AutotestError;
import com.template.lib.exceptions.PageInitializationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class PageWrapper {
    private String currentPageTitle;
    private Page currentPage;

    private final String pagesPackage;

    /**
     *
     * @param pagesPackage a {@link java.lang.String} object.
     */
    public PageWrapper(String pagesPackage) {
        this.pagesPackage = pagesPackage;
    }

    /**
     * Initialize page with specified title and save its instance to
     * {@link PageWrapper#currentPage} for further use
     *
     * @param title page title
     * @return page instance
     * @throws PageInitializationException if failed to execute corresponding
     * page constructor
     */
    public Page getPage(String title) throws PageInitializationException {
        if (null == currentPage || !currentPageTitle.equals(title)) {
            if (null != currentPage) {
                currentPage = getPage(currentPage.getClass().getPackage().getName(), title);
            }
            if (null == currentPage) {
                currentPage = getPage(pagesPackage, title);
            }
            if (null == currentPage) {
                throw new AutotestError("Page object with title '" + title + "' is not registered");
            }
        }
        return currentPage;
    }

    /**
     * Initialize page by class
     *
     * @param page TODO
     * @return TODO
     * @throws PageInitializationException
     * TODO
     */
    public Page getPage(Class<? extends Page> page) throws PageInitializationException {
        return bootstrapPage(page);
    }

    /**
     * <p>
     * Get Page by PageEntry title </p>
     *
     * @param packageName a {@link java.lang.String} object.
     * @param title a {@link java.lang.String} object.
     * @return a Page object.
     * @throws PageInitializationException {@inheritDoc}
     */
    public Page getPage(String packageName, String title) throws PageInitializationException {
        return bootstrapPage(getPageClass(packageName, title));
    }

    /**
     * <p>
     * Getter for the field <code>currentPage</code>.</p>
     *
     * @return a Page object.
     * @throws PageInitializationException
     * TODO
     */
    public Page getCurrentPage() throws PageInitializationException {
        if (null == currentPage) {
            throw new PageInitializationException("Current page not initialized!");
        } else {
            return currentPage;
        }
    }

    /**
     * Redirect to Page by Page Entry url value
     *
     * @param title a {@link java.lang.String} object.
     * @return a Page object.
     * @throws PageInitializationException TODO
     */
    public Page changeUrlByTitle(String title) throws PageInitializationException {
        if (null != currentPage) {
            currentPage = changeUrlByTitle(currentPage.getClass().getPackage().getName(), title);
        }
        if (null == currentPage) {
            currentPage = changeUrlByTitle(pagesPackage, title);
        }
        if (null == currentPage) {
            throw new AutotestError("Page Object with title " + title + " is not registered");
        }
        return currentPage;
    }

    /**
     * Redirect to Page by Page Entry url value
     *
     * @param packageName a {@link java.lang.String} object.
     * @param title a {@link java.lang.String} object.
     * @return a Page object.
     * @throws PageInitializationException
     * TODO
     */
    public Page changeUrlByTitle(String packageName, String title) throws PageInitializationException {

        Class<?> pageClass = getPageClass(packageName, title);
        if (pageClass == null) {
            return null;
        }

        Annotation annotation = pageClass.getAnnotation(PageEntry.class);
        if (annotation != null && !((PageEntry) annotation).url().isEmpty()) {
            if (PageFactory.getWebDriver().getCurrentUrl() == null) {
                throw new AutotestError("Current URL is null");
            } else {
                try {
                    URL currentUrl = new URL(PageFactory.getWebDriver().getCurrentUrl());
                    String finalUrl = new URL(currentUrl.getProtocol(), currentUrl.getHost(), currentUrl.getPort(),
                            ((PageEntry) annotation).url()).toString();
                    PageFactory.getWebDriver().navigate().to(finalUrl);
                } catch (MalformedURLException ex) {
                    log.error("Failed to get current url", ex);
                }
            }

            return bootstrapPage(pageClass);
        }

        throw new AutotestError("Page " + title + " doesn't have fast URL in PageEntry");
    }

    /**
     *
     * @param packageName TODO
     * @param title TODO
     * @return
     */
    private Class<?> getPageClass(final String packageName, String title) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Set<Class<?>> allClasses = new HashSet<>();
        try {
            for (ClassPath.ClassInfo info : ClassPath.from(loader).getAllClasses()) {
                if (info.getName().startsWith(packageName + ".")) {
                    allClasses.add(info.load());
                }
            }
        } catch (IOException ex) {
            log.warn("Failed to shape class info set", ex);
        }

        for (Class<?> page : allClasses) {
            String pageTitle = null;
            if (null != page.getAnnotation(PageEntry.class)) {
                pageTitle = page.getAnnotation(PageEntry.class).title();
            } else {
                try {
                    pageTitle = (String) FieldUtils.readStaticField(page, "title", true);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    log.debug("Failed to read {} becase it is not page object", pageTitle, ex);
                }
            }
            if (pageTitle != null && pageTitle.equals(title)) {
                return page;
            }
        }

        return null;
    }

    /**
     * Run constructor of specified page class and put its instance into static
     * {@link #currentPage} variable
     *
     * @param page page class
     * @return initialized page
     * @throws PageInitializationException if failed to execute corresponding
     * page constructor
     */
    private Page bootstrapPage(Class<?> page) throws PageInitializationException {
        if (page != null) {
            try {
                @SuppressWarnings("unchecked")
                Constructor<Page> constructor = ((Constructor<Page>) page.getConstructor());
                constructor.setAccessible(true);
                currentPage = constructor.newInstance();
                currentPageTitle = currentPage.getTitle();
                return currentPage;
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new PageInitializationException("Failed to initialize page '" + page + "'", e);
            }
        }
        return null;
    }

    /**
     * @return the currentPageTitle
     */
    public String getCurrentPageTitle() {
        return currentPageTitle;
    }
}
