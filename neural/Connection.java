package neural;
public class Connection {
    
    public int target;
    public float weight;

    public static Connection mutateFromParent(Connection parent) {
        Connection child = new Connection(parent.target, parent.weight + (0.5 - Math.random()));
        return child;
    }

    public Connection(int target, double weight) {
        this.target = target;
        this.weight = (float) weight;
    }

    public Connection(int target, float weight) {
        this.target = target;
        this.weight = weight;
    }

    public Connection(int target) {
        this.target = target;
        this.weight = (float) Math.random();
    }

    public void trigger(Layer nextLayer, float value) {
        nextLayer.neurons[this.target].value += value * this.weight;
    }

}
