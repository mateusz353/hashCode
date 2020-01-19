import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;



// Discret Backpack Problem - simple Dynamic programming (top-down)

public class DiscreteBackpackProblemDTMem {

    private static final String problemName = "c_medium";
    private static final String srcDir = "/home/mnowakowski/Dokumenty/hashCode/src/";

    private static final Path inputFilePath = Paths.get(srcDir + problemName +".in");
    private static final Path outputFilePath = Paths.get(srcDir + problemName +".out");

    public static void main(String[] args) {
        Instant start = Instant.now();

        DiscreteBackpackProblemTDMem solution = new DiscreteBackpackProblemTDMem();
        solution.solveProblem();

        Instant finish = Instant.now();
        System.out.println("Execution time:" + Duration.between(start, finish).toMillis() + " ms.");

    }

    public void solveProblem(){

        Input input = readInputFile();
        input.print();
        Set<Integer> result = new HashSet<Integer>();

        System.out.println("Max possible Weight: " + recursiveAux(input.getMaxWeight(), input.getNumberOfElements(), input.getWeights(), result));
        System.out.println(Arrays.toString(result.toArray()));

        writeResultToFile(result);


    }

    private int recursiveAux(int maxWeight, int numberOfElements, int[] weights, Set<Integer> result){
        if(numberOfElements == 0 || maxWeight == 0){
            return 0;
        }
        if(weights[numberOfElements-1] > maxWeight) {
            return recursiveAux(maxWeight, numberOfElements-1, weights, result);

        }
        Set<Integer> resultWithElement = new HashSet<Integer>();
        Set<Integer> resultWithoutElement = new HashSet<Integer>();
        int weightWithElement = weights[numberOfElements-1] + recursiveAux(maxWeight - weights[numberOfElements-1], numberOfElements-1, weights, resultWithElement);
        int weightWithoutElement = recursiveAux(maxWeight, numberOfElements-1, weights, resultWithoutElement);
        if(weightWithElement > weightWithoutElement){
            result.addAll(resultWithElement);
            result.add(numberOfElements-1);
            return weightWithElement;
        }
        result.addAll( resultWithoutElement);
        return weightWithoutElement;
    
    }

    private Input readInputFile() {
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            Input input = new Input();
            String[] firstLine =  reader.readLine().split(" ");
            input.setMaxWeight(Integer.parseInt(firstLine[0]));
            input.setNumberOfElements(Integer.parseInt(firstLine[1]));
            input.setWeights(stringArrayToIntArray(reader.readLine().split(" ")));
            return input;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
    }

    int[] stringArrayToIntArray(String[] stringArray)
    {
        return Stream.of(stringArray).mapToInt(Integer::parseInt).toArray();
    }

    private void writeResultToFile(Set<Integer> result) {
        StringBuilder outputFileBuilder = new StringBuilder();
        outputFileBuilder.append(result.size()+"\n");
        result.stream().forEach(item -> outputFileBuilder.append(item+" "));

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
            writer.write(outputFileBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Input{
        private int maxWeight;
        private int numberOfElements;
        private int[] weights;

        public int getMaxWeight(){
            return maxWeight;
        }

        public void setMaxWeight(int maxWeight){
            this.maxWeight = maxWeight;
        }

        public int getNumberOfElements(){
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements){
            this.numberOfElements = numberOfElements;
        }

        public int[] getWeights(){
            return weights;
        }

        public void setWeights(int[] weights){
            this.weights = weights;
        }

        public void print(){
            System.out.println("maxWeight = " + maxWeight + "\nnumberOfElements = " + numberOfElements + "\nweights = " + Arrays.toString(weights) + "\n");
        }
    }
}
