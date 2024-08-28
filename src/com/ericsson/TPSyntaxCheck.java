package com.ericsson;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

/**
 * @author GA337688
 *
 */
public class TPSyntaxCheck {

	
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("To execute Model-T Syntax jar file use following command\n java -jar TPSyntaxCheck.jar <Model-T Name> <Previous Node Version> <Latest Node Version>");
        } else {
            FileInputStream in = null;
            File file = null;

            Logger logger = Logger.getLogger("MyLog");
            Logger vectorLogger = Logger.getLogger("MyLog2"); //logger to create a new log file only for vector sheet
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");
            FileHandler fh;
            FileHandler vectorfh; //FileHandler to create a new log file only for vector sheet
            String FDFullName = null;
            try {

                file = new File(args[0]);
                FDFullName = file.getName();
                in = new FileInputStream(file);
        
            } catch (Exception e) {
                System.out.println("\tException in accessing Model-T:" + e + "\n");
                e.printStackTrace();
                System.exit(0);
            }

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String FDName = FDFullName.substring(0, FDFullName.indexOf("."));

            try {
                fh = new FileHandler("log_" + FDName + "_" + dateFormat.format(date) + ".log");
                vectorfh = new FileHandler("log_Vector_Sheet_" + FDName + "_" + dateFormat.format(date) + ".log");
                logger.addHandler(fh);
                vectorLogger.addHandler(vectorfh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                vectorfh.setFormatter(formatter);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            XSSFWorkbook workBook = null;

            XSSFSheet coversheetSheet = null, factTablesSheet = null, keysSheet = null, countersSheet = null, vectorsSheet = null,
                    topologyTablesSheet = null, topologyKeysSheet = null, dataFormatSheet = null, interfacesSheet = null, transformationsSheet = null,
                    BHSheet = null, BHRankKeysSheet = null, externalStatementSheet = null, universeExtensionSheet = null,
                    universeTopologyTablesSheet = null, universeClassSheet = null, universeTopologyObjectsSheet = null,
                    universeConditionsSheet = null, universeJionsSheet = null,computedCountersSheet=null, reportObjectsSheet = null, reportConditionsSheet = null;
            try {
                workBook = new XSSFWorkbook(in);
                coversheetSheet = workBook.getSheet("Coversheet");
                factTablesSheet = workBook.getSheet("Fact Tables");
                keysSheet = workBook.getSheet("Keys");
                countersSheet = workBook.getSheet("Counters");
                vectorsSheet = workBook.getSheet("Vectors");
                topologyTablesSheet = workBook.getSheet("Topology Tables");
                topologyKeysSheet = workBook.getSheet("Topology Keys");
                dataFormatSheet = workBook.getSheet("Data Format");
                interfacesSheet = workBook.getSheet("Interfaces");
                transformationsSheet = workBook.getSheet("Transformations");
                BHSheet = workBook.getSheet("BH");
                BHRankKeysSheet = workBook.getSheet("BH Rank Keys");
                externalStatementSheet = workBook.getSheet("External Statement");
                universeExtensionSheet = workBook.getSheet("Universe Extension");
                universeTopologyTablesSheet = workBook.getSheet("Universe Topology Tables");
                universeClassSheet = workBook.getSheet("Universe Class");
                universeTopologyObjectsSheet = workBook.getSheet("Universe Topology Objects");
                universeConditionsSheet = workBook.getSheet("Universe Conditions");
                universeJionsSheet = workBook.getSheet("Universe Joins");
                computedCountersSheet=workBook.getSheet("Computed Counters");
                reportObjectsSheet = workBook.getSheet("Report objects");
                reportConditionsSheet = workBook.getSheet("Report conditions");

                /*
                 * System.out.println(coversheetSheet.getLastRowNum()); System.out.println(factTablesSheet.getLastRowNum());
                 * System.out.println(keysSheet.getLastRowNum()); System.out.println(CountersSheet.getLastRowNum());
                 * System.out.println(vectorsSheet.getLastRowNum()); System.out.println(topologyTablesSheet.getLastRowNum());
                 * System.out.println(topologyKeysSheet.getLastRowNum()); System.out.println(dataFormatSheet.getLastRowNum());
                 * System.out.println(interfaceSheet.getLastRowNum()); System.out.println(transformationsSheet.getLastRowNum());
                 * System.out.println(BHSheet.getLastRowNum()); System.out.println(BHRankKeysSheet.getLastRowNum());
                 * System.out.println(externalStatementSheet.getLastRowNum()); System.out.println(universeExtensionSheet.getLastRowNum());
                 * System.out.println(universeTopologyTablesSheet.getLastRowNum()); System.out.println(universeClassSheet.getLastRowNum());
                 * System.out.println(universeTopologyObjectsSheet.getLastRowNum()); System.out.println(universeConditionsSheet.getLastRowNum());
                 * System.out.println(universeJionsSheet.getLastRowNum()); System.out.println(reportObjectsSheet.getLastRowNum());
                 * System.out.println(reportConditionsSheet.getLastRowNum());
                 */
            } catch (Exception e) {
                logger.severe("\tException while creating sheets:" + e + "\n");
                e.printStackTrace();
            }

            logger.info("\tSyntax Verification for Model-T:" + FDName + "\n");

			logger.info("\tChecking if any Cell Value in Model-T starts with '=' Character");

			checkEqualToSymbol(workBook, logger);

            isValid(coversheetSheet, factTablesSheet, keysSheet, countersSheet, vectorsSheet, topologyTablesSheet, topologyKeysSheet, dataFormatSheet,
                    interfacesSheet, transformationsSheet, BHSheet, BHRankKeysSheet, externalStatementSheet, universeExtensionSheet,
                    universeTopologyTablesSheet, universeClassSheet, universeTopologyObjectsSheet, universeConditionsSheet, universeJionsSheet,computedCountersSheet,
                    reportObjectsSheet, reportConditionsSheet, file, logger, vectorLogger,args[1],args[2]);
            logger.info("\tSyntax Verification for Model-T:" + FDName + " is Finished \n");
        }
    }

    public static void isValid(XSSFSheet coversheetSheet, XSSFSheet factTablesSheet, XSSFSheet keysSheet, XSSFSheet countersSheet,
                               XSSFSheet vectorsSheet, XSSFSheet topologyTablesSheet, XSSFSheet topologyKeysSheet, XSSFSheet dataFormatSheet,
                               XSSFSheet interfacesSheet, XSSFSheet transformationsSheet, XSSFSheet BHSheet, XSSFSheet BHRankKeysSheet,
                               XSSFSheet externalStatementSheet, XSSFSheet universeExtensionSheet, XSSFSheet universeTopologyTablesSheet,
                               XSSFSheet universeClassSheet, XSSFSheet universeTopologyObjectsSheet, XSSFSheet universeConditionsSheet,
                               XSSFSheet universeJionsSheet,XSSFSheet computedCountersSheet, XSSFSheet reportObjectsSheet, XSSFSheet reportConditionsSheet, File file, Logger logger,
                               Logger vectorLogger, String PrevVer, String NewVer) {

        int flag, z, count;
        XSSFRow firstRow = null, row = null;
        Iterator<Row> rowIterator = null;

        HashMap<String, Integer> errorCountMap = new HashMap<String, Integer>();
        HashMap<String, String> tableNameVendorIdMap = new HashMap<String, String>();
        LinkedHashMap<String, HashMap<String, String>> map1 = new LinkedHashMap<String, HashMap<String, String>>();
        HashMap<String, String> tableVendorIdMap = new HashMap<String, String>();
        HashMap<String, HashSet<String>> tableandKeyName = new HashMap<String, HashSet<String>>(); //73352
        LinkedHashMap<Integer, ArrayList<String>> dataTableHashMap = new LinkedHashMap<>(); //74801
        LinkedHashMap<Integer, String> parserNameMap = new LinkedHashMap<>(); //74801

        HashSet<String> factTablesSet = new HashSet<String>();
        HashSet<String> factTablesVectorSet = new HashSet<String>();
        HashSet<String> factTablesBHSet = new HashSet<String>();
        HashSet<String> factTablesSpecialSet = new HashSet<String>();
        HashSet<String> keysSet = new HashSet<String>();
        HashSet<String> keysBHSet = new HashSet<String>();
        HashSet<String> keysSetBH = new HashSet<String>();
        HashSet<String> keysSetUnv = new HashSet<String>();
        HashSet<String> countersSet = new HashSet<String>();
        HashSet<String> topologyTablesSet = new HashSet<String>();
        HashSet<String> topologyTablesSet1 = new HashSet<String>();
        HashSet<String> topologyKeysSet = new HashSet<String>();
        HashSet<String> dataFormatSet = new HashSet<String>();
        HashSet<String> vectorsSet = new HashSet<String>(); //EQEV-63309 @
        HashSet<String> vectorsVendorReleaseSet = new HashSet<String>(); //EQEV-63309 @
        HashSet<String> universeExtensionSet = new HashSet<String>();
        HashSet<String> BHSet = new HashSet<String>();
        HashSet<String> BHRankKeysSet = new HashSet<String>();
        HashSet<String> srckeys = new HashSet<>();
        HashSet<String> trgkeys = new HashSet<>();
        HashSet<String> scalarTableAndCounters = new HashSet<>();
        HashSet<String> vectorTablesAndCounters = new HashSet<>();

        HashSet<String> measurementInterfaceSet = new HashSet<String>();
        HashSet<String> countersFactTableSet = new HashSet<String>();
        HashSet<String> keysFactTableSet = new HashSet<String>();
        HashSet<String> topologyKeysTopologyTableSet = new HashSet<String>();
        HashSet<String> dataFormatTableSet = new HashSet<String>();
        HashSet<String> vectorsFactTableSet = new HashSet<String>();
        HashSet<String> transformationsTableSet = new HashSet<String>();
        HashSet<String> factTableTransformationsSet = new HashSet<String>();
        HashSet<String> topologyTableTransformationsSet = new HashSet<String>();
        HashSet<String> dbKeyWordsSet = new HashSet<String>();
        HashSet<String> descriptionSpecialCharacterSet = new HashSet<String>();
        HashSet<String> keyElementColumnSet = new HashSet<String>();
        HashSet<String> keyElementColumnTablesSet = new HashSet<String>();
        HashSet<String> vectorCountersSet = new HashSet<String>();
        HashSet<String> universeClassFactTableSet = new HashSet<String>();
        HashSet<String> universeClassTopologyTableSet = new HashSet<String>();
        HashSet<String> parserNameConflictSet = new HashSet<String>();
        HashSet<String> duplicateRowInterfacesSheet = new HashSet<String>(); //JIRA EQEV-59062 @
        HashSet<String>	unvObjSet = new HashSet<String>();  //EQEV-58209 @
        HashSet<String>	tableNameCounterAndTypeSet = new HashSet<String>();  //EQEV-59884 @
        HashSet<String>	vectorFactTablesNotPresent = new HashSet<String>();
        HashSet<String>	keyDatatypeAndBhCheck = new HashSet<String>();
        HashSet<String>	factTableAndObjectBh = new HashSet<String>();
        HashSet<String>	bhTableDatatypeAndBhCheck = new HashSet<String>();
        
        HashMap<String, ArrayList<String>> KeysMap = new HashMap<>();
        HashMap<String, ArrayList<String>> CommonKeysMap = new HashMap<>();
        HashMap<String, String> UnvExtMap = new HashMap<>();
        ArrayList<String> supportedVersionSet = new ArrayList<String>();
        HashMap<String, ArrayList<String>> MultipleParser = new HashMap<>();
        HashMap<String, Set<String>> TempMultipleParser = new HashMap<>();
        HashMap<String, List<String>> TempMultipleParserforKey = new HashMap<>();

        LinkedList<String> duplicateCountersList = new LinkedList<String>();
        LinkedList<String> duplicateKeysList = new LinkedList<String>();
        LinkedList<String> duplicateTopologyKeysList = new LinkedList<String>();
        LinkedList<String> duplicateTopologyTablesList = new LinkedList<String>();
        LinkedList<String> duplicateFactTablesList = new LinkedList<String>();
        LinkedList<String> duplicateDataFormatList = new LinkedList<String>();
        LinkedList<String> BHRankkeysList = new LinkedList<>();
        LinkedList<String> duplicateUniverseObjectList = new LinkedList<>();  //EQEV-58209 @

        String[] parserTypes = { "eniqasn1", "3gpp32435", "3gpp32435BCS", "3gpp32435DYN", "ascii", "asn1", "bcd", "csexport", "ct", "eascii",
                "ebinary", "ebs", "hxmliptnms", "iptnmsCS", "iptnmsPS", "mdc", "mdc_ccn", "minilink", "nascii", "nossdb", "omes", "omes2", "raml",
                "redback", "sasn", "stfiop", "twampstparser", "xml","TCIM","3gpp32435_YANG","MRR"};//123834 TCIM,3gpp32435_YANG added // MRR added on EQEV-128335
        String[] counterTypes = { "PEG", "GAUGE", "VECTOR", "COMPRESSEDVECTOR", "CMVECTOR", "UNIQUEVECTOR", 
        						  "PMRESVECTOR","FlexVector", "FlexCompVector","FlexdynVector","FlexdynComVector", "FlexCounter", "DynCounter",
        						  "MultiDynCounter"}; // JIRA EQEV-58205 @, EQEV-60554 @, EQEV - 120332 @
        String[] transformationTypes = { "alarm", "bitmaplookup", "calculation", "condition", "convertipaddress", "copy", "currenttime",
                "databaselookup", "dateformat", "defaulttimehandler", "dstparameters", "fixed", "fieldtokenizer", "hashid", "lookup", "postappender",
                "preappender", "propertytokenizer", "radixconvertor", "reducedate", "roptime", "roundtime", "switch", "configlookup" };
        String[] aggregationTypes = { "SUM", "AVG", "MAX", "MIN", "NONE" };
        String[] universeTypes = { "Character", "Number", "Date" };
        String[] universeObjectTypes = { "Character", "Number", "Date" }; // JIRA EQEV-58208 Date should also be included as Universe Type EQEV-58208
        String[] universeQualifications = { "Dimension", "Measure" };
        String[] topologyTableOwners = { "DC", "DWH" };
        String[] externalStatmentDatabaseNames = { "dwh", "dwhrep" };
        String[] tableSizing = { "small", "extrasmall", "medium", "large", "extralarge", "bulk_cm" };
        String [] treatAsVariableVersions = {"true","false"};
        String fdType = null, fDName = null, fDPath = null;
        fDName = file.getName();
        String fDNameExt = fDName.substring(0, fDName.indexOf(".")) + ".txt";
        String techPackName  = null;
        String FName=null,coverSheetBuildNumber = null;

        fDPath = file.getAbsolutePath();
        // System.out.println(fDName+"\t"+fDPath);
        /*
         * reading database keywords
         */

        try {

            BufferedReader buf = new BufferedReader(
                    new InputStreamReader(TPSyntaxCheck.class.getClassLoader().getResourceAsStream("com/resource/conf/keyword.txt")));

            String keyLine = null;
            String[] wordsArray;
            String[] addlist = { "array", "attach", "compressed", "datetimeoffset", "detach", "json", "kerberos", "limit", "merge", "nchar",
                    "nvarchar", "openstring", "openxml", "refresh", "rowtype", "spatial", "subtransaction", "treat", "unnest", "varbit", "varray" };

            while (true) {
                keyLine = buf.readLine();
                if (keyLine == null) {
                    break;
                } else {
                    wordsArray = keyLine.split(" ");
                    for (String each : wordsArray) {
                        dbKeyWordsSet.add(each);
                    }
                }
            }
            for (String a : addlist) {
                dbKeyWordsSet.add(a);
            }
            buf.close();
        } catch (Exception e) {
            logger.severe("\tException in KeyWords:" + e + "\n");
            e.printStackTrace();
        }
        try {
            BufferedReader buf1 = new BufferedReader(
                    new InputStreamReader(TPSyntaxCheck.class.getClassLoader().getResourceAsStream("com/resource/conf/descriptionCheck.txt")));

            String descriptionSpecialCharacter;
            while (true) {
                descriptionSpecialCharacter = buf1.readLine();
                if (descriptionSpecialCharacter != null) {
                    descriptionSpecialCharacterSet.add(descriptionSpecialCharacter);
                } else {
                    break;
                }
            }
            descriptionSpecialCharacterSet.add("\\*");
            buf1.close();
        } catch (Exception e) {
            logger.severe("\tException in DescriptionCheckList:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * CoverSheet verification
         */
        String[] supportedVersions = null;
		int VerFlag=0;
        try {
            logger.info("\tIn CoverSheet Sheet");
            count = 0;
            XSSFCell cell = null, cell1 = null;
            String coverSheetName = null, coverSheetRelease = null, coverSheetFDName = null;
            if (coversheetSheet != null) {
                for (Row r : coversheetSheet) {
                	//System.out.println(isEmptyRow(r));
                    if (!isEmptyRow(r)) {
                        cell = (XSSFCell) r.getCell(0);
                        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                            logger.warning(" In CoverSheet sheet cell is Empty in first column at row: " + (r.getRowNum() + 1));
                            count++;
                        } else {

                            if (cell.getStringCellValue().equalsIgnoreCase("Name")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet Name column value is Empty");
                                    count++;
                                } else {
                                    coverSheetName = cell1.getStringCellValue();
                                    
                                    techPackName = coverSheetName;
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Build Number")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet Build Number Column value is Empty");
                                    count++;
                                } else {
                                    coverSheetBuildNumber = getColumnValue(cell1);

                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Description")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.warning("In CoverSheet sheet Description Column value is Empty");
                                    count++;
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Release")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet Release Column value is Empty");
                                    count++;
                                } else {
                                    coverSheetRelease = getColumnValue(cell1);
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Product number")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet Product number Column value is Empty");
                                    count++;
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("License")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet License Column value is Empty");
                                    count++;
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Type")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.severe("\tIn CoverSheet sheet Type Column value is Empty");
                                    count++;
                                } else {
                                    fdType = cell1.getStringCellValue();
                              
                                }
                            }

                            if (cell.getStringCellValue().equalsIgnoreCase("Supported Versions")) {
                                cell1 = (XSSFCell) r.getCell(1);
                                if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cell1).isEmpty()) {
                                    logger.warning("\tIn CoverSheet sheet Supported versions Column value is Empty");
                                    count++;
                                } else {
                                    String versions = cell1.getStringCellValue();
									if(versions.contains(PrevVer+","+NewVer))
										VerFlag=1;
                                    supportedVersions = versions.split(",");
                                    for (String s : supportedVersions) {
                                        //if (s.charAt(0) != 'R') { //123834
                                            supportedVersionSet.add(s.trim());
                                        //}
                                    }
                                }// if supported versions
                            }
							if (cell.getStringCellValue().equalsIgnoreCase("Network element type")) {
								cell1 = (XSSFCell) r.getCell(1);
								if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK
										|| String.valueOf(cell1).isEmpty()) {
									logger.warning("In CoverSheet sheet Network element type Column value is Empty");
									count++;
								}
							}

							if (cell.getStringCellValue().equalsIgnoreCase("Dependency TechPack")) {
								cell1 = (XSSFCell) r.getCell(1);
								if (cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK) {
									logger.warning("In CoverSheet sheet Dependency TechPack Column value is Empty");
									count++;
								}
							}
							if (cell.getStringCellValue().equalsIgnoreCase("ManModsFile")) {
								cell1 = (XSSFCell) r.getCell(1);
								if (!(cell1 == null || cell1.getCellType() == Cell.CELL_TYPE_BLANK)) {
									FName=cell1.getStringCellValue();
									//System.out.println(FName);
								}
							}
                        } // for
                    }
                } // for each row
                coverSheetFDName = coverSheetName + "_" + coverSheetBuildNumber + "_" + coverSheetRelease + ".xlsx";
                if (!coverSheetFDName.equalsIgnoreCase(fDName)) {
                    logger.severe("\tFD Name is Not matching with Coversheet Details. Model-T Name should be:" + coverSheetFDName
                            + ".\tBut actual Model-T Name:" + fDName);
                    count++;
                }
                if(FName!=null)
                {
                	File f=new File(FName);
                	if(!f.exists())
                		logger.warning("\tThe Manmods File "+FName+" is not in the Current Directory.");
                	else
                	{
                		String[] Version=InFileVersion(f,coverSheetBuildNumber);
                	if(Version!=null) {
                		for(int i=0;i<Version.length;i++) {
                		logger.severe("\tThe TechPack Version in ManMods ("+Version[i]+") and the Version of Model-T ("+coverSheetBuildNumber+") are not same.");
                		count++;
                }}}}
                if (count == 0) {
                    logger.info("\tCoverSheet is Fine");
                } else {
                    logger.info("\tNo of observations in CoverSheet:" + count);
                    errorCountMap.put("CoverSheet Sheet", new Integer(count));
                }
            } else {
                logger.warning("CoverSheet is Empty");
                count++;
            }

            logger.info("\tEnd of CoverSheet Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in CoverSheet Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Interface Sheet
         */
        try {
            logger.info("\tIn Interfaces Sheet");
            count = 0;
            if (interfacesSheet != null) {
                XSSFRow rowInterfaceName = null, rowInterface = null;
                XSSFCell cell = null, cellInterface = null, cellParserName = null, cellElementType = null, cellInterfaceName = null,
                        cellInputDir = null, cellOutputDir = null, cellBaseDir = null,cellTechPackname=null;
                String columnValueInterface = null, columnValueInterfaceName = null;
                rowIterator = interfacesSheet.iterator();
                rowInterface = (XSSFRow) rowIterator.next();
                for (int ii = rowInterface.getFirstCellNum() + 1; ii < rowInterface.getLastCellNum(); ii++) {
                    cellInterface = rowInterface.getCell(ii);
                    if ((cellInterface == null || cellInterface.getCellType() == Cell.CELL_TYPE_BLANK)) {
                        logger.severe("\tIn Interfaces Sheet Interface column value cannot be  Empty  at column:" + (ii + 1));
                        count++;
                    } else {
                        columnValueInterface = cellInterface.getStringCellValue();
                        //logger.info(columnValueInterface);
                    }
                }
                if (!isEmptyRow(rowInterface)) {
                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cell = row.getCell(0);

                            if (!(cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)) {

                                // Parser Name start
                                if (cell.getStringCellValue().equalsIgnoreCase("Parser Name")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellParserName = row.getCell(ii);
                                        if ((cellParserName == null || cellParserName.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe("\tIn Interfaces Sheet parser name column value is Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        } else {
                                            String parserName = cellParserName.getStringCellValue();
                                            if (!Arrays.asList(parserTypes).contains(parserName)) {
                                            	
                                                logger.severe("\tIn Interfaces Sheet parser name column value:" + parserName + " is not valid");
                                                count++;
                                            }
                                            measurementInterfaceSet.add(parserName);
                                        }
                                    }
                                } // Parser Name end
                                  // Element type start
                                if (cell.getStringCellValue().equalsIgnoreCase("Element Type")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellElementType = row.getCell(ii);
                                        if ((cellElementType == null || cellElementType.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe("\tIn Interfaces Sheet Element type column value cannot be is not Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        }
                                    }
                                } // Element type end

                                // InterfaceName start
                                if (cell.getStringCellValue().equalsIgnoreCase("interfaceName")) {
                                    rowInterfaceName = cell.getRow();
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellInterfaceName = row.getCell(ii);
                                        if ((cellInterfaceName == null || cellInterfaceName.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe("\tIn Interfaces Sheet InterfaceName column value cannot be is not Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        } else {
                                            columnValueInterfaceName = cellInterfaceName.getStringCellValue();
                                            // String
                                            // temp=columnValueInterfaceName.substring(0,columnValueInterfaceName.length());
                                            String[] t = rowInterface.getCell(ii).getStringCellValue().split(":");
                                            if (!columnValueInterfaceName.equalsIgnoreCase(t[0])) {
                                                logger.severe("\tIn Interfaces Sheet InterfaceName Column Value:" + columnValueInterfaceName
                                                        + "  is not match with interface column Value:"
                                                        + rowInterface.getCell(ii).getStringCellValue());
                                                count++;
                                            }
                                        }
                                    }
                                } // interfaceName end

                                if (cell.getStringCellValue().equalsIgnoreCase("inDir")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellInputDir = row.getCell(ii);
                                        if ((cellInputDir == null || cellInputDir.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe("\tIn Interfaces Sheet Input Directory cannot be not Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        }
                                    }
                                }

                                if (cell.getStringCellValue().equalsIgnoreCase("outDir")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellOutputDir = row.getCell(ii);
                                        if ((cellOutputDir == null || cellOutputDir.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.warning("In Interfaces Sheet Output Directory cannot be not Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        }
                                    }
                                }
                                if (cell.getStringCellValue().equalsIgnoreCase("baseDir")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellBaseDir = row.getCell(ii);
                                        if ((cellBaseDir == null || cellBaseDir.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.warning("In Interfaces Sheet Base Directory cannot be not Empty in Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        }
                                    }
                                }
                                //Techpack Row - EQEV-136268
                                //System.out.println(cell.getStringCellValue());
                                if (cell.getStringCellValue().equalsIgnoreCase("Tech Pack")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellTechPackname = row.getCell(ii);
                                        if ((cellTechPackname == null || cellTechPackname.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe("\tIn Interfaces Sheet Tech Pack value is Empty for Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        } 
                                        else 
                                        {
                                        	String Temp = cellTechPackname.getStringCellValue().trim();
                                            for(int i=0;i<Temp.length();i++)
                                            {
                                            	if(!(Character.isDigit(Temp.charAt(i)) || Character.isAlphabetic(Temp.charAt(i))))
                                            	{
                                            		if(!(i+1 >= Temp.length() || i+2 >= Temp.length() || i+3 >= Temp.length() || i==0))
                                            		{
                                            			String Temp1 = Character.toString(Temp.charAt(i+1))+ Character.toString(Temp.charAt(i+2))+ Character.toString(Temp.charAt(i+3));
                                            			if(Temp.charAt(i) == ',' && !(Temp1.equalsIgnoreCase("DC_") || Temp1.equalsIgnoreCase("DIM")))
                                            			{
                                            				logger.severe("\tIn Interfaces sheet, After ',' the techpack is not starting with DC_ or DIM in Techpack Value for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                            				count++;
                                            			}
                                            		}
                                            		else
                                            		{
                                            			logger.severe("\tIn Interfaces Sheet, Tech Pack value is Invalid for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                            			count++;
                                            		}
                                            	}
                                            		
                                            }
                                            String[] Array = Temp.split("DC_");
                                            String[] Array1 = Temp.split("DIM_");
                                            if(Array1.length >= 2) Array = Array1;
                                            for(int i=1;i<Array.length-1;i++)
                                            {
                                            	String S=Array[i];
                                            	//System.out.println(S);
                                           		if(!S.endsWith(","))
                                            	{
                                            		logger.severe("\tIn Interfaces Sheet, Tech pack value is not seperated with ',' for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                        			count++;
                                            	}
                                            	
                                            }
                                        }
                                    }
                                }
                                //Dependencies Row - EQEV-136268
                                if (cell.getStringCellValue().equalsIgnoreCase("Dependencies")) {
                                    for (int ii = row.getFirstCellNum() + 1; ii < row.getLastCellNum(); ii++) {
                                        cellTechPackname = row.getCell(ii);
                                        if ((cellTechPackname == null || cellTechPackname.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.warning("\tIn Interfaces Sheet Dependencies value is Empty for Interface:"
                                                    + rowInterface.getCell(ii).getStringCellValue());
                                            count++;
                                        } 
                                        else 
                                        {
                                        	String Temp = cellTechPackname.getStringCellValue().trim();
                                            for(int i=0;i<Temp.length();i++)
                                            {
                                            	if(!(Character.isDigit(Temp.charAt(i)) || Character.isAlphabetic(Temp.charAt(i))))
                                            	{
                                            		if(!(i+1 >= Temp.length() || i+2 >= Temp.length() || i+3 >= Temp.length() || i==0))
                                            		{
                                            			String Temp1 = Character.toString(Temp.charAt(i+1))+ Character.toString(Temp.charAt(i+2))+ Character.toString(Temp.charAt(i+3));
                                            			if(Temp.charAt(i) == ',' && !(Temp1.equalsIgnoreCase("DC_") || Temp1.equalsIgnoreCase("DIM")))
                                            			{
                                            				logger.severe("\tIn Interfaces sheet, After ',' the techpack is not starting with DC_ or DIM in dependencies Value for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                            				count++;
                                            			}
                                            		}
                                            		else
                                            		{
                                            			logger.severe("\tIn Interfaces Sheet Dependencies value is Invalid for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                            			count++;
                                            		}
                                            	}
                                            		
                                            }
                                            String[] Array = Temp.split("DC_");
                                            String[] Array1 = Temp.split("DIM_");
                                            if(Array1.length >= 2) Array = Array1; 
                                            for(int i=1;i<Array.length-1;i++)
                                            {
                                            	String S=Array[i];
                                            	//System.out.println(S);
                                           		if(!S.endsWith(","))
                                            	{
                                            		logger.severe("\tIn Interfaces Sheet Dependencies value is not seperated with ',' for Interface:"+rowInterface.getCell(ii).getStringCellValue());
                                        			count++;
                                            	}
                                            	
                                            }
                                        }
                                    }
                                }
                                
                                 z = duplicateRowInterfacesSheet.size();    //JIRA EQEV-59062 @
                                 duplicateRowInterfacesSheet.add(cell.getStringCellValue());
                                 if(z == duplicateRowInterfacesSheet.size()) {
                                	 logger.severe("RowName "+cell.getStringCellValue()+" is a Duplicate Rowvalue in Interfaces Sheet");
                                	 count++;
                                 }

                            } // if cell Empty
                        } // if Empty row
                    } // for each row
                } else {
                    logger.warning("In Interfaces Sheet First row is Empty");
                    count++;
                }
            } // if interface sheet Empty
            else {
                logger.warning("Interfaces Sheet is Empty");
                count++;
            }
            if (count == 0) {
                logger.info("\tInterfaces Sheet is Fine ");
            } else {
                logger.info("\tNo of observations in Interfaces Sheet:" + count);
                errorCountMap.put("Interfaces Sheet", new Integer(count));
            }
            logger.info("\tEnd of Interfaces Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Interfaces Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Universe Extension Sheet
         */
        try {
            logger.info("\tIn Universe Extension Sheet");
            count = 0;
            if (universeExtensionSheet != null) {
                XSSFCell cellUniverseName = null, cellUniverseExtension = null, cellUniverseExtName = null;
                String columnNameUniverseName = "Universe Name", columnNameUniverseExtension = "Universe Extension",
                        columnNameUniverseExtName = "Universe Ext Name";
                String columnValueUniverseName = null, columnValueUniverseExtension = null, columnValueUniverseExtName = null;
                int columnNoUniverseName, columnNoUniverseExtension, columnNoUniverseExtName;

                rowIterator = universeExtensionSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoUniverseName = getColumnNo(firstRow, columnNameUniverseName);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);
                    columnNoUniverseExtName = getColumnNo(firstRow, columnNameUniverseExtName);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            // if cellUniverseName Name null Start
                            cellUniverseName = row.getCell(columnNoUniverseName);
                            if (cellUniverseName == null || cellUniverseName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Universe Extension sheet UniverseName column value is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueUniverseName = cellUniverseName.getStringCellValue();
                            } // if cellUniverseName null end

                            // if cellUniverseExtension Name null Start
                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe(
                                        "\tIn Universe Extension sheet UniverseExtension column value is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                universeExtensionSet.add(columnValueUniverseExtension);
                            } // if cellUniverseName null end

                            // if cellUniverseExtName null Start
                            columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                            cellUniverseExtName = row.getCell(columnNoUniverseExtName);
                            if(!(columnValueUniverseExtension.equalsIgnoreCase("ALL"))) {   // JIRA EQEV-56248/EQEV-59121 @UniversalExtenisonName
                            	if (cellUniverseExtName == null || cellUniverseExtName.getCellType() == Cell.CELL_TYPE_BLANK) {
                            		logger.warning("In Universe Extension sheet UniverseExtName column value is Empty at row: " + (row.getRowNum() + 1));
                            		count++;   
                            	} 	else {
                                columnValueUniverseExtName = cellUniverseExtName.getStringCellValue();
                            } // if cellUniverseName null end
                            } //if columnValueUniverseExtension= "ALL" ends
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeExtensionSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Extension Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Universe Extension Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Universe Extension sheet is Empty");
                count++;
            } // if sheet
            if (universeExtensionSet.size() > 0) {
                // To support multiple universes
                universeExtensionSet.add("ALL");
            }
            if (count == 0) {
                logger.info("\tUniverse Extension Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Universe Extension Sheet:" + count);
                errorCountMap.put("Universe Extension Sheet", new Integer(count));
            }
            logger.info("\tEnd of Universe Extension Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\t\tException in Universe Extension Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Fact tables verification
         */

        try {
            logger.info("\tIn Fact Tables sheet");
            count = 0;
            if (factTablesSheet != null) {

                XSSFCell cellFactTableName = null, cellFactTableDescription = null, cellUniverseClass = null, cellTableSizing = null,
                        cellTotalAggregation = null, cellObjectBHs = null, cellElementBHs = null, cellRankTable = null, cellCountTable = null,
                        cellVectorTable = null, cellPlainTable = null, cellUniverseExtension = null, cellJionable = null, cellFollowJohn = null;
                XSSFCell cellTagName = null;

                String columnNameFactTableName = "Fact Table Name", columnNameFactTableDescription = "Fact Table Description",
                        columnNameUniverseClass = "Universe Class", columnNameTableSizing = "Table Sizing",
                        columnNameTotalAggregation = "Total aggregation", columnNameObjectBHs = "Object BHs", columnNameElementBHs = "Element BHs",
                        columnNameRankTable = "Rank Table", columnNameCountTable = "Count Table", columnNameVectorTable = "Vector Table",
                        columnNamePlainTable = "Plain Table", columnNameUniverseExtension = "Universe Extension", columnNameJionable = "Joinable", columnNameFollowJohn = "FOLLOWJOHN";
                String columnValueFactTableName = null, columnValueFactTableDescription = null, columnValueUniverseClass = null,
                        columnValueTableSizing = null, columnValueTotalAggregation = null, columnValueObjectBHs = null, columnValueElementBHs = null,
                        columnValueRankTable = null, columnValueCountTable = null, columnValueVectorTable = null, columnValuePlainTable = null,
                        columnValueUniverseExtension = null, columnValueJionable = null, columnValueTagName = null;
                String[] countArr;
                String tempVector = null;
                int[] columnNoTagName;
                int columnNoFactTableName, columnNoFactTableDescription, columnNoUniverseClass, columnNoTableSizing, columnNoTotalAggregation,
                        columnNoObjectBHs, columnNoElementBHs, columnNoRankTable, columnNoCountTable, columnNoVectorTable, columnNoPlainTable,
                        columnNoUniverseExtension, columnNoJionable, columnNoFollowJohn;

                rowIterator = factTablesSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoTagName = new int[measurementInterfaceSet.size()];
                    z = 0;
                    for (String s : measurementInterfaceSet) {
                    	int columnNumber = getColumnNo(firstRow, s);
						if (columnNumber > 0) {
							columnNoTagName[z++] = columnNumber;
							//logger.info(Integer.toString(columnNumber));
						
						} else {
							logger.warning("Parser Name: "+s+" is not present in Fact Tables sheet");
							count++;
						}
                    }
                    

                    columnNoFactTableName = getColumnNo(firstRow, columnNameFactTableName);
                    columnNoFactTableDescription = getColumnNo(firstRow, columnNameFactTableDescription);
                    columnNoUniverseClass = getColumnNo(firstRow, columnNameUniverseClass);
                    columnNoTableSizing = getColumnNo(firstRow, columnNameTableSizing);
                    columnNoTotalAggregation = getColumnNo(firstRow, columnNameTotalAggregation);
                    columnNoObjectBHs = getColumnNo(firstRow, columnNameObjectBHs);
                    columnNoElementBHs = getColumnNo(firstRow, columnNameElementBHs);
                    columnNoRankTable = getColumnNo(firstRow, columnNameRankTable);
                    columnNoCountTable = getColumnNo(firstRow, columnNameCountTable);
                    columnNoVectorTable = getColumnNo(firstRow, columnNameVectorTable);
                    columnNoPlainTable = getColumnNo(firstRow, columnNamePlainTable);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);
                    columnNoJionable = getColumnNo(firstRow, columnNameJionable);
                    columnNoFollowJohn = getColumnNo(firstRow, columnNameFollowJohn);
                    
                    boolean followJohnFlag = false;
                    if(columnNoFollowJohn!= -1)
                    	followJohnFlag = true;
                    else {
                    	logger.severe("In fact Tables Sheet, FOLLOWJOHN column is not available");
                    	count++;
                    }
                    
                    LinkedList<String> l1 = new LinkedList<String>();
                    LinkedList<String> l2 = new LinkedList<String>();
                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            // Fact Table Name begin
                            cellFactTableName = row.getCell(columnNoFactTableName);
                            if (cellFactTableName == null || cellFactTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Fact Tables sheet Fact table name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueFactTableName = cellFactTableName.getStringCellValue();
                                
                                if (!isValidName(columnValueFactTableName)) {
                                    logger.severe("\tIn Fact Tables sheet FactTableName column value is not valid at row:" + (row.getRowNum() + 1));
                                    count++;
                                }
                                
                              //EQEV-113876
                				if(techPackName.equals("DC_E_TNSPPT"))
                				{
                					
                					if(columnValueFactTableName.startsWith("DC_E_SOEM") || columnValueFactTableName.startsWith(techPackName))
                					{			
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueFactTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}

                				else if(techPackName.equals("DC_E_IMSGW_MGC"))
                				{
                					if( columnValueFactTableName.startsWith("DC_E_IMSMGC") || columnValueFactTableName.startsWith(techPackName))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueFactTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}
                				else if(techPackName.equals("DC_E_IMSGW_MGW"))
                				{
                					if( columnValueFactTableName.startsWith("DC_E_IMSMGW") || columnValueFactTableName.startsWith(techPackName))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueFactTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}

                				else if(techPackName.equals("DC_E_IMSGW_SBG"))
                				{
                					if( columnValueFactTableName.startsWith("DC_E_IMSSBG") || columnValueFactTableName.startsWith(techPackName))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueFactTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}

                				else
                					{
                					if(!columnValueFactTableName.startsWith(techPackName))
                					{
                					logger.severe("\t fail:"+" " + columnValueFactTableName + ": "+ "TableName doesn't start with TP Name or input value");
                					count++;
                					}
                					}

                                z = factTablesSet.size();
                                factTablesSet.add(columnValueFactTableName);
                                //System.out.println("start");
                                if (z == factTablesSet.size()) {
                                    duplicateFactTablesList.add(columnValueFactTableName);
                                } else {
                                    cellVectorTable = row.getCell(columnNoVectorTable);
                                    if (!(cellVectorTable == null || cellVectorTable.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                        columnValueVectorTable = cellVectorTable.getStringCellValue();
                                        if (columnValueFactTableName.endsWith("_V")) {
                                            if (!columnValueVectorTable.equalsIgnoreCase("Y")) {
                                                logger.warning("In Fact Tables sheet vector table name:" + columnValueFactTableName
                                                        + " is  ended with '_V' but in VectorTable value is not'Y'");
                                                count++;
                                            }
                                        }
                                        if (columnValueVectorTable.equalsIgnoreCase("Y")) {
                                            factTablesVectorSet.add(columnValueFactTableName);
                                            if (!columnValueFactTableName.endsWith("_V")) {
                                                logger.warning("In Fact Tables sheet vector table name:" + columnValueFactTableName
                                                        + " is not ended with '_V' ");
                                                count++;
                                            }
                                        }

                                    } // if cell vector Table Null
                                      // JIRA EQEV-46761:BusyHour_RANKINGTABLE_Enabled
                                  
                                    if (!fdType.equalsIgnoreCase("CM")) {
                                    	boolean isRankTable = false;
                                        cellRankTable = row.getCell(columnNoRankTable);
                                        if (!(cellRankTable == null || cellRankTable.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            columnValueRankTable = cellRankTable.getStringCellValue();
                                            if (columnValueRankTable.equalsIgnoreCase("Y")) {
                                            	isRankTable = true;
                                                factTablesBHSet.add(columnValueFactTableName);
                                                if (!columnValueFactTableName.endsWith("BH")) {
                                                    logger.warning("BH Table " + columnValueFactTableName + " is not end with BH ");
                                                    count++;
                                                   }
                                                
                                            } else {
                                            	
                                                cellTotalAggregation = row.getCell(columnNoTotalAggregation);
                                                if (!(cellTotalAggregation == null || cellTotalAggregation.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                                    columnValueTotalAggregation = cellTotalAggregation.getStringCellValue();
                                                    if (columnValueTotalAggregation.equalsIgnoreCase("Y")) {
                                                        // logger.fine(columnValueFactTableName+" is a Normal Table");
                                                        // System.out.println(columnValueFactTableName);

                                                    } else {
                                                        factTablesSpecialSet.add(columnValueFactTableName);
                                                        // logger.warning("In Fact Tables sheet
                                                        // Table:"+columnValueFactTableName+" is not having support in
                                                        // either Rank Table or Total Aggregation");count++;
                                                    } // if TotalAggregation Y
                                                } // if TotalAggregation Null

                                            } // if cellRankTable Null
                                        }
                                        //System.out.println("2");
                                        cellCountTable = row.getCell(columnNoCountTable);
										/*for(int i=0;i<supportedVersionSet.size();i++)
											System.out.print(supportedVersionSet.get(i)+" ");
										System.out.println();*/
										String LatestVersion=null;
										if(supportedVersionSet.size()>0)
										LatestVersion=supportedVersionSet.get(supportedVersionSet.size()-1);
										//System.out.println("l;"+LatestVersion);
										
                                        if (!(cellCountTable == null || cellCountTable.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                        	cellCountTable.setCellType(HSSFCell.CELL_TYPE_STRING);   //JIRA EQEV-60325, EQEV-60501 @
                                            columnValueCountTable = cellCountTable.getStringCellValue();
                                            if (columnValueCountTable.length() > 0) {
                                                countArr = columnValueCountTable.split(",");
                                                for (String s : countArr) {
                                                	if(s.contains(":")) {
                                                		String [] s1 = s.split(":");
                                                		if (!supportedVersionSet.contains(s1[0].trim()) ) {
                                                			logger.severe("\tIn Fact Tables sheet " + s1[0]
                                                					+ "  is not a supported count table version for the Table:" + columnValueFactTableName);
                                                			count++;
                                                		}
                                                		else if(!(LatestVersion.equals(s1[0].trim())) && LatestVersion!=null)
														{
                                                			
															logger.warning("\tIn Fact Tables sheet, Node version is not updated to Latest Version ("+LatestVersion+") for the Count Supported Fact Table "+columnValueFactTableName);
															count++;
														}
                                                		if(!Arrays.asList(treatAsVariableVersions).contains(s1[1])) {
                                                			logger.severe("\tIn Fact Tables sheet " + s1[1]
                                                					+ "  is not a supported count table Treat As Variable in Table:" + columnValueFactTableName);
                                                			count++;
                                                		}
                                                    } 
                                            
                                                	else {
                                                		if (!supportedVersionSet.contains(s.trim()) ) {
                                                			logger.severe("\tIn Fact Tables sheet " + s
                                                					+ "  is not a supported count table version in Table:" + columnValueFactTableName);
                                                			count++;
                                                		}
                                                	}
                                                } // for countArr
                                            } // if columnValueCountTable length
                                        } // ifcellCountTable is null
        
                                        cellTableSizing = row.getCell(columnNoTableSizing);
                                        if ((cellTableSizing == null || cellTableSizing.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.severe(
                                                    "\tIn Fact Tables sheet Table Sizing column value is Empty in Table:" + columnValueFactTableName);
                                            count++;
                                        } else {
                                            columnValueTableSizing = cellTableSizing.getStringCellValue();
                                            if (techPackName.equals("DC_E_BULK_CM")){//123834
                                            	if(!columnValueTableSizing.equals("bulk_cm")) {
                                            		logger.severe("\tIn Fact Tables sheet Table Sizing column value:" + columnValueTableSizing
                                                            + "  in Table:" + columnValueFactTableName);
                                                    count++;
                                            	}
                                            }
                                            else {
                                            if (!Arrays.asList(tableSizing).contains(columnValueTableSizing)) {
                                                logger.severe("\tIn Fact Tables sheet Table Sizing column value:" + columnValueTableSizing
                                                        + "  in Table:" + columnValueFactTableName);
                                                count++;
                                            }
                                        }
                                        }
                                        if (!fdType.equalsIgnoreCase("CM")) {
                                            cellObjectBHs = row.getCell(columnNoObjectBHs);
                                            if ((cellObjectBHs == null || cellObjectBHs.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellObjectBHs).isEmpty())) {
                                                // JIRA EQEV-46761: Measurement_Table_ObjectBH
                                                logger.severe("\tIn Fact Tables sheet ObjectBHs column value is Empty in Table:"
                                                        + columnValueFactTableName);
                                                count++;
                                            } else {
                                                columnValueObjectBHs = cellObjectBHs.getStringCellValue();
                                                String[] objectBh = columnValueObjectBHs.split(",");
												if (!factTablesBHSet.contains(columnValueFactTableName)) {
													for (String bh : objectBh) {
														factTableAndObjectBh.add(columnValueFactTableName + ":" + bh);
													}
												}
												
												if(isRankTable) {
													if(objectBh.length>1) {
														logger.severe("In Fact tables sheet, BH table "+columnValueFactTableName+" contains more than 1 Object BHs");
														count++;
													}
													else {
														String tableName = techPackName+"_"+columnValueObjectBHs+"BH";
														if(!columnValueFactTableName.equals(tableName)) {
															logger.severe("In Fact Tables sheet, BH Table Name "+columnValueFactTableName+" is Invalid");
															count++;
														}
													}
												}
												
						
						// JIRA EQEV-102572 fix: DayBH sets are not being added to the TPI file even after adding details for rank bh and busy hour objects.
                        // If ObjectBHs column have 'ELEM' then warning in logger file.			   		
						//*****************************************************
									
												 if (columnValueFactTableName.contains(columnValueObjectBHs)) { 
													// if(columnValueObjectBHs.endsWith("BH")) {
														 columnValueObjectBHs = cellObjectBHs.getStringCellValue();
														 if(columnValueObjectBHs.equalsIgnoreCase("elem")) {
															 logger.warning("\tBusy Hour Table : " + columnValueFactTableName + " have ObjectBHs as "+
														     columnValueObjectBHs ); 
															 count++;
														 }
													// } 
												 }
						//*****************************************************
												
                                                /*
                                                 * // JIRA EQEV-46761 fix: BusyHour_Table_Name if
                                                 * (columnValueFactTableName.contains(columnValueObjectBHs)) { if
                                                 * (!columnValueObjectBHs.endsWith("BH")) { logger.warning("Busy Hour Table name " +
                                                 * columnValueFactTableName + " is not end with BH"); count++; } }
                                                 */
                                            }
                                        }
                                        cellElementBHs = row.getCell(columnNoElementBHs);
                                        if ((cellElementBHs == null || cellElementBHs.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.fine("In Fact Tables sheet ElementBHs column value is Empty in Table:" + columnValueFactTableName);
                                        } else {
                                            columnValueElementBHs = cellElementBHs.getStringCellValue();
                                        }

                                        cellPlainTable = row.getCell(columnNoPlainTable);
                                        if ((cellPlainTable == null || cellPlainTable.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.fine(
                                                    "In Fact Tables sheet Plain Table column value is Empty in Table:" + columnValueFactTableName);
                                        } else {
                                            columnValuePlainTable = cellPlainTable.getStringCellValue();

                                        }

                                        cellJionable = row.getCell(columnNoJionable);
                                        if ((cellJionable == null || cellJionable.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                            logger.fine("In Fact Tables sheet Jionable column value is Empty in Table:" + columnValueFactTableName);
                                        } else {
                                            columnValueJionable = cellJionable.getStringCellValue();
                                        }
                                        //123834
                                    }
                                       
                                        flag = 0;
                                        ArrayList<String> Parsers = new ArrayList<String>();
                                        Set<String> TParsers = new HashSet<String>();
                                        List<String> S=new ArrayList<String>();
                                        for (int p = 0; p < measurementInterfaceSet.size(); p++) {
                          
                                            if (columnNoTagName[p] > 0) {
                                            	//logger.info(Integer.toString(columnNoTagName[p]));
                                                cellTagName = row.getCell(columnNoTagName[p]);
                                                if (!(cellTagName == null || cellTagName.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                                    flag++;
                                                    columnValueTagName = cellTagName.toString(); //EQEV-64301 @
                                                    String[] split = null;
                                                    Parsers.add(measurementInterfaceSet.toArray()[p].toString());
                                                    TParsers.add(measurementInterfaceSet.toArray()[p].toString());
                                                    S.add(measurementInterfaceSet.toArray()[p].toString());
                                                    //logger.info(Parsers.toString());
                                                    if (columnValueTagName.contains(";")) {
                                                        split = columnValueTagName.split(";");
                                                        for (int i = 0; i < split.length; i++) {
                                                            columnValueTagName = split[i];
                                                            if (columnValueTagName.trim().length() > 0) {
                                                                factTableTransformationsSet.add(columnValueFactTableName.trim());  //EQEV-58207 @
                                                     
                                                                if (columnValueFactTableName.trim().length() > 0) {
                                                                    isDiffTableHaveSameVendorId(
                                                                            firstRow.getCell(columnNoTagName[p]).getStringCellValue().trim(),
                                                                            columnValueFactTableName.trim(), columnValueTagName, map1, logger);

                                                                    if (tableNameVendorIdMap.isEmpty()) {
                                                                    	
                                                                       tableNameVendorIdMap.put(columnValueTagName, columnValueFactTableName.trim());
                                               
                                                                    } else {
                                                                        if (tableNameVendorIdMap.containsKey(columnValueTagName)) {
                                                                            if (!tableNameVendorIdMap.get(columnValueTagName)
                                                                                    .equals(columnValueFactTableName.trim())) {
                                                                                logger.severe(columnValueFactTableName + " and "
                                                                                        + tableNameVendorIdMap.get(columnValueTagName)
                                                                                        + " cannot have same vendor id");
                                                                                count++;
                                                                            }
                                                                        } else {
                                                                            tableNameVendorIdMap.put(columnValueTagName,
                                                                                    columnValueFactTableName.trim());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        if (columnValueTagName.trim().length() > 0) {
                                                            factTableTransformationsSet.add(columnValueFactTableName.trim());   //EQEV-58207 @
                                                            if (columnValueFactTableName.trim().length() > 0) {
                                                                isDiffTableHaveSameVendorId(
                                                                        firstRow.getCell(columnNoTagName[p]).getStringCellValue().trim(),
                                                                        columnValueFactTableName.trim(), columnValueTagName, map1, logger);

                                                                if (tableNameVendorIdMap.isEmpty()) {
                                                                    tableNameVendorIdMap.put(columnValueTagName, columnValueFactTableName.trim());
                                                                } else {
                                                                    if (tableNameVendorIdMap.containsKey(columnValueTagName)) {
                                                                        if (!tableNameVendorIdMap.get(columnValueTagName)
                                                                                .equals(columnValueFactTableName.trim())) {
                                                                            logger.severe(columnValueFactTableName + " and "
                                                                                    + tableNameVendorIdMap.get(columnValueTagName)
                                                                                    + " cannot have same vendor id");
                                                                            count++;
                                                                        }
                                                                    } else {
                                                                        tableNameVendorIdMap.put(columnValueTagName, columnValueFactTableName.trim());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        MultipleParser.put(columnValueFactTableName, Parsers);
                                        TempMultipleParser.put(columnValueFactTableName,TParsers);
                                        TempMultipleParserforKey.put(columnValueFactTableName,S);
                                        if (flag == 0) {
                                            if (!factTablesBHSet.contains(columnValueFactTableName)) {
                                                logger.warning("In Fact Tables sheet Table:" + columnValueFactTableName
                                                        + "  is not having tag support in any parser");
                                                count++;
                                            }
                                        }

                                     // if duplicate
                                    //123834
                                    cellFactTableDescription = row.getCell(columnNoFactTableDescription);
                                    if ((cellFactTableDescription == null || cellFactTableDescription.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                        logger.warning("In Fact Tables sheet FactTable Description column value is Empty in Table:"
                                                + columnValueFactTableName);
                                        count++;
                                    } else {
                                        columnValueFactTableDescription = cellFactTableDescription.getStringCellValue();
                                        // JIRA EQEV-56248/EQEV-59122: Wild card Description_Check removed
                                    	}

                                    cellUniverseClass = row.getCell(columnNoUniverseClass);
                                    if ((cellUniverseClass == null || cellUniverseClass.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                        logger.warning(
                                                "\tIn Fact Tables sheet Universe Class column value is Empty in Table:" + columnValueFactTableName);
                                        count++;
                                    } else {
                                        columnValueUniverseClass = cellUniverseClass.getStringCellValue();
                                        if (columnValueUniverseClass.endsWith("_V")) {
                                            logger.warning(
                                                    "In Fact Tables sheet Universe Class ended with _V in Table:" + columnValueFactTableName);
                                            count++;
                                        }
                                    }

                                    if(followJohnFlag) {
                                    	cellFollowJohn = row.getCell(columnNoFollowJohn);
                                    	if ((cellFollowJohn != null) && (cellFollowJohn.getCellType() != cellFollowJohn.CELL_TYPE_BLANK) && (!String.valueOf(cellFollowJohn).isEmpty())) {
                                    		String columnValueFollowJohn = new DataFormatter().formatCellValue(cellFollowJohn);
                                    		if(!(columnValueFollowJohn.equals("0") || columnValueFollowJohn.equals("1"))) {
                                    			logger.severe("In Fact Tables sheet, FOLLOWJOHN cell value is not in 0 or 1 in Table : "+columnValueFactTableName);
                                    			count++;
                                    		}
                                    	}
                                     	else {
                                    		logger.severe("In Fact Tables sheet, FOLLOWJOHN cell value is empty in Table : "+columnValueFactTableName);
                                    		count++;
                                    	}
                                    }//123834
                                    cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                                    if ((cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                        logger.severe("\tIn Fact Tables sheet Universe Extension column value is Empty in Table:"
                                                + columnValueFactTableName);
                                        count++;
                                    } else {
                                        columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                        String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                        for (String s : universeExtensionArray) {
                                            //JIRA EQEV-46761: Measurement_Table_Universe_Extention
                                            if (!universeExtensionSet.contains(s)) {
                                                logger.severe("\tIn Fact Tables sheet " + s + "is not a valid Universe Extension in Table:"
                                                        + columnValueFactTableName);
                                                count++;
                                            }
                                            UnvExtMap.put(columnValueFactTableName, s);
                                        }
                                    }
                                    ///123834
                                  
                                    if(columnValueFactTableName.endsWith("_V")) {
                                    	for (int p = 0; p < measurementInterfaceSet.size(); p++) {
                                            if (columnNoTagName[p] >= 0) {
                                                cellTagName = row.getCell(columnNoTagName[p]);
                                                if (!(cellTagName == null || cellTagName.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellTagName).isEmpty())) {
                                                    columnValueTagName = cellTagName.toString();
                                                    if(!columnValueTagName.endsWith("_V")) {
                                                    	logger.severe("In fact Tables Sheet, for fact Table "+columnValueFactTableName+" Tag name "
                                                    			+columnValueTagName+ " is Invalid");
                                                    	//System.out.println("22232");
                                                    	count++;
                                                    }
                                                }
                                            }
                                    	}
                                    }
									
                                    
                                } // if cell temp size
                            } // if cell fact table name
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(factTablesSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Fact Tables Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Fact Tables sheet ");
                    count++;
                } // if First row
            } else {
                logger.warning("Fact Tables sheet is Empty");
                count++;
            } // if sheet
            if (duplicateFactTablesList.size() > 0) {
                logger.info("\tList of Duplicate Fact Tables as follows");

                for (String s : duplicateFactTablesList) {
                    logger.severe("\tIn Fact Tables sheet Table:" + s + "  is a duplicate Fact table");
                    count++;
                }
            }

            if (count == 0) {
                logger.info("\tFact Tables Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Fact Tables sheet:" + count);
                errorCountMap.put("Fact Tables Sheet", new Integer(count));
            }
            
            logger.info("\tEnd of FactTables Tab" + "\n");
            //logger.info(MultipleParser.toString());
        } catch (Exception e) {
            System.out.println("Exception in FactTables sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * In counters sheet
         */
        try {
            logger.info("\tIn Counters sheet");
            count = 0;
            if (countersSheet != null) {
                XSSFCell cellFactTableName = null, cellCounterName = null, cellCounterDescription = null, cellDataType = null,
                        cellTimeAggregation = null, cellGroupAggregation = null, cellUniverseObject = null, cellUniverseClass = null,
                        cellCounterType = null, cellIncludeSQL = null, cellFollowJohn = null;
                String columnNameFactTableName = "Fact Table Name", columnNameCounterName = "Counter Name",
                        columnNameCounterDescription = "Counter Description", columnNameDataType = "Data type",
                        columnNameTimeAggregation = "Time Aggregation", columnNameGroupAggregation = "Group Aggregation",
                        columnNameUniverseObject = "Universe Object", columnNameUniverseClass = "Universe Class",
                        columnNameCounterType = "Counter Type", columnNameIncludeSQL = "IncludeSQL", columnNameFollowJohn = "FOLLOWJOHN";
                String columnValueFactTableName = null, columnValueCounterName = null, columnValueCounterDescription = null,
                        columnValueDataType = null, columnValueTimeAggregation = null, columnValueGroupAggregation = null,
                        columnValueUniverseObject = null, columnValueUniverseClass = null, columnValueCounterType = null,
                        columnValueIncludeSQL = null;
                int columnNoFactTableName, columnNoCounterName, columnNoCounterDescription, columnNoDataType, columnNoTimeAggregation,
                        columnNoGroupAggregation, columnNoUniverseObject, columnNoUniverseClass, columnNoCounterType, columnNoIncludeSQL, columnNoFollowJohn;

                rowIterator = countersSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoFactTableName = getColumnNo(firstRow, columnNameFactTableName);
                    columnNoCounterName = getColumnNo(firstRow, columnNameCounterName);
                    columnNoCounterDescription = getColumnNo(firstRow, columnNameCounterDescription);
                    columnNoDataType = getColumnNo(firstRow, columnNameDataType);
                    columnNoTimeAggregation = getColumnNo(firstRow, columnNameTimeAggregation);
                    columnNoGroupAggregation = getColumnNo(firstRow, columnNameGroupAggregation);
                    columnNoUniverseObject = getColumnNo(firstRow, columnNameUniverseObject);
                    columnNoUniverseClass = getColumnNo(firstRow, columnNameUniverseClass);
                    columnNoCounterType = getColumnNo(firstRow, columnNameCounterType);
                    columnNoIncludeSQL = getColumnNo(firstRow, columnNameIncludeSQL);
                    columnNoFollowJohn = getColumnNo(firstRow, columnNameFollowJohn);
                    
                    boolean followJohnFlag = false;
                    if(columnNoFollowJohn != -1)
                    	followJohnFlag = true;
                    else {
                    	logger.severe("In Counters Sheet, FOLLOWJOHN column is not Available");
                    	count++;
                    }

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            boolean factTableNameFlag = false;
                            boolean counterNameFlag = false;
                            boolean counterTypeFlag = false;
                        	
                            // if cellFactTable Name null Start
                            cellFactTableName = row.getCell(columnNoFactTableName);
                            if (cellFactTableName == null || cellFactTableName.getCellType() == Cell.CELL_TYPE_BLANK 
                            		|| String.valueOf(cellFactTableName).isEmpty()) {
                                logger.severe("\tIn Counters sheet FactTable Name column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                            	factTableNameFlag = true;
                                columnValueFactTableName = cellFactTableName.getStringCellValue();
                                if (!factTablesSet.contains(columnValueFactTableName)) {
                                    logger.severe("\tIn Counters sheet FactTableName:" + columnValueFactTableName
                                            + "  is present in Counters Sheet.but it is not present in factTablesSet");
                                    count++;
                                }
                                countersFactTableSet.add(columnValueFactTableName);
                            } // if cellFactTable Name null end

                            // if cellCounterName null start
                            cellCounterName = row.getCell(columnNoCounterName);
                            if (cellCounterName == null || cellCounterName.getCellType() == Cell.CELL_TYPE_BLANK
                            		|| String.valueOf(cellCounterName).isEmpty()) {
                                logger.severe("\tIn Counters sheet Counter Name column value is Empty in Table\t" + columnValueFactTableName
                                        + "  at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                            	counterNameFlag = true;
                                columnValueCounterName = cellCounterName.getStringCellValue();
                                if (!isValidName(columnValueCounterName)) {
                                    logger.severe("\tIn Counters sheet CounterName column value is not valid in Table:" + columnValueFactTableName
                                            + "  Counter : " + columnValueCounterName);
                                    count++;
                                }
                                z = countersSet.size();
                                countersSet.add(columnValueFactTableName + ":" + columnValueCounterName);
                                if (z == countersSet.size()) {
                                    duplicateCountersList.add(columnValueFactTableName + ":" + columnValueCounterName);
                                } else {
                                    if (isKeyWord(columnValueCounterName, dbKeyWordsSet)) {
                                        logger.severe("\tIn Counters sheet at table:" + columnValueFactTableName + "  for counter:"
                                                + columnValueCounterName + "  is a keyword in DataBase");
                                        count++;
                                    }
                                }
                            } // if cellCounterName null end
                            
                            // if cellCounterDescription null Start
                            // JIRA EQEV-46761: Check_Description
                            cellCounterDescription = row.getCell(columnNoCounterDescription);
                            if (cellCounterDescription == null || cellCounterDescription.getCellType() == Cell.CELL_TYPE_BLANK
                            		|| String.valueOf(cellCounterDescription).isEmpty()) {
                                logger.warning("In Counters sheet Counter Description is Empty at table:" + columnValueFactTableName
                                        + "  for counter:" + columnValueCounterName + "  ");
                                count++;
                            } else {
								columnValueCounterDescription = cellCounterDescription.getStringCellValue();

								ArrayList<Character> checkingEqualDoubleQuotes = new ArrayList<>();
								List<Character> doubleQuoteCharList = Arrays.asList('\"', '\u201c', '\u201d', '\u201e',
										'\u201f', '\u2033', '\u2036');

								if (factTableNameFlag && counterNameFlag) {
									for (int i = 0; i < columnValueCounterDescription.length(); i++) {
										char ch = columnValueCounterDescription.charAt(i);
										if (doubleQuoteCharList.contains(ch)) {
											checkingEqualDoubleQuotes.add(ch);
										}
									}
									if (!checkingEqualDoubleQuotes.isEmpty()) {
										if (checkingEqualDoubleQuotes.size() % 2 != 0) {
											logger.severe(
													"In Counters Sheet, Double Quotes do not close properly in Counter Description at Fact Table: "
															+ columnValueFactTableName + " for Counter: "
															+ columnValueCounterName + " at row:"
															+ (row.getRowNum() + 1));
											count++;
										}
									}
								}
                                // JIRA EQEV-56248/EQEV-59122: Wild card Description_Check removed
                            	} // if cellCounterDescription null end

                            // if cellDataType Name null start
                            cellDataType = row.getCell(columnNoDataType);

                            if ((cellDataType == null || cellDataType.getCellType() == Cell.CELL_TYPE_BLANK
                            		|| String.valueOf(cellDataType).isEmpty())) {
                                logger.severe("\tIn Counters sheet DataType is Empty in Table\t" + columnValueFactTableName + "  for counter:"
                                        + columnValueCounterName);
                                count++;
                            } else {
                                columnValueDataType = cellDataType.getStringCellValue();
                                if (!(isValidDataType(columnValueDataType))) {
                                    logger.severe("\tIn Counters sheet at table:" + columnValueFactTableName + "  for counter:"
                                            + columnValueCounterName + "  DataType:" + columnValueDataType + "  is not a valid DataType");
                                    count++;
                                }

                            } // if cellDataType Name null end

                            // if cellTimeAggregation null Start
                            if (!fdType.equalsIgnoreCase("CM")) {
                                cellTimeAggregation = row.getCell(columnNoTimeAggregation);
                                if (cellTimeAggregation == null || cellTimeAggregation.getCellType() == Cell.CELL_TYPE_BLANK
                                		|| String.valueOf(cellTimeAggregation).isEmpty()) {
                                    logger.severe("\tIn Counters sheet in table:" + columnValueFactTableName + "  for counter:"
                                            + columnValueCounterName + "  TimeAggregation is Empty");
                                    count++;
                                } else {
                                    columnValueTimeAggregation = cellTimeAggregation.getStringCellValue().toUpperCase();
                                    if (!Arrays.asList(aggregationTypes).contains(columnValueTimeAggregation)) {
                                        logger.severe(
                                                "\tIn Counters sheet " + columnValueTimeAggregation + "  is not valid Time Aggregation type in table:"
                                                        + columnValueFactTableName + "  for counter:" + columnValueCounterName);
                                        count++;
                                    }
                                    //JIRA EQEV-46761: Counter_Datascale_AVG_Aggregation
                                    if (columnValueTimeAggregation.equalsIgnoreCase("AVG")) {
                                        String[] dataspl = columnValueDataType.split(",");
                                        if (dataspl[1].contains("0")) {
                                            logger.warning("Counters sheet in table:" + columnValueFactTableName + "  for counter:"
                                                    + columnValueCounterName + " invalid data for AVG TimeAggregation");
                                            count++;
                                        }
                                    }
                                } // if cellTimeAggregation null end

                                // if cellGroupAggregation null Start

                                cellGroupAggregation = row.getCell(columnNoGroupAggregation);
                                if (cellGroupAggregation == null || cellGroupAggregation.getCellType() == Cell.CELL_TYPE_BLANK
                                		|| String.valueOf(cellGroupAggregation).isEmpty()) {
                                    logger.severe("\tIn Counters sheet in table:" + columnValueFactTableName + "  for counter:"
                                            + columnValueCounterName + "  GroupAggregation is Empty");
                                    count++;
                                } else {
                                    columnValueGroupAggregation = cellGroupAggregation.getStringCellValue().toUpperCase();
                                    if (!Arrays.asList(aggregationTypes).contains(columnValueGroupAggregation)) {
                                        logger.severe("\tIn Counters sheet " + columnValueGroupAggregation
                                                + "  is not valid Group Aggregation type in table:" + columnValueFactTableName + "  for counter:"
                                                + columnValueCounterName);
                                        count++;
                                    }
                                } // if cellGroupAggregation null end
                            }

                            // if cellUniverseObject null Start
                            cellUniverseObject = row.getCell(columnNoUniverseObject);
                            if (cellUniverseObject == null || cellUniverseObject.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellUniverseObject).isEmpty()) {
                            	columnValueUniverseObject = null;
                                logger.severe("\tIn Counters sheet in table:" + columnValueFactTableName + "  for counter:" + columnValueCounterName
                                        + "  UniverseObject is Empty");
                                count++;
                            } else {
                                columnValueUniverseObject = cellUniverseObject.getStringCellValue();
                            } // if cellUniverseObject null end

                            // if cellUniverseClass null Start
                            cellUniverseClass = row.getCell(columnNoUniverseClass);
                            if (cellUniverseClass == null || cellUniverseClass.getCellType() == Cell.CELL_TYPE_BLANK
                            		|| String.valueOf(cellUniverseClass).isEmpty()) {
                            	logger.fine("In Counters sheet in table:" + columnValueFactTableName + "  for counter:" + columnValueCounterName
                                        + "  cellUniverseClass is Empty");

                            
                            } else {
                                columnValueUniverseClass = cellUniverseClass.getStringCellValue();
                            } // if UniverseClass null end
							
                            cellCounterType = row.getCell(columnNoCounterType);
							if (!(cellCounterType == null || cellCounterType.getCellType() == Cell.CELL_TYPE_BLANK
									|| String.valueOf(cellCounterType).isEmpty())) {
								counterTypeFlag = true;
								columnValueCounterType = cellCounterType.getStringCellValue();
							}
							if(columnValueFactTableName.endsWith("_V"))
                            	vectorTablesAndCounters.add(columnValueFactTableName+ ":" +columnValueCounterName+ ":" +columnValueCounterType);
                            else
                            	scalarTableAndCounters.add(columnValueFactTableName+ ":" +columnValueCounterName);
							
                            if (!fdType.equalsIgnoreCase("CM")) {
								// if cellCounterType Name null start
								if (!(cellCounterType == null || cellCounterType.getCellType() == Cell.CELL_TYPE_BLANK
										|| String.valueOf(cellCounterType).isEmpty())) {
									if (!Arrays.asList(counterTypes).contains(columnValueCounterType)) {
										logger.severe("\tIn Counters sheet " + columnValueCounterType
												+ "  is not valid Counter type in table:" + columnValueFactTableName
												+ "  for counter:" + columnValueCounterName);
										count++;
									}
									else
									{
										 if(columnValueFactTableName.endsWith("_FLEX_DYN_V")) {
												if(!(columnValueCounterType.equals("FlexdynVector") || columnValueCounterType.equals("FlexdynComVector"))) {
													logger.severe("\tIn Counters sheet"+columnValueCounterType
																	+" is not a valid Counter type in table:"+columnValueFactTableName
																	+" for counter:"+columnValueCounterName);count++;}}
										 else if(columnValueFactTableName.endsWith("_V")) {
										if(!Arrays.asList(counterTypes).subList(2,11).contains(columnValueCounterType)) {
											logger.severe("\tIn Counters sheet"+columnValueCounterType
															+" is not a valid Counter type in table:"+columnValueFactTableName
															+" for counter:"+columnValueCounterName);count++;}}
										else if(columnValueFactTableName.endsWith("_FLEX")) {
											if(!columnValueCounterType.equals("FlexCounter")) {
												logger.severe("\tIn Counters sheet"+columnValueCounterType
																+" is not a valid Counter type in table:"+columnValueFactTableName
																+" for counter:"+columnValueCounterName);count++;}}
										else if(columnValueFactTableName.endsWith("_FLEX_DYN")) {
											if(!columnValueCounterType.equals("MultiDynCounter")) {
												logger.severe("\tIn Counters sheet"+columnValueCounterType
																+" is not a valid Counter type in table:"+columnValueFactTableName
																+" for counter:"+columnValueCounterName);count++;}}
										else if(columnValueFactTableName.endsWith("_DYN")) {
											if(!columnValueCounterType.equals("DynCounter")) {
												logger.severe("\tIn Counters sheet"+columnValueCounterType
																+" is not a valid Counter type in table:"+columnValueFactTableName
																+" for counter:"+columnValueCounterName);count++;}}
										
									}
									if (columnValueCounterType.equalsIgnoreCase("Vector")) {
										vectorCountersSet.add(columnValueFactTableName + ":" + columnValueCounterName);
									}
									tableNameCounterAndTypeSet.add(columnValueFactTableName + ":"
											+ columnValueCounterName + ":" + columnValueCounterType + ":" + columnValueCounterDescription); // EQEV-59884
								} else {
									logger.severe("\tIn Counters sheet in table:" + columnValueFactTableName
											+ "  for counter:" + columnValueCounterName + "  CounterType is Empty");
									count++;
								} // if cellCounterType Name null start
							}
                            // if cellIncludeSQL Name null start
                            cellIncludeSQL = row.getCell(columnNoIncludeSQL);
                            if (!(cellIncludeSQL == null || cellIncludeSQL.getCellType() == Cell.CELL_TYPE_BLANK
                            		|| String.valueOf(cellIncludeSQL).isEmpty())) {
                                columnValueIncludeSQL = cellIncludeSQL.getStringCellValue();
                                if (!(columnValueIncludeSQL.equalsIgnoreCase("Y"))) {
                                    logger.warning("In Counters sheet in table:" + columnValueFactTableName + "  for counter:"
                                            + columnValueCounterName + "  IncludeSQL Column value not correct");
                                    count++;
                                }
                            } else {
                                logger.warning("In Counters sheet in table:" + columnValueFactTableName + "  for counter:" + columnValueCounterName
                                        + "  IncludeSQL is Empty");
                                count++;
                            } // if cellIncludeSQL Name null end
                            
                            if(!(columnValueUniverseObject == null || columnValueUniverseObject.isEmpty())) { //EQEV-60857 @
                            	z = unvObjSet.size();  //EQEV-58209 @
                            	unvObjSet.add(columnValueFactTableName + ":" + columnValueUniverseClass + ":" + columnValueUniverseObject);
                            	if(z == unvObjSet.size())
                            		duplicateUniverseObjectList.add(columnValueFactTableName + ":" + columnValueUniverseObject);
                            }
                            
                            if(followJohnFlag) {
                            	cellFollowJohn = row.getCell(columnNoFollowJohn);
                            	if ((cellFollowJohn != null) && (cellFollowJohn.getCellType() != cellFollowJohn.CELL_TYPE_BLANK)&& (!String.valueOf(cellFollowJohn).isEmpty())) {
                            		String columnValueFollowJohn = new DataFormatter().formatCellValue(cellFollowJohn);
                            		if(!(columnValueFollowJohn.equals("0") || columnValueFollowJohn.equals("1"))) {
                            			logger.severe("In Counters sheet, FOLLOWJOHN cell value is not in 0 or 1 in table: " + columnValueFactTableName + " for counter : " +columnValueCounterName);
                            			count++;
                            		}
                            	}
                             	else {
                            		logger.severe("In Counters sheet, FOLLOWJOHN cell is empty in table: " + columnValueFactTableName + " for counter : " +columnValueCounterName);
                            		count++;
                            	}
                            }

							if (factTableNameFlag && counterNameFlag && counterTypeFlag) {
								if (columnValueFactTableName.toUpperCase().endsWith("FLEX")) {
									if (!columnValueCounterType.equalsIgnoreCase("FlexCounter")) {
										logger.severe("In Counters sheet, Fact Table Name: " + columnValueFactTableName
												+ " with Counter: " + columnValueCounterName + " has Counter Type: "
												+ columnValueCounterType + " at row:" + (row.getRowNum() + 1)
										+ " which should be a FlexCounter ");
										count++;
									}
								}
							}
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(countersSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Counters Sheet");
                            		count++;
                            	}
                            }
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.severe("\tFirst row is Empty in Counters Sheet");
                    count++;
                } // if First row
            } else {
                logger.severe("\tCounters Sheet is Empty");
                count++;
            } // if sheet
            if (!((factTablesSet.size() - factTablesBHSet.size()) == countersFactTableSet.size())) {

                HashSet<String> tempFactTablesSet = new HashSet<String>();
                tempFactTablesSet.addAll(factTablesSet);
                for (String s : countersFactTableSet) {
                    tempFactTablesSet.remove(s);
                }
                for (String s : factTablesBHSet) {
                    tempFactTablesSet.remove(s);
                }
                for (String s : factTablesSpecialSet) {
                    tempFactTablesSet.remove(s);
                }
                logger.info("\tFollowing Tables do not have single Counter in Counters Sheet");
                for (String s : tempFactTablesSet) {
                    logger.warning(s + "  Table does not have single Counter in Counters Sheet");
                    count++;
                }
            }
            if (duplicateCountersList.size() > 0) {
                logger.info("\tList of duplicate counters as follows");
                String[] tempArray;
                for (String s : duplicateCountersList) {
                    tempArray = s.split(":");
                    logger.warning("In Table:" + tempArray[0] + "  Counter:" + tempArray[1] + "  is a duplicate Counter");
                    count++;
                }
            } 
            if (duplicateUniverseObjectList.size() > 0) {   //EQEV-58209 @
                logger.info("\t List of duplicate Universe Objects for same Table as follows");
                String[] tempArray;
                for (String s : duplicateUniverseObjectList) {
                    tempArray = s.split(":");
                    logger.warning("In Table: " + tempArray[0] + " Universe Object: " + tempArray[1] + " is a duplicate Universe Object");
                    count++;
                }
            }
            //EQEV-59884
            //EQEV-114577 design patterns for column to avoid case sensitive
            String[] desc_patterns   = {"Deprecated since","Deprecated:(To be deprecated in n-8 release)",
            		"Deprecated,since", "Deprecated,Since", "Deprecated Since", "Deprecated. Since", "Deprecated. since", "Deprecated only in", "Deprecated in", 
            		"Deprecated: Since", "Deprecated:Since", "Deprecated.", "Deprecated,"};
			if (!tableNameCounterAndTypeSet.isEmpty()) {
				List<String> warningMsgSet = new ArrayList<String>();
				List<String> sortedSet = new ArrayList<String>(tableNameCounterAndTypeSet);
				Collections.sort(sortedSet);
				sortedSet.stream().forEach(data -> {
					
					String[] array = data.split(":");
					sortedSet.stream().skip(1).forEach(str -> {
			            int flagArr=0;
						String[] tempArray = str.split(":");
						if (!array[0].endsWith("_V")) {
							String vector = array[0] + "_V";
							if (vector.equals(tempArray[0]) && array[1].equals(tempArray[1])
									&& !tempArray[2].equals("CMVECTOR")) {
								for(String Patterns:desc_patterns){
									if(array[3].contains(Patterns)) {
										flagArr++;
										break;
									}
								}
								if(flagArr==0) {
								warningMsgSet.add("In Table: " + tempArray[0] + " Counter: " + tempArray[1]
										+ " Counter Type: " + tempArray[2] + ". Counter Type should be CMVECTOR");
							}
							}
						}
					});
				});
				for(String s: warningMsgSet) {
					logger.warning(s);
					count++;
				}
			}
			
			
			/*//EQEV-114577 we are commenting the block of code as the requirement is no longer needed.
			 * for(String s : vectorTablesAndCounters) { String [] vectorData =
			 * s.split("[:]"); String scalarTable = vectorData[0].substring(0,
			 * vectorData[0].length()-2);
			 * 
			 * if(scalarTableAndCounters.contains(scalarTable+ ":" + vectorData[1]) &&
			 * vectorData.length>2) {
			 * logger.warning(" counter type for table "+vectorData[0]+
			 * " with counter "+vectorData[1]+" is not empty"); count++; } }
			 */
            if (count == 0) {
                logger.info("\tCounters Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Counters Sheet:" + count);
                errorCountMap.put("Counters Sheet", new Integer(count));
            }

            logger.info("\tEnd of Counters sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in counters Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Keys sheet
         */
        HashMap<String, String> facttableBHkeys = new HashMap<>();
        HashSet<String> facttableBH = new HashSet<>();
        try {
            logger.info("\tIn Keys Sheet");
            count = 0;
            if (keysSheet != null) {
                XSSFCell cellFactTableName = null, cellKeyName = null, cellKeyDescription = null, cellDataType = null, cellDuplicateConstraint = null,
                        cellNullable = null, cellIQIndex = null, cellUniverseObject = null, cellElementColumn = null, cellIncludeSQL = null;
                String columnNameFactTableName = "Fact Table Name", columnNameKeyName = "Key Name", columnNameKeyDescription = "Key Description",
                        columnNameDataType = "Data type", columnNameDuplicateConstraint = "Duplicate Constraint", columnNameNullable = "Nullable",
                        columnNameIQIndex = "IQ Index", columnNameUniverseObject = "Universe object", columnNameElementColumn = "Element Column",
                        columnNameIncludeSQL = "IncludeSQL";
                String[] IgnoredkeyNames = {"RECORDTYPE","IDENTIFIER"};
                String columnValueFactTableName = null, columnValueKeyName = null, columnValueKeyDescription = null, columnValueDataType = null,
                        columnValueDuplicateConstraint = null, columnValueNullable = null, columnValueIQIndex = null,
                        columnValueUniverseObject = null, columnValueIncludeSQL = null, columnValueElementColumn = null;
                int columnNoFactTableName, columnNoKeyName, columnNoKeyDescription, columnNoDataType, columnNoDuplicateConstraint, columnNoNullable,
                        columnNoIQIndex, columnNoUniverseObject, columnNoElementColumn, columnNoIncludeSQL;

                rowIterator = keysSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoFactTableName = getColumnNo(firstRow, columnNameFactTableName);
                    columnNoKeyName = getColumnNo(firstRow, columnNameKeyName);
                    columnNoKeyDescription = getColumnNo(firstRow, columnNameKeyDescription);
                    columnNoDataType = getColumnNo(firstRow, columnNameDataType);
                    columnNoDuplicateConstraint = getColumnNo(firstRow, columnNameDuplicateConstraint);
                    columnNoNullable = getColumnNo(firstRow, columnNameNullable);
                    columnNoIQIndex = getColumnNo(firstRow, columnNameIQIndex);
                    columnNoUniverseObject = getColumnNo(firstRow, columnNameUniverseObject);
                    columnNoElementColumn = getColumnNo(firstRow, columnNameElementColumn);
                    columnNoIncludeSQL = getColumnNo(firstRow, columnNameIncludeSQL);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            // if cellFactTableName null begin
                            cellFactTableName = row.getCell(columnNoFactTableName);
                            if (cellFactTableName == null || cellFactTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Keys Sheet FactTable Name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueFactTableName = cellFactTableName.getStringCellValue();
                                /*
                                 * if(columnValueFactTableName.length()!=columnValueFactTableName.trim().length( )) {
                                 * logger.severe("\tIn Keys Sheet FactTableName column is having spaces at row:" +(row.getRowNum()+1));count++; }
                                 */
                                if (!factTablesSet.contains(columnValueFactTableName)) {
                                    logger.warning("FactTableName:" + columnValueFactTableName
                                            + "  is  present in Keys sheet.but it is not present in factTablesSet");
                                    count++;
                                }
                                keysFactTableSet.add(columnValueFactTableName);
                                // JIRA EQEV-46761 fix: BusyHour_Table_Name
                                Iterator fBH = factTablesBHSet.iterator();
                                while (fBH.hasNext()) {
                                    String BH = fBH.next().toString();
                                    if (!BH.endsWith("BH")) {
                                        logger.warning("Busy Hour Table name " + columnValueFactTableName + " is not end with BH");
                                        count++;
                                    }
                                }
                                /*
                                 * if(columnValueFactTableName.contains(columnNameObjectBHs)) { if(columnValueFactTableName.endsWith("BH")) {
                                 * logger.warning("Busy Hour Table name " +columnValueFactTableName+" is not end with BH");count++;
                                 * facttableBH.add(columnValueFactTableName); } }
                                 */

                            } // if cellFactTableName null end

                            // if cellKeyName null begin
                            cellKeyName = row.getCell(columnNoKeyName);
                            if (cellKeyName == null || cellKeyName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Keys Sheet Key Name is Empty in Table:" + columnValueFactTableName + "  at row:"
                                        + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueKeyName = cellKeyName.getStringCellValue();
                                //logger.info(columnValueFactTableName);
                                if(KeysMap.containsKey(columnValueFactTableName))
                                KeysMap.get(columnValueFactTableName).add(columnValueKeyName);
                                else
                                {
                                	ArrayList<String> Keys = new ArrayList<String>();
                                	Keys.add(columnValueKeyName);
                                	KeysMap.put(columnValueFactTableName, Keys);
                                }
                                //logger.info(KeysMap.toString());
                                if (!isValidName(columnValueKeyName)) {
                                    logger.severe("\tIn Keys Sheet KeyName column value is not valid in Table:" + columnValueFactTableName
                                            + "  Key : " + columnValueKeyName);
                                    count++;
                                }
                                
                                if (columnValueFactTableName.contains("BULK_CM")) { //73352
                                    
                                	if(tableandKeyName.containsKey(columnValueFactTableName))
                                	{
                                		HashSet<String> valueKeyName = tableandKeyName.get(columnValueFactTableName);
                                		valueKeyName.add(columnValueKeyName);
                                		tableandKeyName.put(columnValueFactTableName,valueKeyName);
                                	}
                                	else
                                	{
                                		HashSet<String> valueKeyName = new HashSet<>();
                                		valueKeyName.add(columnValueKeyName);
                                		tableandKeyName.put(columnValueFactTableName,valueKeyName);
                                	}
                                }
                                
                                /*
                                 * if(columnValueKeyName.length()!=columnValueKeyName.trim().length()) {
                                 * logger.severe("\tIn Keys Sheet Key Name is having spaces in Table\t"
                                 * +columnValueFactTableName+"  Key : "+columnValueKeyName);count++; }
                                 */

                                if (!factTablesBHSet.contains(columnValueFactTableName)) {
                                    z = keysSet.size();
                                    keysSet.add(columnValueFactTableName + ":" + columnValueKeyName);

                                    if (z == keysSet.size()) {
                                        duplicateKeysList.add(columnValueFactTableName + ":" + columnValueKeyName);
                                    } else {
                                        if (isKeyWord(columnValueKeyName, dbKeyWordsSet)) {
                                            logger.severe("\tIn Keys Sheet in table:" + columnValueFactTableName + "  for key:" + columnValueKeyName
                                                    + "  is a keyword in DataBase");
                                            count++;
                                        }
                                    }
                                } else if (factTablesBHSet.contains(columnValueFactTableName)) {
                                    keysBHSet.add(columnValueFactTableName + ":" + columnValueKeyName);
                                }
                                /*
                                 * if (factTablesBHSet.contains(columnValueFactTableName)) { keysSetBH.add(columnValueKeyName); }
                                 */			
                            } // if cellKeyName null end

                            // if cellKeyDescription null begin
                            // JIRA EQEV-46761: Check_Description
                            cellKeyDescription = row.getCell(columnNoKeyDescription);
                            if (cellKeyDescription == null || cellKeyDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Keys Sheet Key Description is Empty in Table\t" + columnValueFactTableName + "  for Key:"
                                        + columnValueKeyName);
                                count++;
                            } else {
                                columnValueKeyDescription = cellKeyDescription.getStringCellValue();
                                // JIRA EQEV-56248/EQEV-59122: Wild card Description_Check removed
                            	} // if cellKeyDescription null end

                            // if cellDataType null begin
                            cellDataType = row.getCell(columnNoDataType);
                            if (!(cellDataType == null || cellDataType.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueDataType = cellDataType.getStringCellValue();
                                if (!(isValidDataType(columnValueDataType))) {
                                    logger.severe("\tIn Keys Sheet in table:" + columnValueFactTableName + "  for key:" + columnValueKeyName + "  "
                                            + columnValueDataType + "  is not a valid DataType");
                                    count++;
								} else {
									if (factTablesBHSet.contains(columnValueFactTableName)) {
										bhTableDatatypeAndBhCheck.add(columnValueFactTableName + ":" + columnValueKeyName
												+ ":" + columnValueDataType);
									} else {
										keyDatatypeAndBhCheck.add(columnValueFactTableName + ":" + columnValueKeyName
												+ ":" + columnValueDataType);
									}
								}
                            } else {
                                logger.severe("\tIn Keys Sheet DataType is Empty in Table\t" + columnValueFactTableName + "  at row:"
                                        + (row.getRowNum() + 1));
                                count++;
                            } // if cellDataType null end

                            // if cellDuplicateConstraint null begin
                            cellDuplicateConstraint = row.getCell(columnNoDuplicateConstraint);
                            if (cellDuplicateConstraint == null || cellDuplicateConstraint.getCellType() == Cell.CELL_TYPE_BLANK) {
                            	if((columnValueKeyName.equals("OSS_ID") || columnValueKeyName.equals("DCVECTOR_INDEX"))) //Duplicate Constraint check for OSS_ID and DCVECTOR_INDEX
                            	{
                            		logger.severe("\tIn Keys Sheet, The Duplicate Constraint for the "+columnValueKeyName+" is not Enabled for Fact Table "+cellFactTableName.getStringCellValue());
                                	count++;
                            	}
                            	else
                                logger.fine("In Keys Sheet Duplicate Constraint is Empty in Table\t" + columnValueFactTableName + "  for key:"
                                        + columnValueKeyName);
                            } else {
                                columnValueDuplicateConstraint = cellDuplicateConstraint.getStringCellValue();
                                if (!columnValueDuplicateConstraint.equalsIgnoreCase("Y") && columnValueDuplicateConstraint.trim().length() > 0) {
                                    logger.severe("\tIn Keys Sheet in table:" + columnValueFactTableName + "  for key:" + columnValueKeyName
                                            + "  DuplicateConstraint is having value other than 'Y'");
                                    count++;
                                }
                                else
                                {
                                	if(CommonKeysMap.containsKey(columnValueFactTableName))
                                		CommonKeysMap.get(columnValueFactTableName).add(columnValueKeyName);
                                    else
                                    {
                                       	ArrayList<String> Keys1 = new ArrayList<String>();
                                       	Keys1.add(columnValueKeyName);
                                       	CommonKeysMap.put(columnValueFactTableName, Keys1);
                                    }
                                }
                            } // if cellDuplicateConstraint null end

                            // if cellNullable null begin
                            cellNullable = row.getCell(columnNoNullable);
                            if (cellNullable == null || cellNullable.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Keys Sheet Nullable Column is Empty in Table\t" + columnValueFactTableName + "  for key:"
                                        + columnValueKeyName);
                                count++;
                            } else {
                                columnValueNullable = cellNullable.getStringCellValue();
                                if (!columnValueNullable.equalsIgnoreCase("Y") && columnValueNullable.trim().length() > 0) {
                                    logger.warning("In Keys Sheet in table:" + columnValueFactTableName + "  for key:" + columnValueKeyName
                                            + "  is not having Nullable value Y");
                                    count++;
                                }
                            } // if cellNullable null end

                            // if cellIQIndex null begin
                            cellIQIndex = row.getCell(columnNoIQIndex);
                            if (cellIQIndex == null || cellIQIndex.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Keys Sheet IQIndex is Empty in Table\t" + columnValueFactTableName + "  for key:" + columnValueKeyName);
                                count++;
                            } else {
                                columnValueIQIndex = cellIQIndex.getStringCellValue();
                            } // if cellIQIndex null end

                            // if UniverseObject null begin
                            if(!Arrays.asList(IgnoredkeyNames).contains(columnValueKeyName)) { //EQEV-61226 @
                            cellUniverseObject = row.getCell(columnNoUniverseObject);
                            if (cellUniverseObject == null || cellUniverseObject.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Keys Sheet UniverseObject is Empty in Table\t" + columnValueFactTableName + "  for key:"
                                        + columnValueKeyName);
                                count++;
                            } else {
                                columnValueUniverseObject = cellUniverseObject.getStringCellValue();
                                if(cellUniverseObject.getCellType() == Cell.CELL_TYPE_STRING && columnValueUniverseObject.trim().length()==0) {
                                    logger.warning("In Keys Sheet UniverseObject is Empty in Table\t" + columnValueFactTableName + "  for key:"
                                            + columnValueKeyName);
                                    count++;
                                	}
                            	} 
                            }// if cellKeyDescription null end

                            // if cellElementColumn null begin
                            cellElementColumn = row.getCell(columnNoElementColumn);
                            if (!(cellElementColumn == null || cellElementColumn.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueElementColumn = cellElementColumn.getStringCellValue();
                                if (columnValueElementColumn.equalsIgnoreCase("Y")) {
                                    keyElementColumnSet.add(columnValueKeyName);
                                    keyElementColumnTablesSet.add(columnValueFactTableName);
                                }
                            } // if cellElementColumn null end

                            // if cellIncludeSQL null begin
                            cellIncludeSQL = row.getCell(columnNoIncludeSQL);
                            if (!(cellIncludeSQL == null || cellIncludeSQL.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueIncludeSQL = cellIncludeSQL.getStringCellValue();
                                if (!(columnValueIncludeSQL.equalsIgnoreCase("Y")) && columnValueIncludeSQL.trim().length() > 0) {
                                    logger.warning("In Keys Sheet IncludeSQL value is not Y In table:" + columnValueFactTableName + "  for key:"
                                            + columnValueKeyName);
                                    count++;
                                }
                            } else {
                                logger.warning("In Keys Sheet IncludeSQL is Empty in Table\t" + columnValueFactTableName + "  for key:"
                                        + columnValueKeyName);
                                count++;
                            } // if cellIncludeSQL null end
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(keysSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Keys Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in keys Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("keys sheet is Empty");
                count++;
            } // if sheet
            
          //73352
			ArrayList<String> defaultKeyNames = new ArrayList<String>(Arrays.asList("ELEMENT", "ELEMENTTYPE",
					"ELEMENTPARENT", "SN", "MOID", "OSS_ID", "VSDATAFORMATVERSION"));

			for (Map.Entry<String, HashSet<String>> entry : tableandKeyName.entrySet()) {
				ArrayList<String> entryList = new ArrayList<String>(entry.getValue());
				ArrayList<String> missingDefaultKeyNames = new ArrayList<String>();
				for (String key : defaultKeyNames) {
					if (!entryList.contains(key)) {
						missingDefaultKeyNames.add(key);
					}
				}
				if (!missingDefaultKeyNames.isEmpty()) {
					logger.severe("Default Key: " + String.join(", ", missingDefaultKeyNames)
							+ " missing in Table: " + entry.getKey());
					count++;
				}
			}
			
            if (!(factTablesSet.size() == keysFactTableSet.size())) {

                HashSet<String> tempFactTablesSet = new HashSet<String>();
                tempFactTablesSet.addAll(factTablesSet);
                for (String s : keysFactTableSet) {
                    tempFactTablesSet.remove(s);
                }
                logger.info("\tFollowing Tables do not have single key in Keys Sheet");
                for (String s : tempFactTablesSet) {
                    logger.warning(s + "  Table does not have single key in Keys Sheet");
                    count++;
                }
            }
            for (String s : factTablesVectorSet) {
                if (!keysSet.contains(s + ":DCVECTOR_INDEX")) {
                    logger.warning(s + "  Vector table is not having DCVECTOR_INDEX key in key list");
                    count++;
                }
            }
            if (keyElementColumnSet.size() > 1) {
                logger.warning(
                        "In Keys Sheet ElementColumn should have only one key as Element column value.But the following are mentioned as ElementColumn value"
                                + keyElementColumnSet.toString());
                count++;
            }
            if (duplicateKeysList.size() > 0) {
                logger.info("\tList of duplicate keys as follows");
                String[] tempArray;
                for (String s : duplicateKeysList) {
                    tempArray = s.split(":");
                    logger.warning("In Table:" + tempArray[0] + "  Key:" + tempArray[1] + "  is a duplicate Key");
                    count++;
                }
            }
            List<String> factTableAndObjectBhList = new ArrayList<>(factTableAndObjectBh);
            List<String> bhTableDatatypeAndBhCheckList = new ArrayList<>(bhTableDatatypeAndBhCheck);
            List<String> keyDatatypeAndBhCheckList = new ArrayList<>(keyDatatypeAndBhCheck);
            List<String> keyNotExistsInTable = new ArrayList<>();
            Collections.sort(factTableAndObjectBhList);
            Collections.sort(bhTableDatatypeAndBhCheckList);
            Collections.sort(keyDatatypeAndBhCheckList);
            
			for (String factTableSheetValues : factTableAndObjectBhList) {
				String[] factValues = factTableSheetValues.split(":");
				for (String bhTable : bhTableDatatypeAndBhCheckList) {
					boolean keyFlag = false;
					String[] bhTableValues = bhTable.split(":");
					if (bhTableValues[0].endsWith("_" + factValues[1] + "BH")) {
						for (String keySheetValues : keyDatatypeAndBhCheckList) {
							String[] keyValues = keySheetValues.split(":");
							if (factValues[0].equals(keyValues[0])) {
								if (bhTableValues[1].equals(keyValues[1])) {
									keyFlag = true;
									if (!bhTableValues[2].equals(keyValues[2])) {
										logger.warning("In Keys sheet table "+ factValues[0] +" has "+bhTableValues[0]+" support. Data type is mismatched in BH table for Key: " + keyValues[1]);
										count++;
									}
									break;
								}
							}
						}
						if (!keyFlag) {
							keyNotExistsInTable.add("In Keys sheet table: " + factValues[0] + " has "
									+ bhTableValues[0]  + " support. Key: "+bhTableValues[1] +" is not present in table "+ factValues[0] );
						}
					}
				}
			}
			for(String warnmsg : keyNotExistsInTable ) {
				logger.warning(warnmsg);
				count++;
			}
            
            if (count == 0) {
                logger.info("\tKeys Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Keys Sheet:" + count);
                errorCountMap.put("Keys Sheet", new Integer(count));
            }
            logger.info("\tEnd of Keys Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in keys Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Topology tables
         */
        try {
            logger.info("\tIn Topology Tables sheet");
            count = 0;
            if (topologyTablesSheet != null) {
                XSSFCell cellTopologyTableName = null, cellTopologyTableDescription = null, cellSourceType = null, cellTagName = null;
                String columnNameTopologyTableName = "Topology Table Name", columnNameTopologyTableDescription = "Topology Table Description",
                        columnNameSourceType = "Source Type";
                String columnValueTopologyTableName = null, columnValueTopologyTableDescription = null, columnValueSourceType = null;
                String columnValueTagName = null;
                int columnNoTopologyTableName, columnNoTopologyTableDescription, columnNoSourceType, columnNoTagName[];

                rowIterator = topologyTablesSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoTopologyTableName = getColumnNo(firstRow, columnNameTopologyTableName);
                    columnNoTopologyTableDescription = getColumnNo(firstRow, columnNameTopologyTableDescription);
                    columnNoSourceType = getColumnNo(firstRow, columnNameSourceType);
                    columnNoTagName = new int[measurementInterfaceSet.size()];
                    z = 0;
					for (String s : measurementInterfaceSet) {
						int columnNumber = getColumnNo(firstRow, s);
						if (columnNumber >= 0) {
							columnNoTagName[z++] = columnNumber;
						} else {
							logger.warning("Parser Name: "+s+" is not present in Topology Tables sheet" );
							count++;
						}
					}

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellTopologyTableName = row.getCell(columnNoTopologyTableName);
                            if (cellTopologyTableName == null || cellTopologyTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Topology Tables sheet table name is Empty at row:" + (row.getRowNum() + 1) + "  in"
                                        + columnNameTopologyTableName + "  column");
                                count++;
                            } else {
                                columnValueTopologyTableName = cellTopologyTableName.getStringCellValue();
                                if (!isValidName(columnValueTopologyTableName)) {
                                    logger.severe("\tIn Topology Tables sheet TopologyTable Name:" + columnValueTopologyTableName
                                            + "  is not valid Table Name at row:" + (row.getRowNum() + 1));
                                    count++;
                                }
                                
                              //EQEV-113876
                				if(techPackName.equals("DIM_E_UTRAN"))
                				{
                					if( columnValueTopologyTableName.startsWith("DIM_E_RAN")|| columnValueTopologyTableName.startsWith("DIM_E_RBS") || (columnValueTopologyTableName.startsWith(techPackName)))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}
     							
     					else if(techPackName.equals("DC_E_WLE"))
                				{
                					if(columnValueTopologyTableName.startsWith("DIM_E_WLE") || (columnValueTopologyTableName.startsWith(techPackName)))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}		
     							
     					else if(techPackName.equals("DC_E_LLE"))
                				{
                					if(columnValueTopologyTableName.startsWith("DIM_E_LLE") || (columnValueTopologyTableName.startsWith(techPackName)))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}		
     							
     					else if(techPackName.equals("DC_E_IPRAN"))
                				{
                					if(columnValueTopologyTableName.startsWith("DIM_E_IPRAN") || (columnValueTopologyTableName.startsWith(techPackName)))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}
     							
     					else if(techPackName.equals("DC_E_FFAXW"))
                				{
                					
                					if(columnValueTopologyTableName.startsWith("DIM_E_FFAXW") || (columnValueTopologyTableName.startsWith(techPackName)))
                					{
                					}
                					else
                					{
                						logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
                						count++;
                					}
                				}
     					else if(techPackName.startsWith("DC"))
     					{
     						if(!columnValueTopologyTableName.startsWith("DIM"))
     						{
     					    	logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with DIM");
     					    	count++;
        					}
     					}
     					else
     					{
     						if(!columnValueTopologyTableName.startsWith(techPackName))
     						{
     					    	logger.severe("\t fail:"+" " + columnValueTopologyTableName + ": "+ "TableName doesn't start with TP Name or input value");
     					    	count++;
        					}
     						
     					}

                                /*
                                 * if(columnValueTopologyTableName.length()!=columnValueTopologyTableName.trim() .length()){ logger.
                                 * severe("\tIn Topology Tables sheet TopologyTableName is having spaces at row:" +(row.getRowNum()+1));count++; }
                                 */
                                z = topologyTablesSet.size();
                                topologyTablesSet.add(columnValueTopologyTableName);
                                //abcde
                                if (z == topologyTablesSet.size()) {
                                    duplicateTopologyTablesList.add(columnValueTopologyTableName);
                                }
                            }
                            cellTopologyTableDescription = row.getCell(columnNoTopologyTableDescription);
                            if (cellTopologyTableDescription == null || cellTopologyTableDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Topology Tables sheet Topology Table Description is Empty in Table:" + columnValueTopologyTableName);
                                count++;
                            } else {
                                columnValueTopologyTableDescription = cellTopologyTableDescription.getStringCellValue();
                                // JIRA EQEV-56248/EQEV-59122: Wild card Description_Check removed
                                // logger.fine(columnValueTopologyTableDescription);
                            	}
                            cellSourceType = row.getCell(columnNoSourceType);
                            XSSFCell cellTopologyTableName1 = row.getCell(columnNoTopologyTableName);

                            if (cellSourceType == null || cellSourceType.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Topology Tables sheet Source Type is Empty in Table:" + columnValueTopologyTableName);
                                count++;
                            } else {
                                columnValueSourceType = cellSourceType.getStringCellValue();
                                String topoTable = cellTopologyTableName1.getStringCellValue();
                                topologyTablesSet1.add(topoTable+":"+columnValueSourceType);
                              //  System.out.println("*************************************");
                               // System.out.println("topologyTablesSet1 :"+topologyTablesSet1.toString());
                               // System.out.println("*************************************");
                            }

                            flag = 0;
                            for (int p = 0; p < measurementInterfaceSet.size(); p++) {
                                cellTagName = row.getCell(columnNoTagName[p]);
                                if (!(cellTagName == null || cellTagName.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                    flag++;
                                    columnValueTagName = cellTagName.getStringCellValue();
                                    if (columnValueTopologyTableName.trim().length() > 0) {
                                        topologyTableTransformationsSet.add(columnValueTopologyTableName.trim());  //EQEV-58207 @
                                    }
                                }
                            }
                            //EQEV-114577 for 3rd action point
                            if (flag == 0 && !(columnValueSourceType.equalsIgnoreCase("Static"))) {
                                logger.warning("In Topology Tables sheet Table:" + columnValueTopologyTableName
                                        + "  is not having tag support in any parser");
                                count++;
                            }
                            
                            if(row.getRowNum()==1) {  //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(topologyTablesSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Topology Tables Sheet");
                            		count++;
                            	}
                            }
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Topology Tables Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Topology Tables sheet is Empty");
                count++;
            } // if sheet
            if (duplicateTopologyTablesList.size() > 0) {
                logger.info("\tList of duplicate topology tables as follows");
                for (String s : duplicateTopologyTablesList) {
                    logger.warning("In Topology Tables sheet Table:" + s + "  is a duplicate Topology Table");
                    count++;
                }
            }
            if (count == 0) {
                logger.info("\tTopology Tables Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Topology Tables Sheet:" + count);
                errorCountMap.put("Topology Tables Sheet", new Integer(count));
            }
            logger.info("\tEnd of Topology Tables Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Topology Tables Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Topology keys Sheet
         */
        try {
            logger.info("\tIn Topology Keys sheet");
            count = 0;
            if (topologyKeysSheet != null) {

                XSSFCell cellTopologyTableName = null, cellKeyName = null, cellKeyDescription = null, cellDataType = null,
                        cellDuplicateConstraint = null, cellNullable = null, cellUniverseObject = null, cellUniverseClass = null,
                        cellUniverseCondition = null, cellIncludeSQL = null, cellIncludeUpdate = null;
                String columnNameTopologyTableName = "Topology Table name", columnNameKeyName = "Key Name",
                        columnNameKeyDescription = "Key Description", columnNameDataType = "Data type",
                        columnNameDuplicateConstraint = "Duplicate Constraint", columnNameNullable = "Nullable",
                        columnNameUniverseClass = "Universe Class", columnNameUniverseObject = "Universe Object",
                        columnNameUniverseCondition = "Universe Condition", columnNameIncludeSQL = "IncludeSQL",
                        columnNameIncludeUpdate = "Include Update";
                String columnValueTopologyTableName = null, columnValueKeyName = null, columnValueKeyDescription = null, columnValueDataType = null,
                        columnValueDuplicateConstraint = null, columnValueNullable = null, columnValueUniverseObject = null,
                        columnValueUniverseClass = null, columnValueUniverseCondition = null, columnValueIncludeSQL = null,
                        columnValueIncludeUpdate = null;
                int columnNoTopologyTableName, columnNoKeyName, columnNoKeyDescription, columnNoDataType, columnNoDuplicateConstraint,
                        columnNoNullable, columnNoUniverseObject, columnNoUniverseClass, columnNoUniverseCondition, columnNoIncludeSQL,
                        columnNoIncludeUpdate;

                rowIterator = topologyKeysSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoTopologyTableName = getColumnNo(firstRow, columnNameTopologyTableName);
                    columnNoKeyName = getColumnNo(firstRow, columnNameKeyName);
                    columnNoKeyDescription = getColumnNo(firstRow, columnNameKeyDescription);
                    columnNoDataType = getColumnNo(firstRow, columnNameDataType);
                    columnNoDuplicateConstraint = getColumnNo(firstRow, columnNameDuplicateConstraint);
                    columnNoNullable = getColumnNo(firstRow, columnNameNullable);
                    columnNoUniverseObject = getColumnNo(firstRow, columnNameUniverseObject);
                    columnNoUniverseClass = getColumnNo(firstRow, columnNameUniverseClass);
                    columnNoUniverseCondition = getColumnNo(firstRow, columnNameUniverseCondition);
                    columnNoIncludeUpdate = getColumnNo(firstRow, columnNameIncludeUpdate);
                    columnNoIncludeSQL = getColumnNo(firstRow, columnNameIncludeSQL);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellTopologyTableName = row.getCell(columnNoTopologyTableName);
                            if (cellTopologyTableName == null || cellTopologyTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Topology Keys sheet Topology Table Name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTopologyTableName = cellTopologyTableName.getStringCellValue();
                                /*
                                 * if(columnValueTopologyTableName.length()!=columnValueTopologyTableName.trim() .length()){ logger.
                                 * severe("\tIn Topology Keys sheet TopologyTableName is having spaces at row:"+ (row.getRowNum()+1));count++; }
                                 */
                                if (!topologyTablesSet.contains(columnValueTopologyTableName)) {
                                    logger.warning("In TopologyKeys sheet  TopologyTableName:" + columnValueTopologyTableName
                                            + "  is present in TopologyKeys sheet but not present in TopologyTablesSet");
                                    count++;
                                }
                                topologyKeysTopologyTableSet.add(columnValueTopologyTableName);
                            }

                            cellKeyName = row.getCell(columnNoKeyName);
                            if (cellKeyName == null || cellKeyName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Topology Keys sheet Topology Key Name is Empty in Table\t" + columnValueTopologyTableName
                                        + "  at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueKeyName = cellKeyName.getStringCellValue();
                                if (!isValidName(columnValueKeyName)) {
                                    logger.severe("\tIn Topology Keys sheet Topology Key Name column value is not valid in Table:"
                                            + columnValueTopologyTableName + "  Key : " + columnValueKeyName);
                                    count++;

                                }
                                /*
                                 * if(columnValueKeyName.length()!=columnValueKeyName.trim().length()){ logger.
                                 * severe("\tIn Topology Keys sheet Topology Key Name is having spaces in Table\t"
                                 * +columnValueTopologyTableName+"  at row:"+(row.getRowNum()+1));count++; }
                                 */
                                z = topologyKeysSet.size();
                                topologyKeysSet.add(columnValueTopologyTableName + ":" + columnValueKeyName);
                                if (z == topologyKeysSet.size()) {
                                    duplicateTopologyKeysList.add(columnValueTopologyTableName + ":" + columnValueKeyName);
                                } else {
                                    if (isKeyWord(columnValueKeyName, dbKeyWordsSet)) {
                                        logger.severe("\tIn Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:"
                                                + columnValueKeyName + "  is a keyword in DataBase");
                                        count++;
                                    }
                                }
                            }

                            // if cellKeyDescription null begin
                            cellKeyDescription = row.getCell(columnNoKeyDescription);
                            if (cellKeyDescription == null || cellKeyDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Topology Keys sheet Key Description is Empty in Table\t" + columnValueTopologyTableName
                                        + "  for Key:" + columnValueKeyName);
                                count++;
                            } else {
                                columnValueKeyDescription = cellKeyDescription.getStringCellValue();
                                // JIRA EQEV-56248/EQEV-59122: Wild card Description_Check removed
                            	} // if cellKeyDescription null end

                            cellDataType = row.getCell(columnNoDataType);
                            if (!(cellDataType == null || cellDataType.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueDataType = cellDataType.getStringCellValue();
                                if (!(isValidDataType(columnValueDataType))) {
                                    logger.severe("\tIn Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:"
                                            + columnValueKeyName + "  " + columnValueDataType + "  is not a valid DataType");
                                    count++;
                                }
                            } else {
                                logger.severe("\tIn Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "DataType is Empty");
                                count++;
                            }

                            // if cellDuplicateConstraint null begin
                            cellDuplicateConstraint = row.getCell(columnNoDuplicateConstraint);
                            if (cellDuplicateConstraint == null || cellDuplicateConstraint.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Topology Keys sheet DuplicateConstraint is Empty in Table\t" + columnValueTopologyTableName
                                        + "  for key:" + columnValueKeyName);
                                //count++;
                            } else {
                                columnValueDuplicateConstraint = cellDuplicateConstraint.getStringCellValue();
                                if (!columnValueDuplicateConstraint.equalsIgnoreCase("Y") && columnValueDuplicateConstraint.trim().length() > 0) {
                                    logger.warning("In Topology Keys sheet DuplicateConstraint is having value other than 'Y' In table:"
                                            + columnValueTopologyTableName + "  for key:" + columnValueKeyName);
                                    count++;
                                }
                            } // if cellDuplicateConstraint null end

                            // if cellNullable null begin
                            cellNullable = row.getCell(columnNoNullable);
                            if (cellNullable == null || cellNullable.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Topology Keys sheet Nullable Column is Empty in Table\t" + columnValueTopologyTableName
                                        + "  for key:" + columnValueKeyName);
                                count++;
                            } else {
                                columnValueNullable = cellNullable.getStringCellValue();
                                if (!columnValueNullable.equalsIgnoreCase("Y") && columnValueNullable.trim().length() > 0) {
                                    logger.warning("In Topology Keys sheet Nullable Column is having value other than 'Y'In table:"
                                            + columnValueTopologyTableName + "  for key:" + columnValueKeyName);
                                    count++;
                                }
                            } // if cellNullable null end

                            // if cellUniverseObject null Start
                            cellUniverseObject = row.getCell(columnNoUniverseObject);
                            if (cellUniverseObject == null || cellUniverseObject.getCellType() == Cell.CELL_TYPE_BLANK) {
                            	
                            	if(techPackName.startsWith("DC")) { //123834
                            	if(!(techPackName.equals("DC_E_FFAX") || techPackName.equals("DC_E_FFAXW") || techPackName.equals("DC_E_LLE") || techPackName.equals("DC_E_WLE"))) {
                                logger.warning("In Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "  UniverseObject Column is Empty");
                                count++;
                                }
                            	}
                            } else {
                                columnValueUniverseObject = cellUniverseObject.getStringCellValue();
                            } // if cellUniverseObject null end

                            // if cellUniverseClass null Start
                            cellUniverseClass = row.getCell(columnNoUniverseClass);
                            if (cellUniverseClass == null || cellUniverseClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("In Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "  cellUniverseClass Column is Empty");
                            } else {
                                columnValueUniverseClass = cellUniverseClass.getStringCellValue();
                            } // if UniverseClass null end

                            // if cellUniverseCondition null Start
                            cellUniverseCondition = row.getCell(columnNoUniverseCondition);
                            if (cellUniverseCondition == null || cellUniverseCondition.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("In Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "  cellUniverseCondition Column is Empty");
                            } else {
                                columnValueUniverseCondition = cellUniverseCondition.getStringCellValue();
                            } // if UniverseConditionnull end

                            cellIncludeSQL = row.getCell(columnNoIncludeSQL);
                            if (!(cellIncludeSQL == null || cellIncludeSQL.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueIncludeSQL = cellIncludeSQL.getStringCellValue();
                                if (!(columnValueIncludeSQL.equalsIgnoreCase("Y")) && columnValueIncludeSQL.trim().length() > 0) {
                                    logger.warning("In Topology Keys sheet IncludeSQL is having value other than Y in table:"
                                            + columnValueTopologyTableName + "  for key:" + columnValueKeyName);
                                    count++;
                                }
                            } else {
                                logger.warning("In Topology Keys sheet In table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "  IncludeSQL Column is Empty ");
                                count++;
                            }

                            cellIncludeUpdate = row.getCell(columnNoIncludeUpdate);
                            if (!(cellIncludeUpdate == null || cellIncludeUpdate.getCellType() == Cell.CELL_TYPE_BLANK)) {
                                columnValueIncludeUpdate = cellIncludeUpdate.getStringCellValue();
                                if (!columnValueIncludeUpdate.equalsIgnoreCase("Y") && columnValueIncludeUpdate.trim().length() > 0) {
                                    logger.warning("In Topology Keys sheet IncludeUpdate is having value other than Y in table:"
                                            + columnValueTopologyTableName + "  for key:" + columnValueKeyName);
                                    count++;
                                }
                            } else {
                                logger.warning("In Topology Keys sheet in table:" + columnValueTopologyTableName + "  for key:" + columnValueKeyName
                                        + "IncludeUpdate Column is Empty");
                                count++;
                            }
                            
                            if(row.getRowNum()==1) {  //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(topologyKeysSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Topology Keys Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Topology Keys Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Topology Keys sheet is Empty");
                count++;
            } // if sheet
            if (!(topologyTablesSet.size() == topologyKeysTopologyTableSet.size())) {

                HashSet<String> temptopologyTablesSet = new HashSet<String>();
                temptopologyTablesSet.addAll(topologyTablesSet);
                for (String s : topologyKeysTopologyTableSet) {
                    topologyKeysTopologyTableSet.remove(s);
                }
                logger.info("\tFollowing Tables do not have single key in Topology Keys Sheet");
                for (String s : temptopologyTablesSet) {
                    logger.warning("In Topology Keys sheet Topology Table:" + s + "  is not having single Topology key");
                    count++;
                }
            }
            if (duplicateTopologyKeysList.size() > 0) {
                logger.info("\tDuplicate Topology Key List as follows");
                String[] tempArray;
                for (String s : duplicateTopologyKeysList) {
                    tempArray = s.split(":");
                    logger.warning(
                            "In Topology Keys sheet in Table:" + tempArray[0] + "  Topology Key:" + tempArray[0] + "  is a duplicate topology key");
                    count++;
                }
            }
            if (count == 0) {
                logger.info("\tTopology Keys Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Topology Keys Sheet:" + count);
                errorCountMap.put("Topology Keys Sheet", new Integer(count));
            }
            logger.info("\tEnd of Topology Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in topology keys Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Transformations
         */
        try {
            logger.info("\tIn Transformations Sheet");
            count = 0;
            if (transformationsSheet != null) {
                XSSFCell cellMeasurementInterface = null, cellTableName = null, cellTransformationType = null, cellTransformationSource = null,
                        cellTransformationTarget = null, cellTransformationConfig = null;
                String columnNameMeasurementInterface = "Measurement Interface", columnNameTableName = "Fact Table or Reference Table",
                        columnNameTransformationType = "Transformation Type", columnNameTransforamtionSource = "Transformation Source",
                        columnNameTransforamtionTarget = "Transformation Target", columnNameTransforamtionConfig = "Transformation Config";
                String columnValueMeasurementInterface = null, columnValueTableName = null, columnValueTransformationType = null,
                        columnValueTransforamtionSource = null, columnValueTransforamtionTarget = null, columnValueTransforamtionConfig = null;
                int columnNoMeasurementInterface, columnNoTableName, columnNoTransformationType, columnNoTransforamtionSource,
                        columnNoTransforamtionTarget, columnNoTransforamtionConfig;

                rowIterator = transformationsSheet.iterator();
                firstRow = (XSSFRow) rowIterator.next();

                if (!isEmptyRow(firstRow)) {
                    columnNoMeasurementInterface = getColumnNo(firstRow, columnNameMeasurementInterface);
                    columnNoTableName = getColumnNo(firstRow, columnNameTableName);
                    columnNoTransformationType = getColumnNo(firstRow, columnNameTransformationType);
                    columnNoTransforamtionSource = getColumnNo(firstRow, columnNameTransforamtionSource);
                    columnNoTransforamtionTarget = getColumnNo(firstRow, columnNameTransforamtionTarget);
                    columnNoTransforamtionConfig = getColumnNo(firstRow, columnNameTransforamtionConfig);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellMeasurementInterface = row.getCell(columnNoMeasurementInterface);
                            if (cellMeasurementInterface == null || cellMeasurementInterface.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Transfomations Sheet  Measurement Interface name column value is Empty at row:"
                                        + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueMeasurementInterface = cellMeasurementInterface.getStringCellValue();
                                if (!measurementInterfaceSet.contains(columnValueMeasurementInterface)) {
                                    if (Arrays.asList(parserTypes).contains(columnValueMeasurementInterface)) {
                                        parserNameConflictSet.add(columnValueMeasurementInterface);
                                    } else {
                                        logger.severe("\tIn Transfomations Sheet  Measurement interface name column value\t"
                                                + columnValueMeasurementInterface + "  should be parser name in Interface sheet");
                                        count++;
                                    }
                                }
                            } // if Measurement Interface cell Empty

                            // TableName
                            cellTableName = row.getCell(columnNoTableName);
                            if (cellTableName == null || cellTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Transfomations Sheet Table name column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTableName = cellTableName.getStringCellValue();
                               // System.out.println("columnValueTableName:" +columnValueTableName);
                                transformationsTableSet.add(columnValueTableName.trim());   //EQEV-58207 @
                            } // if Table Name cell Empty

                            // TransformationType
                            cellTransformationType = row.getCell(columnNoTransformationType);
                            if (cellTransformationType == null || cellTransformationType.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn Transfomations Sheet TransformationType column value is Empty in Table:" + columnValueTableName
                                        + "  at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTransformationType = cellTransformationType.getStringCellValue().toLowerCase();
                                if (!Arrays.asList(transformationTypes).contains(columnValueTransformationType)) {
                                    logger.severe("\tIn Transfomations Sheet TransformationType:" + columnValueTransformationType
                                            + "  is not a valid Transformation Type in Table:" + columnValueTableName + "  at row:"
                                            + (row.getRowNum() + 1));
                                    count++;
                                }
                            } // if Transformation Type cell Empty

                            // TransformationSource
                            cellTransformationSource = row.getCell(columnNoTransforamtionSource);
                            if (cellTransformationSource == null || cellTransformationSource.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Transfomations Sheet TransformationSource column value is Empty in Table:" + columnValueTableName
                                        + "  at row:" + (row.getRowNum() + 1));
                            } else {
                                columnValueTransforamtionSource = cellTransformationSource.getStringCellValue();
                            } // if Transformation Source cell Empty

                            // TransformationTarget
                            cellTransformationTarget = row.getCell(columnNoTransforamtionTarget);
                            if (cellTransformationTarget == null || cellTransformationTarget.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Transfomations Sheet TransformationTarget column value is Empty in Table:" + columnValueTableName
                                        + "  at row:" + (row.getRowNum() + 1));
                            } else {
                                columnValueTransforamtionTarget = cellTransformationTarget.getStringCellValue();
                            } // if Transformation Target cell Empty

                            // TransformationConfig
                            cellTransformationConfig = row.getCell(columnNoTransforamtionConfig);
                            if (cellTransformationConfig == null || cellTransformationConfig.getCellType() == Cell.CELL_TYPE_BLANK) {
                                // JIRA EQEV-46761: Transformation_Config_Field
                                if (!columnValueTransformationType.equalsIgnoreCase("copy")) {
                                    logger.warning("\tIn Transformations Sheet TransformationConfig Column value is empty in table:"
                                            + columnValueTableName + " at row: " + (row.getRowNum() + 1));
                                    count++;
                                } else {
                                    logger.fine("\tIn Transfomations Sheet TransformationConfig column value is Empty in Table:"
                                            + columnValueTableName + "  at row:" + (row.getRowNum() + 1));
                                }
                            } else {
                                columnValueTransforamtionConfig = cellTransformationConfig.getStringCellValue();
                                if (columnValueTransformationType.equalsIgnoreCase("lookup")) {
                                    if (!columnValueTransforamtionConfig.contains("pattern")) {
                                        logger.warning(
                                                "\tIn Transfomations Sheet TransformationConfig column value is not having 'pattern' for TransformationType lookup in Table:"
                                                        + columnValueTableName + "  at row:" + (row.getRowNum() + 1));
                                        count++;
                                    }
                                }
                            } // if Transformation Target cell Empty
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(transformationsSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Transformations Sheet");
                            		count++;
                            	}
                            }
                        } // if row not empty
                    } // for each row
                } else {
                    logger.warning("In Transformations Sheet First row is Empty");
                    count++;
                }

            } else {
                logger.warning("Transformations Sheet is Empty");
                count++;
            }

            if (parserNameConflictSet.size() > 0) {
                logger.info(
                        "\tIn Transformations sheet following parsers name are used as measurement interface name but it is not matching with interface sheet parser name");
                for (String s : parserNameConflictSet) {
                    logger.severe(
                            "\t In Transformations sheet " + s + " is used as measurement interface name but it is not present in interface sheet");
                    count++;
                }
            }

            if (count == 0) {
                logger.info("\tTransformations Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Transformations Sheet:" + count);
                errorCountMap.put("Transformations Sheet", new Integer(count));
            }
            logger.info("\tEnd of Transformations Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Transformation Sheet:" + e + "\n");
            e.printStackTrace();
        }
        
        int t = transformationsVerify(transformationsTableSet, factTableTransformationsSet, topologyTableTransformationsSet, logger);
        if (t > 0) {
            errorCountMap.put("TransformationsVerification", new Integer(t));
        }

        /*
         * dataFormat
         */
        
        try {
        	
            logger.info("\tIn DataFormat Sheet");
            count = 0;
            
            if (dataFormatSheet != null) {
                rowIterator = dataFormatSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());
                XSSFCell cellTableName = null, cellCounterKeyName = null, cellTagName = null;
                if (!isEmptyRow(firstRow)) {
                    String columnNameTableName = "Table Name", columnNameCounterKeyName = "Counter/key Name";
                    String columnValueTableName = null, columnValueCounterKeyName = null;
                    String columnValueTagName = null;
                    int columnNoTableName, columnNoCounterKeyName, columnNoTagName[];
					int p1 = 0; // 74801
					 
                    columnNoTableName = getColumnNo(firstRow, columnNameTableName);
                    columnNoCounterKeyName = getColumnNo(firstRow, columnNameCounterKeyName);
                    
					for (String parsername : measurementInterfaceSet) // 74801
					{
						parserNameMap.put(p1++, parsername);	
					}
					columnNoTagName = new int[measurementInterfaceSet.size()];
                    z = 0;
                    for (String s : measurementInterfaceSet) {
                    	int columnNumber = getColumnNo(firstRow, s);
						if (columnNumber >= 0) {
							columnNoTagName[z++] = columnNumber;
						} else {
							logger.warning("Parser Name: "+s+" is not present in DataFormat sheet");
							count++;
						}
                    }
                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellTableName = row.getCell(columnNoTableName);

                            if (cellTableName == null || cellTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn DataFormat Sheet Table Name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTableName = cellTableName.getStringCellValue();
                                /*
                                 * if(columnValueTableName.length()!=columnValueTableName.trim().length()){
                                 * logger.severe("\tIn DataFormat Sheet TableName is having spaces at row:"+(row .getRowNum()+1));count++; }
                                 */

                                if (!factTablesSet.contains(columnValueTableName)) {
                                    if (!topologyTablesSet.contains(columnValueTableName)) {
                                        logger.warning("Table Name:" + columnValueTableName
                                                + "  is present in Data Format Sheet.But it is not present in either factTable or topologyTable sheet");
                                        count++;
                                    }
                                }
                                dataFormatTableSet.add(columnValueTableName);
                            }

                            cellCounterKeyName = row.getCell(columnNoCounterKeyName);
                            if (cellCounterKeyName == null || cellCounterKeyName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn DataFormat Sheet Table Name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueCounterKeyName = cellCounterKeyName.getStringCellValue();
                                /*
                                 * if(columnValueCounterKeyName.length()!=columnValueCounterKeyName.trim(). length()){ logger.
                                 * severe("\tIn DataFormat Sheet CounterKeyName is having spaces in Table:"
                                 * +columnValueTableName+"  at row:"+(row.getRowNum()+1));count++; }
                                 */
                                z = dataFormatSet.size();
                                dataFormatSet.add(columnValueTableName + ":" + columnValueCounterKeyName);
                                if (z == dataFormatSet.size()) {
                                    duplicateDataFormatList.add(columnValueTableName + ":" + columnValueCounterKeyName);
                                }
                            }
                            for (int p = 0; p < measurementInterfaceSet.size(); p++) { //74801
								if (columnNoTagName[p] >= 0 ) {
									cellTagName = row.getCell(columnNoTagName[p]);//parser value
													
									if (!(cellTagName == null || cellTagName.getCellType() == Cell.CELL_TYPE_BLANK) )
									{
										columnValueTagName = cellTagName.toString();
										//logger.info(columnValueTableName);
										if(KeysMap.containsKey(columnValueTableName) && !(KeysMap.get(columnValueTableName).contains(columnValueCounterKeyName)))
										{
											//logger.info(MultipleParser.toString());
											if((MultipleParser.get(columnValueTableName) != null) && !(MultipleParser.get(columnValueTableName).contains(parserNameMap.get(p))))
											{
												logger.severe("In DataFormat Sheet The Counter: "
														+ columnValueCounterKeyName + " of Table: "+columnValueTableName
														+" is wrongly supported to Parser: "
														+ parserNameMap.get(p));
												count++;
											}
											else
											{
												if(TempMultipleParser.containsKey(columnValueTableName) && TempMultipleParser.get(columnValueTableName).contains(parserNameMap.get(p)))
													TempMultipleParser.get(columnValueTableName).remove(parserNameMap.get(p));
											}
										}
										else
										{
											//logger.info(MultipleParser.toString());
											if((MultipleParser.get(columnValueTableName) != null) && !(MultipleParser.get(columnValueTableName).contains(parserNameMap.get(p))))
											{
												logger.severe("In DataFormat Sheet The Key: "
														+ columnValueCounterKeyName + " of Table: "+columnValueTableName
														+" is wrongly supported to Parser: "
														+ parserNameMap.get(p));
												count++;
											}
											else
											{
												if(TempMultipleParserforKey.containsKey(columnValueTableName) && TempMultipleParserforKey.get(columnValueTableName).contains(parserNameMap.get(p)))
													TempMultipleParserforKey.get(columnValueTableName).remove(parserNameMap.get(p));
											}
										}
									}
									else 
									{
										columnValueTagName = null;
										if(CommonKeysMap.get(columnValueTableName) != null && CommonKeysMap.get(columnValueTableName).contains(columnValueCounterKeyName)
												&& (MultipleParser.get(columnValueTableName) != null) && (MultipleParser.get(columnValueTableName).contains(parserNameMap.get(p))))
										{
											logger.severe("In DataFormat Sheet The Common Key: "
													+ columnValueCounterKeyName + " of Table: "+columnValueTableName
													+" is not supported to Parser: "
													+ parserNameMap.get(p));
											count++;
										}
										else if((MultipleParser.get(columnValueTableName) != null) && MultipleParser.get(columnValueTableName).contains(parserNameMap.get(p)) 
												&& MultipleParser.get(columnValueTableName).size()==1)
										{
											logger.severe("In DataFormat Sheet The Key or Counter: "
														+ columnValueCounterKeyName + " of Table: "+columnValueTableName
														+" is not supported to Parser: "
														+ parserNameMap.get(p));
											count++;
										}
								}
								
									if (columnValueTableName != null && columnValueCounterKeyName != null
											&& columnValueTagName != null) {
										String dataFormatRowValues = columnValueTableName + ":"
												+ columnValueCounterKeyName + ":" + columnValueTagName;
										
										if (!dataTableHashMap.containsKey(p)) {
											dataTableHashMap.put(p, new ArrayList<String>());
											dataTableHashMap.get(p).add(dataFormatRowValues);
										} else
											dataTableHashMap.get(p).add(dataFormatRowValues);
									}
								}
							}
							
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(dataFormatSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in DataFormat Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                for (String Table : TempMultipleParser.keySet())
                {
                	if(TempMultipleParser.get(Table).size()>0)
                	{
                		logger.severe("In DataFormat Sheet, The Table: "+Table
								+" didn't have atleast one counter support for Parser(s): "
								+ TempMultipleParser.get(Table).toString());
                		count++;
                	}
                }
                for (String Table : TempMultipleParserforKey.keySet())
                {
                	if(TempMultipleParserforKey.get(Table).size()>0)
                	{
                		logger.severe("In DataFormat Sheet, The Table: "+Table
								+" didn't have atleast one key support for Parser(s): "
								+ TempMultipleParserforKey.get(Table).toString());
                		count++;
                	}
                }
                } else {
                    logger.warning("First row is Empty in Data Format Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Data Format sheet is Empty");
                count++;
            } // if sheet
            if (duplicateDataFormatList.size() > 0) {
                logger.info("\tDuplicate DataForamt List as follows");
                String[] tempArray;
                for (String s : duplicateDataFormatList) {
                    tempArray = s.split(":");
                    logger.warning("In DataFormat Sheet in Table:" + tempArray[0] + "  Key(or)Counter:" + tempArray[1]
                            + "  is a duplicate DataFormat entry");
                    count++;
                }
            }
            
            for (int parserNameColumnNo : dataTableHashMap.keySet()) {  //74801
				LinkedHashMap<String, LinkedHashMap<String, String>> dataTableCounterTagMap = new LinkedHashMap<>();
				ArrayList<String> parserdetails = dataTableHashMap.get(parserNameColumnNo);
				for (String details : parserdetails) {
					String[] str = details.split(":");
					String tableName = str[0];
					String counterName = str[1];
					String tagName = str[2];
					
						if (dataTableCounterTagMap.containsKey(tableName)) {
							LinkedHashMap<String, String> valueCounterTagName = dataTableCounterTagMap.get(tableName);
							valueCounterTagName.put(counterName, tagName);
							dataTableCounterTagMap.put(tableName, valueCounterTagName);
						} else {
							LinkedHashMap<String, String> valueCounterTagName = new LinkedHashMap<String, String>(); // 74801
							valueCounterTagName.put(counterName, tagName);
							dataTableCounterTagMap.put(tableName, valueCounterTagName);
						}
				}

				LinkedHashMap<String, ArrayList<String>> dataTagCounterMap = new LinkedHashMap<>(); 

				for (String entry1 : dataTableCounterTagMap.keySet()) {

					for (Map.Entry<String, String> entry2 : dataTableCounterTagMap.get(entry1).entrySet()) {

						if (dataTagCounterMap.containsKey(entry2.getValue())) {
							dataTagCounterMap.get(entry2.getValue()).add(entry2.getKey());
						} else {
							ArrayList<String> keys = new ArrayList<>();
							keys.add(entry2.getKey());
							dataTagCounterMap.put(entry2.getValue(), keys);
						}
					}

					for (Map.Entry<String, ArrayList<String>> dataTagCounterEntry : dataTagCounterMap.entrySet()) {
						if (dataTagCounterEntry.getValue().size() > 1) {

							logger.warning("In DataFormat Sheet in Table: " + entry1 + " Keys(or)Counters: "
									+ String.join(", ", dataTagCounterEntry.getValue()) + " have same Tag Name: "
									+ dataTagCounterEntry.getKey() + " in Parser Name Column: " + parserNameMap.get(parserNameColumnNo));
							count++;
						}
					}

					dataTagCounterMap.clear();
				}

			}
            
            if (count == 0) {
                logger.info("\tData Format Sheet is Fine");
            } else {
                logger.info("\tNo of observations in DataFormat Sheet:" + count);
                errorCountMap.put("Data Format Sheet", new Integer(count));
            }
            logger.info("\tEnd of Data Format Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Data Format Sheet:" + e + "\n");
            e.printStackTrace();
        }
        int t1 = dataFormatVerify(dataFormatSet, countersSet, keysSet, topologyKeysSet,topologyTablesSet1, logger);
        if (t1 > 0) {
            errorCountMap.put("DataFormatVerification", new Integer(t1));
        }
        /*
         * Vectors Sheet
         */
        try {
            vectorLogger.info("\tIn Vectors Sheet");
            count = 0;
			HashMap<String,HashMap<String,HashSet<String>>> CounterIndexMap = new HashMap<String,HashMap<String,HashSet<String>>>();						 
            if (vectorsSheet != null) {
                XSSFCell cellFactTableName = null, cellCounterName = null, cellVendorRelease = null, cellIndex = null, cellFrom = null, cellTo = null,
                        cellVectorDescription = null, cellQuantity = null;
                String columnNameFactTableName = "Fact Table Name", columnNameCounterName = "Counter Name",
                        columnNameVendorRelease = "Vendor Release", columnNameIndex = "Index", columnNameFrom = "From", columnNameTo = "To",
                        columnNameVectorDescription = "Vector Description", columnNameQuantity = "Quantity";
                String columnValueFactTableName = null, columnValueCounterName = null, columnValueVendorRelease = null, columnValueIndex = null,
                        columnValueFrom = null, columnValueTo = null, columnValueVectorDescription = null, columnValueQuantity = null;
                int columnNoFactTableName, columnNoCounterName, columnNoVendorRelease, columnNoIndex, columnNoFrom, columnNoTo,
                        columnNoVectorDescription, columnNoQuantity;
                rowIterator = vectorsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoFactTableName = getColumnNo(firstRow, columnNameFactTableName);
                    columnNoCounterName = getColumnNo(firstRow, columnNameCounterName);
                    columnNoVendorRelease = getColumnNo(firstRow, columnNameVendorRelease);
                    columnNoIndex = getColumnNo(firstRow, columnNameIndex);
                    columnNoFrom = getColumnNo(firstRow, columnNameFrom);
                    columnNoTo = getColumnNo(firstRow, columnNameTo);
                    columnNoVectorDescription = getColumnNo(firstRow, columnNameVectorDescription);
                    columnNoQuantity = getColumnNo(firstRow, columnNameQuantity);
                    HashSet<String> vectorDescCheck = new HashSet<>();
                    HashMap<String, HashMap<String, String>> vectorIndexParentHashmap = new HashMap<String, HashMap<String, String>>();
                    ArrayList<String> VectorSheetDuplicateValueList = new ArrayList<String>();
                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            // if cellFactTable Name null Start
                            cellFactTableName = row.getCell(columnNoFactTableName);
                            if (cellFactTableName == null || cellFactTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.severe("\tIn Vectors sheet FactTable Name is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueFactTableName = cellFactTableName.getStringCellValue();
								if (!factTablesSet.contains(columnValueFactTableName)) {
									int size = vectorFactTablesNotPresent.size();
									vectorFactTablesNotPresent.add(columnValueFactTableName);
									if (size != vectorFactTablesNotPresent.size()) {
										vectorLogger.warning("Fact Table Name:" + columnValueFactTableName
												+ "  is present in Vectors Sheet.But it is not present in factTableSet");
										count++;
									}
								}

                                vectorsFactTableSet.add(columnValueFactTableName);
                            } // if cellFactTable Name null end

                            // if cellCounterName null start
                            cellCounterName = row.getCell(columnNoCounterName);
                            if (cellCounterName == null || cellCounterName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.severe("\tIn Vectors sheet Counter Name is Empty in Table\t" + columnValueFactTableName + "  at row:"
                                        + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueCounterName = cellCounterName.getStringCellValue();
                                vectorsSet.add(columnValueFactTableName + ":" + columnValueCounterName); //EQEV-63309 @
                            }
							
							// if cellIndex
                            cellIndex = row.getCell(columnNoIndex);
                            if (cellIndex == null || cellIndex.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.severe("\tIn Vectors sheet Index is Empty in Table:" + columnValueFactTableName + "  Counter:"
                                        + columnValueCounterName + "  in vendorRelease:" + columnValueVendorRelease + "  at row: "
                                        + (row.getRowNum() + 1));
                                count++;
                            } else {

                                columnValueIndex = getColumnValue(cellIndex);
                            }
							
                            // if cellVendorRelease
                            cellVendorRelease = row.getCell(columnNoVendorRelease);
                            if (cellVendorRelease == null || cellVendorRelease.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.severe("\tIn Vectors sheet Vendor Release is Empty in Table:" + columnValueFactTableName + "  Counter:"
                                        + columnValueCounterName + "  at row:" + (row.getRowNum() + 1));
                                count++;
							} else {
								columnValueVendorRelease = cellVendorRelease.getStringCellValue();
								if(columnValueVendorRelease.contains(PrevVer))
		                		{		               
									if(!columnValueVendorRelease.contains(NewVer))
									{
		                				//vectorLogger.severe(columnValueVendorRelease);
		                				if(CounterIndexMap.containsKey(columnValueFactTableName))
		                				{
		                					if(CounterIndexMap.get(columnValueFactTableName).containsKey(columnValueCounterName))
		                					{
		                						CounterIndexMap.get(columnValueFactTableName).get(columnValueCounterName).add(columnValueIndex);
		                					}
		                					else
		                					{
		                						HashSet<String> Index = new HashSet<String>();
		                                    	Index.add(columnValueIndex);
		                                    	CounterIndexMap.get(columnValueFactTableName).put(columnValueCounterName, Index);
		                					}
		                					
		                				}
		                                    else
		                                    {
		                                    	HashSet<String> Index = new HashSet<String>();
		                                    	Index.add(columnValueIndex);
		                                    	HashMap<String,HashSet<String>> Temp = new HashMap<String,HashSet<String>>();
		                                    	Temp.put(columnValueCounterName, Index);
		                                    	CounterIndexMap.put(columnValueFactTableName,Temp);
		                                    }
									}
		                		}
								String vendorRelease[] = columnValueVendorRelease.split(",");
								if (vendorRelease.length >= 1) {
									for (String vendorRel : vendorRelease) {
										//if (vendorRel.charAt(0) != 'R') {
											vectorsVendorReleaseSet.add(columnValueFactTableName + ":"
													+ columnValueCounterName + "_" + vendorRel); // EQEV-63309 @
										//}
									}
								}
							}
                            

                            // if cellFrom
                            cellFrom = row.getCell(columnNoFrom);
                            if (cellFrom == null || cellFrom.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.fine("\tIn Vectors sheet From column is Empty in Table:" + columnValueFactTableName + "  Counter:"
                                        + columnValueCounterName + "  in vendorRelease:" + columnValueVendorRelease + "  at index: "
                                        + columnValueIndex);
                                columnValueFrom = "";

                            } else {
                                columnValueFrom = getColumnValue(cellFrom);

                            }

                            // if cellTo
                            cellTo = row.getCell(columnNoTo);
                            if (cellTo == null || cellTo.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.fine("\tIn Vectors sheet To column is Empty in Table:" + columnValueFactTableName + "  Counter:"
                                        + columnValueCounterName + "  in vendorRelease:" + columnValueVendorRelease + "  at index: "
                                        + columnValueIndex);

                                columnValueTo = "";

                            } else {
                                columnValueTo = getColumnValue(cellTo);
                            }

                            // if cellDescription
                            cellVectorDescription = row.getCell(columnNoVectorDescription);
                            if (cellVectorDescription == null || cellVectorDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.warning("In Vectors sheet VectorDescription column is Empty in Table:" + columnValueFactTableName
                                        + "  Counter:" + columnValueCounterName + "  in vendorRelease:" + columnValueVendorRelease + "  at index: "
                                        + columnValueIndex);
                                count++;
                            } else {
                                columnValueVectorDescription = getColumnValue(cellVectorDescription);

                            }
                            //Added by ZVRYADA - Fix for JIRA:EQEV-48396 - Check for descriptions for each vector index.

                            String keyofParentHashMap = "Table:" + columnValueFactTableName + "  " + "Counter:" + columnValueCounterName + "  "
                                    + "Index:" + columnValueIndex;

                            if (vectorIndexParentHashmap.isEmpty() || !(vectorIndexParentHashmap.containsKey(keyofParentHashMap))) {

                                HashMap<String, String> vectorIndexChildHashMap = new HashMap<String, String>();

                                vectorIndexChildHashMap.put("from", columnValueFrom);
                                vectorIndexChildHashMap.put("to", columnValueTo);
                                vectorIndexChildHashMap.put("description", columnValueVectorDescription);

                                vectorIndexParentHashmap.put(keyofParentHashMap, vectorIndexChildHashMap);

                            } else {
                                if (!(vectorIndexParentHashmap.get(keyofParentHashMap).get("from").equals(columnValueFrom))
                                        || !(vectorIndexParentHashmap.get(keyofParentHashMap).get("to").equals(columnValueTo))
                                        || !(vectorIndexParentHashmap.get(keyofParentHashMap).get("description")
                                                .equals(columnValueVectorDescription))) {

                                    vectorLogger.warning("At row number :" + " " + (row.getRowNum() + 1) + " From or To or Description :"
                                            + columnValueFrom + " , " + columnValueTo + " , " + columnValueVectorDescription
                                            + ": is not same on Vector Table: " + columnValueFactTableName + " Counter Name: "
                                            + columnValueCounterName + " index: " + columnValueIndex + " release: " + columnValueVendorRelease);
                                    count++;

                                }
                            }

                            // if cellQuantity
                            cellQuantity = row.getCell(columnNoQuantity);
                            if (cellQuantity == null || cellQuantity.getCellType() == Cell.CELL_TYPE_BLANK) {
                                vectorLogger.warning("In Vectors sheet Quantity column is Empty in Table:" + columnValueFactTableName + "  Counter:"
                                        + columnValueCounterName + "  in vendorRelease:" + columnValueVendorRelease + "  at index: "
                                        + columnValueIndex);
                                count++;
                            } else {
                                columnValueQuantity = getColumnValue(cellQuantity);
                            }
                            String DuplicateVectorSheetValue = "Table Name: " + columnValueFactTableName + " Counter Name: " + columnValueCounterName
                                    + " Vendor Release: " + columnValueVendorRelease + " Index: " + columnValueIndex + " Quantity: "
                                    + columnValueQuantity;
                            VectorSheetDuplicateValueList.add(DuplicateVectorSheetValue);
                        } // if row not Empty

                    }
                    //Added by ZVRYADA - Fix for JIRA:EQEV-51473 - Verify duplicate entries present in the vector sheet.
                    Collections.sort(VectorSheetDuplicateValueList);
                    for (int i = 0; i < VectorSheetDuplicateValueList.size() - 1; i++) {
                        int m = VectorSheetDuplicateValueList.get(i).compareTo(VectorSheetDuplicateValueList.get(i + 1));
                        if (m == 0) {
                            vectorLogger.warning("In Vector Sheet Duplicate Entries present for: " + VectorSheetDuplicateValueList.get(i + 1));
                            count++;
                        }
                    }

                } else {
                    vectorLogger.warning("First row is Empty in Vectors Sheet");
                    count++;
                } // if First row
            } else {
                vectorLogger.warning("Vectors sheet is Empty");
                count++;
            } // if sheet
            
            if (!fdType.equalsIgnoreCase("CM")) {
            	HashSet<String> tempFactTablesVectorSet = new HashSet<String>(); //EQEV-63309 @
                if (!(factTablesVectorSet.size() == vectorsFactTableSet.size())) {
                    tempFactTablesVectorSet.addAll(factTablesVectorSet);
                    for (String s : vectorsFactTableSet) {
                        tempFactTablesVectorSet.remove(s);
                    }
                    vectorLogger.info("\tFollowing  vector Tables do not have single Counter in vectors Sheet");
                    for (String s : tempFactTablesVectorSet) {
                        vectorLogger.warning("In vectors sheet Table:" + s + "  is not have single Counter in Vectors Sheet");
                        count++;
                    }
                }

                //EQEV-63309 @
                /*int flagVector  = 0,size;
                for(String vcounter : vectorCountersSet) {
                	size=0;
                	String[] vcounterArray = vcounter.split(":");
                	if(!tempFactTablesVectorSet.contains(vcounterArray[0])) {
                		for(String s : supportedVersionSet) {
                			if(vectorsSet.contains(vcounter) && !vectorsVendorReleaseSet.contains(vcounter+"_"+s) ) {
                				flagVector = 1;
                                vectorLogger.warning("In Vectors sheet Table: " + vcounterArray[0] + " Counter:" + vcounterArray[1]
                                        + " is not having support for " + s + "version");
                                count++;
                    		}
                			else if(vectorsSet.contains(vcounter) && vectorsVendorReleaseSet.contains(vcounter+"_"+s))
                				size++;
                    	}
                		if (flagVector == 0 && size!=supportedVersionSet.size()) {
                            vectorLogger.warning("In Vectors sheet Table:" + vcounterArray[0] + " Counter:" + vcounterArray[1] + " is not having support for any version");
                            count++;
                        }
                	}
                }Added Latest Version check as part of 134393*/
				if(VerFlag ==1)
                {
                for(String Table: CounterIndexMap.keySet())
                {
                	for(String Counter : CounterIndexMap.get(Table).keySet())
                	{
                	vectorLogger.severe("In Vectors sheet Table: " + Table + " Counter:" + Counter
                            + " is not having support for Latest Version --> "+NewVer+" for Vector Indices:" + CounterIndexMap.get(Table).get(Counter).toString());
                    count++;
                	}
                }
                }
                else
                {
                	vectorLogger.warning("The Given Versions are not present in the Coversheet.");
					count++;
                }
            }
            
            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
            	ArrayList<String> duplicateColumns = duplicateColumnCheck(vectorsSheet);
            	for(String s : duplicateColumns) {
            		vectorLogger.severe("Column_Name "+s+" is a Duplicate Column in Vectors Sheet");
            		count++;
            	}
            }
            
            if (count == 0) {
                vectorLogger.info("\tVectors Sheet is Fine");
            } else {
                vectorLogger.info("\tNo of observations in Vectors Sheet:" + count);
                errorCountMap.put("Vectors Sheet", new Integer(count));
            }

            vectorLogger.info("\tEnd of Vectors Sheet" + "\n");
        } catch (Exception e) {
            vectorLogger.severe("\tException in vectors Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * BHSheet
         */
        try {
            logger.info("\tIn BH Sheet");
            count = 0;
            if (BHSheet != null) {
                XSSFCell cellObjectName = null, cellPlaceholderName = null, cellDescription = null, cellWhereClause = null, cellCriteria = null,
                        cellAggregationType = null, cellLoopback = null, cellPThreshold = null, cellNThreshold = null;
                String columnNameObjectName = "Object Name", columnNamePlaceholderName = "Placeholder Name", columnNameDescription = "Description",
                        columnNameWhereClause = "Where Clause", columnNameCriteria = "Criteria", columnNameAggregationType = "Aggregation Type",
                        columnNameLoopback = "Loopback", columnNamePThreshold = "P Threshold", columnNameNThreshold = "N Threshold";
                String columnValueObjectName = null, columnValuePlaceholderName = null, columnValueDescription = null, columnValueWhereClause,
                        columnValueCriteria = null, columnValueAggregationType = null, columnValueLoopback = null, columnValuePThreshold = null,
                        columnValueNThreshold = null;
                int columnNoObjectName, columnNoPlaceholderName, columnNoDescription, columnNoWhereClause, columnNoCriteria, columnNoAggregationType,
                        columnNoLoopback, columnNoPThreshold, columnNoNThreshold;

                rowIterator = BHSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoObjectName = getColumnNo(firstRow, columnNameObjectName);
                    columnNoPlaceholderName = getColumnNo(firstRow, columnNamePlaceholderName);
                    columnNoDescription = getColumnNo(firstRow, columnNameDescription);
                    columnNoWhereClause = getColumnNo(firstRow, columnNameWhereClause);
                    columnNoCriteria = getColumnNo(firstRow, columnNameCriteria);
                    columnNoAggregationType = getColumnNo(firstRow, columnNameAggregationType);
                    columnNoLoopback = getColumnNo(firstRow, columnNameLoopback);
                    columnNoPThreshold = getColumnNo(firstRow, columnNamePThreshold);
                    columnNoNThreshold = getColumnNo(firstRow, columnNameNThreshold);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            // if cellFactTable Name null Start
                            cellObjectName = row.getCell(columnNoObjectName);
                            if (cellObjectName == null || cellObjectName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn BH Sheet Object Name is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectName = cellObjectName.getStringCellValue();
                            }

                            cellPlaceholderName = row.getCell(columnNoPlaceholderName);
                            if (cellPlaceholderName == null || cellPlaceholderName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.severe("\tIn BH Sheet Placeholder Name is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValuePlaceholderName = cellPlaceholderName.getStringCellValue();
                                // JIRA EQEV-46761 fix: BusyHour_Criteria_Product_Placeholder_Name
                                if (!columnValuePlaceholderName.startsWith("PP") && !columnValuePlaceholderName.startsWith("CP")) {
                                    logger.warning("Placeholder name" + columnValuePlaceholderName + "is not starts with PP or CP ");
                                    count++;
                                }
                                z = BHSet.size();
                                BHSet.add(columnValueObjectName + ":" + columnValuePlaceholderName);
                                if (z == BHSet.size()) {
                                    logger.warning("In BH Sheet Duplicate value in BH Sheet for ObjectName:" + columnValueObjectName
                                            + "  PlaceholderName:" + columnValuePlaceholderName);
                                    count++;
                                }
                            }

                            cellDescription = row.getCell(columnNoDescription);
                            if (cellDescription == null || cellDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet Description is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueDescription = cellDescription.getStringCellValue();
                            }

                            cellWhereClause = row.getCell(columnNoWhereClause);
                            if (cellWhereClause == null || cellWhereClause.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet Where Clause is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueWhereClause = cellWhereClause.getStringCellValue();
                            }

                            cellCriteria = row.getCell(columnNoCriteria);
                            if (cellCriteria == null || cellCriteria.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet Criteria is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueCriteria = cellCriteria.getStringCellValue();
                            }

                            cellAggregationType = row.getCell(columnNoAggregationType);
                            if (cellAggregationType == null || cellAggregationType.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet Aggregation Type is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueAggregationType = cellAggregationType.getStringCellValue().toUpperCase();
                                /*
                                 * if(!Arrays.asList(aggregationTypes).contains(columnValueAggregationType)){
                                 * logger.severe("\tIn BH Sheet Aggregation Type:" +columnValueAggregationType+"  is not valid in ObjectName:"
                                 * +columnValueObjectName+"  PlaceholderName:"+columnValuePlaceholderName);count ++; }
                                 */
                            }

                            cellLoopback = row.getCell(columnNoLoopback);
                            if (cellLoopback == null || cellLoopback.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet Loopback is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueLoopback = getColumnValue(cellLoopback);

                            }
                            cellPThreshold = row.getCell(columnNoPThreshold);
                            if (cellPThreshold == null || cellPThreshold.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet P Threshold is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValuePThreshold = getColumnValue(cellPThreshold);

                            }

                            cellNThreshold = row.getCell(columnNoNThreshold);
                            if (cellNThreshold == null || cellNThreshold.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BH Sheet N Threshold is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueNThreshold = getColumnValue(cellNThreshold);
                            }
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(BHSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in BH Sheet");
                            		count++;
                            	}
                            }
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in BH Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("BH sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tBH Sheet is Fine");
            } else {
                logger.info("\tNo of observations in BH Sheet:" + count);
                errorCountMap.put("BH Sheet", new Integer(count));
            }
            logger.info("\tEnd of BH Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in BH Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * BHRankKeysSheet
         */
        try {
            logger.info("\tIn BH RankKeys Sheet");
            count = 0;
            if (BHRankKeysSheet != null) {
                XSSFCell cellObjectName = null, cellPlaceholderName = null, cellKeyName = null, cellKeyValue = null, cellSourceFactTableName = null;
                String columnNameObjectName = "Object Name", columnNamePlaceholderName = "Placeholder Name", columnNameKeyName = "Key Name",
                        columnNameKeyValue = "Key Value", columnNameSourceFactTableName = "Source Fact Table Name";
                String columnValueObjectName = null, columnValuePlaceholderName = null, columnValueKeyName = null, columnValueKeyValue = null,
                        columnValueSourceFactTableName;
                int columnNoObjectName, columnNoPlaceholderName, columnNoKeyName, columnNoKeyValue, columnNoSourceFactTableName;
                HashMap<String, String> BHKeysMap = new HashMap<>();

                rowIterator = BHRankKeysSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {

                    columnNoObjectName = getColumnNo(firstRow, columnNameObjectName);
                    columnNoPlaceholderName = getColumnNo(firstRow, columnNamePlaceholderName);
                    columnNoKeyName = getColumnNo(firstRow, columnNameKeyName);
                    columnNoKeyValue = getColumnNo(firstRow, columnNameKeyValue);
                    columnNoSourceFactTableName = getColumnNo(firstRow, columnNameSourceFactTableName);

                    int flag1 = 0;
                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            // if cellFactTable Name null Start
                            cellObjectName = row.getCell(columnNoObjectName);
                            if (cellObjectName == null || cellObjectName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BHRankKeys Sheet ObjectName is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectName = cellObjectName.getStringCellValue();
                            }
                            cellPlaceholderName = row.getCell(columnNoPlaceholderName);
                            if (cellPlaceholderName == null || cellPlaceholderName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BHRankKeys Sheet Placeholder Name is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValuePlaceholderName = cellPlaceholderName.getStringCellValue();
                                // JIRA EQEV-46761 fix: BusyHour_Criteria_Product_Placeholder_Name
                                if (!columnValuePlaceholderName.startsWith("PP") && !columnValuePlaceholderName.startsWith("CP")) {
                                    logger.warning("Placeholder name" + columnValuePlaceholderName + "is not starts with PP or CP ");
                                    count++;
                                }
                                BHRankKeysSet.add(columnValueObjectName + ":" + columnValuePlaceholderName);
                            }

                            cellKeyName = row.getCell(columnNoKeyName);
                            if (cellKeyName == null || cellKeyName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BHRankKeys Sheet Key Name is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueKeyName = cellKeyName.getStringCellValue();

                                // JIRA EQEV-46761: BusyHour_Criteria_Placeholder_Keys
                                keysSetBH.add(columnValueObjectName + ":" + columnValueKeyName);
                                BHKeysMap.put(columnValueObjectName, columnValueKeyName);
                                // JIRA EQEV-46761: BusyHour_Measurement_Table_Keys
                                //BHKeysMap.put(key, value)
                                /*
                                 * String[] keysspl = keysSet.toString().split(":"); if(!keysspl[1].contains(columnValueKeyName)) {
                                 * logger.warning("Key " + columnValueKeyName + " is not present in the Keys sheet"); }
                                 */
                            }

                            cellKeyValue = row.getCell(columnNoKeyValue);
                            if (cellKeyValue == null || cellKeyValue.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BHRankKeys Sheet Key Value is Empty in ObjectName:" + columnValueObjectName + "  PlaceholderName:"
                                        + columnValuePlaceholderName);
                                count++;
                            } else {
                                columnValueKeyValue = cellKeyValue.getStringCellValue();
                                if (columnValueKeyName.equalsIgnoreCase("ELEMENT_TYPE")) {
                                    //Fix for JIRA EQEV-47813
                                    if (cellKeyValue.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                        String formula = cellKeyValue.getCellFormula();// logger.warning(formula);
                                        if (!formula.contains("CONCATENATE")) {
                                            logger.warning(
                                                    "In BHRankKeys Sheet Key Value is not having CONCATENATE function in formula bar for ObjectName:"
                                                            + columnValueObjectName + "  PlaceholderName:" + columnValuePlaceholderName);
                                            count++;
                                        }
                                    }
                                }
                            }
                            cellSourceFactTableName = row.getCell(columnNoSourceFactTableName);
                            if (cellSourceFactTableName == null || cellSourceFactTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In BHRankKeys Sheet Source Fact Table Name is Empty in ObjectName:" + columnValueObjectName
                                        + "  PlaceholderName:" + columnValuePlaceholderName);
                                count++;
                            }
                            else  { //EQEV-58206 @
                            	columnValueSourceFactTableName = cellSourceFactTableName.getStringCellValue();
                            	if (columnValueSourceFactTableName.startsWith("DC")) { }
                            	else if(columnValueSourceFactTableName.startsWith("DIM")) {
                            		if(columnValueSourceFactTableName.contains(",")) {
                            			String [] value = columnValueSourceFactTableName.split(",");
                            			for(String s : value) {
                            				if(s.startsWith("DC")) {
                            					logger.warning("In BHRankKeys Sheet Source Fact Table Name value is having DC table after DIM Table in ObjectName:"
                                                                + columnValueObjectName + "  PlaceholderName:" + columnValuePlaceholderName);
                                        		count++;
                            				}    //if ends                      					
                            			} //for ends
                            		} //if ends
                            	} //else if ends
                            	else {
                                    logger.warning(
                                            "In BHRankKeys Sheet Source Fact Table Name value is having Invalid table name after dim Table in ObjectName:"
                                                    + columnValueObjectName + "  PlaceholderName:" + columnValuePlaceholderName);
                                    count++; }
                               
                            } // else ends...
                        } // if row not Empty
                    } // for every row
                    List<String> keylist = new ArrayList<>(keysBHSet);
                    List<String> BHkeylist = new ArrayList<>(keysSetBH);
                    Collections.sort(keylist);
                    Collections.sort(BHkeylist);
                    Iterator it = BHkeylist.iterator();
                    while (it.hasNext()) {
                        String temp = it.next().toString();
                        if (!keylist.contains(temp)) {
                            if (temp.contains("BH")) {
                                logger.warning(temp + " is not present in BHRankKeys sheet");
                                count++;
                            }
                        }
                    }
                    if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                    	ArrayList<String> duplicateColumns = duplicateColumnCheck(BHRankKeysSheet);
                    	for(String s : duplicateColumns) {
                    		logger.severe("Column_Name "+s+" is a Duplicate Column in BHRankKeys Sheet");
                    		count++;
                    	}
                    }
                    
                } else {
                    logger.warning("First row is Empty in BH Rank Keys Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("BH Rank Keys sheet is Empty");
                count++;
            } // if sheet
            String tempS[];
            for (String s : BHSet) {
                if (!BHRankKeysSet.contains(s)) {
                    tempS = s.split(":");
                    logger.warning("In BHRankKeys sheet there is no key added for ObjectName:" + tempS[0] + "  PlaceHolderName:" + tempS[1]);
                    count++;
                }
            }

            if (count == 0) {
                logger.info("\tBHRankKeys Sheet is Fine");
            } else {
                logger.info("\tNo of observations in BHRankKeys Sheet:" + count);
                errorCountMap.put("BHRankKeys Sheet", new Integer(count));
            }

            logger.info("\tEnd of BHRankKeys Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in BHRankKeys Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * ExternalStatement Sheet
         */

        ArrayList<String> duplicateStatementList = new ArrayList<String>();
        String columnValueDefinition = null;
        try {
            logger.info("\tIn External Statement Sheet");
            count = 0;
            flag = 0;
            if (externalStatementSheet != null) {
                XSSFCell cellViewName = null, cellDatabaseName = null, cellDefinition = null;
                String columnNameViewName = "View Name", columnNameDatabaseName = "Database Name", columnNameDefinition = "Definition";
                String columnValueViewName = null, columnValueDatabaseName = null;
                int columnNoViewName, columnNoDatabaseName, columnNoDefinition;

                rowIterator = externalStatementSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoViewName = getColumnNo(firstRow, columnNameViewName);
                    columnNoDatabaseName = getColumnNo(firstRow, columnNameDatabaseName);
                    columnNoDefinition = getColumnNo(firstRow, columnNameDefinition);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            // if cellUniverseName Name null Start
                            cellViewName = row.getCell(columnNoViewName);
                            if (cellViewName == null || cellViewName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In External Statement Sheet ViewName column is Empty at row: " + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueViewName = cellViewName.getStringCellValue();
                                duplicateStatementList.add(columnValueViewName);

                            } // if cellUniverseName null end

                            // if cellDatabaseName Name null Start
                            cellDatabaseName = row.getCell(columnNoDatabaseName);
                            if (cellDatabaseName == null || cellDatabaseName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In External Statement Sheet Database Name is Empty at view:" + columnValueViewName);
                                count++;
                            } else {
                                columnValueDatabaseName = cellDatabaseName.getStringCellValue();
                                if (!Arrays.asList(externalStatmentDatabaseNames).contains(columnValueDatabaseName)) {
                                    logger.warning("In External Statement Sheet Database Name:" + columnValueDatabaseName + "  is not valid at view:"
                                            + columnValueViewName);
                                    count++;
                                }
                            } // if cellUniverseName null end

                            // if cellUniverseExtName null Start
                            cellDefinition = row.getCell(columnNoDefinition);
                            if (cellDefinition == null || cellDefinition.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In External Statement Sheet Definition is Empty at view:" + columnValueViewName);
                                count++;
                            } else {
                                columnValueDefinition = cellDefinition.getStringCellValue();
                                if (columnValueDefinition.endsWith(".txt")) {
                                    flag = 1;
                                    if (!columnValueDefinition.equalsIgnoreCase(fDNameExt)) {
                                        logger.severe("\tIn External statement sheet\t" + columnNameDefinition
                                                + "  Column value should match with Model-T Name");
                                        count++;
                                    }
                                    
                                }
                                else
                                {
                                	String[] strarr=columnValueDefinition.split("\n");
                                	for(String S:strarr)
                                		if(S.contains("(("))
                                    	{
                                    		Pattern p=Pattern.compile("[((]([0-9]+)[))]");
                            				Matcher m=p.matcher(S);
                            				while(m.find())
                            					if(!coverSheetBuildNumber.equals(m.group(1))) {
                            					logger.severe("\tIn External Statement Sheet, The TechPack Version is defined as ("+m.group(1)+") and the build number in Model-T ("+coverSheetBuildNumber+") are not same for the View - "+columnValueViewName);
                            					count++;
                                    	}}
                                		
                                }
                                /*
                                 * if(Pattern.matches(".*txt",columnValueDefinition )){
                                 *
                                 * }
                                 */
                            } // if cellUniverseName null end
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(externalStatementSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in External Statement Sheet");
                            		count++;
                            	}
                            }
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in External Statement Sheet");
                    count++;
                } // if First row

                //Added by ZVRYADA - Fix for JIRA:EQEV-49814 -Throw error When duplicate external statement names are present.
                Collections.sort(duplicateStatementList);

                for (int i = 0; i < duplicateStatementList.size() - 1; i++) {

                    int m = duplicateStatementList.get(i).compareTo(duplicateStatementList.get(i + 1));

                    if (m == 0) {
                        logger.warning("In External Statement Sheet Duplicate Statements are as: " + duplicateStatementList.get(i + 1));
                        count++;
                    }
                }
                
            } else {
                logger.warning("External Statement sheet is Empty");
                count++;
            } // if sheet
            if (flag == 1) {
                String fileName = fDPath.replace(".xlsx", ".txt");
                File f = new File(fileName);
                if (!f.exists()) {
                    logger.warning("In External Statement sheet required external statement text file is not there");
                    count++;
                }
                else
            	{
            	String[] Version=InFileVersion(f,coverSheetBuildNumber);
            	if(Version!=null) {
            		for(int i=0;i<Version.length;i++) {
            		logger.severe("\tThe TechPack Version in ExternalStatement text file "+columnValueDefinition+" ("+Version[i]+") and the Version of Model-T ("+coverSheetBuildNumber+") are not same.");
            		count++;
            	}}}}
            if (count == 0) {
                logger.info("\tExternal Statement Sheet is Fine");
            } else {
                logger.info("\tNo of observations in External Statement Sheet:" + count);
                errorCountMap.put("External Statement Sheet", new Integer(count));
            }
            logger.info("\tEnd of External Statement sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in External Statement Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Universe Topology Tables
         */
        try {
            logger.info("\tIn Universe Topology Tables sheet");
            count = 0;
            if (universeTopologyTablesSheet != null) {
                XSSFCell cellTopologyTableName = null, cellTopologyTableOwner = null, cellTableAlias = null, cellUniverseExtension = null;
                String columnNameTopologyTableName = "Topology Table Name", columnNameTopologyTableOwner = "Topology Table Owner",
                        columnNameTableAlias = "Table Alias", columnNameUniverseExtension = "Universe Extension";
                String columnValueTopologyTableName = null, columnValueTopologyTableOwner = null, columnValueTableAlias = null,
                        columnValueUniverseExtension = null;
                int columnNoTopologyTableName, columnNoTopologyTableOwner, columnNoTableAlias, columnNoUniverseExtension;

                rowIterator = universeTopologyTablesSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoTopologyTableName = getColumnNo(firstRow, columnNameTopologyTableName);
                    columnNoTopologyTableOwner = getColumnNo(firstRow, columnNameTopologyTableOwner);
                    columnNoTableAlias = getColumnNo(firstRow, columnNameTableAlias);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            cellTopologyTableName = row.getCell(columnNoTopologyTableName);
                            if (cellTopologyTableName == null || cellTopologyTableName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Universe Topology Tables sheet Topology table name column  is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTopologyTableName = cellTopologyTableName.getStringCellValue();
                            }

                            cellTopologyTableOwner = row.getCell(columnNoTopologyTableOwner);
                            if (cellTopologyTableOwner == null || cellTopologyTableOwner.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Tables sheet Topology Table column Owner is Empty at TopologyTableName:"
                                        + columnValueTopologyTableName);
                                count++;
                            } else {
                                columnValueTopologyTableOwner = cellTopologyTableOwner.getStringCellValue();
                                if (!Arrays.asList(topologyTableOwners).contains(columnValueTopologyTableOwner)) {
                                    logger.warning("In Universe Topology Tables sheet Topology Table column Owner:" + columnValueTopologyTableOwner
                                            + "  is not valid at TopologyTableName:" + columnValueTopologyTableName);
                                    count++;
                                }
                            }

                            cellTableAlias = row.getCell(columnNoTableAlias);
                            if (cellTableAlias == null || cellTableAlias.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("In Universe Topology Tables sheet Table Alias column is Empty at TopologyTableName:"
                                        + columnValueTopologyTableName);
                            } else {
                                columnValueTableAlias = cellTableAlias.getStringCellValue();
                            }

                            // JIRA EQEV-46761: Universe_Table_AliasName
                            if (columnValueTopologyTableName.equalsIgnoreCase(columnValueTableAlias)) {
                                logger.warning("In Universe Topology Sheet Table Alias should not be same Topology Table Name"
                                        + columnValueTopologyTableName);
                                count++;
                            }

                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Tables sheet Universe Extension column is Empty at TopologyTableName:"
                                        + columnValueTopologyTableName);
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                for (String s : universeExtensionArray) {
                                    if (!universeExtensionSet.contains(s)) {
                                        logger.warning("In Universe Topology Tables sheet Universe Extension:" + s
                                                + "  is not valid in TopologyTableName:" + columnValueTopologyTableName);
                                        count++;
                                    }
                                }
                            }
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeTopologyTablesSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Topology Tables Sheet");
                            		count++;
                            	}
                            }

                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Universe topology Tables Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Universe Topology Tables sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tUniverse Topology Tables Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Universe Topology Tables Sheet:" + count);
                errorCountMap.put("Universe Topology Tables Sheet", new Integer(count));
            }
            logger.info("\tEnd of Universe Topology Tables Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Universe Topology Tables Sheet" + e);
            e.printStackTrace();
        }

        /*
         * Universe Class
         */
        try {
            logger.info("\tIn Universe Class sheet");
            count = 0;
            if (universeClassSheet != null) {
                XSSFCell cellTopologyKeyClassName = null, cellClassDescription = null, cellParentClassName = null, cellUniverseExtension = null;
                String columnNameTopologyKeyClassName = "Topology & Key Class Name", columnNameClassDescription = "Class Description",
                        columnNameParentClassName = "Parent Class Name", columnNameUniverseExtension = "Universe Extension";
                String columnValueTopologyKeyClassName = null, columnValueClassDescription = null, columnValueParentClassName = null,
                        columnValueUniverseExtension = null;
                int columnNoTopologyKeyClassName, columnNoClassDescription, columnNoParentClassName, columnNoUniverseExtension;

                rowIterator = universeClassSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoTopologyKeyClassName = getColumnNo(firstRow, columnNameTopologyKeyClassName);
                    columnNoClassDescription = getColumnNo(firstRow, columnNameClassDescription);
                    columnNoParentClassName = getColumnNo(firstRow, columnNameParentClassName);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellTopologyKeyClassName = row.getCell(columnNoTopologyKeyClassName);
                            if (cellTopologyKeyClassName == null || cellTopologyKeyClassName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Class sheet Topology Key Class Name is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTopologyKeyClassName = cellTopologyKeyClassName.getStringCellValue();

                            }

                            // JIRA EQEV-46761: Check_Description
                            cellClassDescription = row.getCell(columnNoClassDescription);
                            if (cellClassDescription == null || cellClassDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Universe Class sheet Class Description column is Empty at Class:" + columnValueTopologyKeyClassName);
                                count++;
                            } else {
                                columnValueClassDescription = cellClassDescription.getStringCellValue();
                            }
                            cellParentClassName = row.getCell(columnNoParentClassName);
                            if (cellParentClassName == null || cellParentClassName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Class sheet Table Alias column is Empty at Class:" + columnValueTopologyKeyClassName);
                                count++;
                            } else {
                                columnValueParentClassName = cellParentClassName.getStringCellValue();
                                if (columnValueParentClassName.equalsIgnoreCase("Topology")) {
                                    z = universeClassTopologyTableSet.size();
                                    universeClassTopologyTableSet.add(columnValueTopologyKeyClassName);
                                    if (z == universeClassTopologyTableSet.size()) {
                                        logger.warning("In Universe Class sheet TopologyKeyClassName:" + columnValueTopologyKeyClassName
                                                + "  is duplicate in universeClass");
                                        count++;
                                    }
                                } else {
                                    z = universeClassFactTableSet.size();
                                    universeClassFactTableSet.add(columnValueTopologyKeyClassName);
                                    if (z == universeClassFactTableSet.size()) {
                                        logger.warning("In Universe Class sheet TopologyKeyClassName:" + columnValueTopologyKeyClassName
                                                + "  is duplicate in universeClass");
                                        count++;
                                    }
                                }
                            }

                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Universe Class sheet Universe Extension column is Empty at Class:" + columnValueTopologyKeyClassName);
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                for (String s : universeExtensionArray) {
                                    if (!universeExtensionSet.contains(s)) {
                                        logger.warning("In Universe Class sheet Universe Extension:" + s + "  is not valid at TopologyTableName:"
                                                + columnValueTopologyKeyClassName);
                                        count++;
                                    }
                                }
                            }
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeClassSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Class Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.severe("\tFirst row is Empty in Universe Class Sheet");
                    count++;
                } // if First row
            } else {
                logger.severe("\tUniverse Class sheet is Empty");
                count++;
            } // if sheet

            for (String s : factTablesSet) {
                if (!factTablesBHSet.contains(s)) {
                    if (!universeClassFactTableSet.contains(s + "_Keys")) {
                        logger.warning(s + "  FactTable keys are not  added in Universe Class Sheet");
                        count++;
                    }
                }
            }
            if (count == 0) {
                logger.info("\tUniverse Class Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Universe Class Sheet:" + count);
                errorCountMap.put("Universe Class Sheet", new Integer(count));
            }
            logger.info("\tEnd of Universe Class Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Universe Class Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Universe Topology Objects Class
         */
        try {
            logger.info("\tIn Universe Topology Objects sheet");
            count = 0;
            if (universeTopologyObjectsSheet != null) {
                XSSFCell cellUnvClass = null, cellUnvObject = null, cellUnvDescription = null, cellUnvType = null, cellUnvQualification = null,
                        cellUnvAggregation = null, cellSelectStatement = null, cellWhereClause = null, cellPromptHierarchy = null,
                        cellUniverseExtension = null;
                String columnNameUnvClass = "Unv. Class", columnNameUnvObject = "Unv. Object", columnNameUnvDescription = "Unv. Description",
                        columnNameUnvType = "Unv. Type", columnNameUnvQualification = "Unv. Qualification",
                        columnNameUnvAggregation = "Unv. Aggregation", columnNameSelectStatement = "Select statement",
                        columnNameWhereClause = "Where Clause", columnNamePromptHierarchy = "Prompt Hierarchy",
                        columnNameUniverseExtension = "Universe Extension";
                String columnValueUnvClass = null, columnValueUnvObject = null, columnValueUnvDescription = null, columnValueUnvType = null,
                        columnValueUnvQualification = null, columnValueUnvAggregation = null, columnValueSelectStatement,
                        columnValueWhereClause = null, columnValuePromptHierarchy = null, columnValueUniverseExtension = null;
                int columnNoUnvClass, columnNoUnvObject, columnNoUnvDescription, columnNoUnvType, columnNoUnvQualification, columnNoUnvAggregation,
                        columnNoSelectStatement, columnNoWhereClause, columnNoPromptHierarchy, columnNoUniverseExtension;

                rowIterator = universeTopologyObjectsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoUnvClass = getColumnNo(firstRow, columnNameUnvClass);
                    columnNoUnvObject = getColumnNo(firstRow, columnNameUnvObject);
                    columnNoUnvDescription = getColumnNo(firstRow, columnNameUnvDescription);
                    columnNoUnvType = getColumnNo(firstRow, columnNameUnvType);
                    columnNoUnvQualification = getColumnNo(firstRow, columnNameUnvQualification);
                    columnNoUnvAggregation = getColumnNo(firstRow, columnNameUnvAggregation);
                    columnNoSelectStatement = getColumnNo(firstRow, columnNameSelectStatement);
                    columnNoWhereClause = getColumnNo(firstRow, columnNameWhereClause);
                    columnNoPromptHierarchy = getColumnNo(firstRow, columnNamePromptHierarchy);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            cellUnvClass = row.getCell(columnNoUnvClass);
                            if (cellUnvClass == null || cellUnvClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet UnvClass Column is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueUnvClass = cellUnvClass.getStringCellValue();
                                if (!universeClassTopologyTableSet.contains(columnValueUnvClass)) {
                                    if (!universeClassFactTableSet.contains(columnValueUnvClass)) {
                                        logger.warning("In Universe Topology Objects sheet UnvClass:" + columnValueUnvClass
                                                + "  Column is not valid at row:" + (row.getRowNum() + 1));
                                        count++;
                                    }
                                }
                            }

                            // JIRA EQEV-46761:Universe_Conditions_ConditionObject
                            cellUnvObject = row.getCell(columnNoUnvObject);
                            if (cellUnvObject == null || cellUnvObject.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet UnvObject Column is Empty in UnvClass:" + columnValueUnvClass);
                                count++;
                            } else {
                                columnValueUnvObject = cellUnvObject.getStringCellValue();
                            }

                            // JIRA EQEV-46761: Check_Description
                            cellUnvDescription = row.getCell(columnNoUnvDescription);
                            if (cellUnvDescription == null || cellUnvDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet UnvDescription Column is Empty in UnvClass:" + columnValueUnvClass
                                        + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueUnvDescription = cellUnvDescription.getStringCellValue();
                            }

                            cellUnvType = row.getCell(columnNoUnvType);
                            if (cellUnvType == null || cellUnvType.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet UnvType Column is Empty in UnvClass:" + columnValueUnvClass
                                        + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueUnvType = cellUnvType.getStringCellValue();
                                // JIRA EQEV-46761: Universe_Objects_Type
                                if (!Arrays.asList(universeObjectTypes).contains(columnValueUnvType)) {
                                    logger.warning("In Universe Topology Objects sheet UnvType:" + columnValueUnvType + "  is not valid in UnvClass:"
                                            + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                    count++;
                                }
                            }

                            cellUnvQualification = row.getCell(columnNoUnvQualification);
                            if (cellUnvQualification == null || cellUnvQualification.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet Universe Qualification Column is Empty in UnvClass:"
                                        + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueUnvQualification = cellUnvQualification.getStringCellValue();
                                if (!Arrays.asList(universeQualifications).contains(columnValueUnvQualification)) {
                                    logger.warning("In Universe Topology Objects sheet Universe Qualification :" + columnValueUnvQualification
                                            + "  is not valid in UnvClass:" + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                    count++;
                                }

                            }

                            cellUnvAggregation = row.getCell(columnNoUnvAggregation);
                            if (cellUnvAggregation == null || cellUnvAggregation.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet Universe Aggregation Column is Empty in UnvClass:"
                                        + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueUnvAggregation = cellUnvAggregation.getStringCellValue().toUpperCase();
                                if (columnValueUnvQualification.equalsIgnoreCase("Dimension")
                                        && !columnValueUnvAggregation.equalsIgnoreCase("NONE")) {
                                    logger.warning(
                                            "In Universe Topology Objects sheet Universe Aggregation column value should be 'None' in UnvClass:"
                                                    + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                    count++;
                                } else if (!Arrays.asList(aggregationTypes).contains(columnValueUnvAggregation)) {
                                    logger.warning("In Universe Topology Objects sheet Universe Aggregation :" + columnValueUnvAggregation
                                            + "  is not valid in UnvClass:" + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                    count++;
                                }

                            }

                            cellSelectStatement = row.getCell(columnNoSelectStatement);
                            if (cellSelectStatement == null || cellSelectStatement.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet Select Statement Column is Empty in UnvClass:"
                                        + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueSelectStatement = cellSelectStatement.getStringCellValue();
                                /*
                                 * if(!(columnValueSelectStatement.startsWith("DC")||columnValueSelectStatement.
                                 * startsWith("count")||columnValueSelectStatement.startsWith("@"))){ logger.
                                 * warning("In Universe Topology Objects sheet Select Statement Column value:"
                                 * +columnValueSelectStatement+" is not having  prefix as either 'DC' or @ or count in UnvClass:"
                                 * +columnValueUnvClass+"  UnvObject:"+columnValueUnvObject);count++; }
                                 */
                            }

                            cellWhereClause = row.getCell(columnNoWhereClause);
                            if (cellWhereClause == null || cellWhereClause.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Universe Topology Objects sheet Where Clause Column is Empty in UnvClass:" + columnValueUnvClass
                                        + "  UnvObject:" + columnValueUnvObject);
                            } else {
                                columnValueWhereClause = cellWhereClause.getStringCellValue();
                            }

                            cellPromptHierarchy = row.getCell(columnNoPromptHierarchy);
                            if (cellPromptHierarchy == null || cellPromptHierarchy.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Universe Topology Objects sheet Prompt Hierarchy Column is Empty in UnvClass:" + columnValueUnvClass
                                        + "  UnvObject:" + columnValueUnvObject);
                            } else {
                                columnValuePromptHierarchy = cellPromptHierarchy.getStringCellValue();
                            }
                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Topology Objects sheet Universe Extension Column is Empty in UnvClass:"
                                        + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                for (String s : universeExtensionArray) {
                                    if (!universeExtensionSet.contains(s)) {
                                        logger.warning("In Universe Topology Objects sheet Universe Extension:" + s + "  is not valid in UnvClass:"
                                                + columnValueUnvClass + "  UnvObject:" + columnValueUnvObject);
                                        count++;
                                    }
                                }
                            }
                            if(row.getRowNum()==1) {  //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeTopologyObjectsSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Topology Objects Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Universe Topology Objects Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Universe Topology Objects sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tUniverse Topology Objects Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Universe Topology Objects Sheet:" + count);
                errorCountMap.put("Universe Topology Objects Sheet", new Integer(count));
            }
            logger.info("\tEnd of Universe Topology Objects Sheet" + "\n");
        } catch (Exception e) {
            logger.severe("\tException in Universe Topology Objects Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Universe Conditions
         */
        try {
            logger.info("\tIn Universe Conditions sheet");
            count = 0;
            if (universeConditionsSheet != null) {
                XSSFCell cellClass = null, cellConditionName = null, cellConditionDescription = null, cellWhereClause = null, cellAutoGenerate = null,
                        cellConditionObjectClass = null, cellConditionObject = null, cellPromptText = null, cellMultiSelection = null,
                        cellFreeText = null, cellUniverseExtension = null;
                String columnNameClass = "Class", columnNameConditionName = "Condition Name",
                        columnNameConditionDescription = "Condition Description", columnNameWhereClause = "Where Clause",
                        columnNameAutoGenerate = "Auto generate", columnNameConditionObjectClass = "Condition object class",
                        columnNameConditionObject = "Condition object", columnNamePromptText = "Prompt Text",
                        columnNameMultiSelection = "Multi selection", columnNameFreeText = "Free text",
                        columnNameUniverseExtension = "Universe Extension";
                String columnValueClass = null, columnValueConditionName = null, columnValueConditionDescription = null,
                        columnValueWhereClause = null, columnValueAutoGenerate = null, columnValueConditionObjectClass = null,
                        columnValueConditionObject = null, columnValuePromptText = null, columnValueMultiSelection = null, columnValueFreeText = null,
                        columnValueUniverseExtension = null;
                int columnNoClass, columnNoConditionName, columnNoConditionDescription, columnNoWhereClause, columnNoAutoGenerate,
                        columnNoConditionObjectClass, columnNoConditionObject, columnNoPromptText, columnNoMultiSelection, columnNoFreeText,
                        columnNoUniverseExtension;

                rowIterator = universeConditionsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoClass = getColumnNo(firstRow, columnNameClass);
                    columnNoConditionName = getColumnNo(firstRow, columnNameConditionName);
                    columnNoConditionDescription = getColumnNo(firstRow, columnNameConditionDescription);
                    columnNoWhereClause = getColumnNo(firstRow, columnNameWhereClause);
                    columnNoAutoGenerate = getColumnNo(firstRow, columnNameAutoGenerate);
                    columnNoConditionObjectClass = getColumnNo(firstRow, columnNameConditionObjectClass);
                    columnNoConditionObject = getColumnNo(firstRow, columnNameConditionObject);
                    columnNoPromptText = getColumnNo(firstRow, columnNamePromptText);
                    columnNoMultiSelection = getColumnNo(firstRow, columnNameMultiSelection);
                    columnNoFreeText = getColumnNo(firstRow, columnNameFreeText);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            cellClass = row.getCell(columnNoClass);
                            if (cellClass == null || cellClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Class Column is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueClass = cellClass.getStringCellValue();
                            }
                            
                            cellConditionName = row.getCell(columnNoConditionName);
                            if (cellConditionName == null || cellConditionName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Condition Name Column is Empty in:" + columnValueClass + "  class ");
                                count++;
                            } else {
                                columnValueConditionName = cellConditionName.getStringCellValue();
                            }
                            // JIRA EQEV-46761: Check_Description
                            cellConditionDescription = row.getCell(columnNoConditionDescription);
                            if (cellConditionDescription == null || cellConditionDescription.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Condition Description Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueConditionDescription = cellConditionDescription.getStringCellValue();
                            }

                            cellWhereClause = row.getCell(columnNoWhereClause);
                            if (cellWhereClause == null || cellWhereClause.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.fine("\tIn Universe Conditions sheet Where Clause Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                            } else {
                                columnValueWhereClause = cellWhereClause.getStringCellValue();
                            }

                            cellAutoGenerate = row.getCell(columnNoAutoGenerate);
                            if (cellAutoGenerate == null || cellAutoGenerate.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Auto Generate Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueAutoGenerate = cellAutoGenerate.getStringCellValue();
                            }
                            // JIRA EQEV-46761: Universe_Conditions_AutoGenerate
                            if (columnValueWhereClause == null && columnValueAutoGenerate == null) {
                                logger.warning("In Universe Conditions sheet both Where Clause and Auto Generate cannot be null for condition name: "
                                        + columnValueConditionName);
                                count++;
                            }

                            cellConditionObjectClass = row.getCell(columnNoConditionObjectClass);
                            if (cellConditionObjectClass == null || cellConditionObjectClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Condition Object Class Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueConditionObjectClass = cellConditionObjectClass.getStringCellValue();
                            }

                            // JIRA EQEV-46761: Universe_Conditions_ConditionObjectClass.
                            if (!columnValueClass.equals(columnValueConditionObjectClass)) {
                                logger.warning("Universe Class name " + columnValueClass + " is not equal to Conditon Object Class name");
                                count++;
                            }

                            cellConditionObject = row.getCell(columnNoConditionObject);
                            if (cellConditionObject == null || cellConditionObject.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Condition Object Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueConditionObject = cellConditionObject.getStringCellValue();
                            }

                            cellPromptText = row.getCell(columnNoPromptText);
                            if (cellPromptText == null || cellPromptText.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Prompt Text Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValuePromptText = cellPromptText.getStringCellValue();
                                // JIRA EQEV-46761: Universe_Conditions_PromptText
								//! condition added in if condition for JIRA EQEV-114577														
                                if (!columnValuePromptText.contentEquals(columnValueConditionName)) {
                                    logger.warning("In Universe Conditions sheet Prompt Text is not same as Condition Name"
                                            + columnValueConditionName);
                                    count++;
                                }
                            }

                            cellMultiSelection = row.getCell(columnNoMultiSelection);
                            if (cellMultiSelection == null || cellMultiSelection.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet MultiSelection Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueMultiSelection = cellMultiSelection.getStringCellValue();
                            }

                            cellFreeText = row.getCell(columnNoFreeText);
                            if (cellFreeText == null || cellFreeText.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet FreeText Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueFreeText = cellFreeText.getStringCellValue();
                            }

                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Conditions sheet Universe Extension Column is Empty in class:" + columnValueClass
                                        + "  ConditionName:" + columnValueConditionName);
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                for (String s : universeExtensionArray) {
                                    if (!universeExtensionSet.contains(s)) {
                                        logger.warning("In Universe Conditions sheet Universe Extension:" + s + "  is not valid in class:"
                                                + columnValueClass + "  ConditionName:" + columnValueConditionName);
                                        count++;
                                    }
                                }
                            }
                            
                            if(row.getRowNum()==1) {  //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeConditionsSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Conditions Sheet");
                            		count++;
                            	}
                            } 
                            
                        } // if row not Empty
                    } // for every row
                } else {
                    logger.warning("First row is Empty in Universe Conditions Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Universe conditions sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tUniverse Conditions sheet is Fine");
            } else {
                logger.info("\tNo of observations in Universe Conditions sheet:" + count);
                errorCountMap.put("Universe Conditions Sheet", new Integer(count));
            }
            logger.info("\tEnd of Universe Conditions Sheet" + "\n");
        } // try
        catch (Exception e) {
            logger.severe("\tException in Universe Conditions Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Universe Conditions
         */
        try {
            logger.info("\tIn Universe Joins sheet");
            count = 0;
            if (universeJionsSheet != null) {
                XSSFCell cellSourceTable = null, cellSourceLevel = null, cellSourceColumns = null, cellTargetTable = null, cellTargetLevel = null,
                        cellTargetColumns = null, cellJoinCardinality = null, cellContexts = null, cellExcludedContexts = null,
                        cellUniverseExtension = null;
                String columnNameSourceTable = "Source Table", columnNameSourceLevel = "Source Level", columnNameSourceColumns = "Source Columns",
                        columnNameTargetTable = "Target Table", columnNameTargetLevel = "Target Level", columnNameTargetColumns = "Target Columns",
                        columnNameJoinCardinality = "Join Cardinality", columnNameContexts = "Contexts",
                        columnNameExcludedContexts = "Excluded contexts", columnNameUniverseExtension = "Universe Extension";
                String columnValueSourceTable = null, columnValueSourceLevel = null, columnValueSourceColumns = null, columnValueTargetTable = null,
                        columnValueTargetLevel = null, columnValueTargetColumns = null, columnValueJoinCardinality = null, columnValueContexts = null,
                        columnValueExcludedContexts = null, columnValueUniverseExtension = null;
                int columnNoSourceTable, columnNoSourceLevel, columnNoSourceColumns, columnNoTargetTable, columnNoTargetLevel, columnNoTargetColumns,
                        columnNoJoinCardinality, columnNoContexts, columnNoExcludedContexts, columnNoUniverseExtension;
                //HashMap<String, String> UnvJoinKeysMap = new HashMap<>();

                rowIterator = universeJionsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoSourceTable = getColumnNo(firstRow, columnNameSourceTable);
                    columnNoSourceLevel = getColumnNo(firstRow, columnNameSourceLevel);
                    columnNoSourceColumns = getColumnNo(firstRow, columnNameSourceColumns);
                    columnNoTargetTable = getColumnNo(firstRow, columnNameTargetTable);
                    columnNoTargetLevel = getColumnNo(firstRow, columnNameTargetLevel);
                    columnNoTargetColumns = getColumnNo(firstRow, columnNameTargetColumns);
                    columnNoJoinCardinality = getColumnNo(firstRow, columnNameJoinCardinality);
                    columnNoContexts = getColumnNo(firstRow, columnNameContexts);
                    columnNoExcludedContexts = getColumnNo(firstRow, columnNameExcludedContexts);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            cellSourceTable = row.getCell(columnNoSourceTable);
                            if (cellSourceTable == null || cellSourceTable.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Joins sheet SourceTable column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueSourceTable = cellSourceTable.getStringCellValue();

                                if(!columnValueSourceTable.equalsIgnoreCase("All")) {
                                	String [] sourceTables = columnValueSourceTable.split("[,]");
                                	for(String eachTable : sourceTables) {
                                		if(!eachTable.startsWith("DC.")) {
                                			logger.severe("In Universe Joins sheet, Source Table "+eachTable+ " In row "+(row.getRowNum()+1) +" does not starts with DC.");
                                			count++;
                                		}
                                		
                                		if(eachTable.startsWith("DC.DC_E") && !fDName.split("[_]")[2].equals(eachTable.split("[_]")[2])) {
                                			logger.severe("In Universe Joins sheet, SourceTable "+eachTable+ " In row "+(row.getRowNum()+1) +" is not matching wih Model-T Name");
                                			count++;
                                		}
                                	}
                                }
                            }

                            cellSourceLevel = row.getCell(columnNoSourceLevel);
                            Boolean srclvl = false;
                            if (cellSourceLevel == null || cellSourceLevel.getCellType() == Cell.CELL_TYPE_BLANK) {
								//EQEV-114577 for 7th action point
                            	if(!(columnValueSourceTable.contains("DIM_"))) {
                               							   
                                logger.warning("In Universe Joins sheet SourceLevel column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            	}
                            } else {
                                columnValueSourceLevel = cellSourceLevel.getStringCellValue();
                                if (columnValueSourceLevel.equalsIgnoreCase("ALL")) {
                                    srclvl = true;
                                }
                            }

                            cellSourceColumns = row.getCell(columnNoSourceColumns);
                            if (cellSourceColumns == null || cellSourceColumns.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Joins sheet Source Columns column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueSourceColumns = cellSourceColumns.getStringCellValue();
                                if (columnValueSourceTable.contains("DIM_")) {
                                    logger.info(columnValueSourceTable + " key is a part of DIM");
                                } else {
                                    srckeys.add(columnValueSourceColumns);
                                }
                            }

                            cellTargetTable = row.getCell(columnNoTargetTable);
                            if (cellTargetTable == null || cellTargetTable.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Joins sheet Target Table column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTargetTable = cellTargetTable.getStringCellValue();

                                if (!columnValueTargetTable.startsWith("DC")) {
                                    logger.warning("In Universe Joins sheet Target Table:" + columnValueTargetTable
                                            + "  value is not having DC prefix at row:" + (row.getRowNum() + 1));
                                    count++;
                                }
                            }

                          /*EQEV-114577 code commented for 7th action point
                             * 
                             * cellTargetLevel = row.getCell(columnNoTargetLevel);
							
							 * if (cellTargetLevel == null || cellTargetLevel.getCellType() ==
							 * Cell.CELL_TYPE_BLANK) { logger.
							 * warning("In Universe Joins sheet Target Level column value is Empty at row:"
							 * + (row.getRowNum() + 1)); count++; } else { columnValueTargetLevel =
									
							 * cellTargetLevel.getStringCellValue(); }
							 */

                            cellTargetColumns = row.getCell(columnNoTargetColumns);
                            if (cellTargetColumns == null || cellTargetColumns.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Joins sheet Target Columns column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueTargetColumns = cellTargetColumns.getStringCellValue();
                                if (columnValueTargetTable.contains("DIM_")) {
                                    logger.info(columnValueTargetColumns + " key is a part of DIM");
                                } else {
                                    //JIRA EQEV-46761: Universe_Joins_Measurement_Table_Keys
             
                                		trgkeys.add(columnValueTargetColumns);
                                
                                }
                                /*
                                 * String[] topospl = topologyKeysSet.toString().split(":"); if(!topospl[1].contains(columnValueTargetColumns)){
                                 * logger.warning("In Universe Joins sheet Source Columns key "
                                 * +columnValueTargetColumns+" is not found in Topology keys table");count++; }
                                 */
                            }

                            cellJoinCardinality = row.getCell(columnNoJoinCardinality);
                            if (cellJoinCardinality == null || cellJoinCardinality.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning(
                                        "In Universe Joins sheet Join Cardinality Class column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueJoinCardinality = cellJoinCardinality.getStringCellValue();
                                // JIRA EQEV-46761: Universe_Joins_Cardinality.
                                if (!isValidCardinality(columnValueJoinCardinality)) {
                                    logger.warning("Universe joins " + columnValueTargetTable + " should have valid Cardinality");
                                    count++;
                                }
                            }

                            cellContexts = row.getCell(columnNoContexts);
                            if (cellContexts == null || cellContexts.getCellType() == Cell.CELL_TYPE_BLANK) {
							//EQEV-114577 7th action point
                            	if(columnValueSourceTable.contains("DIM_")&& columnValueTargetTable.contains("DIM_") ) {							   
                                logger.warning("In Universe Joins sheet Contexts column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
								}
                            } else {
                                columnValueContexts = cellContexts.getStringCellValue();
                                if (srclvl == true && columnValueContexts.equalsIgnoreCase("ALL")) {
                                    logger.warning("Both Source Level and Context cannot be filled for " + columnValueTargetTable);
                                    count++;
                                }
                            }
                            
						/*	
							  EQEV-114577 code commented for 7th action point
							  
							  cellExcludedContexts = row.getCell(columnNoExcludedContexts);
							  if(cellExcludedContexts == null || cellExcludedContexts.getCellType() ==
							  Cell.CELL_TYPE_BLANK) { logger.
							  warning("In Universe Joins sheet Excluded Contexts column value is Empty at row:"
							  + (row.getRowNum() + 1)); count++; } else { colum
							 nValueExcludedContexts = cellExcludedContexts.getStringCellValue();
								  }*/
							 

                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Universe Joins sheet Universe Extension column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                String[] universeExtensionArray = columnValueUniverseExtension.split(",");
                                for (String s : universeExtensionArray) {
                                    if (!universeExtensionSet.contains(s)) {
                                        logger.warning(
                                                "In Universe Joins sheet Universe Extension:" + s + "  is not valid at row:" + (row.getRowNum() + 1));
                                        count++;
                                    }
                                }
                            }
                            
                            if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(universeJionsSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Universe Joins Sheet");
                            		count++;
                            	}
                            }
                              
                        } // if row not Empty.
                    } // for every row
                    Boolean checker = false;
                    List<String> keylist = new ArrayList<>(keysSet);
                    List<String> trgkeyslist = new ArrayList<>(trgkeys);
                    List<String> srckeyslist = new ArrayList<>(srckeys);
                    Collections.sort(keylist);
                    Collections.sort(srckeyslist);
                    Collections.sort(trgkeyslist);
                    Iterator it = srckeyslist.iterator();
                    while (it.hasNext()) {
                        String temp = it.next().toString();
                        if (!keylist.contains(temp)) {
                            checker = true;
                        }
                    }
                    if (checker == true) {
                        Iterator trgit = trgkeyslist.iterator();
                        while (trgit.hasNext()) {
                            String trgtemp = trgit.next().toString();
                            if (!keylist.contains(trgtemp)) {
                                logger.warning(trgtemp + "Keys are not present in the keys sheet");
                                count++;
                            }
                        }
                    }
                    
                } else {
                    logger.warning("First row is Empty in Universe Joins Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("UniverseJoins sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tUniverse Joins Sheet is Fine");
            } else {
                logger.info("\tNo of observations in UniverseJoins Sheet:" + count);
                errorCountMap.put("Universe Joins Sheet", new Integer(count));
            }
            logger.info("\tEnd of UniverseJoins Sheet" + "\n");
        } // try
        catch (Exception e) {
            logger.severe("\tException in UniverseJoins Sheet:" + e + "\n");
            e.printStackTrace();
        }
        /*
          Computed Counters 
         *
         */
        try {
            logger.info("\tIn Computed Counters sheet");
            count = 0;
            if (computedCountersSheet != null) {
                XSSFCell cellClassName = null, cellObjectName = null, cellDescription = null, cellObjectType = null, cellQualification = null,
                        cellAggregation = null, cellSelect = null, cellParameters = null, cellWhere = null,
                        cellUniverseExtension = null;
                String columnNameClassName = "Class Name", columnNameObjectName = "Object Name", columnNameDescription = "Description",
                        columnNameObjectType = "Object Type", columnNameQualification = "Qualification", columnNameAggregation = "Aggregation",
                        columnNameSelect = "Select", columnNameParameters  = "Parameters",
                        columnNameWhere = "Where", columnNameUniverseExtension = "Universe Extension";
                String columnValueClassName = null, columnValueObjectName = null, columnValueDescription = null, columnValueObjectType = null,
                        columnValueQualification = null, columnValueAggregation = null, columnValueSelect= null, columnValueParameters = null,
                        columnValueWhere = null, columnValueUniverseExtension = null;
                int columnNoClassName, columnNoObjectName, columnNoDescription, columnNoObjectType, columnNoQualification, columnNoAggregation,
                        columnNoSelect, columnNoParameters, columnNoWhere, columnNoUniverseExtension;

                rowIterator = computedCountersSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoClassName = getColumnNo(firstRow, columnNameClassName);
                    columnNoObjectName= getColumnNo(firstRow, columnNameObjectName);
                    columnNoDescription = getColumnNo(firstRow,columnNameDescription );
                    columnNoObjectType = getColumnNo(firstRow, columnNameObjectType );
                    columnNoQualification = getColumnNo(firstRow, columnNameQualification );
                    columnNoAggregation= getColumnNo(firstRow, columnNameAggregation);
                    columnNoSelect = getColumnNo(firstRow, columnNameSelect);
                    columnNoParameters = getColumnNo(firstRow, columnNameParameters);
                    columnNoWhere = getColumnNo(firstRow, columnNameWhere);
                    columnNoUniverseExtension = getColumnNo(firstRow, columnNameUniverseExtension);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {

                            cellClassName = row.getCell(columnNoClassName);
                            if (cellClassName == null || cellClassName.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellClassName).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Class Name column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueClassName = cellClassName.getStringCellValue();
                                              }

                            cellObjectName = row.getCell(columnNoObjectName);
                                                       if (cellObjectName == null || cellObjectName.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellObjectName).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Object Name column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectName = cellObjectName.getStringCellValue();
                            }
                                                        cellDescription = row.getCell(columnNoDescription);
                            if (cellDescription == null || cellDescription.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellDescription).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Description column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueDescription = cellDescription.getStringCellValue();
                                                          }
                            cellObjectType = row.getCell(columnNoObjectType );
                            if (cellObjectType  == null || cellObjectType .getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellObjectType).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Object Type column value  is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectType = cellObjectType.getStringCellValue();
                                                        }
                            cellQualification = row.getCell(columnNoQualification);
                            if (cellQualification == null || cellQualification.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellQualification).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Qualification column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueQualification = cellQualification.getStringCellValue();
                            }

                            cellAggregation = row.getCell(columnNoAggregation);
                            if (cellAggregation == null || cellAggregation.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellAggregation).isEmpty()) {
                                logger.severe("In Computed Counters Sheet Aggregation column value value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueAggregation = cellAggregation.getStringCellValue();
                                                           }

                            cellSelect = row.getCell(columnNoSelect);
                            if (cellSelect == null || cellSelect.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellSelect).isEmpty()) {
                                logger.severe(
                                        "In Computed Counters Sheet Select column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueSelect = cellSelect.getStringCellValue();
                                                           }

                            cellParameters = row.getCell(columnNoParameters);
                            if (cellParameters == null || cellParameters.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellParameters).isEmpty()) {
                                logger.warning("In Computed Counters sheet Parameters column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;

                            } else {
                                columnValueParameters = cellParameters.getStringCellValue();
                               
                            }
                            cellWhere = row.getCell(columnNoWhere);
                            if (cellWhere == null || cellWhere.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellWhere).isEmpty()) {
                                logger.warning("In Computed Counters sheet Where column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueWhere = cellWhere.getStringCellValue();
                            }

                            cellUniverseExtension = row.getCell(columnNoUniverseExtension);
                            if (cellUniverseExtension == null || cellUniverseExtension.getCellType() == Cell.CELL_TYPE_BLANK || String.valueOf(cellUniverseExtension).isEmpty()) {
                                logger.severe("In Computed Counters sheet Universe Extension column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueUniverseExtension = cellUniverseExtension.getStringCellValue();
                                
                                }
                            }
                        if(row.getRowNum()==1) {  
                        	ArrayList<String> duplicateColumns = duplicateColumnCheck(computedCountersSheet);
                        	for(String s : duplicateColumns) {
                        		logger.severe("Column_Name "+s+" is a Duplicate Column in Computed Counters Sheet");
                        		count++;
                        	}
                        }
                        
                      } // if row not Empty.
                    } // for every row
                                     
                 else {
                    logger.warning("First row is Empty in Computed Counters Sheet");
                    count++;
                } // if First row
        }else {
                logger.warning("Computed Counters sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tComputed Counters Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Computed Counters Sheet:" + count);
                errorCountMap.put("Computed Counters", new Integer(count));
            }
            logger.info("\tEnd of Computed Counters Sheet" + "\n");
        
     // try
   }catch (Exception e) {
            logger.severe("\tException in Computed Counters Sheet:" + e + "\n");
            e.printStackTrace();
        }
        /*
         * Report objects Sheet
         */
        try {
            logger.info("\tIn Report objects sheet");
            count = 0;
            if (reportObjectsSheet != null) {
                XSSFCell cellFactTable = null, cellLevel = null, cellObjectClass = null, cellObjectName = null;
                String columnNameFactTable = "Fact Table", columnNameLevel = "Level", columnNameObjectClass = "Object Class",
                        columnNameObjectName = "Object Name";
                String columnValueFactTable, columnValueLevel = null, columnValueObjectClass = null, columnValueObjectName = null;
                int columnNoFactTable, columnNoLevel, columnNoObjectClass, columnNoObjectName;

                rowIterator = reportObjectsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoFactTable = getColumnNo(firstRow, columnNameFactTable);
                    columnNoLevel = getColumnNo(firstRow, columnNameLevel);
                    columnNoObjectClass = getColumnNo(firstRow, columnNameObjectClass);
                    columnNoObjectName = getColumnNo(firstRow, columnNameObjectName);

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellFactTable = row.getCell(columnNoFactTable);
                            if (cellFactTable == null || cellFactTable.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report objects sheet FactTable column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueFactTable = cellFactTable.getStringCellValue();
                            }

                            cellLevel = row.getCell(columnNoLevel);
                            if (cellLevel == null || cellLevel.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report objects sheet Level column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueLevel = cellLevel.getStringCellValue();
                            }

                            cellObjectClass = row.getCell(columnNoObjectClass);
                            if (cellObjectClass == null || cellObjectClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report objects sheet Object Class column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectClass = cellObjectClass.getStringCellValue();
                            }

                            cellObjectName = row.getCell(columnNoObjectName);
                            if (cellObjectName == null || cellObjectName.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report objects sheet ObjectName column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectName = cellObjectName.getStringCellValue();
                            }
                            
                            if(row.getRowNum()==1) {  //JIRA EQEV-59062 @
                            	ArrayList<String> duplicateColumns = duplicateColumnCheck(reportObjectsSheet);
                            	for(String s : duplicateColumns) {
                            		logger.severe("Column_Name "+s+" is a Duplicate Column in Report Objects Sheet");
                            		count++;
                            	}
                            }
                            
                        } // if row not Empty
                    } // for ecah row
                } else {
                    logger.warning("First row is Empty in Report Objects Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Report objects sheet is Empty");
                count++;
            } // if sheet
            if (count == 0) {
                logger.info("\tReport objects Sheet  is Fine");
            } else {
                logger.info("\tNo of observations in Report objects Sheet:" + count);
                errorCountMap.put("Report objects Sheet", new Integer(count));
            }
            logger.info("\tEnd of Report objects Sheet" + "\n");
        } catch (Exception e) {
            logger.warning("Exception in Report objects Sheet:" + e + "\n");
            e.printStackTrace();
        }

        /*
         * Report conditions Sheet
         */

        try {
            logger.info("\tIn Report conditions sheet");
            count = 0;
            if (reportConditionsSheet != null) {
                XSSFCell cellLevel = null, cellConditionClass = null, cellCondition = null, cellObjectCondition = null;
                XSSFCell cellPromptName[] = new XSSFCell[3], cellPromptValue[] = new XSSFCell[3];
                String columnNameLevel = "Level", columnNameConditionClass = "Condition Class", columnNameCondition = "Condition",
                        columnNameObjectCondition = "Object Condition",
                        columnNamePromptName[] = { "Prompt Name (1)", "Prompt Name (2)", "Prompt Name (3)" },
                        columnNamePromptValue[] = { "Prompt Value (1)", "Prompt Value (2)", "Prompt Value (3)" };
                String columnValueLevel = null, columnValueConditionClass = null, columnValueCondition = null, columnValueObjectCondition = null;
                String columnValuePromptName[] = new String[3], columnValuePromptValue[] = new String[3];
                int columnNoLevel, columnNoConditionClass, columnNoCondition, columnNoObjectCondition;
                int columnNoPromptName[] = new int[3], columnNoPromptValue[] = new int[3];

                rowIterator = reportConditionsSheet.iterator();
                firstRow = ((XSSFRow) rowIterator.next());

                if (!isEmptyRow(firstRow)) {
                    columnNoLevel = getColumnNo(firstRow, columnNameLevel);
                    columnNoConditionClass = getColumnNo(firstRow, columnNameConditionClass);
                    columnNoCondition = getColumnNo(firstRow, columnNameCondition);
                    columnNoObjectCondition = getColumnNo(firstRow, columnNameObjectCondition);
                    for (int n = 0; n < 3; n++) {
                        columnNoPromptName[n] = getColumnNo(firstRow, columnNamePromptName[n]);
                        columnNoPromptValue[n] = getColumnNo(firstRow, columnNamePromptValue[n]);
                    }

                    for (; rowIterator.hasNext();) {
                        row = (XSSFRow) rowIterator.next();
                        if (!isEmptyRow(row)) {
                            cellLevel = row.getCell(columnNoLevel);
                            if (cellLevel == null || cellLevel.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report conditions sheet Level column value is Empty at row:" + (row.getRowNum() + 1) + "  in"
                                        + columnNameLevel + "  column\n");
                                count++;
                            } else {
                                columnValueLevel = cellLevel.getStringCellValue();
                            }

                            cellConditionClass = row.getCell(columnNoConditionClass);
                            if (cellConditionClass == null || cellConditionClass.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report conditions sheet ConditionClass column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueConditionClass = cellConditionClass.getStringCellValue();
                            }

                            cellCondition = row.getCell(columnNoCondition);
                            if (cellCondition == null || cellCondition.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report conditions sheet Condition column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueCondition = cellCondition.getStringCellValue();
                            }
                            cellObjectCondition = row.getCell(columnNoObjectCondition);
                            if (cellObjectCondition == null || cellObjectCondition.getCellType() == Cell.CELL_TYPE_BLANK) {
                                logger.warning("In Report conditions sheet ObjectCondition column value is Empty at row:" + (row.getRowNum() + 1));
                                count++;
                            } else {
                                columnValueObjectCondition = cellObjectCondition.getStringCellValue();
                            }
                            //EQEV-114577 changed n<3 to n<1 to check only prompt name 1 and prompt value 1columns
                            for (int n = 0; n < 1; n++) {                               
                            	cellPromptName[n] = row.getCell(columnNoPromptName[n]);
                           
                                if (cellPromptName[n] == null || cellPromptName[n].getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.warning("In Report conditions sheet " + columnNamePromptName[n] + " column value is Empty at row:"
                                            + (row.getRowNum() + 1));
                                    count++;
                                } else {
                                    columnValuePromptName[n] = cellPromptName[n].getStringCellValue();
                                }
                                cellPromptValue[n] = row.getCell(columnNoPromptValue[n]);
                                if (cellPromptValue[n] == null || cellPromptValue[n].getCellType() == Cell.CELL_TYPE_BLANK) {
                                    logger.warning("In Report conditions sheet " + columnNamePromptValue[n] + " column value is Empty at row:"
                                            + (row.getRowNum() + 1));
                                    count++;
                                } else {
                                    columnValuePromptValue[n] = cellPromptValue[n].getStringCellValue();
                                }
                                
                                if(row.getRowNum()==1) {   //JIRA EQEV-59062 @
                                	ArrayList<String> duplicateColumns = duplicateColumnCheck(reportConditionsSheet);
                                	for(String s : duplicateColumns) {
                                		logger.severe("Column_Name "+s+" is a Duplicate Column in Report Conditions Sheet");
                                		count++;
                                	}
                                }
                            }
							for (int n = 1; n < 3; n++) {
								cellPromptName[n] = row.getCell(columnNoPromptName[n]);
								cellPromptValue[n] = row.getCell(columnNoPromptValue[n]);
								if ((!(cellPromptName[n] == null)
										&& !(cellPromptName[n].getCellType() == Cell.CELL_TYPE_BLANK))) {
									if ((cellPromptValue[n] == null
											|| cellPromptValue[n].getCellType() == Cell.CELL_TYPE_BLANK)) {
										logger.warning("In Report conditions sheet " + columnNamePromptValue[n]
												+ " column value is Empty at row:" + (row.getRowNum() + 1));
										cellPromptValue[n] = row.getCell(columnNoPromptValue[n]);
										count++;
									}

								}

							}
								//EQEV-114577
																		 
                        } // if row not Empty
                    } // for ecah row
                } else {
                    logger.warning("First row is Empty in Report Conditions Sheet");
                    count++;
                } // if First row
            } else {
                logger.warning("Report conditions sheet is Empty");
                count++;
            } // if sheet

            if (count == 0) {
                logger.info("\tReport Conditions Sheet is Fine");
            } else {
                logger.info("\tNo of observations in Report Conditions Sheet:" + count);
                errorCountMap.put("Report Conditions Sheet", new Integer(count));
            }
            logger.info("\tEnd of Report conditions Sheet" + "\n");
        } catch (Exception e) {
            logger.warning("Exception in Report conditions Sheet:" + e + "\n");
            e.printStackTrace();
        }
        if (!errorCountMap.isEmpty()) {
            logger.info("\tNo of observations in each sheet as follows");
            Set<String> errorSheet = errorCountMap.keySet();
            for (String s : errorSheet) {
                logger.info("\tNo of Observations:" + errorCountMap.get(s) + " in " + s);
            }

        }
    }

    public static String[] InFileVersion(File fName,String BuildNumber) throws FileNotFoundException 
    {
		Scanner s=new Scanner(fName);
		String Version="";
		while(s.hasNext())
		{
			String Temp=s.nextLine();
			if(Temp.contains("(("))
			{
				Pattern p=Pattern.compile("[((]([0-9]+)[))]");
				Matcher m=p.matcher(Temp);
				while(m.find())
					if(!BuildNumber.equals(m.group(1)))
					{
						if(Version.equals(""))
							Version=m.group(1);
						else
							Version = Version+";"+m.group(1);
			}}
		}
		s.close();
		if(Version.equals(""))
		return null;
		else
		return Version.split(";");
	}

	/**
     * @param parserName
     * @param tableName
     * @param vendorId
     * @param map1
     * @param tableVendorIdMap
     * @param logger
     * @return
     */
    public static void isDiffTableHaveSameVendorId(String parserName, String tableName, String vendorId,
                                                   LinkedHashMap<String, HashMap<String, String>> map1, Logger logger) {
        HashMap<String, String> newMap;
        if (map1.isEmpty()) {
            newMap = new HashMap<String, String>();
            if (vendorId.contains(";")) {
                String[] split = vendorId.split(";");
                
                for (int i = 0; i < split.length; i++) {
                    newMap.put(split[i], tableName);
                }
            } else {
                newMap.put(vendorId, tableName);
            }
            map1.put(parserName, newMap);
        } else {
            if (map1.containsKey(parserName)) {
                if (map1.get(parserName).containsKey(vendorId)) {
                	
                    if (!map1.get(parserName).get(vendorId).equals(tableName)) {
                        logger.severe(map1.get(parserName).get(vendorId) + " and " + tableName + " cannot have same vendor Id");
                    }
                } else {
                    map1.get(parserName).put(vendorId, tableName);
                }
            } else {
                newMap = new HashMap<String, String>();
                newMap.put(vendorId, tableName);
                map1.put(parserName, newMap);
            }
        }

    }

    public static boolean isDescriptionValid(String columnValueDescription, HashSet<String> descriptionSpecialCharacterSet) {
        boolean b = false;
        for (Iterator i = descriptionSpecialCharacterSet.iterator(); i.hasNext();) {
            String s = i.next().toString();
            String reg = ".*" + s + ".*";
            b = Pattern.matches(reg, columnValueDescription);
            if (b == true) {
                return b;
            } else {
                continue;
            }
        }

        return b;
    }

    public static boolean isValidName(String columnValue) {
        for (int i = 0; i < columnValue.length(); i++) {
            char c = columnValue.charAt(i);
            if (c == '_' || (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || (c >= 48 && c <= 57)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
        // return Pattern.matches("[a-zA-Z_0-9]+", columnValue);
    }

    public static int transformationsVerify(HashSet<String> transformationsTableSet, HashSet<String> factTableTransformationsSet,
                                            HashSet<String> topologyTableTransformationsSet, Logger logger) {
        int count = 0;
        //if (transformationsTableSet.size() != factTableTransformationsSet.size() + topologyTableTransformationsSet.size()) {
            logger.info("\tTransformation verification Details:");
            HashSet<String> factTableTransformationsSet1 = new HashSet<String>();
            HashSet<String> topologyTableTransformationsSet1 = new HashSet<String>();
            HashSet<String> transformationsTableSet1 = new HashSet<String>();
           
            factTableTransformationsSet1.addAll(factTableTransformationsSet);
            
            topologyTableTransformationsSet1.addAll(topologyTableTransformationsSet);
            transformationsTableSet1.addAll(transformationsTableSet);
            
            for (String s : transformationsTableSet) {
            	
                if (factTableTransformationsSet1.contains(s)) {
                    factTableTransformationsSet1.remove(s);
                    continue;
                } else if (topologyTableTransformationsSet1.contains(s)) {
                    topologyTableTransformationsSet1.remove(s);
                } 
                else {
                	//
                }
            }
            for (String s : factTableTransformationsSet) {
                if (transformationsTableSet1.contains(s)) {
                    transformationsTableSet1.remove(s);
                }
            }
            for (String s : topologyTableTransformationsSet) {
                if (transformationsTableSet1.contains(s)) {
                    transformationsTableSet1.remove(s);
                }
            }
            //EQEV-58207 previous+3932-3953
            if (factTableTransformationsSet1.size() > 0) {
                logger.info(" \tFollowing list has parser support is there in  Fact Tables sheet.but transformation support is not found");
                for (String s : factTableTransformationsSet1) {
                    logger.warning("Table:" + s + " is having support for parser in fact tables.but in transformations support is not found");
                    count++;
                }
            }
            if (topologyTableTransformationsSet1.size() > 0) {
                logger.info("Following list has parser support is there in  Topology Tables sheet.but transformation support is not found");
                for (String s : topologyTableTransformationsSet1) {
                    logger.warning("Table:" + s + " is having support for parser in topology tables.but in transformations support is not found");
                    count++;
                }
            }
            if (transformationsTableSet1.size() > 0) {
                logger.info(
                        "\tFollowing list has parser support is there in  Transformations sheet.but support is not in Fact Tables or Topology Tables sheet");
                for (String s : transformationsTableSet1) {
                    logger.warning("Table:" + s + " is having support for parser in transformations.but in fact tables or topology tables support is not found");
                    count++;
                }
            }
            logger.info("\tNo of observations in Transformation Verification:" + count);
            logger.info("\tEnd of Transformation verification \n");
        //}
        return count;
    }

    public static int dataFormatVerify(HashSet<String> dataFormatSet, HashSet<String> countersSet, HashSet<String> keysSet,
                                       HashSet<String> topologyKeysSet,HashSet<String> topologyTablesSet1, Logger logger) {
        int count = 0;  
        //logger.info(dataFormatSet.size()+"!="+countersSet.size()+"+"+keysSet.size()+"+"+topologyKeysSet.size());
        //if (dataFormatSet.size() != countersSet.size() + keysSet.size()+topologyKeysSet.size()) {
            logger.info("\tDataFormat verification Details:");
            //logger.info("Hiiiii");
            HashSet<String> dataFormat1Set = new HashSet<String>();
            HashSet<String> counters1Set = new HashSet<String>();
            HashSet<String> keys1Set = new HashSet<String>();
            HashSet<String> topologyKeys1Set = new HashSet<String>();
            HashSet<String> topologyTablesSet2 = new HashSet<String>();
            dataFormat1Set.addAll(dataFormatSet);
            counters1Set.addAll(countersSet);
            keys1Set.addAll(keysSet);
            topologyKeys1Set.addAll(topologyKeysSet);
            topologyTablesSet2.addAll(topologyTablesSet1);
            
            //System.out.println("Topology table set 1 size :"+topologyTablesSet1.size());
           // System.out.println("Topology table set 2 size :"+topologyTablesSet2.size());
            
            Iterator<String> dataFormatIterator = dataFormatSet.iterator();
            Iterator<String> countersSetIterator = countersSet.iterator();
            Iterator<String> keysSetIterator = keysSet.iterator();
            Iterator<String> topologyKeysSetIterator = topologyKeysSet.iterator();
           // Iterator<String> topologyTablesSetIterator = topologyTablesSet2.iterator();
            
            String dataFormat, counters, keys, topologyKeys, tempArr[], topologyTableData, topoTableArr[];

            for (; dataFormatIterator.hasNext();) {
                dataFormat = dataFormatIterator.next().toString();
                if (countersSet.contains(dataFormat)) {
                    counters1Set.remove(dataFormat);
                    continue;
                }
                if (keysSet.contains(dataFormat)) {
                    keys1Set.remove(dataFormat);
                    continue;
                }
                if (topologyKeysSet.contains(dataFormat)) {
                    topologyKeys1Set.remove(dataFormat);
                    continue;
                }
            }

            for (; countersSetIterator.hasNext();) {
                counters = countersSetIterator.next().toString();
                if (dataFormat1Set.contains(counters)) {
                    dataFormat1Set.remove(counters);
                }
            }
            for (; keysSetIterator.hasNext();) {
                keys = keysSetIterator.next().toString();
                if (dataFormat1Set.contains(keys)) {
                    dataFormat1Set.remove(keys);
                }
            }
            for (; topologyKeysSetIterator.hasNext();) {
                topologyKeys = topologyKeysSetIterator.next().toString();
                if (dataFormat1Set.contains(topologyKeys)) {
                    dataFormat1Set.remove(topologyKeys);
                }
            }

            if (dataFormat1Set.size() > 0) {
                logger.info("\tcounters or keys having support only in Data Format as follows");
                for (String s : dataFormat1Set) {
                    tempArr = s.split(":");
                    logger.warning("In Table:" + tempArr[0] + "  Counter(or)Key:" + tempArr[1] + "  having support only in Data Format Sheet");
                    count++;
                }
            }

            if (topologyKeys1Set.size() > 0) {
                logger.info("\tTopology keys having support only in topologyKeys as follows");
                //Iterator<String> topologyTableSetIterator = topologTableSet1.iterator();
                
                for (String s : topologyKeys1Set) {
                	//System.out.println("S :"+s);
                	tempArr = s.split(":");
                    //System.out.println("Topology table set 2 size for :"+topologyTablesSet2.size());

                    Iterator<String> topologyTablesSetIterator = topologyTablesSet2.iterator();

                	for (; topologyTablesSetIterator.hasNext();) {

                		topologyTableData = topologyTablesSetIterator.next().toString();
                		topoTableArr = topologyTableData.split(":");
                		// Changes for EQEV-128335
                		String attribute = "static";
            			//System.out.println("Equal :"+tempArr[0] +"=="+topoTableArr[0]);

                		if(tempArr[0].equals(topoTableArr[0])){
                			
                			//System.out.println("******* :"+tempArr[0] +"=="+topoTableArr[0]);
                			// Changes for EQEV-128335
                			if(attribute.equalsIgnoreCase(topoTableArr[1]) ) {
                			       //Do nothing
								//System.out.println("Equal :"+s);
								
                			}else {
 		               			logger.warning("In Table:" + tempArr[0] + "  topologyKey:" + tempArr[1] + "  having support only in topologyKeys Sheet");
                					count++;;
                			}
                		} 
                	}
                
                }
            }
            if (keys1Set.size() > 0) {
                logger.info("\tKeys having support only in Keys as follows");
                for (String s : keys1Set) {
                    tempArr = s.split(":");
                    logger.warning("In Table:" + tempArr[0] + "  Key:" + tempArr[1] + "  having support only in Keys Sheet");
                    count++;
                }
            }
            if (counters1Set.size() > 0) {
                logger.info("\tCounters or keys having support only in Counters as follows");
                for (String s : counters1Set) {
                    tempArr = s.split(":");
                    logger.warning("In Table:" + tempArr[0] + "  Counter:" + tempArr[1] + "  having support only in Counters Sheet");
                    count++;
                }
            }
            logger.info("\tNo of observations in DataFormat Verification:" + count);
            logger.info("\tEnd of DataFormat verification \n");
        //}
        return count;
    }

    public static boolean isKeyWord(String columnValue, HashSet<String> dbKeyWordsSet) {

        Iterator<String> keyIterator = dbKeyWordsSet.iterator();
        String key = null;
        while (keyIterator.hasNext()) {
            key = keyIterator.next().toString();
            if (key.equalsIgnoreCase(columnValue)) {
                return true;
            }
        }
        return false;
    }

    public static int getColumnNo(XSSFRow firstRow, String columnName) {

        int columnNo = -1;
        for (Cell c : firstRow) {
            if (c.getStringCellValue().equals(columnName)) {
                columnNo = c.getColumnIndex();
            }
        }
        return columnNo;
    }

    public static boolean isValidDataType(String s) {

        boolean b = Pattern.matches(
                "(varchar[(][0-9]+,[0-9]+[)]|integer[(][0-9]+,[0-9]+[)]|numeric[(][0-9]+,[0-9]+[)]|int[(].*[)]|tinyint[(].*,.*[)]|smallint[(].*[)]|bigint[(].*,.*[)]|unsigned int[(][0-9]+,[0-9]+[)]|long[(].*[)] |double[(].*[)]|datetime|datetime[(].*[)]|date|date[(].*[)]|char[(].*[)]|float[(].*[)]|unsigned bigint[(].*[)]|bit[(].*[)])",
                s);
        return b;
    }

    public static boolean isEmptyRow(Row row) {
        int count = 0;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                count++;
            }
        }
        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getColumnValue(XSSFCell cell) {
        String s = null;
        Double d = null;
        Integer i = null;
        switch (cell.getCellType()) {
            case 0:
                d = cell.getNumericCellValue();
                i = d.intValue();
                s = i.toString();
                break;
            case 1:
                s = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return s;
    }

    public static boolean isValidCardinality(String s) {
        Boolean flag = false;
        if (s.equals("n_to_1") || s.equals("1_to_n") || s.equals("n_to_n") || s.equals("1_to_1")) {
            flag = true;
        }
        return flag;
    }
    
    public static ArrayList<String> duplicateColumnCheck(XSSFSheet sheet)  //JIRA EQEV-59062 @
    {
    	XSSFRow firstRow = null;
        Iterator<Row> rowIterator = null;
    	rowIterator = sheet.iterator();
    	firstRow = ((XSSFRow) rowIterator.next());
        HashSet<String> sheetColumnNames =  new HashSet<String>();
        ArrayList<String> duplicateColumns = new ArrayList<String>();
        int size = 0;
        for(int i=0;i<firstRow.getLastCellNum();i++) {
        	if(!String.valueOf(firstRow.getCell(i)).isEmpty()) {
        		sheetColumnNames.add(firstRow.getCell(i).getStringCellValue());
        		if(sheetColumnNames.size()==size)
        			duplicateColumns.add(firstRow.getCell(i).getStringCellValue());
        		else
        			size++;
        	}
        }
        return duplicateColumns;
    }
    
    public static void checkEqualToSymbol(XSSFWorkbook workbook, Logger logger) {

		int count = 0;
		for (XSSFSheet sheet : workbook) {
			for (Row row : sheet) {
				for (Cell cell : row) {
					if ((cell != null) && (cell.getCellType() != Cell.CELL_TYPE_BLANK)
							&& (!String.valueOf(cell).isEmpty())) {
						if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
							logger.severe(
									"In " + sheet.getSheetName() + " Sheet, Cell Value starts with '=' at column: "
											+ (cell.getColumnIndex() + 1) + " and row: " + (row.getRowNum() + 1));
							count++;
						} else {
							cell.setCellType(1);
							if (cell.getStringCellValue().trim().startsWith("=")) {
								logger.severe(
										"In " + sheet.getSheetName() + " Sheet, Cell Value starts with '=' at column: "
												+ ((cell.getColumnIndex() + 1)) + " and row: " + (row.getRowNum() + 1));
								count++;
							}
						}
					}
				}
			}
		}
		if (count == 0) {
			logger.info("\tFD is Fine for '=' condition");
		} else {
			logger.info("\tNo of observations in Model-T for '=' condition:" + count);
		}
		logger.info("\tEnd of Verification for '=' condition in Model-T" + "\n");
	}
   

}
