package com.example.application.views.course;

import com.example.application.AuthControlService;
import com.example.application.CourseService;
import com.example.application.NavigationalTools;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import com.vaadin.flow.component.html.H1;

@PageTitle("Dərslər")
@Route(value = "courses", layout = MainLayout.class)
@Slf4j
public class CoursePage extends VerticalLayout implements NavigationalTools {

    private CourseService courseService;
    private final AuthControlService authControlService;

    public CoursePage(CourseService courseService, AuthControlService authControlService) {
        this.courseService = courseService;
        this.authControlService = authControlService;
        authControlService.check();

        // Заголовок страницы
        H1 title = new H1("Dərslər");

        // Кнопка "Добавить курс"
        Button addButton = new Button("Yeni dərs");
        addButton.addClickListener(event -> {
            UI.getCurrent().access(() -> UI.getCurrent().navigate("courses/add-course"));
        });

        Grid<CourseDTO> courseGrid = new Grid<>();

// Настройка колонок и их заголовков
        Grid.Column<CourseDTO> courseCodeColumn = courseGrid.addColumn(CourseDTO::getCourseCode).setHeader("Kod").setSortable(true);
        Grid.Column<CourseDTO> courseNameColumn = courseGrid.addColumn(CourseDTO::getCourseName).setHeader("Dərsin adı").setSortable(true);
        Grid.Column<CourseDTO> classNoColumn = courseGrid.addColumn(CourseDTO::getClassNo).setHeader("Sinif").setSortable(true);
        Grid.Column<CourseDTO> teacherFirstNameColumn = courseGrid.addColumn(CourseDTO::getTeacherFirstName).setHeader("Müəllimin adı").setSortable(true);
        Grid.Column<CourseDTO> teacherLastNameColumn = courseGrid.addColumn(CourseDTO::getTeacherLastName).setHeader("Müəllimin soyadı").setSortable(true);

// Установка гибкости роста колонок, чтобы они занимали все доступное пространство
        courseCodeColumn.setFlexGrow(1);
        courseNameColumn.setFlexGrow(4);
        classNoColumn.setFlexGrow(1);
        teacherFirstNameColumn.setFlexGrow(4);
        teacherLastNameColumn.setFlexGrow(4);

// Добавляем Grid к компоненту макета
        add(courseGrid);

        // Колонка для кнопки редактирования
        courseGrid.addComponentColumn(courseDTO -> {
            Button editButton = new Button("Redaktə et");
            editButton.addClickListener(event -> editCourse(courseDTO));
            editButton.setWidth("100%");
            return editButton;
        }).setFlexGrow(5);

        // Колонка для кнопки удаления
        courseGrid.addComponentColumn(courseDTO -> {
            Button deleteButton = new Button("Sil");
            deleteButton.addClickListener(event -> deleteCourse(courseDTO));
            return deleteButton;
        }).setFlexGrow(1);

        // Добавление компонентов на страницу
        add(title, addButton, courseGrid);

        UI ui = UI.getCurrent();

        courseService.getCourses()
                .collectList()
                .subscribe(courseDTOS -> {
                    ui.access(() -> {
                        courseGrid.setItems(courseDTOS);
                        ui.push(); // Если push-режим включен
                    });
                });

    }

