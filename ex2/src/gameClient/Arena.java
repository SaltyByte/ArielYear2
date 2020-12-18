package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph - grabs Pokemons.
 * @author boaz.benmoshe
 *
 */
public class Arena {
	public static final double EPS1 = 0.0001, EPS2=EPS1*EPS1, EPS=EPS2;
	private directed_weighted_graph _gg;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;
	private List<String> _info;
	private long time;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	/**
	 * Default constructor.
	 */
	public Arena() {;
		_info = new ArrayList<String>();
	}
	
	/**
	 * Constructor.
	 * @param g - the adirected_weighted_graph
	 * @param listOfAgents - List of CL_Agent
	 * @param listOfPokemons - List of CL_Pokemon
	 */
	private Arena(directed_weighted_graph g, List<CL_Agent> listOfAgents, List<CL_Pokemon> listOfPokemons) {
		_gg = g;
		this.setAgents(listOfAgents);
		this.setPokemons(listOfPokemons);
	}
	
	/**
	 * Set the pokemons.
	 * @param listOfPokemons - list of pokemons
	 */
	public void setPokemons(List<CL_Pokemon> listOfPokemons) {
		this._pokemons = listOfPokemons;
	}
	
	/**
	 * Set the agents.
	 * @param listOfAgents - list of agents
	 */
	public void setAgents(List<CL_Agent> listOfAgents) {
		this._agents = listOfAgents;
	}
	
	/**
	 * Set the graph.
	 * @param g - directed weighted graph
	 */
	public void setGraph(directed_weighted_graph g) {this._gg =g;}//init();}
	
	/**
	 * Init the arena.
	 */
	private void init( ) {
		MIN=null; MAX=null;
		double x0=0,x1=0,y0=0,y1=0;
		Iterator<node_data> iter = _gg.getV().iterator();
		while(iter.hasNext()) {
			geo_location c = iter.next().getLocation();
			if(MIN==null) {x0 = c.x(); y0=c.y(); x1=x0;y1=y0;MIN = new Point3D(x0,y0);}
			if(c.x() < x0) {x0=c.x();}
			if(c.y() < y0) {y0=c.y();}
			if(c.x() > x1) {x1=c.x();}
			if(c.y() > y1) {y1=c.y();}
		}
		double dx = x1-x0, dy = y1-y0;
		MIN = new Point3D(x0-dx/10,y0-dy/10);
		MAX = new Point3D(x1+dx/10,y1+dy/10);
		
	}
	
	/**
	 * Get the list of agents.
	 * @return List of CL_Agent - returns the list of agents
	 */
	public List<CL_Agent> getAgents() {return _agents;}

	/**
	 * Get the list of pokemons.
	 * @return List of CL_Pokemon - returns the list of pokemons
	 */
	public List<CL_Pokemon> getPokemons() {return _pokemons;}

	/**
	 * Get the graph.
	 * @return directed weighted graph - the graph
	 */
	public directed_weighted_graph getGraph() {
		return _gg;
	}
	
	/**
	 * Get the info of the arena.
	 * @return List of String - returns the list of the info
	 */
	public List<String> get_info() {
		return _info;
	}
	
	/**
	 * Set the info of the arena.
	 * @param _info - List of String
	 */
	public void set_info(List<String> _info) {
		this._info = _info;
	}

