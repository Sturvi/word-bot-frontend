package com.example.application.views.helloworld;

import com.example.application.AuthControlService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import lombok.extern.slf4j.Slf4j;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@Slf4j
public class HelloWorldView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    private final AuthControlService authControlService;

    public HelloWorldView(AuthControlService authControlService) {
        this.authControlService = authControlService;

        authControlService.check();

        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }

}
