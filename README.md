# 2023_TER_detectionGaz

- [2023\_TER\_detectionGaz](#2023_ter_detectiongaz)
  - [Arduino installation](#arduino-installation)
      - [Step 1: ESP32 WROOM](#step-1-esp32-wroom)
      - [Step 2: MQ2 library](#step-2-mq2-library)
      - [Step 3: Connect the sensor and the card](#step-3-connect-the-sensor-and-the-card)
      - [Step 4: Copy the code](#step-4-copy-the-code)
      - [Step 5: Upload](#step-5-upload)
  - [Set up the server](#set-up-the-server)
      - [Step 1: node.js installation](#step-1-nodejs-installation)
      - [Step 2: mongodb database installation](#step-2-mongodb-database-installation)
      - [Step 3: Run the server](#step-3-run-the-server)
  - [Download the app](#download-the-app)
      - [Download and open the code](#download-and-open-the-code)
      - [Configure](#configure)
        - [Configure your IP](#configure-your-ip)
      - [Configure your map](#configure-your-map)
      - [Create the apk](#create-the-apk)


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
- Copy the code in Gas_Sensor_Arduino/Gas_Sensor_Arduino.ino to your new file.
- Verify by clicking on Sketch > Verify/Compile. If you have errors, check if you followed each step and restart the Arduino software.

#### Step 5: Upload

- Connect the Arduino cord to your computer where the Arduino Software is installed. **Caution**: if you didn't follow the **step 3** well, you may **damage** your sensor and your card.
- Upload by clicking on Sketch > Upload
- Once the code uploaded, click on Tools > Serial Monitor. The sensor needs time to heat, so the values might not be true at the beginning. You can press the EN button on your card to reboot the program.

## Set up the server

Required:
- A computer or a server that will be running 24h/24

#### Step 1: node.js installation 

Our tutorial works for Windows OS. 
- Install the [node.js installer](https://nodejs.org/en/download). Select LTS then Windows Installer.
- Once installed, look for your msi file and open it. 
- Click on Next then on the checkbox "I accept the terms in the Licence Agreement". Click on Next.
- You can see where node.js will be installed. Click on Next.
- Don't change anything and click on Next.
- You're offered to install tools but they're not needed. Click on Next.
- Click on Install.
- After the loading bar disappear, node.js is installed. Click on Finish.

#### Step 2: mongodb database installation

- Install [MongoDB Community Server](https://www.mongodb.com/try/download/community)
- Once it's installed, look for your msi file and open it.
- A new Window is open. Click on Next.
- Click on the checkbox to accept the Terms of Agreement then click on Next.
- Click on Complete
- Make sure "Install MongoDB as a Service" and "Run service as Network Service user" are selected. Leave "Service Name" field, "Data Directory" field ans "Log Directory" field to their default values. Click Next.
- Make sure "Install MongoDB Compass" is selected. Click on Next.
- Click on Install.
- Once the installation is finished, check if it's insalled. The default directory is C:/Program Files/MongoDB/Server/bin. There should be the mongo.exe file.
- Open your file explorer. Go in the C: folder.
- Create a new folder (use the right click, then select New folder). Name the new folder data then go in it.
- Create a new folder and name it db. You can close your file explorer. 
- Open MongoDBCompass.
- Click on New connection.
- Click on Save & Connect. Name it Alert and choose the color you want.

#### Step 3: Run the server

- Download the server code in the Gas_Sensor_Server (the files ```server.js``` and ```package.json```). Check if both the files are in the same folder on your computer. 
- Open the node.js command prompt (you can find it if type it in the windows search bar).
- Using the ```cd``` command, go to the file where you downloaded the server code.
- Type ```npm install```.
- Run the server by typing ```node ./server.js```.
- Open MongoDB Compass. Double click on the Alert in the Saved connections. 

## Download the app

Required:
- An android smartphone (the application does not work on a IOS smartphone)
- [Android Studio]() installed on your computer

#### Download and open the code

#### Configure

- Open the config.json

##### Configure your IP

- Open the command prompt (you can find it if type it in the windows search bar)
- Find your IPv4 by typing ```ipconfig```.
- Replace the baseUrl "XXX.XXX.XXX.XXX" in the config.json with your ip found next to "Wi-Fi card".

#### Configure your map

#### Create the apk
