Feature: Afp Engine
  Test the Afp C/C++ engine for valid and invalid input parameters/data sets
 
  Scenario: Success Case, Given valid input, a valid output is expected
    Given the afp engine executable at relative path files/japa_awe.exe
    And dataset with control file at relative path files/dataset1/input_control.awe
    And the actual output file will be at relative path files/dataset1/output.awe
    When the engine is executed
    Then the output file should match the expected file at relative path files/dataset1/expected_output.awe
    
 Scenario: Failure Case, Invalid Input Control file
    Given the afp engine executable at relative path files/japa_awe.exe
    And dataset with control file at relative path files/dataset2/input_control.awe
    And the actual output file will be at relative path files/dataset2/output.awe
    When the engine is executed
    Then the output file should not be generated
 
 Scenario: Failure Case, No Input Control file
 
 
 Scenario: Failure Case, Invalid Cell/Network File
 
