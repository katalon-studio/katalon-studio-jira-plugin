package com.katalon.plugin.jira.composer.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.framework.Bundle;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class CustomBrowser {
    private static Logger logger = (Logger) LoggerFactory.getLogger(CustomBrowser.class);

    private static boolean isWindows = org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

    private static Bundle chromiumBundle;

    private static Class<?> chromiumClass;

    private static Constructor<?> constructor;

    private static Method setUrl;

    private static Method setJavascriptEnabled;

    private static Method evaluate;

    private static Method addProgressListener;

    private static Method addLocationListener;

    private static Method getUrl;

    private static Method removeProgressListener;

    private static Method close;

    private static Method execute;

    private Browser defaultBrowser;

    private Object chromiumBrowser;

    static {
        if (isWindows) {
            chromiumBundle = Platform.getBundle("org.eclipse.swt.chromium");
            try {
                chromiumClass = chromiumBundle.loadClass("org.eclipse.swt.chromium.Browser");
                constructor = chromiumClass.getConstructor(new Class<?>[] { Composite.class, int.class });

                setUrl = chromiumClass.getMethod("setUrl", new Class[] { String.class });
                setJavascriptEnabled = chromiumClass.getMethod("setJavascriptEnabled", new Class[] { boolean.class });
                evaluate = chromiumClass.getMethod("evaluate", new Class[] { String.class });
                addProgressListener = chromiumClass.getMethod("addProgressListener",
                        new Class[] { ProgressListener.class });
                addLocationListener = chromiumClass.getMethod("addLocationListener",
                        new Class[] { LocationListener.class });
                getUrl = chromiumClass.getMethod("getUrl");
                removeProgressListener = chromiumClass.getMethod("removeProgressListener",
                        new Class[] { ProgressListener.class });
                close = chromiumClass.getMethod("close");
                execute = chromiumClass.getMethod("execute", new Class[] { String.class });
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                logger.error("Cannot get method from chromium class", e);
            }
        }
    }

    public CustomBrowser(Composite parent, int style) {
        if (isWindows) {
            try {
                chromiumBrowser = constructor.newInstance(parent, style);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.error("Cannot instantiate chromium browser", e);
            }
        } else {
            defaultBrowser = new Browser(parent, style);
        }
    }

    public void setUrl(String url) {
        if (isWindows) {
            try {
                setUrl.invoke(chromiumBrowser, url);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.setUrl(url);
        }
    }

    public void setJavascriptEnabled(boolean enable) {
        if (isWindows) {
            try {
                setJavascriptEnabled.invoke(chromiumBrowser, enable);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.setJavascriptEnabled(enable);
        }
    }

    public Object evaluate(String script) {
        if (isWindows) {
            try {
                return evaluate.invoke(chromiumBrowser, script);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
                return e;
            }
        } else {
            return defaultBrowser.evaluate(script);
        }
    }

    public void setLayoutData(GridData gridData) {
        if (isWindows) {
            ((Control) chromiumBrowser).setLayoutData(gridData);
        } else {
            defaultBrowser.setLayoutData(gridData);
        }
    }

    public void addProgressListener(ProgressListener progressListener) {
        if (isWindows) {
            try {
                addProgressListener.invoke(chromiumBrowser, progressListener);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.addProgressListener(progressListener);
        }
    }

    public void addLocationListener(LocationListener locationListener) {
        if (isWindows) {
            try {
                addLocationListener.invoke(chromiumBrowser, locationListener);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.addLocationListener(locationListener);
        }
    }

    public String getUrl() {
        if (isWindows) {
            try {
                return (String) getUrl.invoke(chromiumBrowser);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
                return e.getMessage();
            }
        } else {
            return defaultBrowser.getUrl();
        }
    }

    public void removeProgressListener(ProgressListener progressListener) {
        if (isWindows) {
            try {
                removeProgressListener.invoke(chromiumBrowser, progressListener);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.removeProgressListener(progressListener);
        }
    }

    public void close() {
        if (isWindows) {
            try {
                close.invoke(chromiumBrowser);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.close();
        }
    }

    public void execute(String script) {
        if (isWindows) {
            try {
                execute.invoke(chromiumBrowser, script);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("", e);
            }
        } else {
            defaultBrowser.execute(script);
        }
    }
}
