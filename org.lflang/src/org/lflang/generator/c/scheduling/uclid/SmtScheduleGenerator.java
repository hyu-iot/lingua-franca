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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.xtext.xbase.lib.Exceptions;
import org.lflang.ErrorReporter;
import org.lflang.FileConfig;
import org.lflang.Target;
import org.lflang.TargetConfig;
import org.lflang.generator.CodeBuilder;
import org.lflang.generator.GeneratorBase;
import org.lflang.generator.ReactionInstance;
import org.lflang.generator.ReactionInstanceGraph;
import org.lflang.generator.ReactorInstance;
import org.lflang.generator.TargetTypes;
import org.lflang.lf.Action;
import org.lflang.lf.VarRef;
import org.lflang.util.FileUtil;
import org.lflang.util.LFCommand;

/**
 * An SMT-based static schedule generator powered by Uclid5 and Z3
 * 
 * @author {Shaokai Lin <shaokai@berkeley.edu>}
 */
public class SmtScheduleGenerator {
    ////////////////////////////////////////////
    //// Protected fields

    /** The current file configuration. */
    protected FileConfig fileConfig;

    /** A error reporter for reporting any errors or warnings during the code generation */
    public ErrorReporter errorReporter;

    /** The main (top-level) reactor instance. */
    public ReactorInstance main;

    /** Reaction instance graph that contains the set of all reactions in the LF program */
    public ReactionInstanceGraph reactionInstanceGraph;

    /** The target properties of the LF program */
    public TargetConfig targetConfig;

    /** The main place to put generated code. */
    protected CodeBuilder code = new CodeBuilder();

    ////////////////////////////////////////////
    //// Private fields
    public SmtScheduleGenerator(FileConfig fileConfig, ErrorReporter errorReporter, ReactorInstance main, TargetConfig targetConfig) {
        this.fileConfig = fileConfig;
        this.errorReporter = errorReporter;
        this.main = main;
        this.reactionInstanceGraph = new ReactionInstanceGraph(this.main, false);
        this.targetConfig = targetConfig;
    }

