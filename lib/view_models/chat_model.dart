import 'package:social_foundation/social_foundation.dart';
import 'package:social_foundation_lc/services/chat_manager.dart';

abstract class SfChatModelLc<TConversation extends SfConversation,TMessage extends SfMessage> extends SfChatModel<TConversation,TMessage>{
  SfChatModelLc(super.args);
  @override
  Future listenMessageEvent() async {
    disposeMessageEvent();
    if(conversation==null) return;
    await queryUnreadMessages();
    return super.listenMessageEvent();
  }
  @override
  void onClientResuming() async {
    await conversation?.queryUnreadMessagesCount();
    listenMessageEvent();
  }
  Future queryUnreadMessages() async {
    if(conversation!.unreadMessagesCount>0){
      List<TMessage> messages = await (SfLocatorManager.chatManager as SfChatManagerLc).queryMessages(conversation!.convId, conversation!.unreadMessagesCount) as List<TMessage>;
      messages = messages.map((message) => list.firstWhereOrNull((data)=>message.equalTo(data)) ?? message).toList();
      await onUnreadMessages(messages);
      messages = messages.where((message) => list.every((data) => !message.equalTo(data))).toList();
      list.insertAll(0,messages);
      list.sort((a,b) => b.timestamp-a.timestamp);
      notifyListeners();
      
      await SfMessage.saveAll(messages);
    }
  }
}