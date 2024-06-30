package com.example.jsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    //        WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        webView = findViewById(R.id.wv);
//        jsoup();
//        getShouYeLieBiao();

//        huoQuYingShiLieBiao();


        getXiangQing();
    }


    public void getShouYeLieBiao() {
        String url;
//        if (page == 1) {
        url = "https://www.dygangs.net/";
//        } else {
//            url = "https://www.dygangs.org/dy/index_" + page + ".html";
//        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL:::", url);
                    Document document = Jsoup.connect(url).get();

//                    Log.e("vvvvvvvvvvv:::", document.outerHtml());
                    Element table = document.selectFirst("div[id=tab1_div_0]");


//                    Element table = index.get(8);


                    Elements tables1 = table.select("[valign=top]").select("tbody").select("tr").select("td").select("[width=132]");
                    Log.e("aaaaaaaaa:::", String.valueOf(tables1.size()));
                    for (int i = 0; i < tables1.size(); i++) {
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
                        Element titleElement = tables1.get(i).select("a").get(1);
                        String title = titleElement.ownText();


                        Element hrefElement = tables1.get(i).selectFirst("a");
                        String href = hrefElement.attr("href");
                        String img = hrefElement.select("img").attr("src");


                        Element timeElement = tables1.get(i).select("table").get(2).selectFirst("td");
                        String time = timeElement.ownText();
//                        Element infoElement = tables1.get(i).select("tbody").select("tr").select("[valign=top]").first();
//                        String info = infoElement.ownText();
                        Log.e("element.1111toString():" + i, String.valueOf("\n    href: " + href + " \n   img:" + img + "\n  time:" + time + "\n  title:" + title));
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
                    }


                    String href = document.select("[align=middle]").outerHtml();//.last().attr("href");

                    Log.e("vvvvvvvv:", String.valueOf(href));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public void huoQuYingShiLieBiao() {
        String url;
//        if (page == 1) {
        url = "https://www.dygangs.net/ys/index_2.htm";
//        } else {
//            url = "https://www.dygangs.org/dy/index_" + page + ".html";
//        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL:::", url);
                    Document document = Jsoup.connect(url).get();


                    Elements index = document.getElementsByTag("table");


                    Element table = index.get(8);


                    Elements tables1 = table.select("table").select("tbody").select("tr").select("[width=50%]").select("[width=388]");
                    Log.e("ggggggg:", String.valueOf(tables1.size()));
                    for (int i = 0; i < tables1.size(); i++) {
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
                        Element titleElement = tables1.get(i).select("tbody").select("tr").select("[width=250]").select("a").first();
                        String title = titleElement.ownText();
                        Element hrefElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[class=border1]").select("tbody").select("tr").select("td").select("a").first();
                        String href = hrefElement.attr("href");
                        String img = hrefElement.select("img").attr("src");
                        Element timeElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[align=center]").select("tbody").select("tr").select("td").first();
                        String time = timeElement.ownText();
                        Element infoElement = tables1.get(i).select("tbody").select("tr").select("[valign=top]").first();
                        String info = infoElement.ownText();
                        Log.e("element.1111toString():" + i, String.valueOf("\n    href: " + href + " \n   img:" + img + "\n  time:" + time + "\n  info:" + info + "\n  title:" + title));
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
                    }


                    String href = document.select("[align=middle] > a").last().attr("href");

                    Log.e("vvvvvvvv:", String.valueOf(href));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


//    public void jsoups(){
//        String url;
////        if (page == 1) {
//        url = "https://www.dygangs.net/ys/index_2.htm";
////        } else {
////            url = "https://www.dygangs.org/dy/index_" + page + ".html";
////        }
//
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    Log.e("URL:::", url);
//                    Document document = Jsoup.connect(url).get();
//
//
//                    Elements index = document.getElementsByTag("table");
//
//
//
//                    Element table = index.get(8);
//
//
//                    Elements tables1 = table.select("#table > tbody > tr > [width=50%] > [width=388]");
//                    Log.e("sssssssssss:", String.valueOf(tables1.size()));
//                    for (int i=0;i<tables1.size();i++){
//                        Log.e("","---------------------------------------------------------------------分割线---------------------------------------------------------------------");
//                        Element titleElement = tables1.get(i).select("tbody").select("tr").select("[width=250]").select("a").first();
//                        String title = titleElement.ownText();
//                        Element hrefElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[class=border1]").select("tbody").select("tr").select("td").select("a").first();
//                        String href = hrefElement.attr("href");
//                        String img = hrefElement.select("img").attr("src");
//                        Element timeElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[align=center]").select("tbody").select("tr").select("td").first();
//                        String time = timeElement.ownText();
//                        Element infoElement = tables1.get(i).select("tbody").select("tr").select("[valign=top]").first();
//                        String info = infoElement.ownText();
//                        Log.e("element.1111toString():"+i, String.valueOf("\n    href: "+href + " \n   img:"+img + "\n  time:"+time+"\n  info:"+info + "\n  title:"+title));
//                        Log.e("","---------------------------------------------------------------------分割线---------------------------------------------------------------------");
//                    }
//
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//
//    }


    public void getFenLei() {
        String url;
//        if (page == 1) {
        url = "https://www.dygangs.net/";
//        } else {
//            url = "https://www.dygangs.org/dy/index_" + page + ".html";
//        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL:::", url);
                    Document document = Jsoup.connect(url).get();


                    Elements index = document.getElementsByTag("table");


                    Element table = index.get(2);


                    Elements tables1 = table.select("tr").select("a");
                    Log.e("sssssssssss:", String.valueOf(tables1.outerHtml()));
                    for (int i = 0; i < tables1.size() - 1; i++) {
                        if (i < 2 || i == tables1.size() - 1) continue;
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
//                        Element titleElement = tables1.get(i).select("tbody").select("tr").select("[width=250]").select("a").first();
                        String title = tables1.get(i).ownText();
//                        Element hrefElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[class=border1]").select("tbody").select("tr").select("td").select("a").first();
                        String href = tables1.get(i).attr("href");
//                        String img = hrefElement.select("img").attr("src");
//                        Element timeElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[align=center]").select("tbody").select("tr").select("td").first();
//                        String time = timeElement.ownText();
//                        Element infoElement = tables1.get(i).select("tbody").select("tr").select("[valign=top]").first();
//                        String info = infoElement.ownText();
                        Log.e("element.1111toString():" + i, String.valueOf("\n    href: " + href + "\n  title:" + title));
                        Log.e("", "---------------------------------------------------------------------分割线---------------------------------------------------------------------");
                    }


                    String href = document.select("[align=middle]").outerHtml();//.last().attr("href");

                    Log.e("vvvvvvvv:", String.valueOf(href));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public void getXiangQing() {
        String url;
//        if (page == 1) {
        url = "https://www.dygangs.net/ys/20240625/54893.htm";
//        } else {
//            url = "https://www.dygangs.org/dy/index_" + page + ".html";
//        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL:::", url);
                    Document document = Jsoup.connect(url).get();


                    Elements index = document.getElementsByTag("table");


                    Element table = index.get(5);


//                    Elements tables1 = table.select("[valign=top]").select("tbody");//.get(3);//.select("[height=30]").select("tbody");

                    Elements tables1 = table.select("[valign=top]").select("[cellspacing=0]").select("[height=30]").select("table");//.select("[height=30]").select("tbody");

                    Elements tableslinks = tables1.select("[cellspacing=1]").first().select("tr").select("a");
                    String circuitName = "磁力线路";
                    Map<String, String> playMap = new LinkedHashMap<>();

                    int number = 0;
                    for (Element a : tableslinks) {
                        List<String> vodItems = new ArrayList<>();
                        String episodeUrl = a.attr("href");
                        String episodeName = a.text();
                        if (!episodeUrl.toLowerCase().startsWith("magnet")) continue;
                        vodItems.add(episodeName + "$" + episodeUrl);

                        if (vodItems.size() > 0) {
                            number++;
                            playMap.put(circuitName + number, TextUtils.join("#", vodItems));

                            Log.e("下载地址爬取地址：", circuitName + number + "  " + TextUtils.join("#", vodItems));
                        }

                    }


                    Element nameElement = table.select("[valign=top]").select("[cellspacing=0]").select("[valign=top]").select("table").get(1).selectFirst("a");
                    Log.e("aaaaaaa:", nameElement.text());
                    String name1 = nameElement.ownText();
                    Element nameElement2 = table.select("[valign=top]").select("[cellspacing=0]").select("[valign=top]").select("[width=91%]").select("tr").first().selectFirst("[width=592]");
                    Log.e("bbbbbbbb:", nameElement2.text());
                    String name2 = nameElement2.text();


                    Element xiangqing = tables1.select("p").first();
                    String pic = xiangqing.select("img").attr("src");
                    Element jianjie = tables1.select("p").get(2);

                    Log.e("cccccc:", xiangqing.outerHtml());

                    String typeName = getStrByRegex(Pattern.compile("◎类　　别　(.*?)<br>"), xiangqing.outerHtml());
//                    if (typeName.equals("")) typeName = doc.select("[rel=category tag]").text();
                    String year = getStrByRegex(Pattern.compile("◎年　　代　(.*?)<br>"), xiangqing.outerHtml());
                    if (year.equals(""))
                        year = getStrByRegex(Pattern.compile("首播:(.*?)<br>"), xiangqing.outerHtml());
                    String area = getStrByRegex(Pattern.compile("◎产　　地　(.*?)<br>"), xiangqing.outerHtml());
                    if (area.equals(""))
                        area = getStrByRegex(Pattern.compile("地区:(.*?)<br>"), xiangqing.outerHtml());
                    String remark = getStrByRegex(Pattern.compile("◎上映日期　(.*?)<br>"), xiangqing.outerHtml());

                    Log.e("主演111：：：：：：：：：：：：：：：", "开始获取");
                    String actor = getActorOrDirectors(Pattern.compile("◎主　　演　(.*?)</p>"), xiangqing.outerHtml());
                    Log.e("主演111：：：：：：：：：：：结束：：：：", actor);

                    if (actor.equals(""))
                        actor = getActorOrDirector(Pattern.compile("◎演　　员　(.*?)</p>"), xiangqing.outerHtml());
                    if (actor.equals(""))
                        actor = getActorOrDirector(Pattern.compile("主演:(.*?)<br>"), xiangqing.outerHtml());
                    String director = getActorOrDirector(Pattern.compile("◎导　　演　(.*?)<br>"), xiangqing.outerHtml());
                    if (director.equals(""))
                        director = getActorOrDirector(Pattern.compile("导演:(.*?)<br>"), xiangqing.outerHtml());
                    String description = getDescription(Pattern.compile("◎简　　介(.*?)<hr>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), jianjie.outerHtml());
                    if (description.equals(""))
                        description = getDescription(Pattern.compile("简介(.*?)</p>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), jianjie.outerHtml());
                    if (description.equals(""))
                        description = jianjie.ownText();

//
                    Log.e("sssssssssss:", String.valueOf("片*1名：" + name1 + "\n 片*2名：" + name2 + "\n 图片：" + pic + "\n类　　别:" + typeName + "\n 年　　代:" + year + "\n 产　　地:" + area + "\n 上映日期:" + remark + "\n 演　　员:" + actor + "\n 导　　演:" + director + "\n 简　　介:" + description));
//                    for (int i=0;i<tables1.size()-1;i++){
//                        Log.e("","---------------------------------------------------------------------分割线---------------------------------------------------------------------");
////                        Element titleElement = tables1.get(i).select("tbody").select("tr").select("[width=250]").select("a").first();
//                        String title = tables1.get(i).ownText();
////                        Element hrefElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[class=border1]").select("tbody").select("tr").select("td").select("a").first();
//                        String href = tables1.get(i).attr("href");
////                        String img = hrefElement.select("img").attr("src");
////                        Element timeElement = tables1.get(i).select("tbody").select("tr").select("[width=132]").select("[align=center]").select("tbody").select("tr").select("td").first();
////                        String time = timeElement.ownText();
////                        Element infoElement = tables1.get(i).select("tbody").select("tr").select("[valign=top]").first();
////                        String info = infoElement.ownText();
//                        Log.e("element.1111toString():"+i, String.valueOf("\n    href: "+href +  "\n  title:"+title));
//                        Log.e("","---------------------------------------------------------------------分割线---------------------------------------------------------------------");
//                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private String getStrByRegex(Pattern pattern, String str) {
        Log.e("hhhhh:", str);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) return matcher.group(1).trim();
        return "";
    }

    private String getActorOrDirector(Pattern pattern, String str) {
        Log.e("fffff:", str);
        return getStrByRegex(pattern, str)
                .replaceAll("<br>", "")
                .replaceAll("&nbsp;", "")
                .replaceAll("&amp;", "")
                .replaceAll("middot;", "・")
                .replaceAll("　　　　　", ",")
                .replaceAll("　　　　 　", ",")
                .replaceAll("　", "");
    }

    private String getActorOrDirectors(Pattern pattern, String str) {


        Matcher matcher = pattern.matcher(str);
        Log.e("getActorOrDirectors:", matcher.group(0));

        if (matcher.find()) {
            return matcher.group(1).trim();//.replaceAll("<br>", "")
//                    .replaceAll("&nbsp;", "")
//                    .replaceAll("&amp;", "")
//                    .replaceAll("middot;", "・")
//                    .replaceAll("　　　　　", ",")
//                    .replaceAll("　　　　 　", ",")
//                    .replaceAll("　", "");
        }
        return "";



    }

    private String getDescription(Pattern pattern, String str) {

        Log.e("rrrrr:", str);
        return getStrByRegex(pattern, str)
                .replaceAll("</?[^>]+>", "")
                .replaceAll("\n", "")
                .replaceAll("&amp;", "")
                .replaceAll("middot;", "・")
                .replaceAll("ldquo;", "【")
                .replaceAll("rdquo;", "】")
                .replaceAll("　", "");
    }


}