    private CodeBuilder generateUclidEncoding() {
        var uclidCode = new CodeBuilder();

        // The module declaration
        uclidCode.pr("module main {");
        uclidCode.pr("");
        uclidCode.indent();

        /*****************************
         * Types, variables, getters *
         *****************************/
        uclidCode.pr(String.join("\n", 
            "/*****************************",
            " * Types, variables, getters *",
            " *****************************/"
        ));
        uclidCode.pr("");

        // Declare the set of reactions.
        uclidCode.pr("// Declare the set of reactions.");
        uclidCode.pr("type task_t = enum {");
        uclidCode.indent();
        for (var rxn : this.reactionInstanceGraph.nodes()) {
            // Replace "." and " " with "_".
            uclidCode.pr(rxn.getFullName().replaceAll("(\\.| )", "_") + ",");
        }
        uclidCode.pr("NULL");
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Declare worker schedule.
        uclidCode.pr("// Declare worker schedule.");
        uclidCode.pr("type schedule_t = {");
        uclidCode.indent();
        uclidCode.pr(String.join(", ", Collections.nCopies(this.reactionInstanceGraph.nodeCount(), "task_t")));
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Declare workers.
        uclidCode.pr("// Declare workers.");
        uclidCode.pr("type workers_t = {");
        uclidCode.indent();
        uclidCode.pr(String.join(", ", Collections.nCopies(this.targetConfig.workers, "schedule_t")));
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Define a group of task indices.
        uclidCode.pr("// Define a group of task indices.");
        uclidCode.pr("group task_indices : integer = {");
        uclidCode.indent();
        uclidCode.pr(String.join(", ",
            IntStream.range(0, this.reactionInstanceGraph.nodeCount() - 1)
            .boxed()
            .collect(Collectors.toList())
            .stream()
            .map(String::valueOf)
            .collect(Collectors.toList())
        ));
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Define a group of worker indices.
        uclidCode.pr("// Define a group of worker indices.");
        uclidCode.pr("group worker_indices : integer = {");
        uclidCode.indent();
        uclidCode.pr(String.join(", ",
            IntStream.range(0, this.targetConfig.workers - 1)
            .boxed()
            .collect(Collectors.toList())
            .stream()
            .map(String::valueOf)
            .collect(Collectors.toList())
        ));
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Compute the most generic schedule.
        // Define the current task set assuming all reactions are triggered.
        uclidCode.pr("// Compute the most generic schedule.");
        uclidCode.pr("// Define the current task set assuming all reactions are triggered.");
        uclidCode.pr("group current_task_set : task_t = {");
        uclidCode.indent();
        for (var rxn : this.reactionInstanceGraph.nodes()) {
            // Replace "." and " " with "_".
            uclidCode.pr(rxn.getFullName().replaceAll("(\\.| )", "_") + ",");
        }
        uclidCode.pr("NULL");
        uclidCode.unindent();
        uclidCode.pr("};");
        uclidCode.pr("");

        // Declare workers.
        uclidCode.pr("// Declare workers.");
        uclidCode.pr("var workers : workers_t;");
        uclidCode.pr("");

        // Get a task from a worker schedule.
        uclidCode.pr("// Get a task from a worker schedule.");
        uclidCode.pr("define getT(s : schedule_t, i : integer) : task_t =");
        uclidCode.indent();
        for (var i = 0; i < this.reactionInstanceGraph.nodeCount(); i++) {
            uclidCode.pr("(if (i == " + i + ") then s._" + (i+1) + " else");
        }
        uclidCode.pr("NULL");
        uclidCode.unindent();
        uclidCode.pr(String.join("", Collections.nCopies(this.reactionInstanceGraph.nodeCount(), ")")) + ";");
        uclidCode.pr("");

        // Get a worker from workers.
        uclidCode.pr("// Get a worker from workers.");
        uclidCode.pr("define getW(workers : workers_t, w : integer) : schedule_t =");
        uclidCode.indent();
        for (var i = 0; i < this.targetConfig.workers-1; i++) {
            uclidCode.pr("(if (w == " + i + ") then workers._" + (i+1) + " else");
        }
        uclidCode.pr("workers._" + this.targetConfig.workers);
        uclidCode.unindent();
        uclidCode.pr(String.join("", Collections.nCopies(this.targetConfig.workers-1, ")")) + ";");
        uclidCode.pr("");

        // Condense the two getters above.
        uclidCode.pr("// Condense the two getters above.");
        uclidCode.pr(String.join("\n", 
            "define get(w, i : integer) : task_t",
            "    = getT(getW(workers, w), i);"
        ));
        uclidCode.pr("");

        /***************
         * Constraints *
         ***************/
        uclidCode.pr(String.join("\n", 
            "/***************",
            " * Constraints *",
            " ***************/"
        ));
        uclidCode.pr("");

        // The schedules cannot be all empty.
        uclidCode.pr("// The schedules cannot be all empty.");
        uclidCode.pr(String.join("\n", 
            "axiom(finite_exists (w : integer) in worker_indices ::",
            "    (finite_exists (i : integer) in task_indices ::",
            "        get(w, i) != NULL));"
        ));
        uclidCode.pr("");

        // Each reaction only appears once.
        uclidCode.pr("// Each reaction only appears once.");
        uclidCode.pr(String.join("\n", 
            "axiom(finite_forall (w1: integer) in worker_indices ::",
            "    (finite_forall (w2 : integer) in worker_indices ::",
            "    (finite_forall (i : integer) in task_indices ::",
            "    (finite_forall (j : integer) in task_indices ::",
            "        (get(w1, i) != NULL)",
            "        ==> ((w1 == w2 && i == j) <==>",
            "        (get(w1, i) == get(w2, j)))",
            "    ))));"
        ));
        uclidCode.pr("");

        // Each reaction appears at least once.
        uclidCode.pr("// Each reaction appears at least once.");
        uclidCode.pr(String.join("\n", 
            "axiom(finite_forall (task : task_t) in current_task_set ::",
            "    (finite_exists (w : integer) in worker_indices ::",
            "    (finite_exists (i : integer) in task_indices ::",
            "        get(w, i) == task",
            "    )));"
        ));
        uclidCode.pr("");

        // Dependency graph (DAG) with reactions as nodes and
        // precedence relations as edges.
        uclidCode.pr(String.join("\n", 
            "// Dependency graph (DAG) with reactions as nodes and",
            "// precedence relations as edges."
        ));
        uclidCode.pr("define precedes(t1, t2 : task_t) : boolean = false");
        uclidCode.indent();
        // Iterate through all the reactions and get their downstream reactions.
        for (var rxn : this.reactionInstanceGraph.nodes()) {
            var downstream = this.reactionInstanceGraph.getDownstreamAdjacentNodes(rxn);
            for (var ds : downstream) {
                uclidCode.pr("|| (t1 == " + rxn.getFullName().replaceAll("(\\.| )", "_")
                    + " && t2 == " + ds.getFullName().replaceAll("(\\.| )", "_") + ")");
            }
        }
        uclidCode.unindent();
        uclidCode.pr(";");
        uclidCode.pr("");

        // Variables for optimization
        uclidCode.pr("// Declare variables for optimization.");
        uclidCode.pr("var num_workers_sum : integer;");
        uclidCode.pr("var DIFF : integer;");
        uclidCode.pr("");

        // Dummy property (to be removed)
        uclidCode.pr("property dummy : false;");
        uclidCode.pr("");

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

        // Create temp folder
        var tempFolder  = fileConfig.getSrcGenPath() + File.separator + "temp";
        try {
            FileUtil.deleteDirectory(Paths.get(tempFolder));
        } catch (IOException e) {
            Exceptions.sneakyThrow(e);
        }

        // Generate Uclid encoding
        var uclidFile   = tempFolder + File.separator + "schedule.ucl";
        var uclidCode   = this.generateUclidEncoding();
        try {
            uclidCode.writeToFile(uclidFile);
        } catch (IOException e) {
            Exceptions.sneakyThrow(e);
        }

        // Compile Uclid encoding to a SMT file.
        LFCommand cmdCompileUclid = LFCommand.get(
            "uclid",
            List.of(uclidFile, "-g", "smt"),
            false,
            Paths.get(tempFolder)
        );
        cmdCompileUclid.run();

        // Load the generated file into a string
        String smtFile = tempFolder + File.separator
            + "smt-property_dummy-v-0001.smt";
        String smtCode = "";
        try {
            smtCode = Files.readString(Paths.get(smtFile), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            Exceptions.sneakyThrow(e);
        }
        System.out.println(smtCode);

        // Remove Uclid variable prefixes using regex.
        smtCode = smtCode.replaceAll("initial_([0-9]+)_", ""); // or "initial_\\d+_", \\ escapes \.
        smtCode = smtCode.replaceAll("\\(check-sat\\)", "");
        smtCode = smtCode.replaceAll("\\(get-info :all-statistics\\)", "");
        System.out.println(smtCode);

        // Add optimization objectives.
        smtCode += String.join("\n", 
            "(minimize (abs num_workers_sum))",
            "(minimize (abs DIFF))"
        );
        smtCode += String.join("\n", 
            "(check-sat)",
            "(get-info :all-statistics)",
            "(get-model)",
            "(get-objectives)"
        );

        // Load the SMT file into the Z3 Java binding.
        Context ctx = new Context();
        Solver s = ctx.mkSolver();
        s.fromString(smtCode);

        // Solve for results.
        Status sat = s.check();
        System.out.println(sat);
        Model model;
        if (sat == Status.SATISFIABLE) {
            model = s.getModel();
            System.out.println(model);
        } else {
            Exceptions.sneakyThrow(new Exception("Error: No satisfiable schedule is found."));
        }

        // Generate preambles (everything other than the schedule in the .h file)

        // Generate executable worker schedules using the custom instruction set.

        return scheduleCode;
    }
}