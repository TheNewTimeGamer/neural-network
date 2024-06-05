package training.mnist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import training.TrainingData;

public class MnistDigits {
    
    // Data is 28x28
    public static TrainingData[] loadTrainingData() throws IOException {
        File file = new File("training-data/mnist-digits/csv/mnist_train.csv");
        byte[] buffer = new byte[(int)file.length()];
        FileInputStream in = new FileInputStream("training-data/mnist-digits/csv/mnist_train.csv");
        in.read(buffer);
        in.close();

        String raw = new String(buffer);
        String[] lines = raw.split("\n");

        TrainingData[] trainingData = new TrainingData[lines.length];
        for(int i = 0; i < lines.length; i++) {
            System.out.println("Building training data " + i + "/" + lines.length);
            trainingData[i] = new MnistTrainingData(lines[i]);
        }

        return trainingData;
    }

}

class MnistTrainingData implements TrainingData {

    public float[] inputs;
    public float[] outputs = new float[1];

    public MnistTrainingData(String raw) {
        String[] data = raw.split(",");
        this.inputs = new float[data.length - 1];
        for(int i = 0; i < data.length - 1; i++) {
            this.inputs[i] = Float.parseFloat(data[i + 1]);
        }
        this.outputs[0] = Float.parseFloat(data[0]);
    }

    public void generate() {}

    public float[] getInputValues() {
        return this.inputs;
    }

    public float[] getExpectedOutputValues() {
       return this.outputs;
    }

}
