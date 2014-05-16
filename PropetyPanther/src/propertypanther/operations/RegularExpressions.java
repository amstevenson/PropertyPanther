package propertypanther.operations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to hold all of the regular expressions used in this application
 * 
 * @author Adam Stevenson
 *
 */
public class RegularExpressions {

	/**
	 *  A method to return whether a string contains numbers or symbols.
	 *  We need a "-" for the location method found in MainActivity. Also, 
	 *  we can use this in the signup forms. 
	 *  
	 *  @return True or false, dependent on whether the string meets
	 *  the regular expressions or not. 
	 *  
	 */
	public boolean contSymOrNum(String s){
		
		// Determine if numbers are used
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher matcher = pattern.matcher(s);
	    
		// if it does not match, there must by symbols
		// or numbers. 
		// Lets keep the "-" symbol however, and accept
		// a string with just that symbol, to pass through the check.
		if (!matcher.matches()) {
			
			// accept "-"'s - this may change. Delete if so.
			// Please note of course, that spaces are acceptable
			pattern = Pattern.compile("(([a-zA-Z]+[-| ])+[a-zA-Z]+)");
			matcher = pattern.matcher(s);
			
			// if we have found any "-" symbol(s), or not, permit.
			if(!matcher.matches()) return true;
			
			else return false;
		}
		else return false;
	}
	
	/**
	 * Email address validation. 
	 * Luckily android has a build in
	 * pattern for this that we can use.
	 * 
	 * @return True or false, dependent on whether the string meets
	 * the regular expressions or not. 
	 */
	public boolean emailValidation(String s){
		
		if (s == null) return false;
	    else return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
	}
	
	
}
