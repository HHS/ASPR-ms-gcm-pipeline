package gov.hhs.aspr.ms.gcm.pipeline;

import java.nio.file.Path;

import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * This is a class that contains all of the paths for all pipeline test output
 * files and the test resource directory.
 * 
 * When adding a new dimension or plugin data, first add the file name in the
 * respective section, and then add the path in the respective section.
 */
public class PipelineTestPaths {
    // name of expected output directory
    private static final String EXPECTED_OUTPUT_DIR_NAME = "expectedOutput";

    // name of test output directory
    private static final String TEST_OUTPUT_DIR_NAME = "testOutput";

    // name of pipelineInput directory
    private static final String PIPELINE_INPUT_DIR_NAME = "pipelineInput";

    // name of each of the pipeline input files
    private static final String SIM_STATE_PIPELINE_INPUT_F_NAME = "simulationStatePipeline.json";

    // name of each the plugin data files
    private static final String POPULATION_P_DATA_F_NAME = "populationPluginData.json";
    private static final String PEOPLE_P_DATA_F_NAME = "peoplePluginData.json";
    private static final String PEOPLE_P_DATA_EMPTY_F_NAME = "peoplePluginData_empty.json";
    private static final String PERSON_PROPS_P_DATA_F_NAME = "personPropertiesPluginData.json";
    private static final String GROUPS_P_DATA_F_NAME = "groupsPluginData.json";
    private static final String REGIONS_P_DATA_F_NAME = "regionsPluginData.json";
    private static final String STOCHASTICS_P_DATA_F_NAME = "stochasticsPluginData.json";

    // name of gcm experiment params and sim state files
    private static final String EXPERIMENT_PARAMS_F_NAME = "experimentParameterData.json";
    private static final String SIM_STATE_F_NAME = "simulationState.json";

    // Absolute path to the resource directory used in testing
    public static final Path RESOURCE_DIR = ResourceHelper.getResourceDir(PipelineTestPaths.class);

    // absolute path to the expected output directory based on the resource dir
    // above
    public static final Path EXPECTED_OUTPUT_DIR = getResolvedResourcePath(EXPECTED_OUTPUT_DIR_NAME);

    // absolute path to the test output directory based on the resource dir above
    public static final Path TEST_OUTPUT_DIR = getResolvedResourcePath(TEST_OUTPUT_DIR_NAME);

    // absolute path to the pipeline input directory based on the resource dir above
    public static final Path PIPELINE_INPUT_DIR = getResolvedResourcePath(PIPELINE_INPUT_DIR_NAME);

    // absolute paths to each of the pipeline input files
    public static final Path SIM_STATE_PIPELINE = getResolvedPipelineInputPath(SIM_STATE_PIPELINE_INPUT_F_NAME);

    // absolute paths to each of the expected output plugin data files
    public static final Path EXPECTED_POPULATION_PLUGIN_DATA = getResolvedOutputPath(POPULATION_P_DATA_F_NAME);
    public static final Path EXPECTED_PEOPLE_PLUGIN_DATA = getResolvedOutputPath(PEOPLE_P_DATA_F_NAME);
    public static final Path EXPECTED_PEOPLE_PLUGIN_DATA_EMPTY = getResolvedOutputPath(PEOPLE_P_DATA_EMPTY_F_NAME);
    public static final Path EXPECTED_PERSON_PROPS_PLUGIN_DATA = getResolvedOutputPath(PERSON_PROPS_P_DATA_F_NAME);
    public static final Path EXPECTED_GROUPS_PLUGIN_DATA = getResolvedOutputPath(GROUPS_P_DATA_F_NAME);
    public static final Path EXPECTED_REGIONS_PLUGIN_DATA = getResolvedOutputPath(REGIONS_P_DATA_F_NAME);
    public static final Path EXPECTED_STOCHASTICS_PLUGIN_DATA = getResolvedOutputPath(STOCHASTICS_P_DATA_F_NAME);

    // absolute paths to the gcm experiment params and sim state files
    public static final Path EXPECTED_EXPERIMENT_PARAMETER_DATA = getResolvedOutputPath(EXPERIMENT_PARAMS_F_NAME);
    public static final Path EXPECTED_SIMULATION_STATE = getResolvedOutputPath(SIM_STATE_F_NAME);

    private PipelineTestPaths() {
    }

    /**
     * Given a path, return it resolved against the resource directory path
     */
    public static Path getResolvedResourcePath(String path) {
        return RESOURCE_DIR.resolve(path).toAbsolutePath();
    }

    /**
     * Given a path, return it resolved against the expected output directory path
     */
    public static Path getResolvedOutputPath(String path) {
        return EXPECTED_OUTPUT_DIR.resolve(path);
    }

    /**
     * Given a path, return it resolved against the pipeline input directory path
     */
    public static Path getResolvedPipelineInputPath(String path) {
        return PIPELINE_INPUT_DIR.resolve(path);
    }
}
