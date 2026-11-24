package ru.netology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RouteAnalyzer {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        int routesCount = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(routesCount);

        for (int i = 0; i < routesCount; i++) {
            executor.execute(() -> {
                String route = generateRoute("RLRFR", 100);

                int rightTurns = 0;
                for (char ch : route.toCharArray()) {
                    if (ch == 'R') {
                        rightTurns++;
                    }
                }

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(rightTurns, 1, Integer::sum);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        printStatistics();
    }

    private static void printStatistics() {
        if (sizeToFreq.isEmpty()) {
            System.out.println("Нет данных для анализа");
            return;
        }

        Map.Entry<Integer, Integer> mostFrequent = sizeToFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        System.out.println(
                "Самое частое количество повторений " + mostFrequent.getKey()
                        + " (встретилось " + mostFrequent.getValue() + " раз)"
        );

        System.out.println("Другие размеры:");

        List<Map.Entry<Integer, Integer>> others = new ArrayList<>(sizeToFreq.entrySet());
        others.remove(mostFrequent);
        others.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());

        for (Map.Entry<Integer, Integer> entry : others) {
            System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
        }
    }
}