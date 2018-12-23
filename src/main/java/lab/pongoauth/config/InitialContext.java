package lab.pongoauth.config;

import java.util.HashMap;
import java.util.Map;

public class InitialContext {

    private Map<String, String> values = new HashMap<String, String>();

    public String getValue(final String key){
        if (values.containsKey(key)) {
            return values.get(key);
        } else {
            if (System.getenv(key) != null){
                values.put(key, System.getenv(key));
            }
            return values.get(key);
        }
    }
}