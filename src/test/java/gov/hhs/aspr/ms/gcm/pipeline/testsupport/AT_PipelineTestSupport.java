package gov.hhs.aspr.ms.gcm.pipeline.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestDimensionInstanceInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestMultiDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestSingleDimensionPipelineInput;
import gov.hhs.aspr.ms.gcm.pipeline.testsupport.input.TestSubPipelineInput;
import gov.hhs.aspr.ms.gcm.simulation.plugins.globalproperties.datamanagers.GlobalPropertiesPluginData;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.globalproperties.GlobalPropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.globalproperties.data.input.GlobalPropertiesPluginDataInput;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.properties.PropertiesTranslator;
import gov.hhs.aspr.ms.gcm.taskit.protobuf.plugins.reports.ReportsTranslator;
import gov.hhs.aspr.ms.taskit.core.TranslationEngine;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_PipelineTestSupport {
    private final Path REOURCE_DIR = ResourceHelper.getResourceDir(this.getClass());
    private final String TEST_OUTPUT_DIR_NAME = "testOutput";
    private final Path TEST_OUTPUT_DIR = getResolvedResourcePath(TEST_OUTPUT_DIR_NAME);
    private final String TEST_FILE_NAME = "pipeline_tester.json";
    private final String TEST_FILE_DIR_NAME = "pipeline_tester_dir.json";
    private final String TEST_FILE_PREV_NAME = "pipeline_tester_prev.json";
    private final String TEST_FILE_BAD_NAME = "pipeline_tester_bad.json";
    private final String TEST_FILE_NAME_RESOLVED = "pipeline_tester_resolved.json";

    private final String TEST_GP_FILE_1 = "globalPropertiesPluginData1.json";
    private final String TEST_GP_FILE_2 = "globalPropertiesPluginData2.json";
    private final String TEST_GP_FILE_3 = "globalPropertiesPluginData3.json";
    private final String TEST_GP_FILE_4 = "globalPropertiesPluginData4.json";

    private final Path TEST_GP_FILE_1_PATH = getResolvedResourcePath(TEST_GP_FILE_1);
    private final Path TEST_GP_FILE_2_PATH = getResolvedResourcePath(TEST_GP_FILE_2);
    private final Path TEST_GP_FILE_3_PATH = getResolvedResourcePath(TEST_GP_FILE_3);
    private final Path TEST_GP_FILE_4_PATH = getResolvedResourcePath(TEST_GP_FILE_4);

    private final Path getResolvedResourcePath(String path) {
        return REOURCE_DIR.resolve(path).toAbsolutePath();
    }

    @Test
    @UnitTestConstructor(target = PipelineTestSupport.class, args = { TranslationEngine.class, Message.class,
            Class.class, Function.class, Path.class })
    public void testConstructor() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder().build(), TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                this::getResolvedResourcePath,
                TEST_OUTPUT_DIR);

        assertNotNull(testPipelineInputTestSupport);
    }

    @Test
    @UnitTestMethod(target = PipelineTestSupport.class, name = "createResolvedPipelineInputFile", args = {
            Message.class,
            String.class })
    public void testCreateResolvedPipelineInputFile() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder().build(), TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                this::getResolvedResourcePath,
                TEST_OUTPUT_DIR);

        TestPipelineInput testPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_NAME);
        TestPipelineInput resolvedPipelineInput = testPipelineInputTestSupport
                .getResolvedPipelineInput(testPipelineInput, false, false);

        String filePath = testPipelineInputTestSupport.createResolvedPipelineInputFile(resolvedPipelineInput,
                TEST_FILE_NAME_RESOLVED);

        Path resolvedPath = Path.of(filePath);

        assertTrue(resolvedPath.isAbsolute());
        assertTrue(resolvedPath.toFile().exists());
    }

    @Test
    @UnitTestMethod(target = PipelineTestSupport.class, name = "filesAreSame", args = { Class.class, Class.class,
            Path.class, Path.class })
    public void testFilesAreSame() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder()
                        .addTranslator(GlobalPropertiesTranslator.getTranslator())
                        .addTranslator(PropertiesTranslator.getTranslator())
                        .addTranslator(ReportsTranslator.getTranslator())
                        .build(),
                TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                this::getResolvedResourcePath,
                TEST_OUTPUT_DIR);

        assertTrue(testPipelineInputTestSupport.filesAreSame(GlobalPropertiesPluginDataInput.class,
                GlobalPropertiesPluginData.class, TEST_GP_FILE_1_PATH, TEST_GP_FILE_2_PATH));
        assertFalse(testPipelineInputTestSupport.filesAreSame(GlobalPropertiesPluginDataInput.class,
                GlobalPropertiesPluginData.class, TEST_GP_FILE_1_PATH, TEST_GP_FILE_3_PATH));
        assertFalse(testPipelineInputTestSupport.filesAreSame(GlobalPropertiesPluginDataInput.class,
                GlobalPropertiesPluginData.class, TEST_GP_FILE_2_PATH, TEST_GP_FILE_3_PATH));
        assertFalse(testPipelineInputTestSupport.filesAreSame(GlobalPropertiesPluginDataInput.class,
                GlobalPropertiesPluginData.class, TEST_GP_FILE_3_PATH, TEST_GP_FILE_4_PATH));
    }

    @Test
    @UnitTestMethod(target = PipelineTestSupport.class, name = "getResolvedPipelineInput", args = { Message.class,
            boolean.class,
            boolean.class })
    public void testGetResolvedPipelineInput() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder().build(), TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                this::getResolvedResourcePath,
                TEST_OUTPUT_DIR);

        TestPipelineInput unresolvedTestPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_NAME);
        TestPipelineInput resolvedPipelineInput = testPipelineInputTestSupport
                .getResolvedPipelineInput(unresolvedTestPipelineInput, false, false);

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
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile3());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile3());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSubPipelineInput.getTestDataFile4());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getTestDataFile4());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSubPipelineInput.getPluginDataFile());
        expectedPath = getResolvedResourcePath(testUnresolvedSubPipelineInput.getPluginDataFile());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSingleDimensionPipelineInput.getDimensionInstanceInput().getInputFile());
        expectedPath = getResolvedResourcePath(
                testUnresolvedSingleDimensionPipelineInput.getDimensionInstanceInput().getInputFile());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(testResolvedSingleDimensionPipelineInput.getDimensionInstanceInput()
                .getDimensionDataFile());
        expectedPath = getResolvedResourcePath(
                testUnresolvedSingleDimensionPipelineInput.getDimensionInstanceInput()
                        .getDimensionDataFile());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

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
            assertEquals(expectedPath, testPath);

            testPath = Path.of(resolvedDimInstance.getDimensionDataFile());
            expectedPath = getResolvedResourcePath(unresolvedDimInstance.getDimensionDataFile());
            assertTrue(testPath.isAbsolute());
            assertEquals(expectedPath, testPath);
        }

        unresolvedTestPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_DIR_NAME);
        resolvedPipelineInput = testPipelineInputTestSupport
                .getResolvedPipelineInput(unresolvedTestPipelineInput, true, false);

        testPath = Path.of(resolvedPipelineInput.getInputDirectory());
        expectedPath = getResolvedResourcePath(unresolvedTestPipelineInput.getInputDirectory());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        testPath = Path.of(resolvedPipelineInput.getOutputDirectory());
        expectedPath = getResolvedResourcePath(unresolvedTestPipelineInput.getOutputDirectory());
        assertTrue(testPath.isAbsolute());
        assertEquals(expectedPath, testPath);

        unresolvedTestPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_PREV_NAME);
        resolvedPipelineInput = testPipelineInputTestSupport
                .getResolvedPipelineInput(unresolvedTestPipelineInput, false, true);

        assertTrue(resolvedPipelineInput.getRunningWithPreviousData());
    }

    @Test
    @UnitTestMethod(target = PipelineTestSupport.class, name = "getUnresolvedPipelineInput", args = {
            String.class })
    public void testGetUnresolvedPipelineInput() {
        PipelineTestSupport<TestPipelineInput> testPipelineInputTestSupport = new PipelineTestSupport<>(
                ProtobufTranslationEngine.builder().build(), TestPipelineInput.getDefaultInstance(),
                TestPipelineInput.class,
                this::getResolvedResourcePath,
                TEST_OUTPUT_DIR);

        TestPipelineInput testPipelineInput = testPipelineInputTestSupport
                .getUnresolvedPipelineInput(TEST_FILE_NAME);

        TestSubPipelineInput testSubPipelineInput = testPipelineInput.getTestPipelineInput();
        TestDimensionPipelineInput testDimensionPipelineInput = testPipelineInput
                .getTestDimensionPipelineInput();

        TestSingleDimensionPipelineInput testSingleDimensionPipelineInput = testDimensionPipelineInput
                .getTestSingleDimensionPipelineInput();
        TestMultiDimensionPipelineInput testMultiDimensionPipelineInput = testDimensionPipelineInput
                .getTestMultiDimensionPipelineInput();

        Path testPath = Path.of(testSubPipelineInput.getTestDataFile1());
        assertNotNull(testPath);

        testPath = Path.of(testSubPipelineInput.getTestDataFile2());
        assertNotNull(testPath);

        testPath = Path.of(testSubPipelineInput.getTestDataFile3());
        assertNotNull(testPath);

        testPath = Path.of(testSubPipelineInput.getTestDataFile4());
        assertNotNull(testPath);

        testPath = Path.of(testSubPipelineInput.getPluginDataFile());
        assertNotNull(testPath);

        testPath = Path.of(testSingleDimensionPipelineInput.getDimensionInstanceInput().getInputFile());
        assertNotNull(testPath);

        testPath = Path.of(testSingleDimensionPipelineInput.getDimensionInstanceInput().getDimensionDataFile());
        assertNotNull(testPath);

        for (TestDimensionInstanceInput testDimensionInstanceInput : testMultiDimensionPipelineInput
                .getDimensionInstanceInputList()) {
            testPath = Path.of(testDimensionInstanceInput.getInputFile());
            assertNotNull(testPath);

            testPath = Path.of(testDimensionInstanceInput.getDimensionDataFile());
            assertNotNull(testPath);
        }

        // preconditions:
        // file path does not exist
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            testPipelineInputTestSupport
                    .getUnresolvedPipelineInput("badFile.json");
        });

        assertEquals(
                "Provided path does not exist: " + getResolvedResourcePath("badFile.json").toAbsolutePath().toString(),
                runtimeException.getMessage());

        // json is bad/has unknown fields
        runtimeException = assertThrows(RuntimeException.class, () -> {
            testPipelineInputTestSupport
                    .getUnresolvedPipelineInput(TEST_FILE_BAD_NAME);
        });

        assertEquals(InvalidProtocolBufferException.class,
                runtimeException.getCause().getClass());
    }
}
