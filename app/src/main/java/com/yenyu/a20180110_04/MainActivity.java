package com.yenyu.a20180110_04;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View v) //點擊讀取網路log.d資料
    {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String str_url="http://rate.bot.com.tw/xrt?Lang=zh-TW";
                URL url= null;

                try {
                    url=new URL(str_url); //初始化URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //利用網址抓取連結
                    conn.setRequestMethod("GET"); //使用什麼方法做連線
                    conn.connect();
                    InputStream inputStream= conn.getInputStream();
                    InputStreamReader isr  = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(isr);

                    StringBuilder sb=new StringBuilder();
                    //為了使用readline 所以需要StringBuilder把東西包進string裡面
                    String str ;
                    while((str = br.readLine()) != null) //當讀取時，不為空值時新增進stringbuilder
                    {
                        sb.append(str);
                    }
                    String str1= sb.toString(); //將stringbuilder轉成String str1
                    Log.d("NET",str1);
                    int index1= str1.indexOf("日圓(JPY)"); //從str1抓出欲讀取的字串的序列位置
                    int index2= str1.indexOf("本行現金賣出", index1);
                    int index3= str1.indexOf("0.266",index2);
                    Log.d("NET","index1:"+index1 +"index2:"+index2+"index3:"+index3);
                    String data1 = str1.substring(index2+56,index2+61);//從str1裡面抓取
                    Log.d("NET",data1);
                    br.close();
                    isr.close();
                    inputStream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch(ProtocolException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();

    }
}
