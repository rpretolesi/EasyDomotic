/*
 TCP IP Server
 
 Circuit:
 * WiFi shield attached
 
 created 03 Feb 2015
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
byte m_byteReadMBMsg[260] = {0};
boolean m_bModbusMBAP = false;
unsigned int m_uiModbusMBAPLength = 0;

byte m_byteToWriteMBAPMsg[260] = {0};
unsigned int m_uiNrByteToWrite = 0;

// Valori condivisi
int iValue;

void setup() 
{
  // Deselect SD Card
  pinMode(4, OUTPUT);     
  digitalWrite(4, 1);

  // Set as Output mode
  pinMode(3, OUTPUT);     
  pinMode(5, OUTPUT);     
  pinMode(6, OUTPUT);     
  pinMode(9, OUTPUT);     


   //Initialize serial and wait for port to open:
  Serial.begin(57600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }

  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue:
    while (true);
  }

  String fv = WiFi.firmwareVersion();
  if ( fv != "1.1.0" )
    Serial.println("Please upgrade the firmware");
  Serial.print(F("Firmware Version:"));
  Serial.println(WiFi.firmwareVersion());

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
  // you're connected now, so print out the status:
  printWifiStatus();

}


void loop() 
{
  // WiFi Communication
  Communication();
}

void Communication()
{
  
  WiFiClient client = m_server.available();   
  if(client != NULL) 
  {
    if(client.connected())
    {
      m_bOneShotClientDisconnected_1 = false;
      m_bOneShotClientDisconnected_2 = false;
      if(m_bOneShotClientConnected == false)
      {
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
      if (client.available() >= 6 && m_bModbusMBAP == false)
      {
        // Parse message in order to get MBAP Header
        Serial.print("MBAP: ");
        for(int index_0 = 0; index_0 < 6; index_0++) {
          m_byteReadMBAP[index_0] = client.read();
          Serial.print(m_byteReadMBAP[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");

        unsigned int uiModbusMBAPTransactionID = getWordFromBytes(m_byteReadMBAP[1], m_byteReadMBAP[0]);
        Serial.print("Transaction ID: ");
        Serial.println(uiModbusMBAPTransactionID);

        unsigned int uiModbusMBAPProtocolID = getWordFromBytes(m_byteReadMBAP[3], m_byteReadMBAP[2]);
        Serial.print("Protocol ID: ");
        Serial.println(uiModbusMBAPProtocolID);

        m_uiModbusMBAPLength = getWordFromBytes(m_byteReadMBAP[5], m_byteReadMBAP[4]);
        Serial.print("Length: ");
        Serial.println(m_uiModbusMBAPLength);

        if ((uiModbusMBAPProtocolID != 0) || (m_uiModbusMBAPLength < 5) || (m_uiModbusMBAPLength > 254)) {
          // Errore, scarico tutto
          initValue();
          client.flush();
        } else {
          m_bModbusMBAP = true;
        }
      }

      if (client.available() >= m_uiModbusMBAPLength && m_bModbusMBAP == true)
      {
        Serial.print("Message: ");
        for(int index_0 = 0; index_0 < m_uiModbusMBAPLength; index_0++) {
          m_byteReadMBMsg[index_0] = client.read();
          Serial.print(m_byteReadMBMsg[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");

        // Messaggio Completo, estraggo i dati:
        unsigned int uiModbusUnitIdentifier = getWordFromBytes(m_byteReadMBMsg[0], 0);
        Serial.print("Unit Identifier: ");
        Serial.println(uiModbusUnitIdentifier);
        
        unsigned int uiModbusFunctionCode = getWordFromBytes(m_byteReadMBMsg[1], 0);
        Serial.print("Function Code: ");
        Serial.println(uiModbusFunctionCode);

        // Tutto Ok, costruisco la risposta, 1° parte
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
        boolean bValueOk = false;
        
        // Function
        if(uiModbusFunctionCode == 0x10 || uiModbusFunctionCode == 0x03){
          bFunctionCodeOk = true;
        }   

        // Address
        int iOutput = 0;
        if(uiModbusFunctionCode == 0x10){
          // Check Data
          unsigned int uiQuantityOfRegisters = getWordFromBytes(m_byteReadMBMsg[5], m_byteReadMBMsg[4]);
          unsigned int uiByteCount = getWordFromBytes(m_byteReadMBMsg[6], 0);
          Serial.print("Quantity of Register: ");
          Serial.println(uiQuantityOfRegisters);
          Serial.print("Byte Count: ");
          Serial.println(uiByteCount);
        
          if(uiByteCount == (uiQuantityOfRegisters * 2)){
            bRegisterAndByteCountOk = true;
          }
          if(bRegisterAndByteCountOk == true){
            finire qui
            unsigned int uiModbusAddress = getWordFromBytes(m_byteReadMBMsg[3], m_byteReadMBMsg[2]);
            Serial.print("Address: ");
            Serial.println(uiModbusAddress);
    
            if(uiModbusAddress == 10000) {  
              iOutput = 3;
              bAddressOk = true;
            }            
            if(uiModbusAddress == 10001) {  
              iOutput = 5;
              bAddressOk = true;
            }            
            if(uiModbusAddress == 10002) {  
              iOutput = 6;
              bAddressOk = true;
            }            
            if(uiModbusAddress == 10003) {  
              iOutput = 9;
              bAddressOk = true;
            }  
            if(bAddressOk == true){       
  
              // Value
              int iModbuSingleValue = getWordFromBytes(m_byteReadMBMsg[5], m_byteReadMBMsg[4]);
              Serial.print("Value: ");
              Serial.println(iModbuSingleValue);
              // Ok, i can use the value
              // Set 0 if value == 1
              // Set 1 if value == 4
              switch(iModbuSingleValue){
                case 1:
                  digitalWrite(iOutput, 0);  
                  bValueOk = true;
                  break;
                
                case 4:
                  digitalWrite(iOutput, 1);            
                  bValueOk = true;
                  break;
                 
                default:
                  // Exception
                  bValueOk = false;
                  
                  break;
              }
              if(bValueOk == true) {
                // Tutto Ok, costruisco la risposta, 2° parte
                unsigned int iMBAPMsgLength = 12;
                m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
                m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
                m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
                m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                m_byteToWriteMBAPMsg[7] = m_byteReadMBMsg[1]; // Function code
                m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
                m_byteToWriteMBAPMsg[8] = m_byteReadMBMsg[2]; // Address
                m_byteToWriteMBAPMsg[9] = m_byteReadMBMsg[3]; // Address
                m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
                m_byteToWriteMBAPMsg[10] = m_byteReadMBMsg[4]; // Value
                m_byteToWriteMBAPMsg[11] = m_byteReadMBMsg[5]; // Value
                m_uiNrByteToWrite = m_uiNrByteToWrite + 2;            
              }            
            }          
          }
        }
        
        int iInput = 0;
        if(uiModbusFunctionCode == 0x03){
          bAddressOk = true;

          unsigned int uiModbusAddress = getWordFromBytes(m_byteReadMBMsg[3], m_byteReadMBMsg[2]);
          Serial.print("Starting Address: ");
          Serial.println(uiModbusAddress);
          if(uiModbusAddress == 20000) {  
            iInput = 0;
            bAddressOk = true;
          }
          if(bAddressOk == true){       
            
            // Quantity of Registers
            unsigned int uiQuantityOfRegister = getWordFromBytes(m_byteReadMBMsg[5], m_byteReadMBMsg[4]);
            Serial.print("Quantity of Register: ");
            Serial.println(uiQuantityOfRegister);

            unsigned int iMBAPMsgLength = 0;
            int iSV = analogRead(iInput);
            float fSV = analogRead(iInput)/3.3;
            switch(uiQuantityOfRegister){
              case 1:
                // short 16 bit
                iMBAPMsgLength = 11;
                // Tutto Ok, costruisco la risposta, 3° parte
                Serial.print("iSV: ");
                Serial.print(iSV);
                shortTobytes(iSV, &m_byteToWriteMBAPMsg[9]);                
                m_uiNrByteToWrite = m_uiNrByteToWrite + 2;            
                
                bValueOk = true;
                break;
              
              case 2:
/*              
                // int 32 bit
     
*/
                // Float 32 bit
                iMBAPMsgLength = 13;
                // Tutto Ok, costruisco la risposta, 3° parte
                Serial.print("fSV: ");
                Serial.print(fSV);
