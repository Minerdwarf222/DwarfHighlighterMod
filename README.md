Added dev version of 1.0.9
Adds:
Weekend's optimal currency conversion
Config now can be separated into categories based on titles.
You can change title visibility if you want sections of items to be visible or not. (/DwarfToggleTitle <List> <Title>).
Can grab the updated list from this github via command (/DwarfGrabNewTCLList). Completely rewrites your tcl list and only saves title visibility.
Can reload a list and save a list via command.
Can edit items by changing their name and quantity required via /DwarfEditItem <List> <Item> <EditType> <Input>. (Saves & Reloads after ran rn)
Can delete items by /DwarfDeleteItem <List> <Item>. Saves and Reloads right after for right now.

This mod is for players of Monumenta. There is a config file "DwarfHighlighterList.txt" where you list the names of all the items you would like to be notified of if they are in a container when you close it.

Requires 1.19.4 Fabric, ClothConfig, and ModMenu.

The notification is of the form of a client-side only chat message listing the items and how many of them are in the container.


As of 1.0.8 there are two config files:

DwarfHighlighterList.txt (Private list of items you are looking for. Qty wanted takes priority here)

DwarfHighlighterTCLList.txt (Guild list of items the guild is looing for.)

Items have [p], [g], or [p/g] after the item name to represent.


The config is of the format:

Lines starting with # are comments.

Empty lines are skipped.

Correctly spell the exact name of an item you are looking for. Capitalization does not matter.

ex:

The Vedha's Soulcrusher

Additional options may be added like specifying what region the item must be (really only for Exalted dungeon items that share the same name as their r1 counterparts), how many of that item is needed, or ignoring multiple lines.

Specifying Region:

(1/2/3) (Item Name)

where 1 = valley, 2 = isles, 3 = ring.

Specifying how many of that item you want:

(Item Name);(Qty)

These can be combined

(1/2/3) (Item Name);(Qty)

Ignoring multiple lines:
Use * to designate the start of the skipped lines. Every line between * and the next empty line or comment (#) will be skipped.
