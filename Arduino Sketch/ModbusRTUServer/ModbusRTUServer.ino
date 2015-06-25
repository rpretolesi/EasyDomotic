/*
 Modbus RTU Server

 Circuit:
 * WiFi shield attached

 created 10 june 2015
 by Riccardo Pretolesi


 */

#include <SPI.h>
#include <WiFi.h>

char m_ssid[] = "PretolesiWiFi";          //  your network SSID (name)
char m_pass[] = "01234567";   // your network password

int status = WL_IDLE_STATUS;
int m_ServerTCPPort = 502;
WiFiServer m_server(m_ServerTCPPort);

boolean m_bOneShotClientConnected = false;
boolean m_bOneShotClientDisconnected_1 = false;
boolean m_bOneShotClientDisconnected_2 = false;

byte m_byteReadMBAP[6] = {0};
byte m_byteReadMBMsg[32] = {0};
boolean m_bModbusMBAP = false;
unsigned int m_uiModbusMBAPLength = 0;

byte m_byteToWriteMBAPMsg[32] = {0};
unsigned int m_uiNrByteToWrite = 0;

// Modbus PDU for answer
byte m_byteOutModbusPDU[32] = {0};

// Bluetooth Communication
#include <SoftwareSerial.h>

#define  BT_RX 5            // PIN TO receive from bluetooth
#define  BT_TX 3            // PIN TO transmit to bluetooth

SoftwareSerial m_btSerial(BT_RX, BT_TX);
byte m_bytebtReadData[32] = {0};
unsigned int m_uibtReadDataLength = 0;
byte m_bytebtWriteData[32] = {0};
unsigned int m_uibtWriteDataLength = 0;
byte m_bytebtPDUWriteData[32] = {0};
unsigned int m_uibtPDUWriteDataLength = 0;

unsigned long m_ulbtRecDataTime = 0;
boolean m_bbtDataAvailable = false;
boolean m_bbtDataNotAvailable = false;
boolean m_bbtDataCompleted = false;
// Create union of shared memory space
union {
  short temp_short;
  byte temp_bytearray[2];
} m_u_CRC;

// Create union of shared memory space
union {
  short temp_short[20];
  long temp_long[10];
  float temp_float[10];
  byte temp_bytearray[40];
} m_union_share_mem;

void setup() {
  // Deselect SD Card
  pinMode(4, OUTPUT);
  digitalWrite(4, 1);

  // Set as Output mode
  pinMode(3, OUTPUT);
  pinMode(5, INPUT);
  // pinMode(6, OUTPUT);
  // pinMode(9, OUTPUT);

  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  Serial.println("Begin Setup");
/*
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue:
    while (true);
  }

  String fv = WiFi.firmwareVersion();
  if ( fv != "1.1.0" ) {
    Serial.println("Please upgrade the firmware");
  } else {
    Serial.print(F("Firmware Version: "));
    Serial.println(WiFi.firmwareVersion());
  }

  // attempt to connect to Wifi network:
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(m_ssid);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
    status = WiFi.begin(m_ssid, m_pass);

    // wait 10 seconds for connection:
    delay(10000);
  }

  // start the server:
  m_server.begin();
  // you're connected now, so printout the status:
  printWifiStatus();
  */
  // Initialize Bluetooth SoftwareSerial port for selected data speed
  m_btSerial.begin(9600);

  Serial.println("End Setup");

}

