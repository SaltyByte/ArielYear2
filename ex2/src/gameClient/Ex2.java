package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;


import javax.swing.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Ex2 implements Runnable{
   private static MyFrame _win;
   private static Arena _ar;

    public static void main(String[] t) {
        Thread client = new Thread(new Ex2());
        client.start();
    }
    @Override
    public void run() {
        int scenario_num = 0;
        game_service game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
        directed_weighted_graph gameGraph = jsonToGraph(game.getGraph());

        initGame(game);

        game.startGame();

        while (game.isRunning()) {
            moveAgents(game, gameGraph);
            _win.repaint();
            try {
                Thread.sleep((game.timeToEnd() / 200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
        game.stopGame();
    }


    public static long timeToSleep(double speed) {
        if (speed == 2.0) {
            return 85;
        }
        if (speed == 5.0) {
            return 70;
        }
        return 100;
    }

    public static void moveAgents(game_service game, directed_weighted_graph gameGraph) {
        String jsonAgents = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(jsonAgents, gameGraph);
        _ar.setAgents(agents);
        List<CL_Pokemon> newPokemons = Arena.json2Pokemons(game.getPokemons());
        for (CL_Pokemon pokemon : newPokemons) {
            Arena.updateEdge(pokemon, gameGraph);
        }
        _ar.setPokemons(newPokemons);
        game.move();
        for (CL_Agent agent : agents) {
            int dest = agent.getNextNode();
            int src = agent.getSrcNode();
            int id = agent.getID();
            if (dest == -1) {
                edge_data toEdge = bestNextEdge(newPokemons, gameGraph, agent);
                int toNode = nextNode(gameGraph,src,toEdge.getSrc());
                if (src == toEdge.getSrc()) {
                    toNode = toEdge.getDest();
                }
                game.chooseNextEdge(id, toNode);
                System.out.println(game);
                System.out.println("Agent:(" + id + ") " + "score: " + agent.getValue() + " Moving from node: " + src + " to node: " + toNode + " Speed: " + agent.getSpeed());
            }
        }
    }

    private static int nextNode(directed_weighted_graph graph, int src, int dest) {
        dw_graph_algorithms algoGraph = new DWGraph_Algo();
        algoGraph.init(graph);
        List<node_data> shortestPath = algoGraph.shortestPath(src, dest);
        if (shortestPath.size() == 1){
            return  shortestPath.get(0).getKey();
        }
        return shortestPath.get(1).getKey();
    }

    private static edge_data insertAgents(game_service game, directed_weighted_graph gameGraph) {
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(game.getPokemons());
        double maxValue = 0;
        CL_Pokemon bestPokemon = null;
        for (CL_Pokemon pokemon: pokemons) {
            if (maxValue < pokemon.getValue()) {
                maxValue = pokemon.getValue();
                bestPokemon = pokemon;
            }
        }
        CL_Pokemon pokemonEdge = bestPokemon;
        Arena.updateEdge(pokemonEdge,gameGraph);
        return pokemonEdge.get_edge();
    }

    private static edge_data bestNextEdge(List<CL_Pokemon> pokemons, directed_weighted_graph gameGraph, CL_Agent agent) {
        // if agent already moving to the selected pokemon
        double maxValue = 0;
        CL_Pokemon bestPokemon = null;
        if (pokemons.size() > 2) {
            pokemons = getClosestPokemons(pokemons, agent, gameGraph);
        }
        for (CL_Pokemon pokemon : pokemons) {
            if (maxValue < pokemon.getValue()) {
                maxValue = pokemon.getValue();
                bestPokemon = pokemon;
            }
        }
        CL_Pokemon pokemonEdge = bestPokemon;
        Arena.updateEdge(pokemonEdge,gameGraph);
        return pokemonEdge.get_edge();
    }

    private static List<CL_Pokemon> getClosestPokemons(List<CL_Pokemon> pokemons, CL_Agent agent,directed_weighted_graph gameGraph){
        node_data srcNode = gameGraph.getNode(agent.getSrcNode());
        List <CL_Pokemon> bestPokemonList = new ArrayList<>();

        while(bestPokemonList.size() < 2 && pokemons.size() > 2) {
            double shortestDistance = Double.POSITIVE_INFINITY;
            CL_Pokemon closestPokemon = null;
            for (CL_Pokemon pokemon : pokemons) {
                if (shortestDistance > srcNode.getLocation().distance(pokemon.getLocation()) && !bestPokemonList.contains(pokemon)) {
                    shortestDistance = srcNode.getLocation().distance(pokemon.getLocation());
                    closestPokemon = pokemon;
                }
            }
            bestPokemonList.add(closestPokemon);
        }
    return bestPokemonList;
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
        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gameGraph);
        _ar.setPokemons(pokemons);
        _win = new MyFrame("My Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);
        _win.setResizable(true);
        //_win.show();
        _win.setVisible(true);


        String gameString = game.toString();
        JSONObject gameJsonObject;
        try {
            gameJsonObject = new JSONObject(gameString);
            JSONObject gameJsonServer = gameJsonObject.getJSONObject("GameServer");
            int agentNumber = gameJsonServer.getInt("agents");
            for (int i = 0; i < agentNumber; i++) {
                game.addAgent(insertAgents(game, gameGraph).getSrc());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                geo_location location = new GeoLocation(Double.parseDouble(posString[0]),Double.parseDouble(posString[1]),Double.parseDouble(posString[2]));
                n.setLocation(location);
                graph.addNode(n);
            }
            for (JsonElement edge : edges) {
                int src = edge.getAsJsonObject().get("src").getAsInt();
                int dest = edge.getAsJsonObject().get("dest").getAsInt();
                double weight = edge.getAsJsonObject().get("w").getAsDouble();
                graph.connect(src,dest,weight);
            }
            return graph;
        }
    }
}



