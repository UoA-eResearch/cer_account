package nz.ac.auckland.cer.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TemplateUtilTest {
	
	private TemplateUtil tu = new TemplateUtil();
	
	@Test
	public void testNull() throws Exception {
		
		String s = null;
		Map<String,String> m = null;
		assertNull(tu.replace(s, m));	
		m = new HashMap<String,String>();
		assertNull(tu.replace(s, m));
		m.put("some_key", "some_value");
		assertNull(tu.replace(s, m));
	}

	@Test
	public void testEmptyString() throws Exception {
		
		String s = "";
		Map<String,String> m = null;
		assertEquals("", tu.replace(s, m));	
		m = new HashMap<String,String>();
		assertEquals("", tu.replace(s, m));	
		m.put("some_key", "some_value");
		assertEquals("", tu.replace(s, m));	
	}

	@Test
	public void testNoReplacement() throws Exception {
		
		String s = "test string";
		Map<String,String> m = null;
		assertEquals(s, tu.replace(s, m));	
		m = new HashMap<String,String>();
		assertEquals(s, tu.replace(s, m));	
		m.put("some_key", "some_value");
		assertEquals(s, tu.replace(s, m));	
	}

	@Test
	public void testNotAvailable() throws Exception {
		
		String s = "test __PARAM__ string";
		Map<String,String> m = new HashMap<String,String>();
		m.put("__PARAM__", null);
		assertEquals("test N/A string", tu.replace(s, m));	
	}

	@Test
	public void testSimpleReplacement() throws Exception {
		
		String s = "test ${PARAM} string";
		Map<String,String> m = new HashMap<String,String>();
		m.put("${PARAM}", "1");
		assertEquals("test 1 string", tu.replace(s, m));	
	}

	@Test
	public void testMultipleReplacements1() throws Exception {
		
		String s = "${PARAM} test${PARAM}string ${PARAM}";
		Map<String,String> m = new HashMap<String,String>();
		m.put("${PARAM}", "1");
		assertEquals("1 test1string 1", tu.replace(s, m));	
	}

	@Test
	public void testMultipleReplacements2() throws Exception {
		
		String s = "${PARAM1} test string ${PARAM2}";
		Map<String,String> m = new HashMap<String,String>();
		m.put("${PARAM1}", "1");
		m.put("${PARAM2}", "2");
		assertEquals("1 test string 2", tu.replace(s, m));	
	}


}
