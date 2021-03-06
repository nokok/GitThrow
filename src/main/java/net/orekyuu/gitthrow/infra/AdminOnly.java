package net.orekyuu.gitthrow.infra;


import java.lang.annotation.*;

/**
 * 管理者のみがアクセスできる
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOnly {
}
