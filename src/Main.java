import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Использование: java TextSorter <путь к файлу> <результат в файл> <режим сортировки> [<номер слова>]");
            System.out.println("Режимы сортировки: 1 - по алфавиту, 2 - по количеству символов, 3 - по слову в строке.");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        int mode = Integer.parseInt(args[2]);
        int wordIndex;
        if (mode == 3 && args.length > 3) {
            wordIndex = Integer.parseInt(args[3]) - 1;
        } else {
            wordIndex = -1;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(inputPath));
            Map<String, Long> frequencyMap = lines.stream()
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

            List<String> sortedLines = new ArrayList<>(lines);
            switch (mode) {
                case 1:
                    sortedLines.sort(Comparator.naturalOrder());
                    break;
                case 2:
                    sortedLines.sort(Comparator.comparingInt(String::length));
                    break;
                case 3:
                    if (wordIndex >= 0) {
                        sortedLines.sort(Comparator.comparing(line -> getWordFromLine(line, wordIndex)));
                    } else {
                        System.out.println("Укажите индекс слова для сортировки.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Недопустимый режим сортировки.");
                    return;
            }


            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
                for (String line : sortedLines) {
                    writer.write(line + " " + frequencyMap.get(line));
                    writer.newLine();
                }
            }

            System.out.println("Файл успешно обработан и сохранен: " + outputPath);

        } catch (IOException e) {
            System.out.println("Ошибка при чтении/записи файла: " + e.getMessage());
        }
    }

    private static String getWordFromLine(String line, int index) {
        String[] words = line.split(" ");
        return index >= 0 && index < words.length ? words[index] : "";
    }
}