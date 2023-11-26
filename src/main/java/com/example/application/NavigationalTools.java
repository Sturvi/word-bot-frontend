package com.example.application;

import com.vaadin.flow.component.UI;

public interface NavigationalTools {

    default void reloadPage() {
        UI.getCurrent().access(() -> UI.getCurrent().getPage().reload());
    }
}
