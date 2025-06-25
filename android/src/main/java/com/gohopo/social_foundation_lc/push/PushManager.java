package com.gohopo.social_foundation_lc.push;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.heytap.msp.push.HeytapPushManager;
import com.hihonor.push.sdk.HonorPushClient;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.push.HmsMessaging;
import com.vivo.push.PushConfig;

import cn.leancloud.AVLogger;
import cn.leancloud.AVManifestUtils;
import cn.leancloud.utils.LogUtil;
import cn.leancloud.utils.StringUtil;

public class PushManager {
    static final AVLogger LOGGER = LogUtil.getLogger(PushManager.class);

    public static void registerPush(Activity activity,String oppoAppKey,String oppoAppSecret,String miAppId,String miAppKey){
        Application application = activity.getApplication();

        if(HeytapPushManager.isSupportPush(application)){
            registerOppoPush(application,oppoAppKey,oppoAppSecret);
        }
        else if(isSupportVIVOPush(application)){
            registerVivoPush(application);
        }
        else if(isXiaomiPhone()){
            registerXiaomiPush(application,miAppId,miAppKey);
        }
        else if(HonorPushClient.getInstance().checkSupportHonorPush(application)){
            registerHonorPush(application);
        }
        else if(isHuaweiPhone()){
            registerHuaweiPush(activity);
        }
    }
    private static void printErrorLog(String error) {
        if (StringUtil.isEmpty(error)) return;
        LOGGER.e(error);
    }
    //Oppo
    private static void registerOppoPush(Context application,String oppoAppKey,String oppoAppSecret){
        if (StringUtil.isEmpty(oppoAppKey) || StringUtil.isEmpty(oppoAppSecret)) {
            printErrorLog("invalid parameter. appKey=" + oppoAppKey);
            return;
        }
        HeytapPushManager.init(application, true);
        HeytapPushManager.register(application, oppoAppKey, oppoAppSecret, new OppoPushService());
    }
    //Vivo
    private static boolean isSupportVIVOPush(Context application) {
        com.vivo.push.PushClient client = com.vivo.push.PushClient.getInstance(application);
        if (null == client) {
            return false;
        }
        return client.isSupport();
    }
    private static void registerVivoPush(Context application){
        com.vivo.push.PushClient client = com.vivo.push.PushClient.getInstance(application.getApplicationContext());
        try {
            client.checkManifest();
            if (!isSupportVIVOPush(application)) {
                printErrorLog("current device doesn't support VIVO Push.");
                return;
            }
            PushConfig config = new PushConfig.Builder()
                    .agreePrivacyStatement(true)
                    .build();
            client.initialize(config);
        } catch (com.vivo.push.util.VivoPushException ex) {
            printErrorLog("register error, mainifest is incomplete! details=" + ex.getMessage());
        }
    }
    //Xiaomi
    private static boolean isXiaomiPhone() {
        final String phoneManufacturer = Build.MANUFACTURER;
        return !StringUtil.isEmpty(phoneManufacturer)
                && phoneManufacturer.toLowerCase().contains("xiaomi");
    }
    private static boolean checkXiaomiManifest(Context application) {
        try {
            return AVManifestUtils.checkReceiver(application, MiPushService.class);
        } catch (Exception e) {
            LOGGER.d(e.getMessage());
        }
        return false;
    }
    private static void registerXiaomiPush(Context application,String miAppId,String miAppKey){
        if (StringUtil.isEmpty(miAppId)) {
            printErrorLog("miAppId cannot be null.");
            return;
        }
        if (StringUtil.isEmpty(miAppKey)) {
            printErrorLog("miAppKey cannot be null.");
            return;
        }
        if (!checkXiaomiManifest(application)) {
            printErrorLog("register error, mainifest is incomplete(receiver not found: "
                    + MiPushService.class.getSimpleName() + ")!");
            return;
        }

        com.xiaomi.mipush.sdk.MiPushClient.registerPush(application, miAppId, miAppKey);

        LOGGER.d("finished to register mi push");
    }
    //Honor
    private static void registerHonorPush(Context application){
        HonorPushClient.getInstance().init(application, true);
    }
    //Huawei
    private static boolean isHuaweiPhone() {
        final String phoneBrand = Build.BRAND;
        try {
            return (phoneBrand.equalsIgnoreCase("huawei") || phoneBrand.equalsIgnoreCase("honor"));
        } catch (Exception e) {
            return false;
        }
    }
    private static boolean checkHuaweiManifest(Context application) {
        boolean result = false;
        try {
            result = AVManifestUtils.checkPermission(application, android.Manifest.permission.INTERNET)
                    && AVManifestUtils.checkService(application, HuaweiPushService.class);
        } catch (Exception e) {
            LOGGER.d(e.getMessage());
        }
        return result;
    }
    private static void registerHuaweiPush(Activity activity){
        if (!checkHuaweiManifest(activity.getApplication())) {
            printErrorLog("[HMS] register error, mainifest is incomplete!");
            return;
        }
        LOGGER.d("[HMS] start register HMS push");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String appId = new AGConnectOptionsBuilder().build(activity).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(activity).getToken(appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
                    LOGGER.d("found HMS appId: " + appId + ", token: " + token);
                    HuaweiPushService.updateAVInstallation(token);
                } catch (Exception ex) {
                    LOGGER.w("failed to get hms token. cause: " + ex.getMessage());
                }
            }
        }).start();
    }
}
