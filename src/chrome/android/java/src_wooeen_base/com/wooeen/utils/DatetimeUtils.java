package com.wooeen.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public abstract class DatetimeUtils {
	
	public static Date getTimeSaoPaulo(){
		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR_OF_DAY, -3);
		return date.getTime();
	}
	
	public static Calendar getCalendarSaoPaulo(){
		Calendar date = Calendar.getInstance();
		date.add(Calendar.HOUR_OF_DAY, -3);
		return date;
	}
	
	/**
     * Converte um java.util.date em java.sql.time
     * @param hora A hora a ser convertida como java.util.date
     * @return a hora em java.sql.time
     */
    public static java.sql.Time timeToSQL(Date hora) {
    	if(hora != null)
    		return new java.sql.Time(hora.getTime());
    	else return null;
 	}

	/**
     * Converte um java.util.date em java.sql.date
     * @param data A data a ser convertida como java.util.date
     * @return a data em java.sql.date
     */
    public static java.sql.Date dateToSQL(Date data) {
    	if(data != null)
    		return new java.sql.Date(data.getTime());
    	else return null;
 	}
    
	/**
     * Converte um java.util.date em java.sql.timestamp
     * @param data A data/hora a ser convertida como java.util.date
     * @return a data/hora em java.sql.timestamp
     */
    public static java.sql.Timestamp datetimeToSQL(Date data) {
    	if(data != null)
    		return new java.sql.Timestamp(data.getTime());
    	else return null;
 	}
    
    /**
     * Converte um java.util.date em String yyyy-MM-dd
     * @param data A data a ser convertida como java.util.date
     * @return  yyyy-MM-dd
     */
    public static String dateToString(Date data) {
    	if(data != null){
    		SimpleDateFormat conversor = new SimpleDateFormat( "yyyy-MM-dd" , new Locale("pt", "BR"));
    		return conversor.format(data);
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String no formato @param formato
     * @param data a data em String
     * @param formato o formato que vai ser convertido
     * @return
     */
    public static String dateToString(Date data,String formato) {
    	if(data != null){
    		SimpleDateFormat conversor = new SimpleDateFormat( formato , new Locale("pt", "BR"));
    		return conversor.format(data);
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String E, EEEE, dd 'de' MMMM 'de' yyyy
     * @param data A data a ser convertida como java.util.date
     * @return a data em String E, EEEE, dd 'de' MMMM 'de' yyyy
     */
    public static String dateToStringLong(Date data) {
    	if(data != null){
    		SimpleDateFormat conversor = new SimpleDateFormat( "EEEE, dd 'de' MMMM 'de' yyyy" , new Locale("pt", "BR"));
    		return conversor.format(data);
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String EEE dd/MM
     * @param data A data a ser convertida como java.util.date
     * @return a data em String EEE dd/MM
     */
    public static String dateToStringNoYear(Date data) {
    	if(data != null){
    		SimpleDateFormat conversor = new SimpleDateFormat( "EEE dd/MM" , new Locale("pt", "BR") );
    		return conversor.format(data);
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String yyyy-MM-dd
     * @param data A data a ser convertida como java.util.date
     * @return  yyyy-MM-dd
     */
    public static String datetimeToString(Date data) {
    	if(data != null){
    		SimpleDateFormat conversor = new SimpleDateFormat( "yyyy-MM-dd HH:mm" , new Locale("pt", "BR"));
    		return conversor.format(data);
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String HH:mm
     * @param data A data a ser convertida como java.util.date
     * @return a data em String HH:mm
     */
    public static String timeToString(Date hora) {
    	if(hora != null){
    		SimpleDateFormat df = new SimpleDateFormat("HH:mm" , new Locale("pt", "BR"));
    		return df.format(hora.getTime());
    	}else return null;
 	}
    
    /**
     * Converte um java.util.date em String HH:mm
     * @param data A data a ser convertida como java.util.date
     * @return a data em String HH:mm
     */
    public static String timeToStringLong(Date hora) {
    	if(hora != null){
    		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS" , new Locale("pt", "BR"));
    		return df.format(hora.getTime());
    	}else return null;
 	}
    
    /**
     * Converte uma String HH:mm em java.util.Date
     * @param data A hora a ser convertida como string
     * @return a data em java.util.Date
     */
 	public static Date stringToTime(String dataString){
 		return stringToTime(dataString,"HH:mm");
 	}
 	
 	public static Date stringToDatetime(String dataString){
 		return stringToTime(dataString,"yyyy-MM-dd HH:mm");
 	}
 	
 	/**
     * Converte uma String em java.util.Date
     * @param data A hora a ser convertida como string
     * @return a data em java.util.Date
     * @param o formato do objeto data
     */
 	public static Date stringToTime(String dataString, String formato){
 		Date date = null;
 		
 		if(dataString != null && !dataString.replaceAll("\\/", "").trim().isEmpty()){
 			try {
 				SimpleDateFormat dateFormat = new SimpleDateFormat(formato, Locale.ENGLISH);
 				dateFormat.setLenient(false);
 				date = dateFormat.parse(dataString);
 			} catch (ParseException e) {
 				date = null;
			}
 		}
 		
 		return date;
 	}
 	
 	/**
 	 * Conversao dos formatos CSV de sync para datetime
 	 */
 	public static java.sql.Timestamp csvDatetimeToSQL(String dataString){
 		return datetimeToSQL(DatetimeUtils.csvStringToDate(dataString,"yyyy-MM-dd HH:mm:ss"));
 	}
 	
 	/**
 	 * Conversao dos formatos CSV de sync para date
 	 */
 	public static java.sql.Date csvDateToSQL(String dataString){
 		return dateToSQL(DatetimeUtils.csvStringToDate(dataString,"yyyy-MM-dd"));
 	}
 	
 	/**
 	 * Conversao dos formatos CSV de sync para time
 	 */
 	public static java.sql.Time csvTimeToSQL(String dataString){
 		return timeToSQL(DatetimeUtils.stringToTime(dataString,"HH:mm:ss"));
 	}
 	
 	public static Date csvStringToDate(String dataString, String formato){
 		Date date = null;
 		
 		if(dataString != null && !dataString.replaceAll("\\/", "").trim().isEmpty()){
 			try {
 				SimpleDateFormat dateFormat = new SimpleDateFormat(formato, Locale.ENGLISH);
 				dateFormat.setLenient(false);
 				date = dateFormat.parse(dataString);
 			} catch (ParseException e) {
 				date = null;
			}
 		}
 		
 		return date;
 	}
 	
 	/**
     * Converte uma String yyyy-MM-dd em java.util.Date
     * @param data A data a ser convertida como string
     * @return a data em java.util.Date
     */
 	public static Date stringToDate(String dataString){
 		return stringToDate(dataString,"yyyy-MM-dd");
 	}
 	
 	/**
     * Converte uma String yyyy-MM-dd em java.util.Date
     * @param data A data a ser convertida como string
     * @return a data em java.util.Date
     */
 	public static Date stringToDateUniversal(String dataString){
 		return stringToDate(dataString,"yyyy-MM-dd");
 	}
 	
 	/**
     * Converte uma String em java.util.Date
     * @param data A data a ser convertida como string
     * @return a data em java.util.Date
     * @param o formato do objeto data
     */
 	public static Date stringToDate(String dataString, String formato){
 		Date data = null;
 		
 		if(dataString != null && !dataString.replaceAll("\\/", "").trim().isEmpty()){
 			try {
 				SimpleDateFormat dateFormat = new SimpleDateFormat(formato, Locale.ENGLISH);
 				dateFormat.setLenient(false);
 				data = dateFormat.parse(dataString);
 			} catch (ParseException e) {
				data = null;
			}
 		}
 		
 		return data;
 	}
 	
 	/**
 	 * Compara as horas HH:mm de dois Calendars
 	 * Se a hora 1 for menor que hora 2 -> Retorna -1
 	 * Se a hora 1 for igual a hora 2 -> Retorna 0
 	 * Se a hora 1 for maior que hora 2 -> Retorna 1
 	 * Se a hora 1 ou a hora 2 forem invalidos -> Retorna 2
 	 * @param hora1
 	 * @param hora2
 	 * @return Um valor de diferença entre os dois Calendars
 	 */
 	public static int compareHoras(Calendar calendar1,Calendar calendar2){
 		if(calendar1 == null || calendar2 == null)
 			return 2;
 		
 		int horaCalendar1 = calendar1.get(Calendar.HOUR_OF_DAY);
 		int minutoCalendar1 = calendar1.get(Calendar.MINUTE);
 		int horaCalendar2 = calendar2.get(Calendar.HOUR_OF_DAY);
 		int minutoCalendar2 = calendar2.get(Calendar.MINUTE);
 		
 		int retorno = 0;
 		
 		if(horaCalendar1 < horaCalendar2)
 			retorno = -1;
 		else if(horaCalendar1 > horaCalendar2)
 			retorno = 1;
 		else if(minutoCalendar1 < minutoCalendar2)
 			retorno = -1;
 		else if(minutoCalendar1 > minutoCalendar2)
 			retorno = 1;
 		else
 			retorno = 0;
 		
 		return retorno;
 	}
 	
 	public static int compareHoras(Date hora1,Date hora2){
 		Calendar calendar1 = Calendar.getInstance();
 		if(hora1 != null){
 			calendar1.setTime(hora1);
 		}else{
 			calendar1.set(Calendar.HOUR_OF_DAY, 0);
 			calendar1.set(Calendar.MINUTE, 0);
 		}
 		
 		Calendar calendar2 = Calendar.getInstance();
 		if(hora2 != null){
 			calendar2.setTime(hora2);
 		}else{
 			calendar2.set(Calendar.HOUR_OF_DAY, 0);
 			calendar2.set(Calendar.MINUTE, 0);
 		}
 		
 		return compareHoras(calendar1, calendar2);
 	}
 	
 	/**
 	 * Compara as datas DD/MM/YYYY de dois Calendars
 	 * Se a data 1 for menor que a data 2 -> Retorna -1
 	 * Se a data 1 for igual a data 2 -> Retorna 0
 	 * Se a data 1 for maior que a data 2 -> Retorna 1
 	 * Se a data 1 ou a data 2 forem invalidos -> Retorna 2
 	 * @param hora1
 	 * @param hora2
 	 * @return Um valor de diferença entre os dois Calendars
 	 */
 	public static int compareDatas(Calendar calendar1,Calendar calendar2,boolean consideraAno){
 		if(calendar1 == null || calendar2 == null)
 			return 2;
 		
 		int anoCalendar1 = calendar1.get(Calendar.YEAR);
 		int mesCalendar1 = calendar1.get(Calendar.MONTH);
 		int diaCalendar1 = calendar1.get(Calendar.DAY_OF_YEAR);
 		int anoCalendar2 = calendar2.get(Calendar.YEAR);
 		int mesCalendar2 = calendar2.get(Calendar.MONTH);
 		int diaCalendar2 = calendar2.get(Calendar.DAY_OF_YEAR);
 		
 		int retorno = 0;
 		
 		if(anoCalendar1 < anoCalendar2 && consideraAno)
 			retorno = -1;
 		else if(anoCalendar1 > anoCalendar2 && consideraAno)
 			retorno = 1;
 		else if(mesCalendar1 < mesCalendar2)
 			retorno = -1;
 		else if(mesCalendar1 > mesCalendar2)
 			retorno = 1;
 		else if(diaCalendar1 < diaCalendar2)
 			retorno = -1;
 		else if(diaCalendar1 > diaCalendar2)
 			retorno = 1;
 		else
 			retorno = 0;
 		
 		return retorno;
 	}
 	
 	public static int compareDatas(Date date1, Date date2,boolean consideraAno){
 		Calendar calendar1 = Calendar.getInstance();
 		if(date1 != null){
 			calendar1.setTime(date1);
 		}else{
 			calendar1.set(Calendar.YEAR, 0);
 			calendar1.set(Calendar.MONTH, 0);
 			calendar1.set(Calendar.DAY_OF_YEAR, 0);
 		}
 		
 		Calendar calendar2 = Calendar.getInstance();
 		if(date2 != null){
 			calendar2.setTime(date2);
 		}else{
 			calendar2.set(Calendar.YEAR, 0);
 			calendar2.set(Calendar.MONTH, 0);
 			calendar2.set(Calendar.DAY_OF_YEAR, 0);
 		}
 		
 		return compareDatas(calendar1, calendar2,consideraAno);
 	}
 	
 	public static int compareDatas(Calendar calendar1,Calendar calendar2){
 		return compareDatas(calendar1, calendar2,true);
 	}
 	
 	public static int compareDatas(Date date1, Date date2){
 		return compareDatas(date1, date2,true);
 	}
 	
 	
 	/** 
 	  * Método para comparar as das e retornar o numero de dias de diferença entre elas 
 	  * 
 	  * Compare two date and return the difference between them in days. 
 	  * 
 	  * @param dataLow The lowest date 
 	  * @param dataHigh The highest date 
 	  * 
 	  * @return int 
 	  */  
 	public static int dataDiff(Date dataLow, Date dataHigh){
 	  
 	     GregorianCalendar startTime = new GregorianCalendar();  
 	     GregorianCalendar endTime = new GregorianCalendar();  
 	       
 	     GregorianCalendar curTime = new GregorianCalendar();  
 	     GregorianCalendar baseTime = new GregorianCalendar();  
 	  
 	     startTime.setTime(dataLow);  
 	     endTime.setTime(dataHigh);  
 	       
 	     int dif_multiplier = 1;  
 	       
 	     // Verifica a ordem de inicio das datas  
 	     if( dataLow.compareTo( dataHigh ) < 0 ){  
 	         baseTime.setTime(dataHigh);  
 	         curTime.setTime(dataLow);  
 	         dif_multiplier = 1;  
 	     }else{  
 	         baseTime.setTime(dataLow);  
 	         curTime.setTime(dataHigh);  
 	         dif_multiplier = -1;  
 	     }  
 	       
 	     int result_years = 0;  
 	     int result_months = 0;  
 	     int result_days = 0;  
 	  
 	     // Para cada mes e ano, vai de mes em mes pegar o ultimo dia para import acumulando  
 	     // no total de dias. Ja leva em consideracao ano bissesto  
 	     while( curTime.get(GregorianCalendar.YEAR) < baseTime.get(GregorianCalendar.YEAR) ||  
 	            curTime.get(GregorianCalendar.MONTH) < baseTime.get(GregorianCalendar.MONTH)  )  
 	     {  
 	           
 	         int max_day = curTime.getActualMaximum( GregorianCalendar.DAY_OF_MONTH );  
 	         result_months += max_day;  
 	         curTime.add(GregorianCalendar.MONTH, 1);  
 	           
 	     }  
 	       
 	     // Marca que é um saldo negativo ou positivo  
 	     result_months = result_months*dif_multiplier;  
 	       
 	       
 	     // Retirna a diferenca de dias do total dos meses  
 	     result_days += (endTime.get(GregorianCalendar.DAY_OF_MONTH) - startTime.get(GregorianCalendar.DAY_OF_MONTH));  
 	       
 	     return result_years+result_months+result_days;  
 	}
 	
 	public static Date getRangeDateFirst(String range) {
 		if(range == null || "".equals(range))
 			return null;
 		
 		if(range.indexOf("-") < 0)
 			return stringToDate(range);
 		
 		return stringToDate(range.substring(0, range.indexOf("-")));
 	}
 	
 	public static Date getRangeDateLast(String range) {
 		if(range == null || "".equals(range) || range.indexOf("-") < 0 || range.indexOf("-") >= range.length() - 1)
 			return null;
 		
 		return stringToDate(range.substring(range.indexOf("-") + 1));
 	}
 	
 	public static Date addDate(Date date,int days) {
 		Calendar calendar = Calendar.getInstance();
 		calendar.setTime(date);
 		calendar.add(Calendar.DAY_OF_YEAR, days);
 		return calendar.getTime();
 	}
 	
 	public static Date stringToDateCustom(String value,String format) {
 		if(value == null || "".equals(value))
 			return null;
 		
 		try {
			return new SimpleDateFormat(format).parse(value);
		} catch (ParseException e) {
			return null;
		}
 	}
 	
 	public static Date stringToDateCake(String value) {
 		if(value == null || "".equals(value))
 			return null;
 		
 		Date date = stringToDateCustom(value, "yyyy-MM-dd'T'HH:mm:ss.SSS");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd'T'HH:mm:ss");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd'T'HH:mm");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd");
 		if(date != null)
 			return date;
 		
 		return null;
 	}
 	
 	public static Date stringToDateNetAffiliation(String value) {
 		if(value == null || "".equals(value))
 			return null;
 		
 		Date date = stringToDateCustom(value, "yyyy-MM-dd HH:mm:ss");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd HH:mm:ss");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd HH:mm");
 		if(date != null)
 			return date;
 		
 		date = stringToDateCustom(value, "yyyy-MM-dd");
 		if(date != null)
 			return date;
 		
 		return null;
 	}
}
