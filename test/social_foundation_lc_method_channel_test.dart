import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:social_foundation_lc/social_foundation_lc_method_channel.dart';

void main() {
  MethodChannelSocialFoundationLc platform = MethodChannelSocialFoundationLc();
  const MethodChannel channel = MethodChannel('social_foundation_lc');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
