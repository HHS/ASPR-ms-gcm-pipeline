package gov.hhs.aspr.ms.gcm.pipeline.testsupport;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngineId;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * Test support class for testing pipelines.
 * <p>
 * This test is only to be used if you are using protobuf to load and parse your
 * pipeline input file. This is highly advised and if you aren't doing so, it is
 * a good time to ask yourself why you are not.
 * <p>
 * The param for this class is the type for your pipeline input class. Again, it
 * assumes you are using protobuf and thus this type must be an extension of the
 * {@link Message} type.
 * <p>
 * Provides utility methods for loading unresolved pipeline input files,
 * resolving pipeline input files, creating resolved pipeline input files on
 * disk and comparing 2 output files.
 * 
 * @param <T> the type for your pipeline input class
 */
public class PipelineTestSupport<T extends Message> {

    private final TaskitEngineManager taskitEngineManager;
    private final Class<T> pipelineInputClassRef;
    private final T pipelineInputInstance;
    private final PipelineInputResolver pipelineInputResolver;
    private final Function<String, Path> resolverFunction;
    private final Path testOutputDir;

    /**
     * Creates a PipelineTestSupport class
     * 
     * Takes a translation engine builder that should contain a translation engine
     * with any and all translation specs needed to validate any and all java types
     * used in the pipeline tests.
     * 
     * Takes a default instance of the given pipeline input type.
     * 
     * Takes a class reference of the given pipeline input type.
     * 
     * Takes a resolver function that will be used to resolve the paths in the file
     * to the equivalent absolute paths.
     * 
     * Takes in a string for the test output directory so that it can be created for
     * you when getting a resolved pipeline input.
     * 
     * @param taskitEngineManager
     * @param pipelineInputInstance
     * @param pipelineInputClassRef
     * @param resolverFunction
     * @param testOutputDir
     * 
     */
    public PipelineTestSupport(TaskitEngineManager taskitEngineManager, T pipelineInputInstance,
            Class<T> pipelineInputClassRef, Function<String, Path> resolverFunction, Path testOutputDir) {
        this.taskitEngineManager = taskitEngineManager;
        this.pipelineInputInstance = pipelineInputInstance;
        this.pipelineInputClassRef = pipelineInputClassRef;
        this.pipelineInputResolver = new PipelineInputResolver(resolverFunction);
        this.resolverFunction = resolverFunction;
        this.testOutputDir = testOutputDir;
    }

    /**
     * Given a pipeline input and a file name, uses taskit to output the pipeline
     * input to a file with the given name.
     * 
     * uses a protobuf translation engine and the file will be written in json
     */
    public String createResolvedPipelineInputFile(T input, String fileName) {
        T.Builder builder = input.toBuilder();

        Path resolvedPipelineInputPath = this.resolverFunction.apply(fileName);

        this.taskitEngineManager.write(resolvedPipelineInputPath, builder.build(),
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        return resolvedPipelineInputPath.toString();
    }

    /**
     * Given two class refs and 2 paths, read in each path using taskit and compare
     * the resulting app object (APP_OBJ) classes for both object equals and string
     * equals
     * <p>
     * This test will fail if there isn't a properly implemented equals contract on
     * the APP_OBJ
     * <p>
     * This test will fail if there isn't a properly implemented toString on the
     * APP_OBJ
     * <p>
     * uses the translation engine provided to this class
     */
    public <INPUT_OBJ extends Message, APP_OBJ> boolean filesAreSame(Class<INPUT_OBJ> inputClassRef,
            Class<APP_OBJ> outputClassRef, Path pathOfExpectedOutput, Path pathOfActualOutput) {

        APP_OBJ obj1 = this.taskitEngineManager.readAndTranslate(pathOfExpectedOutput, inputClassRef,
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        APP_OBJ obj2 = this.taskitEngineManager.readAndTranslate(pathOfActualOutput, inputClassRef,
                ProtobufTaskitEngineId.JSON_ENGINE_ID);

        return obj1.equals(obj2) && obj1.toString().equals(obj2.toString());
    }

    /**
     * Given an unresolved pipeline input, creates a resolved pipeline input based
     * on whether to use the directory input file or not, and whether to set
     * runningWithPreviousData to true or not
     * <p>
     * for the directory input file, it only resolves the input and output directory
     * paths. Otherwise, it will call the PipelineInputResolver to resolve each and
     * every path within the input
     * <p>
     * then creates the Test output dir
     * <p>
     * ---------------------
     * <p>
     * For the input and output directory, the variable names MUST BE: 'inputDirectory'
     * and 'outputDirectory' otherwise they will not be resolved.
     * <p>
     * For the setPrev option, it will set the value of 'runningWithPreviousData' to
     * true if set. Note that the variable name MUST be 'runningWithPreviousData'
     */
    public T getResolvedPipelineInput(T unresolvedInput, boolean useDirectoryFile, boolean setPrev) {

        T.Builder resolvedInputBuilder = unresolvedInput.toBuilder();

        Map<FieldDescriptor, Object> fields = resolvedInputBuilder.getAllFields();

        if (!useDirectoryFile) {
            fields.forEach((pipelineField, pipelineFieldValue) -> {
                this.pipelineInputResolver.resolvePipelineInput(pipelineField, pipelineFieldValue,
                        resolvedInputBuilder);
            });

            if (setPrev) {
                fields.forEach((pipelineField, pipelineFieldValue) -> {
                    if (pipelineField.getName().equals("runningWithPreviousData")) {
                        resolvedInputBuilder.setField(pipelineField, setPrev);

                        return;
                    }
                });
            }
        } else {
            fields.forEach((pipelineField, pipelineFieldValue) -> {
                if (pipelineField.getName().equals("inputDirectory")
                        || pipelineField.getName().equals("outputDirectory")) {
                    this.pipelineInputResolver.resolvePipelineInput(pipelineField, pipelineFieldValue,
                            resolvedInputBuilder);
                }
            });
        }

        // make outputDir
        ResourceHelper.createDirectory(testOutputDir);

        return this.pipelineInputClassRef.cast(resolvedInputBuilder.build());
    }

    /**
     * given a filename, attempts to load the file using protobuf
     * <p>
     * file must be of the type assigned to this class and is not allowed to have
     * any missing/omitted fields that are not labeled optional.
     */
    public T getUnresolvedPipelineInput(String inputFileName) {
        Path pipelineInputPath = this.resolverFunction.apply(inputFileName);

        if (!Files.exists(pipelineInputPath)) {
            throw new RuntimeException(
                    "Provided path does not exist: " + pipelineInputPath.toAbsolutePath().toString());
        }

        Parser jsonParser = JsonFormat.parser();

        T.Builder builder = this.pipelineInputInstance.newBuilderForType();

        try {
            Reader reader = new FileReader(pipelineInputPath.toFile());
            jsonParser.merge(reader, builder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.pipelineInputClassRef.cast(builder.build());
    }
}
