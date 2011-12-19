package com.grlicky.demo.conversation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vladimir Grlicky
 */
public class ConversationContextManager {

    private final ConversationIdGenerator conversationIdGenerator;

    /** All conversation contexts identified by their IDs. */
    private final Map<String, ConversationContext> contexts = new HashMap<String, ConversationContext>();

    public ConversationContextManager(ConversationIdGenerator conversationIdGenerator) {
        this.conversationIdGenerator = conversationIdGenerator;
    }

    public Object getAttribute(String name, String conversationId) {
        final ConversationContext context = getConversationContextOrThrowException(conversationId);
        return context.getAttribute(name);
    }

    public void setAttribute(String name, Object value, String conversationId) {
        final ConversationContext context = getConversationContextOrThrowException(conversationId);
        context.setAttribute(name, value);
    }

    public String createConversation() {
        return createConversationInternally(conversationIdGenerator.generateConversationId());
    }

    public String createConversation(String conversationId) {
        if (contexts.keySet().contains(conversationId)) {
            throw new IllegalStateException(
                    "Conversation context with the conversationId = '" + conversationId + "' already exists"
            );
        }
        return createConversationInternally(conversationId);
    }

    private String createConversationInternally(String conversationId) {
        final ConversationContext context = new ConversationContext();
        contexts.put(conversationId, context);

        return conversationId;
    }

    public void destroyConversation(String conversationId) {
        contexts.remove(conversationId);
    }

    public Map<String, ConversationContext> getContexts() {
        return contexts;
    }

    private ConversationContext getConversationContextOrThrowException(String conversationId) {
        final ConversationContext context = contexts.get(conversationId);
        if (context == null) {
            throw new IllegalStateException(
                    "Conversation context with the conversationId = '" + conversationId + "' does not exist"
            );
        }
        return context;
    }

    /* ************************************************** */

    public static class TimeBasedConversationIdGenerator
        implements ConversationIdGenerator {

        public String generateConversationId() {
            return String.valueOf(System.currentTimeMillis());
        }
    }

    public static class IncrementalConversationIdGenerator
        implements ConversationIdGenerator {

        private int index = 1;

        public String generateConversationId() {
            return String.valueOf(index++);
        }
    }
}
