package domain.level;

import java.util.*;

public class RoomGraph {
    private static final int SIZE = 3;
    private static final int ROOMS = SIZE * SIZE;
    private boolean[][] connections; // матрица смежности

    public RoomGraph() {
        connections = new boolean[ROOMS][ROOMS];
        createFullGrid();
        removeRandomEdges(Room.rndBetween(2,4));
    }

    // Создает полную сетку (все соседи соединены)
    private void createFullGrid() {
        for (int room = 0; room < ROOMS; room++) {
            int row = room / SIZE;
            int col = room % SIZE;

            // Соединяем с правым соседом
            if (col < SIZE - 1) {
                connections[room][room + 1] = true;
                connections[room + 1][room] = true;
            }

            // Соединяем с нижним соседом
            if (row < SIZE - 1) {
                connections[room][room + SIZE] = true;
                connections[room + SIZE][room] = true;
            }
        }
    }

    // Получить список соседей для комнаты
    private List<Integer> getNeighbors(int room) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < ROOMS; i++) {
            if (connections[room][i]) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    // Проверка связности графа (DFS обход)
    public boolean isConnected() {
        boolean[] visited = new boolean[ROOMS];
        dfs(0, visited); // начинаем с комнаты 0

        // Проверяем, все ли комнаты посетили
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    // Рекурсивный обход в глубину
    private void dfs(int room, boolean[] visited) {
        visited[room] = true;

        for (int neighbor : getNeighbors(room)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    // Удаление случайного ребра с проверкой связности
    public boolean removeRandomEdge() {
        // Собираем все существующие ребра
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < ROOMS; i++) {
            for (int j = i + 1; j < ROOMS; j++) {
                if (connections[i][j]) {
                    edges.add(new Edge(i, j));
                }
            }
        }

        // Перемешиваем ребра для случайного выбора
        Collections.shuffle(edges);

        // Пробуем удалять по очереди
        for (Edge edge : edges) {
            // Запоминаем состояние
            boolean wasConnected = connections[edge.room1][edge.room2];

            // Удаляем ребро
            connections[edge.room1][edge.room2] = false;
            connections[edge.room2][edge.room1] = false;

            // Проверяем связность
            if (isConnected()) {
                return true; // успешно удалили
            } else {
                // Возвращаем ребро обратно
                connections[edge.room1][edge.room2] = wasConnected;
                connections[edge.room2][edge.room1] = wasConnected;
            }
        }
        return false; // не нашли подходящего ребра
    }

    // Удалить несколько ребер
    public void removeRandomEdges(int count) {
        for (int i = 0; i < count; i++) {
            if (!removeRandomEdge()) {
                System.out.println("Stop on " + i + " remove edges");
                break;
            }
        }
    }

    // Вспомогательный класс для хранения ребра
    private static class Edge {
        int room1;
        int room2;

        Edge(int r1, int r2) {
            room1 = r1;
            room2 = r2;
        }
    }

    public boolean[] getRoomDoors(int roomNum) {
        //массив направления дверей
        //0 - север, 1 - восток, 2 - юг, 3 - запад
        boolean[] doors = new boolean[4]; // по умолчанию все false

        int row = roomNum / SIZE;
        int col = roomNum % SIZE;

        // Север (0)
        if (row > 0) doors[0] = connections[roomNum][roomNum - SIZE];

        // Восток (1)
        if (col < SIZE - 1) doors[1] = connections[roomNum][roomNum + 1];

        // Юг (2)
        if (row < SIZE - 1) doors[2] = connections[roomNum][roomNum + SIZE];

        // Запад (3)
        if (col > 0) doors[3] = connections[roomNum][roomNum - 1];

        return doors;
    }

    //Получить матрицу смежности
    public boolean[][] getConnections() {
        return connections;
    }

    // Визуализация графа
    public void printGraph() {
        System.out.println("\nCurrent edges:");

        // Верхняя граница
        System.out.print("   ");
        for (int col = 0; col < SIZE; col++) {
            System.out.print(" " + col + "  ");
        }
        System.out.println();

        for (int row = 0; row < SIZE; row++) {
            // Рисуем комнаты и горизонтальные связи
            System.out.print(row + "  ");
            for (int col = 0; col < SIZE; col++) {
                int room = row * SIZE + col;
                System.out.print("[" + room + "]");

                // Связь вправо
                if (col < SIZE - 1 && connections[room][room + 1]) {
                    System.out.print("-");
                } else if (col < SIZE - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();

            // Рисуем вертикальные связи
            if (row < SIZE - 1) {
                System.out.print("   ");
                for (int col = 0; col < SIZE; col++) {
                    int room = row * SIZE + col;
                    if (connections[room][room + SIZE]) {
                        System.out.print("|   ");
                    } else {
                        System.out.print("    ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    // Печать матрицы связаности комнат
    public void printMatrix() {
        System.out.println("Matrix linked:");
        for (int i = 0; i < ROOMS; i++) {
            for (int j = 0; j < ROOMS; j++) {
                System.out.print((connections[i][j] ? 1 : 0) + " ");
            }
            System.out.println();
        }
    }
/*
        RoomGraph graph = new RoomGraph();

        System.out.println("Source full grid:");
        graph.printGraph();

        // Удаляем несколько ребер
        System.out.println("Remove 3 random edges:");
        graph.removeRandomEdges(4);

        graph.printGraph();
        graph.printMatrix();

        // Проверим связность финального графа
        System.out.println("Graph is connected? " + graph.isConnected());

 */
}