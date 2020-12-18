package api;

/**
 * This class implements edge_data interface that represents
 * the set of operations applicable on a directional
 * edge(src,dest) in a (directional) weighted graph.
 */
public class EdgeData implements edge_data {
	
    private int src, dest, tag;
    private double weight;
    private String info;

    /**
	 * Constructor that copies the src, dest and weight.
	 * @param src - start node
	 * @param dest - end (target) node
	 * @param weight - the weight of src to dest
	 */
    public EdgeData(int src, int dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    /**
	 * The id of the source node of this edge.
	 * @return int src
	 */
    @Override
    public int getSrc() {
        return this.src;
    }

    /**
	 * The id of the destination node of this edge.
	 * @return int dest
	 */
    @Override
    public int getDest() {
        return this.dest;
    }

    /**
	 * @return the weight of this edge (positive value).
	 */
    @Override
    public double getWeight() {
        return this.weight;
    }

    /**
	 * Returns the remark (meta data) associated with this edge.
	 * @return String info
	 */
    @Override
    public String getInfo() {
        return this.info;
    }

	/**
	 * Allows changing the remark (meta data) associated with this edge.
	 * @param s - the new info
	 */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used by algorithms.
	 * @return int tag
	 */
    @Override
    public int getTag() {
        return this.tag;
    }

    /** 
	 * This method allows setting the "tag" value for temporal marking an edge - common
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    public String toString() {
        return "|src: " + this.src + " dest: " + this.dest + " weight: " + this.weight + "|";
    }
}