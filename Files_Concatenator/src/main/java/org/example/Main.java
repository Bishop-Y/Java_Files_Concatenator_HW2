package org.example;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    /**
     * Словарь, в котором ключом является индекс, а значением - файл.
     */
    private final static Map<Integer, File> map = new HashMap<>();

    /**
     * Путь до директории. в которой будет работать программа
     */
    public static File file;

    /**
     * Разделитель, используемый для разделения строк пути.
     */
    public static String separator;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type absolute path to directory:");
        String path = scanner.nextLine();
        file = new File(path);
        while (!file.exists() && !file.isDirectory()) {
            System.out.println("Incorrect path. Try again:");
            path = scanner.nextLine();
            file = new File(path);
        }
        separator = File.separator;

        listFilesForFolder(file);
        if (map.size() == 0) {
            System.out.println("Empty directory.");
            return;
        }

        Node.nodes = new Node[map.size()];
        for (int key : map.keySet()) {
            Node.nodes[key] = new Node(map.get(key));
        }

        try {
            Node.fillNeighborNodes();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Node node : Node.nodes) {
            if (node.hasCycle()) {
                System.out.println(Node.whereCycle);
                return;
            }
        }
        for (Node node : Node.nodes) {
            node.isVisited = false;
        }

        System.out.println();
        TopologicalSort.sort(Node.nodes);
    }

    /**
     * Считывание всех файлов текущей папке
     * @param folder папка
     */
    private static void listFilesForFolder(File folder) {
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                listFilesForFolder(file);
            } else {
                map.put(map.size(), file);
            }
        }
    }
}
