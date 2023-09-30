package com.SideProject.ECommerce.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//http://localhost:8085/E-Commerce-SpringBoot/swagger-ui/index.html
@Configuration
@EnableSwagger2
public class SwaggerConfig {  

   private static final String splitor = ";"; 

   public Docket createDocket(String groupName, ApiInfo apiInfo, String basePackage){
      return new Docket(DocumentationType.SWAGGER_2)
             .groupName(groupName)
             .apiInfo(apiInfo)
             .select()
             .apis(basePackage(basePackage))
             .paths(PathSelectors.any())
             .build();
   }

   @Bean
   public Docket createBackEndDocket() {
      return createDocket("BackEnd", BackEndInfo(), "com.SideProject.ECommerce.controller.BackEnd");
   }

   private ApiInfo BackEndInfo() {
      return new ApiInfoBuilder()
             .title("BackEnd API")
             .description("BackEnd API 文件")
             .licenseUrl("http://localhost:8085/E-Commerce-SpringBoot/swagger-ui/index.html")
             .version("1.0")
             .build();
   }
   
   @Bean
   public Docket createFrontEndDocket() {
      return createDocket("FrontEnd", BackEndInfo(), "com.SideProject.ECommerce.controller.FrontEnd");
   }

   private ApiInfo FrontEndInfo() {
      return new ApiInfoBuilder()
             .title("FrontEnd API")
             .description("FrontEnd API 文件")
             .licenseUrl("http://localhost:8085/E-Commerce-SpringBoot/swagger-ui/index.html")
             .version("1.0")
             .build();
   }

   public static Predicate<RequestHandler> basePackage(final String basePackage) {
      return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
   }

   private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
      return input -> {
          for (String strPackage : basePackage.split(splitor)) {
             boolean isMatch = input.getPackage().getName().startsWith(strPackage);
             if (isMatch) {
                return true;
             }
          }
          return false;
      };
   }  

   private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
      return Optional.fromNullable(input.declaringClass());
   }  

} 