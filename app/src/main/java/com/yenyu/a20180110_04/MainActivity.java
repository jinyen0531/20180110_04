package com.yenyu.a20180110_04;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends AppCompatActivity {
    ListView lv;
    MyAdapter adapter;
    MyHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv= (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it= new Intent(MainActivity.this,WebActivity.class);
                it.putExtra("link",dataHandler.newsItems.get(i).link);
                //放內容進intent,名稱為link,內容從MyHandler的link陣列抓取 它的第i個
                startActivity(it);
            }
        });

    }

    @Override //新增Menu到介面上
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) //選擇itemID
        {
            case R.id.menu_reload: //id為R.id.menu_reload這個項目
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String str_url = "https://www.mobile01.com/rss/news.xml";
                    URL url = null;

                    try {
                        url = new URL(str_url); //初始化URL
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //利用網址抓取連結
                        conn.setRequestMethod("GET"); //使用什麼方法做連線
                        conn.connect();
                        InputStream inputStream = conn.getInputStream();
                        InputStreamReader isr = new InputStreamReader(inputStream);
                        BufferedReader br = new BufferedReader(isr);

                        StringBuilder sb = new StringBuilder();
                        //為了使用readline 所以需要StringBuilder把東西包進string裡面
                        String str;
                        while ((str = br.readLine()) != null) //當讀取時，不為空值時新增進stringbuilder
                        {
                            sb.append(str);
                        }
                        String str1 = sb.toString(); //將stringbuilder轉成String str1
                        Log.d("NET", str1);

                        dataHandler = new MyHandler(); //新增MyHandler class 為dataHandler
                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        SAXParser sp = spf.newSAXParser();
                        XMLReader xr = sp.getXMLReader();
                        xr.setContentHandler(dataHandler);
                        xr.parse(new InputSource(new StringReader(str1)));

                        br.close();
                        isr.close();
                        inputStream.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter = new MyAdapter(MainActivity.this,
                                        dataHandler.newsItems);
                                lv.setAdapter(adapter);
                            }
                        });

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }


                }
            }.start();
            break;
        }
        return super.onOptionsItemSelected(item);

    }

}
