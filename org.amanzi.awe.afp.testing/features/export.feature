Feature: Afp Exporter
  Test the Afp exporter code
 
  Scenario: Success Case
    Given the control file with dataset at relative path files/dataset1/input_control.awe
    
    When the createCarrierFile function is executed
    Then the output carrierFile should match the expected file at relative path files/data_exporter/cell_expected.awe
    
    When the createControlFile function is executed
    Then the output controlFile should match the expected file at relative path files/data_exporter/control_expected.awe  
    
    When the createNeighboursFile function is executed
    Then the output neighboursFile should match the expected file at relative path files/data_exporter/neighbours_expected.awe
    
    When the createInterferenceFile function is executed
    Then the output interferenceFile should match the expected file at relative path files/data_exporter/interference_expected.awe
  
    When the createExceptionFile function is executed
    Then the output exceptionFile should match the expected file at relative path files/data_exporter/exception_expected.awe
    
    When the createForbiddenFile function is executed
    Then the output forbiddenFile should match the expected file at relative path files/data_exporter/forbidden_expected.awe
    