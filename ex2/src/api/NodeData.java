package api;

/**
 * This class implements node_data interface that represents
 * the set of operations applicable on a 
 * node (vertex) in a (directional) weighted graph.
 */
public class NodeData implements node_data {
	
    private int key, tag;
    private double weight;
    private String info;
    private geo_location pos;

    /**
	 * Constructor that copies the key.
	 * @param key - the key that needs to be copied
	 */
    public NodeData(int key) {
        this.key = key;
        this.weight = 0;
    }
    
    /**
	 * Returns the key (id) associated with this node.
	 * @return int key
	 */
    @Override
    public int getKey() {
        return this.key;
    }

    /** Returns the location of this node, if
	 * none return null.
	 * @return geo_location location
	 */
    @Override
    public geo_location getLocation() {
        return this.pos;
    }

    /** Allows changing this node's location.
	 * @param p - new new location  (position) of this node.
	 */
    @Override
    public void setLocation(geo_location p) {
        this.pos = p;
    }

    /**
	 * Returns the weight associated with this node.
	 * @return double weight
	 */
    @Override
    public double getWeight() {
        return this.weight;
    }

    /**
	 * Allows changing this node's weight.
	 * @param w - the new weight
	 */
    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    /**
	 * Returns the remark (meta data) associated with this node.
	 * @return String info
	 */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
	 * Allows changing the remark (meta data) associated with this node.
	 * @param s - the new info
	 */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used be algorithms.
	 * @return int tag
	 */
    @Override
    public int getTag() {
        return this.tag;
    }

    /** 
	 * Allows setting the "tag" value for temporal marking an node - common
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    public String toString() {
        return "key: " + this.key;
    }
}