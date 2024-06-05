package neural;
public class Neuron {
    
    public float value;
    public float bias;

    public Connection[] connections;

    public ActivationFunction activationFunction;

    public static Neuron mutateFromParent(Neuron parent) {
        Neuron child = new Neuron(parent.bias + (0.5 - Math.random()));
        if(parent.connections == null) {
            return child;
        }

        child.connections = new Connection[parent.connections.length];
        for(int i = 0; i < parent.connections.length; i++) {
            child.connections[i] = Connection.mutateFromParent(parent.connections[i]);
        } 
        return child;
    }

    public Neuron(double bias){
        this.bias = (float) bias;
    }

    public Neuron(float bias) {
        this.bias = bias;
    }

    public Neuron() {
        this.bias = (float) Math.random();
    }

    public void generateConnectionsTo(Layer layer) {
        this.connections = new Connection[layer.neurons.length];
        for(int i = 0; i < layer.neurons.length; i++) {
            this.connections[i] = new Connection(i);
        }
    }
    
    public void trigger(Layer nextLayer) {
        for (Connection connection : connections) {
            float output = this.value + this.bias;
            if(this.activationFunction != null) {
                output = this.activationFunction.invoke(output);
            }
            this.value = 0;
            connection.trigger(nextLayer, output);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(this.connections != null) {
            builder.append(this.bias + ",");
            for(int i = 0; i < this.connections.length; i++) {
                builder.append(this.connections[i].weight + ",");
            }
            builder.setLength(builder.length()-1);
        }else{
            builder.append(this.bias);
        }
        return builder.toString();
    }

}
