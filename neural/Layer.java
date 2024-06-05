package neural;
public class Layer {
    
    public String name = "Unnamed";
    public Neuron[] neurons;

    public static Layer mutateFromParent(Layer parent) {
        Layer child = new Layer(parent.name, parent.neurons.length);
        for(int i = 0 ; i < child.neurons.length; i++) {
            child.neurons[i] = Neuron.mutateFromParent(parent.neurons[i]);
        }
        return child;
    }


    public Layer(String name, int size) {
        this.neurons = new Neuron[size];
    }

    public void generateNeurons(boolean noBias) {
        for(int i = 0; i < neurons.length; i++) {
            neurons[i] = noBias ? new Neuron(0) : new Neuron();
        }
    }

    public void connectTo(Layer layer) {
        for (Neuron neuron : neurons) {
            neuron.generateConnectionsTo(layer);
        }
    }

    public void trigger(Layer nextLayer) {
        for (Neuron neuron : neurons) {
            neuron.trigger(nextLayer);
        }
    }

    public void setValues(float[] values) {
        for(int i = 0; i < neurons.length; i++) {
            if(i >= values.length) {break;}
            neurons[i].value = values[i];
        }
    }

    public float[] getValues(boolean clearValues) {
        float[] values = new float[this.neurons.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = neurons[i].value;
            neurons[i].value = 0;
        }
        return values;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < this.neurons.length; i++){
            builder.append(this.neurons[i].toString() + "\r\n");
        }
        return builder.toString();
    }

}
