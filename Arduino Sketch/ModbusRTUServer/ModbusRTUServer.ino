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
byte m_byteReadMBMsg[260] = {0};
boolean m_bModbusMBAP = false;
unsigned int m_uiModbusMBAPLength = 0;

byte m_byteToWriteMBAPMsg[260] = {0};
unsigned int m_uiNrByteToWrite = 0;

// Valori condivisi
short shBoolValue = 0; // 2 bytes

short shInValue = 0; // 2 bytes
long lInValue = 0; // 4 bytes
float fInValue = 0.0;

short shOutValue = 0; // 2 bytes
long lOutValue = 0; // 4 bytes
float fOutValue = 0.0;

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

void loop() {
  // put your main code here, to run repeatedly:
  WiFiClient client = m_server.available();   
  if(client != NULL) 
  {
    if(client.connected())
    {
      m_bOneShotClientDisconnected_1 = false;
      m_bOneShotClientDisconnected_2 = false;
      if(m_bOneShotClientConnected == false){
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
        Serial.println("Begin. ");
        Serial.print("MBAP Start: ");
        for(int index_0 = 0; index_0 < 6; index_0++) {
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

// Create union of shared memory space
union {
  short temp_short[20];
  long temp_long[10];
  float temp_float[10];
  byte temp_bytearray[40];
} m_union_share_mem;
  

// Short
void setShortToBytes(short shortVal, byte* bytearrayVal){
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

short getShortFromBytes(byte* bytearrayVal){
  // Create union of shared memory space
  union {
    short temp_short;
    byte temp_bytearray[2];
  } u;
  
  u.temp_bytearray[1] = bytearrayVal[0];
  u.temp_bytearray[0] = bytearrayVal[1];
  
  return u.temp_short;
}

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

