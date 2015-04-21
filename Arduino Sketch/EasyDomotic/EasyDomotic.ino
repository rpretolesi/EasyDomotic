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

// Dati di comunicazione
byte SOH = 0x01;
byte EOT = 0x04;
byte ENQ = 0x05;
byte ACK = 0x06;


int m_iNrByteToRead = 0;
int m_iNrByteRead = 0;
byte m_byteRead[16] = {0};
boolean m_bENQInProgress = false;
boolean m_bSOHInProgress = false;
byte m_byteFirstByteRead = 0;
byte m_byteToWrite[16] = {0};

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
        m_iNrByteRead = 0;
        for(int indice_1 = 0; indice_1 < 16; indice_1++)
        {
          m_byteRead[indice_1] = 0;
        }
        m_byteToWrite[0] = ACK;
        m_byteToWrite[15] = EOT;

        Serial.println("Client Connected.");
      }

      // Read and write operation....
      // Checking the first byte....
      // Devono essere 16
      m_iNrByteToRead = client.available();
      if (m_iNrByteToRead >= 1) 
      {
        if(m_bENQInProgress == false && m_bSOHInProgress == false)
        {
          // Check the message
          // Read the first byte
          m_byteFirstByteRead = client.read(); 
          m_iNrByteRead = m_iNrByteRead + 1;
          // Just a enquiry....
          if(m_byteFirstByteRead == ENQ)
          {
            m_bENQInProgress = true;
          }
          // Data to read....
          if(m_byteFirstByteRead == SOH)
          {
            m_bSOHInProgress = true;
          }
        }

        // Just a enquiry....
        if(m_bENQInProgress == true)
        {
          //          Serial.println("ENQ byte read.");

          for(int index_1 = 0; index_1 < 16; index_1++)
          {
            client.write(m_byteToWrite[index_1]);
            //            Serial.print("ENQ byte write: ");
            //            Serial.print(m_byteToWrite[index_1]);
            //            Serial.print(" index: ");
            //            Serial.println(index_1);
          } 
          m_iNrByteRead = 0;
          m_bENQInProgress = false;          
        }

        // Data to read....
        if(m_bSOHInProgress == true)
        {
          for(int index_0 = m_iNrByteRead; index_0 < m_iNrByteToRead; index_0++)
          {
            m_byteRead[m_iNrByteRead] = client.read();
            //            Serial.print("SOH byte read: ");
            //            Serial.print(m_byteRead[m_iNrByteRead]);
            //            Serial.print(" index: ");
            //            Serial.println(m_iNrByteRead);
            m_iNrByteRead = m_iNrByteRead + 1;  
            if(m_iNrByteRead >= 16)
            {
              m_iNrByteRead = 0;
              m_bSOHInProgress = false;

              // Check the last char...
              if(m_byteRead[15] == EOT)
              {
                // Store the result and write back....
                // Here i can use the data received....
                // Digital
                boolean b_1_1 = ((m_byteRead[1] & 0b00000001) == 1);
                boolean b_1_2 = ((m_byteRead[1] & 0b00000010) == 2);
                boolean b_1_3 = ((m_byteRead[1] & 0b00000100) == 4);
                boolean b_1_4 = ((m_byteRead[1] & 0b00001000) == 8);
                boolean b_1_5 = ((m_byteRead[1] & 0b00010000) == 16);
                boolean b_1_6 = ((m_byteRead[1] & 0b00100000) == 32);
                boolean b_1_7 = ((m_byteRead[1] & 0b01000000) == 64);
                boolean b_1_8 = ((m_byteRead[1] & 0b10000000) == 128);

                boolean b_2_1 = ((m_byteRead[2] & 0b00000001) == 1);
                boolean b_2_2 = ((m_byteRead[2] & 0b00000010) == 2);
                boolean b_2_3 = ((m_byteRead[2] & 0b00000100) == 4);
                boolean b_2_4 = ((m_byteRead[2] & 0b00001000) == 8);
                boolean b_2_5 = ((m_byteRead[2] & 0b00010000) == 16);
                boolean b_2_6 = ((m_byteRead[2] & 0b00100000) == 32);
                boolean b_2_7 = ((m_byteRead[2] & 0b01000000) == 64);
                boolean b_2_8 = ((m_byteRead[2] & 0b10000000) == 128);

                // Analogic
                byte byte_5 = m_byteRead[5];
                byte byte_6 = m_byteRead[6];
                byte byte_7 = m_byteRead[7];
                byte byte_8 = m_byteRead[8];
                // ...
                byte byte_14 = m_byteRead[14];

                // Output
                if(b_2_1 == true){
                  digitalWrite(3, true); 
                } 
                else {
                  analogWrite(3, byte_5); 
                }

                if(b_2_2 == true){
                  digitalWrite(5, true); 
                } 
                else {
                  analogWrite(5, byte_6);       
                }

                if(b_2_3 == true){
                  digitalWrite(6, true); 
                } 
                else {
                  analogWrite(6, byte_7);       
                }

                if(b_2_4 == true){
                  digitalWrite(9, true); 
                } 
                else {
                  analogWrite(9, byte_8);       
                }

                // Write back just for test....
                // You can assigne here any value that you would like to read on the app....
                // Digital
                m_byteToWrite[1] = m_byteRead[1];
                m_byteToWrite[2] = m_byteRead[2];

                // Analogic
                m_byteToWrite[5] = m_byteRead[5];
                m_byteToWrite[6] = m_byteRead[6];
                m_byteToWrite[7] = m_byteRead[7];
                m_byteToWrite[8] = m_byteRead[8];
                m_byteToWrite[9] = m_byteRead[9];
                m_byteToWrite[10] = m_byteRead[10];
                m_byteToWrite[11] = m_byteRead[11];
                m_byteToWrite[12] = m_byteRead[12];
                m_byteToWrite[13] = m_byteRead[13];
                m_byteToWrite[14] = m_byteRead[14];


                /*
                 * Test
                 *                
                 for(int index_2 = 1; index_2 < 15; index_2++)
                 {
                 m_byteToWrite[index_2] = m_byteRead[index_2];
                 }
                 */

                for(int index_3 = 0; index_3 < 16; index_3++)
                {
                  client.write(m_byteToWrite[index_3]);
                  //                   Serial.print("SOH byte write: ");
                  //                   Serial.print(m_byteToWrite[index_3]);
                  //                   Serial.print(" index: ");
                  //                   Serial.println(index_3);
                }                
              }
              else
              {
                Serial.println("EOT Error. ");
                client.stop();

                initValue();
              }
              break;  
            }
          }
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
  // Variable
  m_iNrByteToRead = 0;
  m_iNrByteRead = 0;
  m_bENQInProgress = false;
  m_bSOHInProgress = false;
  m_byteFirstByteRead = 0;

  for(int index_0 = 0; index_0 < 16; index_0++) {
    m_byteRead[index_0] = 0;
    m_byteToWrite[index_0] = 0;
  }

  // Output
  analogWrite(3, 0);       
  analogWrite(5, 0);       
  analogWrite(6, 0);       
  analogWrite(9, 0);       

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

