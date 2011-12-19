package com.grlicky.demo.conversation;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vladimir Grlicky
 */
public class ConversationAwareHttpServletRequest
    extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ConversationAwareHttpServletRequest.class);

    private static final String CONVERSATION_ID_PARAMETER_NAME = "cid";
    private static final String CONVERSATION_ID_COOKIE_NAME = "conversationId";

    private final ConversationAwareHttpSession session;

    public ConversationAwareHttpServletRequest(HttpServletRequest request, ConversationAwareHttpSession session) {
        super(request);
        this.session = session;

        // print cookies known by this request
        logger.debug("request cookies:");
        for (Cookie cookie : getCookies()) {
            logger.debug("  - cookie: name = {}, value = {}", cookie.getName(), cookie.getValue());
        }
    }

    public String getConversationId() {
        final String conversationIdFromCookie = getConversationIdFromCookies();
        final String conversationIdFromParameter = getConversationIdFromRequestParameters();

        logger.debug(
                "getConversationId: available conversationIds: fromParameter = {}, fromCookie = {}",
                conversationIdFromParameter, conversationIdFromCookie
        );
        return conversationIdFromParameter != null ?  conversationIdFromParameter : conversationIdFromCookie;
    }

    public String getConversationIdFromRequestParameters() {
        return getParameter(CONVERSATION_ID_PARAMETER_NAME);
    }

    public String getConversationIdFromCookies() {
        for (Cookie cookie : getCookies()) {
            if (cookie.getName().equals(CONVERSATION_ID_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return session;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }
}
