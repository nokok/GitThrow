package net.orekyuu.gitthrow.infra;

import net.orekyuu.gitthrow.config.security.WorkbenchUserDetails;
import net.orekyuu.gitthrow.service.exceptions.NotAdminException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminOnlyAspect {

    @Around("@annotation(net.orekyuu.gitthrow.infra.AdminOnly)")
    public Object projectAuthAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        WorkbenchUserDetails userDetails = (WorkbenchUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        if (!userDetails.getUser().isAdmin()) {
            throw new NotAdminException();
        }

        return joinPoint.proceed();
    }
}
