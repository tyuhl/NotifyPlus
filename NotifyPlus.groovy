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
 * Inspired by a similar device by thebearmay
 * *
 *  History:
 *  4/5/21 - Initial release.
 *
 */
import java.text.SimpleDateFormat
import groovy.transform.Field

@Field int attribLimit = 1024
@Field int messageSize = 4
@Field int maxTextLen = 75

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
			importUrl:"https://raw.githubusercontent.com/tyuhl/NotifyPlus/main/NotifyPlus.groovy",
			author: "Tim Yuhl") {

		capability 	"Notification"

		attribute "Html", "string"
		attribute "Notify1", "string"
		attribute "Notify2", "string"
		attribute "Notify3", "string"
		attribute "Notify4", "string"

		command "reset"
	}
	preferences {
		section("Prefs") {
			input("showTimestamp", "bool", title:"Add Timestamp to Notification", defaultValue: true)
			input("dateFormat", "string", title: "Advanced setting: Date Format", defaultValue: "MM/dd/yyyy HH:mm:ss")
			input("maxLineLen", "number", title: "Set Maximum Message Length", defaultValue: maxTextLen)
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
	maxLineLen = maxTextLen
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
	log("deviceNotification called", "trace")
	updateList(notificationTxt)
}

def updateList(notificationTxt) {
	log("updateList called", "trace")

	notificationTxt = ellipsize(notificationTxt)

	if (showTimestamp) {
		dateNow = new Date()
		sdf = new SimpleDateFormat(dateFormat)
		notificationTxt = "<span class=\"npl-date\" style=\"padding-right:8px\">" + sdf.format(dateNow) + ":</span><span class=\"npl-text\">" + notificationTxt + "</span>"
	} else {
		notificationTxt = "<span class=\"npl-text\">" + notificationTxt + "</span>"
	}

	def curHtml = device.currentValue("Html")
	def clen = curHtml.length()

	// wack old messages if needed to keep under max attrib length
	int i = 0
	String newHtml
	int newLen
	String[] Messages = deserializeMessages()
	while( i < 5 ) {
		def innerList = buildList(Messages, notificationTxt)
		newHtml = "<ul class=\"npl-ul\" style=\"list-style-type:none; padding:0; text-align:left\">" + innerList + "</ul>"
		newLen = newHtml.length()
		if (newLen > attribLimit) {
			log("Trimming old messages to fit new message", "trace")
			for( int j = 3; j >= 0 ; j-- ) {
				if (Messages[j] == " "){
					continue;
				} else {
					Messages[j] = " "
					break;
				}
			}
		}
		else {
			break;
		}
		i++
	}

	Messages[3] = Messages[2]
	Messages[2] = Messages[1]
	Messages[1] = Messages[0]
	Messages[0] = notificationTxt
	serializeMessages(Messages)
	sendEvent(name:"Html", value: newHtml)
}

def serializeMessages(String[] Messages) {
	sendEvent(name: "Notify1", value: Messages[0])
	sendEvent(name: "Notify2", value: Messages[1])
	sendEvent(name: "Notify3", value: Messages[2])
	sendEvent(name: "Notify4", value: Messages[3])
}

def deserializeMessages() {
	String[] Messages = [" ", " ", " ", " "]
	Messages[0] = device.currentValue("Notify1")
	Messages[1] = device.currentValue("Notify2")
	Messages[2] = device.currentValue("Notify3")
	Messages[3] = device.currentValue("Notify4")
	return Messages
}

def reset() {
	log("reset called", "trace")
	sendEvent(name: "Notify1", value: " ")
	sendEvent(name: "Notify2", value: " ")
	sendEvent(name: "Notify3", value: " ")
	sendEvent(name: "Notify4", value: " ")
	sendEvent(name:"Html", value: " ")
}

private buildList(String[] Messages, newEntry) {
	def innerList = "<li>" + newEntry + "</li>"
	for( int i = 0; i < 4; i++) {
		if (Messages[i] == " ") {
			break;
		}
		innerList += "<li>" + Messages[i] + "</li>"
	}
	return innerList
}

private ellipsize(txtToShorten) {
	if (txtToShorten.length() > maxLineLen) {
		txtToShorten = txtToShorten.substring(0, (int)(maxLineLen - 3)) + "..."
	}
	return txtToShorten
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
