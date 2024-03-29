package com.acmedcare.framework.boot.swagger.autoconfigure;

import com.acmedcare.framework.boot.swagger.config.SwaggerConfigurationProperties;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link SwaggerAutoConfiguration}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2019-07-01.
 */
@SuppressWarnings("Duplicates")
@Configuration
@Import(EmbeddedSwaggerConfiguration.class)
public class SwaggerAutoConfiguration implements BeanFactoryAware {

  private BeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public SwaggerConfigurationProperties swaggerConfigurationProperties() {
    return new SwaggerConfigurationProperties();
  }

  @Bean
  public UiConfiguration uiConfiguration(SwaggerConfigurationProperties properties) {
    return UiConfigurationBuilder.builder()
        .deepLinking(properties.getUiConfig().getDeepLinking())
        .defaultModelExpandDepth(properties.getUiConfig().getDefaultModelExpandDepth())
        .defaultModelRendering(properties.getUiConfig().getDefaultModelRendering())
        .defaultModelsExpandDepth(properties.getUiConfig().getDefaultModelsExpandDepth())
        .displayOperationId(properties.getUiConfig().getDisplayOperationId())
        .displayRequestDuration(properties.getUiConfig().getDisplayRequestDuration())
        .docExpansion(properties.getUiConfig().getDocExpansion())
        .maxDisplayedTags(properties.getUiConfig().getMaxDisplayedTags())
        .operationsSorter(properties.getUiConfig().getOperationsSorter())
        .showExtensions(properties.getUiConfig().getShowExtensions())
        .tagsSorter(properties.getUiConfig().getTagsSorter())
        .validatorUrl(properties.getUiConfig().getValidatorUrl())
        .build();
  }

