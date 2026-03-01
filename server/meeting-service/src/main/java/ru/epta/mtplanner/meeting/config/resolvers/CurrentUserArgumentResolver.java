package ru.epta.mtplanner.meeting.config.resolvers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.epta.mtplanner.commons.exception.UnauthorizedException;
import ru.epta.mtplanner.commons.model.TokenPayload;
import ru.epta.mtplanner.meeting.config.annotation.CurrentUser;
import ru.epta.mtplanner.meeting.connector.AuthConnector;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectProvider<AuthConnector> authConnectorProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
               && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String sessionId = extractSession(request);

        AuthConnector authConnector = authConnectorProvider.getIfAvailable();

        TokenPayload tokenPayload = authConnector.validateSession(sessionId);

        return tokenPayload.getUserId();
    }

    private String extractSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnauthorizedException("User does not authenticated");
        }
        return Arrays.stream(cookies)
            .filter(c -> "sessionId".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new UnauthorizedException("User does not authenticated"));
    }
}
