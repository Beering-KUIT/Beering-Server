package kuit.project.beering.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtExceptionFilter 진입");
        try {
            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            setResponse(response, new ObjectMapper(), e.getStatus());
        } catch (JwtException e) {
            setResponse(response, new ObjectMapper(), BaseResponseStatus.INVALID_TOKEN_TYPE);
        }
    }

    private void setResponse(HttpServletResponse response, ObjectMapper objectMapper, BaseResponseStatus status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new BaseResponse<>(status));
    }
}

