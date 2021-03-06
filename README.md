# NotifyPlus Tile Driver for Hubitat
 
NotifyPlus Tile Driver is a simple way to display a list of the last 5 notifications sent to
it in a Hubitat dashboard tile, with optional time/date stamp. Hubitat dashboard tiles are limited
to a maximum of 1024 characters for attributes and this driver ensures
that you don't exceed that limit - it trims older messages as needed to keep
you from exceeding the 1024 character maximum. 

**To use this driver:**

1. Install the driver code.
2. Create an instance of the driver on the Devices page.
3. Review and change preferences as needed
4. In one of your dashboards, add a tile for NotifyPlus. Pick the Attribute template
   and select the Html attribute.
5. Test by entering text in the Device Notification command and clicking the command button.
6. Modify any automations that you want to use with it to send text notifications to the driver.
7. You also may want to create a virtual button and then create an automation for that button to call the reset() command, which will clear the contents of the tile.

Note: Inline styling has been coded into the generated HTML to make the tile usable without
needing to add custom CSS code.  CSS classes are available for advanced customization, however. 

**Preferences:**

Add Timestamp to Notification - automatically adds a time/date stamp before the message.

Date Format - change the date and/or time format _(advanced setting)_.

Set Maximum Message Length - limits the message length to the value set.  If the message
exceeds the limit, an ellipses will be added to the shortened message to indicate that it was
shortened. 

**Advanced CSS styling support:**

NotifyPlus implements the list displayed as a table element.
You can apply CSS styles to the table element, the time-stamp field and the text field to change the
look of the list.

Classes available for custom styling:

1. table element:  "npl-tbl" class
2. date field:  "npl-date" class
3. notification text field: "npl-text" class

To add custom CSS, open the tile settings dialog and click on "Advanced". Click on
the CSS button and enter the custom CSS at the bottom of the page.
Click on "Save CSS" to save your CSS.  Here is a simple example of custom CSS
to style the date and notification fields:
~~~
.npl-date {
font-size: 18px;
font-weight: 200;
}
.npl-text {
font-weight: 500;
color: DarkSlateBlue;
}

~~~
These CSS examples will change the date font to be 18 pixels with a light font. The text
field will be dark slate blue color with a bold font.
