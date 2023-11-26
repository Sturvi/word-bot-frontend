package com.example.application;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@JsModule("frontend://token.js")
public class AuthControlService {

    private final AuthServiceClient authServiceClient;

    public void check() {
        UI currentUI = UI.getCurrent();
        currentUI.getPage().executeJs(
                "try {" +
                        "  return window.getAuthToken();" +
                        "} catch (e) {" +
                        "  console.error('Ошибка в JavaScript:', e);" +
                        "  return 'error';" +
                        "}"
        ).then(String.class, token -> {
            if ("error".equals(token)) {
                currentUI.access(() -> currentUI.navigate("login"));
            } else {
                log.info("token: " + token);
                authServiceClient.validateToken(token)
                        .subscribe(isValid -> {
                            if (!isValid) {
                                currentUI.access(() -> currentUI.navigate("login"));
                            }
                        });
            }
        });
    }
}
