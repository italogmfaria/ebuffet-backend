package com.ebuffet.utils.filters;

import com.ebuffet.utils.Constants;
import com.ebuffet.utils.security.BuffetContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BuffetContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(Constants.BUFFET_ID_HEADER);

        if (header != null && !header.isBlank()) {
            try {
                BuffetContext.set(Long.valueOf(header));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Buffet-Id header");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            BuffetContext.clear();
        }
    }
}
