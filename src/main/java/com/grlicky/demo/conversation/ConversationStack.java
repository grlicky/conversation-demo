package com.grlicky.demo.conversation;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Vladimir Grlicky
 */
public class ConversationStack {

    private final Deque<String> conversations = new LinkedList<String>();

    public String push(String conversationId) {
        if (conversationId != null) {
            conversations.addFirst(conversationId);
        }
        return conversationId;
    }

    public String pop() {
        return !isEmpty() ? conversations.removeFirst() : null;
    }

    public String peek() {
        return !isEmpty() ? conversations.peekFirst() : null;
    }

    public boolean isEmpty() {
        return conversations.isEmpty();
    }

    @Override
    public String toString() {
        return "ConversationStack{" +
                "conversations=" + conversations +
                '}';
    }
}
