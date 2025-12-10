package examples.tensorflow;

import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TInt32;


/**
 * TensorFlow Java는 주로 Python으로 학습된 모델을 로드하여
 * Java 애플리케이션에서 추론(inference)하는 데 사용되며, 
 * TensorFlow 공식 문서](https://www.tensorflow.org/install/lang_java?hl=ko)에서 
 * 설치 및 예제 코드를 제공합니다. 
 * 기본적인 예시는 TensorFlow C++ API를 활용해 SavedModel을 불러와 텐서(Tensor)를 주고받으며
 * 예측하는 방식으로 구성되며, 
 * Maven/Gradle 의존성 추가, 모델 로드, 입력 텐서 생성, 예측 실행, 결과 해석 과정을 거칩니다. 
 * @see https://www.tensorflow.org/jvm/install?hl=ko
 * @see https://github.com/tensorflow/java-models
 */
public class HelloTensorFlow {

	public static void main(String[] args) {
		System.out.println("Hello TensorFlow " + TensorFlow.version());
		
		try(ConcreteFunction db1 = ConcreteFunction.create(HelloTensorFlow::db1);
				TInt32 x = TInt32.scalarOf(10);
				Tensor db1X = db1.call(x)) {
			System.out.println(x.getInt() + " doubled is " + ((TInt32)db1X).getInt());
		}
	}
	
	private static Signature db1(Ops tf) {
		Placeholder<TInt32> x = tf.placeholder(TInt32.class);
		Add<TInt32> db1X = tf.math.add(x, x);
		return Signature.builder().input("x", x).output("db1", db1X).build();
	}

}
