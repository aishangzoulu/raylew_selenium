package cn.edu.muc.ilab.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Raymond on 2017/4/1.
 */
public class ShanBay {
    private static final Logger logger = Logger.getLogger("ShanBay");
    private static WebDriver driver;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","C:/Program Files (x86)/Google/Chrome/Application/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://www.shanbay.com/web/account/login/");
        boolean result;
        try {
            result = loginShanBay();
            Thread.sleep(3*1000);
        } catch(Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
        }
        if (!result) {
            System.exit(1);
        }
        //得到未打卡的成员
        String members = getMembers();
        //关闭
        driver.close();
        //写入文件
        writeToFile(members);
    }

    private static boolean loginShanBay(){
        List<WebElement> inputs= driver.findElements(By.tagName("input"));
        inputs.get(0).sendKeys("E1225");
        inputs.get(1).sendKeys("12wsbql25");
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        buttons.get(0).click();
        return true;
    }

    private static String getMembers(){
        driver.get("https://www.shanbay.com/team/members/#p1");
        List<WebElement> elements = driver.findElements(By.tagName("a").className("endless_page_link"));
        int totalPage=Integer.parseInt(elements.get(elements.size()-2).getText());
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=1;i<=totalPage;i++){
            driver.get("https://www.shanbay.com/team/members/?page="+i+"#p1");
            List<WebElement> members = driver.findElements(By.tagName("tr").className("member"));
            for(WebElement member:members){
                if(member.getText().contains("未打卡")){
                    WebElement nickNameElement = member.findElement(By.tagName("a").className("nickname"));
                    String nickName=nickNameElement.getText();
                    if(!nickName.contains("^$")) {
                        stringBuilder.append("@" + nickName+" ");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    private static void writeToFile(String members){
        Date date=new Date();
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);
        File file=new File("D:/englishshop/check/"+dateStr+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(PrintWriter out = new PrintWriter(file)){
            out.println( members );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
