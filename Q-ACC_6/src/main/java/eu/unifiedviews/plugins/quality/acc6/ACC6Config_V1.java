package eu.unifiedviews.plugins.quality.acc6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for ACC6.
 *
 * @author Vincenzo Cutrona
 */
public class ACC6Config_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> regularExpression = new ArrayList<>();
    private Map<String, String> filters = new HashMap<>();

    public ACC6Config_V1() {
        filters.put("Postal code", "[0-9][0-9][0-9][0-9][0-9]");
        filters.put("Email address", "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        filters.put("DateTime", "^(?ni:(?=\\d)((?'year'((1[6-9])|([2-9]\\d))\\d\\d)(?'sep'[/.-])(?'month'0?[1-9]|1[012])\\2(?'day'((?<!(\\2((0?[2469])|11)\\2))31)|(?<!\\2(0?2)\\2)(29|30)|((?<=((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|(16|[2468][048]|[3579][26])00)\\2\\3\\2)29)|((0?[1-9])|(1\\d)|(2[0-8])))(?:(?=\\x20\\d)\\x20|$))?((?<time>((0?[1-9]|1[012])(:[0-5]\\d){0,2}(\\x20[AP]M))|([01]\\d|2[0-3])(:[0-5]\\d){1,2}))?)$");
        filters.put("German postal code", "\\b((?:0[1-46-9]\\d{3})|(?:[1-357-9]\\d{4})|(?:[4][0-24-9]\\d{3})|(?:[6][013-9]\\d{3}))\\b");
        filters.put("Telephone number (10 digit)", "^(\\([2-9]|[2-9])(\\d{2}|\\d{2}\\))(-|.|\\s)?\\d{3}(-|.|\\s)?\\d{4}$");
        filters.put("Italian mobile phone number", "^([+]39)?((38[{8,9}|0])|(34[{7-9}|0])|(36[6|8|0])|(33[{3-9}|0])|(32[{8,9}]))([\\d]{7})$");
        filters.put("Currency", "^(?!\\u00a2)");
    }

    public ArrayList<String> getSubject() { return subject; }

    public void setSubject(ArrayList<String> subject) { this.subject = subject; }

    public ArrayList<String> getProperty() {
        return property;
    }

    public void setProperty(ArrayList<String> property) {
        this.property = property;
    }

    public ArrayList<String> getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(ArrayList<String> regularExpression) {
        this.regularExpression = regularExpression;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

}
