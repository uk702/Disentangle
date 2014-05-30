package net.walend.scalagraph.minimizer

import net.walend.digraph.semiring.SemiringSupport

import scalax.collection.GraphPredef.EdgeLikeIn
import scalax.collection.Graph

/**
 * Convert a scala-graph Graph to a Digraph of Labels.
 *
 * @author dwalend
 * @since v0.1.0
 */
object ConvertToLabelDigraph {

  /**
   * Create a digraph of Labels from an arbitrary Digraph.
   *
   * @return a label edge list, a node list, and support.semiring.O to build a net.walend.digraph.Digraph.
   */
  import scala.language.higherKinds
  def convert[Node,E[X] <: EdgeLikeIn[X],Label,Key](originalGraph:Graph[Node,E],
                                                    support:SemiringSupport[Label,Key])(
                                                    labelForEdge:originalGraph.EdgeT=>(Node,Node,Label)):(Seq[(Node,Node,Label)],Seq[Node],Label) = {

    val nodes:Seq[Node] = originalGraph.nodes.toOuter.to[Seq]

    val identityEdges:Seq[(Node,Node,Label)] = nodes.map(x => (x,x,support.semiring.I))
    val graphEdges:Seq[(Node,Node,Label)] = originalGraph.edges.map(labelForEdge).to[Seq].filter(x => x._1 != x._2) //no self-edges

    val edges:Seq[(Node,Node,Label)] = identityEdges ++ graphEdges

    (edges,nodes,support.semiring.O)
  }

}