package walend.scalax.semiring

//todo why is this JBoolean?
import java.lang.{Boolean => JBoolean}
import walend.scalax.heap.HeapOrdering
import MLDiEdge._

/**
 * Labels are true if the sink can be reached from the source, false if not.
 *
 * @author dwalend
 * @since v1
 */
object TransitiveClosureSemiring extends Semiring[JBoolean] {


  //identity and annihilator
  def I: JBoolean = true
  def O: JBoolean = false

  def summary(fromThroughToLabel:JBoolean,
              currentLabel:JBoolean):JBoolean = {
    fromThroughToLabel | currentLabel
  }

  def extend(fromThroughLabel:JBoolean,throughToLabel:JBoolean):JBoolean = {
    fromThroughLabel & throughToLabel
  }

}

object TransitiveClosureLabelGraphBuilder extends LabelGraphBuilder[JBoolean] {
  import scalax.collection.Graph
  import scalax.collection.GraphPredef.EdgeLikeIn
  import scala.language.higherKinds

  def semiring = TransitiveClosureSemiring

  def initialLabelFromGraphEdge[N, E[X] <: EdgeLikeIn[X]](originalGraph: Graph[N, E])(edgeT: originalGraph.type#EdgeT): JBoolean = TransitiveClosureSemiring.I
}

final case class TransitiveClosureHeapKey(label:Boolean, state:Int)

object TransitiveClosureHeapKey {
  val TrueKey = TransitiveClosureHeapKey(true,1)
  val FalseKey = TransitiveClosureHeapKey(false,0)
  val TopKey = TransitiveClosureHeapKey(true,2)

  def keyForLabel(label:JBoolean):TransitiveClosureHeapKey = {
    if(label) TrueKey
    else FalseKey
  }
}

/**
 * A heap ordering that puts true above false.
 */
object TransitiveClosureHeapOrdering extends HeapOrdering[TransitiveClosureHeapKey] {

  def lteq(x: TransitiveClosureHeapKey, y: TransitiveClosureHeapKey): Boolean = {
    x.state <= y.state
  }

  /**
   * @return Some negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second, or None if they can't be compared

   */
  def tryCompare(x: TransitiveClosureHeapKey, y: TransitiveClosureHeapKey): Option[Int] = {
    Some(x.state - y.state)
  }

  /**
   * @throws IllegalArgumentException if the key is unusable
   */
  def checkKey(key: TransitiveClosureHeapKey): Unit = {
    //sealed class. Nothing to check.
  }

  /**
   * Minimum value for the DoubleHeap
   */
  def AlwaysTop:TransitiveClosureHeapKey = TransitiveClosureHeapKey.TopKey

  /**
   * A key that will among items on the bottom of the heap. Used primarily to add items that will eventually flow higher.
   */
  def AlwaysBottom: TransitiveClosureHeapKey = TransitiveClosureHeapKey.FalseKey
}

object TransitiveClosure extends GraphMinimizerSupport[JBoolean,TransitiveClosureHeapKey] {
  def semiring = TransitiveClosureSemiring

  def heapKeyForLabel = TransitiveClosureHeapKey.keyForLabel

  def heapOrdering = TransitiveClosureHeapOrdering
}