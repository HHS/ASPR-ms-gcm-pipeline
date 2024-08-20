package gov.hhs.aspr.ms.gcm.pipeline.pipelines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.gcm.pipeline.PipelineTestHelper;
import gov.hhs.aspr.ms.gcm.pipeline.PipelineTestPaths;
import gov.hhs.aspr.ms.gcm.pipeline.input.ExperimentParameterPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.PipelineTestSupport;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.ExperimentParameterData;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.input.ExperimentParameterDataInput;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_ExperimentParameterDataPipeline {

    PipelineTestSupport<ExperimentParameterPipelineInput> pipelineInputTestSupport = new PipelineTestSupport<>(
            PipelineTestHelper.taskitEngineManager,
            ExperimentParameterPipelineInput.getDefaultInstance(), ExperimentParameterPipelineInput.class,
            PipelineTestPaths::getResolvedResourcePath, PipelineTestPaths.TEST_OUTPUT_DIR);

    ExperimentParameterPipelineInput unresolvedPipelineInput = pipelineInputTestSupport
            .getUnresolvedPipelineInput(PipelineTestPaths.EXPERIMENT_PARAM_DATA_PIPELINE.toString());

    ExperimentParameterPipelineInput resolvedPipelineInput = pipelineInputTestSupport
            .getResolvedPipelineInput(unresolvedPipelineInput, false, false);

    @Test
    @UnitTestMethod(target = ExperimentParameterDataPipeline.class, name = "using", args = {
            ExperimentParameterPipelineInput.class, Path.class, Path.class })
    public void testUsing() {

        Path inputPath = Path.of("");
        Path outputPath = Path.of("");

        ExperimentParameterPipelineInput pipelineInput = resolvedPipelineInput;

        ExperimentParameterDataPipeline pipeline = new ExperimentParameterDataPipeline(
                PipelineTestHelper.taskitEngineManager)
                .using(pipelineInput,
                        inputPath,
                        outputPath);

        assertNotNull(pipeline);

        // preconditions
        // experimentParametersFile is invalid
        ContractException contractException = assertThrows(ContractException.class, () -> {
            pipeline.using(
                    pipelineInput.toBuilder().setExperimentParametersFile("invalidPath").build(),
                    inputPath,
                    outputPath);
        });

        assertEquals(ResourceError.UNKNOWN_FILE, contractException.getErrorType());

        // explicitScenarioIdsFile is invalid
        contractException = assertThrows(ContractException.class, () -> {
            pipeline.using(
                    pipelineInput.toBuilder().setExplicitScenarioIdsFile("invalidPath").build(),
                    inputPath, outputPath);
        });

        assertEquals(ResourceError.UNKNOWN_FILE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = ExperimentParameterDataPipeline.class, name = "execute", args = {})
    public void testExecute() throws IOException {

        Path resourceDir = PipelineTestPaths.RESOURCE_DIR;

        Path inputPath = Path.of("");
        Path outputPath = Path.of("");

        ExperimentParameterPipelineInput pipelineInput = resolvedPipelineInput;

        Path dataFile = resourceDir.resolve(pipelineInput.getExperimentParameterDataFile());

        ResourceHelper.createDirectory(dataFile.getParent());
        ResourceHelper.createFile(dataFile.getParent(), dataFile.getFileName().toString());

        ExperimentParameterDataPipeline pipeline = new ExperimentParameterDataPipeline(
                PipelineTestHelper.taskitEngineManager)
                .using(pipelineInput,
                        inputPath,
                        outputPath);

        pipeline.execute();

        assertTrue(pipelineInputTestSupport.filesAreSame(ExperimentParameterDataInput.class,
                ExperimentParameterData.class, PipelineTestPaths.EXPECTED_EXPERIMENT_PARAMETER_DATA,
                dataFile));
    }
}
