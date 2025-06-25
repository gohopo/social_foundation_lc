import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'social_foundation_lc_method_channel.dart';

abstract class SocialFoundationLcPlatform extends PlatformInterface {
  /// Constructs a SocialFoundationLcPlatform.
  SocialFoundationLcPlatform() : super(token: _token);

  static final Object _token = Object();

  static SocialFoundationLcPlatform _instance = MethodChannelSocialFoundationLc();

  /// The default instance of [SocialFoundationLcPlatform] to use.
  ///
  /// Defaults to [MethodChannelSocialFoundationLc].
  static SocialFoundationLcPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SocialFoundationLcPlatform] when
  /// they register themselves.
  static set instance(SocialFoundationLcPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<void> registerPush(String oppoAppKey,String oppoAppSecret,String miAppId,String miAppKey) {
    throw UnimplementedError('registerPush() has not been implemented.');
  }
}
