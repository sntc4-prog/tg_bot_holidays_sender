package ru.sntc.tg_bot_holidays.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sntc.tg_bot_holidays.config.BotConfig;
import ru.sntc.tg_bot_holidays.model.Holiday;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {

        this.config = config;
    }

    @Override
    public String getBotUsername() {

        return config.getBotName();
    }

    @Override
    public String getBotToken() {

        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            SendMessage messageMain = new SendMessage();
            messageMain.enableMarkdown(true);


            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId);
                    break;
                case "Какой сегодня праздник?":
                    startCommandReceived(chatId);
                    break;
                default:
                    sendMsg(chatId);
                    break;
            }


        }
    }

    public void sendMsg(final long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите пункт меню: ");
        //Клавиатура
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
// Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        KeyboardButton keyboardButton1 = new KeyboardButton("Какой сегодня праздник?");
        keyboardFirstRow.add(keyboardButton1);
        // Вторая строчка клавиатуры
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        // Добавляем кнопки во вторую строчку клавиатуры
//        keyboardSecondRow.add(new KeyboardButton(""));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    private void startCommandReceived(final long chatId) {
        sendHolidays(chatId);
    }

    private void sendHolidays(long chatId) {

        List<Holiday> holidays = getHolidays();

        for (Holiday holiday : holidays) {

            SendMessage message = new SendMessage();
            message.enableHtml(true);
            message.setChatId(String.valueOf(chatId));
            message.disableWebPagePreview();
            String mess = String.format("Сегодня отмечают: <a href=\"%s\"><b>%s</b></a>!",
                    holiday.getHolidaysHref(),
                    holiday.getHolidaysName());
            message.setText(mess);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Holiday> getHolidays() {

        List<Holiday> holidays = new ArrayList<>();
        String holidaysUrl = "https://my-calend.ru/holidays";
        try {
            Document doc = Jsoup.connect(holidaysUrl).get();
            Elements rawHolidays = doc.select(".holidays-items li");
            for (var element : rawHolidays) {
                if (element.select("a").hasText()) {
                    String holidaysName = element.select("a").text();//Название праздника
                    String holidaysHref = element.select("a").attr("href");//Ссылка на описание праздника
                    holidays.add(new Holiday(holidaysName, holidaysHref));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return holidays;
    }


}
