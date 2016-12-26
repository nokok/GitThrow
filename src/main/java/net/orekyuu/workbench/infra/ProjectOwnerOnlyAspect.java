package net.orekyuu.workbench.infra;

import net.orekyuu.workbench.config.security.WorkbenchUserDetails;
import net.orekyuu.workbench.controller.view.user.project.NotMemberException;
import net.orekyuu.workbench.project.domain.model.Project;
import net.orekyuu.workbench.service.ProjectService;
import net.orekyuu.workbench.service.exceptions.ProjectNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class ProjectOwnerOnlyAspect {
    @Autowired
    private ProjectService projectService;

    @Around("@annotation(net.orekyuu.workbench.infra.ProjectOwnerOnly)")
    public Object projectAuthAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        String projectName = getProjectName(joinPoint).orElseThrow(IllegalArgumentException::new);

        WorkbenchUserDetails userDetails = (WorkbenchUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        Project project = projectService.findById(projectName).orElseThrow(() -> new ProjectNotFoundException(projectName));
        boolean joined = Objects.equals(project.ownerUserId, userDetails.getUser().id);
        if (!joined) {
            throw new NotMemberException();
        }

        return joinPoint.proceed();
    }

    private Optional<String> getProjectName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //ProjectNameに一致する引数がないか
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            ProjectName[] nameAnnotations = param.getDeclaredAnnotationsByType(ProjectName.class);
            if (nameAnnotations.length == 0) {
                //アノテーションがないので次
                continue;
            }

            //文字列じゃないので次
            if (!String.class.isAssignableFrom(param.getType())) {
                continue;
            }

            return Optional.ofNullable((String) joinPoint.getArgs()[i]);
        }

        return Optional.empty();
    }
}
