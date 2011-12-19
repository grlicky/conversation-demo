package com.grlicky.demo.conversation;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grlicky.demo.conversation.ConversationContextManager.IncrementalConversationIdGenerator;

/**
 * @author Vladimir Grlicky
 */
public class ConversationAwareHttpSession
    implements HttpSession {

    private static final Logger logger = LoggerFactory.getLogger(ConversationAwareHttpSession.class);

    private static final String CONVERSATION_CONTEXT_MANAGER_NAME = "ConversationContextManager";

    private final HttpSession session;
    private final ConversationStack conversationStack = new ConversationStack();

    public ConversationAwareHttpSession(HttpSession session) {
        this(session, null);
    }

    public ConversationAwareHttpSession(HttpSession session, String conversationId) {
        this.session = session;
        conversationStack.push(conversationId);
    }

    public boolean conversationExists(String conversationId) {
        return getConversationContextManager().getContexts().keySet().contains(conversationId);
    }

    public String getConversation() {
        return conversationStack.peek();
    }

    public String beginConversation() {
        final String conversationId = getConversationContextManager().createConversation();
        setConversation(conversationId);
        return conversationId;
    }

    public String beginConversation(String conversationId) {
        getConversationContextManager().createConversation(conversationId);
        setConversation(conversationId);
        return conversationId;
    }

    public void setConversation(String conversationId) {
        conversationStack.push(conversationId);

        logger.debug("setConversation: {}", conversationId);
        logger.debug("setConversation: stack: {}", conversationStack);
    }

    public void endConversation() {
        final String conversationId = conversationStack.pop();
        getConversationContextManager().destroyConversation(conversationId);
        logger.debug("endConversation: {}", conversationId);
        logger.debug("endConversation: stack: {}", conversationStack);
    }

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public String getId() {
        return session.getId();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }

    public Object getAttribute(String name) {
        final String conversationId = conversationStack.peek();
        final Object value = getConversationContextManager().getAttribute(name, conversationId);
        logger.debug("getAttribute({}): name: {}, value: {}", new Object[] { conversationId, name, value });
        return value;
    }

    public Object getValue(String name) {
        return session.getValue(name);
    }

    public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }

    public String[] getValueNames() {
        return session.getValueNames();
    }

    public void setAttribute(String name, Object value) {
        final String conversationId = conversationStack.peek();
        getConversationContextManager().setAttribute(name, value, conversationId);
        logger.debug("setAttribute({}): name: {}, value: {}", new Object[]{conversationId, name, value});
    }

    public void putValue(String name, Object value) {
        session.putValue(name, value);
    }

    public void removeAttribute(String name) {
        setAttribute(name, null);
    }

    public void removeValue(String name) {
        session.removeValue(name);
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    /* ************************************************** */

    public ConversationContextManager getConversationContextManager() {
        ConversationContextManager manager =
                (ConversationContextManager) session.getAttribute(CONVERSATION_CONTEXT_MANAGER_NAME);

        if (manager == null) {
            final ConversationIdGenerator conversationIdGenerator = new IncrementalConversationIdGenerator();
            manager = new ConversationContextManager(conversationIdGenerator);
            session.setAttribute(CONVERSATION_CONTEXT_MANAGER_NAME, manager);
        }
        return manager;
    }
}
