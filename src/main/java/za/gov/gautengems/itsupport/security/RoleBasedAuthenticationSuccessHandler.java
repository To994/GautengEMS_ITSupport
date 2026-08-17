package za.gov.gautengems.itsupport.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class RoleBasedAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles =
                AuthorityUtils.authorityListToSet(
                        authentication.getAuthorities()
                );

        if (roles.contains("ROLE_ADMIN")) {

            response.sendRedirect("/dashboard");

        } else if (roles.contains("ROLE_TECHNICIAN")) {

            response.sendRedirect("/technician-dashboard");

        } else if (roles.contains("ROLE_EMPLOYEE")) {

            response.sendRedirect("/employee-dashboard");

        } else {

            response.sendRedirect("/login?error=true");
        }
    }
}