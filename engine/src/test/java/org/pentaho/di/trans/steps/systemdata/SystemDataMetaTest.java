/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.steps.systemdata;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * User: Dzmitry Stsiapanau Date: 1/20/14 Time: 3:04 PM
 */
public class SystemDataMetaTest implements InitializerInterface<StepMetaInterface> {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();
  LoadSaveTester loadSaveTester;
  Class<SystemDataMeta> testMetaClass = SystemDataMeta.class;
  SystemDataMeta expectedSystemDataMeta;
  String expectedXML = "    <fields>\n" + "      <field>\n" + "        <name>hostname_real</name>\n"
      + "        <type>Hostname real</type>\n" + "        </field>\n" + "      <field>\n"
      + "        <name>hostname</name>\n" + "        <type>Hostname</type>\n" + "        </field>\n"
      + "      </fields>\n";

  @Before
  public void setUp() throws Exception {
    expectedSystemDataMeta = new SystemDataMeta();
    expectedSystemDataMeta.allocate( 2 );
    String[] names = expectedSystemDataMeta.getFieldName();
    SystemDataTypes[] types = expectedSystemDataMeta.getFieldType();
    names[0] = "hostname_real";
    names[1] = "hostname";
    types[0] = SystemDataTypes.getTypeFromString( SystemDataTypes.TYPE_SYSTEM_INFO_HOSTNAME_REAL.getDescription() );
    types[1] = SystemDataTypes.getTypeFromString( SystemDataTypes.TYPE_SYSTEM_INFO_HOSTNAME.getDescription() );
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testLoadXML() throws Exception {
    SystemDataMeta systemDataMeta = new SystemDataMeta();
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse( new InputSource( new StringReader( expectedXML ) ) );
    Node node = document;
    IMetaStore store = null;
    systemDataMeta.loadXML( node, null, store );
    assertEquals( expectedSystemDataMeta, systemDataMeta );
  }

  @Test
  public void testGetXML() throws Exception {
    String generatedXML = expectedSystemDataMeta.getXML();
    assertEquals( expectedXML.replaceAll( "\n", "" ).replaceAll( "\r", "" ), generatedXML.replaceAll( "\n", "" )
        .replaceAll( "\r", "" ) );
  }
  @Before
  public void setUpLoadSave() throws Exception {
    KettleEnvironment.init();
    PluginRegistry.init( false );
    List<String> attributes =
        Arrays.asList( "fieldName", "fieldType" );

    Map<String, String> getterMap = new HashMap<String, String>() {
      {
        put( "fieldName", "getFieldName" );
        put( "fieldType", "getFieldType" );
      }
    };
    Map<String, String> setterMap = new HashMap<String, String>() {
      {
        put( "fieldName", "setFieldName" );
        put( "fieldType", "setFieldType" );
      }
    };
    FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator =
        new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 5 );

    FieldLoadSaveValidator<SystemDataTypes[]> sdtArrayLoadSaveValidator =
        new ArrayLoadSaveValidator<SystemDataTypes>( new SystemDataTypesLoadSaveValidator(), 5 );

    Map<String, FieldLoadSaveValidator<?>> attrValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();
    attrValidatorMap.put( "fieldName", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "fieldType", sdtArrayLoadSaveValidator );

    Map<String, FieldLoadSaveValidator<?>> typeValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();

    loadSaveTester =
        new LoadSaveTester( testMetaClass, attributes, new ArrayList<String>(), new ArrayList<String>(),
            getterMap, setterMap, attrValidatorMap, typeValidatorMap, this );
  }

  // Call the allocate method on the LoadSaveTester meta class
  @Override
  public void modify( StepMetaInterface someMeta ) {
    if ( someMeta instanceof SystemDataMeta ) {
      ( (SystemDataMeta) someMeta ).allocate( 5 );
    }
  }

  @Test
  public void testSerialization() throws KettleException {
    loadSaveTester.testSerialization();
  }

  public class SystemDataTypesLoadSaveValidator implements FieldLoadSaveValidator<SystemDataTypes> {
    final Random rand = new Random();
    @Override
    public SystemDataTypes getTestObject() {
      SystemDataTypes[] allTypes = SystemDataTypes.values();
      return allTypes[rand.nextInt( allTypes.length )];
    }

    @Override
    public boolean validateTestObject( SystemDataTypes testObject, Object actual ) {
      if ( !( actual instanceof SystemDataTypes ) ) {
        return false;
      }
      SystemDataTypes actualInput = (SystemDataTypes) actual;
      return ( testObject.toString().equals( actualInput.toString() ) );
    }
  }


}
