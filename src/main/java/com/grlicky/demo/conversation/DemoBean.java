package com.grlicky.demo.conversation;

import java.io.Serializable;

/**
 * @author Vladimir Grlicky
 */
public class DemoBean
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text;
    private int counter;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        this.counter++;
    }

    @Override
    public String toString() {
        return "DemoBean{" +
                "text='" + text + '\'' +
                ", counter=" + counter +
                '}';
    }
}
