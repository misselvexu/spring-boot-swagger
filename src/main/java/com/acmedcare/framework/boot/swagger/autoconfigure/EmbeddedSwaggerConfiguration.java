package com.acmedcare.framework.boot.swagger.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

/**
 * {@link EmbeddedSwaggerConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2019-07-01.
 */
@Configuration
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
@Import({
    Swagger2DocumentationConfiguration.class,
    BeanValidatorPluginsConfiguration.class
})
public class EmbeddedSwaggerConfiguration {
}
