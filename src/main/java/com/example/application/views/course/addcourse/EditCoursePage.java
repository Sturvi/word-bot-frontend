package com.example.application.views.course.addcourse;

import com.example.application.CourseService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@PageTitle("Dərsi redaktə etmək")
@Route(value = "courses/edit-course", layout = MainLayout.class)
@Slf4j
public class EditCoursePage extends VerticalLayout {

    private CourseService courseService;
    private TextField courseCode = new TextField("Dərsin kodu");
    private TextField courseName = new TextField("Dərsin adı");
    private NumberField classNo = new NumberField("Sinif nömrəsi");
    private TextField teacherFirstName = new TextField("Müəllimin adı");
    private TextField teacherLastName = new TextField("Müəllimin soyadı");

    public EditCoursePage(CourseService courseService, @OptionalParameter String courseId) {
        this.courseService = courseService;


    }
}
