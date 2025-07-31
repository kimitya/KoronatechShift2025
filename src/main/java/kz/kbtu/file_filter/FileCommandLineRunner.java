package kz.kbtu.file_filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FileCommandLineRunner implements CommandLineRunner {

    @Autowired
    private FileService fileService;

    @Autowired
    private Statistics statistics;

    private boolean shortStat = false;
    private boolean fullStat = false;
    private boolean appendMode = false;
    private String prefix = "";
    private String path = "./"; // текущая папка
    private List<String> files = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.err.println("Не указаны входные файлы!");
            return;
        }

        try {
            parseArguments(args);

            if (!shortStat && !fullStat) {
                shortStat = true;
            }

            statistics.setStat(fullStat);
            statistics.setAppendMode(appendMode);

            if (files.isEmpty()) {
                System.err.println("Не указаны файлы для обработки!");
                return;
            }

            fileService.processFiles(files, prefix, path);
            statistics.printStatistics(shortStat, fullStat);

        } catch (Exception e) {
            System.err.println("Ошибка при обработке: " + e.getMessage());
        }
    }

    private void parseArguments(String... args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-s":
                    case "--short":
                        shortStat = true;
                        break;
                    case "-f":
                    case "--full":
                        fullStat = true;
                        break;
                    case "-o":
                    case "--output":
                        if (i + 1 < args.length) {
                            path = args[++i];
                        } else {
                            System.err.println("Не указан путь для опции -o");
                        }
                        break;
                    case "-p":
                    case "--prefix":
                        if (i + 1 < args.length) {
                            prefix = args[++i];
                        } else {
                            System.err.println("Не указан префикс для опции -p");
                        }
                        break;
                    case "-a":
                    case "--append":
                        appendMode = true;
                        break;
                    default:
                        System.err.println("Неизвестная опция: " + arg);
                        break;
                }
            } else {
                // остаток-имя файлов
                files.add(arg);
            }
        }
    }
}