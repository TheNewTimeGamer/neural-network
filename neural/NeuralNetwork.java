package neural;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NeuralNetwork {

    public Layer inputLayer;
    public Layer outputLayer;

    public int currentLayer = -1;
    public ArrayList<Layer> layers = new ArrayList<Layer>();

    public static NeuralNetwork mutateFromParent(NeuralNetwork parent) {
        NeuralNetwork child = new NeuralNetwork();
        child.inputLayer = new Layer("Input", parent.inputLayer.neurons.length);
        child.inputLayer.generateNeurons(true);

        child.outputLayer = Layer.mutateFromParent(parent.outputLayer);

        for(int i = 0; i < parent.layers.size(); i++){
            child.layers.add(Layer.mutateFromParent(parent.layers.get(i)));
        }

        child.inputLayer.connectTo(child.layers.get(0));
        return child;
    }

    public static NeuralNetwork loadFromFile(File file) throws IOException {
        NeuralNetwork network = new NeuralNetwork();

        byte[] buffer = new byte[(int)file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(buffer);
        in.close();

        String raw = new String(buffer);

        String[] layers = raw.split("\r\n\r\n");
        for(int i = 0; i < layers.length; i++) {         
            String[] neurons = layers[i].split("\r\n");
            Layer layer = new Layer("hidden", neurons.length);
            for(int n = 0; n < neurons.length; n++) {
                String[] data = neurons[n].split(",");
                layer.neurons[n] = new Neuron();
                layer.neurons[n].bias = Float.parseFloat(data[0]);
                if(i < layers.length - 1){
                    layer.neurons[n].connections = new Connection[data.length - 1];
                    for(int c = 0; c < data.length - 1; c++) {
                        layer.neurons[n].connections[c] = new Connection(c);
                        layer.neurons[n].connections[c].weight = Float.parseFloat(data[c + 1]);
                    }
                }
            }
            if(i == 0) {
                layer.name = "input";
                network.inputLayer = layer;
            }else if(i == layers.length - 1) {
                layer.name = "output";
                network.outputLayer = layer;
            }else{
                network.layers.add(layer);
            }
        }

        return network;
    }

    public NeuralNetwork(){}

    public NeuralNetwork(int inputLayerSize, int outputLayerSize) {
        this.inputLayer = new Layer("input", inputLayerSize);
        this.outputLayer = new Layer("output", outputLayerSize);

        System.out.println("Generating input and output layer.");
        this.inputLayer.generateNeurons(true);
        this.outputLayer.generateNeurons(false);
    }

    public void addLayer(int size) {
        this.layers.add(new Layer("hidden", size));
    }

    public void build() {
        System.out.println("Generating neurons.");
        for (Layer layer : layers) {            
            layer.generateNeurons(false);
        }

        for (int i = -1; i < layers.size(); i++) {
            if(i < 0){
                inputLayer.connectTo(layers.get(0));
                System.out.println("Connecting input layer to next.");
                continue;
            }
            if(i + 1 < layers.size()) {
                layers.get(i).connectTo(layers.get(i + 1));
                System.out.println("Connecting layer " + i + " to next.");
                continue;
            }
            System.out.println("Connecting layer " + i + " to output.");
            layers.get(i).connectTo(outputLayer);
        }
    }

    public void step() {
        if(this.currentLayer < 0) {
            this.triggerInputLayer();
        }else {
            this.triggerCurrentLayer();
        }
        if(++this.currentLayer >= this.layers.size()) {
            this.currentLayer = -1;
        }
    }

    public float[] run() {
        do{
            this.step();
        }while(this.currentLayer > -1);
        float[] output = this.outputLayer.getValues(true);
        return output;
    }

    private void triggerInputLayer() {
        this.inputLayer.trigger(layers.get(0));
    }

    private void triggerCurrentLayer() {
        Layer nextLayer = null;
        if(this.currentLayer + 1 < this.layers.size()) {
            nextLayer = this.layers.get(this.currentLayer + 1);
        }else {
            nextLayer = this.outputLayer;
        }
        this.layers.get(this.currentLayer).trigger(nextLayer);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.inputLayer.toString() + "\r\n");
        for(int i = 0; i < this.layers.size(); i++){
            builder.append(this.layers.get(i).toString() + "\r\n");
        }
        builder.append(this.outputLayer.toString() + "\r\n");
        return builder.toString();
    }

    public void save(String name) throws IOException {
        FileOutputStream out = new FileOutputStream(name);
        out.write(this.toString().getBytes());
        out.flush();
        out.close();
    }

}