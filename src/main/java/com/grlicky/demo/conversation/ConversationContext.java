package com.grlicky.demo.conversation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vladimir Grlicky
 */
public class ConversationContext {

    private ConversationContext parentContext;

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public ConversationContext() {
        this(null);
    }

    public ConversationContext(ConversationContext parentContext) {
        this.parentContext = parentContext;
    }

    public Object getAttribute(String name) {
        final Object localValue = attributes.get(name);
        return localValue == null && parentContext != null ? parentContext.getAttribute(name) : localValue;
    }

    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
        }
        else {
            attributes.put(name, value);
        }
    }

    @Override
    public String toString() {
        return "ConversationContext{" +
                "parentContext=" + parentContext +
                ", attributes=" + attributes +
                '}';
    }
}
