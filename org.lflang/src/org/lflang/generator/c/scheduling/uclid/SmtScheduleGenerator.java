// An SMT-based static schedule generator powered by Uclid5 and Z3

/*************
Copyright (c) 2019-2022, The University of California at Berkeley.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
***************/

package org.lflang.generator.c.scheduling.uclid;

import org.lflang.ErrorReporter;
import org.lflang.FileConfig;
import org.lflang.Target;
import org.lflang.TargetConfig;
import org.lflang.generator.CodeBuilder;
import org.lflang.generator.GeneratorBase;
import org.lflang.generator.TargetTypes;
import org.lflang.lf.Action;
import org.lflang.lf.VarRef;

/**
 * An SMT-based static schedule generator powered by Uclid5 and Z3
 * 
 * @author {Shaokai Lin <shaokai@berkeley.edu>}
 */
public class SmtScheduleGenerator extends GeneratorBase {
    ////////////////////////////////////////////
    //// Protected fields
    
    /** The main place to put generated code. */
    protected CodeBuilder code = new CodeBuilder();

    ////////////////////////////////////////////
    //// Private fields
    public SmtScheduleGenerator(FileConfig fileConfig, ErrorReporter errorReporter) {
        super(fileConfig, errorReporter);
    }

    public CodeBuilder generateScheduleCode() {
        CodeBuilder scheduleCode = new CodeBuilder();

        return scheduleCode;
    }

    ////////////////////////////////////////////
    //// Public methods.            
    /** Returns the Target enum for this generator */
    @Override
    public Target getTarget() {
        return Target.C;
    }

    @Override
    public TargetTypes getTargetTypes() {
        throw new UnsupportedOperationException("TODO: auto-generated method stub");
    }

    @Override
    public String getNetworkBufferType() {
        throw new UnsupportedOperationException("TODO: auto-generated method stub");
    }

    @Override
    public String generateDelayGeneric() {
        throw new UnsupportedOperationException("TODO: auto-generated method stub");
    }

    @Override
    public String generateDelayBody(Action action, VarRef port) { 
        throw new UnsupportedOperationException("TODO: auto-generated method stub");
    }

    @Override
    public String generateForwardBody(Action action, VarRef port) {
        throw new UnsupportedOperationException("TODO: auto-generated method stub");
    }
}