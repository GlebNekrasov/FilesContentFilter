package ru.cft.shift.nekrasovgleb;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    @Parameter(description = "Входные файлы с данными для обработки.", required = true)
    List<String> inputFiles = new ArrayList<>();

    @Parameter(names = "-o", description = "Путь к директории, в которой будут сохранены файлы с результатами фильтрации.",
            order = 0)
    String pathToOutputFiles = "";

    @Parameter(names = "-p", description = "Префикс для имен файлов с результатами фильтрации.", order = 1)
    String outputFilesNamesPrefix = "";

    @Parameter(names = "-a", description = "Режим добавления новых данных в существующие файлы с результатами фильтрации.",
            order = 2)
    boolean addToFiles;

    @Parameter(names = "-f", description = "Режим сбора полной статистики. При запуске программы нужно обязательно " +
            "указать один из двух режимов сбора статистики - полный или краткий.", order = 3)
    boolean isFullStatistics;

    @Parameter(names = "-s", description = "Режим сбора краткой статистики. При запуске программы нужно обязательно " +
            "указать один из двух режимов сбора статистики - полный или краткий.", order = 4)
    boolean isShortStatistics;
}
