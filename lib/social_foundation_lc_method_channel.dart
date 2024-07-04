import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'social_foundation_lc_platform_interface.dart';

/// An implementation of [SocialFoundationLcPlatform] that uses method channels.
class MethodChannelSocialFoundationLc extends SocialFoundationLcPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('social_foundation_lc');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
