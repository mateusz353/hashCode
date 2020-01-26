import static java.util.function.Function.identity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Stream;

// Discret Backpack Problem - simple Dynamic programming (top-down) with cache

public class DiscreteBackpackProblemTDMemParallel {

    private static final String problemName = "d_quite_big";
    private static final String srcDir = "/home/mnowakowski/Dokumenty/hashCode/inout/";

    private static final Path inputFilePath = Paths.get(srcDir + problemName + ".in");
    private static final Path outputFilePath = Paths.get(srcDir + problemName + ".out");

    private static final ExecutorService POOL = Executors.newFixedThreadPool(6);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Instant start = Instant.now();

        DiscreteBackpackProblemTDMemParallel solution = new DiscreteBackpackProblemTDMemParallel();
        solution.solveProblem();

        Instant finish = Instant.now();
        System.out.println("Execution time:" + Duration.between(start, finish).toMillis() + " ms.");

    }

    public void solveProblem() throws ExecutionException, InterruptedException {

        Input input = readInputFile();
        input.print();
        Set<Integer> result = new HashSet<>();
        Map<Pair<Integer, Integer>, Integer> memo = new HashMap<>();

        System.out.println("Max possible Weight: " + recursiveAux(input.getMaxWeight(), input.getNumberOfElements(), input.getWeights(), memo,
                result).join());
        System.out.println(Arrays.toString(result.toArray()));
        System.out.println(result.stream().map(index -> input.getWeights()[index]).reduce(Integer::sum));

        writeResultToFile(result);

    }

    private CompletableFuture<Integer> recursiveAux(Integer maxWeight, Integer numberOfElements, Integer[] weights,
                                                    Map<Pair<Integer, Integer>, Integer> memo, Set<Integer> result) throws ExecutionException,
            InterruptedException {
        if (numberOfElements == 0 || maxWeight == 0) {
            return terminate(0);
        }
        Pair key = new Pair(numberOfElements, maxWeight);
        System.out.println(String.format("[%d %d]", numberOfElements, maxWeight));
        if (!memo.containsKey(key)) {
            if (weights[numberOfElements - 1] > maxWeight) {
                memo.put(key, recursiveAux(maxWeight, numberOfElements - 1, weights, memo, result).get());

            } else {
                Set<Integer> resultWithElement = new HashSet<>();
                Set<Integer> resultWithoutElement = new HashSet<>();
                Integer weightWithElement = weights[numberOfElements - 1] + recursiveAux(maxWeight - weights[numberOfElements - 1],
                        numberOfElements - 1, weights, memo, resultWithElement).get();
                Integer weightWithoutElement = recursiveAux(maxWeight, numberOfElements - 1, weights, memo, resultWithoutElement).get();
                if (weightWithElement >= weightWithoutElement) {
                    result.addAll(resultWithElement);
                    result.add(numberOfElements - 1);
                    memo.put(key, weightWithElement);
                } else {
                    result.addAll(resultWithoutElement);
                    memo.put(key, weightWithoutElement);
                }
            }
        }
        return terminate(memo.get(key));
    }

    private static <T> CompletableFuture<T> terminate(T t) {
        return CompletableFuture.completedFuture(t);
    }

    private static <T> CompletableFuture<T> tailcall(Supplier<CompletableFuture<T>> s) {
        return CompletableFuture.supplyAsync(s, POOL).thenCompose(identity());
    }

    private Input readInputFile() {
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            Input input = new Input();
            String[] firstLine = reader.readLine().split(" ");
            input.setMaxWeight(Integer.parseInt(firstLine[0]));
            input.setNumberOfElements(Integer.parseInt(firstLine[1]));
            input.setWeights(stringArrayToIntegerArray(reader.readLine().split(" ")));
            return input;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Integer[] stringArrayToIntegerArray(String[] stringArray) {
        return Stream.of(stringArray).map(Integer::new).toArray(Integer[]::new);
    }

    private void writeResultToFile(Set<Integer> result) {
        StringBuilder outputFileBuilder = new StringBuilder();
        outputFileBuilder.append(result.size() + "\n");
        result.stream().forEach(item -> outputFileBuilder.append(item + " "));

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
            writer.write(outputFileBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Input {

        private Integer maxWeight;
        private Integer numberOfElements;
        private Integer[] weights;

        public Integer getMaxWeight() {
            return maxWeight;
        }

        public void setMaxWeight(Integer maxWeight) {
            this.maxWeight = maxWeight;
        }

        public Integer getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(Integer numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public Integer[] getWeights() {
            return weights;
        }

        public void setWeights(Integer[] weights) {
            this.weights = weights;
        }

        public void print() {
            System.out.println("maxWeight = " + maxWeight + "\nnumberOfElements = " + numberOfElements + "\nweights = " + Arrays.toString(weights) + "\n");
        }
    }

    private class Pair<K, V> {

        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(key, pair.key) &&
                    Objects.equals(value, pair.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
