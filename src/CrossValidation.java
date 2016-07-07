import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CrossValidation {
	private int fold;
	private Map<String, List<String>> category_file_path_list;

	public CrossValidation(int fold, String path) {
		this.fold = fold;
		List<String> file_path_list = (new FileIO()).readDir(path);
		category_file_path_list = new HashMap<>();
		for (String file_path : file_path_list) {
			File file = new File(file_path);
			String category = file.getParentFile().getName();
			if (category_file_path_list.containsKey(category)) {
				category_file_path_list.get(category).add(file_path);
			} else {
				List<String> tmp = new ArrayList<>();
				tmp.add(file_path);
				category_file_path_list.put(category, tmp);
			}
		}
	}

	public List<String> getTrainSet(int id) {
		List<String> train_set = new ArrayList<>();
		for (Entry entry : category_file_path_list.entrySet()) {
			List<String> file_path_list = (List<String>) entry.getValue();
			int size = file_path_list.size() / fold;
			for (int i = 0; i < id * size; ++i) {
				train_set.add(file_path_list.get(i));
			}
			for (int i = Math.min((id + 1) * size, file_path_list.size()); i < file_path_list.size(); ++i) {
				train_set.add(file_path_list.get(i));
			}
		}
		return train_set;
	}

	public List<String> getTestSet(int id) {
		List<String> test_set = new ArrayList<>();
		for (Entry entry : category_file_path_list.entrySet()) {
			List<String> file_path_list = (List<String>) entry.getValue();
			int size = (file_path_list.size() + fold - 1) / fold;
			// System.out.println(file_path_list.size() + " " + size + " " + id * size + " " + Math.min((id + 1) * size, file_path_list.size()));
			for (int i = id * size; i < Math.min((id + 1) * size, file_path_list.size()); ++i) {
				test_set.add(file_path_list.get(i));
			}
		}
		return test_set;
	}
}
