package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class Ex2 implements Runnable {
    private static MyPanel panel = new MyPanel();
    private static JFrame frame;
    private static Arena arena;
    private static HashMap<Integer, CL_Pokemon> agentToPokemon = new HashMap<>();

    public static void main(String[] t) {
        Thread server = new Thread(new Ex2());
        server.start();
        Thread client =  new Thread(new MyPanel());
        client.start();
    }

    @Override
    public void run () {
        int scenario_num = 0;
        game_service game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
        directed_weighted_graph gameGraph = jsonToGraph(game.getGraph());

        initGame(game);
        game.startGame();
        while (game.isRunning()) {
            try {
                Thread.sleep(100);
                moveAgents(game, gameGraph);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(game.toString());
        System.exit(0);
        game.stopGame();
    }

    public static void moveAgents(game_service game, directed_weighted_graph gameGraph) {
        String jsonAgents = game.getAgents();
        List<CL_Agent> agentList = Arena.getAgents(jsonAgents, gameGraph);
        arena.setAgents(agentList);
        List<CL_Pokemon> pokemonList = Arena.json2Pokemons(game.getPokemons());
        for (CL_Pokemon pokemon : pokemonList) {
            Arena.updateEdge(pokemon, gameGraph);
        }
        arena.setPokemons(pokemonList);
        game.move();
        for (CL_Agent agent : agentList) {
            int dest = agent.getNextNode();
            int src = agent.getSrcNode();
            int id = agent.getID();
            if (dest == -1) {
                edge_data toEdge = bestNextEdge(pokemonList, agentList, agent, gameGraph);
                int toNode = nextNode(gameGraph, src, toEdge.getSrc());
                if (src == toEdge.getSrc()) {
                    toNode = toEdge.getDest();
                }
                game.chooseNextEdge(id, toNode);
                System.out.println("Agent:(" + id + ") " + "score: " + agent.getValue() + " Moving from node: " + src + " to node: " + toNode + " Speed: " + agent.getSpeed() + " at edge: " + agentToPokemon.get(agent.getID()).get_edge());
            }
        }
    }

    private static int nextNode(directed_weighted_graph graph, int src, int dest) {
        dw_graph_algorithms algoGraph = new DWGraph_Algo();
        algoGraph.init(graph);
        List<node_data> shortestPath = algoGraph.shortestPath(src, dest);
        if (shortestPath.size() == 1) {
            return shortestPath.get(0).getKey();
        }
        return shortestPath.get(1).getKey();
    }

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
                game.chooseNextEdge(i,pokemons.get(i).get_edge().getSrc());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static edge_data bestNextEdge(List<CL_Pokemon> pokemons, List<CL_Agent> agents, CL_Agent currAgent, directed_weighted_graph gameGraph) {
        CL_Pokemon pokemon = getClosestPokemon(pokemons, currAgent, gameGraph, agents);
        agentToPokemon.put(currAgent.getID(),pokemon);
        return pokemon.get_edge();
    }


    private static CL_Pokemon getClosestPokemon(List<CL_Pokemon> pokemons, CL_Agent currAgent, directed_weighted_graph gameGraph, List<CL_Agent> agents) {
        dw_graph_algorithms graphAlgo = new DWGraph_Algo();
        graphAlgo.init(gameGraph);
        double shortestPathDist = Double.POSITIVE_INFINITY;
        CL_Pokemon closestPokemon = null;
        for (CL_Pokemon pokemon : pokemons) {
            boolean isAfter = false;
            for (CL_Agent agent : agents) {
                if (agentToPokemon.get(agent.getID()).get_edge().getSrc() == pokemon.get_edge().getSrc() && currAgent != agent) {
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

    public static directed_weighted_graph jsonToGraph(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DWGraph_DS.class, new GraphJsonDeserializer());
        Gson gson = builder.create();
        return gson.fromJson(jsonString, DWGraph_DS.class);
    }

    private static void initGame(game_service game) {
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
        directed_weighted_graph gameGraph = jsonToGraph(game.getGraph());
        arena = new Arena();
        frame = new JFrame();
        arena.setGraph(gameGraph);
        arena.setPokemons(pokemons);
        panel.setSize(1000, 700);
        panel.update(arena);
        frame.add(panel);
        frame.setSize(1000,700);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        insertAgents(game, gameGraph);

    }

    private static class GraphJsonDeserializer implements JsonDeserializer<DWGraph_DS> {
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

    private static class ValueComparator implements Comparator<CL_Pokemon> {

        /**
         * Overrides compare method by tag (weight), if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
         * used in the priorityQueue.
         *
         * @param pokemon1 to compare
         * @param pokemon2 to compare
         * @return - int if node1 > node2 return 1, if node1 < node2 return -1, else return 0.
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



