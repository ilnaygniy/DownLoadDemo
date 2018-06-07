package downloaddemo.liyanying.example.com.download.xmlutils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import downloaddemo.liyanying.example.com.download.listners.ParserXMLListener;

public class XmlPull {

    private LinkedList<Map> linkedList = new LinkedList<>();
    private Map finalMap = null;
    private String containsStr = "recordinfo,roominfo,docsall,shapeIndex,playBackInfo";

    private ParserXMLListener parserXMLListener;
    private String savePath;
    private String readPath;
    private String roomNum;

    private List pageIdList;
    private List flvList;
    private boolean intoplayBackInfo=false;
    /**
     * 把服务器传递过的XML流数据解析成对象
     * //     * @param inputStream  XML流
     *
     * @return
     */
    private void parserXML()
            throws XmlPullParserException, IOException {
        if(readPath==null||savePath==null){
            return;
        }
        String path=readPath;
        String encode="UTF-8";
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //获得一个XMLPULL工厂类的实例
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //获得一个XML解析器的实例
        XmlPullParser parser = factory.newPullParser();
        //设置解析器的输入，使用inputStream流式数据。
        parser.setInput(inputStream, encode);
        //判断当前的事件类型
        int eventType = parser.getEventType();
        //循环读取，知道事件类型为文档结束
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // 通过判断事件类型来选择执行不同的代码
                case XmlPullParser.START_DOCUMENT:
                    //文档开始的时候，实例化list对象，用于存放XML解析后的数据
                    if(parserXMLListener!=null){
                        parserXMLListener.statrParser();
                    }

                    break;
                case XmlPullParser.START_TAG:
                    //读取标签的时候触发这个事件
                    if (parser.getName().equals("NULL") || parser.getName().equals("null")) {
                        break;
                    }
                    String name = parser.getAttributeValue(null, "name");
                    if(name.equals("playBackInfo")){
                        intoplayBackInfo=true;
                    }
                    if (name.equals("")) {
                        break;
                    }
                    if (parser.getName().equals("MAP") || parser.getName().equals("map")) {
                        Map map = new HashMap();
                        if (!name.trim().equals("")) {
                            map.put("mapMame", name);
                        } else {
                            map.put("mapMame", "firstName");
                        }
                        linkedList.addLast(map);
                    } else {
                        String value = parser.nextText();
                        if(intoplayBackInfo){
                            if(name.equals("pageId")){
                                pageIdList.add(value);
                            }
                        }
                        if(value.contains("flv")){
                            flvList.add(value);
                        }
                        linkedList.peekLast().put(name, value);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("MAP")) {
                        int linkListSize =linkedList.size();
                        if(linkListSize==0){
                            if(parserXMLListener!=null){
                                parserXMLListener.endParser();
                            }
                            break;
                        }
                        String mapName = (String) linkedList.peekLast().get("mapMame");
                        if(mapName.equals("playBackInfo")){
                            intoplayBackInfo=false;
                        }
                        if (linkListSize> 1) {
                            linkedList.get((linkedList.size() - 2)).put(mapName, linkedList.pollLast());
                        } else if (linkListSize == 1) {
                            if (mapName.equals("attaches") || mapName.equals("scrollBarInfo")) {
                                linkedList.removeLast();
                                break;
                            } else if (containsStr.contains(mapName)) {
                                linkedList.peekLast().remove("mapMame");
                                finalMap.put(mapName, linkedList.pollLast());
                                if (finalMap.size() == 5) {
                                    fileSaveObject(finalMap, roomNum+"_roominfo");
                                }
                            } else {
                                linkedList.peekLast().remove("mapMame");
                                fileSaveObject(linkedList.pollLast(), mapName);
                            }
                            linkedList.clear();
                        }
                    }
                    break;
                default:
                    break;
            }
            //读取
            eventType = parser.next();
        }
    }

    public List getFlvList() {
        return flvList;
    }

    public List getPageIdList() {
        return pageIdList;
    }

    public static ThreadPoolExecutor cachedThreadPool;

    public XmlPull(String readPath,String savePath,String roomNum) {
        finalMap = new HashMap();
        pageIdList=new ArrayList();
        flvList=new ArrayList();
        this.savePath=savePath;
        this.readPath=readPath;
        this.roomNum=roomNum;
        if(cachedThreadPool==null){
            cachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        }

    }

    public static int XMLPULLPARSERERROR=-1;
    public static int XMLPULLIOERROR=-2;
    public void parserXMLThread(ParserXMLListener listener){
        parserXMLListener=listener;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    parserXML();
                } catch (XmlPullParserException e) {
                    if(parserXMLListener!=null){
                        parserXMLListener.Error(XMLPULLPARSERERROR);
                    }

//                    e.printStackTrace();
                } catch (IOException e) {
                    if(parserXMLListener!=null){
                        parserXMLListener.Error(XMLPULLIOERROR);
                    }
//                    e.printStackTrace();
                }
            }
        });

    }
    //保存obj到本地
    public void fileSaveObject(final Object obj,final String objName) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                    File sdFile = new File(savePath, objName);
                    FileOutputStream fos = null;
                    ObjectOutputStream oos = null;
                    try {
                        fos = new FileOutputStream(sdFile);
                        oos = new ObjectOutputStream(fos);
                        oos.writeObject(obj); //写入
                        oos.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                            if (oos != null) {
                                oos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
