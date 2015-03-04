package nz.ac.auckland.cer.common.util;

import java.util.Map;

import org.apache.log4j.Logger;

public class TemplateUtil {

    private Logger log = Logger.getLogger(TemplateUtil.class.getName());

	public String replace(String s, Map<String, String> templateParams) {
		String tmp = s;
        if (templateParams != null && tmp != null) {
            for (String key : templateParams.keySet()) {
                String value = templateParams.get(key);
                if (value == null) {
                    log.warn("Value of replacement parameter is null: " +  key);
                    value = "N/A";
                }
                tmp = tmp.replace(key, value);
            }
        }
        return tmp;
	}
}