//                shortTobytes(iSV, &m_byteToWriteMBAPMsg[9]);                
                floatTobytes(fSV, &m_byteToWriteMBAPMsg[9]);
                m_uiNrByteToWrite = m_uiNrByteToWrite + 4;            

                bValueOk = true;
                break;
               
              default:
                // Exception
                bValueOk = false;
                
                break;
            }   
               
            if(bValueOk == true) {
              // Tutto Ok, costruisco la risposta, 2° parte
              m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
              m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
              m_byteToWriteMBAPMsg[6] = m_byteReadMBMsg[0]; // Unit Identifier
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[7] = m_byteReadMBMsg[1]; // Function code
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[8] = (byte)(2 * uiQuantityOfRegister); // Byte Count (2 x uiQuantityOfRegister)
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
            }                     
          }
        }

          
        if(bFunctionCodeOk == true) {
          if(bAddressOk == true) {         
            if(bValueOk == true) {
            } else {
              // Exception
              // Bad Value
              unsigned int iMBAPMsgLength = 9;
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
          } else {
            // Exception
            // Bad Address
            unsigned int iMBAPMsgLength = 9;
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
          unsigned int iMBAPMsgLength = 9;
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

        // Risposta completa, la invio
        Serial.print("Nr of bytes to write: ");
        Serial.println(m_uiNrByteToWrite);
        
        Serial.print("Answer: ");
        for(int index_0 = 0; index_0 < m_uiNrByteToWrite; index_0++) {
          client.write(m_byteToWriteMBAPMsg[index_0]);
          Serial.print(m_byteToWriteMBAPMsg[index_0]);
          Serial.print(" ");
        }
        Serial.println(" ");

        // Operazione Terminata
        initValue();
      }
    }
    else
    {
      m_bOneShotClientConnected = false;

      if(m_bOneShotClientDisconnected_1 == false)
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

    if(m_bOneShotClientDisconnected_2 == false)
    {
      m_bOneShotClientDisconnected_2 = true;

      Serial.println("Client Null."); 

      initValue();
    }
  }
}

