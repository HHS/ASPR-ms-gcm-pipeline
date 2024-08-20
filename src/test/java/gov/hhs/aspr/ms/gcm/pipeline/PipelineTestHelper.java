package gov.hhs.aspr.ms.gcm.pipeline;

import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.translation.NucleusTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.globalproperties.translation.GlobalPropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.groups.translation.GroupsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.people.translation.PeopleTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.personproperties.translation.PersonPropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.properties.translation.PropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.regions.translation.RegionsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.reports.translation.ReportsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.stochastics.translation.StochasticsTranslator;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;

/**
 * This class is to help test the pipelines
 */
public class PipelineTestHelper {

    // Taskit Engine Manager
    public static final TaskitEngineManager taskitEngineManager = TaskitEngineManager
            .builder()
            .addTaskitEngine(ProtobufJsonTaskitEngine.builder()
                    .addTranslator(GlobalPropertiesTranslator.getTranslator())
                    .addTranslator(PeopleTranslator.getTranslator())
                    .addTranslator(PersonPropertiesTranslator.getTranslator())
                    .addTranslator(GroupsTranslator.getTranslator())
                    .addTranslator(RegionsTranslator.getTranslator())
                    .addTranslator(ReportsTranslator.getTranslator())
                    .addTranslator(StochasticsTranslator.getTranslator())
                    .addTranslator(PropertiesTranslator.getTranslator())
                    .addTranslator(NucleusTranslator.getTranslator())
                    .build())
            .build();
}
