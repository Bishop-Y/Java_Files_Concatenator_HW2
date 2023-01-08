package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс для топологической сортирвоки.
 */
public final class TopologicalSort {
    /**
     * Узлы, отсортированные топологически.
     */
    private static final List<Node> inOrder = new ArrayList<>();


    /**
     * Метод, запускающий топологическую сортировку. Выводит содержимое файлов в нужном порядке.
     * @param nodes массив узлов ориентированного графа.
     */
    public static void sort(Node[] nodes) {
        for (Node node : nodes) {
            node.sortNeighborNodes();
        }

        for (Node node : nodes) {
            if (!node.isVisited) {
                depthFirstSearch(node);
            }
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter("result.txt", StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println("An error occurred while writing the result.");
            return;
        }

        for (Node node : inOrder) {
            writer.write(node.toString());
            System.out.print(node);
        }
        writer.close();
    }

    /**
     * Рекурсивный метод поиска в глубину.
     * @param node узел.
     */
    private static void depthFirstSearch(Node node) {
        node.isVisited = true;
        List<Node> neighborNodes = node.getNeighborNodes();
        for (Node neighborNode : neighborNodes) {
            if (!neighborNode.isVisited) {
                depthFirstSearch(neighborNode);
            }
        }
        inOrder.add(0, node);
    }
}


