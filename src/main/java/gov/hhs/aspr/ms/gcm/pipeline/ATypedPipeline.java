package gov.hhs.aspr.ms.gcm.pipeline;

import java.nio.file.Path;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;

public abstract class ATypedPipeline<T> implements IPipeline {

    protected final TaskitEngineManager taskitEngineManager;

    public ATypedPipeline(TaskitEngineManager taskitEngineManager) {
        this.taskitEngineManager = taskitEngineManager;
    }

    public abstract ATypedPipeline<T> using(T pipelineInput, Path inputDirectory, Path outputDirectory);
}
