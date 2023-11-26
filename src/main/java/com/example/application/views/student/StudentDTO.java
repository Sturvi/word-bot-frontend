package com.example.application.views.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StudentDTO {

    private Integer studentID; // number(5,0)
    private String studentFirstName; // varchar(30)
    private String studentLastName; // varchar(30)
    private Integer classNo; // number(2,0)


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDTO that = (StudentDTO) o;
        return Objects.equals(studentID, that.studentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID);
    }

    @Override
    public String toString() {
        return studentFirstName + " " + studentLastName;
    }
}
