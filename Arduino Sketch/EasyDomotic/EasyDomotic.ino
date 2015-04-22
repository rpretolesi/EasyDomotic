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
  Serial.begin(9600);
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

        unsigned int uiModbusMBAPTransactionID = getWordFromBytes(m_byteReadMBAP[0], m_byteReadMBAP[1]);
        Serial.print("Transaction ID: ");
        Serial.println(uiModbusMBAPTransactionID);

        unsigned int uiModbusMBAPProtocolID = getWordFromBytes(m_byteReadMBAP[2], m_byteReadMBAP[3]);
        Serial.print("Protocol ID: ");
        Serial.println(uiModbusMBAPProtocolID);

        m_uiModbusMBAPLength = getWordFromBytes(m_byteReadMBAP[4], m_byteReadMBAP[5]);
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
        unsigned int uiModbusUnitIdentifier = getWordFromBytes(m_byteReadMBMsg[6], 0);
        Serial.print("Unit Identifier: ");
        Serial.println(uiModbusUnitIdentifier);
        
        unsigned int uiModbusFunctionCode = getWordFromBytes(m_byteReadMBMsg[7], 0);
        Serial.print("Function Code: ");
        Serial.println(uiModbusFunctionCode);

        // Tutto Ok, costruisco la risposta, 1° parte
        // Intestazione
        m_byteToWriteMBAPMsg[0] = m_byteReadMBAP[0]; // Transaction Identifier
        m_byteToWriteMBAPMsg[1] = m_byteReadMBAP[1]; // Transaction Identifier
        m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
        m_byteToWriteMBAPMsg[2] = m_byteReadMBAP[2]; // Protocol Identifier
        m_byteToWriteMBAPMsg[3] = m_byteReadMBAP[3]; // Protocol Identifier
        m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
        m_byteToWriteMBAPMsg[6] = m_byteReadMBAP[6]; // Unit Identifier
        m_uiNrByteToWrite = m_uiNrByteToWrite + 1;

        if(uiModbusFunctionCode == 0x06){
          unsigned int uiModbusAddress = getWordFromBytes(m_byteReadMBAP[7], m_byteReadMBAP[8]);
          Serial.print("Address: ");
          Serial.println(uiModbusAddress);
          if(uiModbusAddress == 10000) {                       
            int iModbuSingleValue = getWordFromBytes(m_byteReadMBAP[10], m_byteReadMBAP[11]);
            boolean bValueOk = false;
            Serial.print("Value: ");
            Serial.println(iModbuSingleValue);
            // Ok, i can use the value
            // Set 0 if value == 1
            // Set 1 if value == 4
            switch(iModbuSingleValue){
              case 1:
                digitalWrite(3, 0);  
                bValueOk = true;
                break;
              
              case 4:
                digitalWrite(3, 1);            
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
              m_byteToWriteMBAPMsg[7] = m_byteReadMBAP[7]; // Function code
              m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
              m_byteToWriteMBAPMsg[8] = m_byteReadMBAP[8]; // Address
              m_byteToWriteMBAPMsg[9] = m_byteReadMBAP[9]; // Address
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
              m_byteToWriteMBAPMsg[10] = m_byteReadMBAP[10]; // Value
              m_byteToWriteMBAPMsg[11] = m_byteReadMBAP[11]; // Value
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
            } else {
              // Exception
              // Bad Value
              unsigned int iMBAPMsgLength = 9;
              m_byteToWriteMBAPMsg[4] = (iMBAPMsgLength >> 8) & 0xFF; // Lenght
              m_byteToWriteMBAPMsg[5] = iMBAPMsgLength & 0xFF; // Lenght
              m_uiNrByteToWrite = m_uiNrByteToWrite + 2;
              m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBAP[7] + 0x80); // Error code
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
            m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBAP[7] + 0x80); // Error code
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
          m_byteToWriteMBAPMsg[7] = (byte)(m_byteReadMBAP[7] + 0x80); // Error code
          m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
          m_byteToWriteMBAPMsg[8] = 0x01; // Exception code
          m_uiNrByteToWrite = m_uiNrByteToWrite + 1;
        }

        // Risposta completa, la invio
        for(int index_0 = 0; index_0 < m_uiNrByteToWrite; index_0++) {
          client.write(m_byteToWriteMBAPMsg[index_0]);
        }

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
