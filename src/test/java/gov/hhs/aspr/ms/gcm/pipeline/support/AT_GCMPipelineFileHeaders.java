package gov.hhs.aspr.ms.gcm.pipeline.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_GCMPipelineFileHeaders {
    @Test
	@UnitTestField(target = GCMPipelineFileHeaders.class, name = "SIM_STATE_SETTINGS")
	public void testSimStateSettings() {
		assertNotNull(GCMPipelineFileHeaders.SIM_STATE_SETTINGS);
	}

	@Test
	@UnitTestField(target = GCMPipelineFileHeaders.class, name = "EXPERIMENT_PARAMETERS")
	public void testExperimentParameters() {
		assertNotNull(GCMPipelineFileHeaders.EXPERIMENT_PARAMETERS);
	}

	@Test
	@UnitTestField(target = GCMPipelineFileHeaders.class, name = "EXPLICIT_SCENARIO_IDS")
	public void testExplicitScenarioIds() {
		assertNotNull(GCMPipelineFileHeaders.EXPLICIT_SCENARIO_IDS);
	}
}
