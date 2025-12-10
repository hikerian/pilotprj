package examples.tensorflow.model.datasets.mnist;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.tensorflow.ndarray.ByteNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;

import examples.tensorflow.model.datasets.ImageBatch;
import examples.tensorflow.model.datasets.ImageBatchIterator;

import static org.tensorflow.ndarray.index.Indices.sliceFrom;
import static org.tensorflow.ndarray.index.Indices.sliceTo;


public class MnistDataset {
	public static final int NUM_CLASSES = 10;

	public static MnistDataset create(int validationSize, String trainingImagesArchive, String trainingLabelsArchive,
			String testImagesArchive, String testLabelsArchive) {
		try {
			ByteNdArray trainingImages = readArchive(trainingImagesArchive);
			ByteNdArray trainingLabels = readArchive(trainingLabelsArchive);
			ByteNdArray testImages = readArchive(testImagesArchive);
			ByteNdArray testLabels = readArchive(testLabelsArchive);

			if (validationSize > 0) {
				return new MnistDataset(trainingImages.slice(sliceFrom(validationSize)),
						trainingLabels.slice(sliceFrom(validationSize)), trainingImages.slice(sliceTo(validationSize)),
						trainingLabels.slice(sliceTo(validationSize)), testImages, testLabels);
			}
			return new MnistDataset(trainingImages, trainingLabels, null, null, testImages, testLabels);

		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private static ByteNdArray readArchive(String archiveName) throws IOException {
		try (DataInputStream archiveStream = new DataInputStream(
				new GZIPInputStream(MnistDataset.class.getClassLoader().getResourceAsStream(archiveName)))) {
			archiveStream.readShort(); // first two bytes are always 0
			byte magic = archiveStream.readByte();
			if (magic != TYPE_UBYTE) {
				throw new IllegalArgumentException("\"" + archiveName + "\" is not a valid archive");
			}
			int numDims = archiveStream.readByte();
			long[] dimSizes = new long[numDims];
			int size = 1; // for simplicity, we assume that total size does not exceeds Integer.MAX_VALUE
			for (int i = 0; i < dimSizes.length; ++i) {
				dimSizes[i] = archiveStream.readInt();
				size *= dimSizes[i];
			}
			byte[] bytes = new byte[size];
			archiveStream.readFully(bytes);
			return NdArrays.wrap(Shape.of(dimSizes), DataBuffers.of(bytes, true, false));
		}
	}

	private static final int TYPE_UBYTE = 0x08;

	private final ByteNdArray trainingImages;
	private final ByteNdArray trainingLabels;
	private final ByteNdArray validationImages;
	private final ByteNdArray validationLabels;
	private final ByteNdArray testImages;
	private final ByteNdArray testLabels;
	private final long imageSize;

	private MnistDataset(ByteNdArray trainingImages, ByteNdArray trainingLabels, ByteNdArray validationImages,
			ByteNdArray validationLabels, ByteNdArray testImages, ByteNdArray testLabels) {
		this.trainingImages = trainingImages;
		this.trainingLabels = trainingLabels;
		this.validationImages = validationImages;
		this.validationLabels = validationLabels;
		this.testImages = testImages;
		this.testLabels = testLabels;
		this.imageSize = trainingImages.get(0).shape().size();
	}

	public Iterable<ImageBatch> trainingBatches(int batchSize) {
		return () -> new ImageBatchIterator(batchSize, trainingImages, trainingLabels);
	}

	public Iterable<ImageBatch> validationBatches(int batchSize) {
		return () -> new ImageBatchIterator(batchSize, validationImages, validationLabels);
	}

	public Iterable<ImageBatch> testBatches(int batchSize) {
		return () -> new ImageBatchIterator(batchSize, testImages, testLabels);
	}

	public ImageBatch testBatch() {
		return new ImageBatch(testImages, testLabels);
	}

	public long imageSize() {
		return imageSize;
	}

	public long numTrainingExamples() {
		return trainingLabels.shape().get(0);
	}

	public long numTestingExamples() {
		return testLabels.shape().get(0);
	}

	public long numValidationExamples() {
		return validationLabels.shape().get(0);
	}

}
