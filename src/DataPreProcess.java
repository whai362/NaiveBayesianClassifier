import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import net.sf.json.JSONObject;

public class DataPreProcess {
	private static final String ENCODING = "UTF-8";
	private static final String DATA_PATH = "E:\\Project\\project_data\\NaiveBayesianClassifier\\data";
	private static final String STOP_WORDS_PATH = "E:\\Project\\project_data\\NaiveBayesianClassifier\\stop_words\\stop_words.txt";
	private static final String PRE_PROCESS_DATA_PATH = "E:\\Project\\project_data\\NaiveBayesianClassifier\\preprocess_data";
	// 停用词表
	private static Set<String> stop_word_set = new HashSet<>();

	/**
	 * 用ansj_seg进行分词
	 * 
	 * @param 待分词的文本
	 * @return 分词结果
	 */
	private List<String> cutWords(String content) {
		List<String> word_list = new ArrayList<>();
		try {
			List<Term> words = ToAnalysis.parse(content);
			for (Term term : words) {
				word_list.add(term.getName());
			}
		} catch (Exception e) {
			System.out.println("分词失败");
			// e.printStackTrace();
			// TODO: handle exception
		}
		return word_list;
	}

	/**
	 * 加载分词表到stop_word_set
	 */
	private void loadStopWords() {
		try {
			File file = new File(STOP_WORDS_PATH);
			if (file.isFile() && file.exists()) {
				InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), ENCODING);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stop_word_set.add(line);
				}
				stop_word_set.add("");
				stop_word_set.add(" ");
				stop_word_set.add("\r");
				stop_word_set.add("\n");
				stop_word_set.add("\r\n");
				stop_word_set.add("nbsp");
				stop_word_set.add("\u0000");
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("找不到文件");
			// e.printStackTrace();
			// TODO: handle exception
		}
	}

	/**
	 * 去停用词
	 * 
	 * @param 去停用词前的单词列表
	 * @return 去停用词后的单词列表
	 */
	private List<String> filterStopWords(List<String> word_list) {
		List<String> filtered_word_list = new ArrayList<>();
		for (String word : word_list) {
			if (!stop_word_set.contains(word)) {
				filtered_word_list.add(word);
			}
		}
		return filtered_word_list;
	}

	/**
	 * 统计文件中每个词出现的次数, 以json的格式返回
	 * 
	 * @param 单词列表
	 * @return 统计结果, json格式
	 */
	private String word_count(List<String> word_list) {
		Map<String, Integer> word_cnt = new HashMap<>();
		for (String word : word_list) {
			if (word_cnt.containsKey(word)) {
				word_cnt.put(word, word_cnt.get(word) + 1);
			} else {
				word_cnt.put(word, 1);
			}
		}
		String table = JSONObject.fromObject(word_cnt).toString();
		return table;
	}

	public void preProcess() {
		// 遍历文件
		List<String> file_path_list = (new FileIO()).readDir(DATA_PATH);
		preProcess(file_path_list);
	}

	public void preProcess(List<String> file_path_list) {
		// 加载停用词表
		loadStopWords();
		
		File file = new File(PRE_PROCESS_DATA_PATH);
		if (file.exists()) {
			(new FileIO()).deleteDir(file);
		}
		file.mkdir();
		
		for (String file_path : file_path_list) {
			String file_content = (new FileIO()).readFile(file_path);
			// 分词
			List<String> word_list = cutWords(file_content);

			// 去停用词
			word_list = filterStopWords(word_list);

			// 统计文件中每个词出现的次数, 以json的格式返回
			String table = word_count(word_list);
			// System.out.println(table);			
			
			String output_file_path = file_path.replace("\\data\\", "\\preprocess_data\\").replace(".txt", ".json");
			// System.out.println(output_file_path);
			(new FileIO()).writeFile(output_file_path, table);
		}
	}

	public static String getPreProcessDataPath() {
		return PRE_PROCESS_DATA_PATH;
	}

	public static String getDataPath() {
		return DATA_PATH;
	}
}
