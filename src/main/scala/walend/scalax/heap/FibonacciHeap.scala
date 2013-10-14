package walend.scalax.heap

/**
 * A generic Fibonacci heap
 *
 * @author dwalend
 * @since 10/14/13 10:17 AM
 */

//todo custom Ordering with magic top value
//todo make generic key
class FibonacciHeap[V] //todo implement heap
{
  def isEmpty:Boolean = {
    size==0
  }

  def insert(key:Double,value:V):HeapMember = {
    checkKeyValue(key)

    val fibNode:HeapMember = new HeapMember(value)

    reinsert(key,fibNode)
  }

  def topMember:HeapMember = {
    checkTop()
    top
  }

  def topValue:V = {
    topMember.value
  }
  
  def topKey:Double = {
    checkTop()
    top.getKey
  }

  def takeTop():HeapMember = {
    checkTop()
    val z:HeapMember = top
    while(z.child!=null) {
      val x:HeapMember = z.child
      z.releaseChild(x)
      z.cat(x)
    }
    z.release()

    if(z==z.getRight) {
      top = null
    }
    else {
      top = z.getRight
      consolidate()
    }

    size = size -1
    z.clean()
    z
  }

  def changeKey(key:Double,fibNode:HeapMember):Unit = {
    checkKeyValue(key)
    if(key > fibNode.getKey) {
      remove(fibNode)
      reinsert(key,fibNode)
    }
    else {
      decreaseKey(key,fibNode)
    }
  }

  def remove(fibNode:HeapMember):Unit = {
    decreaseKey(-Double.MaxValue,fibNode)
    takeTop()
  }

  //    public boolean contains(DoubleHeapMember node);
  /*
  public void clear()
  {
      top=null;
      size=0;
  }
  */

  override def toString: String = {
    val builder = new StringBuilder()

    builder.append("FibDoubleHeap size: "+size+"\n")
    builder.append("top: ")
    if(top==null)
    {
      builder.append("null")
    }
    else
    {
      builder.append(top.toString())
    }
    builder.append("\n")

    val it:ChildIterator = iterator()
    while(it.hasNext)
    {
      builder.append(it.next().toString)
      builder.append("\n")
    }
    builder.toString()
  }

  private var top:HeapMember = null
  private var size:Int = 0

  private def reinsert(key:Double,fibNode:HeapMember):HeapMember = {
    checkKeyValue(key)

    fibNode.setKey(key)

    if(top != null) {
      top.cat(fibNode)
    }
    if((top==null)||(fibNode.getKey<top.getKey)) {
      top = fibNode
    }
    size = size +1

    fibNode
  }

  /*
  public FibDoubleHeap(DoubleHeap heap)
  {
      //todo union
      //        union(heap);
  }
  */

  private def cascadingCut(y: HeapMember): Unit = {
    val z: HeapMember = y.getParent
    if (z != null) {
      if (!y.lostChild) {
        y.lostChild = true
      }
      else {
        cut(y, z)
        cascadingCut(z)
      }
    }
  }

  private def consolidate():Unit = {

    import scala.collection.mutable.ArraySeq
    val fibNodes:ArraySeq[HeapMember] = new ArraySeq[HeapMember](size)

    var rootCount:Int = 0
    var x:HeapMember = top
    if(x!=null) {
      do {
        rootCount = rootCount + 1
        x = x.getRight
      }//do
      while(x!=top)
  
      while(rootCount>0) {
        var d:Int = x.childCount

        val next:HeapMember = x.getRight
        while(fibNodes(d) != null) {
          var y:HeapMember = fibNodes(d)
          if(x.getKey>y.getKey) {
            val temp:HeapMember = y
            y = x
            x = temp
          }
          link(y,x)
          fibNodes(d) = null
          d = d+1
        }
        rootCount = rootCount -1
        fibNodes(d) = x
        x = next
        //and this be replaced by x.getRight(); here instead?
      }//while
      top = null
  
      for (i <- 0 until fibNodes.length) {
        if(fibNodes(i)!=null) {
          if(top!=null) {
            fibNodes(i).release()
            top.cat(fibNodes(i))
            if(fibNodes(i).getKey < top.getKey){
              top = fibNodes(i)
            }
          }
          else {
            top = fibNodes(i) 
          }
        }//if fibNodes[i]!=null
      }//for
    }//if top!=null
  }

  /**
  Removes x from the child list of y.
    */
  private def cut(x:HeapMember,y:HeapMember):Unit = {
    y.releaseChild(x)
    top.cat(x)
  }

  /**
  Make y a child of x.
    */
  private def link(y:HeapMember,x:HeapMember):Unit = {
    //remove y from the list of the heap
    y.release()
    
    //make y a child of x
    x.addChild(y)
  }

