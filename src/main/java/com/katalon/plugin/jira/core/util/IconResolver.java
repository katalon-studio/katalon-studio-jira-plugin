package com.katalon.plugin.jira.core.util;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * Picks which icon path a plugin should ship, based on the running Katalon Studio version.
 * Falls back to the legacy icon whenever the new one isn't bundled yet, so a build made before
 * the new asset ships can never end up pointing at a missing resource.
 */
public final class IconResolver {

    private static final String MIN_VERSION_FOR_NEW_ICON = "11.5.0";

    private static final String VERSION_SYSTEM_PROPERTY = "application.version";

    private IconResolver() {
    }

    /**
     * @param legacyIconPath path relative to the bundle root, e.g. {@code "icons/jira_active_32x24.png"}
     * @param newIconPath path relative to the bundle root, e.g. {@code "icons-v2/jira_v2.png"}
     */
    public static String resolve(Bundle bundle, String legacyIconPath, String newIconPath) {
        if (isRunningVersionAtLeast(MIN_VERSION_FOR_NEW_ICON) && iconExists(bundle, newIconPath)) {
            return newIconPath;
        }
        return legacyIconPath;
    }

    private static boolean iconExists(Bundle bundle, String iconPath) {
        return FileLocator.find(bundle, new Path(iconPath), null) != null;
    }

    private static boolean isRunningVersionAtLeast(String minVersion) {
        String runningVersion = System.getProperty(VERSION_SYSTEM_PROPERTY);
        if (runningVersion == null) {
            return false;
        }
        int[] running = parse(runningVersion);
        int[] min = parse(minVersion);
        for (int i = 0; i < running.length; i++) {
            if (running[i] != min[i]) {
                return running[i] > min[i];
            }
        }
        return true;
    }

    private static int[] parse(String version) {
        String[] parts = version.split("[.\\-]");
        int[] result = new int[3];
        for (int i = 0; i < result.length && i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }
}
