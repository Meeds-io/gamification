<%
  Object showLocked = request.getAttribute("showLocked");
%>
<div class="VuetifyApp">
  <div id="rulesOverview">
    <script type="text/javascript">
      require(['PORTLET/gamification-portlets/challengesOverview'], app => app.init(<%=showLocked != null && ((String[])showLocked).length == 1 && "true".equals(((String[])showLocked)[0])%>));
    </script>
  </div>
</div>
