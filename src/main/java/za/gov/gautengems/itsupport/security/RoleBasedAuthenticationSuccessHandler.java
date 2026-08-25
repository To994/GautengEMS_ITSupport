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


    // =========================================================
    // LOGIN SUCCESS
    // =========================================================

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {


        // =====================================================
        // GET USER ROLES
        // =====================================================

        Set<String> roles =
                AuthorityUtils.authorityListToSet(
                        authentication.getAuthorities()
                );


        // =====================================================
        // ADMIN
        // =====================================================

        if (roles.contains("ROLE_ADMIN")) {

            response.sendRedirect(
                    "/dashboard"
            );

            return;
        }


        // =====================================================
        // IT TECHNICIAN
        // =====================================================

        if (roles.contains("ROLE_TECHNICIAN")) {

            response.sendRedirect(
                    "/technician-dashboard"
            );

            return;
        }


        // =====================================================
        // STATION MANAGER
        //
        // IMPORTANT:
        //
        // Station Manager was previously called Employee.
        //
        // We are NOT renaming the Employee controller or
        // employee-dashboard.html.
        //
        // We only changed the security role to:
        //
        // ROLE_STATION_MANAGER
        //
        // Therefore Station Manager goes to the existing:
        //
        // /employee/dashboard
        //
        // =====================================================

        if (roles.contains("ROLE_STATION_MANAGER")) {

            response.sendRedirect(
                    "/employee/dashboard"
            );

            return;
        }


        // =====================================================
        // UNKNOWN / UNAUTHORISED ROLE
        // =====================================================

        response.sendRedirect(
                "/login?error=true"
        );
    }
}