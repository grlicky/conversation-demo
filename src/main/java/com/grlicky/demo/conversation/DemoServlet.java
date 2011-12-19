package com.grlicky.demo.conversation;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DemoServlet
    extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        System.out.println("***** DemoServlet: doGet: begin");
        final HttpSession session = request.getSession(true);

        final String v1 = request.getParameter("v1");
        if (v1 != null) {
            session.setAttribute("v1", v1);

            DemoBean demoBean = (DemoBean) session.getAttribute("demoBean");
            if (demoBean == null) {
                demoBean = new DemoBean();
                session.setAttribute("demoBean", demoBean);
            }
            demoBean.setText(v1);
            demoBean.incrementCounter();
        }

        final PrintWriter writer = response.getWriter();
        printBeforeContent(writer);
        printContent(session, writer);
        printAfterContent(writer);

        System.out.println("***** DemoServlet: doGet: end");
    }

    private void printContent(HttpSession session, PrintWriter writer) {
        final String v1 = (String) session.getAttribute("v1");
        final DemoBean demoBean = (DemoBean) session.getAttribute("demoBean");

        writer.println("<p>v1 = " + v1 + "</p>");
        writer.println("<p>demoBean = " + demoBean + "</p>");
    }

    private void printBeforeContent(PrintWriter writer) {
        writer.println(
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "<title>Conversation Demo</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<h1>Conversation Demo</h1>\n"
        );
    }

    private void printAfterContent(PrintWriter writer) {
        writer.println(
                "</body>\n" +
                "\n" +
                "</html>"
        );
    }
}
