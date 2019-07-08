package com.acmedcare.framework.boot.swagger;

import com.acmedcare.framework.boot.swagger.autoconfigure.SwaggerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * {@link EnableSwagger2}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2019-07-01.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SwaggerAutoConfiguration.class)
public @interface EnableSwagger2 {
}
