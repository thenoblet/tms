<%--
  Created by IntelliJ IDEA.
  User: appia
  Date: 17/05/2025
  Time: 16:41
  To change this template use File | Settings | File Templates.
--%>
</div><!-- End of container from header.jsp -->

<footer class="footer mt-auto py-3 bg-light">
  <div class="container text-center">
    <span class="text-muted">NovaTech Task Management System &copy; 2025</span>
  </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>

<!-- Auto-dismiss alerts after 5 seconds -->
<script>
  document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function(alert) {
      setTimeout(function() {
        const closeButton = alert.querySelector('.btn-close');
        if (closeButton) {
          closeButton.click();
        }
      }, 5000);
    });
  });
</script>
</body>
</html>