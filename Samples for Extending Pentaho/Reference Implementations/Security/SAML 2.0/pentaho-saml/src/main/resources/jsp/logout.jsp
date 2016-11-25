<%-- /*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2016 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */ --%> 

<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core'%>
<%@
    page language="java"
    import="org.pentaho.platform.engine.core.system.PentahoSystem,
            org.pentaho.platform.util.messages.LocaleHelper,
          org.pentaho.platform.api.engine.IPluginManager,
            org.pentaho.platform.web.jsp.messages.Messages,
            java.util.List,
            java.util.ArrayList,
            java.util.StringTokenizer,
            org.pentaho.platform.engine.core.system.PentahoSessionHolder,
            org.owasp.encoder.Encode"%>
<%!
  // List of request URL strings to look for to send 401

  private List<String> send401RequestList;

  public void jspInit() {
    // super.jspInit();
    send401RequestList = new ArrayList<String>();
    String unauthList = getServletConfig().getInitParameter("send401List"); //$NON-NLS-1$
    if (unauthList == null) {
      send401RequestList.add("AdhocWebService"); //$NON-NLS-1$
    } else {
      StringTokenizer st = new StringTokenizer(unauthList, ","); //$NON-NLS-1$
      String requestStr;
      while (st.hasMoreElements()) {
        requestStr = st.nextToken();
        send401RequestList.add(requestStr.trim());
      }
    }
  }
%>
<%
  response.setCharacterEncoding(LocaleHelper.getSystemEncoding());
  int year = (new java.util.Date()).getYear() + 1900;
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" class="bootstrap">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title><%=Messages.getInstance().getString("UI.PUC.LOGIN.TITLE")%></title>

  <%
    String ua = request.getHeader("User-Agent").toLowerCase();
    if (!"desktop".equalsIgnoreCase(request.getParameter("mode"))) {
      if (ua.contains("ipad") || ua.contains("ipod") || ua.contains("iphone") || ua.contains("android") || "mobile".equalsIgnoreCase(request.getParameter("mode"))) {
        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, PentahoSessionHolder.getSession());
        List<String> pluginIds = pluginManager.getRegisteredPlugins();
        for (String id : pluginIds) {
          String mobileRedirect = (String)pluginManager.getPluginSetting(id, "mobile-redirect", null);
          if (mobileRedirect != null) {
            // we have a mobile redirect
            //Check for deep linking by fetching the name and startup-url values from URL query parameters
            String name = (String) request.getAttribute("name");
            String startupUrl = (String) request.getAttribute("startup-url");
            if (startupUrl != null && name != null){
              //Sanitize the values assigned
              mobileRedirect += "?name=" + Encode.forJavaScript(name) + "&startup-url=" + Encode.forJavaScript(startupUrl);
            }
  %>
  <script type="text/javascript">
    if(typeof window.top.PentahoMobile != "undefined"){
      window.top.location.reload();
    } else {
      var tag = document.createElement('META');
      tag.setAttribute('HTTP-EQUIV', 'refresh');
      tag.setAttribute('CONTENT', '0;URL=<%=mobileRedirect%>');
      document.getElementsByTagName('HEAD')[0].appendChild(tag);
    }
  </script>
</head>
<BODY>
<!-- this div is here for authentication detection (used by mobile, PIR, etc) -->
<div style="display:none">j_spring_security_check</div>
</BODY>
</HTML>
<%
          return;
        }
      }
    }
  }
%>

<meta name="gwt:property" content="locale=<%=Encode.forHtmlAttribute(request.getLocale().toString())%>">
<link rel="shortcut icon" href="/pentaho-style/favicon.ico" />

<style type="text/css">

  #login-background,
  #loginError.pentaho-dialog,
  #systemDown.pentaho-dialog,
  #login-footer {
    display: none;
  }

  /* below are logout.jsp specific css classes */

  div#login-form-container {
    left: 395px;
  }

  div#animate-wrapper > div.envelop-wrapper {
    width: 465px;
    height: 154px;
    background-color: #FFFFFF;
    border-style: solid;
    border-color: #F2CE17;
    border-width: 1px;
    border-radius: 5px;
  }

  div.envelop-wrapper > div.logout-msg-wrapper {
    float:left;
    height: 25px;
    font-family: 'OpenSansLight', 'OpenSans';
    font-weight: 300;
    font-style: normal;
    font-size: 22px;
    color: #1973BC;
    margin: 25px 0 25px 25px; /* trbl */
  }

  div.envelop-wrapper > hr.logout-hr-line {
    float: left;
    height: 1px;
    width: 413px;
    margin: 0 0 0 25px; /* trbl */
  }

  div.input-container > button.back-to-login-btn {
    float: right;
    margin: 25px 25px 0 0; /* trbl */
  }

</style>

<!--[if IE]>
  <style type="text/css">
      div.envelop-wrapper > div.logout-msg-wrapper {
        float:left;
        height: 30px;
        font-family: 'OpenSansLight', 'OpenSans';
        font-weight: 300;
        font-style: normal;
        font-size: 22px;
        color: #1973BC;
        margin: 0px; 
      }
  </style>
<![endif]-->

<script language="javascript" type="text/javascript" src="webcontext.js"></script>

</head>

<body class="pentaho-page-background">
<div id="login-wrapper">
  <div id="login-background">
    <div id="login-logo"></div>

<% 
    String cleanedLang = Encode.forHtmlAttribute(request.getLocale().toString());
    if ( cleanedLang != null ) {
      if ( cleanedLang.indexOf("_") > 0 ){
        cleanedLang = cleanedLang.substring( 0, cleanedLang.indexOf("_") );
      }
    }
%>
    <div id="login-form-container" class="lang_<%=cleanedLang%>">
      <div id="animate-wrapper">
      <div class="envelop-wrapper"> 
          <div class="logout-msg-wrapper"><%=Messages.getInstance().getString("UI.PUC.LOGOUT.HEADER")%></div>
          <hr class="logout-hr-line" />
          <form name="back2Login" id="back2Login" action="/" method="GET" onkeyup="if(window.event && window.event.keyCode && window.event.keyCode==13){var buttonToClick = document.getElementById('back2LoginBtn'); if(buttonToClick){ buttonToClick.click();}}">
            <div class="row-fluid nowrap">
              <div class="input-container">
                <button type="submit" class="btn back-to-login-btn"><%=Messages.getInstance().getString("UI.PUC.LOGOUT.BUTTON")%></button>
                <input type="hidden" name="locale" value="en_US">
              </div>
            </div>
          </form>
      </div>
      </div>
    </div>
  </div>
  <div id="login-footer-wrapper">
    <div id="login-footer" class="beforeSlide"><%=Messages.getInstance().getString("UI.PUC.LOGIN.COPYRIGHT", String.valueOf(year))%></div>
  </div>
</div>

<script type="text/javascript">

  $(document).ready(function(){

    $("#login-background").fadeIn(1000, function() {
      $("#login-logo").addClass("afterSlide");

      $("#animate-wrapper").addClass("afterSlide");
      $("#j_username").focus();

      $("#login-footer").addClass("afterSlide");

    });
  });
</script>
</body>
