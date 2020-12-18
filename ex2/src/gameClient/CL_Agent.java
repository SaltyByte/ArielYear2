package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * This class includes information about agent in the game.
 */
public class CL_Agent {
		public static final double EPS = 0.0001;
		private static int _count = 0;
		private static int _seed = 3331;
		private int _id;
	//	private long _key;
		private geo_location _pos;
		private double _speed;
		private edge_data _curr_edge;
		private node_data _curr_node;
		private directed_weighted_graph _gg;
		private CL_Pokemon _curr_fruit;
		private long _sg_dt;
		
		private double _value;
		
		/**
		 * Constructor
		 * @param g - directed_weighted_graph
		 * @param start_node - int
		 */
		public CL_Agent(directed_weighted_graph g, int start_node) {
			_gg = g;
			setMoney(0);
			this._curr_node = _gg.getNode(start_node);
			_pos = _curr_node.getLocation();
			_id = -1;
			setSpeed(0);
		}
		
		/**
		 * Updates the agent information from the JSON String
		 * @param json - String
		 */
		public void update(String json) {
			JSONObject line;
			try {
				// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
				line = new JSONObject(json);
				JSONObject ttt = line.getJSONObject("Agent");
				int id = ttt.getInt("id");
				if(id==this.getID() || this.getID() == -1) {
					if(this.getID() == -1) {_id = id;}
					double speed = ttt.getDouble("speed");
					String p = ttt.getString("pos");
					Point3D pp = new Point3D(p);
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					double value = ttt.getDouble("value");
					this._pos = pp;
					this.setCurrNode(src);
					this.setSpeed(speed);
					this.setNextNode(dest);
					this.setMoney(value);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * Get the src node of the agent.
		 * @return src node - int
		 */
		public int getSrcNode() {return this._curr_node.getKey();}
		
		/**
		 * Returns the information of the agent as a string JSON.
		 * @return string - the JSON string
		 */
		public String toJSON() {
			int d = this.getNextNode();
			String ans = "{\"Agent\":{"
					+ "\"id\":"+this._id+","
					+ "\"value\":"+this._value+","
					+ "\"src\":"+this._curr_node.getKey()+","
					+ "\"dest\":"+d+","
					+ "\"speed\":"+this.getSpeed()+","
					+ "\"pos\":\""+_pos.toString()+"\""
					+ "}"
					+ "}";
			return ans;	
		}
		
		/**
		 * Set the money.
		 * @param v - double
		 */
		private void setMoney(double v) {_value = v;}
	
		/**
		 * Set the next node for the agent.
		 * @param dest - the destination node
		 * @return boolean - Returns true if was set, else returns false
		 */
		public boolean setNextNode(int dest) {
			boolean ans = false;
			int src = this._curr_node.getKey();
			this._curr_edge = _gg.getEdge(src, dest);
			if(_curr_edge!=null) {
				ans=true;
			}
			else {_curr_edge = null;}
			return ans;
		}
		
		/**
		 * Set the current node for the agent.
		 * @param src - the source node
		 */
		public void setCurrNode(int src) {
			this._curr_node = _gg.getNode(src);
		}
		
		/**
		 * Check if the agent is moving.
		 * @return boolean - if the agent is moving, returns true, else returns false
		 */
		public boolean isMoving() {
			return this._curr_edge!=null;
		}
		
		/**
		 * Returns the String of the JSON.
		 * @return String - Returns the String of the JSON
		 */
		public String toString() {
			return toJSON();
		}
		
		public String toString1() {
			String ans=""+this.getID()+","+_pos+", "+isMoving()+","+this.getValue();	
			return ans;
		}
		
		/**
		 * Returns the ID of the agent.
		 * @return int - the agent ID number
		 */
		public int getID() {
			return this._id;
		}
	
		/**
		 * Gets the location of the agent.
		 * @return geo_location - returns the location of the agent
		 */
		public geo_location getLocation() {
			return _pos;
		}

		/**
		 * Gets the value of the agent.
		 * @return double - returns the value of the agent
		 */
		public double getValue() {
			return this._value;
		}

		/**
		 * Gets the next node of the agent.
		 * @return int - returns the next node of the agent
		 */
		public int getNextNode() {
			int ans = -2;
			if(this._curr_edge==null) {
				ans = -1;}
			else {
				ans = this._curr_edge.getDest();
			}
			return ans;
		}

		/**
		 * Gets the speed of the agent.
		 * @return double - returns the speed of the agent
		 */
		public double getSpeed() {
			return this._speed;
		}

		/**
		 * Sets the speed of the agent.
		 * @param v - double
		 */
		public void setSpeed(double v) {
			this._speed = v;
		}
		
		/**
		 * Gets the current fruit of the agent.
		 * @return CL_Pokemon - returns the current fruit of the agent
		 */
		public CL_Pokemon get_curr_fruit() {
			return _curr_fruit;
		}
		
		/**
		 * Sets the current fruit of the agent.
		 * @param curr_fruit - CL_Pokemon
		 */
		public void set_curr_fruit(CL_Pokemon curr_fruit) {
			this._curr_fruit = curr_fruit;
		}
		
		/**
		 * Sets the SDT of the agent.
		 * @param ddtt - long
		 */
		public void set_SDT(long ddtt) {
			long ddt = ddtt;
			if(this._curr_edge!=null) {
				double w = get_curr_edge().getWeight();
				geo_location dest = _gg.getNode(get_curr_edge().getDest()).getLocation();
				geo_location src = _gg.getNode(get_curr_edge().getSrc()).getLocation();
				double de = src.distance(dest);
				double dist = _pos.distance(dest);
				if(this.get_curr_fruit().get_edge()==this.get_curr_edge()) {
					 dist = _curr_fruit.getLocation().distance(this._pos);
				}
				double norm = dist/de;
				double dt = w*norm / this.getSpeed(); 
				ddt = (long)(1000.0*dt);
			}
			this.set_sg_dt(ddt);
		}
		
		/**
		 * Gets the current edge of the agent.
		 * @return edge_data - returns the current edge of the agent
		 */
		public edge_data get_curr_edge() {
			return this._curr_edge;
		}
		
		/**
		 * Gets the sg_dt of the agent.
		 * @return long - returns the sg_dt of the agent
		 */
		public long get_sg_dt() {
			return _sg_dt;
		}
		
		/**
		 * Returns the sg_dt of the agent.
		 * @param _sg_dt - long
		 */
		public void set_sg_dt(long _sg_dt) {
			this._sg_dt = _sg_dt;
		}
	}