	/**
	 * Get the agents of the arena.
	 * @param aa - String
	 * @param gg - dircted_weighted_graph
	 * @return List of CL_Agent - returns the list of agents
	 */
	public static List<CL_Agent> getAgents(String aa, directed_weighted_graph gg) {
		ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
		try {
			JSONObject ttt = new JSONObject(aa);
			JSONArray ags = ttt.getJSONArray("Agents");
			for(int i=0;i<ags.length();i++) {
				CL_Agent c = new CL_Agent(gg,0);
				c.update(ags.get(i).toString());
				ans.add(c);
			}
			//= getJSONArray("Agents");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ans;
	}
	
	/**
	 * Returns a list of pokemons from the JSON string.
	 * @param jsonString - String
	 * @return ArrayList of CL_Pokemon - returns an array list of pokemons
	 */
	public static ArrayList<CL_Pokemon> json2Pokemons(String jsonString) {
		ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
		try {
			JSONObject ttt = new JSONObject(jsonString);
			JSONArray ags = ttt.getJSONArray("Pokemons");
			for(int i=0;i<ags.length();i++) {
				JSONObject pp = ags.getJSONObject(i);
				JSONObject pk = pp.getJSONObject("Pokemon");
				int t = pk.getInt("type");
				double v = pk.getDouble("value");
				//double s = 0;//pk.getDouble("speed");
				String p = pk.getString("pos");
				CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
				ans.add(f);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return ans;
	}
	
	/**
	 * Update the edge of the arena.
	 * @param pokemon - CL_Pokemon
	 * @param g - directed_weighted_graph
	 */
	public static void updateEdge(CL_Pokemon pokemon, directed_weighted_graph g) {
		//	oop_edge_data ans = null;
		Iterator<node_data> itr = g.getV().iterator();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean f = isOnEdge(pokemon.getLocation(), e,pokemon.getType(), g);
				if(f) {pokemon.set_edge(e);}
			}
		}
	}

	/**
	 * Check if position is on edge.
	 * @param pos - geo_location
	 * @param src - geo_location
	 * @param dest - geo_location
	 * @return boolean - returns true if the position given is on the edge, else returns false
	 */
	private static boolean isOnEdge(geo_location pos, geo_location src, geo_location dest) {

		boolean isOnEdge = false;
		double distance = src.distance(dest);
		double sumDistance = src.distance(pos) + pos.distance(dest);
		if(distance>sumDistance-EPS2) {isOnEdge = true;}
		return isOnEdge;
	}
	
	/**
	 * Check if position is on edge.
	 * @param pos - geo_location
	 * @param s - the src node
	 * @param d - the dest node
	 * @param g - directed_weighted_graph
	 * @return boolean - returns true if the position given is on the edge, else returns false
	 */
	private static boolean isOnEdge(geo_location pos, int s, int d, directed_weighted_graph g) {
		geo_location src = g.getNode(s).getLocation();
		geo_location dest = g.getNode(d).getLocation();
		return isOnEdge(pos,src,dest);
	}
	
	/**
	 * Check if position is on edge.
	 * @param pos - geo_location
	 * @param edge - edge_data
	 * @param type - int
	 * @param g - directed_weighted_graph
	 * @return boolean - returns true if the position given is on the edge, else returns false
	 */
	private static boolean isOnEdge(geo_location pos, edge_data edge, int type, directed_weighted_graph g) {
		int src = g.getNode(edge.getSrc()).getKey();
		int dest = g.getNode(edge.getDest()).getKey();
		if(type<0 && dest>src) {return false;}
		if(type>0 && src>dest) {return false;}
		return isOnEdge(pos,src, dest, g);
	}

	/**
	 * Returns the graph range in two dimensions.
	 * @param g - directed_weighted_graph
	 * @return Range2D - returns the graph range in two dimensions
	 */
	private static Range2D GraphRange(directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		double x0=0,x1=0,y0=0,y1=0;
		boolean first = true;
		while(itr.hasNext()) {
			geo_location pos = itr.next().getLocation();
			if(first) {
				x0=pos.x(); x1=x0;
				y0=pos.y(); y1=y0;
				first = false;
			}
			else {
				if(pos.x()<x0) {x0=pos.x();}
				if(pos.x()>x1) {x1=pos.x();}
				if(pos.y()<y0) {y0=pos.y();}
				if(pos.y()>y1) {y1=pos.y();}
			}
		}
		Range xr = new Range(x0,x1);
		Range yr = new Range(y0,y1);
		return new Range2D(xr,yr);
	}
	
	/**
	 * Returns the graph range in two dimensions.
	 * @param g - directed_weighted_graph
	 * @param frame - Range2D
	 * @return Range2Range - returns Range2Range
	 */
	public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
		Range2D world = GraphRange(g);
		Range2Range ans = new Range2Range(world, frame);
		return ans;
	}
	
	/**
	 * Set the time of the arena.
	 * @param time - long
	 */
	public void setTime(long time){
		this.time = time;
	}
	
	/**
	 * Get the time of the arena.
	 * @return long - returns the time of the arena
	 */
	public long getTime() {
		return this.time;
	}
}