package ru.sntc.tg_bot_holidays.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Holiday {
    //Название праздника
    private String holidaysName;
    //Ссылка на статью о празднике
    private String holidaysHref;

}
