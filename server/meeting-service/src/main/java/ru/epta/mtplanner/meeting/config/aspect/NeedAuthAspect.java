package ru.epta.mtplanner.meeting.config.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.epta.mtplanner.meeting.connector.AuthConnector;

@Aspect
@Component
public class NeedAuthAspect {

    private final AuthConnector authConnector;

    public NeedAuthAspect(AuthConnector authConnector) {this.authConnector = authConnector;}

    @Pointcut("@annotation(ru.epta.mtplanner.meeting.config.annotation.RequiresAuth)")
    public void requiresAuthPointcut() {
        // Do nothing
    }

    @Before("requiresAuthPointcut()")
    public void processRequireAuth(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader("Authorization");
        authConnector.validate(token);

        throw new RuntimeException("Not implemented");
    }
}
