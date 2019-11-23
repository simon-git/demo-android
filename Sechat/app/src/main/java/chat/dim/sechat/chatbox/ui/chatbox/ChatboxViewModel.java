package chat.dim.sechat.chatbox.ui.chatbox;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import chat.dim.dkd.Content;
import chat.dim.dkd.InstantMessage;
import chat.dim.mkm.ID;
import chat.dim.mkm.User;
import chat.dim.model.Conversation;
import chat.dim.model.ConversationDatabase;
import chat.dim.model.Facebook;
import chat.dim.protocol.Command;
import chat.dim.sechat.Client;

enum MsgType {

    SENT    (1),
    RECEIVED(2),
    COMMAND (3);

    public final int value;

    MsgType(int value) {
        this.value = value;
    }
}

class ChatboxViewModel extends ViewModel {

    List<InstantMessage> getMessages(Conversation chatBox) {
        ConversationDatabase msgDB = ConversationDatabase.getInstance();
        return msgDB.messagesInConversation(chatBox);
    }

    boolean insertMessage(InstantMessage iMsg, Conversation chatBox) {
        ConversationDatabase msgDB = ConversationDatabase.getInstance();
        return msgDB.insertMessage(iMsg, chatBox);
    }

    static MsgType getType(InstantMessage iMsg, Conversation chatBox) {
        Content content = iMsg.content;
        if (content instanceof Command) {
            return MsgType.COMMAND;
        }

        Facebook facebook = Facebook.getInstance();
        ID sender = facebook.getID(iMsg.envelope.sender);
        if (sender.equals(chatBox.identifier)) {
            return MsgType.RECEIVED;
        }

        Client client = Client.getInstance();
        List<User> users = client.allUsers();
        for (User user : users) {
            if (user.identifier.equals(sender)) {
                return MsgType.SENT;
            }
        }
        return MsgType.RECEIVED;
    }
}
