package com.katalon.plugin.jira.core.util;

import com.katalon.platform.api.controller.Controller;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.UIService;

public class PlatformUtil {
    private PlatformUtil() {
        //Disable default constructor
    }
    
    public static ProjectEntity getCurrentProject() {
        return ApplicationManager.getInstance().getProjectManager().getCurrentProject();
    }

    public static <C extends Controller> C getPlatformController(Class<C> clazz) {
        return ApplicationManager.getInstance().getControllerManager().getController(clazz);
    }

    public static <U extends UIService> U getUIService(Class<U> clazz) {
        return ApplicationManager.getInstance().getUIServiceManager().getService(clazz);
    }

    public static TestCaseEntity getTestCase(String testCaseId) throws ResourceException {
        return getPlatformController(TestCaseController.class).getTestCase(getCurrentProject(), testCaseId);
    }
}
