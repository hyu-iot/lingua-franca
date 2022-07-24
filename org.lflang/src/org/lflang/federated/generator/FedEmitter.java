package org.lflang.federated.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import org.lflang.ErrorReporter;
import org.lflang.generator.LFGeneratorContext;
import org.lflang.lf.Reactor;

/**
 * Helper class to generate code for federates.
 */
public class FedEmitter {

    private final FedFileConfig fileConfig;
    private final Reactor originalMainReactor;
    private final ErrorReporter errorReporter;
    private final LinkedHashMap<String, Object> federationRTIProperties;

    public FedEmitter(
        FedFileConfig fileConfig,
        Reactor originalMainReactor,
        ErrorReporter errorReporter,
        LinkedHashMap<String, Object> federationRTIProperties
    ) {
        this.fileConfig = fileConfig;
        this.originalMainReactor = originalMainReactor;
        this.errorReporter = errorReporter;
        this.federationRTIProperties = federationRTIProperties;
    }

    /**
     * Generate a .lf file for federate {@code federate}.
     *
     * @throws IOException
     */
    void generateFederate(
        LFGeneratorContext context,
        FederateInstance federate,
        int numOfFederates
    ) throws IOException {
        String fedName = federate.instantiation.getName();
        Files.createDirectories(fileConfig.getFedSrcPath());
        System.out.println("##### Generating code for federate " + fedName
                               + " in directory "
                               + fileConfig.getFedSrcPath());

        Path lfFilePath = fileConfig.getFedSrcPath().resolve(
            fedName + ".lf");

        String federateCode = String.join(
            "\n",
            (new FedTargetEmitter()).generateTarget(context, federate, fileConfig, errorReporter, federationRTIProperties),
            (new FedImportEmitter()).generateImports(federate, fileConfig),
            (new FedPreambleEmitter()).generatePreamble(federate, federationRTIProperties, numOfFederates, errorReporter),
            (new FedReactorEmitter()).generateReactorDefinitions(federate),
            (new FedMainEmitter()).generateMainReactor(
                federate,
                originalMainReactor,
                errorReporter
            )
        );

        try (var srcWriter = Files.newBufferedWriter(lfFilePath)) {
            srcWriter.write(federateCode);
        }

    }
}