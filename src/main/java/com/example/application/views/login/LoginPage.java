package com.example.application.views.login;

import com.example.application.AuthServiceClient;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Route("login")
@Slf4j

public class LoginPage extends VerticalLayout {

    private final AuthServiceClient authServiceClient;
    private TextField usernameField;
    private PasswordField passwordField;

    public LoginPage(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
        initializeUI();
    }

    private void initializeUI() {
        usernameField = createUsernameField();
        passwordField = createPasswordField();

        add(createTitle(), usernameField, passwordField, createLoginButton());
        configureLayout();
    }

    private H1 createTitle() {
        return new H1("Kabinetə giriş");
    }

    private TextField createUsernameField() {
        TextField usernameField = new TextField("Login");
        usernameField.addKeyPressListener(Key.ENTER, this::onEnterKeyPressed);
        return usernameField;
    }

    private PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField("Şifrə");
        passwordField.addKeyPressListener(Key.ENTER, this::onEnterKeyPressed);
        return passwordField;
    }


    private Button createLoginButton() {
        Button loginButton = new Button("Giriş");
        loginButton.addClickListener(event -> authenticateUser());
        return loginButton;
    }

    private void authenticateUser() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        log.info("Попытка входа пользователя: " + username);
        AuthUserDto authUserDto = AuthUserDto.create(username, password);

        authServiceClient.authenticateUser(authUserDto)
                .subscribe(response -> {
                    log.info("Пользователь успешно аутентифицирован: " + username);

                    getUI().ifPresent(ui -> ui.access(() -> {
                        // Сохранение токена в Local Storage
                        ui.getPage().executeJs("localStorage.setItem('authToken', $0)", response);

                        // Перенаправление на домашнюю страницу
                        ui.navigate("courses");
                    }));
                }, error -> {
                    log.error("Ошибка аутентификации для пользователя: " + username);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show("Yalnış login və ya şifrə.");
                    }));
                });
    }


    private void configureLayout() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private void onEnterKeyPressed(KeyPressEvent event) {
        if (event.getKey().equals(Key.ENTER)) {
            authenticateUser();
        }
    }

}
