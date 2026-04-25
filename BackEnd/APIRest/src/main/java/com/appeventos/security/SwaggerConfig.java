package com.appeventos.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Schema<?> loginSchema = new Schema<>()
                .type("object")
                .addProperty("email", new Schema<String>().type("string").example("usuario@email.com"))
                .addProperty("password", new Schema<String>().type("string").example("contraseña"));

        PathItem loginPath = new PathItem()
                .post(new Operation()
                        .summary("Login — obtener JWT (APILoginManager :8082)")
                        .addTagsItem("Login")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(loginSchema))))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("JWT token como String"))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Credenciales incorrectas"))));

        return new OpenAPI()
                .info(new Info()
                        .title("AppEventos API")
                        .description("API REST para gestión de eventos y reservas.\n\n" +
                                "**Para probar endpoints protegidos:**\n" +
                                "1. Selecciona el servidor `APILoginManager :8082`\n" +
                                "2. Llama a `POST /auth/login` y copia el token\n" +
                                "3. Vuelve a seleccionar `APIRest :8080`\n" +
                                "4. Haz clic en **Authorize** y pega el token")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("APIRest :8080"),
                        new Server().url("http://localhost:8082").description("APILoginManager :8082")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .path("/auth/login", loginPath);
    }
}
