package com.katalon.plugin.jira.composer.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.katalon.plugin.jira.composer.util.ImageUtil;

public class ImageConstants {
    public static Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    public static final Image IMG_16_JIRA = ImageUtil.loadImage(currentBundle, "icons/jira_active_16.png");

    public static final Image IMG_ISSUE_HOVER_IN = ImageUtil.loadImage(currentBundle, "icons/bug_16.png");

    public static final Image IMG_ISSUE_HOVER_OUT = ImageUtil.loadImage(currentBundle,
            "icons/bug_disabled_16.png");

    public static final Image IMG_16_WARNING = ImageUtil.loadImage(currentBundle,
            "icons/warning_16.png");
    
    public static final Image IMG_16_HELP = ImageUtil.loadImage(currentBundle,
            "icons/help_16.png");

    public static final Image IMG_16_ADD = ImageUtil.loadImage(currentBundle,
            "icons/add_16.png");
    
    public static final Image IMG_16_EDIT = ImageUtil.loadImage(currentBundle,
            "icons/edit_16.png");

    public static final Image IMG_16_REMOVE = ImageUtil.loadImage(currentBundle,
            "icons/delete_16.png");
}
