/*
 * Copyright 2014 by the Metanome project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.metanome.algorithm_integration.configuration;

import de.metanome.test_helper.GwtSerializationTester;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ConfigurationSpecificationBoolean}
 */
public class ConfigurationSpecificationBooleanTest {

  /**
   * Test method for {@link ConfigurationSpecificationBoolean#ConfigurationSpecificationBoolean(String)}
   * <p/> The identifier should be set in the constructor and be retrievable through getIdentifier.
   * The numberOfValues should be set to 1.
   */
  @Test
  public void testConstructorGetOne() {
    // Setup
    // Expected values
    String expectedIdentifier = "parameter1";
    int expectedNumberOfValues = 1;

    // Execute functionality
    ConfigurationSpecificationBoolean
        configSpec =
        new ConfigurationSpecificationBoolean(expectedIdentifier);
    String actualIdentifier = configSpec.getIdentifier();
    int actualNumberOfValues = configSpec.getNumberOfValues();

    // Check result
    assertEquals(expectedIdentifier, actualIdentifier);
    assertEquals(expectedNumberOfValues, actualNumberOfValues);
  }

  /**
   * Test method for {@link ConfigurationSpecificationBoolean#ConfigurationSpecificationBoolean(String,
   * int)} <p/> The identifier should be set in the constructor and be retrievable through
   * getIdentifier. The numberOfValues should be set to 2.
   */
  @Test
  public void testConstructorGetTwo() {
    // Setup
    // Expected values
    String expectedIdentifier = "parameter1";
    int expectedNumberOfValues = 2;

    // Execute functionality
    ConfigurationSpecificationBoolean
        configSpec =
        new ConfigurationSpecificationBoolean(expectedIdentifier, expectedNumberOfValues);
    String actualIdentifier = configSpec.getIdentifier();
    int actualNumberOfValues = configSpec.getNumberOfValues();

    // Check result
    assertEquals(expectedIdentifier, actualIdentifier);
    assertEquals(expectedNumberOfValues, actualNumberOfValues);
  }

  /**
   * Test method for {@link de.metanome.algorithm_integration.configuration.ConfigurationSpecificationBoolean#setSettings(ConfigurationSettingBoolean...)}
   *
   * The values should be correctly settable on the specification.
   */
  @Test
  public void testSetValues() {
    // Setup
    ConfigurationSpecificationBoolean
        configSpec =
        new ConfigurationSpecificationBoolean("parameter1", 2);
    // Expected values
    ConfigurationSettingBoolean expectedValue0 = mock(ConfigurationSettingBoolean.class);
    ConfigurationSettingBoolean expectedValue1 = mock(ConfigurationSettingBoolean.class);

    // Execute functionality
    configSpec.setSettings(expectedValue0, expectedValue1);

    // Check result
    assertEquals(expectedValue0, configSpec.settings[0]);
    assertEquals(expectedValue1, configSpec.settings[1]);
  }

  /**
   * Tests that the instances of {@link de.metanome.algorithm_integration.configuration.ConfigurationSpecificationBoolean}
   * are serializable in GWT.
   */
  @Test
  public void testGwtSerialization() {
    GwtSerializationTester
        .checkGwtSerializability(new ConfigurationSpecificationBoolean("some identifier", 3));
  }
}