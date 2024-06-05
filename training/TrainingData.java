package training;

public interface TrainingData {

    public void generate();    
    public float[] getInputValues();
    public float[] getExpectedOutputValues();
    
}