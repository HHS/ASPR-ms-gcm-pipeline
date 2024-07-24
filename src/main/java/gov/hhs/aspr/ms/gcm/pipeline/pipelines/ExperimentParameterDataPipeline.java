package gov.hhs.aspr.ms.gcm.pipeline.pipelines;

import java.nio.file.Path;

import gov.hhs.aspr.ms.gcm.pipeline.input.ExperimentParameterPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.support.GCMPipelineFileHeaders;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.readers.TextTableReader;
import gov.hhs.aspr.ms.gcm.pipeline.IPipeline;
import gov.hhs.aspr.ms.gcm.simulation.nucleus.ExperimentParameterData;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.NucleusTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.input.ExperimentParameterDataInput;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.translationSpecs.ExperimentParameterDataTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.TranslationController;
import gov.hhs.aspr.ms.taskit.core.TranslationEngineType;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationEngine;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * A utility for constructing the Experiment Parameter Data file from the
 * experimentParametersFile, and explicitScenarioIdsFile
 */
public class ExperimentParameterDataPipeline implements IPipeline {

    /**
     * Given a {@link ExperimentParameterPipelineInput}, an input directory path and
     * an output directory path, return a {@link ExperimentParameterDataPipeline}
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@link ResourceError#UNKNOWN_FILE} if any of
     *                           the input paths are not valid</li>
     *                           <li>{@link ResourceError#FILE_PATH_IS_DIRECTORY} if
     *                           any of the input paths or output path point to a directory</li>
     *                           </ul>
     */
    public static ExperimentParameterDataPipeline from(ExperimentParameterPipelineInput pipelineInput,
            Path inputDirectory, Path outputDirectory) {
        String experimentParametersFile = inputDirectory.resolve(pipelineInput.getExperimentParametersFile())
                .toString();
        String explicitScenarioIdsFile = inputDirectory.resolve(pipelineInput.getExplicitScenarioIdsFile()).toString();
        String experimentParameterDataFile = outputDirectory.resolve(pipelineInput.getExperimentParameterDataFile())
                .toString();

        Path experimentParametersPath = ResourceHelper.validateFile(experimentParametersFile);
        Path explicitScenarioIdsPath = ResourceHelper.validateFile(explicitScenarioIdsFile);

        Path experimentParameterDataPath = ResourceHelper.validateFilePath(experimentParameterDataFile);

        return new ExperimentParameterDataPipeline(experimentParametersPath, explicitScenarioIdsPath,
                experimentParameterDataPath);
    }

    private final Path experimentParametersFile;
    private final Path explicitScenarioIdsFile;

    private final Path experimentParameterDataFile;

    private ExperimentParameterDataPipeline(Path experimentParametersFile, Path explicitScenarioIdsFile,
            Path experimentParameterDataFile) {
        this.experimentParametersFile = experimentParametersFile;
        this.explicitScenarioIdsFile = explicitScenarioIdsFile;
        this.experimentParameterDataFile = experimentParameterDataFile;
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
        // create the translation engine and have it use the nucleus translator and
        ProtobufTranslationEngine protobufTranslationEngine = ProtobufTranslationEngine.builder()//
                .addTranslator(NucleusTranslator.getTranslator())//
                .build();//

        ExperimentParameterDataTranslationSpec translationSpec = new ExperimentParameterDataTranslationSpec();
        translationSpec.init(protobufTranslationEngine);

        ExperimentParameterDataInput input = translationSpec.convert(builder.build());
        builder = null;

        System.out.println("ExperimentParameterData Pipeline::writing experiment parameter data");
        // build the translation controller and have it write the
        // experimentParameterData file
        TranslationController.builder()
                .addTranslationEngine(protobufTranslationEngine)//
                .build()//
                .writeOutput(input, experimentParameterDataFile, TranslationEngineType.PROTOBUF);//

        System.out.println("ExperimentParameterData Pipeline::done");

    }

    private void loadExperimentParametersFile(ExperimentParameterData.Builder builder) {
        TextTableReader.read(",", GCMPipelineFileHeaders.EXPERIMENT_PARAMETERS, experimentParametersFile, (values) -> {
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
        TextTableReader.read(",", GCMPipelineFileHeaders.EXPLICIT_SCENARIO_IDS, explicitScenarioIdsFile, (values) -> {
            builder.addExplicitScenarioId(Integer.parseInt(values[0]));
        });
    }
}
