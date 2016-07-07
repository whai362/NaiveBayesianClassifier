import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataContr {
	private static final int MIN_FILE_NUM = 1000;
	private static final int MIN_CATEGORY_NUM = 2;
	private Map<String, List<String>> files_in_category;
	private List<String> category_list;
	List<String> file_path_list;

	public DataContr(String data_path) {
		files_in_category = new HashMap<>();
		category_list = new ArrayList<>();
		file_path_list = (new FileIO()).readDir(data_path);
		for (String file_path : file_path_list) {
			String category = (new File(file_path)).getParentFile().getName();
			if (files_in_category.containsKey(category)) {
				files_in_category.get(category).add(file_path);
			} else {
				List<String> tmp = new ArrayList<>();
				tmp.add(file_path);
				files_in_category.put(category, tmp);
				category_list.add(category);
			}
		}
	}

	public List<String> getData(int file_num, int category_num) {
		if(file_num != 0) {
			file_num = Math.max(file_num, MIN_FILE_NUM);
			file_num = Math.min(file_num, file_path_list.size());
		}
		category_num = Math.max(category_num, MIN_CATEGORY_NUM);
		category_num = Math.min(category_num, category_list.size());
		List<String> file_path_list = new ArrayList<>();
		if (file_num == 0) {
			for (int i = 0; i < category_num; ++i) {
				file_path_list.addAll(files_in_category.get(category_list.get(i)));
			}
		} else {
			int col = 0;
			int cnt = 0;
			while (true) {
				for (String category : category_list) {
					List<String> tmp = files_in_category.get(category);
					if (col < tmp.size()) {
						file_path_list.add(tmp.get(col));
						++cnt;
						if (cnt >= file_num)
							break;
					}
				}
				if (cnt >= file_num)
					break;
				++col;
			}
		}
		return file_path_list;
	}
}
