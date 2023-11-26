package com.example.application;

import com.example.application.views.student.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {
    private final WebClient webClient;

    @Autowired
    public StudentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090/students").build();
    }


    public Mono<Boolean> addStudent(StudentDTO studentDTO) {
        return webClient.post()
                .uri("/create")
                .body(Mono.just(studentDTO), StudentDTO.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Flux<StudentDTO> getStudents() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(StudentDTO.class);
    }

    public Mono<Boolean> updateCourse(StudentDTO studentDTO) {
        return webClient.post()
                .uri("/update")
                .body(Mono.just(studentDTO), StudentDTO.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Boolean> deleteCourse(Long id) {
        return webClient.delete()
                .uri("/" + id.toString())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().equals(HttpStatus.OK))
                .defaultIfEmpty(false);
    }
}
