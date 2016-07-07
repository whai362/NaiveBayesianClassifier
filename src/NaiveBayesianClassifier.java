import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONObject;

public class NaiveBayesianClassifier {
	private static final double parameter = 0.001;
	private static final String remark = "_条件概率公式参数为" + parameter;

	// 总词数
	private Double word_num;

	// 词语列表
	private Set<String> word_set;

	// 每个类别的总词数
	private Map<String, Double> category_word_num;

	// 每个类别中每个词的出现次数
	private Map<String, Map<String, Double>> category_word_cnt;

	private List<String> real_category_list;
	private List<String> classify_category_list;

	public void train(List<String> train_set) {
		word_num = 0.0;
		word_set = new HashSet<>();
		category_word_num = new HashMap<>();
		category_word_cnt = new HashMap<>();

		for (String file_path : train_set) {
			File file = new File(file_path);
			String category = file.getParentFile().getName();
			String file_content = (new FileIO()).readFile(file_path);
			JSONObject jsonObject = JSONObject.fromString(file_content);
			Iterator<String> json_keys = jsonObject.keys();
			while (json_keys.hasNext()) {
				String key = json_keys.next();
				Double value = Double.valueOf(jsonObject.getInt(key));

				word_num += value;
				word_set.add(key);

				if (category_word_num.containsKey(category)) {
					category_word_num.put(category, category_word_num.get(category) + value);
				} else {
					category_word_num.put(category, value);
				}

				if (category_word_cnt.containsKey(category)) {
					if (category_word_cnt.get(category).containsKey(key)) {
						category_word_cnt.get(category).put(key, category_word_cnt.get(category).get(key) + value);
					} else {
						category_word_cnt.get(category).put(key, value);
					}
				} else {
					Map<String, Double> tmp = new HashMap<>();
					tmp.put(key, value);
					category_word_cnt.put(category, tmp);
				}
			}
		}
	}

	private Map<String, Double> computeCategoryProbability(double word_num, Set<String> word_set,
			Map<String, Double> category_word_num, Map<String, Map<String, Double>> category_word_cnt,
			Map<String, Double> testfile_word_cnt) {
		Map<String, Double> category_probability = new HashMap<>();
		for (Entry i : category_word_num.entrySet()) {
			// 一般情况下p很小, 所以对p取对数
			double log_p = 0;
			String category = (String) i.getKey();
			double cate_word_num = (double) i.getValue();
			log_p += Math.log(cate_word_num / word_num);
			for (Entry j : testfile_word_cnt.entrySet()) {
				String word = (String) j.getKey();
				double cate_word_cnt = 0.0;
				if (category_word_cnt.get(category).containsKey(word)) {
					cate_word_cnt = category_word_cnt.get(category).get(word);
				}
				log_p += Math.log((cate_word_cnt + parameter) / (cate_word_num + word_set.size()));
			}

			category_probability.put(category, log_p);
		}
		return category_probability;
	}

	public String computeConfusionMatrix(List<String> real_category_list, List<String> classify_category_list) {
		Map<String, Integer> category_to_id = new HashMap<>();
		Map<Integer, String> id_to_category = new HashMap<>();
		int category_num = 0;
		for (Entry entry : category_word_num.entrySet()) {
			String category = (String) entry.getKey();
			category_to_id.put(category, category_num);
			id_to_category.put(category_num, category);
			++category_num;
		}
		int[][] confusion_matrix = new int[category_num][category_num];
		for (int i = 0; i < confusion_matrix.length; ++i) {
			for (int j = 0; j < confusion_matrix[i].length; ++j) {
				confusion_matrix[i][j] = 0;
			}
		}
		for (int i = 0; i < real_category_list.size(); ++i) {
			String real_category = real_category_list.get(i);
			String classify_category = classify_category_list.get(i);
			int u = category_to_id.get(real_category);
			int v = category_to_id.get(classify_category);
			++confusion_matrix[u][v];
		}
		String matrix_to_string = "";
		for (int i = 0; i < category_num; ++i) {
			matrix_to_string += "	" + id_to_category.get(i);
		}
		matrix_to_string += "	accuracy\n";
		for (int i = 0; i < confusion_matrix.length; ++i) {
			matrix_to_string += id_to_category.get(i);
			double sum = 0.0;
			double hit = 0.0;
			for (int j = 0; j < confusion_matrix[i].length; ++j) {
				matrix_to_string += "	" + confusion_matrix[i][j];
				sum += confusion_matrix[i][j];
				if (i == j)
					hit = confusion_matrix[i][j];
			}
			matrix_to_string += "	" + (hit / sum) + "\r\n";
		}
		return matrix_to_string;
	}

	public String classify(String file_path) {
		String file_content = (new FileIO()).readFile(file_path);
		JSONObject jsonObject = JSONObject.fromString(file_content);
		Map<String, Double> file_word_cnt = new HashMap<>();
		Iterator<String> json_keys = jsonObject.keys();
		while (json_keys.hasNext()) {
			String key = json_keys.next();
			Double value = Double.valueOf(jsonObject.getInt(key));
			file_word_cnt.put(key, value);
		}
		// System.out.println(file_word_cnt);
		Map<String, Double> category_probability = computeCategoryProbability(word_num, word_set, category_word_num,
				category_word_cnt, file_word_cnt);
		String best_category = null;
		double max_value = 0;
		for (Entry entry : category_probability.entrySet()) {
			if (best_category == null) {
				best_category = (String) entry.getKey();
				max_value = (double) entry.getValue();
			} else if (max_value < (double) entry.getValue()) {
				best_category = (String) entry.getKey();
				max_value = (double) entry.getValue();
			}
		}
		return best_category;
	}

	public double classify(List<String> file_path_list) {
		int right_cnt = 0;
		real_category_list = new ArrayList<>();
		classify_category_list = new ArrayList<>();

		for (String file_path : file_path_list) {
			File file = new File(file_path);
			String real_category = file.getParentFile().getName();
			String classify_category = classify(file_path);
			real_category_list.add(real_category);
			classify_category_list.add(classify_category);
			if (real_category.equals(classify_category)) {
				++right_cnt;
			}
		}

		return Double.valueOf(right_cnt) / file_path_list.size();
	}

	public List<String> getReal_category_list() {
		return real_category_list;
	}

	public List<String> getClassify_category_list() {
		return classify_category_list;
	}

	public static String getRemark() {
		return remark;
	}
}
