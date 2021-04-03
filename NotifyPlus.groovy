/**
 *  Notify Tile Driver
 *
 *  Author: Tim Yuhl
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  History:
 *
 *
 */
import java.text.SimpleDateFormat

String appVersion()   { return "1.0.0" }
def setVersion(){
	state.name = "NotifyPlus Driver"
	state.version = "1.0.0"
}

metadata {
	definition (
			name: "NotifyPlus Tile Driver",
			namespace: "tyuhl",
			description:"Driver to provide notification tile",
			importUrl:"",
			author: "Tim Yuhl") {

		capability 	"Notification"

		attribute "NotificationText", "string"
		attribute "Html", "string"
		attribute "Notify1", "string"
		attribute "Notify2", "string"
		attribute "Notify3", "string"
		attribute "Notify4", "string"
		attribute "Notify5", "string"

		command "reset"
	}
	preferences {
		section("Logging") {
			input("showTimestamp", "bool", title:"Add Timestamp to Notification", defaultValue: true)
			input("dateFormat", "string", title: "Advanced setting: Date Format", defaultValue: "MM/dd/yyyy HH:mm:ss")
			input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
		}
	}
}
/**
 * Boilerplate callback methods called by the framework
 */
void configure() {
	log("configure called", "trace")
}

void installed()
{
	log("installed called", "trace")
	showTimestamp = true
	dateFormat = "MM/dd/yyyy HH:mm:ss"
	reset()
}

void uninstalled()
{
	log("uninstalled called", "trace")
}

void updated()
{
	log("updated called", "trace")
}

void parse(String message)
{
	log("parse called with message: ${message}", "trace")
}
/* End of built-in callbacks */

def deviceNotification(notificationTxt){
	sendEvent(name:"NotificationTxt", value: notificationTxt)
	updateList(notificationTxt)
}

def updateList(notificationTxt) {
	if (showTimestamp) {
		dateNow = new Date()
		sdf = new SimpleDateFormat(dateFormat)
		notificationTxt = "<div class=\"npl-date\" style=\"display:inline-block; padding-right: 5px\">" + sdf.format(dateNow) + ": </div><div class=\"npl-text\" style=\"display:inline-block; padding-right: 5px\" >" + notificationTxt + "</div>"
	} else {
		notificationTxt = "<div class=\"npl-text\" style=\"display:inline-block; padding-right: 5px\" >" + notificationTxt + "</div>"
	}
	newHtml = "<ul class=\"np0l-ul\" style=\"list-style-type:none; margin:0; padding:0; text-align:left\"><li>" + notificationTxt + "</li><li>" + device.currentValue("Notify1") + "</li><li>" + device.currentValue("Notify2") + "</li><li>" + device.currentValue("Notify3") + "</li><li>" + device.currentValue("Notify4") + "</li><li>" + device.currentValue("Notify5") +"</li></ul>"
	sendEvent(name:"Notify5", value: device.currentValue("Notify4"))
	sendEvent(name:"Notify4", value: device.currentValue("Notify3"))
	sendEvent(name:"Notify3", value: device.currentValue("Notify2"))
	sendEvent(name:"Notify2", value: device.currentValue("Notify1"))
	sendEvent(name:"Html", value: newHtml)
	sendEvent(name:"Notify1",value: notificationTxt)
}

def reset() {
	log("reset called", "trace")
	sendEvent(name:"Notify1", value: " ")
	sendEvent(name:"Notify2", value: " ")
	sendEvent(name:"Notify3", value: " ")
	sendEvent(name:"Notify4", value: " ")
	sendEvent(name:"Notify5", value: " ")
	sendEvent(name:"Html", value: " ")
}

/* boilerplate logging */
private determineLogLevel(data) {
	switch (data?.toUpperCase()) {
		case "TRACE":
			return 0
			break
		case "DEBUG":
			return 1
			break
		case "INFO":
			return 2
			break
		case "WARN":
			return 3
			break
		case "ERROR":
			return 4
			break
		default:
			return 1
	}
}

def log(Object data, String type) {
	data = "-- ${device.label} -- ${data ?: ''}"

	if (determineLogLevel(type) >= determineLogLevel(settings?.logging ?: "INFO")) {
		switch (type?.toUpperCase()) {
			case "TRACE":
				log.trace "${data}"
				break
			case "DEBUG":
				log.debug "${data}"
				break
			case "INFO":
				log.info "${data}"
				break
			case "WARN":
				log.warn "${data}"
				break
			case "ERROR":
				log.error "${data}"
				break
			default:
				log.error("-- ${device.label} -- Invalid Log Setting")
		}
	}
}
