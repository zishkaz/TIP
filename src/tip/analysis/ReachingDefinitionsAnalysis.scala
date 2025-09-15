package tip.analysis

import tip.ast.AstNodeData.DeclarationData
import tip.ast._
import tip.cfg._
import tip.lattices._
import tip.solvers._

/**
 * Base class for live variables analysis.
 */
abstract class ReachingDefinitionsAnalysis(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData) extends FlowSensitiveAnalysis(false) {

  val lattice: MapLattice[CfgNode, PowersetLattice[AStmt]] = new MapLattice(new PowersetLattice())

  val domain: Set[CfgNode] = cfg.nodes

  NoPointers.assertContainsProgram(cfg.prog)
  NoRecords.assertContainsProgram(cfg.prog)

  def transfer(n: CfgNode, s: lattice.sublattice.Element): lattice.sublattice.Element =
    n match {
      case r: CfgStmtNode =>
        r.data match {
          case as: AAssignStmt =>
            as.left match {
              case id: AIdentifier => removedefs(id, s) + as
              case _ => ???
            }
          case varr: AVarStmt => s + varr
          case _ => s
        }
      case _ => s
    }

  private def removedefs(id: AIdentifier, s: lattice.sublattice.Element): lattice.sublattice.Element =
    s.filter {
      case as: AAssignStmt =>
        as.left match {
          case iden: AIdentifier => iden.name != id.name
          case _ => ???
        }
      case varr: AVarStmt => !varr.declIds.exists(id2 => id2.name == id.name)
      case _ => ???
    }
}

class ReachingDefAnalysisSimpleSolver(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
  extends ReachingDefinitionsAnalysis(cfg)
    with SimpleMapLatticeFixpointSolver[CfgNode]
    with ForwardDependencies


class ReachingDefAnalysisWorklistSolver(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
  extends ReachingDefinitionsAnalysis(cfg)
    with SimpleWorklistFixpointSolver[CfgNode]
    with ForwardDependencies