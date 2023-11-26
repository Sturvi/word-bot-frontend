package com.example.application.views.exam;

import com.example.application.views.course.CourseDTO;
import com.example.application.views.student.StudentDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Builder
public class ExamDTO {

    private Long id;
    private StudentDTO student;
    private CourseDTO course;
    private LocalDate examDate;
    private Integer score;


}