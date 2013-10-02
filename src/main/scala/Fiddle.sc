
/**
 *
 *
 * @author dwalend
 * @since 8/16/13 4:25 PM
 */

val graph = SomeGraph.graph





FloydWarshall.allPairsShortestPaths(graph)(TransitiveClosureSemiring)(TransitiveClosureLabelGraphBuilder)








FloydWarshall.allPairsShortestPaths(graph)(CountFewestNodesBetweenSemiring)(CountFewestNodesBetweenGraphBuilder)






FloydWarshall.allPairsShortestPaths(graph)(new OneShortestPathSemiring[String])(new OneShortestPathGraphBuilder[String])











val allPairs = FloydWarshall.allPairsShortestPaths(graph)(new AllShortestPathsSemiring[String])(new AllShortestPathsGraphBuilder[String])











println(allPairs.edges.mkString("\n"))







































def outer(node:String):String = graph get outer(node)

import scalax.collection.edge.Implicits._
import scalax.collection.edge.LBase._
object StringLabel extends LEdgeImplicits[String]
import StringLabel._

val path:Option[graph.Path] = graph.get("A").shortestPathTo(graph.get("E"))




































































































































































































































