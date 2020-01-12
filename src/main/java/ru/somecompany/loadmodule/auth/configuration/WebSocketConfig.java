package ru.somecompany.loadmodule.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ru.somecompany.loadmodule.projects.components.WebSocketProjectRunHandler;

@Configuration
@EnableWebSocket
@Service
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    WebSocketProjectRunHandler webSocketProjectRunHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler( webSocketProjectRunHandler, "/projects/websocketrun");
    }
}
