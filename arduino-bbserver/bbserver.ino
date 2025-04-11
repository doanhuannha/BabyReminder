#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#define AP_ID "BB-WIFI"
#define AP_KEY "hongcopass"
ESP8266WebServer _webServer(80);
void setup() {
    Serial.begin(9600);
    delay(10);
    WiFi.mode(WIFI_AP);
    
    IPAddress ap_ip(10,11,12,13);
    IPAddress ap_gateway(10,11,12,13);
    IPAddress ap_subnet(255,255,255,0);
    
    Serial.print(F("Configuring access point..."));
    WiFi.softAPConfig(ap_ip, ap_gateway, ap_subnet);
    WiFi.softAP(AP_ID,AP_KEY);
    
    IPAddress myIP = WiFi.softAPIP();
    Serial.print(F("AP IP address: "));
    Serial.println(myIP);
    _webServer.on("/", handleHttp);
    _webServer.begin();

    Serial.println(F("Setup mode is ready"));
}

void loop() {
    _webServer.handleClient();
}
void handleHttp() {
    Serial.println(F("Request is coming"));
    _webServer.send(200, F("text/txt"), F("I am alive"));
}

