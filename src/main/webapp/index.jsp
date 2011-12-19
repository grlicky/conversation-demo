<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.grlicky.demo.conversation.DemoBean" %>
<html>

<head>
<title>Conversation Demo Application</title>
<script type="text/javascript" src="scripts/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="scripts/jquery.cookie.js"></script>
<script type="text/javascript">
    jQuery(document).ready(function() {
        var windowName = window.name;
        if (!windowName) {
            windowName = new Date().getTime();
            window.name = windowName;
        }
        jQuery.cookie('conversationId', window.name);

        jQuery(window).focus(function() {
            var windowNameFromCookie = jQuery.cookie('conversationId');

            if (windowName != windowNameFromCookie) {
                jQuery.cookie('conversationId', window.name);
                alert("Window '" + windowName + "' has focus!");
            }
        });
    });
</script>
</head>

<body>
<h1>Conversation Demo</h1>
<p>Request 'cid' parameter: <%= request.getParameter("cid") %></p>

<%
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
%>
<p>v1 = <%= session.getAttribute("v1") %></p>
<p>demoBean = <%= session.getAttribute("demoBean") %></p>

<hr/>

<a href="?c=status">Show all contexts</a>
</body>

</html>
