package examples.tensorflow.model.datasets;

import org.tensorflow.ndarray.ByteNdArray;


public record ImageBatch(ByteNdArray images, ByteNdArray labels) {

}
