package br.com.pasteldahora.notification.application.port.out;

public interface EmailSender {
    void send(MessageCommand messageCommand);
}
