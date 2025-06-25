package com.gohopo.social_foundation_lc.push;

import android.content.Context;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import cn.leancloud.AVException;
import cn.leancloud.AVInstallation;
import cn.leancloud.AVLogger;
import cn.leancloud.callback.SaveCallback;
import cn.leancloud.convertor.ObserverBuilder;
import cn.leancloud.utils.LogUtil;
import cn.leancloud.utils.StringUtil;

public class VivoPushService extends OpenClientPushMessageReceiver {
    private static final AVLogger LOGGER = LogUtil.getLogger(VivoPushService.class);
    private final String VIVO_VENDOR = "vivo";

    public static final String MIXPUSH_PROFILE = "deviceProfile";
    public static String vivoDeviceProfile = "";

    /**
     * 通知被点击后的结果返回。
     * @param context context
     * @param msg  **UPSNotificationMessage 包含以下字段**
     *              msgId：通知id。
     *              title：通知标题。
     *              content：通知内容。
     *              skipContent：通知自定义内容。
     *              params：自定义键值对。
     * @deprecated 当push发出的通知被点击后便会触发onNotificationClicked通知应用
     *             该接口仅用来兼容老版本，在 3.x 中已经不再使用。
     */
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
    }

    /**
     * RegId 结果返回。当开发者首次调用 turnOnPush 成功或 regId 发生改变时会回调该方法。
     * @param var1 应用上下文
     * @param regId 当前设备的当前应用的唯一标识
     *
     */
    @Override
    public void onReceiveRegId(Context var1, final String regId) {
        if (StringUtil.isEmpty(regId)) {
            LOGGER.e("received empty regId from VIVO server.");
        } else {
            AVInstallation installation = AVInstallation.getCurrentInstallation();

            if (!VIVO_VENDOR.equals(installation.getString(AVInstallation.VENDOR))) {
                installation.put(AVInstallation.VENDOR, VIVO_VENDOR);
            }
            if (!regId.equals(installation.getString(AVInstallation.REGISTRATION_ID))) {
                installation.put(AVInstallation.REGISTRATION_ID, regId);
            }

            String localProfile = installation.getString(MIXPUSH_PROFILE);
            localProfile = (null != localProfile ? localProfile : "");
            if (!localProfile.equals(vivoDeviceProfile)) {
                installation.put(MIXPUSH_PROFILE, vivoDeviceProfile);
            }

            installation.saveInBackground().subscribe(ObserverBuilder.buildSingleObserver(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null != e) {
                        LOGGER.e("update installation(for vivo) error!", e);
                    } else {
                        LOGGER.d("vivo push registration successful! regId=" + regId);
                    }
                }
            }));
        }
    }
}
