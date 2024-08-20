package gov.hhs.aspr.ms.gcm.pipeline.pipelines;

import java.nio.file.Path;

import gov.hhs.aspr.ms.gcm.pipeline.ATypedPipeline;
import gov.hhs.aspr.ms.gcm.pipeline.input.ExperimentParameterPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.support.GCMPipelineFileHeaders;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.ExperimentParameterData;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.input.ExperimentParameterDataInput;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngineId;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.readers.TextTableReader;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * A utility for constructing the Experiment Parameter Data file from the
 * experimentParametersFile, and explicitScenarioIdsFile
 */
public class ExperimentParameterDataPipeline extends ATypedPipeline<ExperimentParameterPipelineInput> {

    /**
     * Given a {@link ExperimentParameterPipelineInput}, an input directory path and
     * an output directory path, return a {@link ExperimentParameterDataPipeline}
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
    public ExperimentParameterDataPipeline using(ExperimentParameterPipelineInput pipelineInput,
            Path inputDirectory, Path outputDirectory) {

        this.experimentParametersFilePath = ResourceHelper
                .validateFile(inputDirectory.resolve(pipelineInput.getExperimentParametersFile()));
        this.explicitScenarioIdsFilePath = ResourceHelper
                .validateFile(inputDirectory.resolve(pipelineInput.getExplicitScenarioIdsFile()));

        this.experimentParameterDataFilePath = ResourceHelper
                .validateFilePath(outputDirectory.resolve(pipelineInput.getExperimentParameterDataFile()));

        return this;
    }

    private Path experimentParametersFilePath;
    private Path explicitScenarioIdsFilePath;

    private Path experimentParameterDataFilePath;

    public ExperimentParameterDataPipeline(TaskitEngineManager taskitEngineManager) {
        super(taskitEngineManager);
    }

    /**
     * Executes the pipeline with the given input files
     * <p>
     * writes out the resulting experimentParameter file to the path provided
     */
    public void execute() {
        ExperimentParameterData.Builder builder = ExperimentParameterData.builder();

        System.out.println("ExperimentParameterData Pipeline::loading experiment parameters");
        loadExperimentParametersFile(builder);

        System.out.println("ExperimentParameterData Pipeline::loading explicit scenario ids");
        loadExplicitScenarioIdsFile(builder);

        System.out.println("ExperimentParameterData Pipeline::translating experiment parameter data");
        ExperimentParameterDataInput translatedObject = this.taskitEngineManager.translateObject(builder.build(),
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        // null out the data
        builder = null;

        System.out.println("ExperimentParameterData Pipeline::writing experiment parameter data");
        this.taskitEngineManager.write(this.experimentParameterDataFilePath, translatedObject,
                ProtobufTaskitEngineId.JSON_ENGINE_ID);
        // null out the data
        translatedObject = null;

        System.out.println("ExperimentParameterData Pipeline::done");
    }

    private void loadExperimentParametersFile(ExperimentParameterData.Builder builder) {
        TextTableReader.read(",", GCMPipelineFileHeaders.EXPERIMENT_PARAMETERS, experimentParametersFilePath,
                (values) -> {
                    switch (values[0]) {
                        case "thread_count":
                            builder.setThreadCount(Integer.parseInt(values[1]));
                            break;
                        case "state_recording_is_scheduled":
                            builder.setRecordState(Boolean.parseBoolean(values[1]));
                            break;
                        case "simulation_halt_time":
                            builder.setSimulationHaltTime(Double.parseDouble(values[1]));
                            break;
                        case "halt_on_exception":
                            builder.setHaltOnException(Boolean.parseBoolean(values[1]));
                            break;
                        case "experiment_progress_log_path":
                            builder.setExperimentProgressLog(Path.of(values[1]));
                            break;
                        case "continue_from_progress_log":
                            builder.setContinueFromProgressLog(Boolean.parseBoolean(values[1]));
                            break;
                    }
                });
    }

    private void loadExplicitScenarioIdsFile(ExperimentParameterData.Builder builder) {
        TextTableReader.read(",", GCMPipelineFileHeaders.EXPLICIT_SCENARIO_IDS, explicitScenarioIdsFilePath,
                (values) -> {
                    builder.addExplicitScenarioId(Integer.parseInt(values[0]));
                });
    }
}
