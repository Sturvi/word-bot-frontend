package com.example.application.views.course.addcourse;

import com.example.application.CourseService;
import com.example.application.views.MainLayout;
import com.example.application.views.course.CourseDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dərsi əlavə etmək")
@Route(value = "courses/add-course", layout = MainLayout.class)
public class AddCoursePage extends VerticalLayout {

    private CourseService courseService;
    private TextField courseCode = new TextField("Dərsin kodu");
    private TextField courseName = new TextField("Dərsin adı");
    private NumberField classNo = new NumberField("Sinif nömrəsi");
    private TextField teacherFirstName = new TextField("Müəllimin adı");
    private TextField teacherLastName = new TextField("Müəllimin soyadı");

    public AddCoursePage(CourseService courseService) {
        this.courseService = courseService;

        configureFields();

        Button saveButton = new Button("Dərsi əlavə et", event -> addCourse());

        // Добавление компонентов на страницу
        add(new H1("Yeni dərsi əlavə et"), courseCode, courseName, classNo, teacherFirstName, teacherLastName, saveButton);
    }

    private void configureFields() {
        courseCode.setPattern("[A-Za-z]{3}");
        courseCode.setErrorMessage("3 hərifdən ibarət olmalıdır");
        courseCode.setRequiredIndicatorVisible(true);

        courseName.setPattern("^[\\p{L}\\p{N}\\s',.-]{1,30}$");
        courseName.setErrorMessage("Ad doğru yazılmayıb");
        courseName.setRequiredIndicatorVisible(true);

        classNo.setMin(1);
        classNo.setMax(99);
        classNo.setErrorMessage("Sinifin nömrəsi 1-dən 99-za qədər ola bilər");
        classNo.setRequiredIndicatorVisible(true);

        teacherFirstName.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        teacherFirstName.setErrorMessage("Ad doğru yazılmayıb");
        teacherFirstName.setRequiredIndicatorVisible(true);

        teacherLastName.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        teacherLastName.setErrorMessage("Soyad doğru yazılmayıb");
        teacherLastName.setRequiredIndicatorVisible(true);

    }

    private void addCourse() {
        if (!courseCode.isInvalid() && !classNo.isInvalid() &&
                !teacherFirstName.isInvalid() && !teacherLastName.isInvalid()) {
            CourseDTO courseDTO = new CourseDTO(null, courseCode.getValue(), courseName.getValue(),
                    classNo.getValue() != null ? classNo.getValue().intValue() : 0,
                    teacherFirstName.getValue(), teacherLastName.getValue());

            Boolean isAdded = courseService.addCourse(courseDTO).block();

            if (Boolean.TRUE.equals(isAdded)) {
                UI.getCurrent().access(() -> UI.getCurrent().navigate("/courses"));
            } else {
                Notification.show("Kurs əlavə edərkən xəta baş verdi.", 3000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Validation Error", 3000, Notification.Position.MIDDLE);
        }
    }
}
