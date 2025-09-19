package com.hackathon.medreminder.shared;

import com.hackathon.medreminder.shared.security.RestAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestAuthenticationEntryPointTest {

    @Test
    void commence_setsUnauthorizedStatusAndWritesJsonError() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        when(exception.getMessage()).thenReturn("Authentication failed");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint();

        entryPoint.commence(request, response, exception);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        printWriter.flush();

        String jsonOutput = stringWriter.toString();
        assertTrue(jsonOutput.contains("Unauthorized"));
        assertTrue(jsonOutput.contains("Authentication failed"));
    }
}
