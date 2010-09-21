Feature: Afp Exporter
  Test the Afp exporter code
 
  Scenario: Success Case
    Given the control file with dataset at relative path files/dataset1/input_control.awe
    When the createInterferenceFile function is executed
    Then the output interferenceFile should match the expected file at relative path files/data_exporter/interference_expected.awe
    
    