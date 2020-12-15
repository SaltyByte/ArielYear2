package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class Ex2 implements Runnable {
    private static MyPanel panel = new MyPanel();
    private static JFrame frame;
    private static Arena arena;
    private static HashMap<Integer, Integer> agentToPokemon = new HashMap<>();
    private static HashMap<Integer, CL_Pokemon> lastLocation = new HashMap<>();

    public static void main(String[] t) {
        Thread server = new Thread(new Ex2());
        server.start();
    }

    @Override
    public void run() {
        int scenario_num = 20;
        game_service game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
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
        try {
            if (agentList.size() == 1) {
                time = 50;
            }
            else if (agentList.size() == 2) {
                time = 67;
            }
            else {
                time = 75;
            }

            for (CL_Agent agent : agentList) {
                int calcMoveSpeed = (int)(agentList.size() * agent.getSpeed() * time);
                long agentTTW;
                int src = agent.getSrcNode();
                int id = agent.getID();
                edge_data toEdge = bestNextEdge(pokemonList, agent, gameGraph);
                if (toEdge == null) {
                    continue;
                }
                node_data nextNode = nextNode(gameGraph, src, toEdge.getSrc());
                node_data destNode = nextNode(gameGraph, src, toEdge.getDest());

                if (toEdge.getSrc() == src) {
                    game.chooseNextEdge(id, toEdge.getDest());
                    CL_Pokemon pokemon = getClosestPokemon(pokemonList, agent, gameGraph);
                    double edgeDistance = nextNode.getLocation().distance(destNode.getLocation());
                    double distanceToPokemon = gameGraph.getNode(agent.getSrcNode()).getLocation().distance(pokemon.getLocation());
                    double pokemonDiffOnEdge = distanceToPokemon / edgeDistance;
                    double pokemonLocation = pokemonDiffOnEdge * toEdge.getWeight();
                    agentTTW = (long) ((pokemonLocation / agent.getSpeed()) * calcMoveSpeed);
                    lastLocation.put(id, pokemon);
                } else if (agent.getNextNode() != lastLocation.get(id).get_edge().getDest()) {
                    game.chooseNextEdge(id, nextNode.getKey());
                    geo_location agentLocation = agent.getLocation();
                    if (agentLocation.distance(gameGraph.getNode(agent.getSrcNode()).getLocation()) < 0.0001) {
                        edge_data currEdge = gameGraph.getEdge(agent.getSrcNode(), nextNode.getKey());
                        agentTTW = (long) ((currEdge.getWeight() / agent.getSpeed()) * 1000);
                    }
                    else {
                        double edgeDistance = gameGraph.getNode(agent.getSrcNode()).getLocation().distance(destNode.getLocation());
                        double distanceFromAgent = destNode.getLocation().distance(agent.getLocation());
                        double agentDiffOnEdge = distanceFromAgent / edgeDistance;
                        double pokemonLocation = agentDiffOnEdge * toEdge.getWeight();
                        agentTTW = (long) ((pokemonLocation / agent.getSpeed()) * 1000);
                    }

                } else {
                    double edgeDistance = lastLocation.get(id).getLocation().distance(destNode.getLocation());
                    double distanceFromPokemon = destNode.getLocation().distance(lastLocation.get(id).getLocation());
                    double pokemonDiffOnEdge = distanceFromPokemon / edgeDistance;
                    double pokemonLocation = pokemonDiffOnEdge * toEdge.getWeight();
                    agentTTW = (long) ((pokemonLocation / agent.getSpeed()) * calcMoveSpeed);
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


    private static node_data nextNode(directed_weighted_graph graph, int src, int dest) {
        dw_graph_algorithms algoGraph = new DWGraph_Algo();
        algoGraph.init(graph);
        List<node_data> shortestPath = algoGraph.shortestPath(src, dest);
        if (shortestPath.size() == 1) {
            return shortestPath.get(0);
        }
        return shortestPath.get(1);
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
                agentToPokemon.put(i, pokemons.get(i).get_edge().getSrc());
                game.chooseNextEdge(i, pokemons.get(i).get_edge().getSrc());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static edge_data bestNextEdge(List<CL_Pokemon> pokemons, CL_Agent currAgent, directed_weighted_graph gameGraph) {
        CL_Pokemon pokemon = getClosestPokemon(pokemons, currAgent, gameGraph);
        if (pokemon == null){
            return null;
        }
        agentToPokemon.put(currAgent.getID(), pokemon.get_edge().getSrc());
        return pokemon.get_edge();
    }


    private static CL_Pokemon getClosestPokemon(List<CL_Pokemon> pokemons, CL_Agent
            currAgent, directed_weighted_graph gameGraph) {
        dw_graph_algorithms graphAlgo = new DWGraph_Algo();
        graphAlgo.init(gameGraph);
        double shortestPathDist = Double.POSITIVE_INFINITY;
        CL_Pokemon closestPokemon = null;
        for (CL_Pokemon pokemon : pokemons) {
            boolean isAfter = false;
            for (int agent : agentToPokemon.keySet()) {
                if (agentToPokemon.get(agent) == pokemon.get_edge().getSrc() && currAgent.getID() != agent) {
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
        panel.update(arena);
//        frame.pack();
        frame.setTitle("I Wanna Be The Very Best, Like No One Ever Was.");
        frame.add(panel);
        frame.setSize(1000, 700);
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



