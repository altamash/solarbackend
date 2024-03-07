//package com.solar.api.configuration.websocket;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.converter.MessageConverter;
//import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
//import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
//
//import javax.validation.constraints.NotNull;
//import java.util.List;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
//        stompEndpointRegistry.addEndpoint("/websocket-example").setAllowedOrigins("https://pgfe.azurewebsites.net")
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic");
//        registry.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    public void configureWebSocketTransport(@NotNull WebSocketTransportRegistration webSocketTransportRegistration) {
//
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
//
//    }
//
//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration channelRegistration) {
//
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
//
//    }
//
//    @Override
//    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {
//
//    }
//
//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> list) {
//        return false;
//    }
//}
