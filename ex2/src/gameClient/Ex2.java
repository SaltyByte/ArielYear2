package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

import java.lang.reflect.Type;
import java.util.*;

/**
 * This class implements Runnable interface that can be run as a thread.
 * It is the main class that includes the main function that starts the game and the following functions:
 * 0. run
 * 1. moveAgents
 * 2. calcDiffLocation
 * 3. nextNode
 * 4. insertAgents
 * 5. bestNextEdge
 * 6. getClosestPokemon
 * 7. jsonToGraph
 * 8. initGame
 */
public class Ex2 implements Runnable {
	private static MyPanel panel = new MyPanel();
	private static JFrame frame;
	private static Arena arena;
	public static int loginID;
	public static int scenarioLevel;
	public static Thread server;
	private static HashMap <Integer, CL_Pokemon> agentToPokemon = new HashMap<>();
	private static HashMap <Integer, CL_Pokemon> lastPokemonLocation = new HashMap<>();

	/**
	 * The main function starts the game by receiving in command line the following two arguments.
	 * @param args - the login ID, and the scenario level
	 */
	public static void main(String[] args) {
		server = new Thread(new Ex2());
		if (args.length == 2) {
			loginID = Integer.parseInt(args[0]);
			scenarioLevel = Integer.parseInt(args[1]);
			server.start();
		}
		else {
			new loginFrame();
		}
	}

	/**
	 * This method overrides the run method of Runnable, it is activated when the thread starts.
	 * It starts the game by loading the graph from the relevant JSON file according to the scenario chosen.
	 * It uses the login ID to login to the game.
	 * Then manages the game by activating relevant functions.
	 */
	@Override
	public void run() {
		game_service game = Game_Server_Ex2.getServer(scenarioLevel);
		game.login(loginID);
		directed_weighted_graph gameGraph = jsonToGraph(game.getGraph());

		initGame(game);
		game.startGame();
		while (game.isRunning()) {
			List<String> info = new ArrayList<>();
			info.add("" + game.toString());
			arena.setTime(game.timeToEnd());
			arena.set_info(info);
			moveAgents(game, gameGraph);
		}
		System.out.println(game.toString());
		System.exit(0);
		game.stopGame();
	}