void loop() {

  // Bluetooth
  if (m_btSerial.available() > 0){
    // After 4 ms i suppose that all data are received
    m_bbtDataNotAvailable = false;
    if(m_bbtDataAvailable == false){
      m_bbtDataAvailable = true;
      m_uibtReadDataLength = 0;
      m_ulbtRecDataTime = micros();    
      Serial.println("Data available.");
    }
    if(m_ulbtRecDataTime + 4000 <= micros()){
      Serial.println("Bletooth data...");
      while(m_btSerial.available()){
        m_bytebtReadData[m_uibtReadDataLength] = m_btSerial.read();
        Serial.print(m_bytebtReadData[m_uibtReadDataLength]);  
        Serial.print(" ");  
        m_uibtReadDataLength = m_uibtReadDataLength + 1;
      }
      Serial.println("");
      m_bbtDataCompleted = true;
      m_u_CRC.temp_short = getCRC(m_bytebtReadData, m_uibtReadDataLength - 2);
      if((m_bytebtReadData[m_uibtReadDataLength - 2] == m_u_CRC.temp_bytearray[1]) && (m_bytebtReadData[m_uibtReadDataLength - 1] == m_u_CRC.temp_bytearray[0])){

        // Data completed successfully
        Serial.println("CRC Ok!");
        processModbusPDU(&m_bytebtReadData[1], m_uibtReadDataLength - 3, &m_bytebtPDUWriteData[0], &m_uibtPDUWriteDataLength);

        // Prepare answer
        m_bytebtWriteData[0] = m_bytebtReadData[0]; // Address
        m_uibtWriteDataLength = 1;
        
        memcpy(&m_bytebtWriteData[1], &m_bytebtPDUWriteData[0], m_uibtPDUWriteDataLength); // PDU
        m_uibtWriteDataLength = m_uibtWriteDataLength + m_uibtPDUWriteDataLength;
        
        m_u_CRC.temp_short = getCRC(m_bytebtWriteData, m_uibtWriteDataLength); // CRC
        m_bytebtWriteData[m_uibtWriteDataLength] = m_u_CRC.temp_bytearray[1]; // CRC
        m_uibtWriteDataLength = m_uibtWriteDataLength + 1;
        m_bytebtWriteData[m_uibtWriteDataLength] = m_u_CRC.temp_bytearray[0]; // CRC
        m_uibtWriteDataLength = m_uibtWriteDataLength + 1;
        
        Serial.println("Answer Start: ");
        for (int index_0 = 0; index_0 < m_uibtWriteDataLength; index_0++) {
          m_btSerial.write(m_bytebtWriteData[index_0]);
          Serial.print(m_bytebtWriteData[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");
        Serial.println("Answer End. ");        
      }
    }
  } else {
    m_bbtDataAvailable = false;
    if(m_bbtDataNotAvailable == false){
      m_bbtDataNotAvailable = true;
      m_uibtReadDataLength = 0;
      Serial.println("Data NOT available.");
      Serial.println("");
    }
  }
/*
  // put your main code here, to run repeatedly:
  WiFiClient client = NULL;//m_server.available();
  if (client != NULL) {
    if (client.connected()) {
      
      m_bOneShotClientDisconnected_1 = false;
      m_bOneShotClientDisconnected_2 = false;

      if (m_bOneShotClientConnected == false) {
        m_bOneShotClientConnected = true;

        // clear input buffer:
        // Don't use this function.
        // If after the connection the client send immediatly a frame, this will be removed from this instruction
        // because 'm_server.available()' take some while befor return and this time it's enaught long to do the mess
        //        m_client.flush();

        // Init buffer data
        initValue();

        Serial.println("Client Connected.");
      }

      // Read and write operation....
      // Get MBAP
      if (client.available() >= 6 && m_bModbusMBAP == false) {
        // Parse message in order to get MBAP Header
        Serial.println(" ");
        Serial.println("MBAP Start: ");
        for (int index_0 = 0; index_0 < 6; index_0++) {
          m_byteReadMBAP[index_0] = client.read();
          Serial.print(m_byteReadMBAP[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");

        unsigned int uiModbusMBAPTransactionID = getShortFromBytes(&m_byteReadMBAP[0]);
        Serial.print("Transaction ID: ");
        Serial.println(uiModbusMBAPTransactionID);

        unsigned int uiModbusMBAPProtocolID = getShortFromBytes(&m_byteReadMBAP[2]);
        Serial.print("Protocol ID: ");
        Serial.println(uiModbusMBAPProtocolID);

        m_uiModbusMBAPLength = getShortFromBytes(&m_byteReadMBAP[4]);
        Serial.print("Length: ");
        Serial.println(m_uiModbusMBAPLength);

        Serial.println("MBAP End.");

        if ((uiModbusMBAPProtocolID != 0) || (m_uiModbusMBAPLength < 5) || (m_uiModbusMBAPLength > 254)) {
          // Errore, scarico tutto
          initValue();
          client.flush();
        } else {
          m_bModbusMBAP = true;
        }

        if (client.available() >= m_uiModbusMBAPLength && m_bModbusMBAP == true) {
          Serial.println("MBM Start: ");
          for (int index_0 = 0; index_0 < m_uiModbusMBAPLength; index_0++) {
            m_byteReadMBMsg[index_0] = client.read();
            Serial.print(m_byteReadMBMsg[index_0]);
            Serial.print(" ");
          }
          Serial.println(" ");
          Serial.println("MBM End: ");

          Serial.println("Data Start: ");
          // Messaggio Completo, estraggo i dati:
          byte byteModbusUnitIdentifier = m_byteReadMBMsg[0];
          Serial.print("Unit Identifier: ");
          Serial.println(byteModbusUnitIdentifier);

          byte byteModbusFunctionCode = m_byteReadMBMsg[1];
          Serial.print("Function Code: ");
          Serial.println(byteModbusFunctionCode);

          // Tutto Ok, costruisco la risposta, 1Â° parte
          // Intestazione
          m_byteToWriteMBAPMsg[0] = m_byteReadMBAP[0]; // Transaction Identifier
          m_byteToWriteMBAPMsg[1] = m_byteReadMBAP[1]; // Transaction Identifier
          m_uiNrByteToWrite = 2;
          m_byteToWriteMBAPMsg[2] = m_byteReadMBAP[2]; // Protocol Identifier
          m_byteToWriteMBAPMsg[3] = m_byteReadMBAP[3]; // Protocol Identifier
          m_uiNrByteToWrite = m_uiNrByteToWrite + 2;

          // Begin....
          boolean bFunctionCodeOk = false;
          boolean bRegisterAndByteCountOk = false;
          boolean bAddressOk = false;

          // Function
          if (byteModbusFunctionCode == 0x10 || byteModbusFunctionCode == 0x03) {
            bFunctionCodeOk = true;
          }

          // Address
          // Write Multiple Register
          if (byteModbusFunctionCode == 0x10) {
            // Check Data
            short shortQuantityOfRegisters = getShortFromBytes(&m_byteReadMBMsg[4]);
            byte byteByteCount = m_byteReadMBMsg[6];
            Serial.print("Quantity of Register: ");
            Serial.println(shortQuantityOfRegisters);
            Serial.print("Byte Count: ");
            Serial.println(byteByteCount);

            if (byteByteCount == (2 * shortQuantityOfRegisters)) {
              bRegisterAndByteCountOk = true;
            }

            if (bRegisterAndByteCountOk == true) {
              unsigned short ushortModbusAddress = getShortFromBytes(&m_byteReadMBMsg[2]);
              Serial.print("Address: ");
              Serial.println(ushortModbusAddress);

              if (ushortModbusAddress + shortQuantityOfRegisters < 20) {
                bAddressOk = true;

                // Copy data to union
                memcpy(&m_union_share_mem.temp_bytearray[ushortModbusAddress], &m_byteReadMBMsg[7], byteByteCount);

                // Print data read
                Serial.print("Short: ");
                union {
                  short sh;
                  byte shByteTemp[2];
                } ush;
                memcpy(ush.shByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 2);
                reverseByteArray(ush.shByteTemp, 0, 1);
                Serial.print(ush.sh);

                Serial.print(", Long: ");
                union {
                  long l;
                  byte lByteTemp[4];
                } ul;
                memcpy(ul.lByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
                reverseByteArray(ul.lByteTemp, 0, 3);
                Serial.print(ul.l);


                Serial.print(", Float: ");
                union {
                  float f;
                  byte fByteTemp[4];
                } uf;
                memcpy(uf.fByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
                reverseByteArray(uf.fByteTemp, 0, 3);
                Serial.print(uf.f);

                Serial.println(" ");

                // You can use here the values!!!!


                // Answer
                if (bAddressOk == true) {
                  // Tutto Ok, costruisco la risposta, 2Â° parte
                  short shortMBAPMsgLength = 6;
                  m_byteToWriteMBAPMsg[4] = (shortMBAPMsgLength >> 8) & 0xFF; // Lenght
                  m_byteToWriteMBAPMsg[5] = shortMBAPMsgLength & 0xFF; // Lenght
                  m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                  m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
                  m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                  m_byteToWriteMBAPMsg[7] = m_byteReadMBMsg[1]; // Function code
                  m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                  m_byteToWriteMBAPMsg[8] = m_byteReadMBMsg[2]; // Address
                  m_byteToWriteMBAPMsg[9] = m_byteReadMBMsg[3]; // Address
                  m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                  m_byteToWriteMBAPMsg[10] = m_byteReadMBMsg[4]; // Quantity of registers
                  m_byteToWriteMBAPMsg[11] = m_byteReadMBMsg[5]; // Quantity of registers
                  m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                }
              }
            }
          }

          if (byteModbusFunctionCode == 0x03) {
            short shortMBAPMsgLength = 0;
            unsigned short ushortModbusAddress = getShortFromBytes(&m_byteReadMBMsg[2]);
            Serial.print("Starting Address: ");
            Serial.println(ushortModbusAddress);
            // Quantity of Registers
            short shortQuantityOfRegisters = getShortFromBytes(&m_byteReadMBMsg[4]);
            Serial.print("Quantity of Register: ");
            Serial.println(shortQuantityOfRegisters);
            if (shortQuantityOfRegisters >= 1 && shortQuantityOfRegisters <= 125) {
              if (ushortModbusAddress + shortQuantityOfRegisters < 20) {
                bAddressOk = true;

                shortMBAPMsgLength = 3 + (2 * shortQuantityOfRegisters);

                // Get data from union
                memcpy(&m_byteToWriteMBAPMsg[9], &m_union_share_mem.temp_bytearray[ushortModbusAddress], (2 * shortQuantityOfRegisters));

                m_uiNrByteToWrite = m_uiNrByteToWrite + (2 * shortQuantityOfRegisters);
              }
            }

            if (bAddressOk == true) {
              // Tutto Ok, costruisco la risposta, 2Â° parte
              m_byteToWriteMBAPMsg[4] = (shortMBAPMsgLength >> 8) & 0xFF; // Lenght
              m_byteToWriteMBAPMsg[5] = shortMBAPMsgLength & 0xFF; // Lenght
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
              m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[7] = m_byteReadMBMsg[1]; // Function code
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[8] = (byte)(2 * shortQuantityOfRegisters); // Byte Count (2 x uiQuantityOfRegister)
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
            }
          }

          if (bFunctionCodeOk == true) {
            if (bAddressOk == true) {  
*/              
              /*
              if(bValueOk == true) {
              } else {
                // Exception
                // Bad Value
                unsigned int iMBAPMsgLength = 3;
                m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
                m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
                m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
                m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBMsg[1] + 0x80); // Error code
                m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                m_byteToWriteMBAPMsg[8] = 0x03; // Exception code
                m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              }
              */     
/*              
            } else {
              // Exception
              // Bad Address
              unsigned int iMBAPMsgLength = 3;
              m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
              m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
              m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBMsg[1] + 0x80); // Error code
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[8] = 0x02; // Exception code
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
            }
          } else {
            // Bad Function code
            unsigned int iMBAPMsgLength = 3;
            m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
            m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
            m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
            m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
            m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
            m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBMsg[1] + 0x80); // Error code
            m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
            m_byteToWriteMBAPMsg[8] = 0x01; // Exception code
            m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
          }
        }

        // Risposta completa, la invio
        Serial.println("Nr of bytes to write: ");
        Serial.println(m_uiNrByteToWrite);

        Serial.println("Answer Start: ");
        for (int index_0 = 0; index_0 < m_uiNrByteToWrite; index_0++) {
          client.write(m_byteToWriteMBAPMsg[index_0]);
          Serial.print(m_byteToWriteMBAPMsg[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");
        Serial.println("Answer End. ");

        // Operazione Terminata
        initValue();

      }      
    }
    else
    {
      m_bOneShotClientConnected = false;

      if (m_bOneShotClientDisconnected_1 == false)
      {
        m_bOneShotClientDisconnected_1 = true;

        Serial.println("Client Disconnected.");

        initValue();
      }
    }     
  }
  else
  {
    m_bOneShotClientConnected = false;

    if (m_bOneShotClientDisconnected_2 == false)
    {
      m_bOneShotClientDisconnected_2 = true;

      Serial.println("Client Null.");

      initValue();
    }
  } 
*/  
}

void initValue() {

  // Initializing the Value
  m_bModbusMBAP = false;

  for (int index_0 = 0; index_0 < 6; index_0++) {
    m_byteReadMBAP[index_0] = 0;
  }

  for (int index_0 = 0; index_0 < 32; index_0++) {
    m_byteReadMBMsg[index_0] = 0;
    m_byteToWriteMBAPMsg[index_0] = 0;
  }

  // Output
  //  analogWrite(3, 0);
  //  analogWrite(5, 0);
  //  analogWrite(6, 0);
  //  analogWrite(9, 0);

}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("TCP/IP Address: ");
  Serial.println(ip);
  Serial.print("TCP/IP Port: ");
  Serial.println(m_ServerTCPPort);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");

}

// Process MODBUS PDU
void processModbusPDU(byte byteInModbusPDU[], int iInlenght, byte byteOutModbusPDU[], unsigned int *iOutlenght){

  boolean bFunctionCodeOk = false;
  boolean bRegisterAndByteCountOk = false;
  boolean bAddressOk = false;
  byte byteModbusFunctionCode = byteInModbusPDU[0];
  Serial.print("Function Code: ");
  Serial.println(byteModbusFunctionCode);

  // Function
  if (byteModbusFunctionCode == 0x10 || byteModbusFunctionCode == 0x03) {
    bFunctionCodeOk = true;
  }

  if(bFunctionCodeOk == true) {

    // Address
    // Write Multiple Register
    if (byteModbusFunctionCode == 0x10) {
      // Check Data
      short shortQuantityOfRegisters = getShortFromBytes(&byteInModbusPDU[3]);
      byte byteByteCount = byteInModbusPDU[5];
      Serial.print("Quantity of Register: ");
      Serial.println(shortQuantityOfRegisters);
      Serial.print("Byte Count: ");
      Serial.println(byteByteCount);
  
      if (byteByteCount == (2 * shortQuantityOfRegisters)) {
        bRegisterAndByteCountOk = true;
      }
  
      if (bRegisterAndByteCountOk == true) {
        unsigned short ushortModbusAddress = getShortFromBytes(&byteInModbusPDU[1]);
        Serial.print("Address: ");
        Serial.println(ushortModbusAddress);
  
        if (ushortModbusAddress + shortQuantityOfRegisters < 20) {
          bAddressOk = true;
  
          // Copy data to union
          memcpy(&m_union_share_mem.temp_bytearray[ushortModbusAddress], &byteInModbusPDU[6], byteByteCount);
  
          // Print data read
          Serial.print("Short: ");
          union {
            short sh;
            byte shByteTemp[2];
          } ush;
          memcpy(ush.shByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 2);
          reverseByteArray(ush.shByteTemp, 0, 1);
          Serial.print(ush.sh);
  
          Serial.print(", Long: ");
          union {
            long l;
            byte lByteTemp[4];
          } ul;
          memcpy(ul.lByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
          reverseByteArray(ul.lByteTemp, 0, 3);
          Serial.print(ul.l);
   
          Serial.print(", Float: ");
          union {
            float f;
            byte fByteTemp[4];
          } uf;
          memcpy(uf.fByteTemp, &m_union_share_mem.temp_bytearray[ushortModbusAddress], 4);
          reverseByteArray(uf.fByteTemp, 0, 3);
          Serial.print(uf.f);
  
          Serial.println(" ");
  
          // You can use here the values!!!!
 
         // Prepare answer
         byteOutModbusPDU[0] = byteInModbusPDU[0];
         byteOutModbusPDU[1] = byteInModbusPDU[1];
         byteOutModbusPDU[2] = byteInModbusPDU[2];
         byteOutModbusPDU[3] = byteInModbusPDU[3];
         byteOutModbusPDU[4] = byteInModbusPDU[4];
         *iOutlenght = 5;
         
        }
      }
    }
    // Write Multiple Register
    if (byteModbusFunctionCode == 0x03) {
      unsigned short ushortModbusAddress = getShortFromBytes(&byteInModbusPDU[1]);
      Serial.print("Address: ");
      Serial.println(ushortModbusAddress);
      short shortQuantityOfRegisters = getShortFromBytes(&byteInModbusPDU[3]);
      Serial.print("Quantity of Register: ");
      Serial.println(shortQuantityOfRegisters);
      byte byteByteCount = shortQuantityOfRegisters * 2;
      Serial.print("Byte Count: ");
      Serial.println(byteByteCount);

      // Prepare answer
      byteOutModbusPDU[0] = byteInModbusPDU[0]; // Function Code
      *iOutlenght = 1;
      byteOutModbusPDU[1] = byteByteCount; // Byte Count
      *iOutlenght = *iOutlenght + 1;

      // Copy data to union
      memcpy(&byteOutModbusPDU[2], &m_union_share_mem.temp_bytearray[ushortModbusAddress], byteByteCount);
      *iOutlenght = *iOutlenght + byteByteCount;

    }    
  }  
}

// Short
void setShortToBytes(short shortVal, byte* bytearrayVal) {
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;
  // Overite bytes of union with float variable
  u.temp_short = shortVal;
  // Assign bytes to input array
  bytearrayVal[0] = u.temp_bytearray[1];
  bytearrayVal[1] = u.temp_bytearray[0];
}

short getShortFromBytes(byte* bytearrayVal) {
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;

  u.temp_bytearray[1] = bytearrayVal[0];
  u.temp_bytearray[0] = bytearrayVal[1];

  return u.temp_short;
}

/*
// Long 4 byte
void setLongToBytes(long longVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    long temp_long;
    byte temp_bytearray[4];
  } u;
  // Overite bytes of union with int variable
  u.temp_long = longVal;

  bytearrayVal[0] = u.temp_bytearray[3];
  bytearrayVal[1] = u.temp_bytearray[2];
  bytearrayVal[2] = u.temp_bytearray[1];
  bytearrayVal[3] = u.temp_bytearray[0];
}

long getLongFromBytes(byte* bytearrayVal){
  // Create union of shared memory space
  union {
    long temp_long;
    byte temp_bytearray[4];
  } u;

  u.temp_bytearray[3] = bytearrayVal[0];
  u.temp_bytearray[2] = bytearrayVal[1];
  u.temp_bytearray[1] = bytearrayVal[2];
  u.temp_bytearray[0] = bytearrayVal[3];

  return u.temp_long;
}

// Float 4 byte
void setFloatToBytes(float floatVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    float temp_float;
    byte temp_bytearray[4];
  } u;
  // Overite bytes of union with float variable
  u.temp_float = floatVal;

  bytearrayVal[0] = u.temp_bytearray[3];
  bytearrayVal[1] = u.temp_bytearray[2];
  bytearrayVal[2] = u.temp_bytearray[1];
  bytearrayVal[3] = u.temp_bytearray[0];
}

float getFloatFromBytes(byte* bytearrayVal){
  // Create union of shared memory space
  union {
    float temp_float;
    byte temp_bytearray[4];
  } u;

  u.temp_bytearray[3] = bytearrayVal[0];
  u.temp_bytearray[2] = bytearrayVal[1];
  u.temp_bytearray[1] = bytearrayVal[2];
  u.temp_bytearray[0] = bytearrayVal[3];

  return u.temp_float;
}
*/
/* Function to reverse arr[] from start to end*/
void reverseByteArray(byte Array[], int iStart, int iEnd)
{
  byte temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseShortArray(short Array[], int iStart, int iEnd)
{
  short temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseLongArray(long Array[], int iStart, int iEnd)
{
  long temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

void reverseFloatArray(float Array[], int iStart, int iEnd)
{
  float temp;
  while (iStart < iEnd)
  {
    temp = Array[iStart];
    Array[iStart] = Array[iEnd];
    Array[iEnd] = temp;
    iStart++;
    iEnd--;
  }
}

// Modbus CRC
// Compute the MODBUS RTU CRC
unsigned short getCRC(byte buf[], int len)
{
  unsigned short crc = 0xFFFF;
 
  for (int pos = 0; pos < len; pos++) {
    crc ^= (unsigned short)buf[pos];          // XOR byte into least sig. byte of crc
 
    for (int i = 8; i != 0; i--) {    // Loop over each bit
      if ((crc & 0x0001) != 0) {      // If the LSB is set
        crc >>= 1;                    // Shift right and XOR 0xA001
        crc ^= 0xA001;
      }
      else                            // Else LSB is not set
        crc >>= 1;                    // Just shift right
    }
  }
  // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
  return crc;  
}

