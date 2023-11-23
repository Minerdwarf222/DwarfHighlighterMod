This mod is for players of Monumenta. There is a config file "DwarfHighlighterList.txt" where you list the names of all the items you would like to be notified of if they are in a container when you close it.

Requires 1.19.4 Fabric, ClothConfig, and ModMenu.

The notification is of the form of a client-side only chat message listing the items and how many of them are in the container.

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
