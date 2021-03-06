package de.geops.geoserver.documentor.directive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.geotools.util.logging.Logging;

/**
 * Implementation of the domain-specific description directives used to
 * reference entities to include in the documentation
 * 
 * Example:
 *    [@documentor include the-whole-wide-world] 
 *    
 *    
 * Directives:
 * 
 *   * ignore
 *     ignore the current entity
 *   
 *   * include-ref [reference]
 *     Also load the documentation for the referenced entity
 *     
 *   * ignore-ref [reference]
 *     Ignore the reference to the specified entity
 *
 *     
 * References:
 * 
 * The syntax to reference tables is
 * 
 *     table:[table schema].[table name]
 *
 * Quoted identifiers are allowed.
 * 
 * @author nico
 *
 */
public class DirectiveParser {

	//private static final Logger LOGGER = Logging.getLogger(DirectiveParser.class);

	public static String markerName = "documentor";
	
	final private String input;
	final private HashMap<String, ArrayList<Directive>> directives;
	
	private static final String DIRECTIVE_IGNORE = "ignore"; 
	private static final String DIRECTIVE_IGNORE_REF = "ignore-ref"; 
	private static final String DIRECTIVE_INCLUDE_REF = "include-ref"; 
	
	
	// static private Pattern directivePattern = Pattern.compile("\\[\\s*@documentor\\s+([^\\]]+)\\]");
	static private Pattern directivePattern = Pattern.compile("\\[\\s*@"+DirectiveParser.markerName+"\\s+(.+?)\\]");

	public DirectiveParser(String input) {
		this.input = input;
		this.directives = loadDirectives();
	}

	/**
	 * return the input string with all directives being removed
	 * 
	 * @return
	 */
	public String getClearedInput() {
		if (this.input == null) {
			return null;
		}
		return this.input.replaceAll(directivePattern.pattern(), "");
	}

	/**
	 * 
	 * @param directiveName
	 * @return
	 */
	private Set<String> getDirectiveArguments(String directiveName) {
		HashSet<String> results = new HashSet<String>();
		if (this.directives.containsKey(directiveName)) {
			for (Directive directive: this.directives.get(directiveName)) {
				String value = directive.getArgument();
				if (value != null && !value.equals("")) {
					results.add(value);
				}
			}
		}
		return results;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Directive> getDirectives() {
		ArrayList<Directive> directivesList = new ArrayList<Directive>();
		for (ArrayList<Directive> dList: directives.values()) {
			for (Directive d : dList) {
				directivesList.add(d);
			}
		}
		return directivesList;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getIgnoreReferences() {
		return getDirectiveArguments(DIRECTIVE_IGNORE_REF);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getIncludeReferences() {
		return getDirectiveArguments(DIRECTIVE_INCLUDE_REF);
	}
	
	/**
	 * returns true if the current entity should be ignored
	 * 
	 * @return
	 */
	public boolean ignoreThisEntity() {
		return this.directives.containsKey(DIRECTIVE_IGNORE);
	}
	
	/**
	 * 
	 * @return
	 */
	private HashMap<String, ArrayList<Directive>> loadDirectives() {
		HashMap<String, ArrayList<Directive>> directives = new HashMap<String, ArrayList<Directive>>();
		if (this.input == null) {
			return directives;
		}
		
		Matcher matcher = DirectiveParser.directivePattern.matcher(this.input);
		while (matcher.find()) {
			String fullDirectiveText = matcher.group(1);
			int spacePos = fullDirectiveText.indexOf(' ');
			Directive directive = new Directive();
			if (spacePos != -1) {
				directive.setName(fullDirectiveText.substring(0, spacePos));
				if (fullDirectiveText.length()>(spacePos+1)) {
					directive.setArgument(fullDirectiveText.substring(spacePos+1, fullDirectiveText.length()).trim());
				}
			} else {
				directive.setName(fullDirectiveText.trim());
			}
			
			if (!directives.containsKey(directive.getName())) {
				directives.put(directive.getName(), new ArrayList<Directive>());
			}
			directives.get(directive.getName()).add(directive);
		}
		return directives;
	}
}
