package mqo_gq;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import Common.SeverInfo;

public class Utils {
	
	public static void main(String[] args) {
//		System.out.println(getSesameSeverArr());
	 

		
	}
	
	// 读取配置文件信息
	public static ArrayList<SeverInfo> getSesameSeverArr(){
		Properties prop = new Properties();
//		String file_name = "D:\\myStudyCode\\java_workspace\\multiQueryOpt\\src\\server.properties";
		ArrayList<SeverInfo> severList = new ArrayList<SeverInfo>();
		SeverInfo serverInfo = null;
		String[] RepositoryArr = {"WATDIV","TEST"};
		try {
//			InputStream in = new FileInputStream(new File(file_name));
			InputStream in = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream("server.properties");
			prop.load(in);
			
			// 存入服务器信息
			String sesameS = prop.getProperty("SESAMESERVER");
			for(int i=0;i<RepositoryArr.length;i++){
				serverInfo = new SeverInfo();
				String RepositoryID = prop.getProperty(RepositoryArr[i]);
				serverInfo.setSesameServer(sesameS);
				serverInfo.setRepositoryID(RepositoryID);
				severList.add(serverInfo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return severList;
	}
	
	
	public static void map_test(){
        TreeMap<Integer,String> treemap = new TreeMap<Integer, String>();
        HashMap<Integer,String> hashmap = new HashMap<Integer,String>();

        treemap.put(1234, "北京");
        treemap.put(345, "南京");
        treemap.put(664, "秦皇岛");
        treemap.put(1266, "济南");
        treemap.put(178, "天津");
        treemap.put(1789, "上海");
        treemap.put(1023, "苏州");

        hashmap.put(1234, "北京");
        hashmap.put(345, "南京");
        hashmap.put(664, "秦皇岛");
        hashmap.put(1266, "济南");
        hashmap.put(178, "天津");
        hashmap.put(1789, "上海");
        hashmap.put(1023, "苏州");

        System.out.println("******************TreeMap Output******************");

        Set<Entry<Integer,String>> entrySet = treemap.entrySet();
        for(Entry<Integer,String> ent : entrySet){

            System.out.println(ent.getValue() + " " + ent.getKey());

        }

        System.out.println("******************HashMap Output***********************");

        Set<Entry<Integer,String>> entrySet1 = hashmap.entrySet();
        for(Entry<Integer,String> ent : entrySet1){

            System.out.println(ent.getValue() + " " + ent.getKey());

        }
	}
	
	
	
	
	
}
