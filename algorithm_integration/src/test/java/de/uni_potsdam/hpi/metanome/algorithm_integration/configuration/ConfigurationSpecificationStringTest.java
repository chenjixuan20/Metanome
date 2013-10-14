package de.uni_potsdam.hpi.metanome.algorithm_integration.configuration;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_potsdam.hpi.metanome.algorithm_integration.configuration.ConfigurationSpecificationString;

/**
 * Tests for {@link ConfigurationSpecificationString}
 */
public class ConfigurationSpecificationStringTest {

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	/**
	 * The identifier should be set in the constructor and be retrievable through getIdentifier.
	 */
	@Test
	public void testGetIdentifier() {
		// Setup
		// Expected values
		String expectedIdentifier = "parameter1";
		ConfigurationSpecificationString configSpec = new ConfigurationSpecificationString(expectedIdentifier);
		
		// Execute functionality
		String actualIdentifier = configSpec.getIdentifier();
		
		// Check result
		assertEquals(expectedIdentifier, actualIdentifier);
	}
}
