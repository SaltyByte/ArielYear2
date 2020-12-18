Assignment Two Information
=
This repository is split into two parts.  
Part one of the repository is a representation of a directed weighted graph.  
The second part is a pokemon game which uses the part one graph methods and implements them into the game.

Part One Explanation:
=
The first part of the repository is located in the src/api folders and is representing a directed weighted graph.  
A directed weighted graph is a graph which each edge has source and destination nodes connected to it.  
A valid path must start with source node and end with destination node, cant be otherwise.  

Each node contains a value which is unique, cant have same id's in one graph.  
Each edge is connected with two nodes and contains a weight value.  
The weight value is the "distance" or weight the edge has.  

Secondly the api folder contains graph algorithms class which is the algorithms used on the graph.  
The main algorithm is Dijkstra's algorithm, which finds the shortest path between two nodes by weights on the graph.
The main functions are copy (deep copy of another graph), shortestPathDist(returns sum of weights of the shortest path)  
and lastly shortestPath (returns a list of nodes with the  shortest path between two nodes based on weights).


Part Two Explanation:
=
Second part is mainly focused on a pokemon game which is located in the src/gameClient folders.  
The game is based on the part one graphs, first we insert the agents on the graph near the pokemon which has highest value,  
then the agent is going after the closest pokemon which no other agent is going after.  
We must use moves as little as we can, and still get the highest grade possible.  

The agent knows how to follow the closest pokemon via part one graph algorithm which is shortest path.
Depends on the scenario level, the game ends after the time ends.  
The agents use a very accurate algorithm to catch the pokemons.  
When no pokemon on the edge the agent just jumps to the dest node resulting in one move used.  
When there is a pokemon the agent jumps on the edge as low as we can time so we get good move count but still catch the pokemon or more if necessary.  

To run a scenario first we run the Ex2 class, in the User ID text field we enter out id,  
in the Scenario we enter the level number we wish to run.  
After all the information is inserted correctly, press start button and the game should run.  
After the game ends it stops and closses the frame.  
 