	/**
	 * This method move the agents in the graph of the game to capture the pokemons.
	 * It moves the agents in an optimized way, by calculating how long to wait between each move.
	 * @param game - the game service
	 * @param gameGraph - the directed weighted graph of the game
	 */
	public static void moveAgents(game_service game, directed_weighted_graph gameGraph) {
		String jsonAgents = game.getAgents();
		List<CL_Agent> agentList = Arena.getAgents(jsonAgents, gameGraph);
		arena.setAgents(agentList);
		List<CL_Pokemon> pokemonList = Arena.json2Pokemons(game.getPokemons());
		for (CL_Pokemon pokemon : pokemonList) {
			Arena.updateEdge(pokemon, gameGraph);
		}
		arena.setPokemons(pokemonList);
		long timeToWait = Long.MAX_VALUE;
		int time;

		try { // best so far 75,80,100
			if (agentList.size() == 1) {
				time = 75;
			}
			else if (agentList.size() == 2) {
				time = 80;
			}
			else {
				time = 100;
			}

			for (CL_Agent agent : agentList) {
				int calcMoveSpeed = (int)(agentList.size() * (agent.getSpeed() / 2 + 2) * time);
				long agentTTW;
				int src = agent.getSrcNode();
				int id = agent.getID();
				edge_data toEdge = bestNextEdge(pokemonList, agent, gameGraph);
				if (toEdge == null) {
					continue;
				}
				node_data nextNode = nextNode(gameGraph, src, toEdge.getSrc());

				if (toEdge.getSrc() == src) {
					game.chooseNextEdge(id, toEdge.getDest());
					CL_Pokemon pokemon = agentToPokemon.get(agent.getID());
					node_data srcNode = gameGraph.getNode(pokemon.get_edge().getSrc());
					node_data destNode = gameGraph.getNode(pokemon.get_edge().getDest());
					double pokemonLocation = calcDiffLocation(srcNode,destNode,srcNode.getLocation(), pokemon.getLocation(),gameGraph);
					agentTTW = (long) ((pokemonLocation / agent.getSpeed()) * calcMoveSpeed);
					lastPokemonLocation.put(id, pokemon);
				}
				else if (agent.getNextNode() != lastPokemonLocation.get(id).get_edge().getDest()) {
					game.chooseNextEdge(id, nextNode.getKey());
					if (agent.getLocation().distance(gameGraph.getNode(agent.getSrcNode()).getLocation()) < 0.0001) {
						node_data srcNode = gameGraph.getNode(agent.getSrcNode());
						node_data destNode = gameGraph.getNode(nextNode.getKey());
						double agentLocation = calcDiffLocation(srcNode,destNode, srcNode.getLocation(), destNode.getLocation(),gameGraph);
						agentTTW = (long) ((agentLocation / agent.getSpeed()) * 1000);
					}
					else {
						node_data srcNode = gameGraph.getNode(agent.getSrcNode());
						node_data destNode = gameGraph.getNode(agent.getNextNode());
						double agentLocation = calcDiffLocation(srcNode, destNode, agent.getLocation(), destNode.getLocation(), gameGraph);
						agentTTW = (long) ((agentLocation / agent.getSpeed()) * 1000);
					}
				}
				else {
					node_data srcNode = gameGraph.getNode(lastPokemonLocation.get(agent.getID()).get_edge().getSrc());
					node_data destNode = gameGraph.getNode(lastPokemonLocation.get(agent.getID()).get_edge().getDest());
					double agentLocation = calcDiffLocation(srcNode, destNode, lastPokemonLocation.get(agent.getID()).getLocation(),destNode.getLocation(),gameGraph);
					agentTTW = (long) ((agentLocation / agent.getSpeed()) * calcMoveSpeed);
				}
				if (agentTTW < timeToWait) {
					timeToWait = agentTTW;
				}
			}
			Thread.sleep(timeToWait + 5);
			game.move();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method calculate the distance from the current position of the agent to the target location.
	 * It returns the ratio of this distance multiplied by the weight of the current edge.
	 * @param srcNode - the start node
	 * @param destNode - the end node
	 * @param fromLocation - the start location
	 * @param toLocation - the end location
	 * @param gameGraph - the directed weighted graph of the game
	 * @return double - the ratio of the calculated distance multiplied by the weight of the current edge
	 */
	private static double calcDiffLocation (node_data srcNode, node_data destNode, geo_location fromLocation, geo_location toLocation, directed_weighted_graph gameGraph) {
		edge_data currEdge = gameGraph.getEdge(srcNode.getKey(),destNode.getKey());
		double edgeLength = srcNode.getLocation().distance(destNode.getLocation());
		double distanceToEnd = fromLocation.distance(toLocation);
		double distanceDiff = distanceToEnd / edgeLength;
		return distanceDiff * currEdge.getWeight();
	}

	/**
	 * This method gets the next node provided by the shortest path algorithm.
	 * it uses the src dest that this method received
	 * and gets the next node (the index 1) in the list of the shortest path.
	 * If the list of the shortest path size is equal to 1, then it gets the first node in the list.
	 * @param graph - the directed weighted graph of the game
	 * @param src - the start node
	 * @param dest - the dest node
	 * @return node_data - the next node in the list that provides the shortest path
	 */
	private static node_data nextNode(directed_weighted_graph graph, int src, int dest) {
		dw_graph_algorithms algoGraph = new DWGraph_Algo();
		algoGraph.init(graph);
		List<node_data> shortestPath = algoGraph.shortestPath(src, dest);
		if (shortestPath.size() == 1) {
			return shortestPath.get(0);
		}
		return shortestPath.get(1);
	}

	/**
	 * This method inserts agents from the JSON into the graph.
	 * @param game - the game service
	 * @param gameGraph - the directed weighted graph of the game
	 */
	private static void insertAgents(game_service game, directed_weighted_graph gameGraph) {
		List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
		for (CL_Pokemon pokemon : pokemons) {
			Arena.updateEdge(pokemon, gameGraph);
		}
		pokemons.sort(new ValueComparator());
		String gameString = game.toString();
		JSONObject gameJsonObject;
		try {
			gameJsonObject = new JSONObject(gameString);
			JSONObject gameJsonServer = gameJsonObject.getJSONObject("GameServer");
			int agentNumber = gameJsonServer.getInt("agents");
			for (int i = 0; i < agentNumber; i++) {
				game.addAgent(pokemons.get(i).get_edge().getSrc());
				agentToPokemon.put(i, pokemons.get(i));
				game.chooseNextEdge(i, pokemons.get(i).get_edge().getSrc());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns the best next edge in the graph.
	 * It uses getClosestPokemon function to get the pokemon that is the closest to the current agent.
	 * It puts in a HashMap the ID of the current agent and the pokemon that the current agent goes to.
	 * @param pokemons - the list of pokemons
	 * @param currAgent - the current agent
	 * @param gameGraph - the directed weighted graph of the game
	 * @return edge_data the best next edge
	 */
	private static edge_data bestNextEdge(List<CL_Pokemon> pokemons, CL_Agent currAgent, directed_weighted_graph gameGraph) {
		CL_Pokemon pokemon = getClosestPokemon(pokemons, currAgent, gameGraph);
		if (pokemon == null){
			return null;
		}
		agentToPokemon.put(currAgent.getID(), pokemon);
		return pokemon.get_edge();
	}

	/**
	 * This method gets the closest pokemon to the current agent by using the shortestPathDist algorithm
	 * to find the closest pokemon.
	 * @param pokemons - the list of pokemons
	 * @param currAgent - the current agent
	 * @param gameGraph - the directed weighted graph of the game
	 * @return CL_Pokemon - the closest pokemon to the current agent
	 */
	private static CL_Pokemon getClosestPokemon(List<CL_Pokemon> pokemons, CL_Agent currAgent, directed_weighted_graph gameGraph) {
		dw_graph_algorithms graphAlgo = new DWGraph_Algo();
		graphAlgo.init(gameGraph);
		double shortestPathDist = Double.POSITIVE_INFINITY;
		CL_Pokemon closestPokemon = null;
		for (CL_Pokemon pokemon : pokemons) {
			boolean isAfter = false;
			for (int agent : agentToPokemon.keySet()) {
				if (agentToPokemon.get(agent).get_edge().getSrc() == pokemon.get_edge().getSrc() && currAgent.getID() != agent) {
					isAfter = true;
					break;
				}
			}
			if (!isAfter && shortestPathDist > graphAlgo.shortestPathDist(pokemon.get_edge().getSrc(), currAgent.getSrcNode())) {
				shortestPathDist = graphAlgo.shortestPathDist(pokemon.get_edge().getSrc(), currAgent.getSrcNode());
				closestPokemon = pokemon;
			}
		}
		return closestPokemon;
	}

	/**
	 * This method loads the JSON string and builds a directed weighted graph
	 * @param jsonString - the JSON string
	 * @return directed weighted graph - returns the directed_weighted_graph that was built.
	 */
	public static directed_weighted_graph jsonToGraph(String jsonString) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(DWGraph_DS.class, new GraphJsonDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(jsonString, DWGraph_DS.class);
	}

	/**
	 * This method initializes the game, it retrieves the pokemons and the game graph from the JSON file,
	 * then updates the graph, the pokemons and the agents to the game. 
	 * @param game - the game service
	 */
	private static void initGame(game_service game) {
		List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
		directed_weighted_graph gameGraph = jsonToGraph(game.getGraph());
		arena = new Arena();
		frame = new JFrame();
		arena.setGraph(gameGraph);
		arena.setPokemons(pokemons);
		panel.update(arena);
		frame.setTitle("I Wanna Be The Very Best, Like No One Ever Was.");
		frame.add(panel);
		frame.setSize(1000, 700);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setVisible(true);

		insertAgents(game, gameGraph);

	}

	/**
	 * This private class implements JsonDeserializer interface for DWGraph_DS,
	 * This is needed for the GsonBuilder to be able to deserialize the graph.
	 */
	private static class GraphJsonDeserializer implements JsonDeserializer<DWGraph_DS> {

		/**
		 * Override the deserialize method for DWGRaph_DS.
		 * @param json - the JSON element
		 * @param arg1 - the type
		 * @param arg2 - the JSON deserialization context
		 * @return DWGraph_DS - returns the graph
		 */
		@Override
		public DWGraph_DS deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			JsonArray edges = jsonObject.get("Edges").getAsJsonArray();
			JsonArray nodes = jsonObject.get("Nodes").getAsJsonArray();
			DWGraph_DS graph = new DWGraph_DS();
			for (JsonElement node : nodes) {
				node_data n = new NodeData(node.getAsJsonObject().get("id").getAsInt());
				String pos = node.getAsJsonObject().get("pos").getAsString();
				String[] posString = pos.split(",");
				geo_location location = new GeoLocation(Double.parseDouble(posString[0]), Double.parseDouble(posString[1]), Double.parseDouble(posString[2]));
				n.setLocation(location);
				graph.addNode(n);
			}
			for (JsonElement edge : edges) {
				int src = edge.getAsJsonObject().get("src").getAsInt();
				int dest = edge.getAsJsonObject().get("dest").getAsInt();
				double weight = edge.getAsJsonObject().get("w").getAsDouble();
				graph.connect(src, dest, weight);
			}
			return graph;
		}
	}

	/**
	 * This private class implements Comparator interface and compare values
	 * that are stored in the pokemons to sort the pokemon list by the pokemon values,
	 * so that the highest value of a pokemon will be the first one at the list.
	 * The function insertAgents uses this Comparator to insert the agents
	 * in the game in the location of where the highest pokemons values are placed in.
	 */
	private static class ValueComparator implements Comparator<CL_Pokemon> {

		/**
		 * Overrides compare method by the value of pokemons,
		 * if pokemon1 is larger than pokemon2 return -1,
		 * if pokemon1 is less than pokemon2 return 1, else return 0.
		 * used in the priorityQueue.
		 *
		 * @param pokemon1 to compare
		 * @param pokemon2 to compare
		 * @return - int if pokemon1 is larger than pokemon2 return -1, if pokemon1 is less than pokemon2 return 1, else return 0.
		 */
		@Override
		public int compare(CL_Pokemon pokemon1, CL_Pokemon pokemon2) {
			if (pokemon1.getValue() > pokemon2.getValue()) {
				return -1;
			} else if (pokemon1.getValue() < pokemon2.getValue()) {
				return 1;
			}
			return 0;
		}
	}
}