import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerArray;

import neural.NeuralNetwork;
import training.SimpleAddition;
import training.Trainer;
import training.TrainingData;
import training.mnist.MnistDigits;

public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();

        // TrainingData[] trainingData = new TrainingData[10000];
        // for(int i = 0; i < trainingData.length; i++){
        // trainingData[i] = new SimpleAddition();
        //     trainingData[i].generate();
        // }
        System.out.println(Arrays.toString(main.runExisitingNetwork(new float[]{4, 4})));

        // TrainingData[] trainingData = MnistDigits.loadTrainingData();
        // System.out.println("data length: " + trainingData[0].getInputValues().length);

        // try {
        //     for(int i = 0; i < 5; i++) {
        //         float[] output = main.runExisitingNetwork(trainingData[i].getInputValues());
        //         float delta = Math.abs(trainingData[i].getExpectedOutputValues()[0] - output[0]);
        //         System.out.println("Expected: " + Arrays.toString(trainingData[i].getExpectedOutputValues()) + " got " + Arrays.toString(output) + " delta " + delta);
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }


        // main.trainNetworks(trainingData, 10000);
    }

    public float[] runExisitingNetwork(float[] inputValues) throws IOException {
        NeuralNetwork network = NeuralNetwork.loadFromFile(new File("test.nn"));
        network.inputLayer.setValues(inputValues);
        return network.run();
    }

    public void trainNetworks(TrainingData[] trainingData, int cycles) {
        System.out.println("Generating training data.");

        Trainer trainer = new Trainer(trainingData);

        System.out.println("Building neural networks.");
        NeuralNetwork[] networks = new NeuralNetwork[32];

        for (int i = 0; i < networks.length; i++) {
            networks[i] = new NeuralNetwork(2, 1);
            networks[i].addLayer(4);
            networks[i].build();
        }

        System.out.println("Running neural networks.");

        ExecutorService executorService = Executors.newFixedThreadPool(networks.length);

        NeuralNetwork bestNetwork = null;
        float bestScore = Float.MAX_VALUE;

        System.out.println("Running thread pool");
        for (int cycle = 0; cycle < cycles; cycle++) {
            long last = System.currentTimeMillis();
            float[] scores = runNetworks(executorService, trainer, networks);
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] < bestScore) {
                    bestScore = scores[i];
                    bestNetwork = networks[i];
                }
            }

            long mutationTimeDelta = System.currentTimeMillis();
            mutateNetworks(executorService, bestNetwork, networks);
            mutationTimeDelta = System.currentTimeMillis() - mutationTimeDelta;

            long delta = System.currentTimeMillis() - last;

            double estimatedTimeRemaining = ((int)((cycles * delta) / 600.0)) / 100.0;

            System.out.print("Completed cycle: " + cycle + " | ");
            System.out.print("Best neural network: " + bestScore + " | ");
            System.out.print("Estimated time: " + estimatedTimeRemaining + " minutes @ " + delta + "ms/cycle" + " | ");
            System.out.println("Mutation time: " + mutationTimeDelta + "ms");

            last = System.currentTimeMillis();
        }

        System.out.print("Shutting down thread pool.. ");
        executorService.shutdown();
        System.out.println(" Done!");

        try {
            bestNetwork.save("test.nn");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private float[] runNetworks(ExecutorService executorService, Trainer trainer, NeuralNetwork[] networks) {
        float[] scores = new float[networks.length];
        AtomicIntegerArray progress = new AtomicIntegerArray(networks.length);
        for (int i = 0; i < networks.length; i++) {
            IndexedTask indexedTask = new IndexedTask(i) {
                public void run() {
                    scores[this.index] = trainer.runNeuralNetworkAgainstTrainingData(networks[this.index]);
                    progress.set(this.index, 1);
                }
            };
            executorService.submit(indexedTask);
        }

        while (!this.isTaskFinished(progress)) {}
        
        return scores;
    }

    private void mutateNetworks(ExecutorService executorService, NeuralNetwork bestNetwork, NeuralNetwork[] networks) {
        AtomicIntegerArray progress = new AtomicIntegerArray(networks.length);
        for (int i = 0; i < networks.length; i++) {
            if (networks[i] == bestNetwork) {
                progress.set(i, 1);
                continue;
            }
            IndexedTask indexedTask = new IndexedTask(i) {
                public void run() {
                    networks[this.index] = NeuralNetwork.mutateFromParent(bestNetwork);
                    progress.set(this.index, 1);
                }
            };
            executorService.submit(indexedTask);
        }

        while (!this.isTaskFinished(progress)) {}
    }

    private boolean isTaskFinished(AtomicIntegerArray progress) {
        for(int i = 0; i < progress.length(); i++){
            if(progress.get(i) == 0){return false;}
        }
        return true;
    }

}

class IndexedTask implements Runnable {

    public final int index;

    public IndexedTask(int index) {
        this.index = index;
    }

    public void run() {
    }

}
