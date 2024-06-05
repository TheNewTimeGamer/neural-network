package training;

public class SimpleAddition implements TrainingData {

    private float[] inputValues = new float[2];
    private float[] outputValues = new float[1];

    public void generate() {
        inputValues[0] = (float) Math.random();
        inputValues[1] = (float) Math.random();

        outputValues[0] = inputValues[0] + inputValues[1];
    }

    public float[] getInputValues() {
        return this.inputValues;
    }

    public float[] getExpectedOutputValues() {
        return this.outputValues;
    }
    
}
