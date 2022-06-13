package com.wooeen.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberUtils {

	public static final NumberFormat NUMERO = new DecimalFormat("##0.00; -##0.00",DecimalFormatSymbols.getInstance(Locale.US));  
    public static final NumberFormat REAL = new DecimalFormat("'$' ##0.00;'$' -##0.00",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat REAL_INTEIRO = new DecimalFormat("'$' ##0;'$' -##0",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat MONETARY = new DecimalFormat("#,##0.00;-#,##0.00",new DecimalFormatSymbols (Locale.US));
    public static final NumberFormat PORCENTAGEM_PRINT = new DecimalFormat("###.##'%';-###.##'%'",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat PORCENTAGEM = new DecimalFormat("##0.00'%';-##0.00'%'",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat INTEIRO = new DecimalFormat("##0;-##0",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat INTEIRO_MASK = new DecimalFormat("#,##0;-#,##0",DecimalFormatSymbols.getInstance(Locale.US));
    public static final NumberFormat INTEIRO_NO_MASK = new DecimalFormat("###0;-###0",DecimalFormatSymbols.getInstance(Locale.US));
    
    public static ArrayList<Integer> getArrayInteger(String values) {
    	if(values == null)
    		return new ArrayList<Integer>();
    	
    	String[] valuesBy = values.split(",");
    	ArrayList<Integer> integers = new ArrayList<Integer>();
    	
    	for(int x=0;x < valuesBy.length;x++) {
    		integers.add(getInteger(valuesBy[x]));
    	}
    	
    	return integers;
    }
    
    public static double getRealDouble(double valor){
    	BigDecimal a = new BigDecimal(valor);
    	BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    	return roundOff.doubleValue();
    }
    
    public static String getRealCentavos(double valor) {
    	String valueS = doubleToStringUniversal(valor);
    	return removeNotNumbers(valueS);
    }
    
    public static String doubleToString(double valor,int casasDecimais){
    	String mask = "#,##0";
    	
    	if(casasDecimais > 0)
    		mask += ".";
    	
    	while(casasDecimais > 0){
    		mask += "0";
    		casasDecimais--;
    	}
    	
    	mask = mask + "; -"+mask;
    	
    	return new DecimalFormat(mask).format(valor);
    }
    
    public static String doubleToString(double valor){
    	return doubleToString(valor,2);
    }
    public static String doubleToStringUniversal(double valor){
    	DecimalFormat df = new DecimalFormat("#.00",DecimalFormatSymbols.getInstance(Locale.US));
    	return df.format(valor);
    }
    
    public static int doubleToInt(double valor) {
    	Double valorD = getRealDouble(valor);
    	return valorD.intValue();
    }
    
    public static String realToInt(double valor) {
    	Double valorD = getRealDouble(valor);
    	return ""+valorD.intValue();
    }
    
    public static String realToDecimals(double valor) {
    	String real = realToString(valor);
    	return real.substring(real.length() - 2 , real.length());
    }
    
    public static String realToString(double valor){
    	valor = getRealDouble(valor);
    	
    	return REAL.format(valor);
    }
    
    public static String monetaryToString(double valor){
    	valor = getRealDouble(valor);
    	
    	return MONETARY.format(valor);
    }
    
    public static String percentToString(double valor){
    	return PORCENTAGEM.format(valor);
    }
    
    public static String percentPrintToString(double valor){
    	return PORCENTAGEM_PRINT.format(valor);
    }
    
    public static String integerToString(double valor){
    	return INTEIRO.format(valor);
    }
    
    public static String integerToStringMask(double valor){
    	return INTEIRO_MASK.format(valor);
    }
    
    public static String integerToStringNoMask(double valor){
    	return INTEIRO_NO_MASK.format(valor);
    }
    
    public static String getValorPagSeguro(double valor){
    	DecimalFormat df = new DecimalFormat("#.00",DecimalFormatSymbols.getInstance(Locale.US));
    	return df.format(valor);
    }
    
    public static double getDouble(String valor){
    	if(valor == null || "".equals(valor))
    		return 0;
    	
    	boolean negativo = valor.contains("-");
    	
    	String value = "";
    	if(valor.contains(",") && 
				(valor.lastIndexOf(",") >= valor.length() - 3)) {
			value = valor.replaceAll("[^0-9/,]", "");
			value = value.replaceAll(",", ".");
		}else {
			value = valor.replaceAll("[^0-9/.]", "");
		}
    	
		try {
//			Number number = Double.parseDouble(valor);
//			double numero = number.doubleValue();
			
			double numero = Double.parseDouble(value);
			
			if(negativo)
				numero = numero * (-1);
			
			return numero;
		} catch (Exception e) {
			return 0;
		}
    }
    
    public static double getDoubleUniversal(String valor){
    	if(valor == null || "".equals(valor))
    		return 0;
    	
    	boolean negativo = valor.contains("-");
    	String value = valor.replaceAll("[^0-9/.]", "");

		try {
//			Number number = Double.parseDouble(valor);
//			double numero = number.doubleValue();
			
			double numero = Double.parseDouble(value);
			
			if(negativo)
				numero = numero * (-1);
			
			return numero;
		} catch (Exception e) {
			return 0;
		}
    }
    
    public static String getCurrency(String valor) {
    	if(valor == null || "".equals(valor) || valor.length() < 3)
    		return null;
    	
    	String justLetters = valor.replaceAll("[^A-Za-z]", "");
    	if(justLetters == null || "".equals(justLetters) || justLetters.length() != 3)
    		return null;
    	
    	return justLetters.toUpperCase();
    }
    
    public static int getInteger(String valor){
    	if(valor == null)
    		return 0;
    	
    	boolean negativo = valor.contains("-");
    	String value = valor.replaceAll("[^0-9/,/.]", "");
    	
		try {
			Number number = INTEIRO.parse(value);
			int numero = number.intValue();
			
			if(negativo)
				numero = numero * (-1);
			
			return numero;
		} catch (Exception e) {
			return 0;
		}
    }
    
    public static double intToDouble(int number) {
    	String value = "";
    	if(number < 10)
    		value = "0.0"+number;
    	else if(number < 100)
    		value = "0."+number;
    	else {
    		value = ""+number;
    		value = value.substring(0, value.length() - 2)+"."+value.substring(value.length() -2);
    	}
    	
    	BigDecimal a = new BigDecimal(value);
    	BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    	return roundOff.doubleValue();
    }
    
    public static String removeNotNumbers(String valor){
    	if(valor == null)
    		return "";
    	
    	return valor.replaceAll("[^0-9]", "");
    }
    
    public static boolean par(int i) {
	    return i % 2 == 0;
	}
    
    public static boolean impar(int i) {
	    return !par(i);
	}
    
    public static String getMaximum99(int value){
    	if(value > 99)
    		return "+99";
    	else if(value < 0)
    		return "";
    	else
    		return ""+value;
    }
    
    private static final NavigableMap<Long, String> suffixes = new TreeMap<> ();
    static {
      suffixes.put(1_000L, "k");
      suffixes.put(1_000_000L, "M");
      suffixes.put(1_000_000_000L, "G");
      suffixes.put(1_000_000_000_000L, "T");
      suffixes.put(1_000_000_000_000_000L, "P");
      suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String printBigNumbers(long value) {
      //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
      if (value == Long.MIN_VALUE) return printBigNumbers(Long.MIN_VALUE + 1);
      if (value < 0) return "-" + printBigNumbers(-value);
      if (value < 1000) return Long.toString(value); //deal with easy case

      Entry<Long, String> e = suffixes.floorEntry(value);
      Long divideBy = e.getKey();
      String suffix = e.getValue();

      long truncated = value / (divideBy / 10); //the number part of the output times 10
      boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
      return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
    
    public static String realToString(String currency,String country,String lg,double valor) {
    	if(lg != null && lg.length() > 2)
    		lg = lg.substring(0,2);
    	
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(lg,country));
    	
		if(!TextUtils.isEmpty(currency))
    		format.setCurrency(Currency.getInstance(currency));
		
    	return format.format(valor);
    }
    
    public static String currencySymbol(String currency,String country,String lg) {
    	if(lg != null && lg.length() > 2)
    		lg = lg.substring(0,2);
    	
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(lg,country));
    	
		if(!TextUtils.isEmpty(currency))
    		format.setCurrency(Currency.getInstance(currency));
		
    	return format.getCurrency().getSymbol();
    }
}
