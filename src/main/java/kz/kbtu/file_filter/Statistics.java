package kz.kbtu.file_filter;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Statistics {
    private List<String> strings;
    private List<String> integers;
    private List<String> floats;

    private long maxInt = Long.MIN_VALUE;
    private long minInt = Long.MAX_VALUE;
    private long sumInt = 0;

    private double maxFloat = Double.NEGATIVE_INFINITY;
    private double minFloat = Double.POSITIVE_INFINITY;
    private double sumFloat = 0.0;

    private int maxStringLength = 0;
    private int minStringLength = Integer.MAX_VALUE;

    private boolean fullStat = false;
    private boolean appendMode = false;

    public Statistics() {
        strings = new ArrayList<>();
        integers = new ArrayList<>();
        floats = new ArrayList<>();
    }

    public void setStat(boolean fullStat) {
        this.fullStat = fullStat;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public void addInt(String line) {
        try {
            long num = Long.parseLong(line);
            integers.add(line);

            if (fullStat) {
                sumInt += num;
                // инициализация мин и макс при первом элементе
                if (integers.size() == 1) {
                    maxInt = num;
                    minInt = num;
                } else {
                    if (num > maxInt) {
                        maxInt = num;
                    }
                    if (num < minInt) {
                        minInt = num;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Ошибка при парсинге целого числа: " + line);
        }
    }

    public void addFloat(String line) {
        try {
            double num = Double.parseDouble(line);
            floats.add(line);

            if (fullStat) {
                sumFloat += num;
                if (floats.size() == 1) {
                    maxFloat = num;
                    minFloat = num;
                } else {
                    if (num > maxFloat) {
                        maxFloat = num;
                    }
                    if (num < minFloat) {
                        minFloat = num;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Ошибка при парсинге числа с плавающей точкой: " + line);
        }
    }

    public void addString(String line) {
        strings.add(line);

        if (fullStat) {
            int length = line.length();
            if (strings.size() == 1) {
                maxStringLength = length;
                minStringLength = length;
            } else {
                if (length > maxStringLength) {
                    maxStringLength = length;
                }
                if (length < minStringLength) {
                    minStringLength = length;
                }
            }
        }
    }

    public void createFiles(String prefix, String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!integers.isEmpty()) {
            String filename = buildFilename(prefix, "integers.txt");
            File intFile = new File(path, filename);
            writeDataToFile(intFile, integers);
        }

        if (!floats.isEmpty()) {
            String filename = buildFilename(prefix, "floats.txt");
            File floatFile = new File(path, filename);
            writeDataToFile(floatFile, floats);
        }

        if (!strings.isEmpty()) {
            String filename = buildFilename(prefix, "strings.txt");
            File strFile = new File(path, filename);
            writeDataToFile(strFile, strings);
        }
    }

    private String buildFilename(String prefix, String defaultName) {
        return (prefix != null && !prefix.isEmpty()) ? prefix + defaultName : defaultName;
    }

    private void writeDataToFile(File file, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, appendMode))) {
            for (String item : data) {
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + file.getName() + ": " + e.getMessage());
        }
    }

    public void printStatistics(boolean shortStat, boolean fullStat) {
        System.out.println("\n=== СТАТИСТИКА ===");

        if (appendMode) {
            System.out.println("(Режим добавления - статистика только для текущего запуска)");
        }

        if (!integers.isEmpty()) {
            System.out.println("\nЦелые числа:");
            System.out.println("  Количество: " + integers.size());

            if (fullStat && integers.size() > 0) {
                System.out.println("  Минимальное: " + minInt);
                System.out.println("  Максимальное: " + maxInt);
                System.out.println("  Сумма: " + sumInt);
                System.out.println("  Среднее: " + (double) sumInt / integers.size());
            }
        }

        if (!floats.isEmpty()) {
            System.out.println("\nЧисла с плавающей точкой:");
            System.out.println("  Количество: " + floats.size());

            if (fullStat && floats.size() > 0) {
                System.out.println("  Минимальное: " + minFloat);
                System.out.println("  Максимальное: " + maxFloat);
                System.out.println("  Сумма: " + sumFloat);
                System.out.println("  Среднее: " + sumFloat / floats.size());
            }
        }

        if (!strings.isEmpty()) {
            System.out.println("\nСтроки:");
            System.out.println("  Количество: " + strings.size());

            if (fullStat && strings.size() > 0) {
                System.out.println("  Длина самой короткой: " + minStringLength);
                System.out.println("  Длина самой длинной: " + maxStringLength);
            }
        }

        if (integers.isEmpty() && floats.isEmpty() && strings.isEmpty()) {
            System.out.println("Данные для обработки не найдены.");
        }

        if (appendMode) {
            System.out.println("\nДанные добавлены к существующим файлам.");
        }
    }
}