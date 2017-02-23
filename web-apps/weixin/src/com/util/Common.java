package com.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 常用工具类
 * 
 * @author 倪庆洋
 *
 */
public class Common {
	private static Log logger = LogFactory.getLog(Common.class);

	/**
	 * 以指定的字符按照指定的长度补齐某个字符串
	 * 
	 * @param source
	 *            待处理的字符串
	 * @param direction
	 *            在字符串左边还是右边补齐，left,right
	 * @param fillChar
	 *            填充的字符
	 * @param lastLength
	 *            补齐后的长度
	 * @return
	 */
	public static String fillString(String source, String direction, char fillChar, int lastLength) {
		if (source == null || source.length() > lastLength) {
			return source;
		}
		int diff = lastLength - source.length();
		StringBuilder builder = new StringBuilder(source);
		for (int i = 0; i < diff; i++) {
			if ("left".equals(direction)) {
				builder.insert(0, fillChar);
			} else if ("right".equals(direction)) {
				builder.append(fillChar);
			} else {
				throw new RuntimeException("参数direction只能为left或right！");
			}
		}
		return builder.toString();
	}

	/**
	 * 以指定的字符按照指定的长度补齐某个字符串
	 * 
	 * @param source
	 *            待处理的字符串
	 * @param direction
	 *            在字符串左边还是右边补齐，left,right
	 * @param fillChar
	 *            填充的字符
	 * @param lastLength
	 *            补齐后的长度
	 * @return
	 */
	public static String paddingString(String source, String direction, char fillChar, int totalLength) {
		if (source == null) {
			return source;
		}
		StringBuilder builder = new StringBuilder(source);
		for (int i = 0; i < totalLength; i++) {
			if ("left".equals(direction)) {
				builder.insert(0, fillChar);
			} else if ("right".equals(direction)) {
				builder.append(fillChar);
			} else {
				throw new RuntimeException("参数direction只能为left或right！");
			}
		}
		return builder.toString();
	}

	/**
	 * 将两个字符串数组连接起来
	 * 
	 * @param s1
	 *            － 第一个字符串数组
	 * @param s2
	 *            － 第二个字符串数组
	 * @return 连接后的字符串数组
	 */
	public static String[] linkString(String[] s1, String[] s2) {
		int len = s1.length + s2.length;
		String[] rtn = new String[len];
		System.arraycopy(s1, 0, rtn, 0, s1.length);
		System.arraycopy(s2, 0, rtn, s1.length, s2.length);
		return rtn;
	}

	public static <T> List<T> LinkList(List<T> list1, List<T> list2) {
		list1.addAll(list2);
		// for (int i=0; i < list1.size() + list2.size(); i++) {
		// list1.add(list2.get(i));
		// }
		return list1;
	}

