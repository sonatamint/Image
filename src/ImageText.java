import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import com.baidu.aip.ocr.AipOcr;


public class ImageText {
	
	//设置APPID/AK/SK
	public static final String APP_ID = "14719626";
	public static final String API_KEY = "QLbkEKz8RWx7YQ5aNetdMEM4";
	public static final String SECRET_KEY = "lpaKxBBqtOObO4Z3uzKFhj1nxAQI0FZ8";

	public static void main(String[] args) throws Exception {
		
		test();
		//ocr();
		//哈哈哈

	}
	
	public static void test() throws Exception{
		String imgFile = "c:\\facts\\微信图片_20190118111629.jpg";// 待处理的图片
		String imgbese = getImgStr(imgFile);
		//BufferedImage bufImage = ImageIO.read(new File(imgFile));
		//System.out.println(imgbese.length());
		//writeFile("c:\\facts\\images\\IMG_20181202_133842.txt",imgbese);
		String response = framedetection(imgbese);
		System.out.println(response);
		//{"location":{"height":752,"left":1377,"top":2970,"width":980},"name":"ntr_frame","score":0.9931187629699707},
		//		{"location":{"height":1525,"left":153,"top":2232,"width":2632},"name":"txt_frame","score":0.9165641665458679}
		//从返回信息中找到"left","top","width","height"信息填到下面的函数中
		//ImageIO.write(bufImage.getSubimage(1377, 2970, 980, 752), "JPEG", new File(imgFile.replace(".jp", "sub.jp"))) ;
		//generateImage(imgbese,"c:\\facts\\generated\\IMG_20181202_133842.jpg");
	}
	
	public static void ocr() throws Exception{
    	String APP_ID = "14459929";
        String API_KEY = "CimqpDEd3rqR0pOtMHnmteIW";
        String SECRET_KEY = "16KnnaSZPAqpAobdnSo8zaybbiMp2MGn";
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        //
        // 高精度带位置信息
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("recognize_granularity", "big");
        options.put("detect_direction", "true");
        options.put("vertexes_location", "true");
        options.put("probability", "true");
        
        String image = "C:\\facts\\微信图片_20190131114826C.jpg";
        JSONObject res = client.accurateGeneral(image, options);
        String str = res.toString(2);
        Pattern words = Pattern.compile("\"words\":.*\n");
        Matcher mch = words.matcher(str);
        while(mch.find()){
        	System.out.print(mch.group());
		}
        System.out.println(res.toString(2));
        //
        
        /* 高精度
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("probability", "true");
        
        String image = "C:\\Users\\songbo\\Desktop\\食品规则资料\\NewOCR\\微信图片_20181212151232yyy.jpg";
        JSONObject res = client.basicAccurateGeneral(image, options);
        String str = res.toString(2);
        Pattern words = Pattern.compile("\"words\":.*\n");
        Matcher mch = words.matcher(str);
        while(mch.find()){
        	System.out.print(mch.group());
		}
        System.out.println(res.toString(2));
        
        /*
        // 低精度
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        
        String image = "C:\\Users\\LL\\Desktop\\食品规则资料\\NewOCR\\zhh\\新文档 2018-12-02 09.42.48_1.jpg";
        JSONObject res = client.basicGeneral(image, options);
        String str = res.toString(2);
        Pattern words = Pattern.compile("\"words\":.*\n");
        Matcher mch = words.matcher(str);
        while(mch.find()){
        	System.out.print(mch.group());
		}
        System.out.println(res.toString(2));
        */
    }
	
	static void writeFile(String pathto, String content) throws Exception {
		File fl = new File(pathto);
		File dir = fl.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		FileWriter fout = new FileWriter(fl);
		BufferedWriter bwr = new BufferedWriter(fout);
		bwr.write(content);
		bwr.close();
		fout.close();
	}
	
	public static void tableRecognition() throws Exception {
		
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
		//异步接口
		//使用封装的同步轮询接口
		JSONObject jsonres = client.tableRecognizeToJson("c:\\facts\\images\\table.jpg", 20000);
		String result = ">>> Result 1 is: "+jsonres.toString(2);
		JSONObject excelres = client.tableRecognizeToExcelUrl("c:\\facts\\images\\table.jpg", 20000);
		result += "\r\n>>> Result 2 is: "+excelres.toString(2);
		writeFile("c:\\facts\\images\\table.txt",result);
		}

	public static String framedetection(String imgbase) throws Exception {
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();  
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build(); 
		
		JSONObject json = new JSONObject();
		json.put("image", imgbase);
		//CloseableHttpClient httpClient = HttpClientBuilder.create().build();//this produces cookies error
		String result = "";
		String line = "";
		try {
			HttpPost request = new HttpPost(
					"https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/labelwithntr?access_token=24.28db137a570baaa7d9c61431a52b857e.2592000.1550386023.282335-14466713");
			
			//HttpPost request = new HttpPost(
			//		"https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/textframeinphoto?access_token=24.78800ad74b9819622cfca0571e3be149.2592000.1545188788.282335-14466713");
			
			//HttpPost request = new HttpPost(
			//		"https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/textframe?access_token=24.78800ad74b9819622cfca0571e3be149.2592000.1545188788.282335-14466713");
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			// handle response here...
			if (response != null) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				while ((line = in.readLine()) != null) {
					result += line + "\n";
				}
				in.close();
				return result;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpClient.close();
		}
		return "";
	}

	public static String getImgStr(String imgFile) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(data));
	}
	

	public static boolean generateImage(String imgStr, String imgFilePath) {
		// 图像数据为空
		if (imgStr == null)
			return false;
		try {
			// Base64解码
			byte[] b = Base64.decodeBase64(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

}
