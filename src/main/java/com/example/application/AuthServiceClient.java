package com.example.application;

import com.example.application.views.login.AuthUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8090")
                .build();
    }

    public Mono<String> authenticateUser(AuthUserDto authUserDto) {
        return webClient.post()
                .uri("/auth/login")
                .body(Mono.just(authUserDto), AuthUserDto.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Boolean> validateToken(String token) {
        return webClient.post()
                .uri("/auth/validateToken")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TokenValidationRequest(token))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .doOnError(e -> log.error("Ошибка при валидации токена", e));
    }



    @Getter
    @Setter
    static class TokenValidationRequest {
        private String token;

        public TokenValidationRequest(String token) {
            this.token = token;
        }

    }
}
