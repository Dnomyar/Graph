package fr.damienraymond.graph

import scala.collection.immutable.Seq

/**
  * Created by damien on 11/01/2017.
  */
class AdjacencyListDirectedGraph(nodes: List[NodeDirected]) extends IDirectedGraph {

  override lazy val nbEdges: Int = nodes.map(_.successors.size).sum
  override lazy val nbNodes: Int = nodes.size

  override def isArc(x: Int, y: Int): Boolean =
    nodes.exists(node => node.id == x && node.successors.contains(y))

  override def removeArc(x: Int, y: Int): AdjacencyListDirectedGraph =
    new AdjacencyListDirectedGraph(
      nodes.mapIfDefined {
        case node if node.id == x =>
          node.copy(successors = node.successors.filterNot(_ == y))
      }
    )

  override def addArc(x: Int, y: Int): AdjacencyListDirectedGraph =
    new AdjacencyListDirectedGraph(
      nodes.mapIfDefined {
        case node if node.id == x =>
          node.copy(successors = node.successors + nodes.find(_.id == y).get.id)
      }
    )


  override def getSuccessors(node: Int): Set[Int] =
    nodes.find(_.id == node).map(_.successors).getOrElse(List.empty).toSet


  override def getPredecessors(node: Int): Set[Int] =
    nodes.filter(_.successors.exists(_ == node)).map(_.id).toSet

  override def toAdjacencyMatrix: AdjMatGraph =
    AdjMatGraph(
        (0 until nbNodes).map { i =>
          (0 until nbNodes).map { j =>
            isArc(i, j).toInt
          }.toList
        }.toList
      )


  lazy val inverse: AdjacencyListDirectedGraph =
   new AdjacencyListDirectedGraph({
      for{
        node <- nodes
        pred = getPredecessors(node.id)
      } yield NodeDirected(node.id, pred)
    })
}

object AdjacencyListDirectedGraph {
  def apply(mat: AdjMatGraph): AdjacencyListDirectedGraph = {
    val nodes =
      mat.mat.zipWithIndex.collect{
        case (line, i) =>
          val succs =
            line.zipWithIndex.collect {
              case (el, j) if el == 1 => j
            }
          NodeDirected(i, succs.toSet)
      }

    println(nodes)

    new AdjacencyListDirectedGraph(nodes)
  }
}