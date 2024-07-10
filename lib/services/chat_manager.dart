import 'dart:convert';
import 'dart:math';

import 'package:leancloud_official_plugin/leancloud_plugin.dart';
import 'package:social_foundation/social_foundation.dart' hide Client;

abstract class SfChatManagerLc<TConversation extends SfConversation,TMessage extends SfMessage> extends SfChatManager<TConversation,TMessage>{
  late Client _client;
  @override
  Future close() => _client.close();
  @override
  Future<TConversation> convJoin(String conversationId) async {
    var conversation = await protectedGetConversation(conversationId);
    await conversation.join();
    return protectedConvertConversation(conversation);
  }
  @override
  Future convQuit(String conversationId) async {
    var conversation = await protectedGetConversation(conversationId);
    await conversation.quit();
  }
  @override
  Future convRead(String conversationId) async {
    var conversation = await protectedGetConversation(conversationId);
    return conversation.read();
  }
  @override
  Future convRecall(String messageID,{String? conversationId,int? timestamp}) async {
    var conversation = await protectedGetConversation(conversationId!);
    return conversation.recallMessage(messageID:messageID,messageTimestamp:timestamp);
  }
  @override
  Future<TConversation> getConversation(String conversationId) async {
    var conversation = await protectedGetConversation(conversationId);
    return protectedConvertConversation(conversation);
  }
  @override
  Future login(String userId,{String? token}){
    _client = Client(id:userId);
    _client.onDisconnected = ({required client,exception}) => onClientDisconnected();
    _client.onResuming = ({required client}) => onClientResuming();
    _client.onMessage = ({required client,required conversation,required message}) => protectedOnMessageReceived(protectedConvertConversation(conversation),protectedConvertMessage(message)!);
    _client.onUnreadMessageCountUpdated = ({required client,required conversation}) => onUnreadMessagesCountUpdated(protectedConvertConversation(conversation));
    _client.onMessageRecalled = ({required client,required conversation,required recalledMessage}) => onMessageRecalled(protectedConvertMessage(recalledMessage)!);
    return _client.open();
  }
  void onUnreadMessagesCountUpdated(TConversation conversation) async {
    if(conversation.unreadMessagesCount<=0) return;
    saveConversation(conversation,unreadMessageCountUpdated:true);
    convRead(conversation.convId);
    SfUnreadMessagesCountUpdatedEvent<TConversation>(conversation:conversation).emit();
  }
  TConversation protectedConvertConversation(Conversation conversation){
    var map = {};
    map['ownerId'] = SfLocatorManager.userState.curUserId;
    map['convId'] = conversation.id;
    map['name'] = conversation.name;
    map['creator'] = conversation.creator;
    map['members'] = conversation.members;
    map['unreadMessagesCount'] = conversation.unreadMessageCount;
    map['lastMessage'] = protectedConvertMessage(conversation.lastMessage);
    map['lastMessageAt'] = conversation.lastMessage?.sentTimestamp;
    return conversationFactory(map);
  }
  TMessage? protectedConvertMessage(Message? message){
    if(message==null) return null;
    var map = {};
    map['ownerId'] = SfLocatorManager.userState.curUserId;
    map['msgId'] = message.id;
    map['convId'] = message.conversationID;
    map['fromId'] = message.fromClientID;
    map['timestamp'] = message.sentTimestamp;
    map['status'] = message.status.index;
    map['receiptTimestamp'] = message.deliveredTimestamp;
    if(message is TextMessage){
      map.addAll(jsonDecode(message.text!));
    }
    else if(message is RecalledMessage){
      map['msgType'] = SfMessageType.recall;
    }
    return messageFactory(map);
  }
  Future<Conversation> protectedGetConversation(String conversationId) async {
    var conversation = _client.conversationMap[conversationId];
    if(conversation == null){
      var query = _client.conversationQuery();
      query.whereString = jsonEncode({
        'objectId': conversationId,
      });
      query.limit = 1;
      var result = await query.find();
      if(result.isEmpty) throw '未查询到会话!';
      conversation = result[0];
    }
    return conversation;
  }
  void protectedOnMessageReceived(TConversation conversation,TMessage message){
    if(!message.transient) saveConversation(conversation,fromReceived:true);
    onMessageReceived(message);
  }
  Future<Message> protectedSend(Conversation conversation,Message message,bool transient) => conversation.send(message:message,transient:transient);
  @override
  Future<TMessage> protectedSendMessage(String conversationId,String? msg,String msgType,Map msgExtra) async {
    var conversation = await protectedGetConversation(conversationId);
    var message = json.encode({
      'msg': msg,
      'msgType': msgType,
      'msgExtra': msgExtra
    });
    var result = await protectedSend(conversation,TextMessage.from(text:message),msgExtra['transient']??false);
    return protectedConvertMessage(result)!;
  }
  Future<List<TMessage>> queryMessages(String conversationId,int limit) async {
    var conversation = await protectedGetConversation(conversationId);
    List<TMessage> messages = [];
    TMessage? startMessage;
    while(limit > 0){
      var count = min(limit,100);
      var result = await conversation.queryMessage(startMessageID:startMessage?.msgId,startTimestamp:startMessage?.timestamp,startClosed:startMessage!=null?false:null,limit:count);
      messages.addAll(result.map((data) => protectedConvertMessage(data)!));
      startMessage = messages.lastOrNull;
      limit -= count;
    }
    return messages;
  }
  @override
  Future reconnect() => _client.open(reconnect:true);
  @override
  void saveConversation(TConversation conversation,{bool? fromReceived,bool? unreadMessageCountUpdated}){
    var index = SfLocatorManager.chatState.list.indexWhere((data)=>data.convId==conversation.convId);
    if(index != -1){
      var conv = SfLocatorManager.chatState.list[index] as TConversation;
      if(fromReceived == true){
        conversation.unreadMessagesCount = conv.unreadMessagesCount;
      }
      else if(unreadMessageCountUpdated == true){
        conversation.unreadMessagesCount += conv.unreadMessagesCount;
      }
      conversation = conv..copyWith(conversation);
    }
    super.saveConversation(conversation,fromReceived:fromReceived,unreadMessageCountUpdated:unreadMessageCountUpdated);
  }
}