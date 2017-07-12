package com.template.lib.allurehelper;

import com.template.lib.support.ScreenShooter;
import ru.yandex.qatools.allure.cucumberjvm.callback.OnFailureCallback;

/**
 * Created by nestor on 11.07.2017.
 */
public class OnFailureScheduler implements OnFailureCallback {

    @Override
    public Object call() {
        ParamsHelper.addAttachment(ScreenShooter.take(), "Screenshot", Type.PNG);
        return null;
    }
}