void initValue(){

  // Initializing the Value
  m_bModbusMBAP = false;
  
  for(int index_0 = 0; index_0 < 6; index_0++) {
    m_byteReadMBAP[index_0] = 0;
  }

  for(int index_0 = 0; index_0 < 260; index_0++) {
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
  Serial.print("PCP/IP Address: ");
  Serial.println(ip);
  Serial.print("TCP/IP Port: ");
  Serial.println(m_ServerTCPPort);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

//#define bytesToWord(hb,lb) ( (((WORD)(hb&0xFF))<<8) | ((WORD)lb) )
word getWordFromBytes(byte lowByte, byte highByte) {
    return ((word)(((byte)(lowByte))|(((word)((byte)(highByte)))<<8)));
}

void shortTobytes(short shortVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;
  // Overite bytes of union with float variable
  u.temp_short = shortVal;
  // Assign bytes to input array
  memcpy(bytearrayVal, u.temp_bytearray, 2);
}

void floatTobytes(float floatVal, byte* bytearrayVal){
  // Create union of shared memory space
  union {
    float temp_float;
    byte temp_bytearray[4];
  } u;
  // Overite bytes of union with float variable
  u.temp_float = floatVal;
  // Assign bytes to input array
  memcpy(bytearrayVal, u.temp_bytearray, 4);
}