    private void editCourse(CourseDTO courseDTO) {
        Dialog editDialog = new Dialog();
        editDialog.setWidth("75%"); // Устанавливаем ширину диалога

        // Создаем поля для ввода данных о курсе
        TextField courseCodeField = new TextField("Kod");
        courseCodeField.setValue(courseDTO.getCourseCode());
        courseCodeField.setPattern("[A-Za-z]{3}");
        courseCodeField.setErrorMessage("3 hərifdən ibarət olmalıdır");
        courseCodeField.setRequiredIndicatorVisible(true);
        courseCodeField.setWidthFull(); // Растягиваем на всю ширину

        TextField courseNameField = new TextField("Dərsin adı");
        courseNameField.setPattern("^[\\p{L}\\p{N}\\s',.-]{1,30}$");
        courseNameField.setErrorMessage("Ad doğru yazılmayıb");
        courseNameField.setRequiredIndicatorVisible(true);
        courseNameField.setValue(courseDTO.getCourseName());
        courseNameField.setWidthFull(); // Растягиваем на всю ширину

        NumberField classNoField = new NumberField("Sinif");
        classNoField.setMin(1);
        classNoField.setMax(99);
        classNoField.setErrorMessage("Sinifin nömrəsi 1-dən 99-za qədər ola bilər");
        classNoField.setRequiredIndicatorVisible(true);
        classNoField.setValue(Double.valueOf(courseDTO.getClassNo()));
        classNoField.setWidthFull(); // Растягиваем на всю ширину

        TextField teacherFirstNameField = new TextField("Müəllimin adı");
        teacherFirstNameField.setValue(courseDTO.getTeacherFirstName());
        teacherFirstNameField.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        teacherFirstNameField.setErrorMessage("Ad doğru yazılmayıb");
        teacherFirstNameField.setRequiredIndicatorVisible(true);
        teacherFirstNameField.setWidthFull(); // Растягиваем на всю ширину

        TextField teacherLastNameField = new TextField("Müəllimin soyadı");
        teacherLastNameField.setPattern("[\\p{L}'-]{2,20}(?: [\\p{L}'-]{2,20})?");
        teacherLastNameField.setErrorMessage("Soyad doğru yazılmayıb");
        teacherLastNameField.setRequiredIndicatorVisible(true);
        teacherLastNameField.setValue(courseDTO.getTeacherLastName());
        teacherLastNameField.setWidthFull(); // Растягиваем на всю ширину

        // Кнопки для действий
        Button saveButton = new Button("Yenilə", event -> {
            String courseCode = courseCodeField.getValue();
            String courseName = courseNameField.getValue();
            Integer classNo = classNoField.getValue().intValue();
            String teacherFirstName = teacherFirstNameField.getValue();
            String teacherLastName = teacherLastNameField.getValue();

            // Обновление объекта DTO
            courseDTO.setCourseCode(courseCode);
            courseDTO.setCourseName(courseName);
            courseDTO.setClassNo(classNo); // Предполагаем, что classNo - это целое число
            courseDTO.setTeacherFirstName(teacherFirstName);
            courseDTO.setTeacherLastName(teacherLastName);

            saveCourseChanges(courseDTO);
        });

        Button cancelButton = new Button("Ləğv et", event -> editDialog.close());

        // Горизонтальный макет для кнопок
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setWidthFull(); // Устанавливаем ширину во всю доступную ширину
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Выравниваем кнопки по центру

        // Вертикальный макет для всех элементов
        VerticalLayout dialogLayout = new VerticalLayout(courseCodeField, courseNameField, classNoField, teacherFirstNameField, teacherLastNameField, buttonsLayout);
        dialogLayout.setWidthFull(); // Устанавливаем ширину макета во всю доступную ширину

        editDialog.add(dialogLayout);

        editDialog.open();
    }

    private void saveCourseChanges(CourseDTO updatedCourseDTO) {
        if (!updatedCourseDTO.getCourseCode().isEmpty() &&
                updatedCourseDTO.getClassNo() != 0 &&
                !updatedCourseDTO.getTeacherFirstName().isEmpty() &&
                !updatedCourseDTO.getTeacherLastName().isEmpty()) {

            Boolean isUpdated = courseService.updateCourse(updatedCourseDTO).block();

            if (Boolean.TRUE.equals(isUpdated)) {
                reloadPage();
            } else {
                Notification.show("Kursu yeniləyərkən xəta baş verdi.", 3000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Validation Error", 3000, Notification.Position.MIDDLE);
        }
    }


    private void deleteCourse(CourseDTO courseDTO) {
        Boolean isDeleted = Boolean.TRUE.equals(courseService.deleteCourse(courseDTO.getId()).block());

        if (Boolean.TRUE.equals(isDeleted)) {
            reloadPage();
        } else {
            Notification.show("Kursu silərkən xəta baş verdi.", 3000, Notification.Position.MIDDLE);
        }
    }
}
