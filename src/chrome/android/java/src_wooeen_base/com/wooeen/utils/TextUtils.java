package com.wooeen.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
	
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
	
	public static String quoteQuotationMarks(String value){
		if(value == null)
			return null;
		
		return value.replaceAll("([^,])[\"]([^,])", "$1\\\\\"$2");
	}
	
	public static String noNumbers(String str_cnpj){
		if(isEmpty(str_cnpj))
			return "";
		
    	str_cnpj=str_cnpj.replaceAll("[^a-zA-Z]","");
        
        return str_cnpj;
    }
	
	public static String noSpecialChars(String str_cnpj){
		if(isEmpty(str_cnpj))
			return "";
		
    	str_cnpj=str_cnpj.replaceAll("[^a-zA-Z0-9]","");
        
        return str_cnpj;
    }
	
	public static String justNumbers(String numbers){
    	numbers=numbers.replaceAll("[^0-9]","");
        
        return numbers;
    }
	
	public static String firstUppercase(String str) {
		if(str == null || "".equals(str))
			return str;
		
		if(str.length() == 1)
			return str.toUpperCase();
		
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
	
	public static String firstWord(String str) {
		if(str == null || !str.contains(" "))
			return str;
		
		return str.substring(0, str.indexOf(" "));
	}
	
	public static boolean isUrlValid(String url) {
		if(isEmpty(url))
			return false;
		
		Pattern p = Pattern.compile(URL_REGEX);
		Matcher m = p.matcher(url);//replace with string to compare
		return m.find();
	}
	
	public static boolean allowLimitChars(String value,int limit) {
		return value != null && value.length() <= limit;
	}
	
	public static boolean isEmpty(String value){
		return value == null || "".equals(value) || "null".equals(value);
	}
	
	public static boolean isEmpty(int value){
		return value <= 0;
	}
	
	public static boolean isEmailValid(String email){
		if(isEmpty(email))
			return false;
		
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher= pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Substitui os caracteres [èéêë] por [e], [ûù] por [u],[ïî] por [i],[àâ] por [a],[Ô] por [o]
	 * @param string O texto com acentos
	 * @return Um novo texto sem acentos
	 */
	public static String noAccent(String string){
		String s = string;
		
		s = s.replaceAll("[èéêëẽ]","e");
	    s = s.replaceAll("[ùúûũ]","u");
	    s = s.replaceAll("[ìíîĩï]","i");
	    s = s.replaceAll("[àáâã]","a");
	    s = s.replaceAll("[òóôõ]","o");
	    s = s.replaceAll("[ç]", "c");
	    
	    s = s.replaceAll("[ÈÉÊËẼ]","E");
	    s = s.replaceAll("[ÙÚÛŨ]","U");
	    s = s.replaceAll("[ÌÍÎĨÏ]","I");
	    s = s.replaceAll("[ÀÁÂÃ]","A");
	    s = s.replaceAll("[ÒÓÔÕ]","O");
	    s = s.replaceAll("[Ç]", "C");
	    
	    return s;
	}
	
	public static String prepareToQuery(String q) {
		q = q.replaceAll(" ",",");
		q = noAccent(q);
		q = q.replaceAll("[^a-zA-Z0-9,]","");
		return q;
	}
	
	public static String prepareWordsToQuery(String q) {
		if(isEmpty(q))
			return "";
		
		q = noAccent(q);
		return q;
	}
	
	public static String prepareTextToSms(String string){
		if(string == null)
			return "";
		
		String s = string;
		
		s = noAccent(s);
		s = s.replaceAll("[^a-zA-Z0-9\\.\\,\\!\\?\\-\\s\\:\\/]","");
	    
	    return s;
	}
	
	public static String prepareTextToNickname(String string){
		if(string == null)
			return "";
		
		String s = string;
		
		s = noAccent(s);
		s = s.toLowerCase();
		s = s.replaceAll(" ", "-");
		s = s.replaceAll("\\.", "-");
		s = s.replaceAll("\\%", "");
//		s = s.replaceAll("[^a-zA-Z0-9\\-]","");
	    
	    return s;
	}
	
	public static String prepareTextToNicknameCategory(String string){
		if(string == null)
			return "";
		
		String s = string;
		
		s = noAccent(s);
		s = s.toLowerCase();
		s = s.replaceAll(" > ", "/");
		s = s.replaceAll(">", "/");
		s = s.replaceAll(" ", "-");
		s = s.replaceAll("\\.", "-");
//		s = s.replaceAll("[^a-zA-Z0-9\\-\\/]","");
	    
	    return s;
	}
	
	/**
	 * 
	 * @param string1 A String que sera pesquisado se contem a String 2
	 * @param string2 A String que sera usada na pesquisa da String 1
	 * @return
	 */
	public static boolean contains(String string1,String string2){
		if(string1 == null || string2 == null)
			return false;
		
		String novaString1 = string1.toLowerCase().trim();
		String novaString2 = string2.toLowerCase().trim();

		novaString1 = noAccent(novaString1);
		novaString2 = noAccent(novaString2);
		
		novaString1 = noSpecialChars(novaString1);
		novaString2 = noSpecialChars(novaString2);
		
		if(novaString1.contains(novaString2))
			return true;
		
		return false;
	}
	
	public static boolean equals(String string1,String string2){
		if(string1 == null || string2 == null)
			return false;
		
		String novaString1 = string1.toLowerCase().trim();
		String novaString2 = string2.toLowerCase().trim();

		novaString1 = noAccent(novaString1);
		novaString2 = noAccent(novaString2);
		
		novaString1 = noSpecialChars(novaString1);
		novaString2 = noSpecialChars(novaString2);
		
		if(novaString1.equals(novaString2))
			return true;
		
		return false;
	}
	
	public static boolean startsWith(String string1,String string2){
		if(string1 == null || string2 == null)
			return false;
		
		String novaString1 = string1.toLowerCase().trim();
		String novaString2 = string2.toLowerCase().trim();

		novaString1 = noAccent(novaString1);
		novaString2 = noAccent(novaString2);
		
		novaString1 = noSpecialChars(novaString1);
		novaString2 = noSpecialChars(novaString2);
		
		if(novaString1.startsWith(novaString2))
			return true;
		
		return false;
	}
	
	public static boolean endsWith(String string1,String string2){
		if(string1 == null || string2 == null)
			return false;
		
		String novaString1 = string1.toLowerCase().trim();
		String novaString2 = string2.toLowerCase().trim();

		novaString1 = noAccent(novaString1);
		novaString2 = noAccent(novaString2);
		
		novaString1 = noSpecialChars(novaString1);
		novaString2 = noSpecialChars(novaString2);
		
		if(novaString1.endsWith(novaString2))
			return true;
		
		return false;
	}
	
	public static int getQuantidadeChar(String str,char search){
		if(str == null)
			return 0;
					
		int charCount = 0;
		char temp;

		for( int i = 0; i < str.length(); i++ ){
		    temp = str.charAt(i);

		    if(temp == search)
		        charCount++;
		}
		
		return charCount;
	}
	
	public static int indexQuantidadeChar(String str,char search,int qtd){
		if(str == null)
			return 0;
					
		int charCount = 0;
		int charFound = 0;
		char temp;

		for( int i = 0; i < str.length(); i++ ){
		    temp = str.charAt(i);

		    if(temp == search)
		        charFound++;
		    
		    if(qtd <= charFound)
		    	return charCount;
		    
		    charCount++;
		}
		
		return -1;
	}
	
	public static String getLimit(String str,int limit){
		if(str == null)
			return "";
		
		if(str.length() < limit)
			return str;
		
		return str.substring(0, limit);
	}
	
	public static List<String> getValues(String string){
		if(isEmpty(string) || "null".equals(string))
			return new ArrayList<String>();
		
		String[] values = string.split(",");
		return Arrays.asList(values);
	}
	
	public static String[] getValuesArray(String string){
		if(isEmpty(string) || "null".equals(string))
			return new String[0];
		
		return string.split(",");
	}
	
	public static String[] getValuesArray(String string,String split){
		if(isEmpty(string) || "null".equals(string))
			return new String[0];
		
		return string.split(split);
	}
	
	public static List<Integer> getValuesIntArray(String string){
		String[] values = getValuesArray(string);
		if(values == null || values.length <=0)
			return new ArrayList<Integer>();
		
		List<Integer> valuesInt = new ArrayList<Integer>();
		for(String value:values) {
			int number = NumberUtils.getInteger(value);
			if(number > 0)
				valuesInt.add(number);
		}
		return valuesInt;
	}
	
	public static List<String> getValuesStringArray(String[] string){
		if(string == null || string.length <= 0)
			return null;
					
		return new ArrayList<String>(Arrays.asList(string));
	}
	
	public static List<Integer> getValuesIntArray(String[] string){
		List<String> valuesString = getValuesStringArray(string);
		if(valuesString == null)
			return null;
					
		List<Integer> valuesInt = new ArrayList<Integer>();
		for(String value:valuesString) {
			int number = NumberUtils.getInteger(value);
			if(number > 0)
				valuesInt.add(number);
		}
		return valuesInt;
	}
	
	public static String getValuesTags(List<String> lista){
		if(lista == null || lista.isEmpty())
			return "";
		
		String string = "";
		for(String value:lista) {
			if(!"".equals(string))
				string += " ";
			
			string += "#"+value;
		}
		string = "" + string + "";
		return string;
	}
	
	public static String concat(String string1,String string2){
		if(string1 == null)
			string1 = "";
		
		if(string2 == null)
			string2 = "";
		
		return string1+string2;
	}
	
	public static String concat(String string1,String string2,String string3){
		if(string1 == null)
			string1 = "";
		
		if(string2 == null)
			string2 = "";
		
		if(string3 == null)
			string3 = "";
		
		return string1+string2+string3;
	}
	
	public static String getTags(String[] values) {
		if(values == null || values.length <= 0)
			return null;
		
		String tags = "";
		for(String tag:values) {
			if(!"".equals(tags))
				tags += ",";
			tags += tag;
		}
		return tags;
	}

	private static final String[] artigos = new String[] {
			"o", "os", "a", "as", "um", "uns", "uma", "umas",
			"ao", "aos", "à", "às",
			"do", "dos",
			"no", "nos", "na", "nas", "num", "nuns", "numa", "numas",
			"pelo", "pelos", "pela", "pelas"
	};
	public static boolean isWord(String value) {
		if(value == null)
			return false;
		
		if(value.length() <= 2)
			return false;
		
		for(String artigo:artigos) {
			if(artigo.equals(value))
				return false;
		}
		
		return true;
	}
}
