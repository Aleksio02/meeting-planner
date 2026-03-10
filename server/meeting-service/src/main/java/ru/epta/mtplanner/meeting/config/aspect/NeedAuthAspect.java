package ru.epta.mtplanner.meeting.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NeedAuthAspect {

    @Pointcut("@annotation(ru.epta.mtplanner.meeting.config.annotation.RequiresAuth)")
    public void requiresAuthPointcut() {
        // Do nothing
    }

    @Before("requiresAuthPointcut()")
    public void processRequireAuth(JoinPoint joinPoint) {
        throw new RuntimeException("Not implemented");
    }
}
