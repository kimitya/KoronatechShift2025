package kz.kbtu.file_filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

@Service
public class FileService {

    @Autowired
    private Statistics statistics;
    @Autowired
    private DataTypeDetector detector;

    public void processFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            System.err.println("Файл не найден: " + fileName);
            return;
        }
        if (!file.canRead()) {
            System.err.println("Нет доступа к файлу: " + fileName);
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    classifyAndAddData(line);
                } catch (Exception e) {
                    System.err.println("Ошибка обработки строки " + lineNumber +
                            " в файле " + fileName + ": " + e.getMessage());
                }
            }
            System.out.println("Обработан файл: " + fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + fileName);
        } catch (Exception e) {
            System.err.println("Ошибка при чтении файла " + fileName + ": " + e.getMessage());
        }
    }

    private void classifyAndAddData(String line) {
        if (detector.isInteger(line)) {
            statistics.addInt(line);
        } else if (detector.isFloat(line)) {
            statistics.addFloat(line);
        } else {
            statistics.addString(line);
        }
    }

    public void processFiles(List<String> files, String prefix, String path) {
        if (files == null || files.isEmpty()) {
            System.err.println("Список файлов пуст!");
            return;
        }

        validateOutputDirectory(path);

        for (String fileName : files) {
            if (fileName != null && !fileName.trim().isEmpty()) {
                processFile(fileName.trim());
            }
        }

        // создание резов
        try {
            statistics.createFiles(prefix, path);
            System.out.println("Файлы созданы в директории: " + path);
        } catch (Exception e) {
            System.err.println("Ошибка при создании выходных файлов: " + e.getMessage());
        }
    }

    private void validateOutputDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Не удалось создать директорию: " + path);
            }
        } else if (!dir.isDirectory()) {
            System.err.println("Указанный путь не является директорией: " + path);
        } else if (!dir.canWrite()) {
            System.err.println("Нет прав записи в директорию: " + path);
        }
    }

}