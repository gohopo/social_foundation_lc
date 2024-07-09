import 'social_foundation_lc_platform_interface.dart';

export 'package:leancloud_official_plugin/leancloud_plugin.dart' hide Client,Conversation,Message;
export 'package:social_foundation/social_foundation.dart';
//viewmodels
export './view_models/chat_model.dart';
//services
export './services/chat_manager.dart';

class SocialFoundationLc {
  Future<String?> getPlatformVersion() {
    return SocialFoundationLcPlatform.instance.getPlatformVersion();
  }
}
