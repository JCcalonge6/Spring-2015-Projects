package comjccalonge6.httpsgithub.jccalongescardpricer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by JCcalonge on 5/12/2015.
 */
public class GetPrice {
    public String getInternetData(String cardToPrice) throws Exception{
        BufferedReader in = null;
        List<String> words = new ArrayList<String>();

        String cardName = cardToPrice;
        String urlBase = "http://store.tcgplayer.com/cardfight-vanguard/product/show?ProductName=";
        cardName = cardName.replaceAll(" ","+").replaceAll(",","%2c");
        String urlFinal = urlBase+cardName;
        String cardPriceFinal = "";
        Stack<String> low = new Stack<String>();
        Stack<String> median = new Stack<String>();

        words.add("Low:");
        words.add("Median:");
        words.add("Number - Rarity :");
        try{
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(urlFinal);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String newLine = System.getProperty("line.separator");

            //test target URL
            // sb.append(urlFinal + newLine + newLine);

            while ((line = in.readLine()) != null) {
                boolean found = false;

                if(line.contains("Low:")) {
                    found = true;
                    line = line.replaceAll(".*\\\">","").replaceAll("\\</.*","");
                    low.push(line);
                }
                if(line.contains("Median:")) {
                    found = true;
                    line = line.replaceAll(".*\\\">","").replaceAll("\\</.*","");
                    median.push(line);
                }
                else if(line.contains("Number - Rarity")) {
                    found = true;
                    line = line.replaceAll("^ +","");
                    line = line.replace("Number - Rarity :", "Pack - Rarity:");
                    sb.append(line + newLine);
                    if (!low.empty()) {
                        line = low.pop();
                        sb.append(line + newLine);
                    }
                    if (!median.empty()) {
                        line = median.pop();
                        sb.append(line + newLine + newLine);
                    }
                }
                else if(line.contains("Your search")) {
                    found = true;
                    line = "Your search did not match any products.";
                    sb.append(line + newLine);
                    break;
                }
                else if(line.contains("out of stock")) {
                    found = true;
                    line = line.replaceAll(".*\\<p>","").replaceAll("\\</.*","");
                    sb.append(line + newLine + newLine);
                }

                if(found) continue;

            }
            in.close();

            cardPriceFinal = sb.toString();
            return cardPriceFinal;

        }finally {
            if (in != null) {
                try{
                    in.close();
                    return cardPriceFinal;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
