//package downloaddemo.liyanying.example.com.download.xmlutils;
//
//import android.content.Context;
//import android.content.res.XmlResourceParser;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.xml.sax.Attributes;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.InputSource;
//import org.xml.sax.Locator;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXNotRecognizedException;
//import org.xml.sax.SAXNotSupportedException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.DefaultHandler;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.ObjectOutputStream;
//import java.io.StringReader;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.PriorityBlockingQueue;
//import java.util.concurrent.SynchronousQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.regex.Pattern;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//
//public class TestXmlSAX {
//
//    int hasFile = 0;
//    private LinkedList<Map> linkedList = new LinkedList<>();
//    private String currentTag = null;
//    private String currentValue = null;
//    private Map zzzzmap = new HashMap();
//
//    private int hierarchy = 0;
//    private Map bigMap = new HashMap();
//
//    private String path_saveMap = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1" + File.separator + "sina" + File.separator;
//
//    private String roomNum = "";
//
//
//    /**
//     * 将parser作为线程变量。
//     */
//    private ThreadLocal<SAXParser> parser = new ThreadLocal<SAXParser>() {
//        @Override
//        protected SAXParser initialValue() {
//            SAXParser newParser = null;
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            factory.setNamespaceAware(true);
//            try {
//                newParser = factory.newSAXParser();
//            } catch (ParserConfigurationException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            }
//            return newParser;
//        }
//
//        ;
//    };
//
//    private String containsStr = "recordinfo,roominfo,docsall,shapeIndex,playBackInfo";
//
//    private ThreadPoolExecutor cachedThreadPool;
//
//    public TestXmlSAX() {
//        cachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
//    }
//
//    public void getXmls(String path, final Context context) {
//
//        FileInputStream fileIS = null;
//        try {
//            fileIS = new FileInputStream(path);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        //获取解析器
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        try {
//            SAXParser parser = factory.newSAXParser();
//            parser.parse(fileIS, new DefaultHandler() {
//                @Override
//                public void startDocument() throws SAXException {
//                    super.startDocument();
//                    Log.d("test_sequence", "   ---> "+"startDocument");
//                    if (runTimeListener != null) {
//                        runTimeListener.runStart();
//                    }
//                }
//
//                @Override
//                public void endDocument() throws SAXException {
//                    super.endDocument();
//                    Log.d("test_sequence", "   ---> "+"endDocument");
//                    if (runTimeListener != null) {
//                        runTimeListener.runEnd();
//                    }
//                }
//
//                @Override
//                public void characters(char[] ch, int start, int length) throws SAXException {
//                    super.characters(ch, start, length);
//
//                    Log.d("test_sequence", "   ---> "+"characters"+"  new String   "+new String(ch, start, length));
//                    Map map2 = linkedList.peekLast();
//                    if (map2 != null && currentTag != null) {
//                        currentValue = new String(ch, start, length);
//                        map2.put(currentTag, currentValue);
//                    }
//                    currentTag = null;
//                    currentValue = null;
//
//                }
//
//                @Override
//                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//                    super.startElement(uri, localName, qName, attributes);
//                    Log.d("test_sequence", "   ---> "+"startElement"+"   localName "+localName);
//                    if (qName.equals("MAP") || qName.equals("map")) {
//                        String attributesName = attributes.getValue("name");
//                        Map map = new HashMap();
//                        if (!attributesName.trim().equals("")) {
//                            map.put("mapMame", attributesName);
//                        } else {
//                            map.put("mapMame", "firstName");
//                        }
//                        linkedList.addLast(map);
//
//                    } else {
//                        currentTag = attributes.getValue("name");
//                    }
//                }
//
//                @Override
//                public void endElement(String uri, String localName, String qName) throws SAXException {
//                    super.endElement(uri, localName, qName);
//                    Log.d("test_sequence", "   ---> "+"endElement"+"   localName "+localName);
//                    if (qName.equals("MAP")) {
//                        if (linkedList.size() > 1) {
//                            String mapName = (String) linkedList.peekLast().get("mapMame");
//                            linkedList.peekLast().remove("mapMame");
//                            linkedList.get((linkedList.size() - 2)).put(mapName, linkedList.peekLast());
//                            linkedList.removeLast();
//                        } else {
//                            linkedList.peekLast().remove("mapMame");
//                            zzzzmap = linkedList.pollLast();
//                            linkedList.clear();
//                        }
//                    }
//                }
//            });
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void getXml(String path) throws IOException {
//
//
//        File myfile = new File(path);
//
//        if (myfile.exists()) {
//            hasFile = 1;
//        }
//
//        FileInputStream fileIS = null;
//        try {
//            fileIS = new FileInputStream(path);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        StringBuffer sb = new StringBuffer();
//
//        BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
//
//        String readString = new String();
//
//        //just reading each line and pass it on the debugger
//
//        while ((readString = buf.readLine()) != null) {
//
//            sb.append(readString);
//
//        }
//
//        //创建一个SAX的解析器
//
//        SAXParserFactory parsefac = SAXParserFactory.newInstance();
//
//        XMLReader reader = null;
//        try {
//            reader = parsefac.newSAXParser().getXMLReader();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//
//
//        //为XMLreader创建解析容器
//
//        reader.setContentHandler(new ContentHandler() {
//            @Override
//            public void setDocumentLocator(Locator locator) {
//            }
//
//            @Override
//            public void startDocument() throws SAXException {
//            }
//
//            @Override
//            public void endDocument() throws SAXException {
//            }
//
//            @Override
//            public void startPrefixMapping(String prefix, String uri) throws SAXException {
//            }
//
//            @Override
//            public void endPrefixMapping(String prefix) throws SAXException {
//
//            }
//
//            @Override
//            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
//
////              if(qName.equals("MAP")){
////                  Map map=new HashMap();
////                  if(!atts.getValue("name").trim().equals("")){
////                      map.put("mapMame", atts.getValue("name"));
////                  }else {
////                      map.put("mapMame", "firstName");
////                  }
////
////                  linkedList.addLast(map);
////              }else {
////
////                  currentTag=atts.getValue("name");
////              }
//
//            }
//
//            @Override
//            public void endElement(String uri, String localName, String qName) throws SAXException {
////              if(qName.equals("MAP")){
////                  if(linkedList.size()>1){
////                      linkedList.get((linkedList.size()-2)).put(linkedList.peekLast().get("mapMame"), linkedList.peekLast());
////                      linkedList.peekLast().remove("mapMame");
////                      linkedList.removeLast();
////                  }else {
////                      int asd=linkedList.size();
////                      linkedList.peekLast().remove("mapMame");
////                      zzzzmap=linkedList.pollLast();
////
////                     String str="结束";
////
////                  }
////              }
//            }
//
//            @Override
//            public void characters(char[] ch, int start, int length) throws SAXException {
////              Map map2= linkedList.peekLast();
////              if(map2!=null&&currentTag!=null){
////                  currentValue=new String(ch,start,length);
////                  map2.put(currentTag, currentValue);
////              }
////              currentTag=null;
////              currentValue=null;
//            }
//
//            @Override
//            public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
//
//            }
//
//            @Override
//            public void processingInstruction(String target, String data) throws SAXException {
//
//            }
//
//            @Override
//            public void skippedEntity(String name) throws SAXException {
//
//            }
//        });//在这就要复写Handler类方法了.在各个事件中写自己想要实现的效果我的类叫MyContentHandler
//
//        //开始解析文件
//
//        String s1 = sb.toString();
//
//        try {
//            reader.parse(new InputSource(new StringReader(s1)));
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //保存obj到本地
//    public void fileSaveObject(final Object obj, final String path, final String objName) {
//        cachedThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//
//                    File sdFile = new File(path, objName);
//                    FileOutputStream fos = null;
//                    ObjectOutputStream oos = null;
//                    try {
//                        fos = new FileOutputStream(sdFile);
//                        oos = new ObjectOutputStream(fos);
//                        oos.writeObject(obj); //写入
//                        oos.flush();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            if (fos != null) {
//                                fos.close();
//                            }
//                            if (oos != null) {
//                                oos.close();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//        });
//
//    }
//
//
//    private RunTimeListener runTimeListener;
//
//    public void setRunTimeListener(RunTimeListener runTimeListener) {
//        this.runTimeListener = runTimeListener;
//    }
//
//    public interface RunTimeListener {
//        void runStart();
//
//        void runEnd();
//    }
//
//    /**
//     * 判断字符串是否只包含数字
//     *
//     * @param str
//     * @return
//     */
//    public static boolean isNumeric(String str) {
//
//        Pattern pattern = Pattern.compile("[0-9]*");
//        return pattern.matcher(str).matches() && (str.length() == 8);
//    }
//
//}
