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
package jdk.graal.compiler.lir.alloc.verifier;

import java.util.ListIterator;

import jdk.graal.compiler.debug.GraalError;
import jdk.graal.compiler.lir.alloc.RegisterAllocationPhase;
import jdk.graal.compiler.lir.phases.AllocationPhase;
import jdk.graal.compiler.lir.phases.AllocationPhase.AllocationContext;
import jdk.graal.compiler.lir.phases.LIRPhase;
import jdk.graal.compiler.lir.phases.LIRPhaseSuite;
import jdk.graal.compiler.lir.stackslotalloc.StackSlotAllocator;

public final class RegAllocVerifierUtil {
    /**
     * Replaces the register allocator with a verifier phase that wraps the register allocator and
     * stack-slot allocator.
     *
     * @param allocationStage the allocation stage being modified
     */
    public static void hookAllocationStage(LIRPhaseSuite<AllocationPhase.AllocationContext> allocationStage, boolean withStackAllocator) {
        LIRPhase<AllocationContext> stackAllocator = removeStackAllocator(allocationStage);

        ListIterator<LIRPhase<AllocationContext>> iterator = allocationStage.findPhase(RegisterAllocationPhase.class);
        GraalError.guarantee(iterator != null, "register allocation phase is missing");
        RegisterAllocationPhase allocator = (RegisterAllocationPhase) iterator.previous();
        iterator.set(new RegAllocVerifierPhase(allocator, stackAllocator));
    }

    public static void hookAllocationStage(LIRPhaseSuite<AllocationContext> allocationStage) {
        hookAllocationStage(allocationStage, true);
    }

    private static LIRPhase<AllocationContext> removeStackAllocator(LIRPhaseSuite<AllocationContext> allocationStage) {
        var iterator = allocationStage.findPhase(StackSlotAllocator.class);
        GraalError.guarantee(iterator != null, "stack-slot allocation phase is missing");
        LIRPhase<AllocationContext> stackAllocator = iterator.previous();
        iterator.remove();
        return stackAllocator;
    }
}
