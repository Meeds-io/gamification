<% 
  Object period = request.getAttribute("period");
  if (period == null) {
    period = "WEEK";
  } else {
    period = ((String[])period)[0];
  }
  Object limit = request.getAttribute("limit");
  if (limit == null) {
    limit = 5;
  } else {
    limit = ((String[])limit)[0];
  }
%>
<div class="VuetifyApp">
  <div id="popularSpacesApplication">
    <script type="text/javascript">
      require(['PORTLET/gamification-portlets/PopularSpaces'], app => app.init('<%=period%>', <%=limit%>));
    </script>
  </div>
</div>
