Project explanation:
=
This project represents a node, a weighted graph and algorithms classes.  
The src folder contains three interfaces classes and two classes which implements the interfaces.  
The test's folder contains two junit test classes which tests the implemented classes.

Classes explanation:
=
NodeInfo:
-
NodeInfo is a class that implements the node_data interface which represents a single node in a graph.  
NodeInfo object has unique key with each node. Each node has info and tag.  
The tag and info contains data which is used by algorithms.  


WGraph_DS:
-
WGraph_DS is a class that implements the weighted_graph interface which represents a weighted graph.  
A weighted graph is a graph which every edge it has must have a weight which is the "distance" between the two nodes
(note it can't be below 0 as in negative distance is not real).

The class consists the methods:  
getNode(int key); return the node associated with the key, if no node return null.  
hasEdge(int node1, int node2); return true if there's an edge between nodes else false.  
getEdge(int node1, int node2); return the weight of the edge of the two nodes, if no edge return -1.  
addNode(int key); adds new node with the associated key to the graph. if the key is already there, do nothing.  
connect(int node1, int node2, double w); connect two nodes with an edge with the given weight.  
getV(); return a collection of all the nodes in the graph.  
getV(int node_id); return a collection of all the "neighbors" of the node_id node.  
removeNode(int key); removes the node with the associated key from the graph and deletes all the edges connected to it.  
removeEdge(int node1, int node2); removes the edge connected with node1 and node2.  
nodeSize(); return node size in the graph.  
edgeSize(); return edge size in the graph.  
getMC(); return number of changed made in the graph.  



WGraph_Algo:
-
WGraph_Algo is a class that implements the graph_algorithms interface which represents algorithms to be used on a weighted graph.  

The class consists the methods:  
init(weighted_graph g); Initiate the weghted graph with the WGraph_Algo object.  
getGraph(); Returns the graph associated with the WGraph_Algo object.  
copy(); Returns a deep copied graph.  
isConnected(); Return true if all the nodes in the graph are connected, else returns false.  
shortestPathDist(int src, int dest); Returns the sum of the weights in the shortest path.  
shortestPath(int src, int dest); Returns a list of the shortest path.  
save(String file); Saves the graph associated with the WGraph_Algo object as a stream of bits.  
load(String file); Loads an object which is a stream of bits and casts it as a graph.  



Data Structures explanation:
=
HashMap:
-

HashMap is a map used to store mapping of key-value pairs.  
HashMap in Java works on hashing principles. It is a data structure
which allows us to store object and retrieve it in constant time O(1) provided we know the key.  
In hashing, hash functions are used to link key and value in HashMap.  
This is useful to this project because we can in time complexity of O(1) find any node in the graph because every key is unique.  



Linked List:
-

Similar to arrays in Java, LinkedList is a linear data structure.  
However, LinkedList elements are not stored in contiguous locations like arrays,
they are linked with each other using pointers.  
Each element of the LinkedList has the reference(address/pointer) to the next element of the LinkedList.  

Array List:
-

An ArrayList is a re-sizable array, also called a dynamic array.  
It grows its size to accommodate new elements and shrinks the size when the elements are removed.  
ArrayList internally uses an array to store the elements.  
Just like arrays, It allows you to retrieve the elements by their index.  
Java ArrayList is an ordered collection.  
It maintains the insertion order of the elements.  


Algorithms explanation:
=
Dijkstra Algorithm:
-
Dijkstra's algorithm is an algorithm for finding the shortest path between two nodes in a graph.  
Dijkstra's algorithm on time complexity of O(n+v * log(v)) when n is number of nodes and v is number of edges in the graph.  
The algorithm first "tags" all the nodes in the graph as infinite and the source node as 0.  
Then the algorithm checks the shortest path between each node to itself with the help of tags.  
The algorithm uses the priorityQueue to take the lowest path as he compares all the nodes.  
The algorithm stops when all the nodes in the connected graph are with updated tag.  
If a node still has a tag of infinite, the graph wasn't connected to it.  
Then each node has an updated tag which is the weight or distance between the node and the source.  

