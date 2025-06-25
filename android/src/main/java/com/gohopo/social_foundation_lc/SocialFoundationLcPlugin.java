package com.gohopo.social_foundation_lc;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.gohopo.social_foundation_lc.push.PushManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** SocialFoundationLcPlugin */
public class SocialFoundationLcPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  public static Context context;
  public static Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "social_foundation_lc");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }
  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if(channel!=null) channel.setMethodCallHandler(null);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if(call.method.equals("getPlatformVersion")){
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    else if(call.method.equals("registerPush")){
      String oppoAppKey = call.argument("oppoAppKey");
      String oppoAppSecret = call.argument("oppoAppSecret");
      String miAppId = call.argument("miAppId");
      String miAppKey = call.argument("miAppKey");
      PushManager.registerPush(activity,oppoAppKey,oppoAppSecret,miAppId,miAppKey);
    }
    else{
      result.notImplemented();
    }
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }
  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }
  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

  }
  @Override
  public void onDetachedFromActivity() {

  }
}
