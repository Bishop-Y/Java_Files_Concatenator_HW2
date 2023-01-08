package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Класс узлов.
 */
public class Node implements Comparable<Node>{

    /**
     * Строка, в которой будет записано название файла, в котором будет обнаружен цикл.
     */
    public static String whereCycle = "";

    /**
     * Массив узлов.
     */
    public static Node[] nodes;

    /**
     * Файл текущего узла.
     */
    private final File file;

    /**
     * Всопомогательное поле, которое проверяется в методе hasCycle.
     */
    public boolean beingVisited = false;

    /**
     * Вспомогательное поле, которое используется в методе hasCycle и в методе sort.
     */
    public boolean isVisited = false;

    /**
     * Узлы, для которых текущий узел является одним из "родителей".
     */
    private final List<Node> neighbors = new ArrayList<>();

    /**
     * Содержимое файла.
     */
    private String content;

    /**
     * Создание узла.
     * @param file файл.
     */
    public Node(File file) {
        this.file = file;
    }

    /**
     *
     * @return путь до файла в текущем узле.
     */
    public String path() {
        return file.getPath();
    }

    /**
     *
     * @return имя файла в текущем узле.
     */
    public String fileName() {
        return file.getName();
    }

    /**
     * Добавление узла, для которого текущий узел будет "родителем".
     * @param node добавляемый узел.
     */
    public void addNeighborNode(Node node) {
        this.neighbors.add(node);
    }

    /**
     *
     * @return список узлов, для которых текущий узел является "родителем".
     */
    public List<Node> getNeighborNodes() {
        return neighbors;
    }

    public void sortNeighborNodes() {
        Collections.sort(neighbors);
    }

    /**
     * Проверка на цикличность.
     * @return true, если есть цикл, инчае false.
     */
    boolean hasCycle() {
        this.beingVisited = true;

        for (Node neighbor : this.neighbors) {
            if (neighbor.beingVisited) {
                if (whereCycle.isEmpty()) {
                    whereCycle = "Detected cycle in file " + neighbor.fileName();
                }
                return true;
            } else if (!neighbor.isVisited && neighbor.hasCycle()) {
                if (whereCycle.isEmpty()) {
                    whereCycle = "Detected cycle in file " + neighbor.fileName();
                }
                return true;
            }
        }
        this.beingVisited = false;
        this.isVisited = true;
        return false;
    }

    /**
     * Нахождение зависимостей в текущем узле.
     * @return все пути до файлов, от которых зависит текущий узел.
     */
    public List<String> findRequirements() {
        Scanner input;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(fileName() + " does not exist.");
        }

        List<String> requirements = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        String line;

        while (input.hasNextLine()) {
            line = input.nextLine();
            if (line.startsWith("require '")) {
                requirements.add(line.substring(9, line.length() - 1));
            }
            result.append(line).append('\n');
        }

        content = result.toString();
        return requirements;
    }

    /**
     * Возваращает узел, который имеет такой же путь, что и в передаваемой параметре.
     * @param path путь.
     * @param neighborNode соседний узел.
     */
    public static void addNeighborNodeByRequirement(String path, Node neighborNode) {
        for (Node node : nodes) {
            if (node.path().equals(path)) {
                node.addNeighborNode(neighborNode);
            }
        }
    }

    /**
     * После нахождения всех зависмостей для каждого узла добавляем "дочерние" узлы.
     */
    public static void fillNeighborNodes() {
        for (Node node : Node.nodes) {
            List<String> requirements = node.findRequirements();
            for (String requirement : requirements) {
                File temporaryFile = new File(Main.file.getPath() + Main.separator + requirement);
                if (temporaryFile.exists() && !temporaryFile.isDirectory()) {
                    Node.addNeighborNodeByRequirement(temporaryFile.getPath(), node);
                } else {
                    throw new RuntimeException("Invalid requirement in file " + node.fileName());
                }
            }
        }
    }

    /**
     *
     * @return содержимое файла.
     */
    @Override
    public String toString() {
        return content;
    }

    /**
     * Переопределённый компаратор для сортировки соседних узлов по имени.
     * @param node узел, с которым идёт сравнение.
     * @return стандратный результат при лексикографическом сравнении.
     */
    @Override
    public int compareTo(Node node) {
        return fileName().compareTo(node.fileName());
    }
}
