import 'dart:convert';

import 'package:social_foundation/social_foundation.dart';

abstract class SfConversationLc<TMessage extends SfMessage> extends SfConversation<TMessage>{
  Map _attributes;
  SfConversationLc(Map data):_attributes=data['attributes']??{},super(data);
  Map get attributes => _attributes;
  @override
  Map<String,dynamic> toMap(){
    var map = super.toMap();
    map['attributes'] = jsonEncode(attributes);
    return map;
  }
}