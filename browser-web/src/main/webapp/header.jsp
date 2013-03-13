<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

    <link REL="SHORTCUT ICON" href="<c:url value='/images/browser/tgac_logo.png'/>">

    <link rel="stylesheet" href="<c:url value='/styles/style.css'/>" type="text/css">

    <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/prototype.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/fluxion-ajax/fluxion-ajax-compiled.js'/>"></script>

    <!-- jQuery -->
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-1.4.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-ui-1.8.custom.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tablesorter.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.drawinglibrary.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.cookie.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jcanvas.min.js'/>"></script>


    <link rel="stylesheet" href="<c:url value='/scripts/jquery/css/smoothness/jquery-ui-1.8.custom.css'/>"

          type="text/css">
    <script type="text/javascript" src="<c:url value='/scripts/jquery/colorbox/jquery.colorbox-min.js'/>"></script>
    <link rel="stylesheet" href="<c:url value='/scripts/jquery/colorbox/colorbox.css'/>"
          type="text/css">
    <script type="text/javascript">jQuery.noConflict();</script>

    <!--Browser Functions-->
    <script type="text/javascript" src="<c:url value='/scripts/init.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/browser.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/sequenceController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/trackController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/popup.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/getset.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/utils.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/browser_ajax.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/uploadTrackController.js'/>"></script>
    <%--<title>TGAC Browser - BRIC CHO <c:if test="${not empty title}">- ${title}</c:if></title>--%>
    <title>${initParam.pageTitle}</title>
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-21666189-7']);
        _gaq.push(['_trackPageview']);

        (function () {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();

    </script>
</head>
<body>

<div id="dbinfo"></div>
<div class="blasttab">


    <input type="text" id="search" class="seachbox" value=${initParam.defaultRef}/>
    <%--<button id="searchbutton" class="ui-state-default ui-corner-all"--%>
    <%--onclick="search(jQuery('#search').val(), oldTracklist);">Search--%>
    <%--</button>--%>
    <c:set var="databases">${initParam.blastdblink} </c:set>

    <c:set var="dateParts" value="${fn:split(databases, ',')}"/>

    <c:set var="length">${fn:length(dateParts)}</c:set>

    ${initParam.fasta == "true" && length > 0 ? "| <a href=\"blast.jsp\"><span>Blast Search</span></a>" : ""}


    | <a href="<c:url value="session.jsp"/>"><span>Load Session</span></a>
</div>

<div class="headerbar">
    <center>
        <table>
            <tr>
                <td>
                    <a class="headerlink" href="<c:url value="/"/>"> <font color=white> <b>TGAC Browser
                        - ${initParam.header} </font></a>
                </td>
                <td>
                    <font color=white>
                        <div id="seqnameh1"></div>
                    </font> </b>
                </td>
            </tr>
        </table>
    </center>
</div>

<%--<table border="0" width="100%">--%>
<%--<tr>--%>
<%--<td class="headertable" align="left" onclick="window.location.href='<c:url value='/'/>'">--%>
<%--<img src="<c:url value='/images/tgac_new_logo_nosponsers.png'/>" alt="TGAC Logo" name="logo"--%>
<%--border="0" id="TGAClogo"/>--%>
<%--</td>--%>
<%--<td align="right" >--%>
<%--<div id=dbinfo></div>--%>
<%--<div id=sessionid></div>--%>
<%--</td>--%>
<%--</tr>--%>
<%--</table>--%>

<%--<div id="navtabs">--%>
<%--<ul>--%>
<%--<li><a href="<c:url value="index.jsp"/>"><span>Home</span></a></li>--%>
<%--<li><a href="<c:url value="blast.jsp"/>"><span>Blast Search</span></a></li>--%>
<%--<li><a href="<c:url value="session.jsp"/>"><span>Load Session</span></a></li>--%>
<%--</ul>--%>
<%--</div>--%>
<div id="filetrack" style="visibility: hidden; position: fixed;">${initParam.trackfiles}</div>
      <div id="title" style="visibility: hidden; position: fixed;">${initParam.urlpath}</div>
<div id="content">