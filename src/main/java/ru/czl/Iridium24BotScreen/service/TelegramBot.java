package ru.czl.Iridium24BotScreen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.czl.Iridium24BotScreen.config.BotConfig;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    static final String HELP_TEXT = "Бот создан для проверки баланса сервисов Iridium.\n\n" +
            "Введите /balance для проверки баланса";

    static final String ERROR_TEXT= "Error occurred: ";

    String filename = (String.valueOf(LocalDate.now()));

    public TelegramBot(BotConfig config) {
        this.config=config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/balance", "Показать баланс всех карт"));
        listofCommands.add(new BotCommand("/help", "Помощь"));
        try{
            this.execute(new SetMyCommands(listofCommands,new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotname();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/balance":
                    //registerUser(update.getMessage());
                    starCommandReceived(chatId, update.getMessage().getChat().getFirstName());

                    break;

                case "/help":

                    prepareAndSendMessage(chatId, HELP_TEXT);
                    break;

                default:

                    prepareAndSendMessage(chatId, "Команда не поддерживается, введите: /help");
            }
        }
    }

    private void starCommandReceived(long chatId, String name) { // Ответ на старт
        String answer= "Добро пожаловать, "+name+". Я бот который поможет узнать баланс сервисов Iridium";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
        screenshot(filename);
        sendImageUploadingAFile(chatId);

    }

    private void sendMessage(long chatId, String textToSend) { // ответ на закрепленные комады
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());

        }
    }

    public void sendImageUploadingAFile(long chatId) { // отправка скриншота
        String img = "C:\\jdk-15.0.2\\IdeaProjects\\Iridium24BotScreen\\"+filename+".jpg";
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(String.valueOf(chatId));
        // Set the photo file as a new photo (You can also use InputStream with a constructor overload)
        sendPhotoRequest.setPhoto(new InputFile(new File(img)));
        try {
            // Execute the method
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    static { // работает

        System.setProperty("java.awt.headless", "false");
    }
    public void screenshot(String filename) { // подготовка скриншота
       try {
           Robot robot = new Robot();
            String format = "jpg";
           String filenameF = filename + "." + format;

           Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
           BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
           ImageIO.write(screenFullImage, format, new File(filenameF));

           System.out.println("Скриншот сохранён!");
        } catch (Exception ex) {
            System.err.println(ex);
       }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) { // текстовый ответ
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());

        }
    }



}
