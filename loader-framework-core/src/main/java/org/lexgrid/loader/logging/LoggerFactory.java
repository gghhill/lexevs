/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.lexgrid.loader.logging;


/**
 * A factory for creating Logger objects.
 */
public class LoggerFactory {
	
	/** The load logger name. */
	private static String loadLoggerName = "LB_LOAD_LOGGER";

	/**
	 * Gets the load logger.
	 * 
	 * @return the load logger
	 */
	
	/*
	public static Logger getLoadLogger(){
		//Make sure the ResourceManager has been initialized.
		ResourceManager.instance();
		Logger loadLogger = LogManager.getLogger(loadLoggerName);
		return loadLogger;
	}
	*/
}
