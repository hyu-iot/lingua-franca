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

import com.microsoft.z3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.xtext.xbase.lib.Exceptions;
import org.lflang.ErrorReporter;
import org.lflang.FileConfig;
import org.lflang.Target;
import org.lflang.TargetConfig;
import org.lflang.generator.CodeBuilder;
import org.lflang.generator.GeneratorBase;
import org.lflang.generator.TargetTypes;
import org.lflang.lf.Action;
import org.lflang.lf.VarRef;
import org.lflang.util.LFCommand;

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

    private CodeBuilder generateUclidEncoding() {
        var uclidCode = new CodeBuilder();

        // The module declaration
        uclidCode.pr("module main {");
        uclidCode.indent();

        // Dummy property (to be removed)
        uclidCode.pr("property dummy : false;");

        // The control block
        uclidCode.pr(String.join("\n", 
            "control {",
            "    v = unroll(0);",
            "    check;",
            "    print_results;",
            "    v.print_cex;",
            "}"
        ));

        // End the module declaration
        uclidCode.unindent();
        uclidCode.pr("}");

        return uclidCode;
    }

    public CodeBuilder generateScheduleCode() {
        CodeBuilder scheduleCode = new CodeBuilder();

        // Generate Uclid encoding
        var tempFolder  = fileConfig.getSrcGenPath() + File.separator + "temp";
        var uclidFile   = tempFolder + File.separator + "schedule.ucl";
        var uclidCode   = this.generateUclidEncoding();
        try {
            uclidCode.writeToFile(uclidFile);
        } catch (IOException e) {
            Exceptions.sneakyThrow(e);
        }

        // Compile Uclid encoding to a SMT file.
        /*
        LFCommand cmdCompileUclid = LFCommand.get(
            "uclid",
            List.of(uclidFile, "-g", "smt"),
            false,
            Paths.get(tempFolder)
        );
        cmdCompileUclid.run();
        */

        // Load the generated file into a string
        String smtFile = tempFolder + File.separator + "smt-property_dummy-v-0001.smt";
        String smtCode = "";
        try {
            smtCode = Files.readString(Paths.get(smtFile), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            Exceptions.sneakyThrow(e);
        }
        System.out.println(smtCode);

        // Remove Uclid variable prefixes using regex.
        smtCode = smtCode.replaceAll("initial_([0-9]+)_", ""); // or "initial_\\d+_", \\ escapes \.
        System.out.println(smtCode);

        // Load the SMT file into the Z3 Java binding.

        // Add optimization objectives.

        // Solve for results.

        // Generate preambles (everything other than the schedule in the .h file)

        // Generate executable worker schedules using the custom instruction set.

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