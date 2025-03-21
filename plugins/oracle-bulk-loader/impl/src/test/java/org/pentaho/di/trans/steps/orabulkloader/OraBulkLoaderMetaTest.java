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


package org.pentaho.di.trans.steps.orabulkloader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.DatabaseMetaLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OraBulkLoaderMetaTest {
  Class<OraBulkLoaderMeta> testMetaClass = OraBulkLoaderMeta.class;
  LoadSaveTester loadSaveTester;
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @Before
  public void setUp() throws Exception {
    KettleEnvironment.init();
    PluginRegistry.init( false );
    List<String> attributes =
        Arrays.asList( "schemaName", "tableName", "sqlldr", "controlFile", "dataFile", "logFile", "badFile",
            "discardFile", "commitSize", "bindSize", "readSize", "maxErrors", "loadMethod", "loadAction",
            "encoding", "characterSetName", "directPath", "eraseFiles", "dbNameOverride", "failOnWarning",
            "failOnError", "parallel", "altRecordTerm", "fieldTable", "fieldStream", "dateMask", "databaseMeta" );

    Map<String, String> getterMap = new HashMap<String, String>() {
      {
        put( "schemaName", "getSchemaName" );
        put( "tableName", "getTableName" );
        put( "sqlldr", "getSqlldr" );
        put( "controlFile", "getControlFile" );
        put( "dataFile", "getDataFile" );
        put( "logFile", "getLogFile" );
        put( "badFile", "getBadFile" );
        put( "discardFile", "getDiscardFile" );
        put( "commitSize", "getCommitSize" );
        put( "bindSize", "getBindSize" );
        put( "readSize", "getReadSize" );
        put( "maxErrors", "getMaxErrors" );
        put( "loadMethod", "getLoadMethod" );
        put( "loadAction", "getLoadAction" );
        put( "encoding", "getEncoding" );
        put( "characterSetName", "getCharacterSetName" );
        put( "directPath", "isDirectPath" );
        put( "eraseFiles", "isEraseFiles" );
        put( "dbNameOverride", "getDbNameOverride" );
        put( "failOnWarning", "isFailOnWarning" );
        put( "failOnError", "isFailOnError" );
        put( "parallel", "isParallel" );
        put( "altRecordTerm", "getAltRecordTerm" );
        put( "fieldTable", "getFieldTable" );
        put( "fieldStream", "getFieldStream" );
        put( "dateMask", "getDateMask" );
        put( "databaseMeta", "getDatabaseMeta" );
      }
    };
    Map<String, String> setterMap = new HashMap<String, String>() {
      {
        put( "schemaName", "setSchemaName" );
        put( "tableName", "setTableName" );
        put( "sqlldr", "setSqlldr" );
        put( "controlFile", "setControlFile" );
        put( "dataFile", "setDataFile" );
        put( "logFile", "setLogFile" );
        put( "badFile", "setBadFile" );
        put( "discardFile", "setDiscardFile" );
        put( "commitSize", "setCommitSize" );
        put( "bindSize", "setBindSize" );
        put( "readSize", "setReadSize" );
        put( "maxErrors", "setMaxErrors" );
        put( "loadMethod", "setLoadMethod" );
        put( "loadAction", "setLoadAction" );
        put( "encoding", "setEncoding" );
        put( "characterSetName", "setCharacterSetName" );
        put( "directPath", "setDirectPath" );
        put( "eraseFiles", "setEraseFiles" );
        put( "dbNameOverride", "setDbNameOverride" );
        put( "failOnWarning", "setFailOnWarning" );
        put( "failOnError", "setFailOnError" );
        put( "parallel", "setParallel" );
        put( "altRecordTerm", "setAltRecordTerm" );
        put( "fieldTable", "setFieldTable" );
        put( "fieldStream", "setFieldStream" );
        put( "dateMask", "setDateMask" );
        put( "databaseMeta", "setDatabaseMeta" );
      }
    };
    FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator =
        new ArrayLoadSaveValidator<>( new StringLoadSaveValidator(), 5 );
    FieldLoadSaveValidator<String[]> datemaskArrayLoadSaveValidator =
        new ArrayLoadSaveValidator<>( new DateMaskLoadSaveValidator(), 5 );

    Map<String, FieldLoadSaveValidator<?>> attrValidatorMap = new HashMap<>();
    attrValidatorMap.put( "fieldTable", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "fieldStream", stringArrayLoadSaveValidator );
    attrValidatorMap.put( "dateMask", datemaskArrayLoadSaveValidator );
    attrValidatorMap.put( "databaseMeta", new DatabaseMetaLoadSaveValidator() );

    Map<String, FieldLoadSaveValidator<?>> typeValidatorMap = new HashMap<>();
    // typeValidatorMap.put( int[].class.getCanonicalName(), new PrimitiveIntArrayLoadSaveValidator( new IntLoadSaveValidator(), 1 ) );

    loadSaveTester =
        new LoadSaveTester( testMetaClass, attributes, getterMap, setterMap, attrValidatorMap, typeValidatorMap );
  }

  @Test
  public void testSerialization() throws KettleException {
    loadSaveTester.testSerialization();
  }

  public class DateMaskLoadSaveValidator implements FieldLoadSaveValidator<String> {
    Random r = new Random();

    @Override
    public String getTestObject() {
      boolean ltr = r.nextBoolean();
      String dm = ltr ? "DATE" : "DATETIME";
      return dm;
    }

    @Override
    public boolean validateTestObject( String test, Object actual ) {
      return test.equals( actual );
    }
  }

  //PDI-16472
  @Test
  public void testGetXML() {
    OraBulkLoaderMeta oraBulkLoaderMeta = new OraBulkLoaderMeta();
    oraBulkLoaderMeta.setFieldTable( new String[] { "fieldTable1", "fieldTable2" } );
    oraBulkLoaderMeta.setFieldStream( new String[] { "fieldStreamValue1" } );
    oraBulkLoaderMeta.setDateMask( new String[] {} );

    oraBulkLoaderMeta.afterInjectionSynchronization();
    //run without exception
    oraBulkLoaderMeta.getXML();
    Assert.assertEquals( oraBulkLoaderMeta.getFieldStream().length, oraBulkLoaderMeta.getDateMask().length );
  }

}
