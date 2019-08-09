/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.client;

import chat.dim.dkd.InstantMessage;
import chat.dim.mkm.entity.Entity;
import chat.dim.mkm.entity.ID;

public class Amanuensis {
    private static final Amanuensis ourInstance = new Amanuensis();
    public static Amanuensis getInstance() { return ourInstance; }
    private Amanuensis() {
    }

    public ConversationDataSource conversationDataSource = null;

    // conversation factory
    public Conversation getConversation(ID identifier) {
        // create directly if we can find the entity
        Facebook facebook = Facebook.getInstance();
        Entity entity = null;
        if (identifier.getType().isCommunicator()) {
            entity = facebook.getAccount(identifier);
        } else if (identifier.getType().isGroup()) {
            entity = facebook.getGroup(identifier);
        }
        if (entity == null) {
            throw new NullPointerException("failed to create conversation:" + identifier);
        }
        return new Conversation(entity);
    }

    public Conversation getConversation(InstantMessage iMsg) {
        ID receiver = ID.getInstance(iMsg.envelope.receiver);
        if (receiver.getType().isGroup()) {
            // group chat, get chat box with group ID
            return getConversation(receiver);
        }
        ID group = ID.getInstance(iMsg.getGroup());
        if (group != null) {
            // group chat, get chat box with group ID
            return getConversation(group);
        }
        // personal chat, get chat box with contact ID
        ID sender = ID.getInstance(iMsg.envelope.sender);
        return getConversation(sender);
    }

    // interfaces for ConversationDataSource

    public int numberOfConversations() {
        return conversationDataSource.numberOfConversations();
    }

    public Conversation conversationAtIndex(int index) {
        return conversationDataSource.conversationAtIndex(index);
    }

    public ID removeConversationAtIndex(int index) {
        return conversationDataSource.removeConversationAtIndex(index);
    }

    public boolean removeConversation(Conversation chatBox) {
        return conversationDataSource.removeConversation(chatBox);
    }
}