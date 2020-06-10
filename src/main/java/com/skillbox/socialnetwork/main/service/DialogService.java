package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.dialog.DialogAddRequest;
import com.skillbox.socialnetwork.main.dto.dialog.LongpollHistoryRequest;
import com.skillbox.socialnetwork.main.dto.dialog.response.LinkDto;
import com.skillbox.socialnetwork.main.dto.dialog.response.MessageTextDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.model.Person;

public interface DialogService {
    BaseResponseList getAllMessages(String query, int offset, int limit, Person person);
    BaseResponse addDialog(DialogAddRequest request);
    BaseResponse getUnreadMessages(Person person);
    BaseResponse deleteDialog(int dialogId);
    BaseResponse addUsersToDialog(DialogAddRequest request);
    BaseResponse deleteUsersFromDialog(DialogAddRequest request);
    BaseResponse inviteUserToDialog(int id);
    BaseResponse joinDialog(int id, LinkDto linkDto);
    BaseResponseList getMessagesFromDialog(int id, String query, int offset, int limit);
    BaseResponse addMessage(int dialogId, MessageTextDto message);
    BaseResponse deleteMessage(int dialogId, int messageId);
    BaseResponse editMessage(int dialogId, int messageId, MessageTextDto messageText);
    BaseResponse recoverMessage(int dialogId, int messageId);
    BaseResponse markMessageAsRead(int dialogId, int messageId);
    BaseResponse getUserActivityStatus(int dialogId, int userId);
    BaseResponse changeUserActivityStatus(int dialogId, int userId);
    BaseResponse getLongPollCredentials();
    BaseResponse getLongPollHistory(LongpollHistoryRequest request);

}
