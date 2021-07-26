package com.katalon.plugin.jira.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class ClassLoaderUtil {

    private static Object newInstance(String className, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                try {
                    spiClass = Class.forName(className, false, classLoader);
                } catch (ClassNotFoundException ex) {
                    spiClass = Class.forName(className);
                }
            }
            return spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw x;
        } catch (Exception x) {
            throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    public static Object find(String factoryId, String fallbackClassName) throws ClassNotFoundException {
        ClassLoader classLoader = getContextClassLoader();

        String serviceId = "META-INF/services/" + factoryId;
        // try to find services in CLASSPATH
        try {
            InputStream is;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is = classLoader.getResourceAsStream(serviceId);
            }

            if (is != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String factoryClassName = rd.readLine();
                rd.close();

                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return newInstance(factoryClassName, classLoader);
                }
            }
        } catch (Exception ex) {}

        // try to read from $java.home/lib/jaxrs.properties
        try {
            String javah = System.getProperty("java.home");
            String configFile = javah + File.separator + "lib" + File.separator + "jaxrs.properties";
            File f = new File(configFile);
            if (f.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(f));
                String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryClassName, classLoader);
            }
        } catch (Exception ex) {}

        // Use the system property
        try {
            String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, classLoader);
            }
        } catch (SecurityException se) {}

        if (fallbackClassName == null) {
            throw new ClassNotFoundException("Provider for " + factoryId + " cannot be found", null);
        }

        return newInstance(fallbackClassName, classLoader);
    }

    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {}
                return cl;
            }
        });
    }
}
