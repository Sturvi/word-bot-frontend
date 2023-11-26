package com.example.application.views.exam;

import com.example.application.*;
import com.example.application.views.MainLayout;
import com.example.application.views.course.CourseDTO;
import com.example.application.views.student.StudentDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@PageTitle("İmtahanlar")
@Route(value = "exams", layout = MainLayout.class)
@Slf4j
public class ExamPage extends VerticalLayout implements NavigationalTools {
    private final AuthControlService authControlService;
    private final ExamService examService;
    private final CourseService courseService;
    private final StudentService studentService;


    public ExamPage(AuthControlService authControlService, ExamService examService, CourseService courseService, StudentService studentService) {
        this.authControlService = authControlService;
        this.examService = examService;
        this.courseService = courseService;
        this.studentService = studentService;
        authControlService.check();

        H1 title = new H1("İmtahan nəticələri");

        Button addButton = new Button("Yeni imtahan");
        addButton.addClickListener(event -> addExam());

        Grid<ExamDTO> examGrid = new Grid<>();

        examGrid.addColumn(ExamDTO::getStudent)
                .setHeader("Şagird")
                .setSortable(true)
                .setFlexGrow(3);

        examGrid.addColumn(ExamDTO::getCourse)
                .setHeader("Dərsin adı")
                .setSortable(true)
                .setFlexGrow(3);

        examGrid.addColumn(ExamDTO::getExamDate)
                .setHeader("Tarix")
                .setSortable(true)
                .setFlexGrow(3);

        examGrid.addColumn(ExamDTO::getScore)
                .setHeader("Qiymət")
                .setSortable(true)
                .setFlexGrow(1);

        examGrid.addComponentColumn(studentDTO -> {
            Button editButton = new Button("Redaktə et");
            editButton.addClickListener(event -> editExam(studentDTO));
            editButton.setWidth("100%");
            return editButton;
        }).setFlexGrow(3);

        examGrid.addComponentColumn(studentDTO -> {
            Button deleteButton = new Button("Sil");
            deleteButton.addClickListener(event -> deleteCourse(studentDTO));
            return deleteButton;
        }).setFlexGrow(1);


        add(title, addButton, examGrid);

        UI ui = UI.getCurrent();

        examService.getExams()
                .collectList()
                .subscribe(examDTOS -> {
                    ui.access(() -> {
                        examGrid.setItems(examDTOS);
                        ui.push();
                    });
                });


    }

    private void editExam(ExamDTO studentDTO) {
        configureExamDialog(studentDTO);
    }

    private void addExam() {
        configureExamDialog(null);
    }

    private void configureExamDialog(ExamDTO examDTO) {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("75%");

        ComboBox<StudentDTO> studentComboBox = new ComboBox<>("Şagird");
        studentComboBox.setWidthFull();
        studentComboBox.setRequiredIndicatorVisible(true);

        ComboBox<CourseDTO> courseComboBox = new ComboBox<>("Fən");
        courseComboBox.setWidthFull();
        courseComboBox.setRequiredIndicatorVisible(true);

        DatePicker examDatePicker = new DatePicker("İmtahan tarixi");
        examDatePicker.setWidthFull();
        examDatePicker.setRequiredIndicatorVisible(true);

        IntegerField scoreField = new IntegerField("Qiymət");
        scoreField.setMin(1);
        scoreField.setMax(10);
        scoreField.setErrorMessage("Qiymət 1-dən 10-a qədər ola bilər");
        scoreField.setRequiredIndicatorVisible(true);
        scoreField.setWidthFull();


        UI ui = UI.getCurrent();

        studentService.getStudents()
                .collectList()
                .subscribe(students -> {
                    ui.access(() -> {
                        studentComboBox.setItems(students);
                        studentComboBox.setItemLabelGenerator(studentDTO -> studentDTO.getStudentFirstName() + " " + studentDTO.getStudentLastName() + ". Sinif: " + studentDTO.getClassNo());

                        // Установка значения для ComboBox, если examDTO не равно null
                        if (examDTO != null) {
                            studentComboBox.setValue(examDTO.getStudent());
                        }
                    });
                });

        courseService.getCourses()
                .collectList()
                .subscribe(courses -> {
                    ui.access(() -> {
                        courseComboBox.setItems(courses);
                        courseComboBox.setItemLabelGenerator(CourseDTO::getCourseName);

                        // Установка значения для ComboBox, если examDTO не равно null
                        if (examDTO != null) {
                            courseComboBox.setValue(examDTO.getCourse());
                        }
                    });
                });


        if (examDTO != null) {
            examDatePicker.setValue(examDTO.getExamDate());
            scoreField.setValue(examDTO.getScore());
        }

        Button saveButton = new Button(examDTO == null ? "Əlavə et" : "Yenilə", event -> {
            ExamDTO newExamDTO = ExamDTO.builder()
                    .id(examDTO == null ? null : examDTO.getId())
                    .student(studentComboBox.getValue())
                    .course(courseComboBox.getValue())
                    .examDate(examDatePicker.getValue())
                    .score(scoreField.getValue())
                    .build();

            Boolean isSaved = (examDTO == null ? examService.addExam(newExamDTO) : examService.updateExam(newExamDTO)).block();

            if (isSaved.equals(Boolean.TRUE)) {
                reloadPage();
            } else {
                Notification.show("Xəta baş verdi.", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button("Ləğv et", event -> editDialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);


        VerticalLayout layout = new VerticalLayout(studentComboBox, courseComboBox, examDatePicker, scoreField, buttonsLayout);
        editDialog.add(layout);




        editDialog.open();
    }

    private void deleteCourse(ExamDTO examDTO) {
        Boolean isDeleted = examService.deleteExam(examDTO.getId()).block();

        if (Boolean.TRUE.equals(isDeleted)) {
            reloadPage();
        } else {
            Notification.show("İmtahanı silərkən xəta baş verdi.", 3000, Notification.Position.MIDDLE);
        }

    }
}
