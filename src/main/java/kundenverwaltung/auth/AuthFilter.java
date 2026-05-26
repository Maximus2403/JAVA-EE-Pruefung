package kundenverwaltung.auth;

import kundenverwaltung.user.UserRole;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@WebFilter("*.xhtml")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PAGES = Set.of(
            "/index.xhtml",
            "/login.xhtml",
            "/register.xhtml",
            "/error.xhtml"
    );

    @Inject
    private AuthController authController;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String contextPath = httpRequest.getContextPath();
        String requestUri  = httpRequest.getRequestURI();
        String path        = requestUri.substring(contextPath.length());

        // JSF-Ressourcen immer durchlassen
        if (path.startsWith("/jakarta.faces.resource/")) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        // Öffentliche Seiten immer durchlassen
        if (PUBLIC_PAGES.contains(path)) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        // CDI AuthController direkt prüfen (SessionScoped — bleibt über Requests erhalten)
        if (authController.isLoggedIn()) {
            UserRole role = authController.getCurrentUser().getRole();

            // /admin/* → nur ADMIN
            if (path.startsWith("/admin/") && role != UserRole.ADMIN) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // /mgmt/* → SACHBEARBEITER oder ADMIN
            if (path.startsWith("/mgmt/")
                    && role != UserRole.SACHBEARBEITER
                    && role != UserRole.ADMIN) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        // Nicht eingeloggt → zur Login-Seite
        String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
        httpResponse.sendRedirect(contextPath + "/login.xhtml?redirect=" + encodedPath);
    }
}
