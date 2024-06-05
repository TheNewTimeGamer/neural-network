import java.io.File;
import java.io.IOException;

import neural.NeuralNetwork;
import training.SimpleAddition;
import training.Trainer;
import training.TrainingData;

public class Main {
    
    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.loadExisitingNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // main.trainNetworks(5000);
    }

    public void loadExisitingNetwork() throws IOException {
        NeuralNetwork network = NeuralNetwork.loadFromFile(new File("test.nn"));
        network.inputLayer.setValues(new float[]{12, 6});
        float[] output = network.run();
        System.out.println(output[0]);
    }

    public void trainNetworks(int cycles) {
        System.out.println("Generating training data.");
        TrainingData[] trainingData = new TrainingData[1024];
        for(int i = 0; i < trainingData.length; i++){
            trainingData[i] = new SimpleAddition();
            trainingData[i].generate();
        }

        Trainer trainer = new Trainer(trainingData);

        System.out.println("Building neural networks.");
        NeuralNetwork[] networks = new NeuralNetwork[32];

        for(int i = 0; i < networks.length; i++) {
            networks[i] = new NeuralNetwork(2, 1);
            networks[i].addLayer(16);
            networks[i].build();
        }

        System.out.println("Running neural networks.");
        
        NeuralNetwork bestNetwork = null;
        float bestScore = Float.MAX_VALUE;
        for(int cycle = 0; cycle < cycles; cycle++) {
            for(int i = 0; i < networks.length; i++) {
                float score = trainer.runNeuralNetworkAgainstTrainingData(networks[i]);
                if(score < bestScore) {
                    bestNetwork = networks[i];
                    bestScore = score;
                }
            }
            System.out.println("Completed cycle: " + cycle);
            System.out.println("Best neural network: " + bestScore);

            for(int i = 0; i < networks.length; i++){
                if(networks[i] == bestNetwork) { continue; }
                networks[i] = NeuralNetwork.mutateFromParent(bestNetwork);
            }
        }

        try {
            bestNetwork.save("test.nn");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }    

}
