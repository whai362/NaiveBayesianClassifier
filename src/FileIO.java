import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileIO {
	private static final String ENCODING = "UTF-8";
	private List<String> file_path_list;

	/**
	 * 获取目录下的文件名
	 * 
	 * @param 文件所在的目录
	 */
	private void dfsReadDir(String path) {
		try {
			File file = new File(path);
			if (!file.isDirectory()) {

			} else {
				String[] list = file.list();
				for (String file_name : list) {
					File tmp_dir = new File(path + File.separator + file_name);
					if (!tmp_dir.isDirectory()) {
						file_path_list.add(tmp_dir.getAbsolutePath());
					} else {
						dfsReadDir(path + File.separator + file_name);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("找不到目录");
			// e.printStackTrace();
			// TODO: handle exception
		}
	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; ++i) {
				deleteDir(new File(dir, children[i]));
			}
		}
		// 目录此时为空，可以删除
		dir.delete();
	}

	public List<String> readDir(String path) {
		file_path_list = new ArrayList<>();
		dfsReadDir(path);
		return file_path_list;
	}

	public String readFile(String file_path) {
		return readFile(file_path, ENCODING);
	}

	/**
	 * 获取对应文件的内容
	 * 
	 * @param 文件的路径
	 * @return 文件内容
	 */
	public String readFile(String file_path, String encoding) {
		String file_content = "";
		try {
			File file = new File(file_path);
			if (file.isFile() && file.exists()) {
				InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					file_content += line + "\n";
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("找不到文件");
			// e.printStackTrace();
			// TODO: handle exception
		}
		return file_content;
	}

	public void writeFile(String file_path, String content) {
		writeFile(file_path, content, ENCODING, false);
	}

	public void writeFile(String file_path, String content, boolean over_write) {
		writeFile(file_path, content, ENCODING, over_write);
	}

	/**
	 * 把字符串输出到相应文件
	 * 
	 * @param 文件路径
	 * @param 要输出的内容
	 */
	public void writeFile(String file_path, String content, String encoding, boolean over_write) {
		try {
			File file = new File(file_path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, over_write),
					encoding);
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			bufferedWriter.write(content);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("输出到文件失败");
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void transEncoding_big(String old_encoding, String new_encoding, String path) {
		List<String> file_path_list = readDir(path);
		for (String file_path : file_path_list) {
			System.out.println(file_path);
			try {
				File file = new File(file_path);
				if (file.isFile() && file.exists()) {
					InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
							old_encoding);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						line += "\n";
						writeFile(file_path.replace(".txt", "_trans.txt"), line, new_encoding, true);
					}
					bufferedReader.close();
					System.out.println("转化成功");
				}
			} catch (Exception e) {
				System.out.println("找不到文件");
				e.printStackTrace();
				// TODO: handle exception
			}
			// writeFile(file_path, readFile(file_path, old_encoding),
			// new_encoding, false);
		}
	}

	public void transEncoding(String old_encoding, String new_encoding, String path) {
		List<String> file_path_list = readDir(path);
		for (String file_path : file_path_list) {
			System.out.println(file_path);
			writeFile(file_path, readFile(file_path, old_encoding), new_encoding, false);
		}
	}
}
