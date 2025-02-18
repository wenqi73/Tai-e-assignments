/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.solver;

import pascal.taie.analysis.dataflow.analysis.DataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.graph.cfg.CFG;

import java.util.ArrayList;
import java.util.Set;

class IterativeSolver<Node, Fact> extends Solver<Node, Fact> {

    public IterativeSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        ArrayList<Node> changedList = new ArrayList<>();
        cfg.forEach(node -> {
            if (!cfg.isExit(node)) {
                changedList.add(node);
            }
        });
        while (!changedList.isEmpty()) {
            // remove last
            Node current = changedList.remove(changedList.size() - 1);
            Fact out = result.getOutFact(current);
            Set<Node> successors = cfg.getSuccsOf(current);
            successors.forEach(successor -> {
                Fact fact = result.getInFact(successor);
                // update out
                analysis.meetInto(fact, out);
            });
            Fact in = result.getInFact(current);
            // update in
            boolean changed = analysis.transferNode(current, in, out);
            if (changed) {
                changedList.add(0, current);
            }
        }
    }
}
