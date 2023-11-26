package com.example.application;

import com.example.application.views.exam.ExamDTO;
import com.example.application.views.student.StudentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.channels.MembershipKey;

@Service
@Slf4j
public class ExamService {
    private final WebClient webClient;

    @Autowired
    public ExamService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090/exams").build();
    }
    public Flux<ExamDTO> getExams() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(ExamDTO.class);
    }


    public Mono<Boolean> addExam(ExamDTO examDTO) {
        return webClient.post()
                .uri("/create")
                .body(Mono.just(examDTO), ExamDTO.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Boolean> updateExam(ExamDTO examDTO) {
        return webClient.post()
                .uri("/update")
                .body(Mono.just(examDTO), ExamDTO.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Boolean> deleteExam (Long id) {
        return webClient.delete()
                .uri("/" + id.toString())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().equals(HttpStatus.OK))
                .defaultIfEmpty(false);
    }
}
