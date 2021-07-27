package org.lflang.generator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

import org.lflang.ASTUtils;
import org.lflang.ErrorReporter;
import org.lflang.FileConfig;
import org.lflang.Target;
import org.lflang.generator.c.CGenerator;
import org.lflang.scoping.LFGlobalScopeProvider;

import com.google.inject.Inject;

/**
 * Generates code from your model files on save.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
public class LFGenerator extends AbstractGenerator {

    @Inject
    private LFGlobalScopeProvider scopeProvider;

    // Indicator of whether generator errors occurred.
    protected boolean generatorErrorsOccurred = false;


    private String getPackageName(Target target) {
        switch (target) {
        case CPP: return "cpp";
        case TS: return "ts";
        default:
            throw new IllegalArgumentException("Unexpected target '" + target + "'");
        }
    }

    private String getClassNamePrefix(Target target) {
        switch (target) {
        case CPP: return "Cpp";
        case TS: return "TS";
        default:
            throw new IllegalArgumentException("Unexpected target '" + target + "'");
        }
    }

    /**
     * Create a target-specific FileConfig object in Kotlin
     *
     * Since the CppFileConfig and TypeScriptFileConfig class are implemented in Kotlin, the classes are
     * not visible from all contexts. If the RCA is run from within Eclipse via
     * "Run as Eclipse Application", the Kotlin classes are unfortunately not
     * available at runtime due to bugs in the Eclipse Kotlin plugin. (See
     * https://stackoverflow.com/questions/68095816/is-ist-possible-to-build-mixed-kotlin-and-java-applications-with-a-recent-eclips)
     *
     * If the FileConfig class is found, this method returns an instance.
     * Otherwise, it returns an Instance of FileConfig.
     *
     * @return A FileConfig object in Kotlin if the class can be found.
     * @throws IOException If the file config could not be created properly
     */
    private FileConfig createFileConfig(final Target target,
                                              Resource resource,
                                              IFileSystemAccess2 fsa,
                                              IGeneratorContext context)
        throws IOException {
        // Since our Eclipse Plugin uses code injection via guice, we need to
        // play a few tricks here so that FileConfig does not appear as an
        // import. Instead we look the class up at runtime and instantiate it if
        // found.
        switch (target) {
        case CPP:
        case TS: {
            // These targets use kotlin
            String packageName = getPackageName(target);
            String classNamePrefix = getClassNamePrefix(target);
            String className = "org.lflang.generator." + packageName + "." + classNamePrefix + "FileConfig";
            try {

                return (FileConfig) Class.forName(className)
                                         .getDeclaredConstructor(Resource.class, IFileSystemAccess2.class, IGeneratorContext.class)
                                         .newInstance(resource, fsa, context);

            } catch (Exception e) {
                return new FileConfig(resource, fsa, context);
            }
        }
        default: {
            return new FileConfig(resource, fsa, context);
        }
        }
    }

    /** Create a generator object for the given target */
    private GeneratorBase createGenerator(Target target, FileConfig fileConfig,
            ErrorReporter errorReporter) {
        switch (target) {
        case C: return new CGenerator(fileConfig, errorReporter);
        case CCPP: return new CCppGenerator(fileConfig, errorReporter);
        case Python: return new PythonGenerator(fileConfig, errorReporter);
        default:
            return createKotlinGenerator(target, fileConfig, errorReporter);
        }
    }

    /**
     * Create a code generator in Kotlin.
     *
     * Since the CppGenerator and TSGenerator class are implemented in Kotlin, the classes are
     * not visible from all contexts. If the RCA is run from within Eclipse via
     * "Run as Eclipse Application", the Kotlin classes are unfortunately not
     * available at runtime due to bugs in the Eclipse Kotlin plugin. (See
     * https://stackoverflow.com/questions/68095816/is-ist-possible-to-build-mixed-kotlin-and-java-applications-with-a-recent-eclips)
     * In this case, the method returns null
     *
     * @return A Kotlin Generator object if the class can be found
     */
    private GeneratorBase createKotlinGenerator(Target target, FileConfig fileConfig,
                                            ErrorReporter errorReporter) {
        // Since our Eclipse Plugin uses code injection via guice, we need to
        // play a few tricks here so that Kotlin FileConfig and
        // Kotlin Generator do not appear as an import. Instead we look the
        // class up at runtime and instantiate it if found.
        String classPrefix = "org.lflang.generator." + getPackageName(target) + "." + getClassNamePrefix(target);
        try {
            Class<?> generatorClass = Class.forName(classPrefix + "Generator");
            Class<?> fileConfigClass = Class.forName(classPrefix + "FileConfig");
            Constructor<?> ctor = generatorClass
                .getDeclaredConstructor(fileConfigClass, ErrorReporter.class, LFGlobalScopeProvider.class);

            return (GeneratorBase) ctor.newInstance(fileConfig, errorReporter, scopeProvider);

        } catch (Exception e) {
            generatorErrorsOccurred = true;
            errorReporter.reportError(
                "The code generator for the " + target + " target could not be found. "
                    + "This is likely because you are running the RCA from"
                    + "Eclipse. The " + target + " code generator is written in Kotlin"
                    + "and, unfortunately, the Eclipse Kotlin plugin is "
                    + "broken, preventing us from loading the generator"
                    + "properly. Please consider building the RCA via Maven.");
            // FIXME: Add a link to the wiki with more information.
            return null;
        }
    }

    @Override
    public void doGenerate(Resource resource, IFileSystemAccess2 fsa,
            IGeneratorContext context) {
        // Determine which target is desired.
        final Target target = Target.fromDecl(ASTUtils.targetDecl(resource));

        FileConfig fileConfig;
        try {
            fileConfig = createFileConfig(target, resource, fsa, context);
        } catch (IOException e) {
            throw new RuntimeException("Error during FileConfig instaniation");
        }
        final ErrorReporter errorReporter = new EclipseErrorReporter(fileConfig);
        final GeneratorBase generator = createGenerator(target, fileConfig, errorReporter);

        if (generator != null) {
            generator.doGenerate(resource, fsa, context);
            generatorErrorsOccurred = generator.errorsOccurred();
        }
    }

    /** Return true if errors occurred in the last call to doGenerate(). */
    public boolean errorsOccurred() {
        return generatorErrorsOccurred;
    }
}
