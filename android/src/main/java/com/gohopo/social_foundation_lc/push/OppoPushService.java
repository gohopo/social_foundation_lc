package com.gohopo.social_foundation_lc.push;

import com.heytap.msp.push.callback.ICallBackResultService;

import cn.leancloud.AVException;
import cn.leancloud.AVInstallation;
import cn.leancloud.AVLogger;
import cn.leancloud.callback.SaveCallback;
import cn.leancloud.convertor.ObserverBuilder;
import cn.leancloud.utils.LogUtil;
import cn.leancloud.utils.StringUtil;

public class OppoPushService implements ICallBackResultService {
    private static final AVLogger LOGGER = LogUtil.getLogger(OppoPushService.class);
    private static final String VENDOR_OPPO = "oppo";

    public static final String MIXPUSH_PROFILE = "deviceProfile";
    public static String oppoDeviceProfile = "";

    private void updateAVInstallation(String registerId) {
        if (!StringUtil.isEmpty(registerId)) {
            AVInstallation installation = AVInstallation.getCurrentInstallation();

            if (!VENDOR_OPPO.equals(installation.getString(AVInstallation.VENDOR))) {
                installation.put(AVInstallation.VENDOR, VENDOR_OPPO);
            }
            if (!registerId.equals(installation.getString(AVInstallation.REGISTRATION_ID))) {
                installation.put(AVInstallation.REGISTRATION_ID, registerId);
            }
            String localProfile = installation.getString(MIXPUSH_PROFILE);
            localProfile = (null != localProfile ? localProfile : "");
            if (!localProfile.equals(oppoDeviceProfile)) {
                installation.put(MIXPUSH_PROFILE, oppoDeviceProfile);
            }
            installation.saveInBackground().subscribe(ObserverBuilder.buildSingleObserver(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null != e) {
                        LOGGER.e("update installation error!", e);
                    } else {
                        LOGGER.d("oppo push registration successful!");
                    }
                }
            }));
        }
    }

    public void onRegister(int responseCode, String registerID, String packageName, String miniProgramPkg) {
        if (responseCode != 0) {
            LOGGER.e("failed to register device. errorCode: " + responseCode);
            return;
        }
        if (StringUtil.isEmpty(registerID)) {
            LOGGER.e("oppo register id is empty.");
            return;
        }
        updateAVInstallation(registerID);
    }

    public void onUnRegister(int responseCode, String packageName, String miniProgramPkg) {
        if (responseCode != 0) {
            LOGGER.e("failed to unregister device. errorCode: " + responseCode);
        } else {
            LOGGER.i("succeeded to unregister device.");
        }
    }

    public void onSetPushTime(int responseCode, String var2) {
        if (responseCode != 0) {
            LOGGER.e("failed to setPushTime. errorCode: " + responseCode);
        } else {
            LOGGER.i("succeeded to setPushTime.");
        }
    }

    public void onGetPushStatus(int responseCode, int status) {
        if (responseCode != 0) {
            LOGGER.e("failed to getPushStatus. errorCode: " + responseCode);
        } else {
            LOGGER.i("succeeded to getPushStatus.");
        }
    }

    public void onGetNotificationStatus(int responseCode, int status) {
        if (responseCode != 0) {
            LOGGER.e("failed to getNotificationStatus. errorCode: " + responseCode);
        } else {
            LOGGER.i("succeeded to getNotificationStatus.");
        }
    }

    public void onError(int errorCode, String message, String packageName, String miniProgramPkg) {
        LOGGER.w("error occurred. code:" + errorCode + ", message:" + message
                + ", package:" + packageName + ", miniPkg:" + miniProgramPkg);
    }
}
