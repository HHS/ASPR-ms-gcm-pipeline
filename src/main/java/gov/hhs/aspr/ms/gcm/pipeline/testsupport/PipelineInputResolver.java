package gov.hhs.aspr.ms.gcm.pipeline.testsupport;

import com.google.protobuf.Descriptors.FieldDescriptor;

import java.nio.file.Path;
import java.util.function.Function;

import com.google.protobuf.Message;

/* 
 * This class contains advanced protobuf logic to progomatically go through each field in the pipeline input and resolve the path against PipelineTestPaths.RESOURCE_DIR
 * 
 * If you are not familiar with protobuf, it is suggested to not look nor touch this code with a 10ft pole.
 * 
 * Additionally, this code should rarely ever change under normal circumstances.
 * 
 * Above each method is a small blurb about what the method does, if you are curious.
 */
public class PipelineInputResolver {

    private final Function<String, Path> resolverFunction;

    // package access for PipelineTestSupport
    PipelineInputResolver(Function<String, Path> resolverFunction) {
        this.resolverFunction = resolverFunction;
    }

    /*
     * this method gets called from PipelineTestHelper
     * 
     * it takes in a protobuf message field, the field value and the pipeline input
     * builder
     * 
     * it determines the type of the field, either message -> normal pipeline,
     * dimensionPipelineInput -> dimension pipeline, or primative.
     * 
     * for primative values, it merely copies the value as is to the builder via the
     * set field
     * 
     * for normal pipelines and dimension pipelines, it calls the respective methods
     */
    public void resolvePipelineInput(FieldDescriptor PLIField, Object PLIValue, Message.Builder resolvedPLIBuilder) {
        resolvedPLIBuilder.setField(PLIField, resolveField(PLIField, PLIValue));
    }

    /*
     * This mathod takes a protobuf message value
     * 
     * It creates a new protobuf message builder for the message value
     * 
     * for each field on the value:
     * 
     * it checks if it is a repeated field or not
     * 
     * if not, it sets the field to the value returned by resolveField
     * 
     * if repeated, it adds the value returned by resolveField to the the field
     * 
     * it returns the built protobuf message
     */
    private Message resolveFields(Message message) {

        Message.Builder builder = message.newBuilderForType();

        message.getAllFields().forEach((field, value) -> {
            if (field.isRepeated()) {
                int fieldCount = message.getRepeatedFieldCount(field);
                for (int i = 0; i < fieldCount; i++) {
                    Object repeatedFieldValue = message.getRepeatedField(field, i);
                    builder.addRepeatedField(field, resolveField(field, repeatedFieldValue));
                }
            } else {
                builder.setField(field, resolveField(field, value));
            }
        });

        return builder.build();
    }

    /* 
     * Takes in a field descriptor and the field value
     * 
     * it determines the type of the field.
     * 
     * There are only 2 cases we care about:
     * 
     * String, in which case we can safely assume that the field is a path field and needs to be resolved
     * 
     * Message, in which case we then go through each field within the message and potentially resolve any fields within it, recurively
     * 
     * returns the new value of the field
     */
    private Object resolveField(FieldDescriptor field, Object value) {
        Object _value = value;

        switch (field.getJavaType()) {
        case STRING:
            String path = (String) _value;
            _value = this.resolverFunction.apply(path).toString();
            break;
        case MESSAGE:
            Message fieldMessage = (Message) _value;
            _value = resolveFields(fieldMessage);
            break;
        default:
            break;
        }
        return _value;
    }
}
