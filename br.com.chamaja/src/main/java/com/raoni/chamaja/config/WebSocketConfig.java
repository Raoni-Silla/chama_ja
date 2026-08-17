package com.raoni.chamaja.config;

import com.raoni.chamaja.seguranca.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    // esse metodo define então a porta de entrada do websocket/stomp (protocolo de mensagens, quem define as regras da comunicação websocket)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat") //define o endereço http inicial para a requisição de handshake
                .setAllowedOriginPatterns("http://localhost:4200");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic"); // gerenciamento das inscrições e roteamento/distribuição das mensagens.
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message,
                                StompHeaderAccessor.class
                        );
                if (StompCommand.CONNECT == accessor.getCommand()){
                    String authorization = accessor.getFirstNativeHeader("Authorization");
                    if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("O token jwt não veio na requisição");
                    }
                    String token = authorization.substring(7);
                    String identificador = jwtService.extrairIdentificador(token);
                    String roleUsuario = jwtService.extrairRole(token);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    identificador,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + roleUsuario))
                            );
                    accessor.setUser(authentication);
                }

                return message;
            }
        });
    }
}
