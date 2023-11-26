package com.example.application;

import com.example.application.views.course.CourseDTO;
import com.example.application.views.student.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.channels.MembershipKey;

@Service
public class CourseService {

    private final WebClient webClient;

    @Autowired
    public CourseService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090").build();
    }

    public Flux<CourseDTO> getCourses() {
        return webClient.get()
                .uri("/courses")
                .retrieve()
                .bodyToFlux(CourseDTO.class);
    }

    public Mono<Boolean> addCourse(CourseDTO course) {
        return webClient.post()
                .uri("/courses/create")
                .body(Mono.just(course), CourseDTO.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Boolean> updateCourse(CourseDTO updatedCourseDTO) {
        return webClient.post()
                .uri("/courses/update")
                .body(Mono.just(updatedCourseDTO), CourseDTO.class)
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
                .uri("/courses/" + id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().equals(HttpStatus.OK))
                .defaultIfEmpty(false);
    }



}
