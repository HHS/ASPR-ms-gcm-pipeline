package gov.hhs.aspr.ms.gcm.pipeline.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestDimensionInstanceInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestMultiDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestSingleDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestSubPipelineInput;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_PipelineInputResolver {
    private static final Path REOURCE_DIR = ResourceHelper.getResourceDir(AT_PipelineInputResolver.class);
    private static final String TEST_OUTPUT_DIR_NAME = "testOutput";
    private static final Path TEST_OUTPUT_DIR = getResolvedResourcePath(TEST_OUTPUT_DIR_NAME);
    private static final String TEST_FILE_NAME = "pipeline_tester.json";

    private static final Path getResolvedResourcePath(String path) {
        return REOURCE_DIR.resolve(path).toAbsolutePath();
    }

    @Test
    @UnitTestMethod(target = PipelineInputResolver.class, name = "resolvePipelineInput", args = { FieldDescriptor.class,
            Object.class, Message.Builder.class })
    public void testResolvePipelineInput() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder().build(), TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                AT_PipelineInputResolver::getResolvedResourcePath,
                AT_PipelineInputResolver.TEST_OUTPUT_DIR);

        PipelineInputResolver pipelineInputResolver = new PipelineInputResolver(
                AT_PipelineInputResolver::getResolvedResourcePath);

        TestPipelineInput unresolvedTestPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_NAME);

        TestPipelineInput.Builder resolvedInputBuilder = unresolvedTestPipelineInput.toBuilder();
        Map<FieldDescriptor, Object> fields = resolvedInputBuilder.getAllFields();

        fields.forEach((pipelineField, pipelineFieldValue) -> {
            pipelineInputResolver.resolvePipelineInput(pipelineField, pipelineFieldValue,
                    resolvedInputBuilder);
        });

        TestPipelineInput resolvedPipelineInput = resolvedInputBuilder.build();

        TestSubPipelineInput testResolvedSubPipelineInput = resolvedPipelineInput.getTestPipelineInput();
        TestDimensionPipelineInput testResolvedDimensionPipelineInput = resolvedPipelineInput
                .getTestDimensionPipelineInput();

        TestSingleDimensionPipelineInput testResolvedSingleDimensionPipelineInput = testResolvedDimensionPipelineInput
                .getTestSingleDimensionPipelineInput();
        TestMultiDimensionPipelineInput testResolvedMultiDimensionPipelineInput = testResolvedDimensionPipelineInput
                .getTestMultiDimensionPipelineInput();

        TestSubPipelineInput testUnresolvedSubPipelineInput = unresolvedTestPipelineInput
                .getTestPipelineInput();
        TestDimensionPipelineInput testUnresolvedDimensionPipelineInput = unresolvedTestPipelineInput
                .getTestDimensionPipelineInput();

        TestSingleDimensionPipelineInput testUnresolvedSingleDimensionPipelineInput = testUnresolvedDimensionPipelineInput
                .getTestSingleDimensionPipelineInput();
        TestMultiDimensionPipelineInput testUnresolvedMultiDimensionPipelineInput = testUnresolvedDimensionPipelineInput
                .getTestMultiDimensionPipelineInput();

        Path testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile1());
        Path expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile1());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile2());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile2());
        assertTrue(testPath.isAbsolute());

        testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile3());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile3());
        assertTrue(testPath.isAbsolute());

        testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile4());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile4());
        assertTrue(testPath.isAbsolute());

        testPath = Path.of(testResolvedSubPipelineInput.getPluginDataFile());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getPluginDataFile());
        assertTrue(testPath.isAbsolute());

        testPath = Path.of(testResolvedSingleDimensionPipelineInput.getDimensionInstanceInput().getInputFile());
        expectedPath = getResolvedResourcePath(
                testUnresolvedSingleDimensionPipelineInput.getDimensionInstanceInput().getInputFile());
        assertTrue(testPath.isAbsolute());

        testPath = Path.of(testResolvedSingleDimensionPipelineInput.getDimensionInstanceInput()
                .getDimensionDataFile());
        expectedPath = getResolvedResourcePath(
                testUnresolvedSingleDimensionPipelineInput.getDimensionInstanceInput()
                        .getDimensionDataFile());
        assertTrue(testPath.isAbsolute());

        assertEquals(testUnresolvedMultiDimensionPipelineInput.getDimensionInstanceInputCount(),
                testResolvedMultiDimensionPipelineInput.getDimensionInstanceInputCount());

        for (int i = 0; i < testResolvedMultiDimensionPipelineInput.getDimensionInstanceInputCount(); i++) {
            TestDimensionInstanceInput unresolvedDimInstance = testUnresolvedMultiDimensionPipelineInput
                    .getDimensionInstanceInput(i);
            TestDimensionInstanceInput resolvedDimInstance = testResolvedMultiDimensionPipelineInput
                    .getDimensionInstanceInput(i);

            testPath = Path.of(resolvedDimInstance.getInputFile());
            expectedPath = getResolvedResourcePath(unresolvedDimInstance.getInputFile());
            assertTrue(testPath.isAbsolute());

            testPath = Path.of(resolvedDimInstance.getDimensionDataFile());
            expectedPath = getResolvedResourcePath(unresolvedDimInstance.getDimensionDataFile());
            assertTrue(testPath.isAbsolute());
        }
    }
}
