import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'social_foundation_lc_platform_interface.dart';

class MethodChannelSocialFoundationLc extends SocialFoundationLcPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('social_foundation_lc');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
  @override
  Future<void> registerPush(String oppoAppKey,String oppoAppSecret,String miAppId,String miAppKey){
    return methodChannel.invokeMethod('registerPush',{
      "oppoAppKey":oppoAppKey,"oppoAppSecret":oppoAppSecret,
      "miAppId":miAppId,"miAppKey":miAppKey
    });
  }
}
