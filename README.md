- [2023\_TER\_detectionGaz](#2023_ter_detectiongaz)
  - [Arduino installation](#arduino-installation)
      - [Step 1: ESP32 WROOM](#step-1-esp32-wroom)
      - [Step 2: MQ2 library](#step-2-mq2-library)
      - [Step 3: Connect the sensor and the card](#step-3-connect-the-sensor-and-the-card)
      - [Step 4: Copy the code](#step-4-copy-the-code)
      - [Step 5: Upload](#step-5-upload)


# 2023_TER_detectionGaz

## Arduino installation

Required:
- 1 ESP 32 Arduino Card
- 1 MQ2 Sensor
- a OTG cable to link the computer and the Arduino card
- 3 wires to link the Arduino card with the MQ2 sensor
- [Arduino 1.8](https://www.arduino.cc/en/software) installed on your computer

#### Step 1: ESP32 WROOM

- Click on File > Preferences
- In the Additional Boards Manager URLs, write the following url : ```https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json```
- Open Boards Manager from Tools > Board > Board Manager... and install *esp32* platform. You can write ```esp32``` in the search bar to find it.
- You can now select your esp32 card in Tools > Board > ESP32 Arduino. This project has been made with ESP32-WROOM-DA Module. 

#### Step 2: MQ2 library

- Click on Tools > Manage Libraries. The arduino software is looking for libraries and it may slow down. 
- Write MQ2 in the search bar. 
- Install the MQUnifiedsensor library.
- Click [here](https://github.com/labay11/MQ-2-sensor-library/archive/refs/heads/master.zip) to download the MQ2 library
- Click on Sketch > Include Library > Add .ZIP Library... and look for the file you downloaded (it should be in the Download folder). Click on it and click on Open.

#### Step 3: Connect the sensor and the card

Use the 3 wires to connect:
- sensor's GND port to arduino card's GND port
- sensor's VCC port to arduino card's VCC port (it might miss a label to this port, check your card's documentation)
- sensor's A0 port to arduino card's IO2 port

#### Step 4: Copy the code

- Click on File > New to create a new file. 
- Copy the code in path/to/our/code to your new file
- Verify by clicking on Sketch > Verify/Compile. If you have errors, check if you followed each step and restart the Arduino software.

#### Step 5: Upload

- Connect the Arduino cord to your computer where the Arduino Software is installed. **Caution**: if you didn't follow the **step 3** well, you may **damage** your sensor and your card.
- Upload by clicking on Sketch > Upload
- Once the code uploaded, click on Tools > Serial Monitor. The sensor needs time to heat, so the values might not be true at the beginning. You can press the EN button on your card to reboot the program.
