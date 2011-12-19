package com.grlicky.demo.conversation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vladimir Grlicky
 */
public class ConversationAwareFilter
    implements Filter {

    private static final String COMMAND_PARAMETER_NAME = "c";

    private static final Logger logger = LoggerFactory.getLogger(ConversationAwareFilter.class);

    public void init(FilterConfig filterConfig)
        throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        final HttpSession httpSession = httpRequest.getSession(true);

        // conversation-aware instances
        final ConversationAwareHttpSession session = new ConversationAwareHttpSession(httpSession);
        final ConversationAwareHttpServletRequest request = new ConversationAwareHttpServletRequest(httpRequest, session);

        if (processCommands(request, httpResponse, session)) {
            // end request processing...
            System.out.println();  // FIXME just for better log reading
            return;
        }

        final String conversationId = request.getConversationId();
        logger.debug("doFilter: conversationId = {}", conversationId);
        if (conversationId != null) {
            logger.debug("doFilter: run inside long-running conversation ({})...", conversationId);
            if (session.conversationExists(conversationId)) {
                session.setConversation(conversationId);
            }
            else {
                session.beginConversation(conversationId);
            }
            chain.doFilter(request, httpResponse);
        }
        else {
            final String transientConversationId = session.beginConversation();
            logger.debug("doFilter: begin transient conversation ({})...", transientConversationId);

            chain.doFilter(request, httpResponse);

            session.endConversation();
            logger.debug("doFilter: ...end transient conversation ({})", transientConversationId);
        }

        System.out.println();  // FIXME just for better log reading
    }

    public void destroy() {
    }

    private boolean processCommands(ConversationAwareHttpServletRequest request, HttpServletResponse response,
                                    ConversationAwareHttpSession session) throws IOException {
        final PrintWriter writer = response.getWriter();

        final String command = request.getParameter(COMMAND_PARAMETER_NAME);

        // starts a new long-running conversation
        if ("begin".equalsIgnoreCase(command)) {
            final String conversationId = session.beginConversation();
            logger.debug("doFilter: begin long-running conversation ({})...", conversationId);

            writer.println("A long-running conversation (" + conversationId + ") started");
            writer.println();
            printContextManagerContent(session, response);

            return true;
        }
        // end a new long-running conversation (identified by the 'cid' request parameter or 'conversationId' cookie)
        else if ("end".equalsIgnoreCase(command)) {
            final String conversationId = request.getConversationId();
            if (conversationId != null) {
                session.setConversation(conversationId);
                session.endConversation();
                logger.debug("doFilter: ...end long-running conversation");

                writer.println("A long-running conversation (" + conversationId + ") ended");
                writer.println();
                printContextManagerContent(session, response);
            }
            else {
                // no conversation to end
            }
            return true;
        }
        // prints the conversation context manager content
        else if ("status".equalsIgnoreCase(command)) {
            printContextManagerContent(session, response);
            return true;
        }
        return false;
    }

    /* ************************************************** */

    private void printContextManagerContent(ConversationAwareHttpSession session, HttpServletResponse response)
        throws IOException {
        final PrintWriter writer = response.getWriter();

        final ConversationContextManager manager = session.getConversationContextManager();
        final Map<String, ConversationContext> contexts = manager.getContexts();

        writer.println("ConversationContextManager for this HTTP session:");
        writer.println("  - number of conversation contexts:   " + contexts.size());
        writer.println();
        for (Map.Entry<String, ConversationContext> entry : contexts.entrySet()) {
            final String conversationId = entry.getKey();
            final ConversationContext conversationContext = entry.getValue();

            writer.println(
                    "[" + conversationId + "] => " + conversationContext
            );
        }
    }
}
