package com.example.poller_bear.security;

import com.example.poller_bear.dto.ErrorResponseDto;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException, ServletException {

        ErrorResponseDto responseDto = new ErrorResponseDto(exception.getMessage(), HttpStatus.UNAUTHORIZED);

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonString = new Gson().toJson(responseDto);

        httpServletResponse.getWriter().write(jsonString);

//        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }
}
