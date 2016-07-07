import java.io.File;
import java.util.List;

public class Test {
	private static final int FOLD = 10;

	public static void main(String[] args) {
		// (new FileIO()).transEncoding("GBK", "UTF-8", DataPreProcess.getDataPath());
		// experiment0(14);
		// experiment1(42000);
		experiment2(DataPreProcess.getDataPath());
	}

	private static void experiment0(int max_category_num) {
		DataContr dataContr = new DataContr(DataPreProcess.getDataPath());
		DataPreProcess dataPreProcess = new DataPreProcess();
		NaiveBayesianClassifier naiveBayesianClassifier = new NaiveBayesianClassifier();
		for (int category_num = 2; category_num <= max_category_num; ++category_num) {
			List<String> file_path_list = dataContr.getData(0, category_num);
			dataPreProcess.preProcess(file_path_list);

			CrossValidation crossValidation = new CrossValidation(FOLD, DataPreProcess.getPreProcessDataPath());

			double accuracy = 0.0;
			for (int i = 0; i < FOLD; ++i) {
				List<String> train_set = crossValidation.getTrainSet(i);
				List<String> test_set = crossValidation.getTestSet(i);
				naiveBayesianClassifier.train(train_set);
				double tmp = naiveBayesianClassifier.classify(test_set);
				accuracy += tmp;
			}
			System.out.println("category num: " + category_num + " acc: " + accuracy / FOLD);
		}
	}

	private static void experiment1(int max_file_num) {
		DataContr dataContr = new DataContr(DataPreProcess.getDataPath());
		DataPreProcess dataPreProcess = new DataPreProcess();
		NaiveBayesianClassifier naiveBayesianClassifier = new NaiveBayesianClassifier();
		for (int file_num = 1000; file_num <= max_file_num; file_num += 4100) {
			List<String> file_path_list = dataContr.getData(file_num, 0);
			dataPreProcess.preProcess(file_path_list);

			CrossValidation crossValidation = new CrossValidation(FOLD, DataPreProcess.getPreProcessDataPath());

			double accuracy = 0.0;
			for (int i = 0; i < FOLD; ++i) {
				List<String> train_set = crossValidation.getTrainSet(i);
				List<String> test_set = crossValidation.getTestSet(i);

				// System.out.println("正在训练第" + (i + 1) + "组训练集");
				naiveBayesianClassifier.train(train_set);

				// System.out.println("正在测试第" + (i + 1) + "组测试集");
				double tmp = naiveBayesianClassifier.classify(test_set);
				// System.out.println("第" + (i + 1) + "次实验的准确率为" + tmp);
				accuracy += tmp;
			}
			System.out.println("file num: " + file_num + " acc: " + accuracy / FOLD);
		}
	}

	private static void experiment2(String data_path) {
		DataPreProcess dataPreProcess = new DataPreProcess();
		NaiveBayesianClassifier naiveBayesianClassifier = new NaiveBayesianClassifier();

		List<String> file_path_list = (new FileIO()).readDir(data_path);
		dataPreProcess.preProcess(file_path_list);

		CrossValidation crossValidation = new CrossValidation(FOLD, DataPreProcess.getPreProcessDataPath());

		double accuracy = 0.0;
		for (int i = 0; i < FOLD; ++i) {
			List<String> train_set = crossValidation.getTrainSet(i);
			List<String> test_set = crossValidation.getTestSet(i);
			// System.out.println(train_set.size() + " " + test_set.size());

			System.out.println("正在训练第" + (i + 1) + "组训练集");
			naiveBayesianClassifier.train(train_set);

			System.out.println("正在测试第" + (i + 1) + "组测试集");
			double tmp = naiveBayesianClassifier.classify(test_set);
			System.out.println("第" + (i + 1) + "次实验的准确率为" + tmp);
			accuracy += tmp;
		}
		System.out.println("acc: " + accuracy / FOLD);
	}
}
