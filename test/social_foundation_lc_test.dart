import 'package:flutter_test/flutter_test.dart';
import 'package:social_foundation_lc/social_foundation_lc.dart';
import 'package:social_foundation_lc/social_foundation_lc_platform_interface.dart';
import 'package:social_foundation_lc/social_foundation_lc_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSocialFoundationLcPlatform
    with MockPlatformInterfaceMixin
    implements SocialFoundationLcPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SocialFoundationLcPlatform initialPlatform = SocialFoundationLcPlatform.instance;

  test('$MethodChannelSocialFoundationLc is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSocialFoundationLc>());
  });

  test('getPlatformVersion', () async {
    SocialFoundationLc socialFoundationLcPlugin = SocialFoundationLc();
    MockSocialFoundationLcPlatform fakePlatform = MockSocialFoundationLcPlatform();
    SocialFoundationLcPlatform.instance = fakePlatform;

    expect(await socialFoundationLcPlugin.getPlatformVersion(), '42');
  });
}
