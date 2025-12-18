package example.djl;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;


/**
 * An example of inference using an image classification model.
 * <p>See this <a href="https://github.com/deepjavalibrary/djl/blob/master/examples/docs/image_classification.md">doc</a>
 * for information about this example.
 * @see TrainMnist.java
 */
public class ImageClassification {
	private static final Logger logger = LoggerFactory.getLogger(ImageClassification.class);
	
	
	private ImageClassification() {
		
	}
	
	public static void main(String[] args) throws MalformedModelException, IOException, TranslateException {
		Classifications classifications = predict();
		logger.info("{}", classifications);
	}
	
	public static Classifications predict() throws IOException, MalformedModelException, TranslateException {
		Path imageFile = Paths.get("src/test/resources/0.png");
		Image img = ImageFactory.getInstance().fromFile(imageFile);
		
		String modelName = "mlp";
		try(Model model = Model.newInstance(modelName, "PyTorch")) {
			model.setBlock(new Mlp(28 * 28, 10, new int[] {128, 64}));
			
			// Assume you have run TrainMnist.java example, and saved model in mlp_model folder.
			Path modelDir = Paths.get("mlp_model");
			model.load(modelDir);
			
			List<String> classes = IntStream.range(0,  10).mapToObj(String::valueOf).collect(Collectors.toList());
			Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
					.addTransform(new ToTensor())
					.optSynset(classes)
					.optApplySoftmax(true)
					.build();
			
			try(Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
				return predictor.predict(img);
			}
		}
		
	}

}
