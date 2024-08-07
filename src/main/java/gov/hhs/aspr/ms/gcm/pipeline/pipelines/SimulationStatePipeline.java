package gov.hhs.aspr.ms.gcm.pipeline.pipelines;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import gov.hhs.aspr.ms.gcm.pipeline.ATypedPipeline;
import gov.hhs.aspr.ms.gcm.pipeline.input.SimulationStatePipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.support.GCMPipelineFileHeaders;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.SimulationState;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.input.SimulationStateInput;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngineId;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.readers.TextTableReader;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * A utility for constructing the SimulationState file from the
 * simStateSettingsFile
 */
public class SimulationStatePipeline extends ATypedPipeline<SimulationStatePipelineInput> {

    /**
     * Given a {@link SimulationStatePipelineInput}, an input directory path and an
     * output directory path, return a {@link SimulationStatePipeline}
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@link ResourceError#UNKNOWN_FILE} if any of
     *                           the input paths are not valid</li>
     *                           <li>{@link ResourceError#FILE_PATH_IS_DIRECTORY} if
     *                           any of the input paths or output path point to a
     *                           directory</li>
     *                           </ul>
     */
    public SimulationStatePipeline using(SimulationStatePipelineInput pipelineInput, Path inputDirectory,
            Path outputDirectory) {

        this.simStateSettingsFilePath = ResourceHelper
                .validateFile(inputDirectory.resolve(pipelineInput.getSimulationSettingsFile()));

        this.simStateFilePath = ResourceHelper
                .validateFilePath(outputDirectory.resolve(pipelineInput.getSimulationStateFile()));

        return this;
    }

    private Path simStateSettingsFilePath;

    private Path simStateFilePath;

    public SimulationStatePipeline(TaskitEngineManager taskitEngineManager) {
        super(taskitEngineManager);
    }

    /**
     * Executes the pipeline with the given input files
     * <p>
     * writes out the resulting simState file to the path provided
     */
    public void execute() {
        SimulationState.Builder builder = SimulationState.builder();

        System.out.println("SimulationState Pipeline::loading simulation state settings file");
        loadSimStateSettingsFile(builder);

        System.out.println("SimulationState Pipeline::translating simulation state");
        SimulationStateInput translatedObject = this.taskitEngineManager.translateObject(builder.build(),
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        // null out the data
        builder = null;

        System.out.println("SimulationState Pipeline::writing simulation state");
        this.taskitEngineManager.write(this.simStateFilePath, translatedObject,
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        // null out the data
        translatedObject = null;

        System.out.println("SimulationState Pipeline::done");
    }

    private void loadSimStateSettingsFile(SimulationState.Builder builder) {
        List<DateTimeFormatter> formatters = new ArrayList<>();

        formatters.add(DateTimeFormatter.ofPattern("M-d-uuuu"));
        formatters.add(DateTimeFormatter.ofPattern("M/d/uuuu"));
        formatters.add(DateTimeFormatter.ofPattern("uuuu-M-d"));
        formatters.add(DateTimeFormatter.ofPattern("uuuu/M/d"));

        TextTableReader.read(",", GCMPipelineFileHeaders.SIM_STATE_SETTINGS, simStateSettingsFilePath, (values) -> {
            switch (values[0]) {
                case "start_time":
                    builder.setStartTime(Double.parseDouble(values[1]));
                    break;
                case "base_date":
                    LocalDate localDate;
                    RuntimeException runtimeException = null;
                    for (DateTimeFormatter formatter : formatters) {
                        try {
                            localDate = LocalDate.parse(values[1], formatter);
                            builder.setBaseDate(localDate);
                            runtimeException = null;
                            break;
                        } catch (DateTimeParseException e) {
                            runtimeException = new RuntimeException(e);
                        }
                    }

                    if (runtimeException != null) {
                        throw runtimeException;
                    }

                    break;
                // plans are not currently supported
            }
        });
    }
}
