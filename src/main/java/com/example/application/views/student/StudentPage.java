package com.example.application.views.student;

import com.example.application.AuthControlService;
import com.example.application.NavigationalTools;
import com.example.application.StudentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import lombok.extern.slf4j.Slf4j;

@PageTitle("Şagirdlər")
@Route(value = "students", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Slf4j
public class StudentPage extends VerticalLayout implements NavigationalTools {

    private final AuthControlService authControlService;
    private final StudentService studentService;


    public StudentPage(AuthControlService authControlService, StudentService studentService) {
        this.authControlService = authControlService;
        this.studentService = studentService;
        authControlService.check();

        H1 title = new H1("Şagirdlər");

        Button addButton = new Button("Yeni şagird");
        addButton.addClickListener(event -> addStudent());

        Grid<StudentDTO> studentGrid = new Grid<>();

        studentGrid.addColumn(StudentDTO::getStudentFirstName)
                .setHeader("Ad")
                .setSortable(true)
                .setFlexGrow(3);
        studentGrid.addColumn(StudentDTO::getStudentLastName)
                .setHeader("Soyad")
                .setSortable(true)
                .setFlexGrow(3);
        studentGrid.addColumn(StudentDTO::getClassNo)
                .setHeader("Sinif nömrəsi")
                .setSortable(true)
                .setFlexGrow(1);

        studentGrid.addComponentColumn(studentDTO -> {
            Button editButton = new Button("Redaktə et");
            editButton.addClickListener(event -> editStudent(studentDTO));
            editButton.setWidth("100%");
            return editButton;
        }).setFlexGrow(3);

        studentGrid.addComponentColumn(studentDTO -> {
            Button deleteButton = new Button("Sil");
            deleteButton.addClickListener(event -> deleteCourse(studentDTO));
            return deleteButton;
        }).setFlexGrow(1);

        add(title, addButton, studentGrid);

        UI ui = UI.getCurrent();

        studentService.getStudents()
                .collectList()
                .subscribe(studentDTOS -> {
                    ui.access(() -> {
                        studentGrid.setItems(studentDTOS);
                        ui.push();
                    });
                });

    }

    private void configureStudentDialog(StudentDTO studentDTO) {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("75%");

        TextField studentFirstNameField = new TextField("Şagirdin adı");
        studentFirstNameField.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        studentFirstNameField.setErrorMessage("Ad doğru yazılmayıb");
        studentFirstNameField.setRequiredIndicatorVisible(true);
        studentFirstNameField.setWidthFull();

        TextField studentLastNameField = new TextField("Şagirdin soyadı");
        studentLastNameField.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        studentLastNameField.setErrorMessage("Soyad doğru yazılmayıb");
        studentLastNameField.setRequiredIndicatorVisible(true);
        studentLastNameField.setWidthFull();

        NumberField classNoField = new NumberField("Sinif");
        classNoField.setMin(1);
        classNoField.setMax(99);
        classNoField.setErrorMessage("Sinifin nömrəsi 1-dən 99-za qədər ola bilər");
        classNoField.setRequiredIndicatorVisible(true);
        classNoField.setWidthFull();

        // Заполнение полей, если студент существует
        if (studentDTO != null) {
            studentFirstNameField.setValue(studentDTO.getStudentFirstName());
            studentLastNameField.setValue(studentDTO.getStudentLastName());
            classNoField.setValue(studentDTO.getClassNo().doubleValue());
        }

        Button saveButton = new Button(studentDTO == null ? "Əlavə et" : "Yenilə", event -> {
            StudentDTO dto = new StudentDTO(
                    studentDTO == null ? null : studentDTO.getStudentID(),
                    studentFirstNameField.getValue(),
                    studentLastNameField.getValue(),
                    classNoField.getValue().intValue()
            );

            Boolean isSaved = (studentDTO == null ? studentService.addStudent(dto) : studentService.updateCourse(dto)).block();

            if (isSaved.equals(Boolean.TRUE)) {
                reloadPage();
            } else {
                Notification.show("Xəta baş verdi.", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button("Ləğv et", event -> editDialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(studentFirstNameField, studentLastNameField, classNoField, buttonsLayout);
        dialogLayout.setWidthFull();

        editDialog.add(dialogLayout);
        editDialog.open();
    }

    private void addStudent() {
        configureStudentDialog(null);
    }

    private void editStudent(StudentDTO studentDTO) {
        configureStudentDialog(studentDTO);
    }


    private void deleteCourse(StudentDTO studentDTO) {
        Boolean isDeleted = studentService.deleteCourse(studentDTO.getStudentID().longValue()).block();

        if (Boolean.TRUE.equals(isDeleted)) {
            reloadPage();
        } else {
            Notification.show("Şagirdi silərkən xəta baş verdi.", 3000, Notification.Position.MIDDLE);
        }

    }

}