  @SuppressWarnings({"Guava", "AlibabaMethodTooLong"})
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(UiConfiguration.class)
  @ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
  public List<Docket> dockets(SwaggerConfigurationProperties properties) {
    ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    List<Docket> docketList = new LinkedList<>();

    if (properties.getDocket().size() == 0) {
      ApiInfo apiInfo =
          new ApiInfoBuilder()
              .title(properties.getTitle())
              .description(properties.getDescription())
              .version(properties.getVersion())
              .license(properties.getLicense())
              .licenseUrl(properties.getLicenseUrl())
              .contact(
                  new Contact(
                      properties.getContact().getName(),
                      properties.getContact().getUrl(),
                      properties.getContact().getEmail()))
              .termsOfServiceUrl(properties.getTermsOfServiceUrl())
              .build();

      if (properties.getBasePath().isEmpty()) {
        properties.getBasePath().add("/**");
      }
      List<Predicate<String>> basePath = Lists.newArrayList();
      for (String path : properties.getBasePath()) {
        basePath.add(PathSelectors.ant(path));
      }

      // exclude-path处理
      List<Predicate<String>> excludePath = new ArrayList<>();
      for (String path : properties.getExcludePath()) {
        excludePath.add(PathSelectors.ant(path));
      }

      Docket docketForBuilder =
          new Docket(DocumentationType.SWAGGER_2)
              .host(properties.getHost())
              .apiInfo(apiInfo)
              .securityContexts(Collections.singletonList(securityContext()))
              .globalOperationParameters(
                  buildGlobalOperationParametersFromSwaggerConfigurationProperties(
                      properties.getGlobalOperationParameters()));

      if ("BasicAuth".equalsIgnoreCase(properties.getAuthorization().getType())) {
        docketForBuilder.securitySchemes(Collections.singletonList(basicAuth()));
      } else if (!"None".equalsIgnoreCase(properties.getAuthorization().getType())) {
        docketForBuilder.securitySchemes(Collections.singletonList(apiKey()));
      }

      if (!properties.getApplyDefaultResponseMessages()) {
        buildGlobalResponseMessage(properties, docketForBuilder);
      }

      Docket docket =
          docketForBuilder
              .select()
              .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
              .paths(
                  Predicates.and(
                      Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
              .build();

      Class<?>[] array = new Class[properties.getIgnoredParameterTypes().size()];
      Class<?>[] ignoredParameterTypes = properties.getIgnoredParameterTypes().toArray(array);
      docket.ignoredParameterTypes(ignoredParameterTypes);

      configurableBeanFactory.registerSingleton("defaultDocket", docket);
      docketList.add(docket);
      return docketList;
    }

    // 分组创建
    for (String groupName : properties.getDocket().keySet()) {
      SwaggerConfigurationProperties.DocketInfo docketInfo = properties.getDocket().get(groupName);

      ApiInfo apiInfo =
          new ApiInfoBuilder()
              .title(
                  docketInfo.getTitle().isEmpty() ? properties.getTitle() : docketInfo.getTitle())
              .description(
                  docketInfo.getDescription().isEmpty()
                      ? properties.getDescription()
                      : docketInfo.getDescription())
              .version(
                  docketInfo.getVersion().isEmpty()
                      ? properties.getVersion()
                      : docketInfo.getVersion())
              .license(
                  docketInfo.getLicense().isEmpty()
                      ? properties.getLicense()
                      : docketInfo.getLicense())
              .licenseUrl(
                  docketInfo.getLicenseUrl().isEmpty()
                      ? properties.getLicenseUrl()
                      : docketInfo.getLicenseUrl())
              .contact(
                  new Contact(
                      docketInfo.getContact().getName().isEmpty()
                          ? properties.getContact().getName()
                          : docketInfo.getContact().getName(),
                      docketInfo.getContact().getUrl().isEmpty()
                          ? properties.getContact().getUrl()
                          : docketInfo.getContact().getUrl(),
                      docketInfo.getContact().getEmail().isEmpty()
                          ? properties.getContact().getEmail()
                          : docketInfo.getContact().getEmail()))
              .termsOfServiceUrl(
                  docketInfo.getTermsOfServiceUrl().isEmpty()
                      ? properties.getTermsOfServiceUrl()
                      : docketInfo.getTermsOfServiceUrl())
              .build();

      if (docketInfo.getBasePath().isEmpty()) {
        docketInfo.getBasePath().add("/**");
      }
      List<Predicate<String>> basePath = Lists.newArrayList();
      for (String path : docketInfo.getBasePath()) {
        basePath.add(PathSelectors.ant(path));
      }

      // exclude-path处理
      List<Predicate<String>> excludePath = Lists.newArrayList();
      for (String path : docketInfo.getExcludePath()) {
        excludePath.add(PathSelectors.ant(path));
      }

      Docket docketForBuilder =
          new Docket(DocumentationType.SWAGGER_2)
              .host(properties.getHost())
              .apiInfo(apiInfo)
              .securityContexts(Collections.singletonList(securityContext()))
              .globalOperationParameters(
                  assemblyGlobalOperationParameters(
                      properties.getGlobalOperationParameters(),
                      docketInfo.getGlobalOperationParameters()));

      if ("BasicAuth".equalsIgnoreCase(properties.getAuthorization().getType())) {
        docketForBuilder.securitySchemes(Collections.singletonList(basicAuth()));
      } else if (!"None".equalsIgnoreCase(properties.getAuthorization().getType())) {
        docketForBuilder.securitySchemes(Collections.singletonList(apiKey()));
      }

      if (!properties.getApplyDefaultResponseMessages()) {
        buildGlobalResponseMessage(properties, docketForBuilder);
      }

      Docket docket =
          docketForBuilder
              .groupName(groupName)
              .select()
              .apis(RequestHandlerSelectors.basePackage(docketInfo.getBasePackage()))
              .paths(
                  Predicates.and(
                      Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
              .build();

      Class<?>[] array = new Class[docketInfo.getIgnoredParameterTypes().size()];
      Class<?>[] ignoredParameterTypes = docketInfo.getIgnoredParameterTypes().toArray(array);
      docket.ignoredParameterTypes(ignoredParameterTypes);

      configurableBeanFactory.registerSingleton(groupName, docket);
      docketList.add(docket);
    }
    return docketList;
  }

  private ApiKey apiKey() {
    return new ApiKey(
        swaggerConfigurationProperties().getAuthorization().getName(),
        swaggerConfigurationProperties().getAuthorization().getKeyName(),
        ApiKeyVehicle.HEADER.getValue());
  }

  private BasicAuth basicAuth() {
    return new BasicAuth(swaggerConfigurationProperties().getAuthorization().getName());
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(
            PathSelectors.regex(swaggerConfigurationProperties().getAuthorization().getAuthRegex()))
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Collections.singletonList(
        SecurityReference.builder()
            .reference(swaggerConfigurationProperties().getAuthorization().getName())
            .scopes(authorizationScopes)
            .build());
  }

  private List<Parameter> buildGlobalOperationParametersFromSwaggerConfigurationProperties(
      List<SwaggerConfigurationProperties.GlobalOperationParameter> globalOperationParameters) {
    List<Parameter> parameters = Lists.newArrayList();

    if (Objects.isNull(globalOperationParameters)) {
      return parameters;
    }
    for (SwaggerConfigurationProperties.GlobalOperationParameter globalOperationParameter :
        globalOperationParameters) {
      parameters.add(
          new ParameterBuilder()
              .name(globalOperationParameter.getName())
              .description(globalOperationParameter.getDescription())
              .modelRef(new ModelRef(globalOperationParameter.getModelRef()))
              .parameterType(globalOperationParameter.getParameterType())
              .required(Boolean.parseBoolean(globalOperationParameter.getRequired()))
              .build());
    }
    return parameters;
  }

  /** 局部参数按照name覆盖局部参数 */
  private List<Parameter> assemblyGlobalOperationParameters(
      List<SwaggerConfigurationProperties.GlobalOperationParameter> globalOperationParameters,
      List<SwaggerConfigurationProperties.GlobalOperationParameter> docketOperationParameters) {

    if (Objects.isNull(docketOperationParameters) || docketOperationParameters.isEmpty()) {
      return buildGlobalOperationParametersFromSwaggerConfigurationProperties(
          globalOperationParameters);
    }

    Set<String> docketNames =
        docketOperationParameters.stream()
            .map(SwaggerConfigurationProperties.GlobalOperationParameter::getName)
            .collect(Collectors.toSet());

    List<SwaggerConfigurationProperties.GlobalOperationParameter> resultOperationParameters =
        Lists.newArrayList();

    if (Objects.nonNull(globalOperationParameters)) {
      for (SwaggerConfigurationProperties.GlobalOperationParameter parameter :
          globalOperationParameters) {
        if (!docketNames.contains(parameter.getName())) {
          resultOperationParameters.add(parameter);
        }
      }
    }

    resultOperationParameters.addAll(docketOperationParameters);
    return buildGlobalOperationParametersFromSwaggerConfigurationProperties(
        resultOperationParameters);
  }

  /**
   * 设置全局响应消息
   *
   * @param properties swaggerConfigurationProperties 支持
   *     POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE
   * @param docketForBuilder swagger docket builder
   */
  private void buildGlobalResponseMessage(
      SwaggerConfigurationProperties properties, Docket docketForBuilder) {

    SwaggerConfigurationProperties.GlobalResponseMessage globalResponseMessages =
        properties.getGlobalResponseMessage();

    /* POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE 响应消息体 **/
    List<ResponseMessage> postResponseMessages =
        getResponseMessageList(globalResponseMessages.getPost());
    List<ResponseMessage> getResponseMessages =
        getResponseMessageList(globalResponseMessages.getGet());
    List<ResponseMessage> putResponseMessages =
        getResponseMessageList(globalResponseMessages.getPut());
    List<ResponseMessage> patchResponseMessages =
        getResponseMessageList(globalResponseMessages.getPatch());
    List<ResponseMessage> deleteResponseMessages =
        getResponseMessageList(globalResponseMessages.getDelete());
    List<ResponseMessage> headResponseMessages =
        getResponseMessageList(globalResponseMessages.getHead());
    List<ResponseMessage> optionsResponseMessages =
        getResponseMessageList(globalResponseMessages.getOptions());
    List<ResponseMessage> trackResponseMessages =
        getResponseMessageList(globalResponseMessages.getTrace());

    docketForBuilder
        .useDefaultResponseMessages(properties.getApplyDefaultResponseMessages())
        .globalResponseMessage(RequestMethod.POST, postResponseMessages)
        .globalResponseMessage(RequestMethod.GET, getResponseMessages)
        .globalResponseMessage(RequestMethod.PUT, putResponseMessages)
        .globalResponseMessage(RequestMethod.PATCH, patchResponseMessages)
        .globalResponseMessage(RequestMethod.DELETE, deleteResponseMessages)
        .globalResponseMessage(RequestMethod.HEAD, headResponseMessages)
        .globalResponseMessage(RequestMethod.OPTIONS, optionsResponseMessages)
        .globalResponseMessage(RequestMethod.TRACE, trackResponseMessages);
  }

  private List<ResponseMessage> getResponseMessageList(
      List<SwaggerConfigurationProperties.GlobalResponseMessageBody>
          globalResponseMessageBodyList) {
    List<ResponseMessage> responseMessages = new ArrayList<>();
    for (SwaggerConfigurationProperties.GlobalResponseMessageBody globalResponseMessageBody :
        globalResponseMessageBodyList) {
      ResponseMessageBuilder responseMessageBuilder = new ResponseMessageBuilder();
      responseMessageBuilder
          .code(globalResponseMessageBody.getCode())
          .message(globalResponseMessageBody.getMessage());

      if (!StringUtils.isEmpty(globalResponseMessageBody.getModelRef())) {
        responseMessageBuilder.responseModel(new ModelRef(globalResponseMessageBody.getModelRef()));
      }
      responseMessages.add(responseMessageBuilder.build());
    }

    return responseMessages;
  }
}
