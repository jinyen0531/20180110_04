package com.yenyu.a20180110_04;

import android.content.Intent;
import android.util.EventLogTags;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Student on 2018/1/10.
 */

public class MyHandler extends DefaultHandler {
    boolean isItem=false;
    boolean isTitle=false;
    boolean isLink=false;
    boolean isDescription=false;


    StringBuilder linkSB = new StringBuilder();
    StringBuilder descSB = new StringBuilder();

    public ArrayList<Mobile01NewsItem> newsItems = new ArrayList<>();
    Mobile01NewsItem item; //建一個item物件 ，一則新聞一個物件

    @Override //Element 是 <>
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        switch(qName) //java7 以後 switch可以抓字串
        {
            case "title":
                isTitle = true;
                break;
            case "item":
                isItem=true;
                item=new Mobile01NewsItem();
                break; //在<item>後 給予Mobile01NewsItem的物件
            case "link":
                isLink=true;
                break;
            case "description":
                isDescription=true;
                descSB=new StringBuilder();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        switch(qName)
        {
            case "title":
                isTitle = false;
                break;
            case "item":
                isItem=false;
                newsItems.add(item);
                break; //在</item> 結束後將剖析到的字元丟進 newsItem的陣列
            case "link":
                isLink=false;
                if(isItem)
                {
                    item.link=linkSB.toString();
                    //根據mobile01的rss 為了避免誤抓第一個link，item外的link不抓
                    linkSB = new StringBuilder();
                    //在關閉</link>時，把中間的string裝進builder裡面
                }
                break;
            case "description":
                isDescription=false;
                if(isItem)
                {
                    String str = descSB.toString();


                    Pattern pattern = Pattern.compile("http.*jpg");
                    Matcher m = pattern.matcher(str);
                    String imgurl ="";
                    if(m.find())
                    {
                        imgurl=m.group(0);
                    }
                    str=str.replaceAll("<img.*/>","");
                    //正規表示法  .=全部字元符號 *前一個字元一次或更多次
                    item.description = str;
                    item.imgurl=imgurl;

                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(isTitle && isItem) //如果isTitle為true 抓取文字內容(遇到endElement為False 則關閉抓取)
        {
            Log.d("NET",new String(ch,start,length));
            item.title= new String(ch,start,length);
        }
        if(isLink && isItem) //抓網址
        {
            linkSB.append(new String(ch,start,length));
            //因為link會分解成三行 所以用StringBuilder裝在一起
        }
        if(isDescription && isItem) //抓描述
        {
            descSB.append(new String(ch,start,length));
            //為了解決BUG 所以在description也建一個StringBuilder
        }
    }
}