  private def checkKeyValue(key:Double):Unit = {
    if(!(key > -Double.MaxValue)) {
      throw new IllegalArgumentException("key is "+key+" but must be greater than "+ -Double.MaxValue)
    }
  }

  private def checkTop():Unit = {
    if(top==null) {
      throw new IllegalStateException("The heap is empty.")
    }
  }

  private def decreaseKey(key:Double,fibNode:HeapMember):Unit = {
    fibNode.setKey(key)
    val y:HeapMember = fibNode.getParent
    if((y!=null)&&(fibNode.getKey<y.getKey)){
      cut(fibNode,y)
      cascadingCut(y)
    }
    if(fibNode.getKey<top.getKey){
      top = fibNode
    }
  }

  private class ChildIterator(startNode:HeapMember) {
  
    private var currentNode:HeapMember = null
    private var currentChildIterator:ChildIterator = null
    
    def hasNext:Boolean = {
      if((currentChildIterator!=null)&&(currentChildIterator.hasNext)) {
        return true
      }
      startNode != currentNode
    }
    
    def next():HeapMember = {
      if((currentChildIterator!=null)&&(currentChildIterator.hasNext)) {
        currentChildIterator.next()
      }
      else if(currentNode!=null) {
        val oldCurrentNode = currentNode
        currentNode = currentNode.getRight
        if(currentNode.child!=null){
          currentChildIterator = new ChildIterator(currentNode.child)
        }
        oldCurrentNode
      }
      else
      {
        currentNode = startNode.getRight
        if(startNode.child!=null) {
          currentChildIterator = new ChildIterator(startNode.child)
        }
        startNode
      }
    }
    
    def remove():Unit = throw new UnsupportedOperationException
  }

  private def iterator():ChildIterator = {
    new ChildIterator(top)
  }

  //todo move changeKey and remove into here
  //todo more scala-eque access to variables
  class HeapMember(val value:V) {

    private var key: Double = .0
    private var parent: HeapMember = null
    private[FibonacciHeap] var child: HeapMember = null //todo only need accessor outside of member
    private var left: HeapMember = this
    private var right: HeapMember = this
    private[FibonacciHeap] var childCount: Int = 0
    private[FibonacciHeap] var lostChild: Boolean = false
    private var inHeap: Boolean = false

    private def toDebugString: String = {
      val builder: StringBuffer = new StringBuffer
      builder.append("key: " + key + "value: "+value)
      builder.append(" lostChild: " + lostChild)
      builder.append(" left: " + left.getKey)
      builder.append(" right: " + right.getKey)
      if (parent != null) {
        builder.append(" parent: " + parent.getKey)
      }
      if (child != null) {
        builder.append(" child: " + child.getKey)
      }
      builder.append(" childCount: " + childCount)
      builder.append(" inHeap:" + inHeap)
      builder.toString
    }

    override def toString:String = {
      "key: "+key+" value: "+value
    }

    private[FibonacciHeap] def clean():Unit = {
      right = this
      left = this
      parent = null
      child = null
      childCount = 0
      lostChild = false
      key = 0
      inHeap = false
    }

    //todo standard accessors
    def getKey: Double = {
      key
    }

    def isInHeap: Boolean = {
      inHeap
    }

    private[FibonacciHeap] def setKey(key: Double):Unit = {
      this.key = key
      inHeap = true
    }

    private[FibonacciHeap] def release():Unit = {
      getLeft.right = getRight
      getRight.left = getLeft
    }

    private[FibonacciHeap] def cat(node: HeapMember) {
      node.left = this
      node.right = getRight
      right = node
      node.getRight.left = node
    }

    private[FibonacciHeap] def addChild(childNode: HeapMember) {
      if (child == null) {
        child = childNode
        childNode.right = childNode
        childNode.left = childNode
      }
      else {
        child.cat(childNode)
      }
      childNode.parent = this
      childCount += 1
      childNode.lostChild = false
    }

    private[FibonacciHeap] def releaseChild(childNode: HeapMember) {
      childNode.release()
      childCount -= 1
      if (child == childNode) {
        child = childNode.getRight
      }
      if (childCount == 0) {
        child = null
      }
      childNode.parent = null
      childNode.lostChild = true
    }

    private[FibonacciHeap] def getParent: HeapMember = {
      parent
    }

    private[FibonacciHeap] def getLeft: HeapMember = {
      left
    }

    private[FibonacciHeap] def getRight: HeapMember = {
      right
    }
  }
}

object FibonacciHeap {

//todo ordering needs go here

}
