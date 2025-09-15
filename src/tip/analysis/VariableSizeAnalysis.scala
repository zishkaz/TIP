package tip.analysis

import tip.ast.AstNodeData.DeclarationData
import tip.cfg.{CfgNode, IntraproceduralProgramCfg}
import tip.lattices.IntervalLattice
import tip.lattices.IntervalLattice.{MInf, Num, PInf}
import tip.solvers.{WorklistFixpointSolverWithReachabilityAndWidening, WorklistFixpointSolverWithReachabilityAndWideningAndNarrowing}

object   VariableSizeAnalysis {

  object Intraprocedural {

    private val B = Set[Num](0, 1, Byte.MinValue, Byte.MaxValue, Char.MinValue.toInt, Char.MaxValue.toInt, Int.MinValue, Int.MaxValue, MInf, PInf)

    /**
     * Interval analysis, using the worklist solver with init and widening.
     */
    class WorklistSolverWithWidening(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
      extends IntraprocValueAnalysisWorklistSolverWithReachability(cfg, IntervalLattice)
        with WorklistFixpointSolverWithReachabilityAndWidening[CfgNode]
        with IntervalAnalysisWidening {
      override val B = Intraprocedural.B
    }

    /**
     * Interval analysis, using the worklist solver with init, widening, and narrowing.
     */
    class WorklistSolverWithWideningAndNarrowing(cfg: IntraproceduralProgramCfg)(implicit declData: DeclarationData)
      extends IntraprocValueAnalysisWorklistSolverWithReachability(cfg, IntervalLattice)
        with WorklistFixpointSolverWithReachabilityAndWideningAndNarrowing[CfgNode]
        with IntervalAnalysisWidening {
      override val B = Intraprocedural.B

      val narrowingSteps = 5
    }
  }

}