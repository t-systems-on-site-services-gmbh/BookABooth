package de.tsystems.onsite.bookabooth.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import de.tsystems.onsite.bookabooth.security.*;
import de.tsystems.onsite.bookabooth.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.web.filter.CookieCsrfFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(
                csrf ->
                    csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)) // one year
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(
                        permissions ->
                            permissions.policy(
                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                            )
                    ))
            .authorizeHttpRequests(authz -> {
                // prettier-ignore

                // permit all
                authz
                    .requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")).permitAll()
                    .requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")).permitAll()
                    .requestMatchers(mvc.pattern("/assets/**")).permitAll()
                    .requestMatchers(mvc.pattern("/content/**")).permitAll()
                    .requestMatchers(mvc.pattern("/uploads/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.pattern("/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/api/ausstellerliste")).permitAll()
                ;
                // role specific access
                authz
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/account"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/account"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/account/cancel-booking"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/account/change-password"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/account/delete-account/*"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/account/remove-waitinglist"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/booths"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/booths/occupied"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/bookings/mybooking"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/bookings/unavailable"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/bookings/booth/*"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/bookings/confirm/*"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/checklist"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/companies/*/image"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/locations"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/privacy-policy/latest"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/privacy-policy/check"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/privacy-policy/accept"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/service-packages"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/systems"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/waitinglist/add-waitinglist"))
                    .hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER);

                // all other endpoints (only Admin)
                authz
                    .requestMatchers(mvc.pattern("/api/admin/**"))
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/v3/api-docs/**"))
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/**"))
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/**"))
                    .hasAuthority(AuthoritiesConstants.ADMIN);
            })
            .exceptionHandling(
                exceptionHanding ->
                    exceptionHanding.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new OrRequestMatcher(antMatcher("/api/**"))
                    )
            )
            .formLogin(
                formLogin ->
                    formLogin
                        .loginPage("/")
                        .loginProcessingUrl("/api/authentication")
                        .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                        .failureHandler((request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                        .permitAll()
            )
            .logout(
                logout -> logout.logoutUrl("/api/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()).permitAll()
            );
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
