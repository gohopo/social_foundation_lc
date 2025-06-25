package com.gohopo.social_foundation_lc.push;

import com.hihonor.push.sdk.HonorMessageService;
import com.hihonor.push.sdk.HonorPushDataMsg;

import cn.leancloud.AVException;
import cn.leancloud.AVInstallation;
import cn.leancloud.AVLogger;
import cn.leancloud.callback.SaveCallback;
import cn.leancloud.convertor.ObserverBuilder;
import cn.leancloud.push.AndroidNotificationManager;
import cn.leancloud.utils.LogUtil;
import cn.leancloud.utils.StringUtil;

public class HonorPushService extends HonorMessageService {
    static final AVLogger LOGGER = LogUtil.getLogger(HonorPushService.class);

    public static final String MIXPUSH_PROFILE = "deviceProfile";
    static final String PUSH_PROFILE = "deviceProfile";
    static final String VENDOR = "honor";

    public static String deviceProfile = "";

    @Override
    public void onNewToken(String token) {
        updateAVInstallation(token);
    }

    @Override
    public void onMessageReceived(HonorPushDataMsg msg) {
        try {
            AndroidNotificationManager androidNotificationManager = AndroidNotificationManager.getInstance();
            String message = msg.getData();
            if (!StringUtil.isEmpty(message)) {
                LOGGER.d("received passthrough(data) message: " + message);
                androidNotificationManager.processMixPushMessage(message);
            } else {
                LOGGER.e("unknown passthrough message: " + msg.toString());
            }
        } catch (Exception ex) {
            LOGGER.e("failed to process PushMessage.", ex);
        }
    }

    public static void updateAVInstallation(String hwToken) {
        if (StringUtil.isEmpty(hwToken)) {
            return;
        }
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        if (!VENDOR.equals(installation.getString(AVInstallation.VENDOR))) {
            installation.put(AVInstallation.VENDOR, VENDOR);
        }
        if (!hwToken.equals(installation.getString(AVInstallation.REGISTRATION_ID))) {
            installation.put(AVInstallation.REGISTRATION_ID, hwToken);
        }
        String localProfile = installation.getString(PUSH_PROFILE);
        if (null == localProfile) {
            localProfile = "";
        }
        if (!localProfile.equals(deviceProfile)) {
            installation.put(MIXPUSH_PROFILE, deviceProfile);
        }

        installation.saveInBackground().subscribe(ObserverBuilder.buildSingleObserver(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (null != e) {
                    LOGGER.e("update installation error!", e);
                } else {
                    LOGGER.d("Honor push registration successful!");
                }
            }
        }));
    }
}
