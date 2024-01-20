package ru.cft.shift.nekrasovgleb;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FilesContentFilter {
    private final List<String> inputFilesNames;
    private final String outputFilesNamesPrefix;
    private final String pathToOutputFiles;
    private final boolean addToFiles;
    private final boolean fullStatistics;
    private final ElementsStatistics<Long> intsStatistics;
    private final ElementsStatistics<Double> floatsStatistics;
    private final ElementsStatistics<Integer> stringsStatistics;

    public FilesContentFilter (List<String> inputFilesNames, String outputFilesNamesPrefix, String pathToOutputFiles,
                               boolean addToFiles, boolean fullStatistics) {
        this.inputFilesNames = inputFilesNames;
        this.outputFilesNamesPrefix = outputFilesNamesPrefix;
        this.pathToOutputFiles = pathToOutputFiles;
        this.addToFiles = addToFiles;
        this.fullStatistics = fullStatistics;
        this.intsStatistics = new ElementsStatistics<>();
        this.floatsStatistics = new ElementsStatistics<>();
        this.stringsStatistics = new ElementsStatistics<>();
    }

    public void filterFiles() {
        if (inputFilesNames.size() == 0) {
            System.out.println("При запуске программы нужно указать хотя бы один входной файл с данными.");
            return;
        }

        List<LineIterator> lineIterators = new ArrayList<>();

        for (String inputFileName : inputFilesNames) {
            try {
                lineIterators.add(FileUtils.lineIterator(new File(inputFileName), "UTF-8"));
            } catch (IOException e) {
                System.out.println("Данные из файла " + inputFileName
                        + " не будут добавлены в выходной файл, так как файл не удалось открыть.");
            }
        }

        if (lineIterators.size() == 0) {
            System.out.println("Не удалось открыть ни один из входных файлов.");
            return;
        }

        String fullPath = System.getProperty("user.dir") + pathToOutputFiles;
        File intsFile = new File(FilenameUtils.concat(fullPath, outputFilesNamesPrefix + "integers.txt"));
        File floatsFile = new File(FilenameUtils.concat(fullPath, outputFilesNamesPrefix + "floats.txt"));
        File stringsFile = new File(FilenameUtils.concat(fullPath, outputFilesNamesPrefix + "strings.txt"));

        try {
            boolean isEndOfFiles = false;

            while (!isEndOfFiles) {
                isEndOfFiles = true;

                for (LineIterator lineIterator : lineIterators) {
                    if (lineIterator.hasNext()) {
                        isEndOfFiles = false;
                        String nextLine = lineIterator.nextLine();

                        if (nextLine.trim().length() == 0) {
                            continue;
                        }

                        try {
                            long currentInt = Long.parseLong(nextLine.trim());

                            writeLineToFile(currentInt, intsStatistics.getCount(), intsFile, addToFiles);

                            if (fullStatistics) {
                                intsStatistics.updateAll(currentInt);
                            } else {
                                intsStatistics.updateCount();
                            }
                        } catch (NumberFormatException e1) {
                            try {
                                double currentFloat = Double.parseDouble(nextLine.trim());

                                writeLineToFile(currentFloat, floatsStatistics.getCount(), floatsFile, addToFiles);

                                if (fullStatistics) {
                                    floatsStatistics.updateAll(currentFloat);
                                } else {
                                    floatsStatistics.updateCount();
                                }
                            } catch (NumberFormatException e2) {
                                writeLineToFile(nextLine, stringsStatistics.getCount(), stringsFile, addToFiles);

                                if (fullStatistics) {
                                    int currentStringLength = nextLine.length();
                                    stringsStatistics.updateAll(currentStringLength);
                                } else {
                                    stringsStatistics.updateCount();
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            for (LineIterator lineIterator : lineIterators) {
                try {
                    lineIterator.close();
                } catch (IOException e) {
                    System.out.println("Не получилось закрыть все файлы после их чтения");
                }
            }
        }
    }

    private static void writeLineToFile(Object currentObject, int objectsCount, File outputFile, boolean addToFiles) {
        String lineSeparator = System.lineSeparator();

        if (objectsCount == 0 && !addToFiles) {
            FileUtils.deleteQuietly(outputFile);
            lineSeparator = "";
        }

        if (objectsCount == 0 && addToFiles && !outputFile.exists()) {
            lineSeparator = "";
        }

        try {
            FileUtils.writeStringToFile(outputFile, lineSeparator + currentObject, "UTF-8", true);
        } catch (IOException e) {
            System.out.println("Не удалось записать часть данных в файл " + outputFile);
        }
    }

    public void printStatistics() {
        System.out.println("Количество целых чисел: " + intsStatistics.getCount());

        if (fullStatistics && intsStatistics.getCount() != 0) {
            BigDecimal sum = new BigDecimal(intsStatistics.getSum());
            BigDecimal count = new BigDecimal(intsStatistics.getCount());
            BigDecimal average = sum.divide(count, 2, RoundingMode.HALF_UP);
            System.out.println("Минимальное целое число: " + intsStatistics.getMin());
            System.out.println("Максимальное целое число: " + intsStatistics.getMax());
            System.out.println("Сумма целых чисел: " + intsStatistics.getSum());
            System.out.println("Среднее целых чисел: " + average.toPlainString());
        }

        System.out.println("Количество вещественных чисел: " + floatsStatistics.getCount());

        if (fullStatistics && floatsStatistics.getCount() != 0) {
            System.out.println("Минимальное вещественное число: " + floatsStatistics.getMin());
            System.out.println("Максимальное вещественное число: " + floatsStatistics.getMax());
            System.out.println("Сумма вещественных чисел: " + floatsStatistics.getSum());
            System.out.println("Среднее вещественных чисел: " + floatsStatistics.getSum() / floatsStatistics.getCount());
        }

        System.out.println("Количество строк: " + stringsStatistics.getCount());

        if (fullStatistics && stringsStatistics.getCount() != 0) {
            System.out.println("Минимальная длина строки: " + stringsStatistics.getMin());
            System.out.println("Максимальная длина строки: " + stringsStatistics.getMax());
        }
    }
}