	/**
	 * 判断是否为null或空字符串 空--truse 非空--false
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断对象是否为null,并判断对象是否为String类型，如果是，判断是否为空字符串
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isEmpty(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof String) {
			String objStr = (String) object;
			return Common.isEmpty(objStr);
		}
		return false;
	}

	/**
	 * 判断集合为null或长度为0
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Collection<?> col) {
		if (col == null || col.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断数组为null或长度为0
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object[] ary) {
		if (ary == null || ary.length == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 拼接条件SQL语句,多功能
	 * 
	 * @param ids
	 * @param leftWarp
	 *            每个元素左边被包裹的字符串
	 * @param rightWarp
	 *            每个元素右边被包裹的字符串
	 * @param separator
	 *            分隔符
	 * @param isLeft
	 *            分隔符在每个元素的左边 还是在元素的右边
	 * @param firstOrLastHide
	 *            分隔符在第一位或最后一位是否隐藏
	 * @return
	 */
	public static String toString(String[] ids, String leftWarp, String rightWarp, String separator, boolean isLeft,
			boolean firstOrLastHide) {
		if (ids == null) {
			return "";
		}
		StringBuilder condition = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (isLeft) {
				if (!(i == 0 && firstOrLastHide)) {
					condition.append(separator);
				}
			}
			condition.append(leftWarp + ids[i] + rightWarp);
			if (!isLeft) {
				if (!(i == ids.length - 1 && firstOrLastHide)) {
					condition.append(separator);
				}
			}
		}
		return condition.toString();
	}

	/**
	 * 将字符串数组转换成用英文逗号拼接成的字符串，并且每个元素用单引号包围
	 * 
	 * @param ids
	 * @return 数组为null返回空字符串
	 */
	public static String toString(String[] ids) {
		if (ids == null) {
			return "";
		}
		String id_line = "";
		for (int i = 0; i < ids.length; i++) {
			if (i == ids.length - 1) {
				id_line = id_line + "'" + ids[i] + "'";
				break;
			}
			id_line = id_line + "'" + ids[i] + "',";
		}
		return id_line;
	}

	/**
	 * 将字符串数组转换成用英文逗号拼接成的字符串，并且每个元素用单引号包围
	 * 
	 * @param ids
	 * @return 数组为null返回空字符串
	 */
	public static String toString(Object[] ids) {
		return Common.toString(ids);
	}

	/**
	 * 得到相对与本项目的路径
	 * 
	 * @param filePath
	 *            本项目下的相对路径,例子:"/"代表src下的目录
	 * @return 路径
	 * @throws BaseException
	 */
	public static String getPath(String filePath) {
		try {
			return Common.class.getResource(filePath).toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * "/"获取src，即tomcat下classes目标
	 * 
	 * @return
	 */
	public static String getSrcRootPath(String filepath) {
		try {
			String currentPath = Common.class.getResource("").toURI().getPath();
			/// D:/MyProgram/Tomact/Tomcat 6.0/webapps/ROOT/
			String flag = "WEB-INF/classes";
			String root = currentPath.substring(0, currentPath.indexOf(flag) + flag.length()) + filepath;
			return root;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * "/"获取src，即tomcat下项目部署的根目录
	 * 
	 * @return
	 */
	public static String getTomcatRootPath(String filepath) {
		try {
			String currentPath = Common.class.getResource("").toURI().getPath();
			/// D:/MyProgram/Tomact/Tomcat 6.0/webapps/ROOT/
			String flag = "WEB-INF/classes";
			String root = currentPath.substring(0, currentPath.indexOf(flag)) + filepath;
			return root;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 替换字符串,替换所有(效率要很高的，从org项目中反编译来的)
	 * 
	 * @param text
	 *            原字符串
	 * @param repl
	 *            替换前的字符串
	 * @param with
	 *            替换后的字符串
	 * @return 替换后的字符串
	 */
	public static String replace(String text, String repl, String with) {
		return replace(text, repl, with, -1);
	}

	/**
	 * 替换字符串(效率要很高的，从org项目中反编译来的)
	 * 
	 * @param text
	 *            原字符串
	 * @param repl
	 *            替换前的字符串
	 * @param with
	 *            替换后的字符串
	 * @param max
	 *            替换的次数,-1代表替换所有
	 * @return 替换后的字符串
	 */
	public static String replace(String text, String repl, String with, int max) {
		if (isEmpty(text) || isEmpty(repl) || with == null || max == 0)
			return text;
		int start = 0;
		int end = text.indexOf(repl, start);
		if (end == -1)
			return text;
		int replLength = repl.length();
		int increase = with.length() - replLength;
		increase = increase >= 0 ? increase : 0;
		increase *= max >= 0 ? max <= 64 ? max : 64 : 16;
		StringBuffer buf = new StringBuffer(text.length() + increase);
		for (; end != -1; end = text.indexOf(repl, start)) {
			buf.append(text.substring(start, end)).append(with);
			start = end + replLength;
			if (--max == 0)
				break;
		}

		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * 读文件
	 * 
	 * @param filename
	 *            文件名
	 * @return 读取的文件内容
	 */
	public static byte[] readfile(String filename) {
		File file = new File(filename);
		byte[] buff = new byte[(int) file.length()];
		FileInputStream data = null;
		try {
			data = new FileInputStream(file);
			data.read(buff);
			return buff;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			file = null;
			buff = null;
			try {
				if (data != null) {
					data.close();
				}
				data = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 写文件
	 * 
	 * @param filename
	 *            文件名
	 * @param data
	 *            写入文件的数据
	 */
	public static void writefile(String filename, byte[] data) {
		File file = new File(filename);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			file = null;
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取指定URL的内容
	 * 
	 * @param urlPath
	 * @return
	 */
	public static String readURL(String urlPath) {
		String content = "";
		InputStreamReader inReader = null;
		try {
			URL url = new URL(urlPath);
			inReader = new InputStreamReader(url.openStream());
			CharBuffer buffer = CharBuffer.allocate(1024);

			while (true) {
				buffer.clear();
				int read = inReader.read(buffer);
				if (read == -1) {
					break;
				}
				content += String.valueOf(buffer.array());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 将实体类转换为Map集合，键-属性名称，值-属性值，属性值为null则不会保存进去
	 * 
	 * @param object
	 * @return Map<属性名称, 属性值>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertObjectToMap(Object object) {
		if (object == null) {
			return new HashMap<String, Object>();
		}
		if (object instanceof Map) {
			return (Map<String, Object>) object;
		}
		// 封装实体
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
		// PropertyDescriptor[] descriptors =
		// beanWrapper.getPropertyDescriptors();
		Map<String, Object> condition = new HashMap<String, Object>();
		for (Field field : object.getClass().getDeclaredFields()) {
			String proName = field.getName();
			try {
				Object value = beanWrapper.getPropertyValue(proName);
				if (value == null) {
					continue;
				}
				condition.put(proName, value);
			} catch (NotReadablePropertyException e) {
				System.out.println("\n对象:" + object + "错误：属性" + proName + "没有找到get方法！\n");
				continue;
			}
		}
		return condition;
	}

	/**
	 * 将Map集合转换为Class指定的实体类对象
	 * 
	 * @param <T>
	 *            泛型
	 * @param map
	 *            Map集合
	 * @param clazz
	 *            输出对象的类型
	 * @return
	 */
	public static <T> T convertMapToObject(Map<String, Object> map, Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		for (Field field : fields) {
			String proName = field.getName();
			if (map.containsKey(proName)) {
				Object value = map.get(proName);
				try {
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
					Method setMethod = pd.getWriteMethod();
					String proTypeName = pd.getPropertyType().getName();
					System.out.println("-->" + pd.getName() + "-->" + proTypeName + "-->" + value + "--");
					if (proTypeName.equals(Integer.class.getName())) {
						if (Common.isEmpty(value)) {
							value = null;
						}
						setMethod.invoke(instance, new Integer((String) value));
					} else if (proTypeName.equals("int")) {
						if (Common.isEmpty(value)) {
							continue;
						}
						setMethod.invoke(instance, new Integer((String) value));
					} else if (proTypeName.equals(Double.class.getName()) || proTypeName.equals("double")) {
						if (Common.isEmpty(value)) {
							continue;
						}
						setMethod.invoke(instance, new Double((String) value));
					} else if (proTypeName.equals(Long.class.getName()) || proTypeName.equals("long")) {
						if (Common.isEmpty(value)) {
							continue;
						}
						setMethod.invoke(instance, new Long((String) value));
					} else if (proTypeName.equals(Float.class.getName()) || proTypeName.equals("float")) {
						if (Common.isEmpty(value)) {
							continue;
						}
						setMethod.invoke(instance, new Float((String) value));
					} else if (proTypeName.equals(Date.class.getName())) {
						if (!Common.isEmpty(value)) {
							setMethod.invoke(instance, java.sql.Date.valueOf((String) value));
						}
					} else {
						setMethod.invoke(instance, (String) value);
					}
				} catch (IntrospectionException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		return clazz.cast(instance);
	}

	/**
	 * 做MD5摘要运算
	 * 
	 * @param input
	 * @return
	 */
	public static String MD5(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] output = digest.digest(input);
			return new String(output);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用正则表达式将制定字符去掉
	 * 
	 * @param source
	 *            目标字符串
	 * @param tab
	 *            为NULL则去掉字符串中的\t,\n,\r
	 * @return
	 */
	public static String removeSpecialTab(String source, String tab) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(source);
		return m.replaceAll("");
	}

	/**
	 * 根据错误代码得到错误信息
	 * 
	 * @param errorCode
	 *            错误代码
	 * @return 错误信息
	 */
	public static String getErrorMessage(int errorCode, Class<?> clazz) {
		try {
			Class<?> ownerClass = clazz;
			Field[] fields = ownerClass.getFields();
			for (int i = 0; i < fields.length; i++) {
				String errorcodeProName = fields[i].getName();
				Field errorCodeField = ownerClass.getDeclaredField(errorcodeProName);
				errorCodeField.setAccessible(true);
				Object objValue = errorCodeField.get(ownerClass.newInstance());
				if (!(objValue instanceof Integer)) {
					continue;
				}
				int errorCodeProValue = errorCodeField.getInt(ownerClass.newInstance());
				if (errorCodeProValue == errorCode && errorCode < 0) {
					Field errorStrField = ownerClass.getDeclaredField(errorcodeProName + "_STR");
					errorStrField.setAccessible(true);
					return errorStrField.get(ownerClass.newInstance()).toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static ModelAndView getErrorView(HttpServletRequest request, String errorMessage) {
		request.getSession().setAttribute("errorMessage", errorMessage);
		// String viewName = "/uncaughtException.jsp";
		String viewName = "/page/com/error.jsp";
		return new ModelAndView(new RedirectView(viewName));
	}

	/**
	 * 返回两个时间间隔的天数
	 * 
	 * @param startDate
	 *            起始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public static long getBetweenDays(Date startDate, Date endDate) {
		long days = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
		return days;
	}

	/**
	 * 过滤特殊表情方法
	 * 
	 * @param str
	 * @return
	 */
	public static String filterEmojiString(String str) {
		Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher emojiMatcher = emoji.matcher(str);
		if (emojiMatcher.find()) {
			str = emojiMatcher.replaceAll("");
		}
		return str;
	}
}
