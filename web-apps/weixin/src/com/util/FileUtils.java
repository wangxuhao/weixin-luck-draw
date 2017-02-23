package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
	/**
	 * 获取资源文件
	 * @param t
	 * @param path
	 * @return
	 */
	public static File getResourceFile(Class<?> t, String path){
		
		//获取classPath路径
		File class_path_file = new File(t.getClassLoader().getResource("").getPath()+"/");
		
		//获取web根路径
		File root_path_file = class_path_file.getParentFile().getParentFile().getParentFile().getParentFile();
		
		//获取文件
		File file = new File(root_path_file.getAbsolutePath()+"/web-apps/weixin/"+ path);
		return file;
	}

	/**
	 * 将inputStream转String
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
