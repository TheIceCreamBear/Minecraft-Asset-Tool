# Minecraft-Asset-Tool
Minecraft Asset Tool (MAT) is a Graphical utility program that allows the user to find and translate the assets of Minecraft into a file with an English name (not hexadecimal) with the correct file extension. 

## The `.minecraft` Directory
Minecraft stores saves, resource packs, logs, and other important information in the `.minecraft` folder. This folder is found in different default locations depending on platform:
 - Windows - `%APPDATA%\.minecraft`
 - macOS - `"~/Library/Application Support/Minecraft"`
 - Unix - `~/.minecraft`
 
Note that these directories are only the default, used by the official Minecraft Launcher found on [minecraft.net](https://minecraft.net), and can vary depending on user setting or use of third party launchers such as MultiMC.
 
#### Where are the Assets?
Assets such as textures and models are part of resource packs and are built into the game, or are provided by resource packs found in `.minecraft/resourcepacks`. 
The other asset files of the game, audio, language files, and icons, are stored in a sub folder of the `.minecraft/assets/objects/` folder, with their name being the SHA-1 hash of the file or file name (citation needed) and their parent folder being the first two hex digits of their hash.
The correct name of the asset file can be found in the `<verson>.json` file found in `.minecraft/assets/indexes/`, along with the hash of the file and the size of the file in bytes.

#### Normal Asset Name Correction
To correct the names of these files for programs that rely on extensions, such as some audio programs and the operating system, one would have to manually locate the file they are looking for in the `<version>.json` for the file they want.
Then in the objects folder for the corresponding hash, copy, and rename the file to get it to be properly recognized. This process would then have to be repeadted for every file a user wished to convert. Thats where MAT comes in.

## What Does MAT do?
MAT reduces the redundancy of doing the conversion manually. When MAT loads, it will scan the platform default location of `.minecraft` for all of the available index files.
It will then parse that index file, generating an internal tree of check boxes that corresponds to each asset in the index. Once all index files have been parsed, the GUI will be displayed to the user.

#### GUI
The GUI of MAT is relatively minimal. The majority of the gui is used by the tree of check boxes, showing the user of all possible folders and files available to save. This is where the user will 
select all the files they wish to convert.

Above the check box tree is a drop down menu that contains all index files that were found in the default `.minecraft` folder. When this drop down changes, the main tree will be updated to show the 
files specified in newly selected index file.

Below the check box tree is a group of 2 buttons, and a static text field. The left button, "Browse" lets the user browse for the destination folder that the program will copy the selected assets into.
The selected directory will then show up in the text field to the right of the "Browse" button.
The button to the right of this text field is the "Save Selected" button, which when clicked will begin the process of saving the assets to the selected destination folder.

#### The Saving of Assets
The selected destination folder will be the parent folder, with the parents of the selected asset as child folders, with the file being in the lowest child.
For example, if the selected file is `realms/lang/en_us.json` and the user selected the destination folder of `~/assets`, the result will be `~/assets/realms/lang/en_us.json`.
This will run for all assets. Upon completion, a confirmation dialog will show up, letting you know how many assets were successfully transfered.

# Bugs
If any bugs are encountered, report them in the issues tab of this git repository.

# Project Origin/Purpose
The reason for this project stemmed from a personal desire to have access to the noteblock sounds from Minecraft. 
After doing the extracting and conversion process manually a couple times prior and being upset with how manual and redundant it was, I decided i was going to write a program, as any programmer does.
Eventually, after a long break, I _mostly_ finished the project, with some QOL features still to implement as of 05/09/2021.

