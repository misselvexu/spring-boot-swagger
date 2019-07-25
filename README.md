## Acmedcare+ Spring Boot Starter Swagger

> Spring Boot Starter Swagger

### Usage

- Add Maven Pom Dependency
```xml

<dependency>
    <groupId>com.acmedcare.framework</groupId>
    <artifactId>spring-boot-starter-swagger</artifactId>
    <version>2.2.0-BUILD.SNAPSHOT</version>
</dependency>

```

- Add `@EnableSwagger2` Annotation On Startup Class

### Document

- 默认配置

```properties

swagger.enabled=是否启用swagger，默认：true
swagger.title=标题
swagger.description=描述
swagger.version=版本
swagger.license=许可证
swagger.licenseUrl=许可证URL
swagger.termsOfServiceUrl=服务条款URL
swagger.contact.name=维护人
swagger.contact.url=维护人URL
swagger.contact.email=维护人email
swagger.base-package=swagger扫描的基础包，默认：全扫描
swagger.base-path=需要处理的基础URL规则，默认：/**
swagger.exclude-path=需要排除的URL规则，默认：空
swagger.host=文档的host信息，默认：空
swagger.globalOperationParameters[0].name=参数名
swagger.globalOperationParameters[0].description=描述信息
swagger.globalOperationParameters[0].modelRef=指定参数类型
swagger.globalOperationParameters[0].parameterType=指定参数存放位置,可选header,query,path,body.form
swagger.globalOperationParameters[0].required=指定参数是否必传，true,false

```

- 分组配置

```properties

swagger.docket.<name>.title=标题
swagger.docket.<name>.description=描述
swagger.docket.<name>.version=版本
swagger.docket.<name>.license=许可证
swagger.docket.<name>.licenseUrl=许可证URL
swagger.docket.<name>.termsOfServiceUrl=服务条款URL
swagger.docket.<name>.contact.name=维护人
swagger.docket.<name>.contact.url=维护人URL
swagger.docket.<name>.contact.email=维护人email
swagger.docket.<name>.base-package=swagger扫描的基础包，默认：全扫描
swagger.docket.<name>.base-path=需要处理的基础URL规则，默认：/**
swagger.docket.<name>.exclude-path=需要排除的URL规则，默认：空
swagger.docket.<name>.name=参数名
swagger.docket.<name>.modelRef=指定参数类型
swagger.docket.<name>.parameterType=指定参数存放位置,可选header,query,path,body.form
swagger.docket.<name>.required=true=指定参数是否必传，true,false
swagger.docket.<name>.globalOperationParameters[0].name=参数名
swagger.docket.<name>.globalOperationParameters[0].description=描述信息
swagger.docket.<name>.globalOperationParameters[0].modelRef=指定参数存放位置,可选header,query,path,body.form
swagger.docket.<name>.globalOperationParameters[0].parameterType=指定参数是否必传，true,false

```

> 说明：`<name>`为swagger文档的分组名称，区分不同的Api.

- 其它配置

```properties

# json edit
swagger.ui-config.json-editor=false

# header show flag
swagger.ui-config.show-request-headers=true

# timeout
swagger.ui-config.request-timeout=5000

```

### Reference

- [Swagger Reference](https://swagger.io/solutions/getting-started-with-oas/)

### Issues

- [issues](https://github.com/misselvexu/spring-boot-swagger/issues)

### Contract

> iskp.me@gmail.com

