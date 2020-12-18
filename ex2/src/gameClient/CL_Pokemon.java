package gameClient;
import api.edge_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * This class includes information about pokemon in the game.
 */
public class CL_Pokemon {
	private edge_data _edge;
	private double _value;
	private int _type;
	private Point3D _pos;
	private double min_dist;
	private int min_ro;
	
	/**
	 * Constructor
	 * @param p - Point3D
	 * @param t - int
	 * @param v - double
	 * @param s - double
	 * @param e - edge_data
	 */
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		_type = t;
	//	_speed = s;
		_value = v;
		set_edge(e);
		_pos = p;
		min_dist = -1;
		min_ro = -1;
	}
	
	/**
	 * Init pokemon from JSON string.
	 * @param json - String
	 * @return CL_Pokemon - the pokemon created
	 * 
	 */
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	public String toString() {return "F:{v="+_value+", t="+_type+"}";}
	
	/**
	 * Get the edge of the pokemon.
	 * @return edge_data - returns the edge 
	 */
	public edge_data get_edge() {
		return _edge;
	}

	/**
	 * Set the edge of the pokemon.
	 * @param _edge - edge_data 
	 */
	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	/**
	 * Get the location of the pokemon.
	 * @return Point3D - returns the location 
	 */
	public Point3D getLocation() {
		return _pos;
	}
	/**
	 * Get the type of the pokemon.
	 * @return int - returns the type 
	 */
	public int getType() {return _type;}

	//	public double getSpeed() {return _speed;}

	/**
	 * Get the value of the pokemon.
	 * @return double - returns the value of the pokemon 
	 */
	public double getValue() {return _value;}

	/**
	 * Get the minimum distance.
	 * @return double - returns the minimum distance 
	 */
	public double getMin_dist() {
		return min_dist;
	}

	/**
	 * Set the minimum distance.
	 * @param mid_dist - double
	 */
	public void setMin_dist(double mid_dist) {
		this.min_dist = mid_dist;
	}

	/**
	 * Get the minimum ro.
	 * @return int - returns the minimum ro
	 */
	public int getMin_ro() {
		return min_ro;
	}

	/**
	 * Set the minimum ro.
	 * @param min_ro - int
	 */
	public void setMin_ro(int min_ro) {
		this.min_ro = min_ro;
	}
}