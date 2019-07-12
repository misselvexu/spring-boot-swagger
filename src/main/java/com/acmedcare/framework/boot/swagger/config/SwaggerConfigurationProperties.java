package com.acmedcare.framework.boot.swagger.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link SwaggerConfigurationProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2019-07-01.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.swagger")
public class SwaggerConfigurationProperties implements Serializable {

  private Boolean enabled;

  private String title = "";

  private String description = "";

  private String version = "";

  private String license = "";

  private String licenseUrl = "";

  private String termsOfServiceUrl = "";

  private List<Class<?>> ignoredParameterTypes = new ArrayList<>();

  private Contact contact = new Contact();

  private String basePackage = "";

  private List<String> basePath = new ArrayList<>();

  private List<String> excludePath = new ArrayList<>();

  private Map<String, DocketInfo> docket = new LinkedHashMap<>();

  private String host = "";

  private List<GlobalOperationParameter> globalOperationParameters;

  private UiConfig uiConfig = new UiConfig();

  private Boolean applyDefaultResponseMessages = true;

  private GlobalResponseMessage globalResponseMessage;

  private Authorization authorization = new Authorization();

  @Data
  @NoArgsConstructor
  public static class GlobalOperationParameter {
    
    private String name;

    private String description;

    private String modelRef;

    /** 参数放在哪个地方:header,query,path,body.form */
    private String parameterType;

    private String required;
  }

  @Data
  @NoArgsConstructor
  public static class DocketInfo {

    private String title = "";

    private String description = "";

    private String version = "";

    private String license = "";

    private String licenseUrl = "";

    private String termsOfServiceUrl = "";

    private Contact contact = new Contact();
    
    private String basePackage = "";

    private List<String> basePath = new ArrayList<>();

    private List<String> excludePath = new ArrayList<>();

    private List<GlobalOperationParameter> globalOperationParameters;

    private List<Class<?>> ignoredParameterTypes = new ArrayList<>();
  }

  @Data
  @NoArgsConstructor
  public static class Contact {

    private String name = "";

    private String url = "";

    private String email = "";
  }

  @Data
  @NoArgsConstructor
  public static class GlobalResponseMessage {

    List<GlobalResponseMessageBody> post = new ArrayList<>();

    List<GlobalResponseMessageBody> get = new ArrayList<>();

    List<GlobalResponseMessageBody> put = new ArrayList<>();

    List<GlobalResponseMessageBody> patch = new ArrayList<>();

    List<GlobalResponseMessageBody> delete = new ArrayList<>();

    List<GlobalResponseMessageBody> head = new ArrayList<>();
    
    List<GlobalResponseMessageBody> options = new ArrayList<>();

    List<GlobalResponseMessageBody> trace = new ArrayList<>();
  }

  @Data
  @NoArgsConstructor
  public static class GlobalResponseMessageBody {

    private int code;

    private String message;

    private String modelRef;
  }

  @Data
  @NoArgsConstructor
  public static class UiConfig {

    private String apiSorter = "alpha";

    private Boolean jsonEditor = false;
    
    private Boolean showRequestHeaders = true;

    private String submitMethods = "get,post,put,delete,patch";

    private Long requestTimeout = 10000L;

    private Boolean deepLinking;
    
    private Boolean displayOperationId;
    
    private Integer defaultModelsExpandDepth;
    
    private Integer defaultModelExpandDepth;
    
    private ModelRendering defaultModelRendering;

    private Boolean displayRequestDuration = true;

    private DocExpansion docExpansion;

    private Object filter;

    private Integer maxDisplayedTags;
    
    private OperationsSorter operationsSorter;
    
    private Boolean showExtensions;
    
    private TagsSorter tagsSorter;

    /** Network */
    private String validatorUrl;
  }

  @Data
  @NoArgsConstructor
  public static class Authorization {

    private String name = "Authorization";

    private String type = "ApiKey";

    private String keyName = "Token";

    private String authRegex = "^.*$";
  }
}
