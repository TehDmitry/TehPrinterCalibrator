# TehPrinterCalibrator

Pattern generator for 3D printer calibration 

* TemperatureTowerTest
* FlowRateWeightTest 
* RetractOnLongDistanceTest

## Usage

* Extract the archive and launch run.cmd or run.sh
* Open the configuration.json and fill printer configuration with known settings
* Modify test configuration sections
* Launch again
* Print g-code from the test folder

## Tests
### TemperatureTowerTest
Print tower and try to break it. Test for determine themperature interval with best layer adhesion.

### FlowRateWeightTest
[Table for calculation and making graph](https://docs.google.com/spreadsheets/d/1hF5ha3kG58xGCr2bpJKHqIpLiIGtMzdB3o2MIJ43QDI/edit?usp=sharing)

### RetractOnLongDistanceTest
Test for determine retraction speed and retraction distance with minimum oozing.
