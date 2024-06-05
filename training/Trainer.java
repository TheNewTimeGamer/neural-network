package training;

import neural.NeuralNetwork;

public class Trainer {
    
    public final TrainingData[] trainingData;

    public Trainer(TrainingData[] trainingData) {
        this.trainingData = trainingData;
    }

    public float runNeuralNetworkAgainstTrainingData(NeuralNetwork neuralNetwork) {        
        float[][] outputs = new float[this.trainingData.length][neuralNetwork.outputLayer.neurons.length];
        for(int i = 0; i < this.trainingData.length; i++) {
            neuralNetwork.inputLayer.setValues(this.trainingData[i].getInputValues());
            outputs[i] = neuralNetwork.run();
        }

        for(int i = 0; i < this.trainingData.length; i++){
            outputs[i] = this.calculateScoresFromResults(outputs[i], this.trainingData[i].getExpectedOutputValues());
        }

        float[] individualScores = this.getAverageScores(outputs);

        return this.getAverageScore(individualScores);
    }

    public float[] calculateScoresFromResults(float[] outputs, float[] expectedOutputs) {
        float[] scores = new float[expectedOutputs.length];
        for(int i = 0; i < scores.length; i++) {
            scores[i] = Math.abs(outputs[i] - expectedOutputs[i]);
        }
        return scores;
    }

    public float[] getAverageScores(float[][] output) {
        float[] scores = new float[output.length];
        for(int i = 0; i < output.length; i++) {
            scores[i] = this.getAverageScore(output[i]);
        }
        return scores;
    }

    public float getAverageScore(float[] scores) {
        float totalScore = 0;
        for(float score : scores) {
            totalScore += score;
        }
        return totalScore / 2;
    }

}
