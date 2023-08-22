package ru.czl.Iridium24BotScreen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // наличие методов с автозапуском
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String botname;

    @Value("${bot.token}")
    String token;

    @Value("${bot.owner}")
    Long ownerId;
}
