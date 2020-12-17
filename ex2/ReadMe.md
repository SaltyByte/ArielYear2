Project explanation:
=
This project is split into two parts, one part is a representation of a weighted directed graph and the second part is a "Pokemon Game" which is using some of the functions in part one of the project.

Part One:
=
Part one of the project is located in the api folder which is located in the src folder. 
The api folder contains the classes for the weighted directed graph.


Classes explanation:
=
## NodeData:

NodeData is a class that implements the node_data interface which represents a single node in a graph.
NodeData object has unique key with each node. Each node has info, tag and location.
The tag and info contains data which is used by algorithms.
Location is the representation of the node in the graph, it has x,y,z values which is 3D dimension.

## EdgeData

EdgeData is a class that implements the edge_data interface which represents an edge between two nodes in a graph.
Each edge contains two nodes, src and dest node, also it contains the weight information which is the weight of the edge.


## GeoLocation

GeoLocation is a class that implemets the geo_location interface which represents the location in a plane.
GeoLocation class has three params: x, y, z which are the location on a 3D grid.
This class also has the distance function which calculates the distance in a 3D grid between this and other GeoLocation object.

## DWGraph_DS:

DWGraph_DS is a class that implements the directed_weighted_graph interface which represents a directed weighted graph.
A directed weighted graph is a graph which every edge it has must have a weight which is the "distance" between the two nodes (note it can't be below 0 as in negative distance is not real).
Each edge is directed, which means its one way edge, if a->b then b-/>a, unless you connect b->a then the edge is a two way edge.

The class consists the methods:
**getNode(int key);** return the node associated with the key, if no node return null.
**getEdge(int src, int dest);** returns an edge object of the edge of the two nodes, if no edge return null.
**addNode(node_data n);** adds new node with to the graph. if the node is already there, do nothing.
**connect(int src, int dest, double w);** connects src node to dest with an edge with the given weight, this function creates an edge object.
**getV();** return a collection of all the nodes in the graph.
**getE(int node_id);** return a collection of all the edges of the node_id node.
**removeNode(int key);** removes and returns the node with the associated key from the graph and deletes all the edges connected to it.
**removeEdge(int src, int dest);** removes and returns the edge connected with src and dest.
**nodeSize();** returns number of nodes in the graph.
**edgeSize();** returns number of edges in the graph.
**getMC();** return number of changes made in the graph.

## DWGraph_Algo:

DWGraph_Algo is a class that implements the directed_graph_algorithms interface which represents algorithms to be used on a directed weighted graph.

The class consists the methods:
**init(weighted_graph g);** Initiate the directed weighted graph with the DWGraph_Algo object.
**getGraph();** Returns the graph associated with the DWGraph_Algo object.
**copy();** Returns a deep copied graph.
**isConnected();** Return true if all the nodes in the graph are connected, if there is a valid path between each node in the graph, else returns false.
**shortestPathDist(int src, int dest);** Returns the sum of the weights in the shortest path between src and dest nodes, if no valid path return -1.
**shortestPath(int src, int dest);** Returns a list of the shortest path between src and dest nodes, else return null.
**save(String file);** Saves the graph associated with the DWGraph_Algo object in a Json format.
**load(String file);** Loads a new graph from a text file. Text must be in Json format to load. Loads the new graph to the DWGraph_Algo object .

Data Structures explanation:
=
## HashMap:

HashMap is a map used to store mapping of key-value pairs.
HashMap in Java works on hashing principles. It is a data structure which allows us to store object and retrieve it in constant time O(1) provided we know the key.
In hashing, hash functions are used to link key and value in HashMap.
This is useful to this project because we can in time complexity of O(1) find any node in the graph because every key is unique.

## Linked List:

Similar to arrays in Java, LinkedList is a linear data structure.
However, LinkedList elements are not stored in contiguous locations like arrays, they are linked with each other using pointers.
Each element of the LinkedList has the reference(address/pointer) to the next element of the LinkedList.

## Array List:

An ArrayList is a re-sizable array, also called a dynamic array.
It grows its size to accommodate new elements and shrinks the size when the elements are removed.
ArrayList internally uses an array to store the elements.
Just like arrays, It allows you to retrieve the elements by their index.
Java ArrayList is an ordered collection.
It maintains the insertion order of the elements.

Algorithms explanation:
=
## Dijkstra Algorithm:

Dijkstra's algorithm is an algorithm for finding the shortest path between two nodes in a graph.
Dijkstra's algorithm on time complexity of O(n+v * log(v)) when n is number of nodes and v is number of edges in the graph.
The algorithm first "tags" all the nodes in the graph as infinite and the source node as 0.
Then the algorithm checks the shortest path between each node to itself with the help of tags.
The algorithm uses the priorityQueue to take the lowest path as he compares all the nodes.
The algorithm stops when all the nodes in the connected graph are with updated tag.
If a node still has a tag of infinite, the graph wasn't connected to it.
Then each node has an updated tag which is the weight or distance between the node and the source.


# Part Two 
Part two of the project will be explained in the wiki of the git-hub page.