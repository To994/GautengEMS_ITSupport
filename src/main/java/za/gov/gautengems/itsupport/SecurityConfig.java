package za.gov.gautengems.itsupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import za.gov.gautengems.itsupport.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider,
            CustomUserDetailsService userDetailsService
    ) throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login")
                )


                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // =====================================================
                        // PUBLIC PAGES / RESOURCES
                        // =====================================================

                        .requestMatchers(
                                "/login",

                                "/forgot-password",

                                "/reset-password",

                                "/css/**",

                                "/images/**",

                                "/js/**"
                        ).permitAll()


                        // =====================================================
                        // ADMIN-ONLY MANAGEMENT
                        // =====================================================

                        .requestMatchers(
                                "/settings",

                                "/locations/**"
                        ).hasRole("ADMIN")


                        // =====================================================
                        // ALL OTHER AUTHENTICATED USERS
                        // =====================================================

                        .anyRequest().authenticated()
                )


                // =====================================================
                // LOGIN
                // =====================================================

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .successHandler(
                                roleBasedSuccessHandler()
                        )

                        .failureUrl(
                                "/login?error=true"
                        )

                        .permitAll()
                )


                // =====================================================
                // REMEMBER ME
                // =====================================================

                .rememberMe(remember -> remember

                        /*
                         * This must match the name of the
                         * checkbox in login.html:
                         *
                         * name="remember-me"
                         */

                        .rememberMeParameter("remember-me")


                        /*
                         * Remember the user for 14 days.
                         */

                        .tokenValiditySeconds(
                                14 * 24 * 60 * 60
                        )


                        /*
                         * Spring Security uses the existing
                         * user details service to identify
                         * the logged-in user.
                         */

                        .userDetailsService(
                                userDetailsService
                        )


                        /*
                         * Key used to sign the remember-me
                         * authentication token.
                         *
                         * Keep this value unchanged once
                         * deployed.
                         */

                        .key(
                                "GautengEMS-ITSupport-RememberMe-Key-2026"
                        )
                )


                // =====================================================
                // LOGOUT
                // =====================================================

                .logout(logout -> logout

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .permitAll()
                );


        // =========================================================
        // AUTHENTICATION PROVIDER
        // =========================================================

        http.authenticationProvider(
                authenticationProvider
        );


        return http.build();
    }


    // =========================================================
    // ROLE-BASED LOGIN REDIRECTION
    // =========================================================

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {

        return (request, response, authentication) -> {

            boolean isAdmin =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_ADMIN")
                            );


            boolean isTechnician =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_TECHNICIAN")
                            );


            boolean isStationManager =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority.getAuthority()
                                            .equals("ROLE_STATION_MANAGER")
                            );


            // -------------------------------------------------
            // ADMIN
            // -------------------------------------------------

            if (isAdmin) {

                response.sendRedirect(
                        "/dashboard"
                );

            }


            // -------------------------------------------------
            // TECHNICIAN
            // -------------------------------------------------

            else if (isTechnician) {

                response.sendRedirect(
                        "/technician-dashboard"
                );

            }


            // -------------------------------------------------
            // STATION MANAGER
            // -------------------------------------------------

            else if (isStationManager) {

                response.sendRedirect(
                        "/employee/dashboard"
                );

            }


            // -------------------------------------------------
            // UNKNOWN ROLE
            // -------------------------------------------------

            else {

                response.sendRedirect(
                        "/login?error=true"
                );

            }
        };
    }


    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        var provider =
                new org.springframework.security.authentication.dao.DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }
}