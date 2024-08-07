package gov.hhs.aspr.ms.gcm.pipeline.pipelines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.gcm.pipeline.PipelineTestHelper;
import gov.hhs.aspr.ms.gcm.pipeline.PipelineTestPaths;
import gov.hhs.aspr.ms.gcm.pipeline.input.SimulationStatePipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.PipelineTestSupport;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.SimulationState;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.input.SimulationStateInput;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_SimulationStatePipeline {
    PipelineTestSupport<SimulationStatePipelineInput> pipelineInputTestSupport = new PipelineTestSupport<>(
            PipelineTestHelper.taskitEngineManager,
            SimulationStatePipelineInput.getDefaultInstance(), SimulationStatePipelineInput.class,
            PipelineTestPaths::getResolvedResourcePath, PipelineTestPaths.TEST_OUTPUT_DIR);

    SimulationStatePipelineInput unresolvedPipelineInput = pipelineInputTestSupport
            .getUnresolvedPipelineInput(PipelineTestPaths.SIM_STATE_PIPELINE.toString());

    SimulationStatePipelineInput resolvedPipelineInput = pipelineInputTestSupport
            .getResolvedPipelineInput(unresolvedPipelineInput, false, false);

    @Test
    @UnitTestMethod(target = SimulationStatePipeline.class, name = "from", args = {
            SimulationStatePipelineInput.class,
            Path.class, Path.class })
    public void testFrom() {

        Path inputPath = Path.of("");
        Path outputPath = Path.of("");

        SimulationStatePipelineInput pipelineInput = resolvedPipelineInput;

        SimulationStatePipeline pipeline = new SimulationStatePipeline(PipelineTestHelper.taskitEngineManager)
                .using(pipelineInput, inputPath, outputPath);

        assertNotNull(pipeline);

        // preconditions
        // simStateSettingsFile is invalid
        ContractException contractException = assertThrows(ContractException.class, () -> {
            pipeline.using(
                    pipelineInput.toBuilder().setSimulationSettingsFile("invalidPath").build(),
                    inputPath, outputPath);
        });

        assertEquals(ResourceError.UNKNOWN_FILE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = SimulationStatePipeline.class, name = "execute", args = {})
    public void testExecute() {

        Path resourceDir = PipelineTestPaths.RESOURCE_DIR;

        Path inputPath = Path.of("");
        Path outputPath = Path.of("");

        SimulationStatePipelineInput pipelineInput = resolvedPipelineInput;

        Path dataFile = resourceDir.resolve(pipelineInput.getSimulationStateFile());

        ResourceHelper.createDirectory(dataFile.getParent());
        ResourceHelper.createFile(dataFile.getParent(), dataFile.getFileName().toString());

        SimulationStatePipeline pipeline = new SimulationStatePipeline(PipelineTestHelper.taskitEngineManager)
                .using(pipelineInput, inputPath, outputPath);

        pipeline.execute();

        assertTrue(pipelineInputTestSupport.filesAreSame(SimulationStateInput.class,
                SimulationState.class, PipelineTestPaths.EXPECTED_SIMULATION_STATE, dataFile));

        // preconditions
        // base date is invalid
        SimulationStatePipelineInput badDate = pipelineInput.toBuilder()
                .setSimulationSettingsFile(pipelineInput.getSimulationSettingsFile()
                        .replace("simulation_settings.csv", "simulation_settings_bad_date.csv"))
                .build();

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            pipeline.using(badDate, inputPath, outputPath).execute();
        });

        assertEquals(DateTimeParseException.class, runtimeException.getCause().getClass());
    }
}
