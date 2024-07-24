package gov.hhs.aspr.ms.gcm.pipeline;

import gov.hhs.aspr.ms.gcm.taskit.protobuf.nucleus.NucleusTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.groups.GroupsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.people.PeopleTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.personproperties.PersonPropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.properties.PropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.regions.RegionsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.reports.ReportsTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.stochastics.StochasticsTranslator;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationEngine;

/**
 * This class is to help test the pipelines
 */
public class PipelineTestHelper {

    /*
     * static method to return a new protobuf translation engine.
     * 
     * used in a method due to nature of static access
     */
    public static ProtobufTranslationEngine getProtobufTranslationEngine() {
        return ProtobufTranslationEngine.builder()
                .addTranslator(PeopleTranslator.getTranslator())
                .addTranslator(PersonPropertiesTranslator.getTranslator())
                .addTranslator(GroupsTranslator.getTranslator())
                .addTranslator(RegionsTranslator.getTranslator())
                .addTranslator(ReportsTranslator.getTranslator())
                .addTranslator(StochasticsTranslator.getTranslator())
                .addTranslator(PropertiesTranslator.getTranslator())
                .addTranslator(NucleusTranslator.getTranslator())
                .build();
    }
}
