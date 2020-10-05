package com.example.poller_bear.security;

import com.example.poller_bear.service.AccountUserDetailsService;
import com.example.poller_bear.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AccountUserDetailsService accountUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = extractTokenFromRequest(httpServletRequest);

        try {

            if(StringUtils.hasText(jwtToken) && jwtUtil.validate(jwtToken)) {

                Long userId = jwtUtil.extractUserId(jwtToken);

                AccountUserDetails userDetails = accountUserDetailsService.loadByUserId(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource()
                                                .buildDetails(httpServletRequest));

                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
            }
        } catch (Exception exception) {
            // TODO : log
            System.out.println(exception);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String extractTokenFromRequest(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            String token = bearerToken.substring(7);
            return token;
        } else {
            return null;
        }
    }
}
