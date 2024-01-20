package ru.cft.shift.nekrasovgleb;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class FilesContentFilterApp
{
    public static void main(String[] args) {
        Arguments arguments = new Arguments();

        JCommander argumentsParser = JCommander.newBuilder()
                .addObject(arguments)
                .build();

        argumentsParser.setProgramName("java -jar files-content-filter-util.jar");

        try {
            argumentsParser.parse(args);

            if ((arguments.isFullStatistics && arguments.isShortStatistics) ||
                    (!arguments.isFullStatistics && !arguments.isShortStatistics)) {
                System.out.println("При запуске программы нужно выбрать один режим сбора статистики.");
                argumentsParser.usage();
                return;
            }

            if (!arguments.pathToOutputFiles.equals("") &&
                    (!arguments.pathToOutputFiles.startsWith("/") || !arguments.pathToOutputFiles.startsWith("\\"))) {
                System.out.println("При запуске программы указан не корректный путь к выходным файлам. " +
                        "Путь должен начинаться с символа \"/\" или \"\\\".");
                argumentsParser.usage();
                return;
            }

            FilesContentFilter filter = new FilesContentFilter(arguments.inputFiles, arguments.outputFilesNamesPrefix,
                    arguments.pathToOutputFiles, arguments.addToFiles, arguments.isFullStatistics);

            filter.filterFiles();
            filter.printStatistics();
        } catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            argumentsParser.usage();
        }
    }
}
