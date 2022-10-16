package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class WorkWithFiles {
    private static final String NEW_PATH = "resources/results";
    private static boolean IS_COMMENT_BIGGER_THEN_ONE_LINE = false;
    private static int COUNTER = 0;
    public static String CURRENT_FILE = "";

    public void startSearchingInFiles() {
        File file = new File(getPathFromConsole());
        search(file);
    }

    private String getPathFromConsole() {
        String path;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть шлях до файлу:");
        path = scanner.next();
        return path;
    }

    public void search(File file) {
        System.out.println("Пошук файлів дерикторії....");
        if (file.isDirectory()) {
            for (File temp : file.listFiles()) {
                CURRENT_FILE = temp.getPath();
                Thread thr = new Thread(new MyRunnable());
                thr.start();
                try {
                    thr.join();
                } catch (InterruptedException e) {
                    System.out.println("brrrrrrrrrrrrrrrrrrr " + e);
                }
//                search(new File(WorkWithFiles.CURRENT_FILE));
            }
        } else if (file.getName().endsWith(".java")) {
            System.out.println("Файл з розширенням .java знайдено!");
            createNewDirWithModifyFiles(file);
        } else {
            System.out.println("we have a problem...");
        }
    }

    private void createNewDirWithModifyFiles(File file) {
        List<String> infoFromInputFile = getInfoFromFile(file);

        File newDir = new File(NEW_PATH);
        if (!newDir.exists()) {
            System.out.println("Створена нова дерикторія з результатами");
            newDir.mkdir();
        }
        File newFile = createNewFile();
        writeChangedInfoToFile(newFile, infoFromInputFile);
    }

    private List<String> getInfoFromFile(File file) {
        List<String> infoFromInputFile;
        try {
            infoFromInputFile = Files.readAllLines(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new RuntimeException("Can't read from file, ", e);
        }
        return infoFromInputFile;
    }

    private File createNewFile() {
        File newFile = new File(NEW_PATH + "/resultFile" + COUNTER + ".java");
        try {
            newFile.createNewFile();
            System.out.println("Створено файл з результатом під номером " + COUNTER);
            COUNTER++;
        } catch (IOException e) {
            throw new RuntimeException("Can't create file, ", e);
        }
        return newFile;
    }

    private void writeChangedInfoToFile(File newFile, List<String> infoFromInputFile) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile.getPath(), true))) {
            for (String line : infoFromInputFile) {
                bufferedWriter.write(removeComments(line) + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to this file, " + newFile.getPath(), e);
        }
    }

    private String removeComments(String str) {
        if (str.contains("//")) {
            return new StringBuilder(str).delete(str.indexOf("//"), str.length()).toString();
        } else if (str.startsWith("/*") && str.endsWith("*/")) {
            return new StringBuilder(str).delete(0, str.indexOf("*/") + 2).toString();
        } else if (str.startsWith("/*")) {
            IS_COMMENT_BIGGER_THEN_ONE_LINE = true;
            return new StringBuilder(str).delete(0, str.length()).toString();
        } else if (IS_COMMENT_BIGGER_THEN_ONE_LINE) {
            if (str.endsWith("*/")) {
                IS_COMMENT_BIGGER_THEN_ONE_LINE = false;
                return new StringBuilder(str).delete(0, str.length()).toString();
            }
            return new StringBuilder(str).delete(0, str.length()).toString();
        }
        return str;
    }
}

