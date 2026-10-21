/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.graal.compiler.lir.alloc.verifier.printer;

import jdk.graal.compiler.core.common.cfg.BlockMap;
import jdk.graal.compiler.lir.LIR;
import jdk.graal.compiler.lir.alloc.verifier.RAVInstruction;
import jdk.graal.compiler.lir.alloc.verifier.ValueAllocationState;
import jdk.graal.compiler.lir.alloc.verifier.exceptions.ValueNotInRegisterException;
import jdk.graal.compiler.lir.alloc.verifier.values.RAValue;
import jdk.graal.compiler.lir.util.BlockWorkList;
import jdk.graal.compiler.util.EconomicHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Show only the bare minimum of the issue.
 */
public class AggresivePruneStrategy {
    static class BlockState {
        Set<RAValue> variables;
        Set<RAValue> locations;

        BlockState() {
            variables = new EconomicHashSet<>();
            locations = new EconomicHashSet<>();
        }

        BlockState(BlockState prev) {
            this.variables = new EconomicHashSet<>(prev.variables);
            this.locations = new EconomicHashSet<>(prev.locations);
        }
    }

    public BlockMap<List<RAVInstruction.Base>> prune(LIR lir,
                    BlockMap<List<RAVInstruction.Base>> inputInstructions,
                    Map<RAVInstruction.Base, ValueNotInRegisterException> exceptions) {
        var cfg = lir.getControlFlowGraph();
        BlockMap<BlockState> blockState = new BlockMap<>(cfg);
        var worklist = new BlockWorkList(lir.getBlocks().length);
        for (var block : cfg.getBlocks()) {
            if (block.getSuccessorCount() == 0) {
                blockState.put(block, new BlockState());
                worklist.add(block);
            }
        }

        while (!worklist.isEmpty()) {
            var block = worklist.poll();
            var instructions = inputInstructions.get(block);
            var state = new BlockState(blockState.get(block));

            for (var instruction : instructions.reversed()) {
                var exception = exceptions.get(instruction);
                if (exception != null) {
                    state.locations.add(exception.location);
                    state.variables.add(exception.variable);

                    if (exception.state instanceof ValueAllocationState valueAllocationState) {
                        state.variables.add(valueAllocationState.getRAValue());
                    }

                    /// Is of interest.
                }

                /// Is of interest?
                switch (instruction) {
                    case RAVInstruction.Op op -> {
                        for (int i = 0; i < op.dests.count; i++) {

                        }
                    }
                    case RAVInstruction.LocationMove move -> {
                    }
                    case RAVInstruction.ValueMove move -> {
                    }
                    default -> {
                    }
                }
            }
        }

        BlockMap<List<RAVInstruction.Base>> outputInstructions = new BlockMap<>(cfg);
        for (var block : cfg.getBlocks()) {
            var state = blockState.get(block);
            var instructions = inputInstructions.get(block);

            // Check if instruction is of interest, update state and add to the list accordingly
            // => worklist to stabilize input state
        }

        return outputInstructions;
    }
}
